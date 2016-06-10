package com.lennonwoo.rubber.presenter;

import com.lennonwoo.rubber.contract.MusicDataSourceContract;
import com.lennonwoo.rubber.contract.PlayerContract;
import com.lennonwoo.rubber.data.model.local.Song;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class PlayerPresenter implements PlayerContract.Presenter {

    private PlayerContract.View view;

    private MusicDataSourceContract mMusicRepository;

    private CompositeSubscription mSubscriptions;

    private List<Song> currentSongList;

    private int playingSongIndex;


    public PlayerPresenter(PlayerContract.View playerView, MusicDataSourceContract musicRepository) {
        view = playerView;
        mMusicRepository = musicRepository;
        mSubscriptions = new CompositeSubscription();
        view.setPresenter(this);
    }

    @Override
    public void subscribe() {
        playingSongIndex = 0;
        mMusicRepository.refreshRepository();
        loadAllPlaylist();
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    @Override
    public void loadFavPlaylist() {
        mSubscriptions.clear();
        Subscription subscription =
                mMusicRepository.getPlaylist(MusicDataSourceContract.PlaylistType.FAV)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Song>>() {
                    @Override
                    public void call(List<Song> songs) {
                        currentSongList = songs;
                        refreshView();
                    }
                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void loadAllPlaylist() {
        mSubscriptions.clear();
        Subscription subscription =
                mMusicRepository.getPlaylist(MusicDataSourceContract.PlaylistType.ALL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Song>>() {
                    @Override
                    public void call(List<Song> songs) {
                        currentSongList = songs;
                        refreshView();
                    }
                });
        mSubscriptions.add(subscription);
    }

    private void refreshView() {
        view.setRecyclerItems(currentSongList);
        view.setPlayingSongInfo(currentSongList.get(playingSongIndex));
    }

}
