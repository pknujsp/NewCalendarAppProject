package com.zerodsoft.scheduleweather.calendarview.interfaces;

import androidx.fragment.app.Fragment;

import com.zerodsoft.scheduleweather.calendarview.day.DayFragment;
import com.zerodsoft.scheduleweather.calendarview.month.MonthFragment;
import com.zerodsoft.scheduleweather.calendarview.week.WeekFragment;

import java.util.Date;

public interface OnDateTimeChangedListener {
	void receivedTimeTick(Date date);

	void receivedDateChanged(Date date);
}
