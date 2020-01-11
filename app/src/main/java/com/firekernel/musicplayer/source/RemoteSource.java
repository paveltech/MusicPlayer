package com.firekernel.musicplayer.source;

import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import com.firekernel.musicplayer.pojo.SongItem;
import com.firekernel.musicplayer.utils.FireLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

import timber.log.Timber;

/**
 * Utility class to get a list of MusicTrack's based on a server-side JSON
 * configuration.
 */
public class RemoteSource {

    protected static final String CATALOG_URL = "http://storage.googleapis.com/automotive-media/music.json";
    protected static final String basePath = "http://storage.googleapis.com/automotive-media/";


    public ArrayList<SongItem> songItemArrayList;
    public ArrayList<MediaMetadataCompat> mediaMetadataCompatArrayList;
    public MusicProviderSource musicProviderSource;


    public void add(ArrayList<SongItem> songItemArrayList) {
        this.songItemArrayList = songItemArrayList;
        Timber.d("song item " + songItemArrayList.size());
        makeData();
    }

    public RemoteSource(MusicProviderSource musicProviderSource) {
        this.musicProviderSource = musicProviderSource;
        songItemArrayList = new ArrayList<>();
        mediaMetadataCompatArrayList = new ArrayList<>();
    }

    public void makeData() {
        for (int j = 0; j < songItemArrayList.size(); j++) {
            SongItem songItem = songItemArrayList.get(j);
            mediaMetadataCompatArrayList.add(buildFromJSON(songItem));
        }

        Timber.d("media " + mediaMetadataCompatArrayList.size());
        musicProviderSource.iterator(mediaMetadataCompatArrayList);
    }


    private MediaMetadataCompat buildFromJSON(SongItem songItem) {

        String title = songItem.getTitle();
        String album = songItem.getAlbum();
        String artist = songItem.getArtist();
        String genre = songItem.getGenre();
        //String source = songItem.getSource();
        String source = "https://vod.rockerzs.com/music/numb/master.m3u8";
        String iconUrl = songItem.getImage();
        int trackNumber = songItem.getTrackNumber();
        int totalTrackCount = songItem.getTotalTrackCount();
        int duration = songItem.getDuration() * 1000; // ms


        if (!iconUrl.startsWith("http")) {
            iconUrl = basePath + iconUrl;
        }

        String id = String.valueOf(source.hashCode());

        // Adding the music source to the MediaMetadata (and consequently using it in the
        // mediaSession.setMetadata) is not a good idea for a real world music app, because
        // the session metadata can be accessed by notification listeners. This is done in this
        // sample for convenience only.


        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, id)
//                .putString(MusicProviderSource.CUSTOM_METADATA_TRACK_SOURCE, source)
                //.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, source)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, source)
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
}
