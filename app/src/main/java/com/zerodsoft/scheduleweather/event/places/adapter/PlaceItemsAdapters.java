package com.zerodsoft.scheduleweather.event.places.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.kakaomap.callback.PlaceItemCallback;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.event.places.interfaces.IClickedPlaceItem;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;

public class PlaceItemsAdapters extends PagedListAdapter<PlaceDocuments, PlaceItemsAdapters.ItemViewHolder>
{
    private final IClickedPlaceItem iClickedPlaceItem;
    private final PlaceCategoryDTO placeCategoryDTO;

    class ItemViewHolder extends RecyclerView.ViewHolder
    {
        private TextView placeNameTextView;

        public ItemViewHolder(View view)
        {
            super(view);
            placeNameTextView = (TextView) view.findViewById(R.id.place_item_name);
            view.getRootView().setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    iClickedPlaceItem.onClickedItem(getAdapterPosition(), placeCategoryDTO, getCurrentList().snapshot());
                }
            });
        }

        public void bind(PlaceDocuments item)
        {
            placeNameTextView.setText(item.getPlaceName());
        }
    }

    public PlaceItemsAdapters(IClickedPlaceItem iClickedPlaceItem, PlaceCategoryDTO placeCategoryDTO)
    {
        super(new PlaceItemCallback());
        this.iClickedPlaceItem = iClickedPlaceItem;
        this.placeCategoryDTO = placeCategoryDTO;
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

    @Override
    public void submitList(@Nullable PagedList<PlaceDocuments> pagedList)
    {
        super.submitList(pagedList);
    }

    
}