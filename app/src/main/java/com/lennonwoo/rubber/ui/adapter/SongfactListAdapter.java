package com.lennonwoo.rubber.ui.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lennonwoo.rubber.R;
import com.lennonwoo.rubber.contract.SongContract;
import com.lennonwoo.rubber.data.model.remote.SongFact;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

// TODO refactor this Adapter to Songfacts
public class SongfactListAdapter extends RecyclerView.Adapter<SongfactListAdapter.SongViewHolder>{

    private Context context;

    private List<SongFact> factList;

    private SongContract.Presenter presenter;

    public SongfactListAdapter(Context context, SongContract.Presenter presenter) {
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
        holder.songFactTv.setText(factList.get(position).getSongFact());
    }

    @Override
    public int getItemCount() {
        return factList.size();
    }

    public void setFactList(List<SongFact> factList) {
        this.factList = factList;
    }


    public class SongViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.fact_card)
        CardView songCard;
        @BindView(R.id.song_fact_tv)
        TextView songFactTv;

        public SongViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            songFactTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        }
    }

}
