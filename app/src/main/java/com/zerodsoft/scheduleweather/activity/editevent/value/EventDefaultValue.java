package com.zerodsoft.scheduleweather.activity.editevent.value;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CalendarContract;

import androidx.core.content.ContextCompat;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class EventDefaultValue {
	private Context context;
	private Calendar calendar;

	public EventDefaultValue(Context context) {
		this.context = context;
		calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
	}

	private boolean checkPermission(String permission) {
		if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
			return true;
		} else {
			return false;
		}
	}


	public Date[] getDefaultDateTime() {
		// 설정에서 기본 일정 시간 길이 설정가능
		int defaultRange = 60;

		Date[] dates = new Date[2];
		dates[0] = calendar.getTime();
		calendar.add(Calendar.MINUTE, defaultRange);
		dates[1] = calendar.getTime();
		calendar.add(Calendar.MINUTE, -defaultRange);

		return dates;
	}

	public TimeZone getDefaultTimeZone() {
		if (App.isPreference_key_using_timezone_of_device()) {
			return TimeZone.getDefault();
		} else {
			return App.getPreference_key_custom_timezone();
		}
	}

	public int getDefaultAccessLevel() {
		return CalendarContract.Events.ACCESS_DEFAULT;
	}

	public int getDefaultAvailability() {
		return CalendarContract.Events.AVAILABILITY_BUSY;
	}

}
