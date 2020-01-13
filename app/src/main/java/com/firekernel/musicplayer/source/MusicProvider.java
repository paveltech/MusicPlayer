package com.firekernel.musicplayer.source;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;

import com.firekernel.musicplayer.pojo.SongItem;
import com.firekernel.musicplayer.utils.FireLog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;

import static com.firekernel.musicplayer.source.RemoteSource.basePath;


/**
 * Simple data provider for music tracks. The actual metadata localSource is delegated to a
 * MusicProviderSource defined by a constructor argument of this class.
 * MediaId = Category/SubCategory|musicId
 */
public class MusicProvider {
    private static final String TAG = FireLog.makeLogTag(MusicProvider.class);

    private CopyOnWriteArrayList<MediaMetadataCompat> musicList;
    private CopyOnWriteArrayList<MediaMetadataCompat> mediaList;


    ExecutorService executorService = Executors.newSingleThreadExecutor();
    public ArrayList<SongItem> songItemArrayList;


    public MusicProvider(ArrayList<SongItem> songItemArrayList) {
        this.songItemArrayList = songItemArrayList;
        musicList = new CopyOnWriteArrayList<>();
        mediaList = new CopyOnWriteArrayList<>();
    }

    /**
     * Get the list of music tracks from a server and caches the track information
     * for future reference, keying tracks by musicId and grouping by genre.
     */

    @SuppressLint("StaticFieldLeak")
    public void retrieveMediaAsync(final String mediaId, final Callback callback) {
        FireLog.d(TAG, "(++) retrieveMediaAsync");
        // Asynchronously load the music catalog in a separate thread
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return retrieveMedia();
            }

            @Override
            protected void onPostExecute(Boolean initialized) {
                if (callback != null) {
                    callback.onMusicCatalogReady(initialized);
                }
            }
        }.executeOnExecutor(executorService);
    }

    private synchronized boolean retrieveMedia() {
        boolean initialized = false;
        mediaList.clear();

        FireLog.d(TAG, "++ retrieve song size " + songItemArrayList.size());

        try {
            Iterator<MediaMetadataCompat> tracks = iterator(songItemArrayList);
            while (tracks.hasNext()) {
                MediaMetadataCompat item = tracks.next();
                mediaList.add(item);
            }
            initialized = true;
        } catch (Exception e) {
            FireLog.e(TAG, "Media Initialization failed", e);
        }
        return initialized;
    }

    public List<MediaBrowserCompat.MediaItem> getChildren() {
        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();
        // fill the music List once and keep ever

        musicList.addAll(mediaList);
        for (MediaMetadataCompat metadata : getAllRetrievedMetadata()) {
            mediaItems.add(createTracksMediaItem(metadata));
        }

        return mediaItems;
    }

    public List<MediaMetadataCompat> getAllRetrievedMetadata() {
        ArrayList<MediaMetadataCompat> result = new ArrayList<>();
        for (MediaMetadataCompat track : mediaList) {
            result.add(track);
        }
        return result;
    }

    /*
    public static MusicProvider getInstance() {
        return LazyHolder.INSTANCE;
    }
     */

    private MediaBrowserCompat.MediaItem createTracksMediaItem(MediaMetadataCompat metadata) {
        // Since mediaMetadata fields are immutable, we need to create a copy, so we
        // can set a hierarchy-aware mediaID. We will need to know the media hierarchy
        // when we get a onPlayFromMusicID call, so we can create the proper queue based
        // on where the music was selected from (by artist, by genre, random, etc)

        String hierarchyAwareMediaID = metadata.getDescription().getMediaId();
        Timber.d("hierarch " +hierarchyAwareMediaID);

        MediaMetadataCompat copy = new MediaMetadataCompat.Builder(metadata).putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, hierarchyAwareMediaID).build();

        return new MediaBrowserCompat.MediaItem(copy.getDescription(),
                MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);

    }


    public MediaMetadataCompat getMusic(String musicId) {
        for (MediaMetadataCompat metadataCompat : musicList) {
            if (musicId.equals(metadataCompat.getDescription().getMediaId())) {
                return metadataCompat;
            }
        }
        return null;
    }

    public interface Callback {
        void onMusicCatalogReady(boolean success);
    }


    public Iterator<MediaMetadataCompat> iterator(ArrayList<SongItem> songItemArrayList) {

        ArrayList<MediaMetadataCompat> tracks = new ArrayList<>();
        for (int j = 0; j < songItemArrayList.size(); j++) {
            SongItem songItem = songItemArrayList.get(j);
            tracks.add(buildFromJSON(songItem));
        }

        return tracks.iterator();

    }


    private MediaMetadataCompat buildFromJSON(SongItem songItem) {
        String title = songItem.getTitle();
        String album = songItem.getAlbum();
        String artist = songItem.getArtist();
        String genre = songItem.getGenre();
        String source = "https://vod.rockerzs.com/music/numb/master.m3u8";
        String iconUrl = basePath + songItem.getImage();
        int trackNumber = songItem.getTrackNumber();
        int totalTrackCount = songItem.getTotalTrackCount();
        int duration = songItem.getDuration() * 1000; // ms

        // Since we don't have a unique ID in the server, we fake one using the hashcode of
        // the music source. In a real world app, this could come from the server.
        String id = "" + duration;
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, id)
//                .putString(MusicProviderSource.CUSTOM_METADATA_TRACK_SOURCE, source)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, source)
                //.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, "https://vod.rockerzs.com/music/numb/master.m3u8")
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
                .putString(MediaMetadataCompat.METADATA_KEY_GENRE, genre)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, iconUrl)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, trackNumber)
                .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, totalTrackCount)
                .build();
    }

    /*
    private static class LazyHolder {
        public static final MusicProvider INSTANCE = new MusicProvider();
    }

     */

}
