package com.zerodsoft.calendarplatform.calendar;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.Nullable;

import com.zerodsoft.calendarplatform.activity.editevent.interfaces.OnEditEventResultListener;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncQueryService extends Handler {
	private static AtomicInteger mUniqueToken = new AtomicInteger(0);

	private final OnEditEventResultListener onEditEventResultListener;
	private final AsyncQueryServiceHelper asyncQueryServiceHelper;

	private Context context;
	private Handler mHandler = this;

	public AsyncQueryService(Context context, OnEditEventResultListener onEditEventResultListener) {
		this.context = context;
		this.onEditEventResultListener = onEditEventResultListener;
		this.asyncQueryServiceHelper = new AsyncQueryServiceHelper();
	}


	public final int getNextToken() {
		return mUniqueToken.getAndIncrement();
	}

	public final Operation getLastCancelableOperation() {
		return asyncQueryServiceHelper.getLastCancelableOperation();
	}

	public final int cancelOperation(int token) {
		return asyncQueryServiceHelper.cancelOperation(token);
	}

	public void startBatch(int token, @Nullable Object cookie, String authority,
	                       ArrayList<ContentProviderOperation> cpo, long delayMillis, EditEventPrimaryValues editEventPrimaryValues) {
		AsyncQueryServiceHelper.OperationInfo info = new AsyncQueryServiceHelper.OperationInfo();

		info.op = Operation.EVENT_ARG_BATCH;
		info.resolver = context.getContentResolver();
		info.handler = mHandler;
		info.token = token;
		info.cookie = cookie;
		info.authority = authority;
		info.cpo = cpo;
		info.delayMillis = delayMillis;
		info.editEventPrimaryValues = editEventPrimaryValues;

		asyncQueryServiceHelper.queueOperation(context, info, onEditEventResultListener);
	}

	protected void onQueryComplete(int token, @Nullable Object cookie, Cursor cursor) {
	}

	protected void onInsertComplete(int token, @Nullable Object cookie, Uri uri) {
	}

	protected void onUpdateComplete(int token, @Nullable Object cookie, int result) {
	}

	protected void onDeleteComplete(int token, @Nullable Object cookie, int result) {
	}

	protected void onBatchComplete(int token, @Nullable Object cookie, ContentProviderResult[] results) {
	}

	@Override
	public void handleMessage(Message msg) {
		AsyncQueryServiceHelper.OperationInfo info = (AsyncQueryServiceHelper.OperationInfo) msg.obj;

		int token = msg.what;
		int op = msg.arg1;

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

		switch (info.editEventPrimaryValues.getEventEditType()) {
			case SAVE_NEW_EVENT:
				onEditEventResultListener.onSavedNewEvent(info.editEventPrimaryValues.getBegin());
				break;
			case UPDATE_ALL_EVENTS:
				onEditEventResultListener.onUpdatedAllEvents(info.editEventPrimaryValues.getBegin());
				break;

			case UPDATE_FOLLOWING_EVENTS:
				onEditEventResultListener.onUpdatedFollowingEvents(info.editEventPrimaryValues.getBegin());
				break;

			case UPDATE_ONLY_THIS_EVENT:
				onEditEventResultListener.onUpdatedOnlyThisEvent(info.editEventPrimaryValues.getBegin());
				break;

			case REMOVE_ALL_EVENTS:
				onEditEventResultListener.onRemovedAllEvents();
				break;

			case REMOVE_FOLLOWING_EVENTS:
				onEditEventResultListener.onRemovedFollowingEvents();
				break;

			case REMOVE_ONLY_THIS_EVENT:
				onEditEventResultListener.onRemovedOnlyThisEvents();
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
