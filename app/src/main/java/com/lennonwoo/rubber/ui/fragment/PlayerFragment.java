package com.lennonwoo.rubber.ui.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import com.lennonwoo.rubber.service.PlayerService;
import com.lennonwoo.rubber.ui.widget.CircleProgressView;
import com.lennonwoo.rubber.utils.BlurTransformation;
import com.lennonwoo.rubber.utils.RoundedTransformation;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlayerFragment extends Fragment implements PlayerContract.View, CircleProgressView.SongOperation {

    public static final String TAG = PlayerFragment.class.getSimpleName();

    public static final String ACTION_START = "com.lennonwoo.fragment.begin";
    public static final String ACTION_PAUSE = "com.lennonwoo.fragment.pause";
    public static final String ACTION_UPDATE_FRAGMENT = "com.lennonwoo.fragment.updateFragment";

    //preference static string
    private static final String MUSIC_PREFERENCE = "music_preference";
    private static final String PLAY_TYPE = "playType";
    private static final String SHUFFLE = "shuffle";
    private static final String REPEAT_SINGLE = "repeat single";
    private static final String REPEAT_ALL = "repeat all";

    private Context context;

    private PlayerContract.Presenter presenter;

    private SlidingUpPanelLayout slidingUpPanelLayout;

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

    @OnClick(R.id.fab_more)
    void next() {
        //TODO test next function
    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_START:
                    circleProgress.start();
                    break;
                case ACTION_PAUSE:
                    circleProgress.pause();
                    break;
                case ACTION_UPDATE_FRAGMENT:
                    if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN) {
                        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    }
                    presenter.refreshView();
                    break;
            }
        }
    };

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
    public void onStart() {
        super.onStart();
        SharedPreferences preferences = context.getSharedPreferences(MUSIC_PREFERENCE, Context.MODE_PRIVATE);
        String playType = preferences.getString(PLAY_TYPE, SHUFFLE);
        switch (playType) {
            case SHUFFLE:
                presenter.setPlayType(PlayerContract.PlayType.SHUFFLE);
                break;
            case REPEAT_SINGLE:
                presenter.setPlayType(PlayerContract.PlayType.REPEAT_SINGLE);
                break;
            case REPEAT_ALL:
                presenter.setPlayType(PlayerContract.PlayType.REPEAT_ALL);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_START);
        filter.addAction(ACTION_PAUSE);
        filter.addAction(ACTION_UPDATE_FRAGMENT);
        context.registerReceiver(br, filter);
        presenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        context.unregisterReceiver(br);
        presenter.unsubscribe();
    }

    @Override
    public void onStop() {
        super.onStop();
        PlayerContract.PlayType playType = presenter.getPlayType();
        SharedPreferences.Editor editor =
                context.getSharedPreferences(MUSIC_PREFERENCE, Context.MODE_PRIVATE).edit();
        switch (playType) {
            case SHUFFLE:
                editor.putString(PLAY_TYPE, SHUFFLE);
                break;
            case REPEAT_SINGLE:
                editor.putString(PLAY_TYPE, REPEAT_SINGLE);
                break;
            case REPEAT_ALL:
                editor.putString(PLAY_TYPE, REPEAT_ALL);
                break;
            default:
                editor.putString(PLAY_TYPE, SHUFFLE);
                break;
        }
        editor.apply();
    }

    @Override
    public void setPresenter(PlayerContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void changeSong() {
        Intent intent = new Intent();
        intent.setAction(PlayerService.ACTION_CHANGE_SONG);
        context.sendBroadcast(intent);
    }

    @Override
    public void seekSong(int progress) {
        Intent intent = new Intent();
        intent.setAction(PlayerService.ACTION_SEEK_SONG);
        intent.putExtra(PlayerService.SEEK_SONG_TO, progress);
        context.sendBroadcast(intent);
    }

    @Override
    public void setRecyclerItems(List<Song> playlist) {
        //TODO
    }

    @Override
    public void setPlayingSongInfo(Song song) {
        //TODO change image more gently -- Picasso~~!!
        Picasso.with(context)
                .load(new File(song.getArtPath()))
                .resize(250, 250)
                .centerCrop()
                .transform(new RoundedTransformation(125))
                .into(roundedImg);
        Picasso.with(context)
                .load(new File(song.getArtPath()))
                .transform(new BlurTransformation(context))
                .into(blurImg);
        circleProgress
                .setSongDuration(song.getDuration() / 1000)
                .begin();
    }

    public void setSlidingUpPanelLayout(SlidingUpPanelLayout slidingUpPanelLayout) {
        this.slidingUpPanelLayout = slidingUpPanelLayout;
    }

    private void init() {
        circleProgress.setSongOperation(this);
    }
}
