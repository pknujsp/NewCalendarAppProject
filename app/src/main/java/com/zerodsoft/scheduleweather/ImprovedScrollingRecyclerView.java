package com.zerodsoft.scheduleweather;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class ImprovedScrollingRecyclerView extends RecyclerView
{
    private float lastX = 0f;
    private float lastY = 0f;
    private boolean scrolling = false;
    private LayoutManager layoutManager;
    private boolean allowScroll;

    public ImprovedScrollingRecyclerView(@NonNull Context context)
    {
        this(context, null);
    }

    public ImprovedScrollingRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public ImprovedScrollingRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs,
                                         int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e)
    {
        layoutManager = getLayoutManager();
        if (layoutManager == null)
        {
            return super.onInterceptTouchEvent(e);
        }

        switch (e.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
                lastX = e.getX();
                lastY = e.getY();
                Log.e("IMRPOVED_RECYCLER_VIEW", "ACTION DOWN");
                allowScroll = false;
                break;

            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(e.getX() - lastX);
                float dy = Math.abs(e.getY() - lastY);
                allowScroll = dy > dx ? layoutManager.canScrollVertically() : layoutManager.canScrollHorizontally();
                Log.e("IMRPOVED_RECYCLER_VIEW", "ACTION_MOVE");
                break;
        }

        return allowScroll;
    }

}
