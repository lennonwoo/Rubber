package com.lennonwoo.rubber.data.source.local;

import android.provider.BaseColumns;

public class MusicDbPersistenceContract {

    public static abstract class FavDb implements BaseColumns {
        public static final String TABLE_NAME = "fav";
        public static final String COLUMN_NAME_SONG_ID = "song_id";
    }

}
