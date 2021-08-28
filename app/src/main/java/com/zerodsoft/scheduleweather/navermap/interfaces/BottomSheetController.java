package com.zerodsoft.scheduleweather.navermap.interfaces;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.navermap.BottomSheetType;

import java.io.Serializable;
import java.util.List;

public interface BottomSheetController extends Serializable {
	void setStateOfBottomSheet(BottomSheetType bottomSheetType, int state);

	int getStateOfBottomSheet(BottomSheetType bottomSheetType);

	List<BottomSheetBehavior> getBottomSheetBehaviorOfExpanded(BottomSheetBehavior currentBottomSheetBehavior);

	void collapseAllExpandedBottomSheets();

	BottomSheetBehavior getBottomSheetBehavior(BottomSheetType bottomSheetType);
}