package com.lennonwoo.rubber.contract;

import com.lennonwoo.rubber.data.model.local.Song;

import java.util.List;

import rx.Observable;

public interface MusicDataSourceContract {

    Observable<List<Song>> getSongList();

    interface LocalDataSource {

        Observable<List<Song>> getSongList();

    }

    interface RemoteDataSource {

    }

}
