package com.lennonwoo.rubber.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lennonwoo.rubber.R;
import com.lennonwoo.rubber.data.model.local.Song;
import com.lennonwoo.rubber.service.PlayerService;
import com.lennonwoo.rubber.utils.BitmapHelper;
import com.lennonwoo.rubber.utils.RoundedTransformation;
import com.lennonwoo.rubber.utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;
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
    public void onBindViewHolder(final SongViewHolder holder, int position) {
        int duration = songList.get(position).getDuration();
        int sumSecond = duration / 1000;
        holder.songName.setText(songList.get(position).getName());
        holder.songArtist.setText(songList.get(position).getArtist());
        holder.songTime.setText(Utils.durationToString(sumSecond));
        String path;
        if ((path = songList.get(position).getArtPath()) != null) {
            Picasso.with(context)
                    .load(new File(path))
                    .resize(80, 80)
                    .centerCrop()
                    .transform(new RoundedTransformation(40))
                    .into(holder.songArt);
        }
        holder.songInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(PlayerService.ACTION_PLAY_ALL);
                //TODO click play seem have some trouble!!!
                int adapter = holder.getAdapterPosition();
                intent.putExtra(PlayerService.SONG_POSITION, holder.getAdapterPosition());
                context.sendBroadcast(intent);
            }
        });
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
        @BindView(R.id.song_time)
        TextView songTime;

        public SongViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            Bitmap bitmap = BitmapHelper.getBitmapOfVector(
                    context.getDrawable(R.drawable.default_art), 80, 80);
            Bitmap bitmapConverted = BitmapHelper.getRoundedCornerBitmap(bitmap, 40);
            songArt.setImageBitmap(bitmapConverted);
        }
    }

}
