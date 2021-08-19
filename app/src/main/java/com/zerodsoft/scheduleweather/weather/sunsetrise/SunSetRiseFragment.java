package com.zerodsoft.scheduleweather.weather.sunsetrise;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentSunsetriseBinding;
import com.zerodsoft.scheduleweather.databinding.UltraSrtNcstFragmentBinding;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.weather.common.WeatherDataCallback;
import com.zerodsoft.scheduleweather.weather.ultrasrtncst.UltraSrtNcstResult;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class SunSetRiseFragment extends Fragment {
	private FragmentSunsetriseBinding binding;
	private WeatherAreaCodeDTO weatherAreaCode;

	public SunSetRiseFragment(WeatherAreaCodeDTO weatherAreaCode) {
		this.weatherAreaCode = weatherAreaCode;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentSunsetriseBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		binding.customProgressView.setContentView(binding.sunSetRiseLayout);
		binding.customProgressView.onStartedProcessingData();
		clearViews();
		setData();
	}

	private void clearViews() {
		binding.todaySunrise.setText("");
		binding.todaySunset.setText("");
		binding.tomorrowSunrise.setText("");
		binding.tomorrowSunset.setText("");
		binding.todayDatetime.setText("");
		binding.tomorrowDatetime.setText("");
	}

	private void setData() {

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

		binding.customProgressView.onSuccessfulProcessingData();
	}

	public void refresh() {
		binding.customProgressView.onStartedProcessingData();
		setData();
	}
}