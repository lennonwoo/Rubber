package com.lennonwoo.rubber.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.lennonwoo.rubber.R;
import com.lennonwoo.rubber.contract.PlayerContract;
import com.lennonwoo.rubber.data.model.local.Song;
import com.lennonwoo.rubber.ui.activity.MainActivity;
import com.lennonwoo.rubber.ui.fragment.PlayerFragment;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

public class PlayerService extends Service {

    public static final String ACTION_PLAY_ALL = "com.lennonwoo.playAll";
    public static final String ACTION_PLAY_FAV = "com.lennonwoo.playFav";
    public static final String ACTION_NEXT_SONG = "com.lennonwoo.nextSong";
    public static final String ACTION_PREV_SONG = "com.lennonwoo.previousSong";
    public static final String ACTION_START = "com.lennonwoo.start";
    public static final String ACTION_PAUSE = "com.lennonwoo.pause";
    public static final String ACTION_NOTI_CONTENT = "com.lennon.notificationClick";
    public static final String ACTION_NOTI_DELETE = "com.lennon.notificationDelete";

    public static final String SONG_POSITION = "songPosition";

    private static final int NOTIFICATION_ID = 325018;

    //This is used to bind presenter
    private MyBinder mBinder = new MyBinder();

    private PlayerContract.Presenter presenter;

    private MediaPlayer mediaPlayer;

