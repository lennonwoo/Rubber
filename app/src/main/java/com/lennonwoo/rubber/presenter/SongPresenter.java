package com.lennonwoo.rubber.presenter;

import com.lennonwoo.rubber.contract.MusicDataSourceContract;
import com.lennonwoo.rubber.contract.SongContract;
import com.lennonwoo.rubber.data.model.local.Song;
import com.lennonwoo.rubber.data.model.remote.SongFact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SongPresenter implements SongContract.Presenter {

    public static final String TAG = SongPresenter.class.getSimpleName();

    private SongContract.PlayerView playerView;
    private SongContract.SongListView songListView;

    private MusicDataSourceContract mMusicRepository;

    private CompositeSubscription mSubscriptions;

    private List<Song> currentSongList;
    private Map<Song, List<SongFact>> songFactMap = new HashMap<Song, List<SongFact>>();

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
            refreshPlayerView();
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
        Subscription subscription = Observable.just("")
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<String, Observable<List<Song>>>() {
                    @Override
                    public Observable<List<Song>> call(String o) {
                        return mMusicRepository.getPlaylist(MusicDataSourceContract.PlaylistType.FAV);
                    }
                })
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
        mMusicRepository.refreshRepository();
        Subscription subscription = Observable.just("")
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<String, Observable<List<Song>>>() {
                    @Override
                    public Observable<List<Song>> call(String o) {
                        return mMusicRepository.getPlaylist(MusicDataSourceContract.PlaylistType.ALL);
                    }
                })
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
    public void refreshSongFact() {
        Song currentSong = getCurrentPlayingSong();
        if (songFactMap.get(currentSong) != null) {
            playerView.setRecyclerItems(songFactMap.get(currentSong));
        } else {
            Subscription subscription = Observable.just("")
                    .subscribeOn(Schedulers.io())
                    .flatMap(new Func1<String, Observable<List<SongFact>>>() {
                        @Override
                        public Observable<List<SongFact>> call(String o) {
                            return mMusicRepository.getSongFactList(getCurrentPlayingSong());
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<SongFact>>() {
                        @Override
                        public void call(List<SongFact> songFacts) {
                            if (songFacts != null && songFacts.size() != 0) {
                                playerView.setRecyclerItems(songFacts);
                                songFactMap.put(getCurrentPlayingSong(), songFacts);
                            } else {
                                List<SongFact> noFactFoundList = new ArrayList<SongFact>();
                                noFactFoundList.add(new SongFact(null, "No song's fact founded :("));
                                playerView.setRecyclerItems(noFactFoundList);
                            }
                        }
                    });
            mSubscriptions.add(subscription);
        }
    }

    @Override
    public void refreshPlayerView() {
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
