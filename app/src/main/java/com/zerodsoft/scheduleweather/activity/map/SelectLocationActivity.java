package com.zerodsoft.scheduleweather.activity.map;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;

import android.Manifest;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.CalendarContract;

import com.zerodsoft.scheduleweather.activity.map.util.RequestLocationTimer;
import com.zerodsoft.scheduleweather.kakaomap.activity.KakaoMapActivity;

import com.zerodsoft.scheduleweather.kakaomap.model.CustomPoiItem;
import com.zerodsoft.scheduleweather.kakaomap.viewmodel.AddressViewModel;
import com.zerodsoft.scheduleweather.kakaomap.viewmodel.PlacesViewModel;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import net.daum.mf.map.api.MapPOIItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class SelectLocationActivity extends KakaoMapActivity
{
    private OnBackPressedCallback onBackPressedCallback;
    private String eventLocation;
    public static final int RESULT_REMOVED_LOCATION = 10;

    public SelectLocationActivity()
    {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        eventLocation = getIntent().getStringExtra(CalendarContract.Events.EVENT_LOCATION);
        getIntent().removeExtra(CalendarContract.Events.EVENT_LOCATION);

        onBackPressedCallback = new OnBackPressedCallback(true)
        {
            @Override
            public void handleOnBackPressed()
            {
                // 맵 기본화면에서 뒤로가기하는 경우 취소로 판단
                setResult(RESULT_CANCELED);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        onBackPressedCallback.remove();
    }

    @Override
    public void onSelectedLocation()
    {
        // 선택된 poiitem의 리스트내 인덱스를 가져온다.
        int poiItemIndex = kakaoMapFragment.getSelectedPoiItemIndex();
        MapPOIItem[] poiItems = kakaoMapFragment.mapView.getPOIItems();
        // 인덱스로 아이템을 가져온다.
        CustomPoiItem item = (CustomPoiItem) poiItems[poiItemIndex];

        String location = null;
        LocationDTO locationDTO = new LocationDTO();

        // 주소인지 장소인지를 구분한다.
        if (item.getPlaceDocument() != null)
        {
            location = item.getPlaceDocument().getPlaceName();

            locationDTO.setPlaceId(item.getPlaceDocument().getId());
            locationDTO.setPlaceName(item.getPlaceDocument().getPlaceName());
            locationDTO.setLatitude(item.getPlaceDocument().getY());
            locationDTO.setLongitude(item.getPlaceDocument().getX());
        } else if (item.getAddressDocument() != null)
        {
            location = item.getAddressDocument().getAddressName();

            locationDTO.setAddressName(item.getAddressDocument().getAddressName());
            locationDTO.setLatitude(item.getAddressDocument().getY());
            locationDTO.setLongitude(item.getAddressDocument().getX());
        }

        //선택된 위치를 DB에 등록
        Bundle bundle = new Bundle();
        bundle.putString(CalendarContract.Events.EVENT_LOCATION, location);
        bundle.putParcelable("locationObject", locationDTO);

        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onRemovedLocation()
    {
        setResult(RESULT_REMOVED_LOCATION);
        finish();
    }

}