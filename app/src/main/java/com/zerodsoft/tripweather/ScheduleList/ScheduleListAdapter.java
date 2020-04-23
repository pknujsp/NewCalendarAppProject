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

public class ScheduleListAdapter extends RecyclerView.Adapter<ScheduleListAdapter.ViewHolder> {
    private ArrayList<TravelSchedule> travelSchedules = null;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTravelName, textViewTravelPeriod, textViewDestinations;
        LinearLayout linearLayout;

        ViewHolder(View itemView) {
            super(itemView);

            textViewTravelName = (TextView) itemView.findViewById(R.id.text_view_schedule_name);
            textViewTravelPeriod = (TextView) itemView.findViewById(R.id.text_view_schedule_period);
            textViewDestinations = (TextView) itemView.findViewById(R.id.text_view_schedule_destinations);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linear_layout_schedule_item);

            linearLayout.setClickable(true);
        }
    }


    public ScheduleListAdapter(ArrayList<TravelSchedule> travelSchedules) {
        this.travelSchedules = travelSchedules;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.recycler_view_schedule_item, parent, false);
        ScheduleListAdapter.ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String startDate = travelSchedules.get(position).getTravelStartDate();
        String endDate = travelSchedules.get(position).getTravelEndDate();
        String destinations = null;

        for (int index = 0; index < travelSchedules.get(position).getTravelDestinations().size(); ++index) {
            if (index == travelSchedules.get(position).getTravelDestinations().size() - 1) {
                destinations += travelSchedules.get(position).getTravelDestinations().get(index);
            } else {
                destinations = travelSchedules.get(position).getTravelDestinations().get(index) + ", ";
            }
        }

        holder.textViewTravelName.setText(travelSchedules.get(position).getTravelName());
        holder.textViewTravelPeriod.setText(startDate + " -> " + endDate);
        holder.textViewDestinations.setText(destinations);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.linearLayout.getContext(), TravelScheduleActivity.class);
                holder.linearLayout.getContext().startActivity(intent);
            }
        };

        holder.linearLayout.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        if (travelSchedules == null || travelSchedules.isEmpty()) {
            return 0;
        } else {
            return travelSchedules.size();
        }
    }
}
