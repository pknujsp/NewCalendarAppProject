package com.zerodsoft.scheduleweather.calendarview.common;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.zerodsoft.scheduleweather.calendarview.EventTransactionFragment;

import org.jetbrains.annotations.NotNull;

public class CalendarSharedViewModel extends AndroidViewModel {
	private EventTransactionFragment.OnOpenListBtnListener onOpenListBtnListener;

	public CalendarSharedViewModel(@NonNull @NotNull Application application) {
		super(application);
	}

	public void setOnOpenListBtnListener(EventTransactionFragment.OnOpenListBtnListener onOpenListBtnListener) {
		this.onOpenListBtnListener = onOpenListBtnListener;
	}

	public EventTransactionFragment.OnOpenListBtnListener getOnOpenListBtnListener() {
		return onOpenListBtnListener;
	}
}
