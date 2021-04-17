package com.zerodsoft.scheduleweather.event.places.fragment;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.Marker;
import com.zerodsoft.scheduleweather.kakaomap.fragment.main.KakaoMapFragment;
import com.zerodsoft.scheduleweather.navermap.NaverMapFragment;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class SelectedLocationMapFragmentNaver extends NaverMapFragment
{
    public static final String TAG = "SelectedLocationMapFragmentNaver";
    private final LocationDTO selectedLocation;

    public SelectedLocationMapFragmentNaver(LocationDTO selectedLocation)
    {
        this.selectedLocation = selectedLocation;
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

        mapFragment.getMapView().setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                return true;
            }
        });

        binding.naverMapHeaderBar.getRoot().setVisibility(View.GONE);
        binding.naverMapButtonsLayout.gpsButton.setVisibility(View.GONE);
        binding.naverMapButtonsLayout.currentAddress.setVisibility(View.GONE);
        binding.naverMapButtonsLayout.buildingButton.setVisibility(View.GONE);

        RelativeLayout.LayoutParams mapButtonsLayout = (RelativeLayout.LayoutParams) binding.naverMapButtonsLayout.getRoot().getLayoutParams();
        final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, getResources().getDisplayMetrics());
        mapButtonsLayout.rightMargin = margin;
        mapButtonsLayout.bottomMargin = margin;

        binding.naverMapButtonsLayout.getRoot().setLayoutParams(mapButtonsLayout);

        RelativeLayout.LayoutParams zoomInLayoutParams = (RelativeLayout.LayoutParams) binding.naverMapButtonsLayout.zoomInButton.getLayoutParams();
        RelativeLayout.LayoutParams zoomOutLayoutParams = (RelativeLayout.LayoutParams) binding.naverMapButtonsLayout.zoomOutButton.getLayoutParams();

        final int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, getResources().getDisplayMetrics());
        final int bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, getResources().getDisplayMetrics());
        zoomInLayoutParams.width = size;
        zoomInLayoutParams.height = size;
        zoomOutLayoutParams.width = size;
        zoomOutLayoutParams.height = size;
        zoomOutLayoutParams.bottomMargin = bottomMargin;

        binding.naverMapButtonsLayout.zoomInButton.setLayoutParams(zoomInLayoutParams);
        binding.naverMapButtonsLayout.zoomOutButton.setLayoutParams(zoomOutLayoutParams);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap)
    {
        super.onMapReady(naverMap);

        Marker marker = new Marker(new LatLng(selectedLocation.getLatitude(), selectedLocation.getLongitude()));
        marker.setMap(naverMap);

        CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(marker.getPosition(), 14);
        naverMap.moveCamera(cameraUpdate);
    }
}