    private Notification notification;
    private NotificationManager notificationManager;

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleBroadcast(intent);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PLAY_ALL);
        filter.addAction(ACTION_PLAY_FAV);
        filter.addAction(ACTION_NEXT_SONG);
        filter.addAction(ACTION_PREV_SONG);
        filter.addAction(ACTION_START);
        filter.addAction(ACTION_PAUSE);
        filter.addAction(ACTION_NOTI_CONTENT);
        filter.addAction(ACTION_NOTI_DELETE);
        registerReceiver(br, filter);
        mediaPlayer = new MediaPlayer();
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaPlayer.stop();
        unregisterReceiver(br);
        notificationManager.cancel(NOTIFICATION_ID);
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void handleBroadcast(Intent intent) {
        try {
            Song anotherSong = null;
            switch (intent.getAction()) {
                case ACTION_PLAY_ALL:
                    int positionAll = intent.getIntExtra(SONG_POSITION, 0);
                    presenter.loadAllPlaylist(positionAll);
                    anotherSong = presenter.getCurrentPlayingSong();
                    break;
                case ACTION_PLAY_FAV:
                    int positionFav = intent.getIntExtra(SONG_POSITION, 0);
                    presenter.loadFavPlaylist(positionFav);
                    anotherSong = presenter.getCurrentPlayingSong();
                    break;
                case ACTION_PREV_SONG:
                    anotherSong = presenter.getPrevSong();
                    break;
                case ACTION_NEXT_SONG:
                    anotherSong = presenter.getNextSong();
                    break;
                case ACTION_START:
                    mediaPlayer.start();
                    changeNotificationStatus(true);
                    break;
                case ACTION_PAUSE:
                    mediaPlayer.pause();
                    changeNotificationStatus(false);
                    break;
                case ACTION_NOTI_CONTENT:
                    Intent showPanel;
                    if (MainActivity.active) {
                        showPanel = new Intent();
                        showPanel.setAction(MainActivity.ACTION_SHOW_PANEL);
                        sendBroadcast(showPanel);
                    } else {
                        showPanel = new Intent(this, MainActivity.class);
                        showPanel.putExtra(MainActivity.OPEN_PANEL, true);
                        showPanel.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(showPanel);
                    }
                    break;
                case ACTION_NOTI_DELETE:
                    mediaPlayer.stop();
                    notificationManager.cancel(NOTIFICATION_ID);
                    break;
            }
            if (anotherSong != null) {
                cutOffSong(anotherSong);
                updateNotification(anotherSong);
            }
        } catch (IOException e){
            Toast.makeText(PlayerService.this, "This song can't be played", Toast.LENGTH_SHORT).show();
        }
    }

    private void cutOffSong(Song song) throws IOException{
        /*
        You can also just call presenter.refreshView() in this service
        but　it's unnecessary to change view when PlayerFragment is invisible
        so I send a broadcast so that view won't change when fragment is pause
         */
        Intent intent = new Intent();
        intent.setAction(PlayerFragment.ACTION_UPDATE_FRAGMENT);
        sendBroadcast(intent);
        mediaPlayer.reset();
        mediaPlayer.setDataSource(song.getPath());
        mediaPlayer.prepare();
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Song song = presenter.getNextSong();
                try {
                    cutOffSong(song);
                } catch (IOException e) {
                    Toast.makeText(PlayerService.this, "This song can't be played", Toast.LENGTH_SHORT).show();
                }
                updateNotification(song);
            }
        });
    }

    private void changeNotificationStatus(boolean playing) {
        if (playing) {
            Picasso.with(this)
                    .load(R.drawable.ic_pause_white_36dp)
                    .into(notification.bigContentView, R.id.notification_play_pause_song, NOTIFICATION_ID, notification);
            Picasso.with(this)
                    .load(R.drawable.ic_pause_white_36dp)
                    .into(notification.contentView, R.id.notification_play_pause_song, NOTIFICATION_ID, notification);
            Intent pauseIntent = new Intent();
            pauseIntent.setAction(ACTION_PAUSE);
            PendingIntent pausePendingIntent = PendingIntent.getBroadcast(this, 0, pauseIntent, 0);
            notification.bigContentView.setOnClickPendingIntent(R.id.notification_play_pause_song, pausePendingIntent);
            notification.contentView.setOnClickPendingIntent(R.id.notification_play_pause_song, pausePendingIntent);
        } else {
            Picasso.with(this)
                    .load(R.drawable.ic_play_arrow_white_36dp)
                    .into(notification.bigContentView, R.id.notification_play_pause_song, NOTIFICATION_ID, notification);
            Picasso.with(this)
                    .load(R.drawable.ic_play_arrow_white_36dp)
                    .into(notification.contentView, R.id.notification_play_pause_song, NOTIFICATION_ID, notification);
            Intent startIntent = new Intent();
            startIntent.setAction(ACTION_START);
            PendingIntent startPendingIntent = PendingIntent.getBroadcast(this, 0, startIntent, 0);
            notification.bigContentView.setOnClickPendingIntent(R.id.notification_play_pause_song, startPendingIntent);
            notification.contentView.setOnClickPendingIntent(R.id.notification_play_pause_song, startPendingIntent);
        }
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void updateNotification(Song song) {
        if (notification == null) {
            notificationBuild();
        }
        notification.bigContentView.setTextViewText(R.id.notification_song_name, song.getName());
        notification.bigContentView.setTextViewText(R.id.notification_song_artist, song.getArtist());
        notification.contentView.setTextViewText(R.id.notification_song_name, song.getName());
        notification.contentView.setTextViewText(R.id.notification_song_artist, song.getArtist());
        Picasso.with(this)
                .load(new File(song.getArtPath()))
                .resize(100, 100)
                .centerCrop()
                .error(R.drawable.default_art)
                .into(notification.bigContentView, R.id.notification_album_art, NOTIFICATION_ID, notification);
        Picasso.with(this)
                .load(new File(song.getArtPath()))
                .resize(50, 50)
                .centerCrop()
                .error(R.drawable.default_art)
                .into(notification.contentView, R.id.notification_album_art, NOTIFICATION_ID, notification);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void notificationBuild() {
        Intent contentIntent = new Intent();
        contentIntent.setAction(ACTION_NOTI_CONTENT);
        PendingIntent contentPendingIntent = PendingIntent.getBroadcast(this, 0, contentIntent, 0);
        Intent deleteIntent = new Intent();
        deleteIntent.setAction(ACTION_NOTI_DELETE);
        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(this, 0, deleteIntent, 0);
        notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_audiotrack_white_24dp)
                .setOngoing(false)
                .setContentIntent(contentPendingIntent)
                .setDeleteIntent(deletePendingIntent)
                .build();

        RemoteViews bigView = new RemoteViews(getPackageName(), R.layout.notification_content_big);
        RemoteViews normalView = new RemoteViews(getPackageName(), R.layout.notification_content_normal);
        notification.bigContentView = bigView;
        notification.contentView = normalView;
        Intent nextIntent = new Intent();
        nextIntent.setAction(ACTION_NEXT_SONG);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, 0, nextIntent, 0);
        notification.bigContentView.setOnClickPendingIntent(R.id.notification_next_song, nextPendingIntent);
        notification.contentView.setOnClickPendingIntent(R.id.notification_next_song, nextPendingIntent);
        Intent prevIntent = new Intent();
        prevIntent.setAction(ACTION_PREV_SONG);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(this, 0, prevIntent, 0);
        notification.bigContentView.setOnClickPendingIntent(R.id.notification_prev_song, prevPendingIntent);
        notification.contentView.setOnClickPendingIntent(R.id.notification_prev_song, prevPendingIntent);

        Intent pauseIntent = new Intent();
        pauseIntent.setAction(ACTION_PAUSE);
        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(this, 0, pauseIntent, 0);
        notification.bigContentView.setOnClickPendingIntent(R.id.notification_play_pause_song, pausePendingIntent);
        notification.contentView.setOnClickPendingIntent(R.id.notification_play_pause_song, pausePendingIntent);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public class MyBinder extends Binder {
        public void setPresenter(PlayerContract.Presenter playerPresenter) {
            presenter = playerPresenter;
        }
        public void setActivityActive(boolean active) {

        }
    }

}
