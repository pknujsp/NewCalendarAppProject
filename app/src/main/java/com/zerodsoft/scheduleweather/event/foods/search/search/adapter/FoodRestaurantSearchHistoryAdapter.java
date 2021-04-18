package com.zerodsoft.scheduleweather.event.foods.search.search.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.room.dto.SearchHistoryDTO;

import java.util.ArrayList;
import java.util.List;

public class FoodRestaurantSearchHistoryAdapter extends RecyclerView.Adapter<FoodRestaurantSearchHistoryAdapter.ViewHolder>
{
    private List<SearchHistoryDTO> historyList = new ArrayList<>();
    private final OnClickedListItem<SearchHistoryDTO> onClickedListItem;

    public FoodRestaurantSearchHistoryAdapter(OnClickedListItem<SearchHistoryDTO> onClickedListItem)
    {
        this.onClickedListItem = onClickedListItem;
    }

    public void setHistoryList(List<SearchHistoryDTO> historyList)
    {
        this.historyList = historyList;
    }

    public List<SearchHistoryDTO> getHistoryList()
    {
        return historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_history_horizontal_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.onBind();
    }

    @Override
    public int getItemCount()
    {
        return historyList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView valueTextView;
        ImageView deleteButton;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            valueTextView = (TextView) itemView.findViewById(R.id.history_value);
            deleteButton = (ImageView) itemView.findViewById(R.id.delete_history_value);
        }

        public void onBind()
        {
            valueTextView.setText(historyList.get(getAdapterPosition()).getValue());

            valueTextView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    onClickedListItem.onClickedListItem(historyList.get(getAdapterPosition()));
                }
            });
            deleteButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    onClickedListItem.deleteListItem(historyList.get(getAdapterPosition()), getAdapterPosition());
                }
            });
        }
    }
}
