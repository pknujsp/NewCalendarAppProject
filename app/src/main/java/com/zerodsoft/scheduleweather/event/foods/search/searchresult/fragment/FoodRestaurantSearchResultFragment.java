package com.zerodsoft.scheduleweather.event.foods.search.searchresult.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.event.foods.categorylist.RestaurantListFragment;
import com.zerodsoft.scheduleweather.event.foods.favorite.restaurant.FavoriteLocationViewModel;
import com.zerodsoft.scheduleweather.navermap.interfaces.FavoriteLocationsListener;

public class FoodRestaurantSearchResultFragment extends RestaurantListFragment
{
    public static final String TAG = "FoodRestaurantSearchResultFragment";
    private final OnDeleteSearchView onDeleteSearchView;
    private FavoriteLocationViewModel favoriteRestaurantViewModel;

    public FoodRestaurantSearchResultFragment(String searchWord, FavoriteLocationsListener favoriteLocationsListener, Fragment fragment)
    {
        super(searchWord, favoriteLocationsListener);
        this.onDeleteSearchView = (OnDeleteSearchView) fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    public void search(String searchWord)
    {
        this.CATEGORY_NAME = searchWord;
        requestRestaurantList(CATEGORY_NAME);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        favoriteRestaurantViewModel = new ViewModelProvider(this).get(FavoriteLocationViewModel.class);
        favoriteRestaurantDbQuery = favoriteRestaurantViewModel;

        super.onViewCreated(view, savedInstanceState);
    }


    public interface OnDeleteSearchView
    {
        void deleteQuery();
    }
}