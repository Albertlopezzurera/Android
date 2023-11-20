package com.example.reproductormusica;

import android.net.Uri;

public class Cancion {

    public int id;
    public String artist;
    public String title;
    public long duration;
    public Uri uri;

    public Cancion(int id, String artist, String title, long duration, Uri uri) {
        this.id = id;
        this.artist = artist;
        this.title = title;
        this.duration = duration;
        this.uri = uri;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    @Override
    public String toString() {
        return title + ("<unknown>".equals(artist) ? "" : " - " + artist);
    }
}
