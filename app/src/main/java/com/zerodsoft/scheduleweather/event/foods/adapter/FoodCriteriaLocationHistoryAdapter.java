package com.zerodsoft.scheduleweather.event.foods.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.event.foods.interfaces.LocationHistoryController;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationSearchHistoryDTO;

import java.util.List;

public class FoodCriteriaLocationHistoryAdapter extends RecyclerView.Adapter<FoodCriteriaLocationHistoryAdapter.HistoryViewHolder>
{
    private final LocationHistoryController locationHistoryController;

    public FoodCriteriaLocationHistoryAdapter(LocationHistoryController locationHistoryController)
    {
        this.locationHistoryController = locationHistoryController;
    }

    private List<FoodCriteriaLocationSearchHistoryDTO> foodCriteriaLocationHistoryList;

    public void setFoodCriteriaLocationHistoryList(List<FoodCriteriaLocationSearchHistoryDTO> foodCriteriaLocationHistoryList)
    {
        this.foodCriteriaLocationHistoryList = foodCriteriaLocationHistoryList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new HistoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_location_history_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position)
    {
        holder.onBind();
    }

    @Override
    public int getItemCount()
    {
        return foodCriteriaLocationHistoryList.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder
    {
        private LinearLayout rootLayout;
        private TextView placeNameTextView;
        private TextView addressNameTextView;
        private TextView anotherAddressTypeTextView;
        private TextView anotherAddressNameTextView;
        private ImageButton deleteButton;

        public HistoryViewHolder(@NonNull View itemView)
        {
            super(itemView);

            rootLayout = (LinearLayout) itemView.findViewById(R.id.root_layout);
            placeNameTextView = (TextView) itemView.findViewById(R.id.place_name);
            addressNameTextView = (TextView) itemView.findViewById(R.id.address_name);
            anotherAddressTypeTextView = (TextView) itemView.findViewById(R.id.another_address_type);
            anotherAddressNameTextView = (TextView) itemView.findViewById(R.id.another_address_name);
            deleteButton = (ImageButton) itemView.findViewById(R.id.delete_button);
        }

        public void onBind()
        {
            FoodCriteriaLocationSearchHistoryDTO history = foodCriteriaLocationHistoryList.get(getAdapterPosition());

            if (history.getPlaceName() != null)
            {
                placeNameTextView.setText(history.getPlaceName());
                addressNameTextView.setText(history.getAddressName());

                placeNameTextView.setVisibility(View.VISIBLE);
                addressNameTextView.setVisibility(View.VISIBLE);
                anotherAddressTypeTextView.setVisibility(View.GONE);
                anotherAddressNameTextView.setVisibility(View.GONE);
            } else
            {
                addressNameTextView.setText(history.getAddressName());
                anotherAddressNameTextView.setText(history.getRoadAddressName());

                placeNameTextView.setVisibility(View.GONE);
                addressNameTextView.setVisibility(View.VISIBLE);
                anotherAddressTypeTextView.setVisibility(View.VISIBLE);
                anotherAddressNameTextView.setVisibility(View.VISIBLE);
            }

            rootLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //해당 위치를 선택
                    locationHistoryController.onClickedLocationHistoryItem(history);
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    locationHistoryController.delete(history.getId());
                }
            });
        }
    }
}
