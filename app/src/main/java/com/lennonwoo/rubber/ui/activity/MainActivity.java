package com.lennonwoo.rubber.ui.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.lennonwoo.rubber.R;
import com.lennonwoo.rubber.data.source.MusicRepository;
import com.lennonwoo.rubber.data.source.local.MusicLocalDataSource;
import com.lennonwoo.rubber.data.source.remote.MusicRemoteDataSource;
import com.lennonwoo.rubber.presenter.PlayerPresenter;
import com.lennonwoo.rubber.presenter.SongListPresenter;
import com.lennonwoo.rubber.service.PlayerService;
import com.lennonwoo.rubber.ui.fragment.PlayerFragment;
import com.lennonwoo.rubber.ui.fragment.SongListFragment;
import com.lennonwoo.rubber.ui.widget.slidinguppanel.SlidingUpPanelLayout;
import com.lennonwoo.rubber.utils.PermissionChecker;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static final String ACTION_SHOW_PANEL = "com.lennonwoo.showPanel";

    public static boolean active;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.sliding_up_pane_layout)
    SlidingUpPanelLayout slidingUpPanelLayout;

    private PermissionChecker checker;

    private  PlayerPresenter playerPresenter;

    private ServiceConnection connection;

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_SHOW_PANEL:
                    if (slidingUpPanelLayout.isPanelHidden()) {
                        slidingUpPanelLayout.collapsePanel();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checker = new PermissionChecker(this);
            checker.check(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                new PermissionChecker.OnPermissionResponse() {
                    @Override
                    public void onAccepted() {
                        init();
                    }

                    @Override
                    public void onDecline() {
                        Toast.makeText(MainActivity.this, "Sorry, we need this permission or the app won's word", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
        } else {
            init();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SHOW_PANEL);
        registerReceiver(br, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        active = false;
        unregisterReceiver(br);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    private void init() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
                syncState();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                syncState();
            }

        };
        if (drawerLayout != null) {
            drawerLayout.addDrawerListener(drawerToggle);
        }


        SongListFragment songListFragment = new SongListFragment();
        PlayerFragment playerFragment = new PlayerFragment();
        playerFragment.setSlidingUpPanelLayout(slidingUpPanelLayout);
        MusicRepository musicRepository = MusicRepository.getInstance(
                MusicLocalDataSource.getInstance(this), MusicRemoteDataSource.getInstace(this));
        // mvp's bind
        new SongListPresenter(songListFragment, musicRepository);
        playerPresenter = new PlayerPresenter(playerFragment, musicRepository);

        bindService(new Intent(this, PlayerService.class), connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                PlayerService.MyBinder binder = (PlayerService.MyBinder) service;
                binder.setPresenter(playerPresenter);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        }, BIND_AUTO_CREATE);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.main_content_fragment, songListFragment)
                .commit();

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.main_content_panel, playerFragment)
                .commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tasks_fragment_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        checker.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
