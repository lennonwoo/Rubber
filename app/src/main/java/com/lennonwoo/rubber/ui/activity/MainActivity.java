package com.lennonwoo.rubber.ui.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
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

import com.lennonwoo.rubber.R;
import com.lennonwoo.rubber.data.source.MusicRepository;
import com.lennonwoo.rubber.data.source.local.MusicLocalDataSource;
import com.lennonwoo.rubber.data.source.remote.MusicRemoteDataSource;
import com.lennonwoo.rubber.presenter.PlayerPresenter;
import com.lennonwoo.rubber.presenter.SongListPresenter;
import com.lennonwoo.rubber.service.PlayerService;
import com.lennonwoo.rubber.ui.fragment.PlayerFragment;
import com.lennonwoo.rubber.ui.fragment.SongListFragment;
import com.lennonwoo.rubber.utils.PermissionChecker;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.sliding_up_pane_layout)
    SlidingUpPanelLayout slidingUpPanelLayout;

    PermissionChecker checker;

    SongListPresenter songListPresenter;
    PlayerPresenter playerPresenter;

    private ServiceConnection connection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checker = new PermissionChecker(this, drawerLayout);
            checker.check(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                new PermissionChecker.OnPermissionResponse() {
                    @Override
                    public void onAccepted() {
                        init();
                    }

                    @Override
                    public void onDecline() {
                        finish();
                    }
                });
        } else {
            init();
        }
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
        MusicRepository musicRepository = MusicRepository.getInstance(
                MusicLocalDataSource.getInstance(this), MusicRemoteDataSource.getInstace(this));
        songListPresenter = new SongListPresenter(songListFragment, musicRepository);
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

        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
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
