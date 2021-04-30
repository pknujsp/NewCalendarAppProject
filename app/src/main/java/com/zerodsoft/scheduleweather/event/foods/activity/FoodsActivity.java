package com.zerodsoft.scheduleweather.event.foods.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.ContentValues;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.service.carrier.CarrierMessagingService;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.ActivityFoodsBinding;
import com.zerodsoft.scheduleweather.event.foods.favorite.FavoritesMainFragment;
import com.zerodsoft.scheduleweather.event.foods.favorite.restaurant.FavoriteRestaurantViewModel;
import com.zerodsoft.scheduleweather.event.foods.main.fragment.FoodsHomeFragment;
import com.zerodsoft.scheduleweather.event.foods.search.search.fragment.SearchRestaurantFragment;
import com.zerodsoft.scheduleweather.event.foods.settings.FoodsSettingsFragment;
import com.zerodsoft.scheduleweather.event.foods.share.FavoriteRestaurantCloud;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationInfoViewModel;
import com.zerodsoft.scheduleweather.navermap.interfaces.INetwork;
import com.zerodsoft.scheduleweather.room.dto.FavoriteRestaurantDTO;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationInfoDTO;
import com.zerodsoft.scheduleweather.utility.NetworkStatus;

import java.util.List;

public class FoodsActivity extends AppCompatActivity implements INetwork, BottomNavigationView.OnNavigationItemSelectedListener
{
    private ActivityFoodsBinding binding;
    private FoodCriteriaLocationInfoViewModel foodCriteriaLocationInfoViewModel;
    private FavoriteRestaurantViewModel favoriteRestaurantViewModel;
    private NetworkStatus networkStatus;
    private Fragment currentShowingFragment;
    private final ContentValues INSTANCE_VALUES = new ContentValues();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_foods);

        Bundle bundle = getIntent().getExtras();

        INSTANCE_VALUES.put(CalendarContract.Instances.CALENDAR_ID, bundle.getInt(CalendarContract.Instances.CALENDAR_ID));
        INSTANCE_VALUES.put(CalendarContract.Instances._ID, bundle.getLong(CalendarContract.Instances._ID));
        INSTANCE_VALUES.put(CalendarContract.Instances.EVENT_ID, bundle.getLong(CalendarContract.Instances.EVENT_ID));

        networkStatus = new NetworkStatus(getApplicationContext(), new ConnectivityManager.NetworkCallback()
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
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        finish();
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
        foodCriteriaLocationInfoViewModel.selectByEventId(INSTANCE_VALUES.getAsInteger(CalendarContract.Instances.CALENDAR_ID)
                , INSTANCE_VALUES.getAsLong(CalendarContract.Instances.EVENT_ID), new CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO>()
                {
                    @Override
                    public void onReceiveResult(@NonNull FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO) throws RemoteException
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if (foodCriteriaLocationInfoDTO == null)
                                {
                                    //기준 정보가 지정되어 있지 않으면, 지정한 장소/주소를 기준으로 하도록 설정해준다
                                    foodCriteriaLocationInfoViewModel.insertByEventId(INSTANCE_VALUES.getAsInteger(CalendarContract.Instances.CALENDAR_ID)
                                            , INSTANCE_VALUES.getAsLong(CalendarContract.Instances.EVENT_ID)
                                            , FoodCriteriaLocationInfoDTO.TYPE_SELECTED_LOCATION, null, new CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO>()
                                            {
                                                @Override
                                                public void onReceiveResult(@NonNull FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO) throws RemoteException
                                                {
                                                    runOnUiThread(new Runnable()
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
        fragmentBundle.putInt(CalendarContract.Instances.CALENDAR_ID, INSTANCE_VALUES.getAsInteger(CalendarContract.Instances.CALENDAR_ID));
        fragmentBundle.putLong(CalendarContract.Instances._ID, INSTANCE_VALUES.getAsLong(CalendarContract.Instances._ID));
        fragmentBundle.putLong(CalendarContract.Instances.EVENT_ID, INSTANCE_VALUES.getAsLong(CalendarContract.Instances.EVENT_ID));

        FoodsHomeFragment foodsHomeFragment = new FoodsHomeFragment(FoodsActivity.this::networkAvailable, null, null);
        foodsHomeFragment.setArguments(fragmentBundle);

        SearchRestaurantFragment searchRestaurantFragment = new SearchRestaurantFragment(null);
        FoodsSettingsFragment foodsSettingsFragment = new FoodsSettingsFragment(null);
        FavoritesMainFragment favoritesMainFragment = new FavoritesMainFragment(null);

        getSupportFragmentManager().beginTransaction()
                .add(binding.fragmentContainer.getId(), foodsHomeFragment, FoodsHomeFragment.TAG)
                .add(binding.fragmentContainer.getId(), searchRestaurantFragment, SearchRestaurantFragment.TAG)
                .add(binding.fragmentContainer.getId(), foodsSettingsFragment, FoodsSettingsFragment.TAG)
                .add(binding.fragmentContainer.getId(), favoritesMainFragment, FavoritesMainFragment.TAG)
                .hide(foodsSettingsFragment)
                .hide(searchRestaurantFragment)
                .hide(favoritesMainFragment)
                .commit();

        currentShowingFragment = foodsHomeFragment;
    }

    @Override
    protected void onDestroy()
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
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(currentShowingFragment);
        Fragment newFragment = null;

        switch (item.getItemId())
        {
            case R.id.food_main:
            {
                newFragment = fragmentManager.findFragmentByTag(FoodsHomeFragment.TAG);
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
        // removeOnBackPressedOfPreviousFragment(currentShowingFragment);
        // addOnBackPressedOfNewFragment(newFragment);

        fragmentTransaction.show(newFragment)
                .commitNow();

        currentShowingFragment = newFragment;
        return true;
    }

}