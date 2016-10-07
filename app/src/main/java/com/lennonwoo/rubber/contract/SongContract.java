package com.lennonwoo.rubber.contract;

import com.lennonwoo.rubber.contract.base.BasePresenter;
import com.lennonwoo.rubber.contract.base.BaseView;
import com.lennonwoo.rubber.data.model.local.Song;
import com.lennonwoo.rubber.data.model.remote.SongFact;

import java.util.List;

public interface SongContract {

    enum PlayType {
        SHUFFLE, REPEAT_SINGLE, REPEAT_ALL
    }

    interface PlayerView extends BaseView<Presenter> {

        void setPlayingSongInfo(Song currentSong);

        void setRecyclerItems(List<SongFact> factList);

    }

    interface SongListView extends BaseView<Presenter> {

        void showEmptyLayout();

        void setRecyclerItems(List<Song> songsList);

    }

    interface Presenter extends BasePresenter {

        void loadTagedPlaylist();

        void loadPlaylist();

        void refreshPlayerView();

        Song getChangedSong(int position);

        Song getPrevSong();

        Song getNextSong();

        void setPlayType(PlayType playType);

        PlayType getPlayType();

        void refreshSongFact();

        void saveFavSong(long songId);

        void deleteFavSong(long songId);

    }

}
