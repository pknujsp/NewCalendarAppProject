package com.zerodsoft.tripweather.ScheduleList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.tripweather.R;
import com.zerodsoft.tripweather.Room.DTO.Schedule;

import java.util.List;

public class ScheduleListAdapter extends RecyclerView.Adapter<ScheduleListAdapter.ViewHolder>
{
    private List<Schedule> travelSchedules = null;

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textViewTravelDate, textViewArea;
        ImageView morningSky, daySky, eveningSky;
        LinearLayout linearLayout;

        ViewHolder(View itemView)
        {
            super(itemView);

            textViewTravelDate = (TextView) itemView.findViewById(R.id.text_view_travel_date);
            textViewArea = (TextView) itemView.findViewById(R.id.text_view_travel_area);
            morningSky = (ImageView) itemView.findViewById(R.id.image_view_morning_sky);
            daySky = (ImageView) itemView.findViewById(R.id.image_view_day_sky);
            eveningSky = (ImageView) itemView.findViewById(R.id.image_view_evening_sky);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linear_layout_schedule_item);
        }
    }


    public ScheduleListAdapter(List<Schedule> travelSchedules)
    {
        this.travelSchedules = travelSchedules;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.recycler_view_schedule_item, parent, false);
        ScheduleListAdapter.ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        String destination = travelSchedules.get(position).getAreaName();
        String date = travelSchedules.get(position).getDate();

        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        };

        holder.linearLayout.setOnClickListener(onClickListener);
        holder.textViewArea.setText(destination);
        holder.textViewTravelDate.setText(date);
    }

    @Override
    public int getItemCount()
    {
        return travelSchedules.size();
    }
}
