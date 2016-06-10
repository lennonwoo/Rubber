package com.lennonwoo.rubber.contract;

import com.lennonwoo.rubber.contract.base.BasePresenter;
import com.lennonwoo.rubber.contract.base.BaseView;
import com.lennonwoo.rubber.data.model.local.Song;

import java.util.List;

public interface PlayerContract {

    interface View extends BaseView<Presenter> {

        void setPlayingSongInfo(Song currentSong);

        void setRecyclerItems(List<Song> playlist);

    }

    interface Presenter extends BasePresenter {

        void loadFavPlaylist();

        void loadAllPlaylist();

    }

}
