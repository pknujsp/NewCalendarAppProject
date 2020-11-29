package com.zerodsoft.scheduleweather.scheduleinfo.locationfragments.categoryfragments.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;

public class AroundLocationRecyclerViewAdapter extends PagedListAdapter<PlaceDocuments, AroundLocationRecyclerViewAdapter.ItemViewHolder>
{
    class ItemViewHolder extends RecyclerView.ViewHolder
    {
        public ItemViewHolder(View view)
        {
            super(view);
        }

        public void bind()
        {

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

    public AroundLocationRecyclerViewAdapter()
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
        PlaceDocuments item = getItem(position);
        holder.bind();
    }

}
