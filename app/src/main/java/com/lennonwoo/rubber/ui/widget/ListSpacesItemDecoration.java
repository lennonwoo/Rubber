package com.lennonwoo.rubber.ui.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ListSpacesItemDecoration extends RecyclerView.ItemDecoration {
    private Context context;
    private int space;

    public ListSpacesItemDecoration(Context context, int space) {
        this.context = context;
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        int pos = parent.getChildLayoutPosition(view);
        if (pos == 0)
            outRect.left = space;
        outRect.top = space;
        outRect.bottom = space;
        outRect.left = space * 2;
    }

}