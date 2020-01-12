package com.firekernel.musicplayer.utils;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.firekernel.musicplayer.source.MusicProvider;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Utility class to help on queue related tasks.
 */
public class QueueHelper {

    private static final String TAG = FireLog.makeLogTag(QueueHelper.class);

    public static List<MediaSessionCompat.QueueItem> getPlayingQueue(String mediaId, MusicProvider musicProvider) {
        FireLog.d(TAG, "(++) getPlayingQueue: mediaId=" + mediaId);

        // extract the browsing hierarchy from the media ID:
        String[] hierarchy = MediaIDHelper.getHierarchy(mediaId);

        Timber.d("hierarchy" +hierarchy.length);

        /*
        if (hierarchy.length != 2) {
            FireLog.e(TAG, "Could not build a playing queue for this mediaId: " + mediaId);
            return null;
        }

         */

        //String categoryType = hierarchy[0];
        //String categoryValue = hierarchy[1];

        List<MediaMetadataCompat> tracks = musicProvider.getAllRetrievedMetadata();

        return convertToQueue(tracks);
    }

    private static List<MediaSessionCompat.QueueItem> convertToQueue(Iterable<MediaMetadataCompat> tracks) {
        FireLog.d(TAG, "(++) convertToQueue: tracks=" + tracks);
        List<MediaSessionCompat.QueueItem> queue = new ArrayList<>();
        long id = 0;
        for (MediaMetadataCompat track : tracks) {
            String hierarchyAwareMediaID = track.getDescription().getMediaId();
            MediaMetadataCompat trackCopy = new MediaMetadataCompat.Builder(track)
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, hierarchyAwareMediaID)
                    .build();
            MediaSessionCompat.QueueItem item = new MediaSessionCompat.QueueItem(trackCopy.getDescription(), id++);
            queue.add(item);
        }
        return queue;

    }

    public static int getMusicIndexOnQueue(Iterable<MediaSessionCompat.QueueItem> queue, String mediaId) {
        FireLog.d(TAG, "(++) getMusicIndexOnQueue: mediaId=" + mediaId);
        int index = 0;

        if (queue==null){
            FireLog.d(TAG, "++ Queue is null" + mediaId);
        }

        for (MediaSessionCompat.QueueItem item : queue) {
            FireLog.d(TAG, "(++) queue : mediaId=" + item.getDescription().getMediaId());
            if (mediaId.equals(item.getDescription().getMediaId())) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public static int getMusicIndexOnQueue(Iterable<MediaSessionCompat.QueueItem> queue,
                                           long queueId) {
        FireLog.d(TAG, "(++) getMusicIndexOnQueue: queueId=" + queueId);
        int index = 0;
        for (MediaSessionCompat.QueueItem item : queue) {
            if (queueId == item.getQueueId()) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public static boolean isIndexPlayable(int index, List<MediaSessionCompat.QueueItem> queue) {
        return (queue != null && index >= 0 && index < queue.size());
    }
}
