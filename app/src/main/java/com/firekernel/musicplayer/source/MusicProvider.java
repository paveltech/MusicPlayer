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
public class MusicProvider{

    private  CopyOnWriteArrayList<MediaMetadataCompat> musicList;
    private  CopyOnWriteArrayList<MediaMetadataCompat> mediaList;


    public Iterator<MediaMetadataCompat> mediaMetadataCompatArrayList;


    public MusicProvider() {
        musicList = new CopyOnWriteArrayList<>();
        mediaList = new CopyOnWriteArrayList<>();
    }

    public Iterator<MediaMetadataCompat> setIntoMedia(ArrayList<MediaMetadataCompat> mediaMetadataCompats){
        mediaMetadataCompatArrayList = mediaMetadataCompats.iterator();
        return mediaMetadataCompatArrayList;
    }


    public synchronized boolean retrieveMedia() {
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

    public List<MediaBrowserCompat.MediaItem> getChildren() {
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

        String hierarchyAwareMediaID = MediaIDHelper.createMediaID(metadata.getDescription().getMediaId(), "", MEDIA_ID_TRACKS_ALL);
        Timber.d("hierarch " +hierarchyAwareMediaID);

        MediaMetadataCompat copy = new MediaMetadataCompat.Builder(metadata).putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, hierarchyAwareMediaID).build();

        return new MediaBrowserCompat.MediaItem(copy.getDescription(),
                MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);

    }

    public MediaMetadataCompat getMusic(String id) {
        for (MediaMetadataCompat metadataCompat : musicList) {
            if (id.equals(metadataCompat.getDescription().getMediaId())) {
                return metadataCompat;
            }
        }
        return null;
    }


}
