package com.lennonwoo.rubber.ui.activity;

import android.os.Bundle;
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
import com.lennonwoo.rubber.presenter.SongListPresenter;
import com.lennonwoo.rubber.ui.fragment.SongListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    SongListPresenter songListPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
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
        MusicRepository musicRepository = MusicRepository.getInstance(
                MusicLocalDataSource.getInstance(this), MusicRemoteDataSource.getInstace(this));
        songListPresenter = new SongListPresenter(songListFragment, musicRepository);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.main_content_fragment, songListFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tasks_fragment_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
