package com.lennonwoo.rubber.contract;

import com.lennonwoo.rubber.contract.base.BasePresenter;
import com.lennonwoo.rubber.contract.base.BaseView;
import com.lennonwoo.rubber.data.model.local.Song;

import java.util.List;

public interface PlayerContract {

    enum PlayType {
        SHUFFLE, REPEAT_SINGLE, REPEAT_ALL
    }

    interface View extends BaseView<Presenter> {

        void setPlayingSongInfo(Song currentSong);

        void setRecyclerItems(List<Song> playlist);

        void changeSong();

    }

    interface Presenter extends BasePresenter {

        void loadFavPlaylist(int position);

        void loadAllPlaylist(int position);

        void refreshView();

        Song getCurrentPlayingSong();

        Song getPrevSong();

        Song getNextSong();

        void setPlayType(PlayType playType);

        PlayType getPlayType();

    }

}
