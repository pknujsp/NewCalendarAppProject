package com.zerodsoft.scheduleweather.calendar.dto;

import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.Calendar;
import java.util.Date;

public class DateTimeObj {
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

	public long getUtcTimeMillis() {
		Calendar calendar = Calendar.getInstance(ClockUtil.UTC_TIME_ZONE);
		calendar.set(year, month - 1, day, hour, minute, 0);
		return calendar.getTimeInMillis();
	}

	public Date getDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, day, hour, minute, 0);
		return calendar.getTime();
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
}