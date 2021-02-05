package com.zerodsoft.scheduleweather.event.common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.view.Menu;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.editevent.activity.EditEventActivity;
import com.zerodsoft.scheduleweather.activity.map.fragment.map.MapFragment;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.kakaomap.KakaoMapActivity;
import com.zerodsoft.scheduleweather.kakaomap.fragment.KakaoMapFragment;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import java.util.Map;

public class MLocActivity extends KakaoMapActivity
{
    private String savedLocation;
    private String ownerAccount;
    private Integer calendarId;
    private Long eventId;
    private LocationViewModel viewModel;

    public static final int REQUEST_SELECT_LOCATION = 3000;
    public static final int RESULT_SELECTED_LOCATION = 3100;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        Intent intent = getIntent();
        savedLocation = intent.getStringExtra("location");
        ownerAccount = intent.getStringExtra("ownerAccount");
        calendarId = intent.getIntExtra("calendarId", 0);
        eventId = intent.getLongExtra("eventId", 0);

        kakaoMapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.kakao_map_fragment);
        kakaoMapFragment.setiBottomSheet(this);
        kakaoMapFragment.setiMapToolbar(this);
        // 검색 결과가 바로 나타난다.
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        searchView.performClick();
        searchView.setQuery(savedLocation, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed()
    {
        // 미지정 - 그대로 이전 액티비티로 복귀한다.
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onSelectLocation()
    {
        // 지정이 완료된 경우 - DB에 등록하고 이벤트 액티비티로 넘어가서 날씨 또는 주변 정보 프래그먼트를 실행한다.

        // 선택된 poiitem의 리스트내 인덱스를 가져온다.
        int poiItemIndex = kakaoMapFragment.selectedPoiItemIndex;
        KakaoMapFragment.CustomPoiItem[] poiItems = (KakaoMapFragment.CustomPoiItem[]) kakaoMapFragment.mapView.getPOIItems();
        // 인덱스로 아이템을 가져온다.
        KakaoMapFragment.CustomPoiItem item = poiItems[poiItemIndex];

        LocationDTO location = new LocationDTO();
        location.setCalendarId(calendarId);
        location.setEventId(eventId);
        location.setOwnerAccount(ownerAccount);

        // 주소인지 장소인지를 구분한다.
        if (item.getPlaceDocument() != null)
        {
            location.setPlaceId(item.getPlaceDocument().getId());
            location.setPlaceName(item.getPlaceDocument().getPlaceName());
            location.setLatitude(item.getPlaceDocument().getY());
            location.setLongitude(item.getPlaceDocument().getX());
        } else if (item.getAddressDocument() != null)
        {
            location.setAddressName(item.getAddressDocument().getAddressName());
            location.setLatitude(item.getAddressDocument().getY());
            location.setLongitude(item.getAddressDocument().getX());
        }

        //선택된 위치를 DB에 등록
        viewModel.addLocation(location, new CarrierMessagingService.ResultCallback<Boolean>()
        {
            @Override
            public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException
            {
                if (aBoolean)
                {
                    setResult(RESULT_SELECTED_LOCATION);
                    finish();
                } else
                {

                }
            }
        });


    }

    @Override
    public void onRemoveLocation()
    {
        super.onRemoveLocation();
    }
}