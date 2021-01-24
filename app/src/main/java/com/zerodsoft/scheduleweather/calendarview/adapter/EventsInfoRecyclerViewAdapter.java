package com.zerodsoft.scheduleweather.calendarview.adapter;

import android.content.ContentValues;
import android.graphics.Color;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarview.eventdialog.EventListOnDayFragment;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.etc.EventViewUtil;
import com.zerodsoft.scheduleweather.utility.AppSettings;

import java.util.Date;
import java.util.List;

public class EventsInfoRecyclerViewAdapter extends RecyclerView.Adapter<EventsInfoRecyclerViewAdapter.EventsInfoViewHolder>
{
    private List<ContentValues> instances;
    private OnEventItemClickListener onEventItemClickListener;
    private static final int VIEW_MARGIN = 16;
    private final long startDate;
    private final long endDate;

    public EventsInfoRecyclerViewAdapter(EventListOnDayFragment fragment, long startDate, long endDate)
    {
        this.onEventItemClickListener = (OnEventItemClickListener) fragment;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @NonNull
    @Override
    public EventsInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new EventsInfoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.events_info_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EventsInfoViewHolder holder, int position)
    {
        holder.onBind(position);
    }

    @Override
    public int getItemCount()
    {
        return instances == null ? 0 : instances.size();
    }

    public void setInstances(List<ContentValues> instances)
    {
        this.instances = instances;
    }

    class EventsInfoViewHolder extends RecyclerView.ViewHolder
    {
        private int position;
        private int eventId;
        private int calendarId;
        private TextView eventView;

        public EventsInfoViewHolder(View view)
        {
            super(view);
            eventView = (TextView) view;
            eventView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    onEventItemClickListener.onClicked(calendarId, eventId);
                }
            });
        }

        public void onBind(int position)
        {
            this.position = position;
            this.eventId = instances.get(position).getAsInteger(CalendarContract.Instances.EVENT_ID);
            this.calendarId = instances.get(position).getAsInteger(CalendarContract.Instances.CALENDAR_ID);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            int[] margin = EventViewUtil.getViewSideMargin(instances.get(position).getAsInteger(CalendarContract.Instances.BEGIN)
                    , instances.get(position).getAsInteger(CalendarContract.Instances.END)
                    , startDate, endDate, VIEW_MARGIN);

            layoutParams.leftMargin = margin[0];
            layoutParams.rightMargin = margin[1];

            eventView.setLayoutParams(layoutParams);

            float[] hsv = new float[3];
            Color.colorToHSV(instances.get(position).getAsInteger(CalendarContract.Instances.EVENT_COLOR), hsv);
            eventView.setBackgroundColor(Color.HSVToColor(hsv));
            eventView.setTextColor(Color.WHITE);

            eventView.setText(instances.get(position).getAsString(CalendarContract.Instances.TITLE));
        }
    }

}
