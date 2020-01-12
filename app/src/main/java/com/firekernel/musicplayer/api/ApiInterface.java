package com.firekernel.musicplayer.api;

import com.firekernel.musicplayer.pojo.SongResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {
    @GET("music.json")
    Call<SongResponse> getSongs();
}
