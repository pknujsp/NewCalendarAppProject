package com.zerodsoft.scheduleweather.activity.main.sidenav;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class NotScrollExpandableListView extends ExpandableListView
{
    public NotScrollExpandableListView(Context context)
    {
        super(context);
    }

    public NotScrollExpandableListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public NotScrollExpandableListView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }
}
