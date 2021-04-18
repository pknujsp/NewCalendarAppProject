package com.zerodsoft.scheduleweather.event.foods.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.service.carrier.CarrierMessagingService;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.ActivityFoodsBinding;
import com.zerodsoft.scheduleweather.event.foods.categorylist.FoodCategoryTabFragment;
import com.zerodsoft.scheduleweather.event.foods.categorylist.FoodsCategoryListFragment;
import com.zerodsoft.scheduleweather.event.foods.interfaces.FragmentChanger;
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

    private Integer calendarId;
    private Long instanceId;
    private Long eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_foods);

        Bundle bundle = getIntent().getExtras();

        calendarId = bundle.getInt(CalendarContract.Instances.CALENDAR_ID);
        instanceId = bundle.getLong(CalendarContract.Instances._ID);
        eventId = bundle.getLong(CalendarContract.Instances.EVENT_ID);

        networkStatus = new NetworkStatus(this);

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
        foodCriteriaLocationInfoViewModel.selectByEventId(calendarId, eventId, new CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO>()
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
                            foodCriteriaLocationInfoViewModel.insertByEventId(calendarId, eventId, FoodCriteriaLocationInfoDTO.TYPE_SELECTED_LOCATION, null, new CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO>()
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
        fragmentBundle.putInt(CalendarContract.Instances.CALENDAR_ID, calendarId);
        fragmentBundle.putLong(CalendarContract.Instances._ID, instanceId);
        fragmentBundle.putLong(CalendarContract.Instances.EVENT_ID, eventId);

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
        return networkStatus.networkAvailable(this);
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