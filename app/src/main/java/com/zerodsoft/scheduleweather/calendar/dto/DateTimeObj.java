package com.zerodsoft.scheduleweather.calendar.dto;

import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.Calendar;
import java.util.Objects;

public class DateTimeObj implements Cloneable {
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;

	public int getYear() {
		return year;
	}

	public DateTimeObj setYear(int year) {
		this.year = year;
		return this;
	}

	public int getMonth() {
		return month;
	}

	public DateTimeObj setMonth(int month) {
		this.month = month;
		return this;
	}

	public int getDay() {
		return day;
	}

	public DateTimeObj setDay(int day) {
		this.day = day;
		return this;
	}

	public int getHour() {
		return hour;
	}

	public DateTimeObj setHour(int hour) {
		this.hour = hour;
		return this;
	}

	public int getMinute() {
		return minute;
	}

	public DateTimeObj setMinute(int minute) {
		this.minute = minute;
		return this;
	}

	public void add(int field, int amount) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, day, hour, minute, 0);
		calendar.add(field, amount);

		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH) + 1;
		day = calendar.get(Calendar.DAY_OF_MONTH);
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		minute = calendar.get(Calendar.MINUTE);
	}

	public long getTimeMillis() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, day, hour, minute, 0);
		return calendar.getTimeInMillis();
	}

	public Calendar getCalendar() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, day, hour, minute, 0);
		return calendar;
	}

	public Calendar getUtcCalendar() {
		Calendar calendar = Calendar.getInstance(ClockUtil.UTC_TIME_ZONE);
		calendar.set(year, month - 1, day, hour, minute, 0);
		return calendar;
	}

	public void setTimeMillis(long timeMillis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeMillis);

		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH) + 1;
		day = calendar.get(Calendar.DAY_OF_MONTH);
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		minute = calendar.get(Calendar.MINUTE);
	}

	@Override
	public DateTimeObj clone() throws CloneNotSupportedException {
		return (DateTimeObj) super.clone();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		DateTimeObj that = (DateTimeObj) o;
		return year == that.year &&
				month == that.month &&
				day == that.day &&
				hour == that.hour &&
				minute == that.minute;
	}

	@Override
	public int hashCode() {
		return Objects.hash(year, month, day, hour, minute);
	}

	public boolean isSameDate(DateTimeObj dateTimeObj) {
		if (year == dateTimeObj.getYear() && month == dateTimeObj.getMonth()
				&& day == dateTimeObj.getDay()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isSameDateTime(DateTimeObj dateTimeObj) {
		if (year == dateTimeObj.getYear() && month == dateTimeObj.getMonth()
				&& day == dateTimeObj.getDay() && hour == dateTimeObj.getHour()
				&& minute == dateTimeObj.getMinute()) {
			return true;
		} else {
			return false;
		}
	}
}