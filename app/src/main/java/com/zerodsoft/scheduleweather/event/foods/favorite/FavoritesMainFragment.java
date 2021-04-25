package com.zerodsoft.scheduleweather.event.foods.favorite;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentFavoritesMainBinding;
import com.zerodsoft.scheduleweather.event.foods.favorite.restaurant.FavoriteRestaurantFragment;


public class FavoritesMainFragment extends Fragment
{
    public static final String TAG = "FavoritesMainFragment";
    private FragmentFavoritesMainBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = FragmentFavoritesMainBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        FavoriteRestaurantFragment favoriteRestaurantFragment = new FavoriteRestaurantFragment();
        getChildFragmentManager().beginTransaction().add(binding.favoritesFragmentContainer.getId(),
                favoriteRestaurantFragment, FavoriteRestaurantFragment.TAG).commit();
    }
}