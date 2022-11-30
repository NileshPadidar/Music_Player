package com.example.mymusicplayer;

import java.io.Serializable;

public class AudioModel implements Serializable {
    String path;
    String title;
    String duration;
    String artist;
    String album;

    public AudioModel(String path, String title, String duration, String artist, String Album) {
        this.path = path;
        this.title = title;
        this.duration = duration;
        this.artist = artist;
        this.album = Album;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
}
