package com.zerodsoft.tripweather.ScheduleList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.tripweather.R;
import com.zerodsoft.tripweather.Room.TravelScheduleThread;
import com.zerodsoft.tripweather.ScheduleData.TravelData;

import java.util.ArrayList;

public class TravelScheduleListAdapter extends RecyclerView.Adapter<TravelScheduleListAdapter.ViewHolder>
{
    private ArrayList<TravelData> travelDataList = null;
    private Activity activity;

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textViewTravelName, textViewPeriod;
        LinearLayout linearLayout;
        int travelId;

        ViewHolder(View itemView)
        {
            super(itemView);

            textViewTravelName = (TextView) itemView.findViewById(R.id.text_view_travel_name);
            textViewPeriod = (TextView) itemView.findViewById(R.id.text_view_travel_period);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linear_layout_travel_item);
        }
    }

    public TravelScheduleListAdapter(Activity activity, ArrayList<TravelData> travelDataList)
    {
        this.travelDataList = travelDataList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public TravelScheduleListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.recycler_view_travel_item, parent, false);
        TravelScheduleListAdapter.ViewHolder viewHolder = new TravelScheduleListAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TravelScheduleListAdapter.ViewHolder holder, int position)
    {
        holder.textViewTravelName.setText(travelDataList.get(position).getTravelName());
        holder.textViewPeriod.setText(travelDataList.get(position).getPeriod());
        holder.travelId = travelDataList.get(position).getTravelId();

        holder.linearLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                TravelScheduleThread travelScheduleThread = new TravelScheduleThread(activity, holder.travelId, 2);
                travelScheduleThread.start();
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return travelDataList.size();
    }

    public void setActivity(Activity activity)
    {
        this.activity = activity;
    }
}
