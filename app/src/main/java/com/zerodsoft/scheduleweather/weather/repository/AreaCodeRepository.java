package com.zerodsoft.scheduleweather.weather.repository;

import android.content.Context;
import android.service.carrier.CarrierMessagingService;

import androidx.lifecycle.LiveData;

import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dao.WeatherAreaCodeDAO;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.utility.LonLat;
import com.zerodsoft.scheduleweather.weather.interfaces.AreaCodeQuery;

import java.util.List;

import lombok.SneakyThrows;

public class AreaCodeRepository implements AreaCodeQuery {
	/*
	서버로 부터 응답이 10초 이상 없는 경우 업데이트 취소 후 업데이트 실패 안내 메시지 표시
	 */
	private WeatherAreaCodeDAO weatherAreaCodeDAO;

	public AreaCodeRepository(Context context) {
		weatherAreaCodeDAO = AppDb.getInstance(context).weatherAreaCodeDAO();
	}

	@Override
	public void getAreaCodes(LonLat lonLat, CarrierMessagingService.ResultCallback<List<WeatherAreaCodeDTO>> callback) {
		new Thread(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				List<WeatherAreaCodeDTO> list = weatherAreaCodeDAO.getAreaCodes(lonLat.getLatitude(), lonLat.getLongitude());
				callback.onReceiveResult(list);
			}
		}).start();
	}

}
