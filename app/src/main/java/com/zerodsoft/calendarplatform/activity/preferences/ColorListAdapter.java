package com.zerodsoft.calendarplatform.activity.preferences;

import android.content.ContentValues;
import android.content.Context;
import android.provider.CalendarContract;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.event.util.EventUtil;

import java.util.List;

public class ColorListAdapter extends BaseAdapter
{
    private List<ContentValues> colorList;
    private final String currentColorKey;
    private Context context;
    private LayoutInflater layoutInflater;
    private final int VIEW_SIZE;

    public ColorListAdapter(String currentColorKey, List<ContentValues> colorList, Context context)
    {
        this.currentColorKey = currentColorKey;
        this.colorList = colorList;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.VIEW_SIZE = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f, context.getResources().getDisplayMetrics());
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
        ColorViewHolder viewHolder = null;

        if (view == null)
        {
            view = layoutInflater.inflate(R.layout.color_griditem, null);

            view.setLayoutParams(new GridView.LayoutParams(VIEW_SIZE, VIEW_SIZE));

            viewHolder = new ColorViewHolder();
            viewHolder.colorView = (FrameLayout) view.findViewById(R.id.color_view);
            viewHolder.checkImage = (ImageView) view.findViewById(R.id.color_check_image);

            view.setTag(viewHolder);
        } else
        {
            viewHolder = (ColorViewHolder) view.getTag();
        }

        viewHolder.colorView.setBackgroundColor(EventUtil.getColor(colorList.get(index).getAsInteger(CalendarContract.Colors.COLOR)));
        if (currentColorKey != null)
        {
            if (currentColorKey.equals(colorList.get(index).getAsString(CalendarContract.Colors.COLOR_KEY)))
            {
                //현재 색상
                viewHolder.checkImage.setVisibility(View.VISIBLE);
            }
        }
        return view;
    }

    static class ColorViewHolder
    {
        FrameLayout colorView;
        ImageView checkImage;
    }
}
