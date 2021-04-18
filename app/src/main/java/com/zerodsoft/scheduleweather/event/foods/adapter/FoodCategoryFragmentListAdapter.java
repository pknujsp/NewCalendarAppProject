package com.zerodsoft.scheduleweather.event.foods.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.zerodsoft.scheduleweather.event.foods.categorylist.RestaurantListFragment;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnClickedRestaurantItem;

import java.util.ArrayList;
import java.util.List;

public class FoodCategoryFragmentListAdapter extends FragmentStateAdapter
{
    private List<RestaurantListFragment> fragments;
    private List<String> categoryList;
    private OnClickedRestaurantItem onClickedRestaurantItem;

    public FoodCategoryFragmentListAdapter(@NonNull Fragment fragment)
    {
        super(fragment);
    }

    public void init(OnClickedRestaurantItem onClickedRestaurantItem, List<String> categoryList)
    {
        this.onClickedRestaurantItem = onClickedRestaurantItem;
        this.categoryList = categoryList;
        this.fragments = new ArrayList<>();

        for (String categoryName : categoryList)
        {
            fragments.add(new RestaurantListFragment(onClickedRestaurantItem, categoryName));
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
