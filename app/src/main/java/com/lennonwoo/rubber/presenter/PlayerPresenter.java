package com.lennonwoo.rubber.presenter;

import com.lennonwoo.rubber.contract.MusicDataSourceContract;
import com.lennonwoo.rubber.contract.PlayerContract;

import rx.subscriptions.CompositeSubscription;

public class PlayerPresenter implements PlayerContract.Presenter {

    private PlayerContract.View view;

    private MusicDataSourceContract mMusicRepository;

    private CompositeSubscription mSubscriptions;

    public PlayerPresenter(PlayerContract.View playerView, MusicDataSourceContract musicRepository) {
        view = playerView;
        mMusicRepository = musicRepository;
        mSubscriptions = new CompositeSubscription();
        view.setPresenter(this);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }
}
