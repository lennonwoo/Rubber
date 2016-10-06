package com.lennonwoo.rubber.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.lennonwoo.rubber.R;
import com.lennonwoo.rubber.contract.SongContract;
import com.lennonwoo.rubber.data.model.local.Song;
import com.lennonwoo.rubber.service.PlayerService;
import com.lennonwoo.rubber.ui.adapter.SongListAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SongListFragment extends Fragment implements SongContract.SongListView {

    public static final String TAG = SongListFragment.class.getSimpleName();

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.empty_songs_list_layout)
    RelativeLayout emptyLayout;
    @BindView(R.id.songs_list)
    RecyclerView songsList;
    @BindView(R.id.fab_fav)
    FloatingActionButton fabFav;


    @OnClick(R.id.fab_fav)
    void fav() {
        Intent intent = new Intent();
        intent.setAction(PlayerService.ACTION_CHANGE_PLAYLIST_TAG);
        intent.putExtra(PlayerService.SONG_POSITION, 0);
        getActivity().sendBroadcast(intent);
    }

    private SongContract.Presenter presenter;
    private SongListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs_list, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onDestroyView();
        presenter.unsubscribe();
    }

    @Override
    public void setPresenter(SongContract.Presenter presenter) {
        this.presenter = presenter;
    }

    public void showEmptyLayout() {
        coordinatorLayout.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void setRecyclerItems(List<Song> songsList) {
        adapter.setSongList(songsList);
        coordinatorLayout.setVisibility(View.VISIBLE);
        emptyLayout.setVisibility(View.GONE);
    }

    private void init() {
        Context context = getActivity();
        adapter = new SongListAdapter(context);
        songsList.setAdapter(adapter);
        songsList.setLayoutManager(new LinearLayoutManager(context));
        showEmptyLayout();
    }
}
