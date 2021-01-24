package com.zerodsoft.scheduleweather.activity.map;

import androidx.activity.OnBackPressedCallback;

import android.os.Bundle;

import com.zerodsoft.scheduleweather.activity.editevent.activity.EventActivity;
import com.zerodsoft.scheduleweather.kakaomap.KakaoMapActivity;
import com.zerodsoft.scheduleweather.kakaomap.fragment.KakaoMapFragment;
import com.zerodsoft.scheduleweather.activity.map.fragment.map.MapFragment;
import com.zerodsoft.scheduleweather.R;

public class SelectLocationActivity extends KakaoMapActivity
{
    private OnBackPressedCallback onBackPressedCallback;

    public SelectLocationActivity()
    {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

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

        kakaoMapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.kakao_map_fragment);
        kakaoMapFragment.setiBottomSheet(this);
        kakaoMapFragment.setiMapToolbar(this);
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        onBackPressedCallback.remove();
    }

    @Override
    public void onSelectLocation()
    {
        // 선택된 poiitem의 리스트내 인덱스를 가져온다.
        int poiItemIndex = kakaoMapFragment.selectedPoiItemIndex;
        KakaoMapFragment.CustomPoiItem[] poiItems = (KakaoMapFragment.CustomPoiItem[]) kakaoMapFragment.mapView.getPOIItems();
        // 인덱스로 아이템을 가져온다.
        KakaoMapFragment.CustomPoiItem item = poiItems[poiItemIndex];

        String location = null;

        // 주소인지 장소인지를 구분한다.
        if (item.getPlaceDocument() != null)
        {
            location = item.getPlaceDocument().getPlaceName();
        } else if (item.getAddressDocument() != null)
        {
            location = item.getAddressDocument().getAddressName();
        }
        
        //선택된 위치를 DB에 등록


        getIntent().putExtra("location", location);
        setResult(EventActivity.LOCATION_SELECTED, getIntent());
    }

    @Override
    public void onRemoveLocation()
    {
    }
}