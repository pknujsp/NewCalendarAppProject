package com.zerodsoft.scheduleweather.scheduleinfo.locationfragments.categoryfragments.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.AsyncPagedListDiffer;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;

public class PlaceItemsAdapters extends PagedListAdapter<PlaceDocuments, PlaceItemsAdapters.ItemViewHolder>
{
    class ItemViewHolder extends RecyclerView.ViewHolder
    {
        private TextView placeNameTextView;

        public ItemViewHolder(View view)
        {
            super(view);
            placeNameTextView = (TextView) view.findViewById(R.id.place_item_name);
        }

        public void bind(PlaceDocuments item)
        {
            placeNameTextView.setText(item.getPlaceName());
        }
    }

    private static DiffUtil.ItemCallback<PlaceDocuments> ITEM_CALLBACK = new DiffUtil.ItemCallback<PlaceDocuments>()
    {
        @Override
        public boolean areItemsTheSame(@NonNull PlaceDocuments oldItem, @NonNull PlaceDocuments newItem)
        {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull PlaceDocuments oldItem, @NonNull PlaceDocuments newItem)
        {
            return oldItem.getId().equals(newItem.getId());
        }
    };

    public PlaceItemsAdapters()
    {
        super(ITEM_CALLBACK);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item_around_location, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position)
    {
        holder.bind(getItem(position));
    }

}
