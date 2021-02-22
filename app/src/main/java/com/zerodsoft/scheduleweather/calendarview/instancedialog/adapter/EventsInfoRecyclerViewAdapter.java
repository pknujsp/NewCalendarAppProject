package com.zerodsoft.scheduleweather.calendarview.instancedialog.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.provider.CalendarContract;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.event.util.EventUtil;

import java.util.List;

public class EventsInfoRecyclerViewAdapter extends RecyclerView.Adapter<EventsInfoRecyclerViewAdapter.EventsInfoViewHolder>
{
    private List<ContentValues> instances;
    private OnEventItemClickListener onEventItemClickListener;
    private Context context;
    private Float VIEW_MARGIN;
    private final long BEGIN;
    private final long END;

    public EventsInfoRecyclerViewAdapter(OnEventItemClickListener onEventItemClickListener, long BEGIN, long END)
    {
        this.onEventItemClickListener = onEventItemClickListener;
        this.BEGIN = BEGIN;
        this.END = END;
    }

    @NonNull
    @Override
    public EventsInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        this.context = parent.getContext();
        this.VIEW_MARGIN = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, parent.getContext().getResources().getDisplayMetrics());
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
        private long instanceId;
        private int calendarId;
        private long begin;
        private long end;
        private long eventId;
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
                    onEventItemClickListener.onClickedOnDialog(calendarId, instanceId, eventId, begin, end);
                }
            });
        }

        public void onBind(int position)
        {
            this.position = position;
            this.instanceId = instances.get(position).getAsLong(CalendarContract.Instances._ID);
            this.calendarId = instances.get(position).getAsInteger(CalendarContract.Instances.CALENDAR_ID);
            this.begin = instances.get(position).getAsLong(CalendarContract.Instances.BEGIN);
            this.end = instances.get(position).getAsLong(CalendarContract.Instances.END);
            this.eventId = instances.get(position).getAsLong(CalendarContract.Instances.EVENT_ID);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            int[] margin = EventUtil.getViewSideMargin(instances.get(position).getAsLong(CalendarContract.Instances.BEGIN)
                    , instances.get(position).getAsLong(CalendarContract.Instances.END)
                    , BEGIN, END, VIEW_MARGIN.intValue(), instances.get(position).getAsBoolean(CalendarContract.Instances.ALL_DAY));

            layoutParams.leftMargin = margin[0];
            layoutParams.rightMargin = margin[1];

            eventView.setLayoutParams(layoutParams);
            eventView.setBackgroundColor(EventUtil.getColor(instances.get(position).getAsInteger(CalendarContract.Instances.EVENT_COLOR)));

            if (instances.get(position).getAsString(CalendarContract.Instances.TITLE) != null)
            {
                if (instances.get(position).getAsString(CalendarContract.Instances.TITLE).isEmpty())
                {
                    eventView.setText(context.getString(R.string.empty_title));
                } else
                {
                    eventView.setText(instances.get(position).getAsString(CalendarContract.Instances.TITLE));
                }
            } else
            {
                eventView.setText(context.getString(R.string.empty_title));
            }
        }
    }

}
