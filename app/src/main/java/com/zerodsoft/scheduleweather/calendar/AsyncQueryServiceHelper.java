package com.zerodsoft.scheduleweather.calendar;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
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

import com.zerodsoft.scheduleweather.activity.editevent.interfaces.OnEditEventResultListener;
import com.zerodsoft.scheduleweather.common.enums.LocationIntentCode;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.event.common.repository.LocationRepository;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class AsyncQueryServiceHelper extends IntentService {
	private static final PriorityQueue<OperationInfo> sWorkQueue =
			new PriorityQueue<OperationInfo>();
	private Class<AsyncQueryService> mService = AsyncQueryService.class;
	private LocationRepository locationRepository;

	public AsyncQueryServiceHelper(String name) {
		super(name);
	}

	public AsyncQueryServiceHelper() {
		super("AsyncQueryServiceHelper");
	}


	public void queueOperation(Context context, OperationInfo args, OnEditEventResultListener onEditEventResultListener) {
		args.calculateScheduledTime();
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


		return canceled;
	}

	@Override
	protected void onHandleIntent(@Nullable @org.jetbrains.annotations.Nullable Intent intent) {
		OperationInfo args;
		Long originalEventId = null;
		Long newEventId = null;

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
						Integer insertNewEventIndex = null;
						Integer updateOriginalEventIndex = null;

						ArrayList<ContentProviderOperation> cpo = args.cpo;

						int cpoIndex = 0;
						for (ContentProviderOperation contentProviderOperation : cpo) {
							if (contentProviderOperation.isUpdate()) {
								if (contentProviderOperation.getUri().toString().contains(CalendarContract.Events.CONTENT_URI.toString())) {
									updateOriginalEventIndex = cpoIndex;
								}
							} else if (contentProviderOperation.isInsert()) {
								if (contentProviderOperation.getUri().toString().contains(CalendarContract.Events.CONTENT_URI.toString())) {
									insertNewEventIndex = cpoIndex;
								}
							}
							cpoIndex++;
						}

						ContentProviderResult[] contentProviderResults = resolver.applyBatch(args.authority, args.cpo);
						args.result = contentProviderResults;

						if (insertNewEventIndex != null) {
							newEventId = ContentUris.parseId(contentProviderResults[insertNewEventIndex].uri);
							args.editEventPrimaryValues.setNewEventId(newEventId);
						}
						if (updateOriginalEventIndex != null) {
							originalEventId = ContentUris.parseId(cpo.get(updateOriginalEventIndex).getUri());
							args.editEventPrimaryValues.setOriginalEventId(originalEventId);
						}

					} catch (RemoteException e) {
						Log.e("", e.toString());
						args.result = null;
					} catch (OperationApplicationException e) {
						Log.e("", e.toString());
						args.result = null;
					}
					break;
			}

			final EventHelper.EventEditType eventEditType = args.editEventPrimaryValues.getEventEditType();
			final LocationIntentCode locationIntentCode = args.editEventPrimaryValues.getLocationIntentCode();
			final LocationDTO locationDTO = args.editEventPrimaryValues.getNewLocationDto();
			locationRepository = new LocationRepository(getApplicationContext());

			if (eventEditType == EventHelper.EventEditType.SAVE_NEW_EVENT) {
				if (locationIntentCode != null) {
					locationDTO.setEventId(newEventId);
					locationRepository.addLocation(locationDTO, null);
				}
			} else if (eventEditType == EventHelper.EventEditType.UPDATE_ALL_EVENTS) {
				if (locationIntentCode != null) {
					locationDTO.setEventId(originalEventId);

					if (locationIntentCode == LocationIntentCode.RESULT_CODE_CHANGED_LOCATION) {
						locationRepository.removeLocation(originalEventId, null);
						locationRepository.addLocation(locationDTO, null);
					} else if (locationIntentCode == LocationIntentCode.RESULT_CODE_REMOVED_LOCATION) {
						locationRepository.removeLocation(originalEventId, null);
					} else if (locationIntentCode == LocationIntentCode.RESULT_CODE_SELECTED_LOCATION) {
						locationRepository.addLocation(locationDTO, null);
					}
				} else if (locationDTO != null) {

				}
			} else if (eventEditType == EventHelper.EventEditType.UPDATE_FOLLOWING_EVENTS) {
				if (locationIntentCode != null) {
					if (newEventId != null) {
						locationDTO.setEventId(newEventId);
					} else {
						locationDTO.setEventId(originalEventId);
					}

					if (locationIntentCode == LocationIntentCode.RESULT_CODE_CHANGED_LOCATION) {
						if (newEventId == null) {
							locationRepository.removeLocation(originalEventId, null);
						}
						locationRepository.addLocation(locationDTO, null);
					} else if (locationIntentCode == LocationIntentCode.RESULT_CODE_REMOVED_LOCATION) {
						locationRepository.removeLocation(originalEventId, null);
					} else if (locationIntentCode == LocationIntentCode.RESULT_CODE_SELECTED_LOCATION) {
						locationRepository.addLocation(locationDTO, null);
					}
				} else if (locationDTO != null) {
					if (newEventId != null) {
						locationDTO.setEventId(newEventId);
					} else {
						locationDTO.setEventId(originalEventId);
					}
					locationRepository.addLocation(locationDTO, null);
				}
			} else if (eventEditType == EventHelper.EventEditType.UPDATE_ONLY_THIS_EVENT) {
				if (locationIntentCode != null) {
					locationDTO.setEventId(newEventId);

					if (locationIntentCode == LocationIntentCode.RESULT_CODE_CHANGED_LOCATION) {
						locationRepository.addLocation(locationDTO, null);
					} else if (locationIntentCode == LocationIntentCode.RESULT_CODE_REMOVED_LOCATION) {
						locationRepository.removeLocation(originalEventId, null);
					} else if (locationIntentCode == LocationIntentCode.RESULT_CODE_SELECTED_LOCATION) {
						locationRepository.addLocation(locationDTO, null);
					}
				} else if (locationDTO != null) {
					locationDTO.setEventId(newEventId);
					locationRepository.addLocation(locationDTO, null);
				}
			}

			Message reply = args.handler.obtainMessage(args.token);
			reply.obj = args;
			reply.arg1 = args.op;

			reply.sendToTarget();
		}
	}


	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
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
		public EditEventPrimaryValues editEventPrimaryValues;

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
