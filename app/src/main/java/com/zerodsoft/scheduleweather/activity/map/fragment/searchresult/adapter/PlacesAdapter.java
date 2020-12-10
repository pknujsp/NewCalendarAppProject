package com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.adapter;

import android.content.Context;
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

public class PlacesAdapter extends PagedListAdapter<PlaceDocuments, PlacesAdapter.ItemViewHolder>
{
    private Context context;

    class ItemViewHolder extends RecyclerView.ViewHolder
    {
        private TextView placeName;
        private TextView placeIndex;
        private TextView placeCategory;
        private TextView placeAddressName;
        private TextView placeDistance;

        public ItemViewHolder(View view)
        {
            super(view);
            placeName = (TextView) view.findViewById(R.id.place_name);
            placeIndex = (TextView) view.findViewById(R.id.place_index);
            placeCategory = (TextView) view.findViewById(R.id.place_category);
            placeAddressName = (TextView) view.findViewById(R.id.place_address_name);
            placeDistance = (TextView) view.findViewById(R.id.place_distance);
        }

        public void bind(PlaceDocuments item)
        {
            placeName.setText(item.getPlaceName());
            placeIndex.setText(String.valueOf(getAdapterPosition() + 1));
            placeCategory.setText(item.getCategoryName());
            placeAddressName.setText(item.getAddressName());
            placeDistance.setText(item.getDistance() + "M");
        }
    }

    public PlacesAdapter(Context context)
    {
        super(new PlaceItemCallback());
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new PlacesAdapter.ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.place_recycler_view_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position)
    {
        holder.bind(getItem(position));
    }

    @Override
    public void submitList(@Nullable PagedList<PlaceDocuments> pagedList, @Nullable Runnable commitCallback)
    {
        super.submitList(pagedList, commitCallback);
    }

    @Override
    public void submitList(@Nullable PagedList<PlaceDocuments> pagedList)
    {
        super.submitList(pagedList);
    }
}
