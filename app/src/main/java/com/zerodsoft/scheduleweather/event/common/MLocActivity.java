package com.zerodsoft.scheduleweather.event.common;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.view.View;

import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.main.InstanceMainActivity;
import com.zerodsoft.scheduleweather.kakaomap.activity.KakaoMapActivity;
import com.zerodsoft.scheduleweather.kakaomap.callback.ToolbarMenuCallback;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

public class MLocActivity extends KakaoMapActivity
{
    private String savedLocation;
    private String ownerAccount;
    private Integer calendarId;
    private Long eventId;

    private LocationViewModel viewModel;
    private OnBackPressedCallback onBackPressedCallback;
    private final ToolbarMenuCallback toolbarMenuCallback = new ToolbarMenuCallback()
    {
        @Override
        public void onCreateOptionsMenu()
        {
            // iconfy가 false가 되면 search listener실행
            kakaoMapFragment.searchView.setIconified(false);
            kakaoMapFragment.searchView.setQuery(savedLocation, true);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        kakaoMapFragment.setToolbarMenuCallback(toolbarMenuCallback);

        onBackPressedCallback = new OnBackPressedCallback(true)
        {
            @Override
            public void handleOnBackPressed()
            {
                kakaoMapFragment.binding.mapView.removeAllViews();

                setResult(RESULT_CANCELED);
                finish();
                onBackPressedCallback.remove();
            }
        };
        getOnBackPressedDispatcher().addCallback(onBackPressedCallback);

        viewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        Bundle arguments = getIntent().getExtras();
        savedLocation = arguments.getString("location");
        ownerAccount = arguments.getString("ownerAccount");
        calendarId = arguments.getInt("calendarId", 0);
        eventId = arguments.getLong("eventId", 0);

        // 검색 결과가 바로 나타난다.
        kakaoMapFragment.setPlaceBottomSheetSelectBtnVisibility(View.VISIBLE);
        kakaoMapFragment.setPlaceBottomSheetUnSelectBtnVisibility(View.GONE);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public void onSelectedLocation()
    {
        // 지정이 완료된 경우 - DB에 등록하고 이벤트 액티비티로 넘어가서 날씨 또는 주변 정보 프래그먼트를 실행한다.
        LocationDTO location = kakaoMapFragment.getSelectedLocationDto(calendarId, eventId);

        //선택된 위치를 DB에 등록
        viewModel.addLocation(location, new CarrierMessagingService.ResultCallback<Boolean>()
        {
            @Override
            public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (aBoolean)
                        {
                            kakaoMapFragment.binding.mapView.removeAllViews();

                            getIntent().putExtra("selectedLocationName", (location.getAddressName() == null ? location.getPlaceName() : location.getAddressName()) + " 지정완료");
                            setResult(InstanceMainActivity.RESULT_SELECTED_LOCATION, getIntent());
                            finish();
                            onBackPressedCallback.remove();
                        } else
                        {

                        }
                    }
                });

            }
        });

    }

    @Override
    public void onRemovedLocation()
    {
    }
}