package com.zerodsoft.scheduleweather.calendarview.month.EventsInfoFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;
import com.zerodsoft.scheduleweather.utility.AppSettings;

import java.util.Date;
import java.util.List;

public class EventsInfoRecyclerViewAdapter extends RecyclerView.Adapter<EventsInfoRecyclerViewAdapter.EventsInfoViewHolder>
{
    private List<ScheduleDTO> schedulesList;
    private static final int VIEW_MARGIN = 16;
    private Date startDate;
    private Date endDate;

    public EventsInfoRecyclerViewAdapter(Date startDate, Date endDate)
    {
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
        return schedulesList == null ? 0 : schedulesList.size();
    }

    public void setSchedulesList(List<ScheduleDTO> schedulesList)
    {
        this.schedulesList = schedulesList;
    }

    class EventsInfoViewHolder extends RecyclerView.ViewHolder
    {
        private int position;
        private int scheduleId;
        private TextView eventView;

        public EventsInfoViewHolder(View view)
        {
            super(view);
            eventView = (TextView) view;
        }

        public void onBind(int position)
        {
            this.position = position;
            this.scheduleId = schedulesList.get(position).getId();

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            // 시작/종료일이 date가 아니나, 일정에 포함되는 경우
            if (schedulesList.get(position).getStartDate().before(startDate) && schedulesList.get(position).getEndDate().after(endDate))
            {
                layoutParams.leftMargin = 0;
                layoutParams.rightMargin = 0;
            }
            // 시작일이 date인 경우, 종료일은 endDate 이후
            else if (schedulesList.get(position).getEndDate().compareTo(endDate) >= 0 && schedulesList.get(position).getStartDate().compareTo(startDate) >= 0 && schedulesList.get(position).getStartDate().before(endDate))
            {
                layoutParams.leftMargin = VIEW_MARGIN;
                layoutParams.rightMargin = 0;
            }
            // 종료일이 date인 경우, 시작일은 startDate이전
            else if (schedulesList.get(position).getEndDate().compareTo(startDate) >= 0 && schedulesList.get(position).getEndDate().before(endDate) && schedulesList.get(position).getStartDate().before(startDate))
            {
                layoutParams.leftMargin = 0;
                layoutParams.rightMargin = VIEW_MARGIN;
            }
            // 시작/종료일이 date인 경우
            else if (schedulesList.get(position).getEndDate().compareTo(startDate) >= 0 && schedulesList.get(position).getEndDate().before(endDate) && schedulesList.get(position).getStartDate().compareTo(startDate) >= 0 && schedulesList.get(position).getStartDate().before(endDate))
            {
                layoutParams.leftMargin = VIEW_MARGIN;
                layoutParams.rightMargin = VIEW_MARGIN;
            }

            eventView.setLayoutParams(layoutParams);

            if (schedulesList.get(position).getCategory() == ScheduleDTO.GOOGLE_CATEGORY)
            {
                eventView.setBackgroundColor(AppSettings.getGoogleEventBackgroundColor());
                eventView.setTextColor(AppSettings.getGoogleEventTextColor());
            } else
            {
                eventView.setBackgroundColor(AppSettings.getLocalEventBackgroundColor());
                eventView.setTextColor(AppSettings.getLocalEventTextColor());
            }
            eventView.setText(schedulesList.get(position).getSubject());
        }
    }

}
