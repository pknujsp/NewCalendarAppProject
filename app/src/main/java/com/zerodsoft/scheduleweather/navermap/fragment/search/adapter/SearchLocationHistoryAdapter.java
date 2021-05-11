package com.zerodsoft.scheduleweather.navermap.fragment.search.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.room.dto.SearchHistoryDTO;

import java.util.ArrayList;
import java.util.List;

public class SearchLocationHistoryAdapter extends RecyclerView.Adapter<SearchLocationHistoryAdapter.ViewHolder>
{
    private List<SearchHistoryDTO> historyList = new ArrayList<>();
    private final OnClickedListItem<SearchHistoryDTO> onClickedListItem;

    public SearchLocationHistoryAdapter(OnClickedListItem<SearchHistoryDTO> onClickedListItem)
    {
        this.onClickedListItem = onClickedListItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_history_item, parent, false));
    }

    public void setHistoryList(List<SearchHistoryDTO> historyList)
    {
        this.historyList = historyList;
    }

    public List<SearchHistoryDTO> getHistoryList()
    {
        return historyList;
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
            valueTextView.setText(historyList.get(getBindingAdapterPosition()).getValue());

            itemView.getRootView().setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    onClickedListItem.onClickedListItem(historyList.get(getBindingAdapterPosition()), getBindingAdapterPosition());
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    onClickedListItem.deleteListItem(historyList.get(getBindingAdapterPosition()), getBindingAdapterPosition());
                }
            });
        }
    }
}
