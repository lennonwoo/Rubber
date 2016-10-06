package com.lennonwoo.rubber.data.model.local;

public class Song {

    long songId, artistId, albumId;

    String name, artist, album;

    // song's information
    long size, dataAdded;

    int duration;

    String path, artPath;

    boolean fav;

    public Song(long _id, long artist_id, long album_id,
                String title, String artist, String album,
                long _size, int duration, long date_added,
                String _data) {
        songId = _id;
        artistId = artist_id;
        albumId = album_id;
        name = title;
        this.artist = artist;
        this.album = album;
        size = _size;
        this.duration = duration;
        dataAdded = date_added;
        path = _data;
        artPath = null;
        //TODO change the fav to the tag I can add about what I feeling about it
        fav = false;
    }

    public long getSongId() {
        return songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getDataAdded() {
        return dataAdded;
    }

    public void setDataAdded(long dataAdded) {
        this.dataAdded = dataAdded;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getArtPath() {
        return artPath;
    }

    public void setArtPath(String artPath) {
        this.artPath = artPath;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }
}
