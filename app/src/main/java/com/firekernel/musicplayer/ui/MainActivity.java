package com.firekernel.musicplayer.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.firekernel.musicplayer.api.ApiClient;
import com.firekernel.musicplayer.api.ApiInterface;
import com.firekernel.musicplayer.pojo.SongItem;
import com.firekernel.musicplayer.pojo.SongResponse;
import com.firekernel.musicplayer.ui.adapter.MediaListAdapter;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firekernel.musicplayer.FirePopupMenuSelectedListener;
import com.firekernel.musicplayer.R;
import com.firekernel.musicplayer.utils.ActionHelper;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MainActivity extends PlaybackBaseActivity implements MediaListAdapter.OnMediaItemSelectedListener, FirePopupMenuSelectedListener {

    public ApiInterface apiInterface;
    public MediaListAdapter mediaListAdapter;
    public RecyclerView recyclerView;


    private final MediaControllerCompat.Callback mediaControllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
            Timber.d( "(++) onPlaybackStateChanged state= " + state.getState());
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            if (metadata == null) {
                Timber.d(  "(++) MediaController.Callback.onMetadataChanged: metadata is null");
                return;
            }
            MainActivity.this.onMetadataChanged(metadata); // always use this context
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiInterface = ApiClient.getRetrofitInstance().create(ApiInterface.class);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        apiCall();
    }

    public void dataShowIntoList(ArrayList<SongItem> songItemArrayList){
        mediaListAdapter = new MediaListAdapter(getApplicationContext(), songItemArrayList , this);
        recyclerView.setAdapter(mediaListAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Timber.d(  "(++) onStart");
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(this);
        if (controller != null) {
            onConnected();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Timber.d(  "(++) onNewIntent, intent=" + intent);
        ActionHelper.startNowPlayingActivityIfNeeded(this, intent);
    }


    @Override
    protected void onStop() {
        super.onStop();
        Timber.d( "(++) onStop");
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(this);
        if (controller != null) {
            controller.unregisterCallback(mediaControllerCallback);
        }
    }

    @Override
    protected void onMediaControllerConnected() {
        this.onConnected();
    }

    /*
    @Override
    public void onMediaItemSelected(MediaBrowserCompat.MediaItem item) {
        FireLog.d(TAG, "(++) onMediaItemSelected, mediaitem=" + item);
        if (item.isPlayable()) {
            MediaControllerCompat.getMediaController(this).getTransportControls()
                    .playFromMediaId(item.getMediaId(), null);
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
//        ActionHelper.shareTrack(this, item.getDescription().getMediaId());
    }



    private MediaListFragment getMediaListFragment() {
        return (MediaListFragment) getSupportFragmentManager().findFragmentByTag(MediaListFragment.TAG);
    }

    private PlaybackControlsFragment getControlFragment() {
        return (PlaybackControlsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_playback_controls);
    }

     */

    private void onConnected() {
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(this);
        if (controller != null) {
            MainActivity.this.onMetadataChanged(controller.getMetadata());
            controller.registerCallback(mediaControllerCallback);
        }
    }

    private void onMetadataChanged(MediaMetadataCompat metadata) {
        Timber.d(  "(++) onMetadataChanged " + metadata);

    }

    private void apiCall() {
        Call<SongResponse> songResponseCall = apiInterface.getSongs();
        songResponseCall.enqueue(new Callback<SongResponse>() {
            @Override
            public void onResponse(Call<SongResponse> call, Response<SongResponse> response) {
                if (response.isSuccessful()) {
                    Timber.d("response " + response.body().getMusic());
                    dataShowIntoList(response.body().getMusic());
                }
            }

            @Override
            public void onFailure(Call<SongResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onPlaySelected(SongItem item) {

    }

    @Override
    public void onShareSelected(SongItem item) {

    }

    @Override
    public void onMediaItemSelected(SongItem item) {

    }

}

