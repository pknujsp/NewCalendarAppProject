package com.zerodsoft.scheduleweather.navermap.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.zerodsoft.scheduleweather.navermap.interfaces.BottomSheetController;

import org.jetbrains.annotations.NotNull;

public class MapSharedViewModel extends AndroidViewModel {
	private BottomSheetController bottomSheetController;


	public MapSharedViewModel(@NonNull @NotNull Application application) {
		super(application);
	}


	public void setBottomSheetController(BottomSheetController bottomSheetController) {
		this.bottomSheetController = bottomSheetController;
	}

	public BottomSheetController getBottomSheetController() {
		return bottomSheetController;
	}
}
