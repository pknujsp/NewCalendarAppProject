package com.zerodsoft.tripweather.ScheduleList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.tripweather.R;
import com.zerodsoft.tripweather.ScheduleData.TravelSchedule;
import com.zerodsoft.tripweather.TravelScheduleActivity;

import java.nio.BufferUnderflowException;
import java.util.ArrayList;

public class ScheduleListAdapter extends RecyclerView.Adapter<ScheduleListAdapter.ViewHolder>
{
    private ArrayList<TravelSchedule> travelSchedules = null;

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textViewTravelPeriod, textViewDestinations;
        LinearLayout linearLayout;

        ViewHolder(View itemView)
        {
            super(itemView);

            textViewTravelPeriod = (TextView) itemView.findViewById(R.id.text_view_schedule_period);
            textViewDestinations = (TextView) itemView.findViewById(R.id.text_view_schedule_destinations);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linear_layout_schedule_item);
        }
    }


    public ScheduleListAdapter(ArrayList<TravelSchedule> travelSchedules)
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
        String period = travelSchedules.get(position).getStartDateStr() + " -> " + travelSchedules.get(position).getEndDateStr();

        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(holder.linearLayout.getContext(), TravelScheduleActivity.class);
                holder.linearLayout.getContext().startActivity(intent);
            }
        };

        holder.linearLayout.setOnClickListener(onClickListener);
        holder.textViewDestinations.setText(destination);
        holder.textViewTravelPeriod.setText(period);
    }

    @Override
    public int getItemCount()
    {
        if (travelSchedules == null || travelSchedules.isEmpty())
        {
            return 0;
        } else
        {
            return travelSchedules.size();
        }
    }
}
