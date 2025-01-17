package com.zerodsoft.calendarplatform.calendar;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.CalendarContract.Events;
import android.util.Log;

import androidx.annotation.Nullable;

import com.zerodsoft.calendarplatform.activity.editevent.interfaces.OnEditEventResultListener;
import com.zerodsoft.calendarplatform.common.enums.LocationIntentCode;
import com.zerodsoft.calendarplatform.event.common.repository.LocationRepository;
import com.zerodsoft.calendarplatform.event.foods.repository.FoodCriteriaLocationInfoRepository;
import com.zerodsoft.calendarplatform.room.dto.LocationDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class AsyncQueryServiceHelper extends IntentService {
	private static final PriorityQueue<OperationInfo> sWorkQueue =
			new PriorityQueue<OperationInfo>();
	private Class<AsyncQueryService> mService = AsyncQueryService.class;
	private LocationRepository locationRepository;
	private FoodCriteriaLocationInfoRepository foodCriteriaLocationInfoRepository;

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
		Long removeEventId = null;

		Integer insertNewEventIndex = null;
		Integer updateOriginalEventIndex = null;
		Integer exceptionOriginalEventIndex = null;
		Integer removeEventIndex = null;

		ContentProviderResult[] contentProviderResults = null;
		synchronized (sWorkQueue) {
			while (true) {
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
		ArrayList<ContentProviderOperation> cpo = args.cpo;


		if (resolver != null) {
			switch (args.op) {
				case AsyncQueryService.Operation.EVENT_ARG_BATCH:
					try {

						int cpoIndex = 0;
						for (ContentProviderOperation contentProviderOperation : cpo) {
							if (contentProviderOperation.isUpdate()) {
								if (contentProviderOperation.getUri().toString().contains(Events.CONTENT_URI.toString())) {
									updateOriginalEventIndex = cpoIndex;
								}
							} else if (contentProviderOperation.isInsert()) {
								if (contentProviderOperation.getUri().toString().contains(Events.CONTENT_URI.toString())) {
									insertNewEventIndex = cpoIndex;
								} else if (contentProviderOperation.getUri().toString().contains(Events.CONTENT_EXCEPTION_URI.toString())) {
									exceptionOriginalEventIndex = cpoIndex;
								}
							} else if (contentProviderOperation.isDelete()) {
								if (contentProviderOperation.getUri().toString().contains(Events.CONTENT_URI.toString())) {
									removeEventIndex = cpoIndex;
								}
							}
							cpoIndex++;
						}

						contentProviderResults = resolver.applyBatch(args.authority, cpo);
						args.result = contentProviderResults;
					} catch (RemoteException e) {
						Log.e("", e.toString());
						args.result = null;
					} catch (OperationApplicationException e) {
						Log.e("", e.toString());
						args.result = null;
					}
					break;
			}

			if (insertNewEventIndex != null) {
				newEventId = ContentUris.parseId(contentProviderResults[insertNewEventIndex].uri);
				args.editEventPrimaryValues.setNewEventId(newEventId);
			}
			if (updateOriginalEventIndex != null) {
				originalEventId = ContentUris.parseId(cpo.get(updateOriginalEventIndex).getUri());
				args.editEventPrimaryValues.setOriginalEventId(originalEventId);
			}
			if (exceptionOriginalEventIndex != null) {
				originalEventId = ContentUris.parseId(cpo.get(exceptionOriginalEventIndex).getUri());
				args.editEventPrimaryValues.setOriginalEventId(originalEventId);
			}
			if (removeEventIndex != null) {
				removeEventId = ContentUris.parseId(cpo.get(removeEventIndex).getUri());
			}

			final EventHelper.EventEditType eventEditType = args.editEventPrimaryValues.getEventEditType();
			final LocationIntentCode locationIntentCode = args.editEventPrimaryValues.getLocationIntentCode();
			final LocationDTO locationDTO = args.editEventPrimaryValues.getNewLocationDto();
			if (locationDTO != null) {
				locationDTO.setId(0);
			}
			locationRepository = new LocationRepository(getApplicationContext());

			if (eventEditType == EventHelper.EventEditType.SAVE_NEW_EVENT) {
				if (locationIntentCode != null) {
					locationDTO.setEventId(newEventId);
					locationRepository.addLocation(locationDTO, null);
				}
			} else if (eventEditType == EventHelper.EventEditType.UPDATE_ALL_EVENTS) {
				if (locationIntentCode != null) {
					if (locationIntentCode == LocationIntentCode.RESULT_CODE_CHANGED_LOCATION) {
						locationRepository.removeLocation(originalEventId, null);
						locationDTO.setEventId(originalEventId);
						locationRepository.addLocation(locationDTO, null);
					} else if (locationIntentCode == LocationIntentCode.RESULT_CODE_REMOVED_LOCATION) {
						locationRepository.removeLocation(originalEventId, null);
					} else if (locationIntentCode == LocationIntentCode.RESULT_CODE_SELECTED_LOCATION) {
						locationRepository.removeLocation(originalEventId, null);
						locationDTO.setEventId(originalEventId);
						locationRepository.addLocation(locationDTO, null);
					}
				} else if (locationDTO != null) {

				}
			} else if (eventEditType == EventHelper.EventEditType.UPDATE_FOLLOWING_EVENTS) {
				if (locationIntentCode != null) {
					if (locationIntentCode == LocationIntentCode.RESULT_CODE_CHANGED_LOCATION) {
						if (newEventId != null) {
							locationDTO.setEventId(newEventId);
						} else {
							locationDTO.setEventId(originalEventId);
						}

						if (newEventId == null) {
							locationRepository.removeLocation(originalEventId, null);
						}
						locationRepository.addLocation(locationDTO, null);
					} else if (locationIntentCode == LocationIntentCode.RESULT_CODE_REMOVED_LOCATION) {
						locationRepository.removeLocation(originalEventId, null);
					} else if (locationIntentCode == LocationIntentCode.RESULT_CODE_SELECTED_LOCATION) {

						if (newEventId != null) {
							locationDTO.setEventId(newEventId);
						} else {
							locationDTO.setEventId(originalEventId);
						}
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
					if (locationIntentCode == LocationIntentCode.RESULT_CODE_CHANGED_LOCATION) {
						locationDTO.setEventId(newEventId);
						locationRepository.addLocation(locationDTO, null);
					} else if (locationIntentCode == LocationIntentCode.RESULT_CODE_REMOVED_LOCATION) {
						locationRepository.removeLocation(originalEventId, null);
					} else if (locationIntentCode == LocationIntentCode.RESULT_CODE_SELECTED_LOCATION) {
						locationDTO.setEventId(newEventId);
						locationRepository.addLocation(locationDTO, null);
					}
				} else if (locationDTO != null) {
					locationDTO.setEventId(newEventId);
					locationRepository.addLocation(locationDTO, null);
				}
			}


			if (removeEventId != null) {
				foodCriteriaLocationInfoRepository = new FoodCriteriaLocationInfoRepository(getApplicationContext());
				foodCriteriaLocationInfoRepository.deleteByEventId(removeEventId, null);
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
