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
import com.zerodsoft.scheduleweather.event.foods.main.fragment.FoodsMainFragment;
import com.zerodsoft.scheduleweather.event.foods.search.search.fragment.SearchRestaurantFragment;
import com.zerodsoft.scheduleweather.event.foods.settings.FoodsSettingsFragment;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationInfoViewModel;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.INetwork;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationInfoDTO;
import com.zerodsoft.scheduleweather.utility.NetworkStatus;

public class FoodsActivity extends AppCompatActivity implements INetwork, BottomNavigationView.OnNavigationItemSelectedListener
{
    private ActivityFoodsBinding binding;
    private FoodCriteriaLocationInfoViewModel foodCriteriaLocationInfoViewModel;
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

        FoodsMainFragment foodsMainFragment = new FoodsMainFragment(FoodsActivity.this::networkAvailable);
        foodsMainFragment.setArguments(fragmentBundle);

        SearchRestaurantFragment searchRestaurantFragment = new SearchRestaurantFragment();
        FoodsSettingsFragment foodsSettingsFragment = new FoodsSettingsFragment();

        getSupportFragmentManager().beginTransaction()
                .add(binding.fragmentContainer.getId(), foodsMainFragment, FoodsMainFragment.TAG)
                .add(binding.fragmentContainer.getId(), searchRestaurantFragment, SearchRestaurantFragment.TAG)
                .add(binding.fragmentContainer.getId(), foodsSettingsFragment, FoodsSettingsFragment.TAG)
                .hide(foodsSettingsFragment)
                .hide(searchRestaurantFragment)
                .commit();

        currentShowingFragment = foodsMainFragment;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        networkStatus.unregisterNetworkCallback();
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
                newFragment = fragmentManager.findFragmentByTag(FoodsMainFragment.TAG);
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

    private void removeOnBackPressedOfPreviousFragment(Fragment previousFragment)
    {
        if (previousFragment instanceof FoodsMainFragment)
        {
            ((FoodsMainFragment) previousFragment).removeOnBackPressedCallback();
        } else if (previousFragment instanceof SearchRestaurantFragment)
        {
            ((SearchRestaurantFragment) previousFragment).removeOnBackPressedCallback();
        } else if (previousFragment instanceof FoodsSettingsFragment)
        {
            ((FoodsSettingsFragment) previousFragment).removeOnBackPressedCallback();
        }
    }

    private void addOnBackPressedOfNewFragment(Fragment newFragment)
    {
        if (newFragment instanceof FoodsMainFragment)
        {
            ((FoodsMainFragment) newFragment).addOnBackPressedCallback();
        } else if (newFragment instanceof SearchRestaurantFragment)
        {
            ((SearchRestaurantFragment) newFragment).addOnBackPressedCallback();
        } else if (newFragment instanceof FoodsSettingsFragment)
        {
            ((FoodsSettingsFragment) newFragment).addOnBackPressedCallback();
        }
    }
}