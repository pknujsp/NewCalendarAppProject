package com.zerodsoft.tripweather.ScheduleList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.tripweather.R;
import com.zerodsoft.tripweather.ScheduleData.TravelData;

import java.util.ArrayList;

public class TravelScheduleListAdapter extends RecyclerView.Adapter<TravelScheduleListAdapter.ViewHolder> {
    private ArrayList<TravelData> travelDataList = null;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate, textViewDestination;

        ViewHolder(View itemView) {
            super(itemView);

            textViewDate = (TextView) itemView.findViewById(R.id.text_view_travel_date);
            textViewDestination = (TextView) itemView.findViewById(R.id.text_view_travel_destinations);
        }
    }

    public TravelScheduleListAdapter(ArrayList<TravelData> travelDataList) {
        this.travelDataList = travelDataList;
    }

    @NonNull
    @Override
    public TravelScheduleListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.recycler_view_travel_item, parent, false);
        TravelScheduleListAdapter.ViewHolder viewHolder = new TravelScheduleListAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TravelScheduleListAdapter.ViewHolder holder, int position) {
        holder.textViewDestination.setText(travelDataList.get(position).getDestination());
        holder.textViewDate.setText(travelDataList.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return travelDataList.size();
    }
}
