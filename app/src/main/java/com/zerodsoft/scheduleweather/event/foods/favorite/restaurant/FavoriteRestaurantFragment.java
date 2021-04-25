package com.zerodsoft.scheduleweather.event.foods.favorite.restaurant;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.databinding.FragmentFavoriteRestaurantBinding;
import com.zerodsoft.scheduleweather.event.foods.adapter.FoodCategoryFragmentListAdapter;
import com.zerodsoft.scheduleweather.event.foods.categorylist.FoodCategoryTabFragment;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnClickedRestaurantItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.CustomFoodMenuDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FavoriteRestaurantFragment extends Fragment
{
    public static final String TAG = "FavoriteRestaurantFragment";
    private FragmentFavoriteRestaurantBinding binding;
    private FavoriteRestaurantViewModel favoriteRestaurantViewModel;
    private List<String> foodMenusList;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = FragmentFavoriteRestaurantBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        foodMenusList = new ArrayList<>();
        foodMenusList.add(getString(R.string.show_all));
    }

}