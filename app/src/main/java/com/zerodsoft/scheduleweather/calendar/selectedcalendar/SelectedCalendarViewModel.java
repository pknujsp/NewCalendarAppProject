package com.zerodsoft.scheduleweather.calendar.selectedcalendar;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.room.dto.SelectedCalendarDTO;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.annotation.Nonnull;

public class SelectedCalendarViewModel extends AndroidViewModel implements SelectedCalendarQuery {
	private SelectedCalendarRepository repository;
	private MutableLiveData<SelectedCalendarDTO> onAddedSelectedCalendarLiveData;
	private MutableLiveData<List<SelectedCalendarDTO>> onListSelectedCalendarLiveData;
	private MutableLiveData<List<SelectedCalendarDTO>> onDeletedSelectedCalendarLiveData;

	public SelectedCalendarViewModel(@NonNull @NotNull Application application) {
		super(application);
		repository = new SelectedCalendarRepository(application.getApplicationContext());
		onAddedSelectedCalendarLiveData = repository.getOnAddedSelectedCalendarLiveData();
		onListSelectedCalendarLiveData = repository.getOnListSelectedCalendarLiveData();
		onDeletedSelectedCalendarLiveData = repository.getOnDeletedSelectedCalendarLiveData();
	}

	public LiveData<SelectedCalendarDTO> getOnAddedSelectedCalendarLiveData() {
		return onAddedSelectedCalendarLiveData;
	}

	public LiveData<List<SelectedCalendarDTO>> getOnListSelectedCalendarLiveData() {
		return onListSelectedCalendarLiveData;
	}

	public LiveData<List<SelectedCalendarDTO>> getOnDeletedSelectedCalendarLiveData() {
		return onDeletedSelectedCalendarLiveData;
	}


	@Override
	public void add(SelectedCalendarDTO selectedCalendarDTO) {
		repository.add(selectedCalendarDTO);
	}

	@Override
	public void getSelectedCalendarList() {
		repository.getSelectedCalendarList();
	}

	@Override
	public void delete(Integer calendarId) {
		repository.delete(calendarId);
	}

	@Override
	public void delete(@Nonnull DbQueryCallback<Boolean> callback, Integer... calendarId) {
		repository.delete(callback, calendarId);
	}
}
