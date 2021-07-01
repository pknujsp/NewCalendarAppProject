package com.zerodsoft.scheduleweather.calendar;

import android.content.AsyncQueryHandler;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.zerodsoft.scheduleweather.calendar.AsyncQueryServiceHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncQueryService extends Handler {
	static final boolean localLOGV = true;
	private static AtomicInteger mUniqueToken = new AtomicInteger(0);
	private Context context;
	private Handler mHandler = this;

	public AsyncQueryService(Context context) {
		this.context = context;
	}

	public final int getNextToken() {
		return mUniqueToken.getAndIncrement();
	}

	public final Operation getLastCancelableOperation() {
		return AsyncQueryServiceHelper.getLastCancelableOperation();
	}

	public final int cancelOperation(int token) {
		return AsyncQueryServiceHelper.cancelOperation(token);
	}


	public void startQuery(int token, @Nullable Object cookie, Uri uri, String[] projection,
	                       String selection, String[] selectionArgs, String orderBy) {
		AsyncQueryServiceHelper.OperationInfo info = new AsyncQueryServiceHelper.OperationInfo();
		info.op = Operation.EVENT_ARG_QUERY;
		info.resolver = context.getContentResolver();

		info.handler = mHandler;
		info.token = token;
		info.cookie = cookie;
		info.uri = uri;
		info.projection = projection;
		info.selection = selection;
		info.selectionArgs = selectionArgs;
		info.orderBy = orderBy;

		AsyncQueryServiceHelper.queueOperation(context, info);
	}

	public void startInsert(int token, @Nullable Object cookie, Uri uri, ContentValues initialValues,
	                        long delayMillis) {
		AsyncQueryServiceHelper.OperationInfo info = new AsyncQueryServiceHelper.OperationInfo();
		info.op = Operation.EVENT_ARG_INSERT;
		info.resolver = context.getContentResolver();
		info.handler = mHandler;

		info.token = token;
		info.cookie = cookie;
		info.uri = uri;
		info.values = initialValues;
		info.delayMillis = delayMillis;

		AsyncQueryServiceHelper.queueOperation(context, info);
	}

	public void startUpdate(int token, @Nullable Object cookie, Uri uri, ContentValues values,
	                        String selection, String[] selectionArgs, long delayMillis) {
		AsyncQueryServiceHelper.OperationInfo info = new AsyncQueryServiceHelper.OperationInfo();
		info.op = Operation.EVENT_ARG_UPDATE;
		info.resolver = context.getContentResolver();
		info.handler = mHandler;

		info.token = token;
		info.cookie = cookie;
		info.uri = uri;
		info.values = values;
		info.selection = selection;
		info.selectionArgs = selectionArgs;
		info.delayMillis = delayMillis;

		AsyncQueryServiceHelper.queueOperation(context, info);
	}

	public void startDelete(int token, @Nullable Object cookie, Uri uri, String selection,
	                        String[] selectionArgs, long delayMillis) {
		AsyncQueryServiceHelper.OperationInfo info = new AsyncQueryServiceHelper.OperationInfo();
		info.op = Operation.EVENT_ARG_DELETE;
		info.resolver = context.getContentResolver();
		info.handler = mHandler;

		info.token = token;
		info.cookie = cookie;
		info.uri = uri;
		info.selection = selection;
		info.selectionArgs = selectionArgs;
		info.delayMillis = delayMillis;

		AsyncQueryServiceHelper.queueOperation(context, info);
	}

	public void startBatch(int token, @Nullable Object cookie, String authority,
	                       ArrayList<ContentProviderOperation> cpo, long delayMillis) {
		AsyncQueryServiceHelper.OperationInfo info = new AsyncQueryServiceHelper.OperationInfo();
		info.op = Operation.EVENT_ARG_BATCH;
		info.resolver = context.getContentResolver();
		info.handler = mHandler;

		info.token = token;
		info.cookie = cookie;
		info.authority = authority;
		info.cpo = cpo;
		info.delayMillis = delayMillis;

		AsyncQueryServiceHelper.queueOperation(context, info);
	}

	protected void onQueryComplete(int token, @Nullable Object cookie, Cursor cursor) {
		if (localLOGV) {
			Log.d("", "########## default onQueryComplete");
		}
	}

	protected void onInsertComplete(int token, @Nullable Object cookie, Uri uri) {
		if (localLOGV) {
			Log.d("", "########## default onInsertComplete");
		}
	}

	protected void onUpdateComplete(int token, @Nullable Object cookie, int result) {
		if (localLOGV) {
			Log.d("", "########## default onUpdateComplete");
		}
	}

	protected void onDeleteComplete(int token, @Nullable Object cookie, int result) {
		if (localLOGV) {
			Log.d("", "########## default onDeleteComplete");
		}
	}

	protected void onBatchComplete(int token, @Nullable Object cookie, ContentProviderResult[] results) {
		if (localLOGV) {
			Log.d("", "########## default onBatchComplete");
		}
	}

	@Override
	public void handleMessage(Message msg) {
		AsyncQueryServiceHelper.OperationInfo info = (AsyncQueryServiceHelper.OperationInfo) msg.obj;

		int token = msg.what;
		int op = msg.arg1;

		if (localLOGV) {
			Log.d("", "AsyncQueryService.handleMessage: token=" + token + ", op=" + op
					+ ", result=" + info.result);
		}

		// pass token back to caller on each callback.
		switch (op) {
			case Operation.EVENT_ARG_QUERY:
				onQueryComplete(token, info.cookie, (Cursor) info.result);
				break;

			case Operation.EVENT_ARG_INSERT:
				onInsertComplete(token, info.cookie, (Uri) info.result);
				break;

			case Operation.EVENT_ARG_UPDATE:
				onUpdateComplete(token, info.cookie, (Integer) info.result);
				break;

			case Operation.EVENT_ARG_DELETE:
				onDeleteComplete(token, info.cookie, (Integer) info.result);
				break;

			case Operation.EVENT_ARG_BATCH:
				onBatchComplete(token, info.cookie, (ContentProviderResult[]) info.result);
				break;
		}
	}


	public static class Operation {
		static final int EVENT_ARG_QUERY = 1;
		static final int EVENT_ARG_INSERT = 2;
		static final int EVENT_ARG_UPDATE = 3;
		static final int EVENT_ARG_DELETE = 4;
		static final int EVENT_ARG_BATCH = 5;

		/**
		 * unique identify for cancellation purpose
		 */
		public int token;

		/**
		 * One of the EVENT_ARG_ constants in the class describing the operation
		 */
		public int op;


		public long scheduledExecutionTime;

		protected static char opToChar(int op) {
			switch (op) {
				case Operation.EVENT_ARG_QUERY:
					return 'Q';
				case Operation.EVENT_ARG_INSERT:
					return 'I';
				case Operation.EVENT_ARG_UPDATE:
					return 'U';
				case Operation.EVENT_ARG_DELETE:
					return 'D';
				case Operation.EVENT_ARG_BATCH:
					return 'B';
				default:
					return '?';
			}
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Operation [op=");
			builder.append(op);
			builder.append(", token=");
			builder.append(token);
			builder.append(", scheduledExecutionTime=");
			builder.append(scheduledExecutionTime);
			builder.append("]");
			return builder.toString();
		}
	}
}
