package com.zerodsoft.scheduleweather.weather.viewmodel;

import android.app.Application;
import android.content.Context;
import android.service.carrier.CarrierMessagingService;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.weather.interfaces.AreaCodeQuery;
import com.zerodsoft.scheduleweather.weather.repository.AreaCodeRepository;
import com.zerodsoft.scheduleweather.utility.LonLat;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AreaCodeViewModel extends AndroidViewModel implements AreaCodeQuery {
	private AreaCodeRepository areaCodeRepository;

	public AreaCodeViewModel(@NonNull @NotNull Application application) {
		super(application);
		this.areaCodeRepository = new AreaCodeRepository(application.getApplicationContext());
	}

	@Override
	public void getAreaCodes(LonLat lonLat, CarrierMessagingService.ResultCallback<List<WeatherAreaCodeDTO>> callback) {
		areaCodeRepository.getAreaCodes(lonLat, callback);
	}
}
