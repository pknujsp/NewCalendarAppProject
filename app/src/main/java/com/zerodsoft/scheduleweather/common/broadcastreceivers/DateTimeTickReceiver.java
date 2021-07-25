package com.zerodsoft.scheduleweather.common.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;

import com.zerodsoft.scheduleweather.activity.main.AppMainActivity;
import com.zerodsoft.scheduleweather.common.interfaces.BroadcastReceiverCallback;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import lombok.SneakyThrows;

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
