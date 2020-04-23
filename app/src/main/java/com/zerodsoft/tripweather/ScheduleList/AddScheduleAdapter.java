package com.zerodsoft.tripweather.ScheduleList;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.tripweather.AddScheduleActivity;
import com.zerodsoft.tripweather.AreaSelectionActivity;
import com.zerodsoft.tripweather.R;
import com.zerodsoft.tripweather.ScheduleData.TravelSchedule;

import java.util.ArrayList;

public class AddScheduleAdapter extends RecyclerView.Adapter<AddScheduleAdapter.ViewHolder>
{
    private ArrayList<TravelSchedule> travelSchedules = null;
    Context context;
    LayoutInflater layoutInflater;
    ViewGroup viewGroup;

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textViewTravelName, textViewTravelPeriod, textViewDestinations;
        ImageButton btnEditSchedule, btnRemoveSchedule;

        ViewHolder(View itemView)
        {
            super(itemView);


            textViewTravelName = (TextView) itemView.findViewById(R.id.text_view_schedule_name);
            textViewTravelPeriod = (TextView) itemView.findViewById(R.id.text_view_schedule_period);
            textViewDestinations = (TextView) itemView.findViewById(R.id.text_view_schedule_destinations);

            btnEditSchedule = (ImageButton) itemView.findViewById(R.id.btn_edit_schedule);
            btnRemoveSchedule = (ImageButton) itemView.findViewById(R.id.btn_remove_schedule);

        }
    }

    public AddScheduleAdapter(ArrayList<TravelSchedule> travelSchedules)
    {
        this.travelSchedules = travelSchedules;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        context = parent.getContext();
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.recycler_view_schedule_item, parent, false);
        AddScheduleAdapter.ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        String startDate = travelSchedules.get(position).getTravelStartDate();
        String endDate = travelSchedules.get(position).getTravelEndDate();
        String destinations = null;

        for (int index = 0; index < travelSchedules.get(position).getTravelDestinations().size(); ++index)
        {
            if (index == travelSchedules.get(position).getTravelDestinations().size() - 1)
            {
                destinations += travelSchedules.get(position).getTravelDestinations().get(index);
            } else
            {
                destinations = travelSchedules.get(position).getTravelDestinations().get(index) + ", ";
            }
        }

        holder.textViewTravelName.setText(travelSchedules.get(position).getTravelName());
        holder.textViewTravelPeriod.setText(startDate + " -> " + endDate);
        holder.textViewDestinations.setText(destinations);

        View.OnClickListener editBtnOnClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(view.getContext(), "Edit Btn", Toast.LENGTH_SHORT).show();
            }
        };

        View.OnClickListener removeBtnOnClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(view.getContext(), "Remove Btn", Toast.LENGTH_SHORT).show();
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
}
