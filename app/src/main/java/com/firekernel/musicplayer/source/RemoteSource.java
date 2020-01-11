package com.firekernel.musicplayer.source;

import android.support.v4.media.MediaMetadataCompat;

import com.firekernel.musicplayer.pojo.SongItem;

import java.util.ArrayList;

/**
 * Utility class to get a list of MusicTrack's based on a server-side JSON
 * configuration.
 */
public class RemoteSource {

    protected static final String basePath = "http://storage.googleapis.com/automotive-media/";


    public ArrayList<MediaMetadataCompat> makeData(ArrayList<SongItem> songItemArrayList) {
        ArrayList<MediaMetadataCompat> mediaMetadataCompatArrayList = new ArrayList<>();
        for (int j = 0; j < songItemArrayList.size(); j++) {
            SongItem songItem = songItemArrayList.get(j);
            mediaMetadataCompatArrayList.add(buildFromJSON(songItem));
        }
        return mediaMetadataCompatArrayList;
    }


    private MediaMetadataCompat buildFromJSON(SongItem songItem) {
        String title = songItem.getTitle();
        String album = songItem.getAlbum();
        String artist = songItem.getArtist();
        String genre = songItem.getGenre();
        //String source = songItem.getSource();
        String source = "https://vod.rockerzs.com/music/numb/master.m3u8";
        String iconUrl = basePath+ songItem.getImage();
        int trackNumber = songItem.getTrackNumber();
        int totalTrackCount = songItem.getTotalTrackCount();
        int duration = songItem.getDuration() * 1000; // ms
        String id = ""+songItem.getDuration();


        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, id)
                //.putString(MusicProviderSource.CUSTOM_METADATA_TRACK_SOURCE, source)
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
