package com.lennonwoo.rubber.contract;

import com.lennonwoo.rubber.data.model.local.Album;
import com.lennonwoo.rubber.data.model.local.Fav;
import com.lennonwoo.rubber.data.model.local.Song;

import java.util.List;

import rx.Observable;

public interface MusicDataSourceContract {

    enum PlaylistType {
        ALL,
        FAV
    }

    Observable<List<Song>> getSongList();

    Observable<List<Album>> getAlbumList();

    Observable<List<Song>> getPlaylist(PlaylistType type);

    void refreshRepository();

    interface LocalDataSource {

        Observable<List<Song>> getSongList();

        Observable<List<Album>> getAlbumList();

        Observable<List<Fav>> getFavList();

    }

    interface RemoteDataSource {

    }

}
