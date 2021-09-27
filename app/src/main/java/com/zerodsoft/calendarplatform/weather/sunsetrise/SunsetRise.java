package com.zerodsoft.calendarplatform.weather.sunsetrise;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import com.zerodsoft.calendarplatform.utility.ClockUtil;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SunsetRise {
	private SunsetRise() {
	}

	public static List<SunSetRiseData> getSunsetRiseList(Date beginDate, Date endDate, String latitudeSecondsDivide100, String longitudeSecondsDivide100) {
		Calendar calendar = Calendar.getInstance(ClockUtil.TIME_ZONE);
		calendar.setTime(beginDate);

		List<Calendar> calendarList = new ArrayList<>();

		while (true) {
			calendarList.add((Calendar) calendar.clone());

			if (ClockUtil.areSameDate(calendar.getTimeInMillis(), endDate.getTime())) {
				break;
			}
			calendar.add(Calendar.DATE, 1);
		}

		SunriseSunsetCalculator sunriseSunsetCalculator = new SunriseSunsetCalculator(new Location(latitudeSecondsDivide100,
				longitudeSecondsDivide100), ClockUtil.TIME_ZONE);
		Calendar sunRise = null;
		Calendar sunSet = null;

		List<SunSetRiseData> resultList = new ArrayList<>();
		for (Calendar date : calendarList) {
			sunRise = sunriseSunsetCalculator.getOfficialSunriseCalendarForDate(date);
			sunSet = sunriseSunsetCalculator.getOfficialSunsetCalendarForDate(date);
			resultList.add(new SunSetRiseData(date.getTime(), sunRise.getTime(), sunSet.getTime()));
		}
		return resultList;
	}
}
