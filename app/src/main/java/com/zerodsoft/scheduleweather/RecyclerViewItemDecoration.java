package com.zerodsoft.scheduleweather;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration
{
    private final int spacing;

    public RecyclerViewItemDecoration(int spacing)
    {
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state)
    {
        super.getItemOffsets(outRect, view, parent, state);
        int orientation = ((LinearLayoutManager) parent.getLayoutManager()).getOrientation();

        switch (orientation)
        {
            case RecyclerView.HORIZONTAL:
            {
                if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1)
                {
                    outRect.right = spacing;
                }
            }
            break;
            case RecyclerView.VERTICAL:
            {
                if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1)
                {
                    outRect.bottom = spacing;
                }
            }
            break;
        }
    }
}
