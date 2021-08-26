package com.zerodsoft.scheduleweather.navermap.places.selectedlocation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.navermap.NaverMapFragment;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

public class SelectedLocationMapFragmentNaver extends NaverMapFragment {
	public static final String TAG = "SelectedLocationMapFragmentNaver";
	private final LocationDTO selectedLocation;
	private final OnMapReadyCallback onMapReadyCallback;

	public SelectedLocationMapFragmentNaver(LocationDTO selectedLocation, OnMapReadyCallback onMapReadyCallback) {
		this.selectedLocation = selectedLocation;
		this.onMapReadyCallback = onMapReadyCallback;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		binding.bottomNavigation.setVisibility(View.GONE);
		loadMap();

		binding.headerLayout.setVisibility(View.GONE);
		binding.naverMapButtonsLayout.gpsButton.setVisibility(View.GONE);
		binding.naverMapButtonsLayout.currentAddress.setVisibility(View.GONE);
		binding.naverMapButtonsLayout.favoriteLocationsButton.setVisibility(View.GONE);
	}

	@Override
	public void onMapReady(@NonNull NaverMap naverMap) {
		super.onMapReady(naverMap);

		mapFragment.getMapView().setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				return true;
			}
		});


		Marker marker = new Marker(new LatLng(Double.parseDouble(selectedLocation.getLatitude()),
				Double.parseDouble(selectedLocation.getLongitude())));
		marker.setMap(naverMap);
		marker.setIcon(OverlayImage.fromResource(R.drawable.current_location_icon));
		marker.setForceShowIcon(true);

		CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(marker.getPosition(), 15);
		naverMap.moveCamera(cameraUpdate);

		if (onMapReadyCallback != null) {
			onMapReadyCallback.onMapReady(naverMap);
		}
	}
}
