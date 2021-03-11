package com.zerodsoft.scheduleweather.calendarview.instancedialog.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class InstancesOfDayAdapter extends RecyclerView.Adapter<InstancesOfDayAdapter.InstancesViewHolder>
{

    public InstancesOfDayAdapter()
    {
    }

    @NonNull
    @Override
    public InstancesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull InstancesViewHolder holder, int position)
    {

    }

    @Override
    public int getItemCount()
    {
        return Integer.MAX_VALUE;
    }

    static class InstancesViewHolder extends RecyclerView.ViewHolder
    {

        public InstancesViewHolder(@NonNull View itemView)
        {
            super(itemView);
        }
    }
}
