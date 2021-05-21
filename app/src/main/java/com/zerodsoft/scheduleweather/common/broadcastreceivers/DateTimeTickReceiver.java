package com.zerodsoft.scheduleweather.common.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DateTimeTickReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		switch (intent.getAction()) {
			case Intent.ACTION_TIME_TICK:
				receivedTimeTick(context, intent);
				break;
			case Intent.ACTION_DATE_CHANGED:
				receivedDateChanged(context, intent);
				break;
			default:
				break;
		}
	}

	private void receivedTimeTick(Context context, Intent intent) {

	}

	private void receivedDateChanged(Context context, Intent intent) {

	}
}
