package com.zerodsoft.scheduleweather.event.foods.main.fragment;

import android.content.ContentValues;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.RemoteException;
import android.provider.CalendarContract;
import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.OnBackPressedCallbackController;
import com.zerodsoft.scheduleweather.databinding.FragmentNewFoodsMainBinding;
import com.zerodsoft.scheduleweather.event.foods.activity.FoodsActivity;
import com.zerodsoft.scheduleweather.event.foods.favorite.FavoritesMainFragment;
import com.zerodsoft.scheduleweather.event.foods.favorite.restaurant.FavoriteRestaurantViewModel;
import com.zerodsoft.scheduleweather.event.foods.search.search.fragment.SearchRestaurantFragment;
import com.zerodsoft.scheduleweather.event.foods.settings.FoodsSettingsFragment;
import com.zerodsoft.scheduleweather.event.foods.share.FavoriteRestaurantCloud;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationInfoViewModel;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.BottomSheetController;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.INetwork;
import com.zerodsoft.scheduleweather.room.dto.FavoriteRestaurantDTO;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationInfoDTO;
import com.zerodsoft.scheduleweather.utility.NetworkStatus;

import java.util.List;


public class NewFoodsMainFragment extends Fragment implements INetwork, BottomNavigationView.OnNavigationItemSelectedListener, OnBackPressedCallbackController
{
    public static final String TAG = "NewFoodsMainFragment";
    private FragmentNewFoodsMainBinding binding;

    private FoodCriteriaLocationInfoViewModel foodCriteriaLocationInfoViewModel;
    private FavoriteRestaurantViewModel favoriteRestaurantViewModel;
    private NetworkStatus networkStatus;
    private Fragment currentShowingFragment;

    private final int CALENDAR_ID;
    private final long INSTANCE_ID;
    private final long EVENT_ID;
    private final BottomSheetController bottomSheetController;

