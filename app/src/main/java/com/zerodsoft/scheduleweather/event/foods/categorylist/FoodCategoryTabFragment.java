package com.zerodsoft.scheduleweather.event.foods.categorylist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentFoodCategoryTabBinding;
import com.zerodsoft.scheduleweather.event.foods.adapter.FoodCategoryFragmentListAdapter;
import com.zerodsoft.scheduleweather.event.foods.favorite.restaurant.FavoriteRestaurantViewModel;
import com.zerodsoft.scheduleweather.event.foods.main.fragment.NewFoodsMainFragment;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.CustomFoodMenuViewModel;
import com.zerodsoft.scheduleweather.navermap.BottomSheetType;
import com.zerodsoft.scheduleweather.navermap.interfaces.BottomSheetController;
import com.zerodsoft.scheduleweather.room.dto.CustomFoodMenuDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FoodCategoryTabFragment extends Fragment
{
    public static final String TAG = "FoodCategoryTabFragment";
    private FragmentFoodCategoryTabBinding binding;

    private CustomFoodMenuViewModel customFoodCategoryViewModel;
    private FavoriteRestaurantViewModel favoriteRestaurantViewModel;

    private List<String> categoryList;
    private FoodCategoryFragmentListAdapter adapter;
    private final String selectedCategoryName;
    private FoodsCategoryListFragment.RestaurantItemGetter restaurantItemGetter;
    private final NewFoodsMainFragment.FoodMenuChipsViewController foodMenuChipsViewController;
    private final BottomSheetController bottomSheetController;

    public FoodCategoryTabFragment(String selectedCategoryName, NewFoodsMainFragment.FoodMenuChipsViewController foodMenuChipsViewController, BottomSheetController bottomSheetController)
    {
        this.selectedCategoryName = selectedCategoryName;
        this.foodMenuChipsViewController = foodMenuChipsViewController;
        this.bottomSheetController = bottomSheetController;
    }

    public FoodsCategoryListFragment.RestaurantItemGetter getRestaurantItemGetter()
    {
        return restaurantItemGetter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        foodMenuChipsViewController.removeRestaurantListView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = FragmentFoodCategoryTabBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        binding.openMapToShowRestaurants.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String currentCategoryName = categoryList.get(binding.viewpager.getCurrentItem());
                bottomSheetController.setStateOfBottomSheet(BottomSheetType.RESTAURANT, BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        favoriteRestaurantViewModel = new ViewModelProvider(this).get(FavoriteRestaurantViewModel.class);
        customFoodCategoryViewModel = new ViewModelProvider(this).get(CustomFoodMenuViewModel.class);
        customFoodCategoryViewModel.select(new CarrierMessagingService.ResultCallback<List<CustomFoodMenuDTO>>()
        {
            @Override
            public void onReceiveResult(@NonNull List<CustomFoodMenuDTO> resultList) throws RemoteException
            {
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        categoryList = new ArrayList<>();

                        final String[] DEFAULT_FOOD_MENU_NAME_ARR = getResources().getStringArray(R.array.food_menu_list);
                        final List<String> foodMenuNameList = Arrays.asList(DEFAULT_FOOD_MENU_NAME_ARR);

                        categoryList.addAll(Arrays.asList(DEFAULT_FOOD_MENU_NAME_ARR));

                        if (!resultList.isEmpty())
                        {
                            for (CustomFoodMenuDTO customFoodCategory : resultList)
                            {
                                foodMenuNameList.add(customFoodCategory.getMenuName());
                                categoryList.add(customFoodCategory.getMenuName());
                            }
                        }

                        int selectedIndex = categoryList.indexOf(selectedCategoryName);

                        adapter = new FoodCategoryFragmentListAdapter(FoodCategoryTabFragment.this);
                        restaurantItemGetter = adapter;
                        adapter.init(favoriteRestaurantViewModel, categoryList);
                        binding.viewpager.setAdapter(adapter);

                        new TabLayoutMediator(binding.tabs, binding.viewpager,
                                new TabLayoutMediator.TabConfigurationStrategy()
                                {
                                    @Override
                                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position)
                                    {
                                        tab.setText(categoryList.get(position));
                                    }
                                }
                        ).attach();

                        foodMenuChipsViewController.createRestaurantListView(foodMenuNameList, adapter);
                        binding.tabs.selectTab(binding.tabs.getTabAt(selectedIndex));
                    }
                });
            }
        });


    }

    @Override
    public void onHiddenChanged(boolean hidden)
    {
        super.onHiddenChanged(hidden);
        if (hidden)
        {
            //음식점 즐겨찾기 갱신
        } else
        {
            adapter.refreshFavorites();
        }
    }

    public void createFragment(int index)
    {

    }

    public interface RefreshFavoriteState
    {
        void refreshFavorites();
    }
}
