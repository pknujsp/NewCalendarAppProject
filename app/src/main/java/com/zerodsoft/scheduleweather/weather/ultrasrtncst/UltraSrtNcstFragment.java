package com.zerodsoft.scheduleweather.weather.ultrasrtncst;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.UltraSrtNcstFragmentBinding;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.room.dto.WeatherDataDTO;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.weather.common.WeatherDataCallback;
import com.zerodsoft.scheduleweather.weather.dataprocessing.UltraSrtNcstProcessing;
import com.zerodsoft.scheduleweather.weather.dataprocessing.WeatherDataConverter;
import com.zerodsoft.scheduleweather.weather.sunsetrise.SunSetRiseData;
import com.zerodsoft.scheduleweather.weather.sunsetrise.SunsetRise;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class UltraSrtNcstFragment extends Fragment {
	/*
	- 초단기 실황 -
	기온
	1시간 강수량
	동서바람성분(미 표시)
	남북바람성분(미 표시)
	습도
	강수형태
	풍향
	풍속
	 */
	private UltraSrtNcstFragmentBinding binding;
	private WeatherAreaCodeDTO weatherAreaCode;
	private UltraSrtNcstProcessing ultraSrtNcstProcessing;


	public UltraSrtNcstFragment(WeatherAreaCodeDTO weatherAreaCodeDTO) {
		this.weatherAreaCode = weatherAreaCodeDTO;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = UltraSrtNcstFragmentBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		binding.customProgressView.setContentView(binding.ultraSrtNcstLayout);
		binding.customProgressView.onStartedProcessingData();
		clearViews();

		ultraSrtNcstProcessing = new UltraSrtNcstProcessing(getContext(), weatherAreaCode.getY(), weatherAreaCode.getX());
		ultraSrtNcstProcessing.getWeatherData(new WeatherDataCallback<UltraSrtNcstResult>() {
			@Override
			public void isSuccessful(UltraSrtNcstResult e) {
				if (getActivity() != null) {
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							setValue(e);
							binding.customProgressView.onSuccessfulProcessingData();
						}
					});
				}
			}

			@Override
			public void isFailure(Exception e) {
				if (getActivity() != null) {
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							binding.customProgressView.onFailedProcessingData(getString(R.string.error));
							clearViews();
						}
					});
				}
			}
		});
	}

	private void setValue(UltraSrtNcstResult ultraSrtNcstResult) {
		UltraSrtNcstFinalData ultraSrtNcstFinalData = ultraSrtNcstResult.getUltraSrtNcstFinalData();
		//기온
		binding.ultraSrtNcstTemp.setText(ultraSrtNcstFinalData.getTemperature() + getString(R.string.celcius));
		//강수형태
		int ptyDrawableId = WeatherDataConverter.getSkyDrawableId(null, ultraSrtNcstFinalData.getPrecipitationForm(), true);
		if (ptyDrawableId != 0) {
			binding.ptyImage.setImageDrawable(ContextCompat.getDrawable(getContext(), ptyDrawableId));
			binding.ptyImage.setVisibility(View.VISIBLE);
		} else {
			binding.ptyImage.setVisibility(View.GONE);
		}
		//습도
		binding.ultraSrtNcstHumidity.setText(ultraSrtNcstFinalData.getHumidity());
		//바람
		binding.ultraSrtNcstWind.setText(ultraSrtNcstFinalData.getWindSpeed() + "m/s, " + ultraSrtNcstFinalData.getWindDirection() + "\n" +
				WeatherDataConverter.getWindSpeedDescription(ultraSrtNcstFinalData.getWindSpeed()));
		//시간 강수량
		binding.ultraSrtNcstRn1.setText(ultraSrtNcstFinalData.getPrecipitation1Hour());

		Calendar todayCalendar = Calendar.getInstance(ClockUtil.TIME_ZONE);
		Calendar tomorrowCalendar = (Calendar) todayCalendar.clone();
		tomorrowCalendar.add(Calendar.DATE, 1);

		SunriseSunsetCalculator sunriseSunsetCalculator = new SunriseSunsetCalculator(new Location(weatherAreaCode.getLatitudeSecondsDivide100()
				, weatherAreaCode.getLongitudeSecondsDivide100()), todayCalendar.getTimeZone());

		Calendar todaySunRise = sunriseSunsetCalculator.getOfficialSunriseCalendarForDate(todayCalendar);
		Calendar todaySunSet = sunriseSunsetCalculator.getOfficialSunsetCalendarForDate(todayCalendar);
		Calendar tomorrowSunRise = sunriseSunsetCalculator.getOfficialSunriseCalendarForDate(tomorrowCalendar);
		Calendar tomorrowSunSet = sunriseSunsetCalculator.getOfficialSunsetCalendarForDate(tomorrowCalendar);

		SimpleDateFormat sunsetRiseDateFormat = new SimpleDateFormat("HH:mm");
		//날짜
		binding.todayDatetime.setText(ClockUtil.YYYY_M_D_E.format(todayCalendar.getTime()));
		binding.tomorrowDatetime.setText(ClockUtil.YYYY_M_D_E.format(tomorrowCalendar.getTime()));
		//일출
		binding.todaySunrise.setText(sunsetRiseDateFormat.format(todaySunRise.getTime()));
		binding.tomorrowSunrise.setText(sunsetRiseDateFormat.format(tomorrowSunRise.getTime()));
		//일몰
		binding.todaySunset.setText(sunsetRiseDateFormat.format(todaySunSet.getTime()));
		binding.tomorrowSunset.setText(sunsetRiseDateFormat.format(tomorrowSunSet.getTime()));
	}

	public void refresh() {
		binding.customProgressView.onStartedProcessingData();

		ultraSrtNcstProcessing.refresh(new WeatherDataCallback<UltraSrtNcstResult>() {
			@Override
			public void isSuccessful(UltraSrtNcstResult e) {
				if (getActivity() != null) {
					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							setValue(e);
							binding.customProgressView.onSuccessfulProcessingData();
						}
					});
				}
			}

			@Override
			public void isFailure(Exception e) {
				if (getActivity() != null) {
					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							binding.customProgressView.onFailedProcessingData(getString(R.string.error));
							clearViews();
						}
					});
				}
			}
		});
	}

	public void clearViews() {
		binding.ultraSrtNcstTemp.setText("");
		binding.ptyImage.setVisibility(View.GONE);
		binding.ultraSrtNcstHumidity.setText("");
		binding.ultraSrtNcstWind.setText("");
		binding.ultraSrtNcstRn1.setText("");

		binding.todaySunrise.setText("");
		binding.todaySunset.setText("");
		binding.tomorrowSunrise.setText("");
		binding.tomorrowSunset.setText("");
		binding.todayDatetime.setText("");
		binding.tomorrowDatetime.setText("");
	}
}