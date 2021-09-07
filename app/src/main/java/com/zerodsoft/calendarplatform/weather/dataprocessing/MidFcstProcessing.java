package com.zerodsoft.calendarplatform.weather.dataprocessing;

import android.content.Context;

import com.google.gson.Gson;
import com.zerodsoft.calendarplatform.common.classes.JsonDownloader;
import com.zerodsoft.calendarplatform.common.interfaces.DbQueryCallback;
import com.zerodsoft.calendarplatform.retrofit.paremeters.MidLandFcstParameter;
import com.zerodsoft.calendarplatform.retrofit.paremeters.MidTaParameter;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.commons.Header;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.weather.midlandfcstresponse.MidLandFcstRoot;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.weather.midtaresponse.MidTaRoot;
import com.zerodsoft.calendarplatform.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.calendarplatform.room.dto.WeatherDataDTO;
import com.zerodsoft.calendarplatform.utility.ClockUtil;
import com.zerodsoft.calendarplatform.weather.common.WeatherDataCallback;
import com.zerodsoft.calendarplatform.weather.common.WeatherDataHeaderChecker;
import com.zerodsoft.calendarplatform.weather.mid.MidFcstResult;
import com.zerodsoft.calendarplatform.weather.mid.MidFcstRoot;
import com.zerodsoft.calendarplatform.weather.repository.WeatherDataDownloader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MidFcstProcessing extends WeatherDataProcessing<MidFcstResult> {
	private WeatherDataDownloader weatherDataDownloader = WeatherDataDownloader.getInstance();
	private WeatherAreaCodeDTO weatherAreaCode;

	public MidFcstProcessing(Context context, String LATITUDE, String LONGITUDE, WeatherAreaCodeDTO weatherAreaCodeDTO) {
		super(context, LATITUDE, LONGITUDE);
		this.weatherAreaCode = weatherAreaCodeDTO;
	}

	@Override
	public void getWeatherData(WeatherDataCallback<MidFcstResult> weatherDataCallback) {
		weatherDbRepository.getWeatherData(LATITUDE, LONGITUDE, WeatherDataDTO.MID_LAND_FCST,
				new DbQueryCallback<WeatherDataDTO>() {
					@Override
					public void onResultSuccessful(WeatherDataDTO midLandFcstResultDto) {
						weatherDbRepository.getWeatherData(LATITUDE, LONGITUDE, WeatherDataDTO.MID_TA,
								new DbQueryCallback<WeatherDataDTO>() {
									@Override
									public void onResultSuccessful(WeatherDataDTO midTaResultDto) {
										Gson gson = new Gson();
										MidLandFcstRoot midLandFcstRoot = gson.fromJson(midLandFcstResultDto.getJson(), MidLandFcstRoot.class);
										MidTaRoot midTaRoot = gson.fromJson(midTaResultDto.getJson(), MidTaRoot.class);

										Calendar calendar = Calendar.getInstance(ClockUtil.TIME_ZONE);
										calendar.setTimeInMillis(Long.parseLong(midTaResultDto.getBaseDateTime()));

										MidFcstResult midFcstResult = new MidFcstResult();
										midFcstResult.setMidFcstDataList(midLandFcstRoot.getResponse().getBody().getItems()
												, midTaRoot.getResponse().getBody().getItems(), new Date(Long.parseLong(midTaResultDto.getDownloadedDate())),
												calendar.getTime());

										weatherDataCallback.isSuccessful(midFcstResult);
									}

									@Override
									public void onResultNoData() {

									}
								});
					}

					@Override
					public void onResultNoData() {
						refresh(weatherDataCallback);
					}
				});

	}

	@Override
	public void refresh(WeatherDataCallback<MidFcstResult> weatherDataCallback) {
		MidLandFcstParameter midLandFcstParameter = new MidLandFcstParameter();
		MidTaParameter midTaParameter = new MidTaParameter();

		midLandFcstParameter.setNumOfRows("300").setPageNo("1").setRegId(weatherAreaCode.getMidLandFcstCode());
		midTaParameter.setNumOfRows("300").setPageNo("1").setRegId(weatherAreaCode.getMidTaCode());

		final Calendar calendar = Calendar.getInstance(ClockUtil.TIME_ZONE);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		String tmFc = null;

		if (hour >= 18 && minute >= 1) {
			calendar.set(Calendar.HOUR_OF_DAY, 18);
			tmFc = ClockUtil.yyyyMMdd.format(calendar.getTime()) + "1800";
		} else if (hour >= 6 && minute >= 1) {
			calendar.set(Calendar.HOUR_OF_DAY, 6);
			tmFc = ClockUtil.yyyyMMdd.format(calendar.getTime()) + "0600";
		} else {
			calendar.add(Calendar.DAY_OF_YEAR, -1);
			calendar.set(Calendar.HOUR_OF_DAY, 18);
			tmFc = ClockUtil.yyyyMMdd.format(calendar.getTime()) + "1800";
		}

		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		final String finalTmFc = tmFc;

		midLandFcstParameter.setTmFc(finalTmFc);
		midTaParameter.setTmFc(finalTmFc);

		weatherDataDownloader.getMidFcstData(midLandFcstParameter, midTaParameter, new JsonDownloader<MidFcstRoot>() {
			@Override
			public void onResponseSuccessful(MidFcstRoot midFcstRoot) {
				Gson gson = new Gson();
				MidLandFcstRoot midLandFcstRoot = gson.fromJson(midFcstRoot.getMidLandFcst().toString(), MidLandFcstRoot.class);
				MidTaRoot midTaRoot = gson.fromJson(midFcstRoot.getMidTa().toString(), MidTaRoot.class);

				Header[] headers = {midLandFcstRoot.getResponse().getHeader(), midTaRoot.getResponse().getHeader()};
				List<String> exceptionMsgList = new ArrayList<>();
				for (Header header : headers) {
					new WeatherDataHeaderChecker() {
						@Override
						public void isSuccessful() {

						}

						@Override
						public void isFailure(Exception e) {
							exceptionMsgList.add(e.getMessage());
						}
					}.processResult(header);
				}

				if (exceptionMsgList.isEmpty()) {
					Calendar downloadedCalendar = Calendar.getInstance(ClockUtil.TIME_ZONE);

					WeatherDataDTO midLandFcstWeatherDataDTO = new WeatherDataDTO();
					midLandFcstWeatherDataDTO.setLatitude(LATITUDE);
					midLandFcstWeatherDataDTO.setLongitude(LONGITUDE);
					midLandFcstWeatherDataDTO.setDataType(WeatherDataDTO.MID_LAND_FCST);
					midLandFcstWeatherDataDTO.setJson(midFcstRoot.getMidLandFcst().toString());
					midLandFcstWeatherDataDTO.setDownloadedDate(String.valueOf(downloadedCalendar.getTimeInMillis()));
					midLandFcstWeatherDataDTO.setBaseDateTime(String.valueOf(calendar.getTimeInMillis()));

					WeatherDataDTO midTaWeatherDataDTO = new WeatherDataDTO();
					midTaWeatherDataDTO.setLatitude(LATITUDE);
					midTaWeatherDataDTO.setLongitude(LONGITUDE);
					midTaWeatherDataDTO.setDataType(WeatherDataDTO.MID_TA);
					midTaWeatherDataDTO.setJson(midFcstRoot.getMidTa().toString());
					midTaWeatherDataDTO.setDownloadedDate(String.valueOf(downloadedCalendar.getTimeInMillis()));
					midTaWeatherDataDTO.setBaseDateTime(String.valueOf(calendar.getTimeInMillis()));

					weatherDbRepository.contains(LATITUDE, LONGITUDE, WeatherDataDTO.MID_LAND_FCST,
							new DbQueryCallback<Boolean>() {
								@Override
								public void onResultSuccessful(Boolean isContains) {
									if (isContains) {
										weatherDbRepository.update(LATITUDE, LONGITUDE, WeatherDataDTO.MID_LAND_FCST
												, midLandFcstWeatherDataDTO.getJson(), midLandFcstWeatherDataDTO.getDownloadedDate(),
												midLandFcstWeatherDataDTO.getBaseDateTime(),
												new DbQueryCallback<Boolean>() {
													@Override
													public void onResultSuccessful(Boolean result) {

													}

													@Override
													public void onResultNoData() {

													}
												});
									} else {
										weatherDbRepository.insert(midLandFcstWeatherDataDTO, new DbQueryCallback<WeatherDataDTO>() {
											@Override
											public void onResultSuccessful(WeatherDataDTO result) {

											}

											@Override
											public void onResultNoData() {

											}
										});
									}
								}

								@Override
								public void onResultNoData() {

								}
							});

					weatherDbRepository.contains(LATITUDE, LONGITUDE, WeatherDataDTO.MID_TA,
							new DbQueryCallback<Boolean>() {
								@Override
								public void onResultSuccessful(Boolean isContains) {
									if (isContains) {
										weatherDbRepository.update(LATITUDE, LONGITUDE, WeatherDataDTO.MID_TA
												, midTaWeatherDataDTO.getJson(), midTaWeatherDataDTO.getDownloadedDate(), midTaWeatherDataDTO.getBaseDateTime(),
												new DbQueryCallback<Boolean>() {
													@Override
													public void onResultSuccessful(Boolean result) {

													}

													@Override
													public void onResultNoData() {

													}
												});
									} else {
										weatherDbRepository.insert(midTaWeatherDataDTO, new DbQueryCallback<WeatherDataDTO>() {
											@Override
											public void onResultSuccessful(WeatherDataDTO result) {

											}

											@Override
											public void onResultNoData() {

											}
										});
									}
								}

								@Override
								public void onResultNoData() {

								}
							});

					MidFcstResult midFcstResult = new MidFcstResult();
					midFcstResult.setMidFcstDataList(midLandFcstRoot.getResponse().getBody().getItems()
							, midTaRoot.getResponse().getBody().getItems(), downloadedCalendar.getTime(), calendar.getTime());

					weatherDataCallback.isSuccessful(midFcstResult);
				} else {
					weatherDataCallback.isFailure(new Exception(exceptionMsgList.toString()));
				}
			}

			@Override
			public void onResponseFailed(Exception e) {
				weatherDataCallback.isFailure(e);
			}
		});
	}
}