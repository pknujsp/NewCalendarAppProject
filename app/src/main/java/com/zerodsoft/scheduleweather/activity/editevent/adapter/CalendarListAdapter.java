package com.zerodsoft.scheduleweather.activity.editevent.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;

import java.util.List;

public class CalendarListAdapter extends BaseAdapter
{
    private List<ContentValues> calendarList;
    private Context context;

    public CalendarListAdapter(Context context, List<ContentValues> calendarList)
    {
        this.context = context;
        this.calendarList = calendarList;
    }


    @Override
    public int getCount()
    {
        return calendarList.size();
    }

    @Override
    public Object getItem(int i)
    {
        return calendarList.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        if (view == null)
        {
            view = LayoutInflater.from(context).inflate(R.layout.calendar_itemview, viewGroup, false);
            final ViewHolder viewHolder = new ViewHolder();

            viewHolder.color = (View) view.findViewById(R.id.calendar_color);
            viewHolder.name = (TextView) view.findViewById(R.id.calendar_display_name);
            viewHolder.accountName = (TextView) view.findViewById(R.id.calendar_account_name);

            view.setTag(viewHolder);
        }


        float[] hsv = new float[3];
        Color.colorToHSV(calendarList.get(i).getAsInteger(CalendarContract.Calendars.CALENDAR_COLOR), hsv);

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.color.setBackgroundColor(Color.HSVToColor(hsv));
        viewHolder.name.setText(calendarList.get(i).getAsString(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME));
        viewHolder.accountName.setText(calendarList.get(i).getAsString(CalendarContract.Calendars.ACCOUNT_NAME));
        return view;
    }

    static class ViewHolder
    {
        protected View color;
        protected TextView name;
        protected TextView accountName;
    }
}
