package com.lennonwoo.rubber.data.source;

import com.lennonwoo.rubber.contract.MusicDataSourceContract;
import com.lennonwoo.rubber.data.model.local.Album;
import com.lennonwoo.rubber.data.model.local.Song;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.functions.Action1;

public class MusicRepository implements MusicDataSourceContract{

    public static final String TAG = MusicRepository.class.getSimpleName();

    private static MusicRepository INSTANCE;

    private MusicDataSourceContract.LocalDataSource mLocalDataSource;

    private MusicDataSourceContract.RemoteDataSource mRemoteDataSource;

    //cache source
    List<Song> songList;
    List<Album> albumList;
    Map<Long, String> albumArtMap;

    private MusicRepository(MusicDataSourceContract.LocalDataSource localDataSource,
                            MusicDataSourceContract.RemoteDataSource remoteDataSource) {
        mLocalDataSource = localDataSource;
        mRemoteDataSource = remoteDataSource;
        init();
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
        return Observable.from(songList).toList();
    }

    @Override
    public Observable<List<Album>> getAlbumList() {
        return Observable.from(albumList).toList();
    }

    private void init() {
        albumArtMap = new HashMap<>();
        mLocalDataSource.getAlbumList()
                .subscribe(new Action1<List<Album>>() {
                    @Override
                    public void call(List<Album> alba) {
                        albumList = alba;
                        for (Album album : alba) {
                            albumArtMap.put(album.getAlbumId(), album.getArtPath());
                        }
                    }
                });
        mLocalDataSource.getSongList()
                .subscribe(new Action1<List<Song>>() {
                    @Override
                    public void call(List<Song> songs) {
                        songList = songs;
                        for (Song song : songList) {
                            song.setArtPath(albumArtMap.get(song.getAlbumId()));
                        }
                    }
                });
    }

}
