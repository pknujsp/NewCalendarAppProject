package com.zerodsoft.scheduleweather.ScheduleList;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewItemDecoration extends RecyclerView.ItemDecoration {
    private final int itemSpacing;

    public ViewItemDecoration(int itemSpacing) {
        this.itemSpacing = itemSpacing;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = itemSpacing;
        outRect.top = itemSpacing;
    }
}
