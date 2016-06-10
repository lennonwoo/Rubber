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
import com.lennonwoo.rubber.data.model.local.Song;
import com.lennonwoo.rubber.ui.widget.CircleProgressView;
import com.lennonwoo.rubber.utils.BlurTransformation;
import com.lennonwoo.rubber.utils.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

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
    ImageView resumePause;
    @BindView(R.id.blur_img)
    ImageView blurImg;
    @BindView(R.id.song_info_large)
    TextView songInfoLarge;
    @BindView(R.id.rounded_img)
    ImageView roundedImg;
    @BindView(R.id.circle_progress)
    CircleProgressView circleProgress;
    @BindView(R.id.playlist)
    RecyclerView playlist;
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
    public void onResume() {
        super.onResume();
        presenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.unsubscribe();
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

    @Override
    public void setRecyclerItems(List<Song> playlist) {
        //TODO
    }

    @Override
    public void setPlayingSongInfo(Song currentSong) {
        Picasso.with(context)
                .load(new File(currentSong.getArtPath()))
                .resize(250, 250)
                .centerCrop()
                .transform(new RoundedTransformation(125))
                .into(roundedImg);
        Picasso.with(context)
                .load(new File(currentSong.getArtPath()))
                .transform(new BlurTransformation(context))
                .into(blurImg);
    }

    private void init() {
        circleProgress
                .setSongDuration(219)
                .setSongOperation(this);
    }
}
