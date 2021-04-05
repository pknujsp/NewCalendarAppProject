package com.zerodsoft.scheduleweather.event.foods.search.search.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;

import java.util.ArrayList;
import java.util.List;

public class SearchRestaurantHistoryAdapter extends RecyclerView.Adapter<SearchRestaurantHistoryAdapter.ViewHolder>
{
    private List<String> historyList = new ArrayList<>();
    private final OnClickedListItem<String> onClickedListItem;

    public SearchRestaurantHistoryAdapter(OnClickedListItem<String> onClickedListItem)
    {
        this.onClickedListItem = onClickedListItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_history_item, parent, false));
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
        ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            valueTextView = (TextView) itemView.findViewById(R.id.value);
            deleteButton = (ImageButton) itemView.findViewById(R.id.delete);
        }

        public void onBind()
        {
            valueTextView.setText(historyList.get(getAdapterPosition()));
            deleteButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    onClickedListItem.onClickedListItem(historyList.get(getAdapterPosition()));
                }
            });
        }
    }
}
