package com.lennonwoo.rubber.ui.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lennonwoo.rubber.R;
import com.lennonwoo.rubber.data.model.local.Song;
import com.lennonwoo.rubber.utils.Utils;
import com.like.LikeButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.SongViewHolder>{

    private Context context;

    private List<Song> playList;

    public PlayListAdapter(Context context) {
        super();
        this.context = context;
        playList = new ArrayList<>();
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_play_list, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        holder.songName.setText(playList.get(position).getName());
        holder.songArtist.setText(playList.get(position).getArtist());
        holder.songTime.setText(Utils.durationToString(
                playList.get(position).getDuration() / 1000));
    }

    @Override
    public int getItemCount() {
        return playList.size();
    }

    public void setPlayList(List<Song> playList) {
        this.playList = playList;
    }


    public class SongViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.song_card)
        CardView songCard;
        @BindView(R.id.song_name)
        TextView songName;
        @BindView(R.id.song_artist)
        TextView songArtist;
        //TODO listen music for some time to show this btn so that you are really like this music
        @BindView(R.id.fav_btn)
        LikeButton favBtn;
        @BindView(R.id.song_time)
        TextView songTime;

        public SongViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
