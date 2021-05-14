package com.zerodsoft.scheduleweather.event.foods.categorylist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

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
import com.zerodsoft.scheduleweather.event.foods.favorite.restaurant.FavoriteLocationViewModel;
import com.zerodsoft.scheduleweather.event.foods.main.fragment.NewFoodsMainFragment;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.CustomFoodMenuViewModel;
import com.zerodsoft.scheduleweather.event.main.NewInstanceMainFragment;
import com.zerodsoft.scheduleweather.navermap.BottomSheetType;
import com.zerodsoft.scheduleweather.navermap.interfaces.BottomSheetController;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnExtraListDataListener;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.CustomFoodMenuDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import lombok.SneakyThrows;

public class RestaurantListTabFragment extends Fragment implements NewInstanceMainFragment.RestaurantsGetter, OnExtraListDataListener<String>
{
    public static final String TAG = "RestaurantListTabFragment";
    private FragmentFoodCategoryTabBinding binding;

    private CustomFoodMenuViewModel customFoodCategoryViewModel;
    private FavoriteLocationViewModel favoriteRestaurantViewModel;

    private List<String> categoryList;
    private FoodCategoryFragmentListAdapter adapter;
    private final String selectedCategoryName;
    private final NewFoodsMainFragment.FoodMenuChipsViewController foodMenuChipsViewController;
    private final BottomSheetController bottomSheetController;

    private int lastFoodMenuIndex;
    private float lastRecyclerViewY;

    public RestaurantListTabFragment(String selectedCategoryName, NewFoodsMainFragment.FoodMenuChipsViewController foodMenuChipsViewController, BottomSheetController bottomSheetController)
    {
        this.selectedCategoryName = selectedCategoryName;
        this.foodMenuChipsViewController = foodMenuChipsViewController;
        this.bottomSheetController = bottomSheetController;
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
                lastFoodMenuIndex = binding.viewpager.getCurrentItem();
                lastRecyclerViewY = adapter.getFragments().get(lastFoodMenuIndex).binding.recyclerView.getScrollY();
                foodMenuChipsViewController.setCurrentFoodMenuName(categoryList.get(binding.viewpager.getCurrentItem()));
                bottomSheetController.setStateOfBottomSheet(BottomSheetType.RESTAURANT, BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        favoriteRestaurantViewModel = new ViewModelProvider(this).get(FavoriteLocationViewModel.class);
        customFoodCategoryViewModel = new ViewModelProvider(this).get(CustomFoodMenuViewModel.class);
        customFoodCategoryViewModel.select(new CarrierMessagingService.ResultCallback<List<CustomFoodMenuDTO>>()
        {
            @Override
            public void onReceiveResult(@NonNull List<CustomFoodMenuDTO> resultList) throws RemoteException
            {
                requireActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        categoryList = new ArrayList<>();

                        final String[] DEFAULT_FOOD_MENU_NAME_ARR = getResources().getStringArray(R.array.food_menu_list);
                        List<String> foodMenuNameList = new ArrayList<>();
                        foodMenuNameList.addAll(Arrays.asList(DEFAULT_FOOD_MENU_NAME_ARR));
                        categoryList.addAll(foodMenuNameList);

                        if (!resultList.isEmpty())
                        {
                            for (CustomFoodMenuDTO customFoodCategory : resultList)
                            {
                                foodMenuNameList.add(customFoodCategory.getMenuName());
                                categoryList.add(customFoodCategory.getMenuName());
                            }
                        }

                        int selectedIndex = categoryList.indexOf(selectedCategoryName);

                        adapter = new FoodCategoryFragmentListAdapter(RestaurantListTabFragment.this);
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

                        foodMenuChipsViewController.createRestaurantListView(foodMenuNameList, RestaurantListTabFragment.this, RestaurantListTabFragment.this);
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

    @Override
    public void getRestaurants(String foodName, CarrierMessagingService.ResultCallback<List<PlaceDocuments>> callback)
    {
        final int index = categoryList.indexOf(foodName);
        RestaurantListFragment fragment = adapter.getFragments().get(index);
        if (fragment.adapter == null)
        {
            fragment.setAdapterDataObserver(new RecyclerView.AdapterDataObserver()
            {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount)
                {
                    requireActivity().runOnUiThread(new Runnable()
                    {
                        @SneakyThrows
                        @Override
                        public void run()
                        {
                            callback.onReceiveResult(fragment.adapter.getCurrentList().snapshot());
                        }
                    });

                    fragment.adapterDataObserver = null;
                }
            });
            binding.viewpager.setCurrentItem(index);
        } else
        {
            requireActivity().runOnUiThread(new Runnable()
            {
                @SneakyThrows
                @Override
                public void run()
                {
                    callback.onReceiveResult(fragment.adapter.getCurrentList().snapshot());
                }
            });
        }
    }

    @Override
    public void loadExtraListData(String foodMenuName, RecyclerView.AdapterDataObserver adapterDataObserver)
    {
        final int index = categoryList.indexOf(foodMenuName);
        RestaurantListFragment fragment = adapter.getFragments().get(index);

        fragment.adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver()
        {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount)
            {
                super.onItemRangeInserted(positionStart, itemCount);
                adapterDataObserver.onItemRangeInserted(positionStart, itemCount);
                fragment.adapter.unregisterAdapterDataObserver(this);
            }
        });
        fragment.binding.recyclerView.scrollBy(0, 10000);
    }

    @Override
    public void loadExtraListData(RecyclerView.AdapterDataObserver adapterDataObserver)
    {

    }

    public interface RefreshFavoriteState
    {
        void refreshFavorites();
    }


}
