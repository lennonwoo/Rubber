package com.lennonwoo.rubber.ui.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lennonwoo.rubber.R;
import com.lennonwoo.rubber.contract.PlayerContract;
import com.lennonwoo.rubber.data.model.local.Song;
import com.lennonwoo.rubber.utils.Utils;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

// TODO refactor this Adapter to Songfacts
public class SongfactListAdapter extends RecyclerView.Adapter<SongfactListAdapter.SongViewHolder>{

    private Context context;

    private List<Song> factList;

    private PlayerContract.Presenter presenter;

    public SongfactListAdapter(Context context, PlayerContract.Presenter presenter) {
        super();
        this.context = context;
        this.presenter = presenter;
        factList = new ArrayList<>();
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_fact_list, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SongViewHolder holder, int position) {
        holder.songName.setText(factList.get(position).getName());
        holder.songArtist.setText(factList.get(position).getArtist());
        holder.songTime.setText(Utils.durationToString(
                factList.get(position).getDuration() / 1000));
        holder.favBtn.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                long songId = factList.get(holder.getAdapterPosition()).getSongId();
                presenter.saveFavSong(songId);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                long songId = factList.get(holder.getAdapterPosition()).getSongId();
                presenter.deleteFavSong(songId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return factList.size();
    }

    public void setFactList(List<Song> factList) {
        this.factList = factList;
    }


    public class SongViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.fact_card)
        CardView songCard;
        @BindView(R.id.song_name)
        TextView songName;
        @BindView(R.id.song_artist)
        TextView songArtist;
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
