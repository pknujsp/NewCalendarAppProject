package com.zerodsoft.scheduleweather.notification.receiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.CalendarAlerts;

import androidx.core.app.ActivityCompat;

import com.zerodsoft.scheduleweather.calendar.CalendarProvider;

public class EventAlarmProcessingReceiver extends BroadcastReceiver {
	public static final String ACTION_PROCESS_EVENT_STATUS = "android.intent.action.PROCESS_EVENT_STATUS";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		if (action.equals(ACTION_PROCESS_EVENT_STATUS)) {
			if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
				return;
			}

			Bundle bundle = intent.getExtras();
			final Long eventId = bundle.getLong(CalendarAlerts.EVENT_ID);
			final Integer status = bundle.getInt(CalendarAlerts.STATUS);

			CalendarProvider calendarProvider = new CalendarProvider(context);
			calendarProvider.updateEventStatus(eventId, status);
		}
	}
}
