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
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import lombok.SneakyThrows;

public class DateTimeTickReceiver extends BroadcastReceiver {
	private static DateTimeTickReceiver instance = null;
	private CarrierMessagingService.ResultCallback<String> resultCallback;

	public static DateTimeTickReceiver newInstance(CarrierMessagingService.ResultCallback<String> resultCallback) {
		instance = new DateTimeTickReceiver(resultCallback);
		return instance;
	}

	public static DateTimeTickReceiver getInstance() {
		return instance;
	}

	public DateTimeTickReceiver(CarrierMessagingService.ResultCallback<String> resultCallback) {
		this.resultCallback = resultCallback;
	}

	@SneakyThrows
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction() != null) {
			switch (intent.getAction()) {
				case Intent.ACTION_TIME_TICK:
					resultCallback.onReceiveResult(Intent.ACTION_TIME_TICK);
					break;
				case Intent.ACTION_DATE_CHANGED:
					resultCallback.onReceiveResult(Intent.ACTION_DATE_CHANGED);
					break;
			}
		}
	}


}
