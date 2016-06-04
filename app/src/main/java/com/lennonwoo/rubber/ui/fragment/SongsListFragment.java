package com.lennonwoo.rubber.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.lennonwoo.rubber.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lennon on 6/1/16.
 */
public class SongsListFragment extends Fragment {

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.empty_songs_list_layout)
    RelativeLayout emptyLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs_list, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
    }

    private void showEmptyLayout() {
        coordinatorLayout.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.VISIBLE);
    }

}
