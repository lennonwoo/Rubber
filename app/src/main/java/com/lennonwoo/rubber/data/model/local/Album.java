package com.lennonwoo.rubber.data.model.local;

public class Album {

    long albumId, numSongs;

    String name, artistName, artPath;

    public Album(long _id, long numSongs,
                 String album, String artist, String album_path) {
        albumId = _id;
        this.numSongs = numSongs;
        name = album;
        artistName = artist;
        artPath = album_path;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getNumSongs() {
        return numSongs;
    }

    public void setNumSongs(long numSongs) {
        this.numSongs = numSongs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getArtPath() {
        return artPath;
    }

    public void setArtPath(String artPath) {
        this.artPath = artPath;
    }
}
