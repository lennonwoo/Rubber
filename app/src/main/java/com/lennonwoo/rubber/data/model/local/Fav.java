package com.lennonwoo.rubber.data.model.local;

public class Fav {

    private long songId;

    public Fav(long song_id) {
        songId = song_id;
    }

    public long getSongId() {
        return songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }

}
