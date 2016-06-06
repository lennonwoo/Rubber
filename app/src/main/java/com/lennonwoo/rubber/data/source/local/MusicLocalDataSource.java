package com.lennonwoo.rubber.data.source.local;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.lennonwoo.rubber.contract.MusicDataSourceContract;
import com.lennonwoo.rubber.data.model.local.Album;
import com.lennonwoo.rubber.data.model.local.Song;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class MusicLocalDataSource implements MusicDataSourceContract.LocalDataSource{

    private static MusicLocalDataSource INSTANCE;

    Context context;

    private MusicLocalDataSource(Context context) {
        this.context = context;
        MusicDbHelper dbHelper = new MusicDbHelper(context);
    }

    public static MusicLocalDataSource getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new MusicLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public Observable<List<Song>> getSongList() {
        String where = MediaStore.Audio.Media.IS_MUSIC + "=1";
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, where, null, null);
        List<Song> songs = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    songs.add(getSongFromCursor(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return Observable.from(songs).toList();
    }

    @Override
    public Observable<List<Album>> getAlbumList() {
        Cursor cursor = context.getContentResolver()
                .query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, null, null, null, null);
        List<Album> albums = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    albums.add(getAlbumFromCursor(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return Observable.from(albums).toList();
    }

    private Song getSongFromCursor(Cursor cursor) {
        long _id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
        long artist_id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
        long album_id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
        String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
        String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
        String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
        long _size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
        long data_added = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED));
        String _data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
        return new Song(_id, artist_id, album_id,
                        title, artist, album,
                        _size, duration, data_added,
                        _data);
    }

    private Album getAlbumFromCursor(Cursor cursor) {
        long _id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Albums._ID));
        long numSongs = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS));
        String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
        String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST));
        String album_art = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
        return new Album(_id, numSongs,
                album, artist, album_art);
    }

}