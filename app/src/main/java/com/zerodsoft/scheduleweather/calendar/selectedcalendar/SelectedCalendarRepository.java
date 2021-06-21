package com.zerodsoft.scheduleweather.calendar.selectedcalendar;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dao.SelectedCalendarDAO;
import com.zerodsoft.scheduleweather.room.dto.SelectedCalendarDTO;

import java.util.List;

public class SelectedCalendarRepository {
	private SelectedCalendarDAO selectedCalendarDAO;

	private MutableLiveData<SelectedCalendarDTO> onAddedSelectedCalendarLiveData = new MutableLiveData<>();
	private MutableLiveData<List<SelectedCalendarDTO>> onListSelectedCalendarLiveData = new MutableLiveData<>();
	private MutableLiveData<SelectedCalendarDTO> onDeletedSelectedCalendarLiveData = new MutableLiveData<>();

	public SelectedCalendarRepository(Context context) {
		selectedCalendarDAO = AppDb.getInstance(context).selectedCalendarDAO();
	}

	public MutableLiveData<List<SelectedCalendarDTO>> getOnListSelectedCalendarLiveData() {
		return onListSelectedCalendarLiveData;
	}

	public MutableLiveData<SelectedCalendarDTO> getOnAddedSelectedCalendarLiveData() {
		return onAddedSelectedCalendarLiveData;
	}

	public MutableLiveData<SelectedCalendarDTO> getOnDeletedSelectedCalendarLiveData() {
		return onDeletedSelectedCalendarLiveData;
	}
}
