package com.zerodsoft.scheduleweather.etc;

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
        ImprovedTouchListener listener = new ImprovedTouchListener();
        addOnScrollListener(listener);
        addOnItemTouchListener(listener);
    }

    class ImprovedTouchListener extends RecyclerView.OnScrollListener implements OnItemTouchListener
    {
        private int scrollState = RecyclerView.SCROLL_STATE_IDLE;
        private int scrollPointerId = -1;
        private int initialTouchX = 0;
        private int initialTouchY = 0;
        private float dx = 0;
        private float dy = 0;

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e)
        {
            switch (e.getActionMasked())
            {
                case MotionEvent.ACTION_DOWN:
                {
                    scrollPointerId = e.getPointerId(0);
                    initialTouchX = (int) (e.getX() + 0.5f);
                    initialTouchY = (int) (e.getY() + 0.5f);
                }
                break;
                case MotionEvent.ACTION_POINTER_DOWN:
                {
                    int actionIndex = e.getActionIndex();
                    scrollPointerId = e.getPointerId(actionIndex);
                    initialTouchX = (int) (e.getX(actionIndex) + 0.5f);
                    initialTouchY = (int) (e.getY(actionIndex) + 0.5f);
                }
                break;
                case MotionEvent.ACTION_MOVE:
                {
                    int index = e.findPointerIndex(scrollPointerId);
                    if (index >= 0 && scrollState != RecyclerView.SCROLL_STATE_DRAGGING)
                    {
                        int x = (int) (e.getX(index) + 0.5f);
                        int y = (int) (e.getY(index) + 0.5f);
                        dx = x - initialTouchX;
                        dy = y - initialTouchY;
                    }
                    break;
                }
            }
            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e)
        {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept)
        {

        }

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState)
        {
            int oldState = scrollState;
            scrollState = newState;

            if (oldState == RecyclerView.SCROLL_STATE_IDLE && newState == RecyclerView.SCROLL_STATE_DRAGGING)
            {
                LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager != null)
                {
                    boolean canScrollHorizontally = layoutManager.canScrollHorizontally();
                    boolean canScrollVertically = layoutManager.canScrollVertically();

                    if (canScrollHorizontally != canScrollVertically)
                    {
                        if ((canScrollHorizontally && Math.abs(dy) > Math.abs(dx))
                                || (canScrollVertically && Math.abs(dx) > Math.abs(dy)))
                        {
                            recyclerView.stopScroll();
                        }
                    }
                }
            }
        }
    }
}
