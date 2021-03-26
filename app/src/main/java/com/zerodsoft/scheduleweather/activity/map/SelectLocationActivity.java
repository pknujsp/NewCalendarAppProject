package com.zerodsoft.scheduleweather.activity.map;

import androidx.activity.OnBackPressedCallback;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;

import com.zerodsoft.scheduleweather.kakaomap.activity.KakaoMapActivity;

import com.zerodsoft.scheduleweather.kakaomap.model.CustomPoiItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import net.daum.mf.map.api.MapPOIItem;

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
        kakaoMapFragment.setPlaceBottomSheetSelectBtnVisibility(View.VISIBLE);
        kakaoMapFragment.setPlaceBottomSheetUnSelectBtnVisibility(View.GONE);
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
        LocationDTO locationDTO = kakaoMapFragment.getSelectedLocationDto(0, 0L);

        // 주소인지 장소인지를 구분한다.
        if (item.getKakaoLocalDocument() instanceof PlaceDocuments)
        {
            location = ((PlaceDocuments) item.getKakaoLocalDocument()).getPlaceName();
        } else
        {
            location = ((AddressResponseDocuments) item.getKakaoLocalDocument()).getAddressName();
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