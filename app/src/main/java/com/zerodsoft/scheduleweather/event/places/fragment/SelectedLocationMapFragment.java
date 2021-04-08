package com.zerodsoft.scheduleweather.event.places.fragment;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zerodsoft.scheduleweather.kakaomap.fragment.main.KakaoMapFragment;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class SelectedLocationMapFragment extends KakaoMapFragment
{
    public static final String TAG = "SelectedLocationMapFragment";
    private final LocationDTO selectedLocation;

    public SelectedLocationMapFragment(LocationDTO selectedLocation)
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
        mapView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                return true;
            }
        });
        binding.mapHeaderBar.getRoot().setVisibility(View.GONE);
        binding.mapButtonsLayout.gpsButton.setVisibility(View.GONE);
        binding.mapButtonsLayout.currentAddress.setVisibility(View.GONE);

        RelativeLayout.LayoutParams mapButtonsLayout = (RelativeLayout.LayoutParams) binding.mapButtonsLayout.getRoot().getLayoutParams();
        final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, getResources().getDisplayMetrics());
        mapButtonsLayout.rightMargin = margin;
        mapButtonsLayout.bottomMargin = margin;

        binding.mapButtonsLayout.getRoot().setLayoutParams(mapButtonsLayout);

        RelativeLayout.LayoutParams zoomInLayoutParams = (RelativeLayout.LayoutParams) binding.mapButtonsLayout.zoomInButton.getLayoutParams();
        RelativeLayout.LayoutParams zoomOutLayoutParams = (RelativeLayout.LayoutParams) binding.mapButtonsLayout.zoomOutButton.getLayoutParams();

        final int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, getResources().getDisplayMetrics());
        final int bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, getResources().getDisplayMetrics());
        zoomInLayoutParams.width = size;
        zoomInLayoutParams.height = size;
        zoomOutLayoutParams.width = size;
        zoomOutLayoutParams.height = size;
        zoomOutLayoutParams.bottomMargin = bottomMargin;

        binding.mapButtonsLayout.zoomInButton.setLayoutParams(zoomInLayoutParams);
        binding.mapButtonsLayout.zoomOutButton.setLayoutParams(zoomOutLayoutParams);
    }

    @Override
    public void onMapViewInitialized(MapView mapView)
    {
        super.onMapViewInitialized(mapView);

        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(selectedLocation.getLatitude(), selectedLocation.getLongitude());
        MapPOIItem poiItem = new MapPOIItem();
        poiItem.setItemName(selectedLocation.getPlaceName() != null ? selectedLocation.getPlaceName() : selectedLocation.getAddressName());
        poiItem.setMapPoint(mapPoint);
        poiItem.setTag(0);
        poiItem.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(poiItem);
        mapView.setMapCenterPointAndZoomLevel(mapPoint, 4, false);
    }


}
