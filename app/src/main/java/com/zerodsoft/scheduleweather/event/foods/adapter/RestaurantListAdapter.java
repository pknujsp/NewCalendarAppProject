package com.zerodsoft.scheduleweather.event.foods.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnClickedRestaurantItem;
import com.zerodsoft.scheduleweather.event.foods.searchlocation.adapter.PlacesListAdapter;
import com.zerodsoft.scheduleweather.event.foods.searchlocation.interfaces.OnClickedLocationItem;
import com.zerodsoft.scheduleweather.kakaomap.callback.PlaceItemCallback;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;

public class RestaurantListAdapter extends PagedListAdapter<PlaceDocuments, RestaurantListAdapter.ItemViewHolder>
{
    private final OnClickedRestaurantItem onClickedRestaurantItem;

    public RestaurantListAdapter(OnClickedRestaurantItem onClickedRestaurantItem)
    {
        super(new PlaceItemCallback());
        this.onClickedRestaurantItem = onClickedRestaurantItem;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder
    {
        private TextView restaurantName;
        private ImageView restaurantImage;

        public ItemViewHolder(View view)
        {
            super(view);
            restaurantName = (TextView) view.findViewById(R.id.restaurant_name);
            restaurantImage = (ImageView) view.findViewById(R.id.restaurant_image);
        }

        public void bind(PlaceDocuments item)
        {
            restaurantName.setText(item.getPlaceName());

            itemView.getRootView().setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    onClickedRestaurantItem.onClickedRestaurantItem(getItem(getAdapterPosition()));
                }
            });
        }
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_itemview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position)
    {
        holder.bind(getItem(position));
    }
}
