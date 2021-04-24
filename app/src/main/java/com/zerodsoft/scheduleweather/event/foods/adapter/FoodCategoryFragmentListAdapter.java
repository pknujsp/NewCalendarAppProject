package com.zerodsoft.scheduleweather.event.foods.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.zerodsoft.scheduleweather.event.foods.categorylist.RestaurantListFragment;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnClickedRestaurantItem;
import com.zerodsoft.scheduleweather.room.interfaces.FavoriteRestaurantQuery;

import java.util.ArrayList;
import java.util.List;

public class FoodCategoryFragmentListAdapter extends FragmentStateAdapter
{
    private List<RestaurantListFragment> fragments;
    private List<String> categoryList;
    private OnClickedRestaurantItem onClickedRestaurantItem;
    private FavoriteRestaurantQuery favoriteRestaurantQuery;

    public FoodCategoryFragmentListAdapter(@NonNull Fragment fragment)
    {
        super(fragment);
    }

    public void init(OnClickedRestaurantItem onClickedRestaurantItem, FavoriteRestaurantQuery favoriteRestaurantQuery, List<String> categoryList)
    {
        this.onClickedRestaurantItem = onClickedRestaurantItem;
        this.favoriteRestaurantQuery = favoriteRestaurantQuery;
        this.categoryList = categoryList;
        this.fragments = new ArrayList<>();

        for (String categoryName : categoryList)
        {
            fragments.add(new RestaurantListFragment(onClickedRestaurantItem, favoriteRestaurantQuery, categoryName));
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position)
    {
        return fragments.get(position);
    }

    @Override
    public int getItemCount()
    {
        return fragments.size();
    }
}
