package com.firekernel.musicplayer.source;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import com.firekernel.musicplayer.utils.MediaIDHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;

import static com.firekernel.musicplayer.utils.MediaIDHelper.MEDIA_ID_TRACKS_ALL;


/**
 * Simple data provider for music tracks. The actual metadata localSource is delegated to a
 * MusicProviderSource defined by a constructor argument of this class.
 * MediaId = Category/SubCategory|musicId
 */
public class MusicProvider implements MusicProviderSource{

    private  CopyOnWriteArrayList<MediaMetadataCompat> musicList;
    private  CopyOnWriteArrayList<MediaMetadataCompat> mediaList;

    ExecutorService executorService = Executors.newSingleThreadExecutor();
    private RemoteSource remoteSource;
    public Iterator<MediaMetadataCompat> mediaMetadataCompatArrayList;


    //private MusicProviderSource remoteSource;

    private MusicProvider() {
        remoteSource = new RemoteSource(this);
        musicList = new CopyOnWriteArrayList<>();
        mediaList = new CopyOnWriteArrayList<>();
    }

    public static MusicProvider getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * Get the list of music tracks from a server and caches the track information
     * for future reference, keying tracks by musicId and grouping by genre.
     */

    @SuppressLint("StaticFieldLeak")
    public void retrieveMediaAsync(final String mediaId, final Callback callback) {

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return retrieveMedia(mediaId);
            }

            @Override
            protected void onPostExecute(Boolean initialized) {
                if (callback != null) {
                    callback.onMusicCatalogReady(initialized);
                }
            }
        }.executeOnExecutor(executorService);
    }

    private synchronized boolean retrieveMedia(String mediaId) {


        boolean initialized = false;
        mediaList.clear();
        try {
            Iterator<MediaMetadataCompat> tracks = mediaMetadataCompatArrayList;

            while (tracks.hasNext()) {
                MediaMetadataCompat item = tracks.next();
                mediaList.add(item);
            }
            initialized = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return initialized;
    }

    public List<MediaBrowserCompat.MediaItem> getChildren(String mediaId) {

        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();
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

    private MediaBrowserCompat.MediaItem createTracksMediaItem(MediaMetadataCompat metadata) {
        // Since mediaMetadata fields are immutable, we need to create a copy, so we
        // can set a hierarchy-aware mediaID. We will need to know the media hierarchy
        // when we get a onPlayFromMusicID call, so we can create the proper queue based
        // on where the music was selected from (by artist, by genre, random, etc)

        String hierarchyAwareMediaID = MediaIDHelper.createMediaID(metadata.getDescription().getMediaId(), "", MEDIA_ID_TRACKS_ALL);
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

    @Override
    public Iterator<MediaMetadataCompat> iterator(ArrayList<MediaMetadataCompat> mediaMetadataCompats) {
        this.mediaMetadataCompatArrayList = mediaMetadataCompats.iterator();
        Timber.d("music provider: "+mediaMetadataCompatArrayList.hasNext());
        return mediaMetadataCompatArrayList;
    }

    public interface Callback {
        void onMusicCatalogReady(boolean success);
    }
    private static class LazyHolder {
        public static final MusicProvider INSTANCE = new MusicProvider();
    }

}
