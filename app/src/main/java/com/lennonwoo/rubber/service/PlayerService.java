package com.lennonwoo.rubber.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.lennonwoo.rubber.contract.PlayerContract;
import com.lennonwoo.rubber.data.model.local.Song;

import java.io.IOException;

public class PlayerService extends Service {

//    public static final String ACTION_PLAY_SHUFFLE = "Play shuffle";
//    public static final String ACTION_PLAY_ALL = "play all";
//    public static final String ACTION_PLAY_SINGLE = "Play singe";
    public static final String ACTION_NEXT_SONG = "Next_song";
    public static final String ACTION_PREV_SONG = "Previous song";
    public static final String ACTION_CHANGE_SONG = "Change song";
    public static final String ACTION_RESUME = "Resume";
    public static final String ACTION_PAUSE = "Pause";

    private MyBinder mBinder = new MyBinder();

    private PlayerContract.Presenter presenter;

    private MediaPlayer mediaPlayer;

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                try {
                    switch (intent.getAction()) {
                        case ACTION_CHANGE_SONG:
                            Song song = presenter.getCurrentPlayingSong();
                            changeSong(song);
                            break;
                    }
                } catch (IOException e){
                    Toast.makeText(PlayerService.this, "This song can't be played", Toast.LENGTH_SHORT).show();
                }
            }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        IntentFilter filter = new IntentFilter();
//        filter.addAction(ACTION_PLAY_SHUFFLE);
//        filter.addAction(ACTION_PLAY_ALL);
//        filter.addAction(ACTION_PLAY_SINGLE);
        filter.addAction(ACTION_NEXT_SONG);
        filter.addAction(ACTION_PREV_SONG);
        filter.addAction(ACTION_CHANGE_SONG);
        filter.addAction(ACTION_RESUME);
        filter.addAction(ACTION_PAUSE);
        registerReceiver(br, filter);
        mediaPlayer = new MediaPlayer();
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        unregisterReceiver(br);
        return super.onUnbind(intent);
    }


    private void changeSong(Song song) throws IOException{
        mediaPlayer.reset();
        mediaPlayer.setDataSource(song.getPath());
        mediaPlayer.prepare();
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Song song = presenter.getNextSong();
                try {
                    changeSong(song);
                } catch (IOException e) {
                    Toast.makeText(PlayerService.this, "This song can't be played", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class MyBinder extends Binder {
        public void setPresenter(PlayerContract.Presenter playerPresenter) {
            presenter = playerPresenter;
        }
    }

}
