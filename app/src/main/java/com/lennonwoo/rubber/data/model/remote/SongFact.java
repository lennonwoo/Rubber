package com.lennonwoo.rubber.data.model.remote;

public class SongFact {
    private String songName;
    private String songFact;

    public SongFact(String songName, String songFact) {
        this.songName = songName;
        this.songFact = songFact;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongFact() {
        return songFact;
    }

    public void setSongFact(String songFact) {
        this.songFact = songFact;
    }
}
