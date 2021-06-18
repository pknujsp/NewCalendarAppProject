package com.zerodsoft.scheduleweather.calendar;

import android.accounts.Account;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class CalendarSyncService extends Service {
	// Storage for an instance of the sync adapter
	private CalendarSyncAdapter syncAdapter = null;

	@Override
	public void onCreate() {
		super.onCreate();
		if (syncAdapter == null) {
			syncAdapter = new CalendarSyncAdapter(getApplicationContext(), true);
		}
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return syncAdapter.getSyncAdapterBinder();
	}

	static class CalendarSyncAdapter extends AbstractThreadedSyncAdapter {
		private ContentResolver contentResolver;

		public CalendarSyncAdapter(Context context, boolean autoInitialize) {
			super(context, autoInitialize);
			contentResolver = context.getContentResolver();
		}

		public CalendarSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
			super(context, autoInitialize, allowParallelSyncs);
			contentResolver = context.getContentResolver();
		}

		@Override
		public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
			if (account != null) {
			}
		}
	}

}
