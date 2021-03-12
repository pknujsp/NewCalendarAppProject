package com.zerodsoft.scheduleweather.event.places.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.button.MaterialButton;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.event.places.interfaces.IFragment;
import com.zerodsoft.scheduleweather.kakaomap.fragment.KakaoMapFragment;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;

public class DefaultMapFragment extends KakaoMapFragment
{
    public static final String TAG = "DefaultMapFragment";
    private final LocationDTO selectedLocation;
    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true)
    {
        @Override
        public void handleOnBackPressed()
        {
            getParentFragmentManager().popBackStack();
            onBackPressedCallback.remove();
        }
    };

    public DefaultMapFragment(LocationDTO locationDTO)
    {
        super();
        this.selectedLocation = locationDTO;
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        //  requireActivity().getOnBackPressedDispatcher().addCallback(onBackPressedCallback);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        binding.appbarLayout.setVisibility(View.GONE);
        binding.mapButtonsLayout.getRoot().setVisibility(View.GONE);
        binding.bottomSheet.mapBottomSheetToolbar.removeLocationButton.setVisibility(View.GONE);
        binding.bottomSheet.mapBottomSheetToolbar.selectLocationButton.setVisibility(View.GONE);

        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(selectedLocation.getLatitude(), selectedLocation.getLongitude());

        //지정된 위치 표시
        MapPOIItem customMarker = new MapPOIItem();
        customMarker.setItemName("Custom Marker");
        customMarker.setTag(1);
        customMarker.setMapPoint(mapPoint);
        customMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
        customMarker.setCustomImageResourceId(R.drawable.current_location_icon); // 마커 이미지.
        customMarker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
        customMarker.setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.

        mapView.addPOIItem(customMarker);
        mapView.setMapCenterPointAndZoomLevel(mapPoint, 4, true);
    }

}
