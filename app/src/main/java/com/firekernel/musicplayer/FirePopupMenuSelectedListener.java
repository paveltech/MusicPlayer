package com.firekernel.musicplayer;


import com.firekernel.musicplayer.pojo.SongItem;

/**
 * Created by Ashish on 7/5/2017.
 */

public interface FirePopupMenuSelectedListener {
    void onPlaySelected(SongItem item);

    void onShareSelected(SongItem item);
}
