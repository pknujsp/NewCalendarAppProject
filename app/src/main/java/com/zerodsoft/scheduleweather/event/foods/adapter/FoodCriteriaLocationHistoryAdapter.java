package com.zerodsoft.scheduleweather.event.foods.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FoodCriteriaLocationHistoryAdapter extends RecyclerView.Adapter<FoodCriteriaLocationHistoryAdapter.HistoryViewHolder>
{
    private List<FoodCriteriaLocationDTO> foodCriteriaLocationDTOS;

    public void setFoodCriteriaLocationDTOS(List<FoodCriteriaLocationDTO> foodCriteriaLocationDTOS)
    {
        this.foodCriteriaLocationDTOS = foodCriteriaLocationDTOS;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position)
    {
        holder.onBind();
    }

    @Override
    public int getItemCount()
    {
        return foodCriteriaLocationDTOS.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder
    {

        public HistoryViewHolder(@NonNull View itemView)
        {
            super(itemView);
        }

        public void onBind()
        {

        }
    }
}
