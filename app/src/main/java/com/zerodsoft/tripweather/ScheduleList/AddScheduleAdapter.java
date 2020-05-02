package com.zerodsoft.tripweather.ScheduleList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.tripweather.AddScheduleActivity;
import com.zerodsoft.tripweather.NewScheduleActivity;
import com.zerodsoft.tripweather.R;
import com.zerodsoft.tripweather.Room.DTO.Schedule;
import com.zerodsoft.tripweather.Utility.Clock;

import java.util.ArrayList;
import java.util.List;

public class AddScheduleAdapter extends RecyclerView.Adapter<AddScheduleAdapter.ViewHolder>
{
    private List<Schedule> travelSchedules = null;
    Context context;
    Activity activity;
    LayoutInflater layoutInflater;
    ViewGroup viewGroup;

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textViewTravelPeriod, textViewDestinations;
        ImageButton btnEditSchedule, btnRemoveSchedule;

        ViewHolder(View itemView)
        {
            super(itemView);

            textViewTravelPeriod = (TextView) itemView.findViewById(R.id.text_view_added_schedule_period);
            textViewDestinations = (TextView) itemView.findViewById(R.id.text_view_added_schedule_destinations);

            btnEditSchedule = (ImageButton) itemView.findViewById(R.id.btn_edit_schedule);
            btnRemoveSchedule = (ImageButton) itemView.findViewById(R.id.btn_remove_schedule);
        }
    }

    public AddScheduleAdapter(List<Schedule> travelSchedules, Activity activity)
    {
        this.travelSchedules = travelSchedules;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        context = parent.getContext();
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.recycler_view_added_schedule_item, parent, false);
        AddScheduleAdapter.ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        String startDate = Clock.dateFormatSlash.format(travelSchedules.get(position).getStartDateObj().getTime());
        String endDate = Clock.dateFormatSlash.format(travelSchedules.get(position).getEndDateObj().getTime());
        String destinations = travelSchedules.get(position).getArea().toString();

        holder.textViewTravelPeriod.setText(startDate + " -> " + endDate);
        holder.textViewDestinations.setText(destinations);

        View.OnClickListener editBtnOnClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(context, NewScheduleActivity.class);
                Bundle bundle = new Bundle();

                bundle.putSerializable("startDate", travelSchedules.get(position).getStartDateObj());
                bundle.putSerializable("endDate", travelSchedules.get(position).getEndDateObj());
                bundle.putSerializable("area", travelSchedules.get(position).getArea());
                intent.putExtra("position", position);
                intent.setAction("EDIT_SCHEDULE");
                intent.putExtras(bundle);

                activity.startActivityForResult(intent, AddScheduleActivity.EDIT_SCHEDULE);
            }
        };

        View.OnClickListener removeBtnOnClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                travelSchedules.remove(position);
                AddScheduleAdapter.this.notifyDataSetChanged();
            }
        };

        holder.btnEditSchedule.setOnClickListener(editBtnOnClickListener);
        holder.btnRemoveSchedule.setOnClickListener(removeBtnOnClickListener);
    }


    @Override
    public int getItemCount()
    {
        return travelSchedules.size();
    }


    public void addItem(Schedule travelSchedule)
    {
        travelSchedules.add(travelSchedule);
    }

    public void replaceItem(Schedule travelSchedule, int position)
    {
        travelSchedules.set(position, travelSchedule);
    }

    public List<Schedule> getTravelSchedules()
    {
        return travelSchedules;
    }
}
