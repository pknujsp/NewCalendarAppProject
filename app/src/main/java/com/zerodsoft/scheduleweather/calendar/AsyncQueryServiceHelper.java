package com.zerodsoft.scheduleweather.calendar;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.CalendarContract;
import android.util.Log;

import androidx.annotation.Nullable;

import com.zerodsoft.scheduleweather.activity.editevent.activity.ModifyInstanceFragment;
import com.zerodsoft.scheduleweather.activity.editevent.interfaces.OnEditEventResultListener;
import com.zerodsoft.scheduleweather.calendar.interfaces.OnUpdateEventResultListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class AsyncQueryServiceHelper extends IntentService {
	private final PriorityQueue<OperationInfo> sWorkQueue =
			new PriorityQueue<OperationInfo>();
	private static Class<AsyncQueryService> mService = AsyncQueryService.class;
	private static OnEditEventResultListener onEditEventResultListener;
	private static OnUpdateEventResultListener onUpdateEventResultListener;
	private static UpdatedEventPrimaryValues updatedEventPrimaryValues;

	public AsyncQueryServiceHelper(String name) {
		super(name);
	}

	public AsyncQueryServiceHelper() {
		super("AsyncQueryServiceHelper");
	}


	public void setOnUpdateEventResultListener(OnUpdateEventResultListener onUpdateEventResultListener) {
		AsyncQueryServiceHelper.onUpdateEventResultListener = onUpdateEventResultListener;
	}

	public void queueOperation(Context context, OperationInfo args, OnEditEventResultListener onEditEventResultListener) {
		args.calculateScheduledTime();
		updatedEventPrimaryValues = args.updatedEventPrimaryValues;

		AsyncQueryServiceHelper.onEditEventResultListener = onEditEventResultListener;

		synchronized (sWorkQueue) {
			sWorkQueue.add(args);
			sWorkQueue.notify();
		}

		Intent intent = new Intent(context, AsyncQueryServiceHelper.class);
		context.startService(intent);
	}

	public AsyncQueryService.Operation getLastCancelableOperation() {
		long lastScheduleTime = Long.MIN_VALUE;
		AsyncQueryService.Operation op = null;

		synchronized (sWorkQueue) {
			Iterator<OperationInfo> it = sWorkQueue.iterator();
			while (it.hasNext()) {
				OperationInfo info = it.next();
				if (info.delayMillis > 0 && lastScheduleTime < info.mScheduledTimeMillis) {
					if (op == null) {
						op = new AsyncQueryService.Operation();
					}

					op.token = info.token;
					op.op = info.op;
					op.scheduledExecutionTime = info.mScheduledTimeMillis;

					lastScheduleTime = info.mScheduledTimeMillis;
				}
			}
		}

		if (AsyncQueryService.localLOGV) {
			Log.d("", "getLastCancelableOperation -> Operation:" + AsyncQueryService.Operation.opToChar(op.op)
					+ " token:" + op.token);
		}
		return op;
	}


	public int cancelOperation(int token) {
		int canceled = 0;
		synchronized (sWorkQueue) {
			Iterator<OperationInfo> it = sWorkQueue.iterator();
			while (it.hasNext()) {
				if (it.next().token == token) {
					it.remove();
					++canceled;
				}
			}
		}

		if (AsyncQueryService.localLOGV) {
			Log.d("", "cancelOperation(" + token + ") -> " + canceled);
		}
		return canceled;
	}

	@Override
	protected void onHandleIntent(@Nullable @org.jetbrains.annotations.Nullable Intent intent) {
		OperationInfo args;

		if (AsyncQueryService.localLOGV) {
			Log.d("", "onHandleIntent: queue size=" + sWorkQueue.size());
		}
		synchronized (sWorkQueue) {
			while (true) {
				/*
				 * This method can be called with no work because of
				 * cancellations
				 */
				if (sWorkQueue.size() == 0) {
					return;
				} else if (sWorkQueue.size() == 1) {
					OperationInfo first = sWorkQueue.peek();
					long waitTime = first.mScheduledTimeMillis - SystemClock.elapsedRealtime();
					if (waitTime > 0) {
						try {
							sWorkQueue.wait(waitTime);
						} catch (InterruptedException e) {
						}
					}
				}

				args = sWorkQueue.poll();
				if (args != null) {
					// Got work to do. Break out of waiting loop
					break;
				}
			}
		}

		if (AsyncQueryService.localLOGV) {
			Log.d("", "onHandleIntent: " + args);
		}

		ContentResolver resolver = args.resolver;
		if (resolver != null) {

			switch (args.op) {
				case AsyncQueryService.Operation.EVENT_ARG_QUERY:
					Cursor cursor;
					try {
						cursor = resolver.query(args.uri, args.projection, args.selection,
								args.selectionArgs, args.orderBy);
						/*
						 * Calling getCount() causes the cursor window to be
						 * filled, which will make the first access on the main
						 * thread a lot faster
						 */
						if (cursor != null) {
							cursor.getCount();
						}
					} catch (Exception e) {
						Log.w("", e.toString());
						cursor = null;
					}

					args.result = cursor;
					break;

				case AsyncQueryService.Operation.EVENT_ARG_INSERT:
					args.result = resolver.insert(args.uri, args.values);

					if (args.uri.equals(CalendarContract.Events.CONTENT_URI)) {
						Uri insertedUri = (Uri) args.result;
						long newEventId = Long.parseLong(insertedUri.getLastPathSegment());
						updatedEventPrimaryValues.setNewEventId(newEventId);
					}
					break;

				case AsyncQueryService.Operation.EVENT_ARG_UPDATE:
					args.result = resolver.update(args.uri, args.values, args.selection,
							args.selectionArgs);
					break;

				case AsyncQueryService.Operation.EVENT_ARG_DELETE:
					try {
						args.result = resolver.delete(args.uri, args.selection, args.selectionArgs);
					} catch (IllegalArgumentException e) {
						Log.w("", "Delete failed.");
						Log.w("", e.toString());
						args.result = 0;
					}

					break;

				case AsyncQueryService.Operation.EVENT_ARG_BATCH:
					try {
						args.result = resolver.applyBatch(args.authority, args.cpo);
					} catch (RemoteException e) {
						Log.e("", e.toString());
						args.result = null;
					} catch (OperationApplicationException e) {
						Log.e("", e.toString());
						args.result = null;
					}
					break;
			}

			/*
			 * passing the original token value back to the caller on top of the
			 * event values in arg1.
			 */
			Message reply = args.handler.obtainMessage(args.token);
			reply.obj = args;
			reply.arg1 = args.op;

			if (AsyncQueryService.localLOGV) {
				Log.d("", "onHandleIntent: op=" + AsyncQueryService.Operation.opToChar(args.op) + ", token="
						+ reply.what);
			}

			reply.sendToTarget();
		}
	}


	@Override
	public void onStart(Intent intent, int startId) {
		if (AsyncQueryService.localLOGV) {
			Log.d("", "onStart startId=" + startId);
		}
		super.onStart(intent, startId);
	}

	@Override
	public void onCreate() {
		if (AsyncQueryService.localLOGV) {
			Log.e("AsyncQueryService", "onCreate");
		}
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		if (AsyncQueryService.localLOGV) {
			Log.e("AsyncQueryService", "onDestroy");
		}

		switch (updatedEventPrimaryValues.getEventEditType()) {
			case SAVE_NEW_EVENT:
				onEditEventResultListener.onSavedNewEvent(updatedEventPrimaryValues.getBegin());
				break;
			case UPDATE_ALL_EVENTS:
				if (onUpdateEventResultListener != null) {
					onUpdateEventResultListener.onResultUpdatedAllEvents(updatedEventPrimaryValues.getBegin());
				}
				onEditEventResultListener.onUpdatedAllEvents(updatedEventPrimaryValues.getBegin());
				break;

			case UPDATE_FOLLOWING_EVENTS:
				if (onUpdateEventResultListener != null) {
					onUpdateEventResultListener.onResultUpdatedFollowingEvents(updatedEventPrimaryValues.getOriginalEventId() == null ?
							updatedEventPrimaryValues.getNewEventId() :
							updatedEventPrimaryValues.getOriginalEventId(), updatedEventPrimaryValues.getBegin());
				}
				onEditEventResultListener.onUpdatedFollowingEvents(updatedEventPrimaryValues.getBegin());
				break;

			case UPDATE_ONLY_THIS_EVENT:
				if (onUpdateEventResultListener != null) {
					onUpdateEventResultListener.onResultUpdatedThisEvent(updatedEventPrimaryValues.getOriginalEventId() == null ?
							updatedEventPrimaryValues.getNewEventId() :
							updatedEventPrimaryValues.getOriginalEventId(), updatedEventPrimaryValues.getBegin());
				}
				onEditEventResultListener.onUpdatedOnlyThisEvent(updatedEventPrimaryValues.getBegin());
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
		super.onDestroy();
	}

	protected static class OperationInfo implements Delayed {
		public int token; // Used for cancel
		public int op;
		public ContentResolver resolver;
		public Uri uri;
		public String authority;
		public Handler handler;
		public String[] projection;
		public String selection;
		public String[] selectionArgs;
		public String orderBy;
		public Object result;
		@Nullable
		public Object cookie;
		public ContentValues values;
		public ArrayList<ContentProviderOperation> cpo;
		public UpdatedEventPrimaryValues updatedEventPrimaryValues;

		/**
		 * delayMillis is relative time e.g. 10,000 milliseconds
		 */
		public long delayMillis;

		/**
		 * scheduleTimeMillis is the time scheduled for this to be processed.
		 * e.g. SystemClock.elapsedRealtime() + 10,000 milliseconds Based on
		 * {@link android.os.SystemClock#elapsedRealtime }
		 */
		private long mScheduledTimeMillis = 0;

		// @VisibleForTesting
		void calculateScheduledTime() {
			mScheduledTimeMillis = SystemClock.elapsedRealtime() + delayMillis;
		}

		// @Override // Uncomment with Java6
		public long getDelay(TimeUnit unit) {
			return unit.convert(mScheduledTimeMillis - SystemClock.elapsedRealtime(),
					TimeUnit.MILLISECONDS);
		}

		// @Override // Uncomment with Java6
		public int compareTo(Delayed another) {
			OperationInfo anotherArgs = (OperationInfo) another;
			if (this.mScheduledTimeMillis == anotherArgs.mScheduledTimeMillis) {
				return 0;
			} else if (this.mScheduledTimeMillis < anotherArgs.mScheduledTimeMillis) {
				return -1;
			} else {
				return 1;
			}
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("OperationInfo [\n\t token= ");
			builder.append(token);
			builder.append(",\n\t op= ");
			builder.append(AsyncQueryService.Operation.opToChar(op));
			builder.append(",\n\t uri= ");
			builder.append(uri);
			builder.append(",\n\t authority= ");
			builder.append(authority);
			builder.append(",\n\t delayMillis= ");
			builder.append(delayMillis);
			builder.append(",\n\t mScheduledTimeMillis= ");
			builder.append(mScheduledTimeMillis);
			builder.append(",\n\t resolver= ");
			builder.append(resolver);
			builder.append(",\n\t handler= ");
			builder.append(handler);
			builder.append(",\n\t projection= ");
			builder.append(Arrays.toString(projection));
			builder.append(",\n\t selection= ");
			builder.append(selection);
			builder.append(",\n\t selectionArgs= ");
			builder.append(Arrays.toString(selectionArgs));
			builder.append(",\n\t orderBy= ");
			builder.append(orderBy);
			builder.append(",\n\t result= ");
			builder.append(result);
			builder.append(",\n\t cookie= ");
			builder.append(cookie);
			builder.append(",\n\t values= ");
			builder.append(values);
			builder.append(",\n\t cpo= ");
			builder.append(cpo);
			builder.append("\n]");

			return builder.toString();
		}

		/**
		 * Compares an user-visible operation to this private OperationInfo
		 * object
		 *
		 * @param o operation to be compared
		 * @return true if logically equivalent
		 */
		public boolean equivalent(AsyncQueryService.Operation o) {
			return o.token == this.token && o.op == this.op;
		}
	}
}
