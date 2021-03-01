package com.zerodsoft.scheduleweather.event.common;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.view.Menu;
import android.view.View;

import com.zerodsoft.scheduleweather.event.EventActivity;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
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
        kakaoMapFragment.binding.bottomSheet.mapBottomSheetToolbar.removeLocationButton.setVisibility(View.GONE);
        kakaoMapFragment.setToolbarMenuCallback(toolbarMenuCallback);

        onBackPressedCallback = new OnBackPressedCallback(true)
        {
            @Override
            public void handleOnBackPressed()
            {
                setResult(RESULT_CANCELED);
                finish();
                onBackPressedCallback.remove();
            }
        };
        getOnBackPressedDispatcher().addCallback(onBackPressedCallback);

        viewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        Intent intent = getIntent();
        savedLocation = intent.getStringExtra("location");
        ownerAccount = intent.getStringExtra("ownerAccount");
        calendarId = intent.getIntExtra("calendarId", 0);
        eventId = intent.getLongExtra("eventId", 0);

        // 검색 결과가 바로 나타난다.
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
                if (aBoolean)
                {
                    getIntent().putExtra("selectedLocationName", (location.getAddressName() == null ? location.getPlaceName() : location.getAddressName()) + " 지정완료");
                    setResult(EventActivity.RESULT_SELECTED_LOCATION, getIntent());
                    finish();
                    onBackPressedCallback.remove();
                } else
                {

                }
            }
        });

    }

    @Override
    public void onRemovedLocation()
    {
    }
}