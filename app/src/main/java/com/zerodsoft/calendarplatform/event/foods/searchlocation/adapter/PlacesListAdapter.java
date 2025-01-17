package com.zerodsoft.calendarplatform.event.foods.searchlocation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.event.foods.searchlocation.interfaces.OnClickedLocationItem;
import com.zerodsoft.calendarplatform.navermap.callback.PlaceItemCallback;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.placeresponse.PlaceDocuments;

public class PlacesListAdapter extends PagedListAdapter<PlaceDocuments, PlacesListAdapter.ItemViewHolder>
{
    private final OnClickedLocationItem onClickedLocationItem;

    public PlacesListAdapter(OnClickedLocationItem onClickedLocationItem)
    {
        super(new PlaceItemCallback());
        this.onClickedLocationItem = onClickedLocationItem;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder
    {
        private TextView placeName;
        private TextView placeCategory;
        private TextView placeAddressName;

        public ItemViewHolder(View view)
        {
            super(view);
            view.findViewById(R.id.place_index).setVisibility(View.GONE);
            view.findViewById(R.id.place_distance).setVisibility(View.GONE);

            placeName = (TextView) view.findViewById(R.id.place_name);
            placeCategory = (TextView) view.findViewById(R.id.place_category);
            placeAddressName = (TextView) view.findViewById(R.id.place_address_name);
        }

        public void bind(PlaceDocuments item)
        {
            placeName.setText(item.getPlaceName());
            placeCategory.setText(item.getCategoryName());
            placeAddressName.setText(item.getAddressName());

            itemView.getRootView().setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    onClickedLocationItem.onClickedLocationItem(getItem(getBindingAdapterPosition()));
                }
            });
        }
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.place_recycler_view_item, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position)
    {
        holder.bind(getItem(position));
    }
}