    public NewFoodsMainFragment(BottomSheetController bottomSheetController, int CALENDAR_ID, long INSTANCE_ID, long EVENT_ID)
    {
        this.bottomSheetController = bottomSheetController;
        this.CALENDAR_ID = CALENDAR_ID;
        this.INSTANCE_ID = INSTANCE_ID;
        this.EVENT_ID = EVENT_ID;
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
        binding = FragmentNewFoodsMainBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        networkStatus = new NetworkStatus(getContext(), new ConnectivityManager.NetworkCallback()
        {
            @Override
            public void onAvailable(@NonNull Network network)
            {
                super.onAvailable(network);
            }

            @Override
            public void onLost(@NonNull Network network)
            {
                super.onLost(network);
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        requireActivity().finish();
                    }
                });
            }
        });

        binding.bottomNavigation.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
        binding.bottomNavigation.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener()
        {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item)
            {

            }
        });

        favoriteRestaurantViewModel = new ViewModelProvider(this).get(FavoriteRestaurantViewModel.class);
        favoriteRestaurantViewModel.select(new CarrierMessagingService.ResultCallback<List<FavoriteRestaurantDTO>>()
        {
            @Override
            public void onReceiveResult(@NonNull List<FavoriteRestaurantDTO> favoriteRestaurantDTOS) throws RemoteException
            {
                FavoriteRestaurantCloud favoriteRestaurantCloud = FavoriteRestaurantCloud.newInstance();
                for (FavoriteRestaurantDTO favoriteRestaurantDTO : favoriteRestaurantDTOS)
                {
                    favoriteRestaurantCloud.add(favoriteRestaurantDTO.getRestaurantId());
                }
            }
        });

        foodCriteriaLocationInfoViewModel = new ViewModelProvider(this).get(FoodCriteriaLocationInfoViewModel.class);
        //기준 위치 정보가 저장되어있는지 확인
        foodCriteriaLocationInfoViewModel.selectByEventId(CALENDAR_ID
                , EVENT_ID, new CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO>()
                {
                    @Override
                    public void onReceiveResult(@NonNull FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO) throws RemoteException
                    {
                        requireActivity().runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if (foodCriteriaLocationInfoDTO == null)
                                {
                                    //기준 정보가 지정되어 있지 않으면, 지정한 장소/주소를 기준으로 하도록 설정해준다
                                    foodCriteriaLocationInfoViewModel.insertByEventId(CALENDAR_ID
                                            , EVENT_ID
                                            , FoodCriteriaLocationInfoDTO.TYPE_SELECTED_LOCATION, null, new CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO>()
                                            {
                                                @Override
                                                public void onReceiveResult(@NonNull FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO) throws RemoteException
                                                {
                                                    requireActivity().runOnUiThread(new Runnable()
                                                    {
                                                        @Override
                                                        public void run()
                                                        {
                                                            setInitFragments();
                                                        }
                                                    });

                                                }
                                            });
                                } else
                                {
                                    setInitFragments();
                                }
                            }
                        });
                    }
                });

    }

    private void setInitFragments()
    {
        Bundle fragmentBundle = new Bundle();
        fragmentBundle.putInt(CalendarContract.Instances.CALENDAR_ID, CALENDAR_ID);
        fragmentBundle.putLong(CalendarContract.Instances._ID, INSTANCE_ID);
        fragmentBundle.putLong(CalendarContract.Instances.EVENT_ID, EVENT_ID);

        FoodsMainFragment foodsMainFragment = new FoodsMainFragment(this, bottomSheetController);
        foodsMainFragment.setArguments(fragmentBundle);

        SearchRestaurantFragment searchRestaurantFragment = new SearchRestaurantFragment(bottomSheetController);
        FoodsSettingsFragment foodsSettingsFragment = new FoodsSettingsFragment(bottomSheetController);
        FavoritesMainFragment favoritesMainFragment = new FavoritesMainFragment(bottomSheetController);

        getChildFragmentManager().beginTransaction()
                .add(binding.fragmentContainer.getId(), foodsMainFragment, FoodsMainFragment.TAG)
                .add(binding.fragmentContainer.getId(), searchRestaurantFragment, SearchRestaurantFragment.TAG)
                .add(binding.fragmentContainer.getId(), foodsSettingsFragment, FoodsSettingsFragment.TAG)
                .add(binding.fragmentContainer.getId(), favoritesMainFragment, FavoritesMainFragment.TAG)
                .hide(foodsSettingsFragment)
                .hide(searchRestaurantFragment)
                .hide(favoritesMainFragment)
                .commit();

        currentShowingFragment = foodsMainFragment;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        networkStatus.unregisterNetworkCallback();
        FavoriteRestaurantCloud.close();
    }

    @Override
    public boolean networkAvailable()
    {
        return networkStatus.networkAvailable();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment newFragment = null;

        switch (item.getItemId())
        {
            case R.id.food_main:
            {
                newFragment = fragmentManager.findFragmentByTag(FoodsMainFragment.TAG);
                break;
            }

            case R.id.food_favorite_restaurant:
            {
                newFragment = fragmentManager.findFragmentByTag(FavoritesMainFragment.TAG);
                break;
            }

            case R.id.food_search:
            {
                newFragment = fragmentManager.findFragmentByTag(SearchRestaurantFragment.TAG);
                break;
            }

            case R.id.food_settings:
            {
                newFragment = fragmentManager.findFragmentByTag(FoodsSettingsFragment.TAG);
                break;
            }
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(currentShowingFragment).show(newFragment).setPrimaryNavigationFragment(newFragment)
                .commitNow();

        currentShowingFragment = newFragment;
        return true;
    }

    @Override
    public void addOnBackPressedCallback()
    {
        //bottomsheet가 확장될때 호출되고, 현재 표시중인 프래그먼트의 onbackpressed를 활성화한다.
        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment fragment = fragmentManager.getPrimaryNavigationFragment();

        if (fragment instanceof FoodsMainFragment)
        {
            ((FoodsMainFragment) fragment).addOnBackPressedCallback();
        } else if (fragment instanceof FavoritesMainFragment)
        {
            ((FavoritesMainFragment) fragment).addOnBackPressedCallback();
        } else if (fragment instanceof SearchRestaurantFragment)
        {
            ((SearchRestaurantFragment) fragment).addOnBackPressedCallback();
        } else if (fragment instanceof FoodsSettingsFragment)
        {
            ((FoodsSettingsFragment) fragment).addOnBackPressedCallback();
        }
    }

    @Override
    public void removeOnBackPressedCallback()
    {
        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment fragment = fragmentManager.getPrimaryNavigationFragment();

        if (fragment instanceof FoodsMainFragment)
        {
            ((FoodsMainFragment) fragment).removeOnBackPressedCallback();
        } else if (fragment instanceof FavoritesMainFragment)
        {
            ((FavoritesMainFragment) fragment).removeOnBackPressedCallback();
        } else if (fragment instanceof SearchRestaurantFragment)
        {
            ((SearchRestaurantFragment) fragment).removeOnBackPressedCallback();
        } else if (fragment instanceof FoodsSettingsFragment)
        {
            ((FoodsSettingsFragment) fragment).removeOnBackPressedCallback();
        }
    }
}