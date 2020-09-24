package com.zerodsoft.scheduleweather.calendarview.day;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;
import com.zerodsoft.scheduleweather.utility.AppSettings;

import java.util.ArrayList;
import java.util.List;

public class EventListAdapter extends BaseAdapter
{
    private List<ScheduleDTO> allDaySchedules;

    public EventListAdapter()
    {
        allDaySchedules = new ArrayList<>();
    }

    @Override
    public int getCount()
    {
        // all day인 일정만 표시
        return allDaySchedules.size();
    }

    @Override
    public Object getItem(int i)
    {
        return allDaySchedules.get(i);
    }

    public void setAllDaySchedules(List<ScheduleDTO> schedules)
    {
        this.allDaySchedules = schedules;
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        if (view == null)
        {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.allday_event_item, viewGroup, false);
        }
        TextView eventView = view.findViewById(R.id.allday_subject);
        eventView.setText(allDaySchedules.get(i).getSubject());
        eventView.setBackgroundColor(allDaySchedules.get(i).getCategory() == ScheduleDTO.LOCAL_CATEGORY ? AppSettings.getLocalEventBackgroundColor() : AppSettings.getGoogleEventBackgroundColor());
        eventView.setTextColor(allDaySchedules.get(i).getCategory() == ScheduleDTO.LOCAL_CATEGORY ? AppSettings.getLocalEventTextColor() : AppSettings.getGoogleEventTextColor());
        return view;
    }
}
