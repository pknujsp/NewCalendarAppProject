package com.zerodsoft.calendarplatform.weather.sunsetrise;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import com.zerodsoft.calendarplatform.databinding.FragmentSunsetriseBinding;
import com.zerodsoft.calendarplatform.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.calendarplatform.utility.ClockUtil;
import com.zerodsoft.calendarplatform.weather.interfaces.CheckSuccess;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class SunSetRiseFragment extends Fragment implements CheckSuccess {
	private FragmentSunsetriseBinding binding;
	private WeatherAreaCodeDTO weatherAreaCode;
	private Calendar currentCalendar;

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

		if (currentCalendar != null) {
			if (ClockUtil.areSameDate(todayCalendar.getTimeInMillis(), currentCalendar.getTimeInMillis())) {
				return;
			}
		}
		binding.customProgressView.onStartedProcessingData();

		Calendar tomorrowCalendar = (Calendar) todayCalendar.clone();
		currentCalendar = (Calendar) todayCalendar.clone();
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
		setData();
	}

	@Override
	public boolean isSuccess() {
		return binding.customProgressView.isSuccess();
	}
}