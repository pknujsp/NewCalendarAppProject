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

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.ActivityFoodsBinding;
import com.zerodsoft.scheduleweather.event.foods.fragment.FoodCategoryTabFragment;
import com.zerodsoft.scheduleweather.event.foods.fragment.FoodsMainFragment;
import com.zerodsoft.scheduleweather.event.foods.interfaces.FragmentChanger;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationInfoViewModel;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.INetwork;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationInfoDTO;
import com.zerodsoft.scheduleweather.utility.NetworkStatus;

public class FoodsActivity extends AppCompatActivity implements INetwork, FragmentChanger
{
    private ActivityFoodsBinding binding;
    private FoodCriteriaLocationInfoViewModel foodCriteriaLocationInfoViewModel;
    private NetworkStatus networkStatus;

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
                                    FoodsMainFragment foodsMainFragment = new FoodsMainFragment(FoodsActivity.this);

                                    Bundle fragmentBundle = new Bundle();
                                    fragmentBundle.putInt(CalendarContract.Instances.CALENDAR_ID, calendarId);
                                    fragmentBundle.putLong(CalendarContract.Instances._ID, instanceId);
                                    fragmentBundle.putLong(CalendarContract.Instances.EVENT_ID, eventId);

                                    foodsMainFragment.setArguments(fragmentBundle);
                                    getSupportFragmentManager().beginTransaction().add(binding.fragmentContainer.getId(), foodsMainFragment, FoodsMainFragment.TAG).commit();
                                }
                            });
                        } else
                        {
                            FoodsMainFragment foodsMainFragment = new FoodsMainFragment(FoodsActivity.this);
                            Bundle fragmentBundle = new Bundle();
                            fragmentBundle.putInt(CalendarContract.Instances.CALENDAR_ID, calendarId);
                            fragmentBundle.putLong(CalendarContract.Instances._ID, instanceId);
                            fragmentBundle.putLong(CalendarContract.Instances.EVENT_ID, eventId);

                            foodsMainFragment.setArguments(fragmentBundle);
                            getSupportFragmentManager().beginTransaction().add(binding.fragmentContainer.getId(), foodsMainFragment, FoodsMainFragment.TAG).commit();
                        }
                    }
                });
            }
        });

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
    public void changeFragment(Fragment fragment, String tag)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (tag.equals(FoodsMainFragment.TAG))
        {
            fragmentManager.popBackStack();
            Fragment foodsMainFragment = fragmentManager.findFragmentByTag(FoodsMainFragment.TAG);
            ((FoodsMainFragment) foodsMainFragment).addCallback();
        } else if (tag.equals(FoodCategoryTabFragment.TAG))
        {
            Fragment foodsMainFragment = fragmentManager.findFragmentByTag(FoodsMainFragment.TAG);
            fragmentTransaction.add(binding.fragmentContainer.getId(), fragment, tag).hide(foodsMainFragment).addToBackStack(tag).commit();
        }
    }
}