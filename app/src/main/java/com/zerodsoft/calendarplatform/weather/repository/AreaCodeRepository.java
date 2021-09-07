package com.zerodsoft.calendarplatform.weather.repository;

import android.content.Context;

import com.zerodsoft.calendarplatform.common.interfaces.DbQueryCallback;
import com.zerodsoft.calendarplatform.room.AppDb;
import com.zerodsoft.calendarplatform.room.dao.WeatherAreaCodeDAO;
import com.zerodsoft.calendarplatform.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.calendarplatform.weather.interfaces.AreaCodeQuery;

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
	public void getAreaCodes(double latitude, double longitude, DbQueryCallback<List<WeatherAreaCodeDTO>> callback) {
		new Thread(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				List<WeatherAreaCodeDTO> list = weatherAreaCodeDAO.getAreaCodes(latitude, longitude);
				if (list == null) {
					callback.onResultNoData();
				} else {
					callback.onResultSuccessful(list);
				}
			}
		}).start();
	}

	@Override
	public void getCodeOfProximateArea(double latitude, double longitude, DbQueryCallback<WeatherAreaCodeDTO> callback) {
		
	}

}
