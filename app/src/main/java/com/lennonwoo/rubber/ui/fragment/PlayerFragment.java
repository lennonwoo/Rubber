package com.lennonwoo.rubber.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lennonwoo.rubber.R;
import com.lennonwoo.rubber.contract.PlayerContract;
import com.lennonwoo.rubber.ui.widget.CircleProgressView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerFragment extends Fragment implements PlayerContract.View, CircleProgressView.SongOperation {

    private Context context;

    private PlayerContract.Presenter presenter;

    @BindView(R.id.song_art_small)
    ImageView songArtSmall;
    @BindView(R.id.song_info_small)
    TextView songInfoSmall;
    @BindView(R.id.play_stop)
    ImageView playStop;
    @BindView(R.id.blur_img)
    ImageView blurImg;
    @BindView(R.id.song_info_large)
    TextView songInfoLarge;
    @BindView(R.id.rounded_img)
    ImageView roundedImg;
    @BindView(R.id.circle_progress)
    CircleProgressView circleProgress;
    @BindView(R.id.playing_queue)
    RecyclerView playingQueue;
    @BindView(R.id.fab_more)
    FloatingActionButton fabMore;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        context = view.getContext();
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    @Override
    public void setPresenter(PlayerContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void nextSong() {

    }

    @Override
    public void changeProgress(int progress) {

    }

    private void init() {
        circleProgress
                .setSongDuration(219)
                .setSongOperation(this);
    }
}
