package com.zerodsoft.scheduleweather.calendar.selectedcalendar;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dao.SelectedCalendarDAO;
import com.zerodsoft.scheduleweather.room.dto.SelectedCalendarDTO;

import java.util.List;

import javax.annotation.Nonnull;

public class SelectedCalendarRepository implements SelectedCalendarQuery {
	private SelectedCalendarDAO selectedCalendarDAO;

	private MutableLiveData<SelectedCalendarDTO> onAddedSelectedCalendarLiveData = new MutableLiveData<>();
	private MutableLiveData<List<SelectedCalendarDTO>> onListSelectedCalendarLiveData = new MutableLiveData<>();
	private MutableLiveData<List<SelectedCalendarDTO>> onDeletedSelectedCalendarLiveData = new MutableLiveData<>();

	public SelectedCalendarRepository(Context context) {
		selectedCalendarDAO = AppDb.getInstance(context).selectedCalendarDAO();
	}

	public MutableLiveData<List<SelectedCalendarDTO>> getOnListSelectedCalendarLiveData() {
		return onListSelectedCalendarLiveData;
	}

	public MutableLiveData<SelectedCalendarDTO> getOnAddedSelectedCalendarLiveData() {
		return onAddedSelectedCalendarLiveData;
	}

	public MutableLiveData<List<SelectedCalendarDTO>> getOnDeletedSelectedCalendarLiveData() {
		return onDeletedSelectedCalendarLiveData;
	}


	@Override
	public void add(SelectedCalendarDTO selectedCalendarDTO) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				int id = (int) selectedCalendarDAO.add(selectedCalendarDTO);
				onAddedSelectedCalendarLiveData.postValue(selectedCalendarDAO.get(id));
			}
		}).start();
	}

	@Override
	public void getSelectedCalendarList() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				onListSelectedCalendarLiveData.postValue(selectedCalendarDAO.getSelectedCalendarList());
			}
		}).start();
	}

	@Override
	public void delete(Integer calendarId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				selectedCalendarDAO.delete(calendarId);
				onDeletedSelectedCalendarLiveData.postValue(selectedCalendarDAO.getSelectedCalendarList());
			}
		}).start();
	}

	@Override
	public void delete(@Nonnull DbQueryCallback<Boolean> callback, Integer... calendarId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (Integer calendarId : calendarId) {
					selectedCalendarDAO.delete(calendarId);
				}
				callback.processResult(true);
				onDeletedSelectedCalendarLiveData.postValue(selectedCalendarDAO.getSelectedCalendarList());
			}
		}).start();
	}
}
