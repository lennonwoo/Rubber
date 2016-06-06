package com.lennonwoo.rubber.contract;

import com.lennonwoo.rubber.data.model.local.Album;
import com.lennonwoo.rubber.data.model.local.Song;

import java.util.List;

import rx.Observable;

public interface MusicDataSourceContract {

    Observable<List<Song>> getSongList();

    Observable<List<Album>> getAlbumList();


    interface LocalDataSource {

        Observable<List<Song>> getSongList();

        Observable<List<Album>> getAlbumList();

    }

    interface RemoteDataSource {

    }

}
