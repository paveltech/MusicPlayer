package com.firekernel.musicplayer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;

import androidx.fragment.app.Fragment;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.firekernel.musicplayer.FirePopupMenuSelectedListener;
import com.firekernel.musicplayer.R;
import com.firekernel.musicplayer.ui.fragment.MediaListFragment;
import com.firekernel.musicplayer.ui.fragment.PlaybackControlsFragment;
import com.firekernel.musicplayer.utils.ActionHelper;
import com.firekernel.musicplayer.utils.FireLog;
import com.firekernel.musicplayer.utils.ImageHelper;
import com.firekernel.musicplayer.utils.MediaIDHelper;

public class MainActivity extends PlaybackBaseActivity implements MediaListFragment.OnMediaItemSelectedListener, FirePopupMenuSelectedListener {

    private static final String TAG = FireLog.makeLogTag(MainActivity.class);

    private ImageView headerBgView;
    private ImageView bgView;
    private String title;
    private String mArtUrl = ""; //do not set null


    private final MediaControllerCompat.Callback mediaControllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
            FireLog.d(TAG, "(++) onPlaybackStateChanged state= " + state.getState());
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            if (metadata == null) {
                FireLog.e(TAG, "(++) MediaController.Callback.onMetadataChanged: metadata is null");
                return;
            }
            MainActivity.this.onMetadataChanged(metadata); // always use this context
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bgView = (ImageView) findViewById(R.id.bgView);


        if (savedInstanceState == null) {
            // Set the default view when activity is launched on the first time
            loadPrimaryView();
            // Only check if a Now Playing is needed on the first time
            ActionHelper.startNowPlayingActivityIfNeeded(this, getIntent());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FireLog.d(TAG, "(++) onStart");
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(this);
        if (controller != null) {
            onConnected();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        FireLog.d(TAG, "(++) onNewIntent, intent=" + intent);
        ActionHelper.startNowPlayingActivityIfNeeded(this, intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FireLog.d(TAG, "(++) onStop");
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(this);
        if (controller != null) {
            FireLog.d(TAG, "Unregister callback=" + mediaControllerCallback);
            controller.unregisterCallback(mediaControllerCallback);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onMediaControllerConnected() {

        Fragment fragment = getMediaListFragment();
        if (fragment != null) {
            ((MediaListFragment) fragment).onConnected();
        }

        Fragment fragmentControl = getControlFragment();
        if (fragmentControl != null) {
            ((PlaybackControlsFragment) fragmentControl).onConnected();
        }

        this.onConnected();
    }


    @Override
    public void onMediaItemSelected(MediaBrowserCompat.MediaItem item) {
        FireLog.d(TAG, "(++) onMediaItemSelected, mediaitem=" + item);
        if (item.isPlayable()) {
            MediaControllerCompat.getMediaController(this).getTransportControls()
                    .playFromMediaId("103000", null);
        }
    }

    @Override
    public void onPlaySelected(MediaBrowserCompat.MediaItem item) {
        FireLog.d(TAG, "(++) onPlaySelected");
        if (item.isPlayable()) {
            MediaControllerCompat.getMediaController(this).getTransportControls()
                    .playFromMediaId(item.getMediaId(), null);
        }
    }

    @Override
    public void onShareSelected(MediaBrowserCompat.MediaItem item) {
        ActionHelper.shareTrack(this, item.getDescription());
    }



    private MediaListFragment getMediaListFragment() {
        return (MediaListFragment) getSupportFragmentManager().findFragmentByTag(MediaListFragment.TAG);
    }

    private PlaybackControlsFragment getControlFragment() {
        return (PlaybackControlsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_playback_controls);
    }

    private void loadPrimaryView() {
        FireLog.d(TAG, "(++) loadPrimaryView");
        onNavigationItemSelectedForFragment(R.id.nav_tracks);
    }

    private void onNavigationItemSelectedForFragment(int id) {

        Fragment fragment = null;
        String tag = null;

        title = getResources().getString(R.string.nav_menu_tracks);
        fragment = MediaListFragment.newInstance(title, MediaIDHelper.MEDIA_ID_TRACKS_ALL);
        tag = MediaListFragment.TAG;
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.flContent, fragment, tag)
                .commit();
    }

    private void onConnected() {
        FireLog.d(TAG, "onConnected");
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(this);
        if (controller != null) {
            MainActivity.this.onMetadataChanged(controller.getMetadata());
            FireLog.d(TAG, "Register callback=" + mediaControllerCallback);
            controller.registerCallback(mediaControllerCallback);
        }
    }

    private void onMetadataChanged(MediaMetadataCompat metadata) {
        FireLog.d(TAG, "(++) onMetadataChanged " + metadata);

        if (isFinishing() || isDestroyed()) {
            return;
        }
        if (metadata == null) {
            ImageHelper.loadBlurBg(this, headerBgView);
            ImageHelper.loadBlurBg(this, bgView);
            return;
        }

        String artUrl = null;
        if (metadata.getDescription().getIconUri() != null) {
            artUrl = metadata.getDescription().getIconUri().toString();
        }
        FireLog.d(TAG, "mArtUrl=" + mArtUrl + ", artUrl=" + artUrl);
        if (!TextUtils.equals(artUrl, mArtUrl)) {
            mArtUrl = artUrl;
            ImageHelper.loadBlurBg(this, bgView, metadata.getDescription());
            ImageHelper.loadBlurBg(this, headerBgView, metadata.getDescription());
        }
    }

}

