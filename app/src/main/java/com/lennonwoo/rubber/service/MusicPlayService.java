package com.lennonwoo.rubber.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class MusicPlayService extends Service {

    public static final String ACTION_PLAY_SHUFFLE = "Play shuffle";
    public static final String ACTION_PLAY_ALL = "play all";
    public static final String ACIION_PLAY_SINGLE = "Play singe";
    public static final String ACTION_NEXT_SONG = "Next_song";
    public static final String ACTION_PREV_SONG = "Previous song";
    public static final String ACTION_CHANGE_SONG = "Change song";
    public static final String ACTION_RESUME = "Resume";
    public static final String ACTION_PAUSE = "Pause";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
