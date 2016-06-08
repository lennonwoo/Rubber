package com.lennonwoo.rubber.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class MusicPlayService extends Service {

    public static final String ACTION_PLAY_SHUFFLE = "play shuffle";
    public static final String ACTION_PLAY_ALL = "play all";
    public static final String ACIION_PLAY_SINGLE = "Play singe";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
