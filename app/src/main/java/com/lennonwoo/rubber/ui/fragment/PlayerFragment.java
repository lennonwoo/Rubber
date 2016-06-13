package com.lennonwoo.rubber.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lennonwoo.rubber.R;
import com.lennonwoo.rubber.contract.PlayerContract;
import com.lennonwoo.rubber.data.model.local.Song;
import com.lennonwoo.rubber.service.PlayerService;
import com.lennonwoo.rubber.ui.widget.CircleProgressView;
import com.lennonwoo.rubber.ui.widget.slidinguppanel.SlidingUpPanelLayout;
import com.lennonwoo.rubber.utils.BlurTransformation;
import com.lennonwoo.rubber.utils.PaletteGeneratorTransformation;
import com.lennonwoo.rubber.utils.RoundedTransformation;
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

    @BindView(R.id.player_small)
    LinearLayout smallPanel;
    @BindView(R.id.song_art_small)
    ImageView songArtSmall;
    @BindView(R.id.song_info_small)
    TextView songInfoSmall;
    @BindView(R.id.play_stop)
    ImageView resumePause;
    @BindView(R.id.song_art_layout)
    RelativeLayout songArtLayout;
    @BindView(R.id.blur_img)
    ImageView blurImg;
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

    //width and height
    private int smallPanelHeight;
    private int artHeight;


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
                    presenter.refreshView();
                    if (slidingUpPanelLayout.isPanelHidden()) {
                        //TODO why can't collapsePanel???
//                        slidingUpPanelLayout.collapsePanel();
                        slidingUpPanelLayout.expandPanel();
                    }
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
                .resize(smallPanelHeight, smallPanelHeight)
                .centerCrop()
                .transform(new PaletteGeneratorTransformation(24))
                .into(songArtSmall, new PaletteGeneratorTransformation.Callback(songArtSmall) {
                    @Override
                    public void onPalette(Palette palette) {
                        circleProgress.setLoadedProgressColor(
                                    palette.getVibrantColor(context.getResources().getColor(R.color.colorAccent))
                            );
                        circleProgress.setEmptyProgressColor(
                                    palette.getLightMutedColor(context.getResources().getColor(R.color.white))
                            );
                        fabMore.setBackgroundTintList(
                                    ColorStateList.valueOf(palette.getVibrantColor(context.getResources().getColor(R.color.colorAccent)))
                            );
                        circleProgress.setTimeTextColor(
                                    palette.getLightVibrantColor(context.getResources().getColor(R.color.colorPrimary))
                            );
                        }
                    });
        Picasso.with(context)
                .load(new File(song.getArtPath()))
                .resize(250, 250)
                .centerCrop()
                .transform(new RoundedTransformation(125))
                .into(roundedImg);
        Picasso.with(context)
                .load(new File(song.getArtPath()))
                .resize(artHeight, artHeight)
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
        smallPanelHeight = getResources().getDimensionPixelSize(R.dimen.sliding_up_panel_bottom_height);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager()
                            .getDefaultDisplay()
                            .getMetrics(displayMetrics);
        artHeight = displayMetrics.widthPixels;
        songArtLayout.getLayoutParams().height = artHeight;
        blurImg.getLayoutParams().height = artHeight;
        //TODO view's elevation
        circleProgress.setSongOperation(this);
        slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                smallPanel.setAlpha(1 - slideOffset);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, smallPanelHeight);
                params.topMargin = -(int) (smallPanelHeight * slideOffset);
                smallPanel.setLayoutParams(params);
            }

            @Override
            public void onPanelCollapsed(View panel) {
            }

            @Override
            public void onPanelExpanded(View panel) {
            }

            @Override
            public void onPanelAnchored(View panel) {
            }

            @Override
            public void onPanelHidden(View panel) {
            }
        });
    }
}
