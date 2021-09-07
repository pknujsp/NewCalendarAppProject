package com.zerodsoft.calendarplatform.common.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zerodsoft.calendarplatform.common.interfaces.BroadcastReceiverCallback;

public class DateTimeTickReceiver extends BroadcastReceiver {
	private BroadcastReceiverCallback<String> broadcastReceiverCallback;

	public DateTimeTickReceiver(BroadcastReceiverCallback<String> broadcastReceiverCallback) {
		this.broadcastReceiverCallback = broadcastReceiverCallback;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction() != null) {
			switch (intent.getAction()) {
				case Intent.ACTION_TIME_TICK:
					broadcastReceiverCallback.onReceived(Intent.ACTION_TIME_TICK);
					break;
				case Intent.ACTION_DATE_CHANGED:
					broadcastReceiverCallback.onReceived(Intent.ACTION_DATE_CHANGED);
					break;
			}
		}
	}


}
