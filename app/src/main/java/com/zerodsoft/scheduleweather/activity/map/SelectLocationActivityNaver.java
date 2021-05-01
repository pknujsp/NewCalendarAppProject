package com.zerodsoft.scheduleweather.activity.map;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;

import androidx.activity.OnBackPressedCallback;

import com.zerodsoft.scheduleweather.navermap.NaverMapActivity;
import com.zerodsoft.scheduleweather.navermap.NaverMapFragment;
import com.zerodsoft.scheduleweather.navermap.PoiItemType;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

public class SelectLocationActivityNaver extends NaverMapActivity
{
    private OnBackPressedCallback onBackPressedCallback;
    private String eventLocation;
    public static final int RESULT_REMOVED_LOCATION = 10;

    public SelectLocationActivityNaver()
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
        naverMapFragment.setPlaceBottomSheetSelectBtnVisibility(View.VISIBLE);
        naverMapFragment.setPlaceBottomSheetUnSelectBtnVisibility(View.GONE);
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

        String location = null;
        LocationDTO locationDTO = naverMapFragment.getSelectedLocationDto(0, 0L);

        KakaoLocalDocument kakaoLocalDocument = (KakaoLocalDocument) naverMapFragment.markerMap.get((PoiItemType) naverMapFragment.locationItemBottomSheetViewPager.getTag(NaverMapFragment.POI_ITEM_TYPE_OF_LOCATION_ITEMS_BOTTOM_SHEET))
                .get(naverMapFragment.selectedPoiItemIndex).getTag();

        // 주소인지 장소인지를 구분한다.
        if (kakaoLocalDocument instanceof PlaceDocuments)
        {
            location = ((PlaceDocuments) kakaoLocalDocument).getPlaceName();
        } else
        {
            location = ((AddressResponseDocuments) kakaoLocalDocument).getAddressName();
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