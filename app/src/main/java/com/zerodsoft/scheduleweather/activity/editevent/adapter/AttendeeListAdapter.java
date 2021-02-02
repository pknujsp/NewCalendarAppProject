package com.zerodsoft.scheduleweather.activity.editevent.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;


import java.util.List;

public class AttendeeListAdapter extends RecyclerView.Adapter<AttendeeListAdapter.AttendeeViewHolder>
{
    private List<ContentValues> attendeeList;
    private ContentValues selectedCalendar;

    public AttendeeListAdapter(List<ContentValues> attendeeList, ContentValues selectedCalendar)
    {
        this.attendeeList = attendeeList;
        this.selectedCalendar = selectedCalendar;
    }

    @NonNull
    @Override
    public AttendeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new AttendeeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.event_attendee_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AttendeeViewHolder holder, int position)
    {
        holder.onBind(position);
    }

    @Override
    public int getItemCount()
    {
        return attendeeList.size();
    }

    class AttendeeViewHolder extends RecyclerView.ViewHolder
    {
        private TextView attendeeName;
        private ImageButton removeButton;

        public AttendeeViewHolder(@NonNull View itemView)
        {
            super(itemView);
            attendeeName = (TextView) itemView.findViewById(R.id.attendee_name);
            removeButton = (ImageButton) itemView.findViewById(R.id.remove_attendee_button);
        }

        public void onBind(int position)
        {
            attendeeName.setText(attendeeList.get(position).getAsString(CalendarContract.Attendees.ATTENDEE_NAME).isEmpty() ?
                    attendeeList.get(position).getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL) :
                    attendeeList.get(position).getAsString(CalendarContract.Attendees.ATTENDEE_NAME));
            attendeeName.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    //logic for communications with attendee
                }
            });
            removeButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                }
            });
        }
    }
}
