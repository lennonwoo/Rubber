package com.lennonwoo.rubber.presenter;

import com.lennonwoo.rubber.contract.MusicDataSourceContract;
import com.lennonwoo.rubber.contract.SongContract;
import com.lennonwoo.rubber.data.model.local.Song;

import java.util.List;
import java.util.Random;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SongPresenter implements SongContract.Presenter {

    private SongContract.PlayerView playerView;
    private SongContract.SongListView songListView;

    private MusicDataSourceContract mMusicRepository;

    private CompositeSubscription mSubscriptions;

    private List<Song> currentSongList;

    private int playingSongIndex;

    private SongContract.PlayType playType;

    private Random rand;

    private boolean haveLoaded;

    public SongPresenter(SongContract.SongListView songListView, SongContract.PlayerView playerView, MusicDataSourceContract musicRepository) {
        this.playerView = playerView;
        this.songListView = songListView;
        mMusicRepository = musicRepository;
        mSubscriptions = new CompositeSubscription();
        this.playerView.setPresenter(this);
        this.songListView.setPresenter(this);
        playingSongIndex = 0;
        rand = new Random();
    }

    @Override
    public void subscribe() {
        if (!haveLoaded) {
            mMusicRepository.refreshRepository();
            loadPlaylist();
        } else {
            refreshView();
        }
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    @Override
    public void loadTagedPlaylist() {
        mSubscriptions.clear();
        mMusicRepository.refreshRepository();
        Subscription subscription =
                mMusicRepository.getPlaylist(MusicDataSourceContract.PlaylistType.FAV)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Song>>() {
                    @Override
                    public void call(List<Song> songs) {
                        updatePlaylist(songs);
                        playingSongIndex = 1;
                    }
                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void loadPlaylist() {
        mSubscriptions.clear();
        Subscription subscription =
                mMusicRepository.getPlaylist(MusicDataSourceContract.PlaylistType.ALL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Song>>() {
                    @Override
                    public void call(List<Song> songs) {
                        updatePlaylist(songs);
                        playingSongIndex = 1;
                    }
                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void refreshView() {
        playerView.setRecyclerItems(currentSongList);
        playerView.setPlayingSongInfo(currentSongList.get(playingSongIndex));
    }

    @Override
    public Song getChangedSong(int position) {
        playingSongIndex = position;
        return getCurrentPlayingSong();
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
    public void setPlayType(SongContract.PlayType playType) {
        this.playType = playType;
    }

    @Override
    public SongContract.PlayType getPlayType() {
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

    private void updatePlaylist(List<Song> songs) {
        currentSongList = songs;
        haveLoaded = true;
       if (songs.size() == 0) {
            songListView.showEmptyLayout();
        } else {
            songListView.setRecyclerItems(songs);
        }
    }

    private Song getCurrentPlayingSong() {
        return currentSongList.get(playingSongIndex);
    }

}
