package com.firekernel.musicplayer.utils;

import android.app.Activity;
import android.content.Intent;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.widget.Toast;

import com.firekernel.musicplayer.R;
import com.firekernel.musicplayer.ui.NowPlayingActivity;

import java.io.File;

/**
 * Created by Ashish on 8/5/2017.
 */

public class ActionHelper {
    public static final String EXTRA_START_NOW_PLAYING = "com.firekernel.player.EXTRA_START_NOW_PLAYING";
    public static final String EXTRA_CURRENT_MEDIA_DESCRIPTION = "com.firekernel.player.CURRENT_MEDIA_DESCRIPTION";
    private static final String TAG = ActionHelper.class.getSimpleName();

    public static void startNowPlayingActivityIfNeeded(Activity activity, Intent intent) {
        if (intent != null && intent.getBooleanExtra(EXTRA_START_NOW_PLAYING, false)) {
            Intent nowPlayingIntent = new Intent(activity, NowPlayingActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .putExtra(EXTRA_CURRENT_MEDIA_DESCRIPTION, (Bundle) intent.getParcelableExtra(EXTRA_CURRENT_MEDIA_DESCRIPTION));
            activity.startActivity(nowPlayingIntent);
        }
    }

    public static void startNowPlayingActivity(Activity activity) {
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(activity);
        MediaMetadataCompat metadata = controller.getMetadata();

        Intent intent = new Intent(activity, NowPlayingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (metadata != null) {
            intent.putExtra(ActionHelper.EXTRA_CURRENT_MEDIA_DESCRIPTION,
                    metadata.getDescription());
        }
        activity.startActivity(intent);
    }


    public static void shareTrack(Activity activity, MediaDescriptionCompat description) {
        try {

            Uri uri = Uri.fromFile(new File(description.getMediaUri().toString()));
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("audio/*");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            activity.startActivity(Intent.createChooser(share, "Share Sound File"));
        } catch (Exception e) {
            FireLog.e(TAG, "", e);
        }
    }



}
