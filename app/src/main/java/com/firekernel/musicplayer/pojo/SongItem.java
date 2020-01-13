package com.firekernel.musicplayer.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SongItem implements Parcelable {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("album")
    @Expose
    private String album;
    @SerializedName("artist")
    @Expose
    private String artist;
    @SerializedName("genre")
    @Expose
    private String genre;
    @SerializedName("source")
    @Expose
    private String source;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("trackNumber")
    @Expose
    private int trackNumber;
    @SerializedName("totalTrackCount")
    @Expose
    private int totalTrackCount;
    @SerializedName("duration")
    @Expose
    private int duration;
    @SerializedName("site")
    @Expose
    private String site;

    protected SongItem(Parcel in) {
        title = in.readString();
        album = in.readString();
        artist = in.readString();
        genre = in.readString();
        source = in.readString();
        image = in.readString();
        trackNumber = in.readInt();
        totalTrackCount = in.readInt();
        duration = in.readInt();
        site = in.readString();
    }

    public static final Creator<SongItem> CREATOR = new Creator<SongItem>() {
        @Override
        public SongItem createFromParcel(Parcel in) {
            return new SongItem(in);
        }

        @Override
        public SongItem[] newArray(int size) {
            return new SongItem[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }

    public int getTotalTrackCount() {
        return totalTrackCount;
    }

    public void setTotalTrackCount(int totalTrackCount) {
        this.totalTrackCount = totalTrackCount;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(album);
        parcel.writeString(artist);
        parcel.writeString(genre);
        parcel.writeString(source);
        parcel.writeString(image);
        parcel.writeInt(trackNumber);
        parcel.writeInt(totalTrackCount);
        parcel.writeInt(duration);
        parcel.writeString(site);
    }
}
