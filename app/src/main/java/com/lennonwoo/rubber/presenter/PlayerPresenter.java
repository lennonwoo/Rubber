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
        loadAllPlaylist(0);
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    @Override
    public void loadFavPlaylist(final long songId) {
        mSubscriptions.clear();
        Subscription subscription =
                mMusicRepository.getPlaylist(MusicDataSourceContract.PlaylistType.FAV)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Song>>() {
                    @Override
                    public void call(List<Song> songs) {
                        updatePlaylist(songs, songId);
                        refreshView();
                    }
                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void loadAllPlaylist(final long songId) {
        mSubscriptions.clear();
        Subscription subscription =
                mMusicRepository.getPlaylist(MusicDataSourceContract.PlaylistType.ALL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Song>>() {
                    @Override
                    public void call(List<Song> songs) {
                        updatePlaylist(songs, songId);
                        refreshView();
                    }
                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void refreshView() {
        view.setRecyclerItems(currentSongList);
        view.setPlayingSongInfo(currentSongList.get(playingSongIndex));
    }

    @Override
    public Song getCurrentPlayingSong() {
        return currentSongList.get(playingSongIndex);
    }

    @Override
    public Song getPrevSong() {
        playingSongIndex--;
        if (playingSongIndex < 0) {
            playingSongIndex = currentSongList.size() - 1;
        }
        return getCurrentPlayingSong();
    }

    @Override
    public Song getNextSong() {
        playingSongIndex++;
        if (playingSongIndex >= currentSongList.size()) {
            playingSongIndex = 0;
        }
        return getCurrentPlayingSong();
    }

    private void updatePlaylist(List<Song> songs, long songId) {
        currentSongList = songs;
        for (Song song : currentSongList) {
            if (song.getSongId() == songId) {
                playingSongIndex = currentSongList.indexOf(song);
            }
        }
    }

}
