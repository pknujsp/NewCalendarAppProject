package com.zerodsoft.scheduleweather.weather.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.naver.maps.geometry.LatLng;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.weather.interfaces.AreaCodeQuery;
import com.zerodsoft.scheduleweather.weather.repository.AreaCodeRepository;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AreaCodeViewModel extends AndroidViewModel implements AreaCodeQuery {
	private AreaCodeRepository areaCodeRepository;

	public AreaCodeViewModel(@NonNull @NotNull Application application) {
		super(application);
		this.areaCodeRepository = new AreaCodeRepository(application.getApplicationContext());
	}

	@Override
	public void getAreaCodes(double latitude, double longitude, DbQueryCallback<List<WeatherAreaCodeDTO>> callback) {
		areaCodeRepository.getAreaCodes(latitude, longitude, callback);
	}

	public void getCodeOfProximateArea(double latitude, double longitude, DbQueryCallback<WeatherAreaCodeDTO> callback) {
		areaCodeRepository.getAreaCodes(latitude, longitude, new DbQueryCallback<List<WeatherAreaCodeDTO>>() {
			@Override
			public void onResultSuccessful(List<WeatherAreaCodeDTO> result) {
				LatLng criteriaLatLng = new LatLng(latitude, longitude);
				double minDistance = Double.MAX_VALUE;
				double distance = 0;
				WeatherAreaCodeDTO weatherAreaCode = null;

				for (WeatherAreaCodeDTO weatherAreaCodeDTO : result) {
					LatLng latLng = new LatLng(Double.parseDouble(weatherAreaCodeDTO.getLatitudeSecondsDivide100())
							, Double.parseDouble(weatherAreaCodeDTO.getLongitudeSecondsDivide100()));

					distance = criteriaLatLng.distanceTo(latLng);
					if (distance <= minDistance) {
						minDistance = distance;
						weatherAreaCode = weatherAreaCodeDTO;
					}
				}

				callback.onResultSuccessful(weatherAreaCode);
			}

			@Override
			public void onResultNoData() {

			}
		});
	}
}
