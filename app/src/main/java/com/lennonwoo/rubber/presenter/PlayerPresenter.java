package com.lennonwoo.rubber.presenter;

import com.lennonwoo.rubber.contract.MusicDataSourceContract;
import com.lennonwoo.rubber.contract.PlayerContract;
import com.lennonwoo.rubber.data.model.local.Song;

import java.util.List;
import java.util.Random;

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

    private PlayerContract.PlayType playType;

    private Random rand;

    private boolean haveLoaded;

    public PlayerPresenter(PlayerContract.View playerView, MusicDataSourceContract musicRepository) {
        view = playerView;
        mMusicRepository = musicRepository;
        mSubscriptions = new CompositeSubscription();
        view.setPresenter(this);
        playingSongIndex = 0;
        rand = new Random();
    }

    @Override
    public void subscribe() {
        if (!haveLoaded) {
            mMusicRepository.refreshRepository();
            haveLoaded = true;
        } else {
            refreshView();
        }
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    @Override
    public void loadFavPlaylist(final int position) {
        mSubscriptions.clear();
        mMusicRepository.refreshRepository();
        Subscription subscription =
                mMusicRepository.getPlaylist(MusicDataSourceContract.PlaylistType.FAV)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Song>>() {
                    @Override
                    public void call(List<Song> songs) {
                        updatePlaylist(songs, position);
                        view.changeSong();
                    }
                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void loadAllPlaylist(final int position) {
        mSubscriptions.clear();
        Subscription subscription =
                mMusicRepository.getPlaylist(MusicDataSourceContract.PlaylistType.ALL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Song>>() {
                    @Override
                    public void call(List<Song> songs) {
                        updatePlaylist(songs, position);
                        view.changeSong();
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
        switch (playType) {
            case SHUFFLE:
                playingSongIndex = rand.nextInt(currentSongList.size());
                break;
            case REPEAT_SINGLE:
                break;
            case REPEAT_ALL:
                playingSongIndex--;
                if (playingSongIndex < 0) {
                    playingSongIndex = currentSongList.size() - 1;
                }
                break;
        }
        return getCurrentPlayingSong();
    }

    @Override
    public Song getNextSong() {
        switch (playType) {
            case SHUFFLE:
                playingSongIndex = rand.nextInt(currentSongList.size());
                break;
            case REPEAT_SINGLE:
                break;
            case REPEAT_ALL:
                playingSongIndex++;
                if (playingSongIndex >= currentSongList.size()) {
                    playingSongIndex = 0;
                }
                break;
        }
        return getCurrentPlayingSong();
    }

    @Override
    public void setPlayType(PlayerContract.PlayType playType) {
        this.playType = playType;
    }

    @Override
    public PlayerContract.PlayType getPlayType() {
        return playType;
    }

    @Override
    public void saveFavSong(long songId) {
        mMusicRepository.saveFavSong(songId);
    }

    @Override
    public void deleteFavSong(long songId) {
        mMusicRepository.deleteFavSong(songId);
    }

    private void updatePlaylist(List<Song> songs, int position) {
        currentSongList = songs;
        playingSongIndex = position;
    }

}
