package com.zerodsoft.scheduleweather.event.foods.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.zerodsoft.scheduleweather.event.foods.categorylist.FoodCategoryTabFragment;
import com.zerodsoft.scheduleweather.event.foods.categorylist.FoodsCategoryListFragment;
import com.zerodsoft.scheduleweather.event.foods.categorylist.RestaurantListFragment;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnClickedRestaurantItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.interfaces.FavoriteRestaurantQuery;

import java.util.ArrayList;
import java.util.List;

public class FoodCategoryFragmentListAdapter extends FragmentStateAdapter implements FoodCategoryTabFragment.RefreshFavoriteState, FoodsCategoryListFragment.RestaurantItemGetter
{
    private List<RestaurantListFragment> fragments;
    private List<String> categoryList;
    private FavoriteRestaurantQuery favoriteRestaurantQuery;

    public FoodCategoryFragmentListAdapter(@NonNull Fragment fragment)
    {
        super(fragment);
    }

    public void init(FavoriteRestaurantQuery favoriteRestaurantQuery, List<String> categoryList)
    {
        this.favoriteRestaurantQuery = favoriteRestaurantQuery;
        this.categoryList = categoryList;
        this.fragments = new ArrayList<>();

        for (String categoryName : categoryList)
        {
            fragments.add(new RestaurantListFragment(favoriteRestaurantQuery, categoryName));
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

    @Override
    public void refreshFavorites()
    {
        for (RestaurantListFragment fragment : fragments)
        {
            fragment.refreshFavorites();
        }
    }

    @Override
    public List<PlaceDocuments> getRestaurantList(String foodMenuName)
    {
        return fragments.get(categoryList.indexOf(foodMenuName)).getRestaurantList(foodMenuName);
    }
}
