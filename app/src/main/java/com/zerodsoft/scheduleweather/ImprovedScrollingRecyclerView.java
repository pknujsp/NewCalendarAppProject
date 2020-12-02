package com.zerodsoft.scheduleweather;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class ImprovedScrollingRecyclerView extends RecyclerView
{
    private float lastX = 0f;
    private float lastY = 0f;
    private LayoutManager layoutManager;
    private boolean allowScroll;
    private boolean isScrolling;
    public static final String TAG = "TEST_RECYCLER_VIEW";

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
        addOnScrollListener(new OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
                isScrolling = newState != SCROLL_STATE_IDLE;
                Log.e(TAG, "onScrollStateChanged");
            }

        });
        setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e)
    {
        /*
        switch (e.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
            {
                lastX = e.getX();
                lastY = e.getY();
            }
            break;
            case MotionEvent.ACTION_UP:
            {
                float currentX = e.getX();
                float currentY = e.getY();
                allowScroll = (!(Math.abs(currentX - lastX) <= 30)) || (!(Math.abs(currentY - lastY) <= 30));
            }
            break;
            case MotionEvent.ACTION_MOVE:
            {

            }
            break;
        }
        return allowScroll;

         */

        return super.onInterceptTouchEvent(e);
    }

}
