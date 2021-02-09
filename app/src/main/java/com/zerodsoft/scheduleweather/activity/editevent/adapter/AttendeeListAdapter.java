package com.zerodsoft.scheduleweather.activity.editevent.adapter;

import android.content.ContentValues;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;


import java.util.List;

public class AttendeeListAdapter extends RecyclerView.Adapter<AttendeeListAdapter.AttendeeViewHolder>
{
    private List<ContentValues> attendeeList;
    final String SELECTED_CALENDAR_NAME;
    final String SELECTED_CALENDAR_OWNER_ACCOUNT;

    public AttendeeListAdapter(List<ContentValues> attendeeList, ContentValues selectedCalendar)
    {
        this.attendeeList = attendeeList;
        SELECTED_CALENDAR_NAME = selectedCalendar.getAsString(CalendarContract.Attendees.ATTENDEE_NAME);
        SELECTED_CALENDAR_OWNER_ACCOUNT = selectedCalendar.getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL);
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
            ((LinearLayout) itemView.findViewById(R.id.attendee_relationship_status_layout)).setVisibility(View.GONE);

            attendeeName = (TextView) itemView.findViewById(R.id.attendee_name);
            removeButton = (ImageButton) itemView.findViewById(R.id.remove_attendee_button);
        }

        public void onBind(int position)
        {
            ContentValues attendee = attendeeList.get(position);
            String attendeeNameValue = null;

            if (attendee.getAsInteger(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP) == CalendarContract.Attendees.RELATIONSHIP_ORGANIZER)
            {
                attendeeNameValue = attendee.getAsString(CalendarContract.Attendees.ATTENDEE_NAME);
                if (attendeeNameValue.equals(SELECTED_CALENDAR_NAME))
                {
                    attendeeNameValue += "(ME)";
                    removeButton.setVisibility(View.GONE);
                }
            } else
            {
                attendeeNameValue = attendee.getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL);
                if (attendeeNameValue.equals(SELECTED_CALENDAR_OWNER_ACCOUNT))
                {
                    attendeeNameValue += "(ME)";
                    removeButton.setVisibility(View.GONE);
                }
            }

            attendeeName.setText(attendeeNameValue);

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
                    attendeeList.remove(getAdapterPosition());
                    if (attendeeList.size() == 1)
                    {
                        attendeeList.clear();
                    }
                    notifyDataSetChanged();
                }
            });
        }
    }
}
