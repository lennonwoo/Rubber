package com.lennonwoo.rubber.contract;

import com.lennonwoo.rubber.contract.base.BasePresenter;
import com.lennonwoo.rubber.contract.base.BaseView;
import com.lennonwoo.rubber.data.model.local.Song;

import java.util.List;

public interface SongListContract {

    interface View extends BaseView<Presenter> {

        void showEmptyLayout();

        void setRecyclerItems(List<Song> songsList);

    }

    interface Presenter extends BasePresenter {

        void loadSongList();

        void loadAlbumList();
    }

}
