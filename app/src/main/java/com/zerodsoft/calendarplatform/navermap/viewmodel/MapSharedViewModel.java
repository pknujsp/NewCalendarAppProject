package com.zerodsoft.calendarplatform.navermap.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.zerodsoft.calendarplatform.navermap.places.interfaces.MarkerOnClickListener;
import com.zerodsoft.calendarplatform.navermap.interfaces.BottomSheetController;
import com.zerodsoft.calendarplatform.navermap.interfaces.IMapData;
import com.zerodsoft.calendarplatform.navermap.interfaces.IMapPoint;

import org.jetbrains.annotations.NotNull;

public class MapSharedViewModel extends AndroidViewModel {
	private BottomSheetController bottomSheetController;
	private IMapData iMapData;
	private IMapPoint iMapPoint;
	private MarkerOnClickListener markerOnClickListener;


	public MapSharedViewModel(@NonNull @NotNull Application application) {
		super(application);
	}

	public IMapData getiMapData() {
		return iMapData;
	}

	public void setiMapData(IMapData iMapData) {
		this.iMapData = iMapData;
	}

	public IMapPoint getiMapPoint() {
		return iMapPoint;
	}

	public void setiMapPoint(IMapPoint iMapPoint) {
		this.iMapPoint = iMapPoint;
	}

	public void setBottomSheetController(BottomSheetController bottomSheetController) {
		this.bottomSheetController = bottomSheetController;
	}

	public BottomSheetController getBottomSheetController() {
		return bottomSheetController;
	}

	public void setPoiItemOnClickListener(MarkerOnClickListener markerOnClickListener) {
		this.markerOnClickListener = markerOnClickListener;
	}

	public MarkerOnClickListener getPoiItemOnClickListener() {
		return markerOnClickListener;
	}
}
