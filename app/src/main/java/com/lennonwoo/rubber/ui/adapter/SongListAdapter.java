package com.lennonwoo.rubber.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lennonwoo.rubber.R;
import com.lennonwoo.rubber.data.model.local.Song;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.SongViewHolder>{

    private List<Song> songList = new ArrayList<>();

    private Context context;

    public SongListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_song_list, parent, false);
        return new SongViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        holder.songName.setText(songList.get(position).getName());
        holder.songArtist.setText(songList.get(position).getArtist());
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public void setSongList(List<Song> songList) {
        this.songList = songList;
        notifyDataSetChanged();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.song_info_layout)
        RelativeLayout songInfoLayout;
        @BindView(R.id.song_art)
        ImageView songArt;
        @BindView(R.id.song_name)
        TextView songName;
        @BindView(R.id.song_artist)
        TextView songArtist;

        public SongViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
