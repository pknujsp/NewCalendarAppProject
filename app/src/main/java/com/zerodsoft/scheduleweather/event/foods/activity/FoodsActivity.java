package com.zerodsoft.scheduleweather.event.foods.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.widget.Toast;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.ActivityFoodsBinding;
import com.zerodsoft.scheduleweather.event.foods.fragment.FoodsMainFragment;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationInfoViewModel;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.INetwork;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationInfoDTO;
import com.zerodsoft.scheduleweather.utility.NetworkStatus;

public class FoodsActivity extends AppCompatActivity implements INetwork
{
    private ActivityFoodsBinding binding;
    private FoodCriteriaLocationInfoViewModel foodCriteriaLocationInfoViewModel;
    private NetworkStatus networkStatus;


    private int calendarId;
    private long instanceId;
    private long eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_foods);

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
                            foodCriteriaLocationInfoViewModel.insertByEventId(calendarId, eventId, FoodCriteriaLocationInfoDTO.TYPE_SELECTED_LOCATION, null, new CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO>()
                            {
                                @Override
                                public void onReceiveResult(@NonNull FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO) throws RemoteException
                                {
                                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                    FoodsMainFragment foodsMainFragment = new FoodsMainFragment(FoodsActivity.this::networkAvailable);
                                    fragmentTransaction.add(binding.fragmentContainer.getId(), foodsMainFragment, FoodsMainFragment.TAG).commit();
                                }
                            });
                        } else
                        {
                            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            FoodsMainFragment foodsMainFragment = new FoodsMainFragment(FoodsActivity.this::networkAvailable);
                            fragmentTransaction.add(binding.fragmentContainer.getId(), foodsMainFragment, FoodsMainFragment.TAG).commit();
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
}