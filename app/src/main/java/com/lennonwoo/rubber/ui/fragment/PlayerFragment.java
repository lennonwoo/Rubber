package com.lennonwoo.rubber.ui.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lennonwoo.rubber.R;
import com.lennonwoo.rubber.contract.PlayerContract;
import com.lennonwoo.rubber.data.model.local.Song;
import com.lennonwoo.rubber.service.PlayerService;
import com.lennonwoo.rubber.ui.adapter.PlayListAdapter;
import com.lennonwoo.rubber.ui.widget.CircularProgressView;
import com.lennonwoo.rubber.ui.widget.slidinguppanel.SlidingUpPanelLayout;
import com.lennonwoo.rubber.utils.BlurTransformation;
import com.lennonwoo.rubber.utils.PaletteGeneratorTransformation;
import com.lennonwoo.rubber.utils.StepResponseInterpolator;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerFragment extends Fragment implements PlayerContract.View, CircularProgressView.SongOperation {

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
    @BindView(R.id.circular_img)
    CircularImageView circularImg;
    @BindView(R.id.circle_progress)
    CircularProgressView circularProgress;
    @BindView(R.id.playlist)
    RecyclerView playlist;

    //width and height
    private int smallPanelArtLength;
    private int bigPanelArtLength;
    private int circularImgDiam;
    private int circularProgressDiam;

    private PlayListAdapter adapter;

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_START:
                    circularProgress.start();
                    rotateAnim = ObjectAnimator.ofFloat(circularImg, View.ROTATION, 0, 360f);
                    rotateAnim.setDuration(10000);
                    rotateAnim.setRepeatCount(ValueAnimator.INFINITE);
                    rotateAnim.setInterpolator(new LinearInterpolator());
                    rotateAnim.start();
                    break;
                case ACTION_PAUSE:
                    circularProgress.pause();
                    //TODO look xiaojian's blog then make my Interpolator
                    rotateAnim.cancel();
                    if (circularImg.getRotation() > 180f) {
                        rotateAnim.setFloatValues(circularImg.getRotation(), 360f);
                    } else {
                        rotateAnim.setFloatValues(circularImg.getRotation(), 0f);
                    }
                    rotateAnim.setRepeatCount(0);
                    rotateAnim.setDuration(3000);
                    rotateAnim.setInterpolator(new StepResponseInterpolator());
                    rotateAnim.start();
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

    ObjectAnimator rotateAnim;

    final Target blurImgTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            blurImg.setImageBitmap(bitmap);
            ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(blurImg, View.ALPHA, 0.2f, 1f);
            alphaAnim.setDuration(3000);
            alphaAnim.start();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            blurImg.setImageResource(R.drawable.default_art);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    final Target circularImgTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            if (rotateAnim != null) {
                rotateAnim.cancel();
            }
            circularImg.setImageBitmap(bitmap);
            ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(circularImg, View.ALPHA, 0f, 1f);
            alphaAnim.setDuration(4000);
            alphaAnim.start();
            rotateAnim = ObjectAnimator.ofFloat(circularImg, View.ROTATION, 0, 360f);
            rotateAnim.setDuration(10000);
            rotateAnim.setRepeatCount(ValueAnimator.INFINITE);
            rotateAnim.setInterpolator(new LinearInterpolator());
            rotateAnim.start();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            circularImg.setImageResource(R.drawable.default_art);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
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
        intent.putExtra(PlayerService.SEEK_SONG_TO, progress * 1000);
        context.sendBroadcast(intent);
    }

    @Override
    public void setRecyclerItems(List<Song> playlist) {
        adapter.setPlayList(playlist);
    }

    @Override
    public void setPlayingSongInfo(Song song) {
        //TODO change image more gently -- Picasso~~!!
        Picasso.with(context)
                .load(new File(song.getArtPath()))
                .resize(smallPanelArtLength, smallPanelArtLength)
                .centerCrop()
                .transform(new PaletteGeneratorTransformation(24))
                .into(songArtSmall, new PaletteGeneratorTransformation.Callback(songArtSmall) {
                    @Override
                    public void onPalette(Palette palette) {
                        //TODO optimize color arrangement
                            circularProgress.setLoadedProgressColor(
                                        palette.getVibrantColor(context.getResources().getColor(R.color.colorAccent))
                                );
                            circularProgress.setEmptyProgressColor(
                                        palette.getLightMutedColor(context.getResources().getColor(R.color.white))
                                );
                            circularProgress.setTimeTextColor(
                                        palette.getLightVibrantColor(context.getResources().getColor(R.color.colorPrimary))
                                );
                            circularImg.setShadowColor(
                                    palette.getVibrantColor(context.getResources().getColor(R.color.colorAccent))
                                );
                        }
                    });
        Picasso.with(context)
                .load(new File(song.getArtPath()))
                .resize(circularImgDiam, circularImgDiam)
                .centerCrop()
                .into(circularImgTarget);
        Picasso.with(context)
                .load(new File(song.getArtPath()))
                .resize(bigPanelArtLength, bigPanelArtLength)
                .transform(new BlurTransformation(context))
                .into(blurImgTarget);
//        Glide.with(context)
//                .load(new File(song.getArtPath()))
//                .override(bigPanelArtLength, bigPanelArtLength)
//                .into(blurImg);
        circularProgress
                .setSongDuration(song.getDuration() / 1000)
                .begin();
    }


    public void setSlidingUpPanelLayout(SlidingUpPanelLayout slidingUpPanelLayout) {
        this.slidingUpPanelLayout = slidingUpPanelLayout;
    }

    private void init() {
        smallPanelArtLength = getResources().getDimensionPixelSize(R.dimen.sliding_up_panel_bottom_height);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager()
                            .getDefaultDisplay()
                            .getMetrics(displayMetrics);
        bigPanelArtLength = displayMetrics.widthPixels;
        circularImgDiam = (int) (bigPanelArtLength * 0.7);
        circularProgressDiam = (int) (bigPanelArtLength * 0.75);
        songArtLayout.getLayoutParams().height = bigPanelArtLength;
        blurImg.getLayoutParams().height = bigPanelArtLength;
        circularImg.getLayoutParams().height = circularImgDiam;
        circularImg.getLayoutParams().width = circularImgDiam;
        circularProgress.setSongOperation(this);
        circularProgress.getLayoutParams().height = circularProgressDiam;
        circularProgress.getLayoutParams().width = circularProgressDiam;
        slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                smallPanel.setAlpha(1 - slideOffset);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, smallPanelArtLength);
                params.topMargin = -(int) (smallPanelArtLength * slideOffset);
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
        adapter = new PlayListAdapter(context);
        playlist.setLayoutManager(new LinearLayoutManager(context));
        playlist.setAdapter(adapter);

    }
}
