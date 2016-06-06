package com.lennonwoo.rubber.data.source.remote;

import android.content.Context;

import com.lennonwoo.rubber.contract.MusicDataSourceContract;

public class MusicRemoteDataSource implements MusicDataSourceContract.RemoteDataSource {

    private static MusicRemoteDataSource INSTANCE;

    private MusicRemoteDataSource(Context context) {
        //TODO
    }

    public static MusicRemoteDataSource getInstace(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new MusicRemoteDataSource(context);
        }
        return INSTANCE;
    }

}
