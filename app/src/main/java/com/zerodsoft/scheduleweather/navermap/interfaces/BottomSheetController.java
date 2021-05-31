package com.zerodsoft.scheduleweather.navermap.interfaces;

import com.zerodsoft.scheduleweather.navermap.BottomSheetType;

import java.io.Serializable;

public interface BottomSheetController extends Serializable {
	void setStateOfBottomSheet(BottomSheetType bottomSheetType, int state);

	int getStateOfBottomSheet(BottomSheetType bottomSheetType);
}