package com.lennonwoo.rubber.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MusicDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "Music.db";

    private static final String CREATE_TABLE = "CREATE TABLE ";

    private static final String LEFT_BRACKET = " (";
    private static final String RIGHT_BRACKET = ")";
    private static final String COMMA_SEP = ",";

    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";


    private static final String CREATE_TABLE_FAV =
            CREATE_TABLE + MusicDbPersistenceContract.FavDb.TABLE_NAME + LEFT_BRACKET +
                    MusicDbPersistenceContract.FavDb._ID + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP +
                    MusicDbPersistenceContract.FavDb.COLUMN_NAME_SONG_ID + INTEGER_TYPE +
            RIGHT_BRACKET;


    public MusicDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FAV);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
