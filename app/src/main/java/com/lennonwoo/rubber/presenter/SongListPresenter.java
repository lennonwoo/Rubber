package com.lennonwoo.rubber.presenter;

import android.util.Log;

import com.lennonwoo.rubber.contract.MusicDataSourceContract;
import com.lennonwoo.rubber.contract.SongListContract;
import com.lennonwoo.rubber.data.model.local.Album;
import com.lennonwoo.rubber.data.model.local.Song;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SongListPresenter implements SongListContract.Presenter {

    public static final String TAG = SongListPresenter.class.getSimpleName();

    private SongListContract.View view;

    private MusicDataSourceContract mMusicRepository;

    private CompositeSubscription mSubscriptions;

    private List<Song> mSongs;
    private Map<Long, String> albumArtMap;

    public SongListPresenter(SongListContract.View songListView, MusicDataSourceContract musicRepository) {
        view = songListView;
        mMusicRepository = musicRepository;
        mSubscriptions = new CompositeSubscription();
        view.setPresenter(this);
    }

    @Override
    public void subscribe() {
        init();
        loadAlbumList();
        loadSongList();
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    @Override
    public void loadSongList() {
        Subscription subscription = mMusicRepository
                .getSongList()
                .flatMap(new Func1<List<Song>, Observable<Song>>() {
                    @Override
                    public Observable<Song> call(List<Song> songs) {
                        return Observable.from(songs);
                    }
                })
                .filter(new Func1<Song, Boolean>() {
                    @Override
                    public Boolean call(Song song) {
                        // There to choose different song/album/artist
                        return true;
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Song>>() {
                    @Override
                    public void onCompleted() {
                        //TODO
                        Log.d(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        //TODO
                        Log.d(TAG, "onError");
                    }

                    @Override
                    public void onNext(List<Song> songs) {
                        Log.d(TAG, "onNext");
                        long startTime = System.currentTimeMillis();
                        for (Song song : songs) {
                            song.setArtPath(albumArtMap.get(song.getAlbumId()));
                        }
                        long endTiem = System.currentTimeMillis();
                        Log.d(TAG, "passed time : " + (endTiem - startTime));
                        Collections.shuffle(songs);
                        processSongs(songs);
                    }
                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void loadAlbumList() {
//        mSubscriptions.clear();
        Subscription subscription = mMusicRepository
                .getAlbumList()
                .flatMap(new Func1<List<Album>, Observable<Album>>() {
                    @Override
                    public Observable<Album> call(List<Album> alba) {
                        return Observable.from(alba);
                    }
                })
                .filter(new Func1<Album, Boolean>() {
                    @Override
                    public Boolean call(Album album) {
                        // There to choose different song/album/artist
                        return true;
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Album>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Album> alba) {
                        for (Album album : alba) {
                            albumArtMap.put(album.getAlbumId(), album.getArtPath());
                        }
                    }
                });
        mSubscriptions.add(subscription);
    }

    private void init() {
        albumArtMap = new HashMap<>();
    }

    private void processSongs(List<Song> songs) {
        if (songs.size() == 0) {
            view.showEmptyLayout();
        } else {
            view.setRecyclerItems(songs);
        }
    }
}
