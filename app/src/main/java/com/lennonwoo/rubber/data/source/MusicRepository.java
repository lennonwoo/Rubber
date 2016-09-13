package com.lennonwoo.rubber.data.source;

import com.lennonwoo.rubber.contract.MusicDataSourceContract;
import com.lennonwoo.rubber.data.model.local.Album;
import com.lennonwoo.rubber.data.model.local.Fav;
import com.lennonwoo.rubber.data.model.local.Song;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

public class MusicRepository implements MusicDataSourceContract{

    public static final String TAG = MusicRepository.class.getSimpleName();

    private static MusicRepository INSTANCE;

    private MusicDataSourceContract.LocalDataSource mLocalDataSource;

    private MusicDataSourceContract.RemoteDataSource mRemoteDataSource;

    //cache source
    private List<Song> songListCache;
    private List<Album> albumListCache;
    private List<Fav> favListCache;
    private List<Song> playlistCache;

    //album's id with album art't path
    private Map<Long, String> albumArtMap;
    //song's id with class Song
    private Map<Long, Song> songMap;

    private boolean cacheIsDirty;

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

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public Observable<List<Song>> getSongList() {
        return Observable.from(songListCache).toList();
    }

    @Override
    public Observable<List<Album>> getAlbumList() {
        return Observable.from(albumListCache).toList();
    }

    @Override
    public Observable<List<Song>> getPlaylist(PlaylistType type) {
        if (!cacheIsDirty) {
            return Observable.from(playlistCache).toList();
        } else {
            switch (type) {
                case ALL:
                    playlistCache = songListCache;
                    return Observable.from(playlistCache).toList();
                case FAV:
                    return mLocalDataSource.getFavList()
                            .flatMap(new Func1<List<Fav>, Observable<Song>>() {
                                @Override
                                public Observable<Song> call(List<Fav> favs) {
                                    playlistCache.clear();
                                    for (Fav fav : favs) {
                                        playlistCache.add(songMap.get(fav.getSongId()));
                                    }
                                    return Observable.from(playlistCache);
                                }
                            })
                            .doOnCompleted(new Action0() {
                                @Override
                                public void call() {
                                    cacheIsDirty = false;
                                }
                            })
                            .toList();
                default:
                    return null;
            }
        }
    }

    @Override
    public void saveFavSong(long songId) {
        mLocalDataSource.saveFavSong(songId);
    }

    @Override
    public void deleteFavSong(long songId) {
        mLocalDataSource.deleteFavSong(songId);
    }

    @Override
    public void refreshRepository() {
        cacheIsDirty = true;
    }

    private void init() {
        albumArtMap = new HashMap<>();
        songMap = new HashMap<>();
        cacheIsDirty = true;
        mLocalDataSource.getAlbumList()
                .subscribe(new Action1<List<Album>>() {
                    @Override
                    public void call(List<Album> alba) {
                        albumListCache = alba;
                        for (Album album : alba) {
                            albumArtMap.put(album.getAlbumId(), album.getArtPath());
                        }
                    }
                });
        mLocalDataSource.getSongList()
                .subscribe(new Action1<List<Song>>() {
                    @Override
                    public void call(List<Song> songs) {
                        songListCache = songs;
                        for (Song song : songListCache) {
                            song.setArtPath(albumArtMap.get(song.getAlbumId()));
                            songMap.put(song.getSongId(), song);
                        }
                        Collections.shuffle(songListCache);
                    }
                });
    }

}
