package com.zerodsoft.scheduleweather.activity.preferences;

import android.content.ContentValues;
import android.content.Context;
import android.provider.CalendarContract;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.event.util.EventUtil;

import java.util.List;

public class ColorListAdapter extends BaseAdapter
{
    private List<ContentValues> colorList;
    private final int currentColor;
    private Context context;

    public ColorListAdapter(int currentColor, List<ContentValues> colorList, Context context)
    {
        this.currentColor = currentColor;
        this.colorList = colorList;
        this.context = context;
    }

    @Override
    public int getCount()
    {
        return colorList.size();
    }

    @Override
    public Object getItem(int i)
    {
        return colorList.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public View getView(int index, View view, ViewGroup viewGroup)
    {
        if (view == null)
        {
            view = new FrameLayout(context);
            int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32f, context.getResources().getDisplayMetrics());
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(size, size);
            view.setLayoutParams(layoutParams);
        }

        view.setBackgroundColor(EventUtil.getColor(colorList.get(index).getAsInteger(CalendarContract.Colors.COLOR)));
        if (currentColor == colorList.get(index).getAsInteger(CalendarContract.Colors.COLOR_KEY))
        {
            //현재 색상
            ImageView checkImageView = new ImageView(context);
            checkImageView.setImageDrawable(context.getDrawable(R.drawable.check_icon));

            int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, context.getResources().getDisplayMetrics());
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(size, size);
            layoutParams.gravity = Gravity.CENTER;
            checkImageView.setLayoutParams(layoutParams);

            ((FrameLayout) view).addView(checkImageView);
        }
        return view;
    }
}
