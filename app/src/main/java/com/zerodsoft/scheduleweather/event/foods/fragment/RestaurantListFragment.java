package com.zerodsoft.scheduleweather.event.foods.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnClickedRestaurantItem;

public class RestaurantListFragment extends Fragment
{
    private final OnClickedRestaurantItem onClickedRestaurantItem;
    private final String CATEGORY_NAME;
    private RecyclerView restaurantRecyclerView;

    public RestaurantListFragment(OnClickedRestaurantItem onClickedRestaurantItem, String CATEGORY_NAME)
    {
        this.onClickedRestaurantItem = onClickedRestaurantItem;
        this.CATEGORY_NAME = CATEGORY_NAME;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_restaurant_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        restaurantRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

    }
}