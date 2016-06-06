package com.lennonwoo.rubber.data.source;

import com.lennonwoo.rubber.contract.MusicDataSourceContract;
import com.lennonwoo.rubber.data.model.local.Song;

import java.util.List;

import rx.Observable;

public class MusicRepository implements MusicDataSourceContract{

    private static MusicRepository INSTANCE;

    private MusicDataSourceContract.LocalDataSource mLocalDataSource;

    private MusicDataSourceContract.RemoteDataSource mRemoteDataSource;

    private MusicRepository(MusicDataSourceContract.LocalDataSource localDataSource,
                            MusicDataSourceContract.RemoteDataSource remoteDataSource) {
        mLocalDataSource = localDataSource;
        mRemoteDataSource = remoteDataSource;
    }

    public static MusicRepository getInstance(MusicDataSourceContract.LocalDataSource localDataSource,
                                       MusicDataSourceContract.RemoteDataSource remoteDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new MusicRepository(localDataSource, remoteDataSource);
        }
        return INSTANCE;
    }

    @Override
    public Observable<List<Song>> getSongList() {
        return mLocalDataSource.getSongList();
    }

}
