package com.zerodsoft.scheduleweather.notification.receiver;

import android.Manifest;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PowerManager;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Instances;
import android.provider.CalendarContract.Reminders;
import android.provider.CalendarContract.CalendarAlerts;
import android.widget.RemoteViews;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.main.AppMainActivity;
import com.zerodsoft.scheduleweather.calendar.CalendarProvider;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.etc.LocationType;
import com.zerodsoft.scheduleweather.event.common.repository.LocationRepository;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.MsrstnAcctoRltmMesureDnsty.MsrstnAcctoRltmMesureDnstyItem;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.weather.dataprocessing.WeatherDataConverter;
import com.zerodsoft.scheduleweather.weather.aircondition.airconditionbar.AirConditionResult;
import com.zerodsoft.scheduleweather.weather.aircondition.airconditionbar.BarInitDataCreater;
import com.zerodsoft.scheduleweather.weather.common.WeatherDataCallback;
import com.zerodsoft.scheduleweather.weather.dataprocessing.AirConditionProcessing;
import com.zerodsoft.scheduleweather.weather.dataprocessing.UltraSrtNcstProcessing;
import com.zerodsoft.scheduleweather.weather.repository.AreaCodeRepository;
import com.zerodsoft.scheduleweather.weather.ultrasrtncst.UltraSrtNcstFinalData;
import com.zerodsoft.scheduleweather.weather.ultrasrtncst.UltraSrtNcstResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventAlarmReceiver extends BroadcastReceiver {
	public static final String CHANNEL_ID = "channel_id";
	public static final int NOTIFICATION_ID = 500;
	public static final String ALARM_NOTIFICATION_CLICK_ACTION = "ALARM_NOTIFICATION_CLICK_ACTION";

	private boolean isScreenOn(Context context) {
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		return pm.isInteractive();
	}

	private boolean checkDeviceLock(Context context) {
		KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
		return myKM.inKeyguardRestrictedInputMode();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		//전달받는 값 : alarmTime, android.intent.extra.ALARM_COUNT
		if (intent.getAction().equals("android.intent.action.EVENT_REMINDER")) {
			if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
				return;
			}

			final Long alarmTime = intent.getExtras().getLong(CalendarAlerts.ALARM_TIME);

			final String selection = CalendarAlerts.ALARM_TIME + " = ?";
			final String[] selectionArgs = {alarmTime.toString()};

			Cursor cursor = context.getContentResolver().query(CalendarAlerts.CONTENT_URI, null, selection, selectionArgs, null);
			List<ContentValues> calendarAlertsList = new ArrayList<>();

			while (cursor.moveToNext()) {
				ContentValues calendarAlert = new ContentValues();
				calendarAlertsList.add(calendarAlert);

				calendarAlert.put(CalendarAlerts.EVENT_ID, cursor.getLong(cursor.getColumnIndex(CalendarAlerts.EVENT_ID)));
				calendarAlert.put(CalendarAlerts.BEGIN, cursor.getLong(cursor.getColumnIndex(CalendarAlerts.BEGIN)));
				calendarAlert.put(CalendarAlerts.CALENDAR_ID, cursor.getInt(cursor.getColumnIndex(CalendarAlerts.CALENDAR_ID)));
				calendarAlert.put(CalendarAlerts.ALARM_TIME, cursor.getLong(cursor.getColumnIndex(CalendarAlerts.ALARM_TIME)));
			}
			cursor.close();

			List<ContentValues> instanceList = getInstanceList(context, calendarAlertsList);
			if (instanceList.isEmpty()) {
				return;
			}
			setNotifications(context, instanceList);

			//화면이 켜져있으면 notification, 잠겨있으면 : activity, notification
			if (checkDeviceLock(context)) {
				try {
					intent = new Intent(context, AlarmActivity.class);
					Bundle bundle = new Bundle();
					bundle.putParcelableArrayList("instanceList", (ArrayList<? extends Parcelable>) instanceList);
					intent.putExtras(bundle);

					PendingIntent pi = PendingIntent.getActivity(context, 0, intent,
							PendingIntent.FLAG_ONE_SHOT);
					pi.send();
				} catch (PendingIntent.CanceledException e) {
					e.printStackTrace();
				}
			}
		}
	}


	private void setNotifications(Context context, List<ContentValues> instanceList) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		final long alarmTime = instanceList.get(0).getAsLong(CalendarAlerts.ALARM_TIME);
		int requestCode = (int) System.currentTimeMillis();

		for (ContentValues instance : instanceList) {
			Intent activityIntent = new Intent(context, AppMainActivity.class);
			activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

			activityIntent.putExtra(Instances.CALENDAR_ID, instance.getAsInteger(CalendarAlerts.CALENDAR_ID));
			activityIntent.putExtra(Instances._ID, instance.getAsLong(Instances._ID));
			activityIntent.putExtra(Instances.EVENT_ID, instance.getAsLong(CalendarAlerts.EVENT_ID));
			activityIntent.putExtra(Instances.BEGIN, instance.getAsLong(CalendarAlerts.BEGIN));
			activityIntent.putExtra(Instances.END, instance.getAsLong(CalendarAlerts.END));
			activityIntent.setAction(ALARM_NOTIFICATION_CLICK_ACTION);

			PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode++, activityIntent,
					PendingIntent.FLAG_ONE_SHOT);

			Intent confirmEventIntent = new Intent(context, EventAlarmProcessingReceiver.class);
			confirmEventIntent.setAction(EventAlarmProcessingReceiver.ACTION_PROCESS_EVENT_STATUS);

			Bundle bundle = new Bundle();
			bundle.putLong(CalendarAlerts.EVENT_ID, instance.getAsLong(CalendarAlerts.EVENT_ID));
			bundle.putInt(CalendarAlerts.STATUS, CalendarAlerts.STATUS_CONFIRMED);
			confirmEventIntent.putExtras(bundle);

			PendingIntent confirmEventPendingIntent =
					PendingIntent.getBroadcast(context, 0, confirmEventIntent, 0);

			StringBuilder contentStringBuilder = new StringBuilder();
			String dateTime = EventUtil.getSimpleDateTime(context, instance);
			String description = instance.containsKey(CalendarAlerts.DESCRIPTION) ? instance.getAsString(CalendarAlerts.DESCRIPTION) :
					"";

			String location = instance.get(CalendarAlerts.EVENT_LOCATION) != null
					? instance.getAsString(CalendarAlerts.EVENT_LOCATION) : "위치 미설정";

			contentStringBuilder.append(dateTime).append("\n").append(description).append("\n").append(location);
			NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
			bigTextStyle.bigText(contentStringBuilder);

			NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
					.setStyle(bigTextStyle)
					.setContentIntent(pendingIntent)
					.setSmallIcon(R.drawable.ic_launcher_background).setWhen(alarmTime).setAutoCancel(true)
					.setContentTitle(EventUtil.convertTitle(context, instance.getAsString(CalendarAlerts.TITLE)))
					.setContentText(contentStringBuilder.toString())
					.setPriority(Notification.PRIORITY_MAX)
					.setDefaults(NotificationCompat.DEFAULT_ALL)
					.addAction(R.drawable.check_icon, context.getString(R.string.check), confirmEventPendingIntent);

			notificationManager.notify(NOTIFICATION_ID, builder.build());

			/*
			if (instance.get(CalendarAlerts.EVENT_LOCATION) == null) {
				notifyNotificationHasNotLocation(notificationManager, builder, context, instance);
			} else {
				LocationRepository locationRepository = new LocationRepository(context);
				locationRepository.getLocation(
						instance.getAsLong(CalendarContract.CalendarAlerts.EVENT_ID), new DbQueryCallback<LocationDTO>() {
							@Override
							public void onResultSuccessful(LocationDTO result) {
								RemoteViews collapsedView = new RemoteViews(context.getPackageName(),
										R.layout.event_notification_small_view);
								RemoteViews expandedView = new RemoteViews(context.getPackageName(),
										R.layout.event_notification_big_view);

								notifyNotificationHasLocation(notificationManager, builder, context, collapsedView, expandedView,
										result, instance);
							}

							@Override
							public void onResultNoData() {
								notifyNotificationHasNotLocation(notificationManager, builder, context, instance);
							}
						});
			}

			 */
		}
	}

	private void notifyNotificationHasLocation(NotificationManager notificationManager, NotificationCompat.Builder builder, Context context,
	                                           RemoteViews smallView, RemoteViews bigView, LocationDTO locationDTO, ContentValues instance) {
		setInstanceData(instance, locationDTO, smallView, bigView, context);
		builder.setStyle(new NotificationCompat.DecoratedCustomViewStyle());
		builder.setCustomBigContentView(smallView);
		builder.setCustomBigContentView(bigView);
		notificationManager.notify(NOTIFICATION_ID, builder.build());

		/*
		AreaCodeRepository areaCodeRepository = new AreaCodeRepository(context);

		areaCodeRepository.getCodeOfProximateArea(Double.parseDouble(locationDTO.getLatitude()), Double.parseDouble(locationDTO.getLongitude())
				, new DbQueryCallback<WeatherAreaCodeDTO>() {
					@Override
					public void onResultSuccessful(WeatherAreaCodeDTO weatherAreaCodeResultDto) {
						UltraSrtNcstProcessing ultraSrtNcstProcessing = new UltraSrtNcstProcessing(context, weatherAreaCodeResultDto.getY(),
								weatherAreaCodeResultDto.getX());
						AirConditionProcessing airConditionProcessing = new AirConditionProcessing(context, weatherAreaCodeResultDto.getY(),
								weatherAreaCodeResultDto.getX());

						setWeatherData(airConditionProcessing, ultraSrtNcstProcessing, bigView, context, builder, notificationManager);
					}

					@Override
					public void onResultNoData() {

					}
				});
		 */
	}

	private void setWeatherData(AirConditionProcessing airConditionProcessing, UltraSrtNcstProcessing ultraSrtNcstProcessing, RemoteViews bigView,
	                            Context context,
	                            NotificationCompat.Builder builder, NotificationManager notificationManager) {
		ultraSrtNcstProcessing.getWeatherData(new WeatherDataCallback<UltraSrtNcstResult>() {
			@Override
			public void isSuccessful(UltraSrtNcstResult e) {
				setUltraSrtNcstData(e, bigView, context);
				setAirconditionData(airConditionProcessing, bigView, context, builder, notificationManager);
			}

			@Override
			public void isFailure(Exception e) {
				//ncst error
				setUltraSrtNcstData(null, bigView, context);
				setAirconditionData(airConditionProcessing, bigView, context, builder, notificationManager);
			}
		});
	}

	private void setAirconditionData(AirConditionProcessing airConditionProcessing, RemoteViews bigView, Context context,
	                                 NotificationCompat.Builder builder, NotificationManager notificationManager) {
		airConditionProcessing.getWeatherData(new WeatherDataCallback<AirConditionResult>() {
			@Override
			public void isSuccessful(AirConditionResult e) {
				setAirconditionData(e, bigView, context);

				builder.setCustomBigContentView(bigView);
				notificationManager.notify(NOTIFICATION_ID, builder.build());
			}

			@Override
			public void isFailure(Exception e) {
				//aircondition error
				setAirconditionData(null, bigView, context);

				builder.setCustomBigContentView(bigView);
				notificationManager.notify(NOTIFICATION_ID, builder.build());
			}
		});
	}

	private void setInstanceData(ContentValues contentValues, LocationDTO locationDTO, RemoteViews smallView, RemoteViews bigView, Context context) {
		String title = EventUtil.convertTitle(context, contentValues.getAsString(CalendarAlerts.TITLE));

		Date beginDate = new Date(contentValues.getAsLong(CalendarAlerts.BEGIN));
		Date endDate = new Date(contentValues.getAsLong(CalendarAlerts.END));

		String begin = contentValues.getAsBoolean(CalendarAlerts.ALL_DAY) ? ClockUtil.YYYY_M_D_E.format(beginDate)
				: ClockUtil.DATE_FORMAT_NOT_ALLDAY.format(beginDate);
		String end = contentValues.getAsBoolean(CalendarAlerts.ALL_DAY) ? ClockUtil.YYYY_M_D_E.format(endDate)
				: ClockUtil.DATE_FORMAT_NOT_ALLDAY.format(endDate);

		String dateTime = begin + " - " + end;

		String locationName = locationDTO.getLocationType() == LocationType.PLACE ? locationDTO.getPlaceName() :
				locationDTO.getAddressName();

		smallView.setTextViewText(R.id.instance_title, title);
		smallView.setTextViewText(R.id.instance_datetime, dateTime);

		bigView.setTextViewText(R.id.instance_title, title);
		bigView.setTextViewText(R.id.instance_datetime, dateTime);
		bigView.setTextViewText(R.id.instance_location, locationName);
	}

	private void setUltraSrtNcstData(UltraSrtNcstResult ultraSrtNcstResult, RemoteViews remoteViews, Context context) {
		String temp = null;
		String humidity = null;
		String wind = null;
		String rain = null;

		if (ultraSrtNcstResult == null) {
			temp = context.getString(R.string.error);
			humidity = context.getString(R.string.error);
			wind = context.getString(R.string.error);
			rain = context.getString(R.string.error);
		} else {
			UltraSrtNcstFinalData finalData = ultraSrtNcstResult.getUltraSrtNcstFinalData();
			WeatherDataConverter.context = context;

			temp = finalData.getTemperature();
			humidity = finalData.getHumidity();
			wind = finalData.getWindSpeed() + "m/s, " + finalData.getWindDirection() + "\n" +
					WeatherDataConverter.getWindSpeedDescription(finalData.getWindSpeed());
			rain = finalData.getPrecipitation1Hour();
		}
		remoteViews.setTextViewText(R.id.temperature, temp);
		remoteViews.setTextViewText(R.id.humidity, humidity);
		remoteViews.setTextViewText(R.id.wind, wind);
		remoteViews.setTextViewText(R.id.rain, rain);
	}

	private void setAirconditionData(AirConditionResult airConditionResult, RemoteViews remoteViews, Context context) {
		String pm10 = "";
		String pm25 = "";

		if (airConditionResult == null) {
			pm10 = context.getString(R.string.error);
			pm25 = context.getString(R.string.error);
		} else {
			MsrstnAcctoRltmMesureDnstyItem finalData = airConditionResult.getAirConditionFinalData();
			//pm10
			if (finalData.getPm10Flag() == null) {
				pm10 = BarInitDataCreater.getGrade(finalData.getPm10Grade1h(), context) + ", " + finalData.getPm10Value()
						+ context.getString(R.string.finedust_unit);
				remoteViews.setTextColor(R.id.pm10_status, BarInitDataCreater.getGradeColor(finalData.getPm10Grade1h(), context));
			} else {
				pm10 = finalData.getPm10Flag();
			}

			//pm2.5
			if (finalData.getPm25Flag() == null) {
				pm25 = BarInitDataCreater.getGrade(finalData.getPm25Grade1h(), context) + ", " + finalData.getPm25Value()
						+ context.getString(R.string.finedust_unit);
				remoteViews.setTextColor(R.id.pm2_5_status, BarInitDataCreater.getGradeColor(finalData.getPm25Grade1h(), context));
			} else {
				pm25 = finalData.getPm25Flag();
			}
		}

		remoteViews.setTextViewText(R.id.pm10_status, pm10);
		remoteViews.setTextViewText(R.id.pm2_5_status, pm25);
	}


	private void notifyNotificationHasNotLocation(NotificationManager notificationManager, NotificationCompat.Builder builder,
	                                              Context context, ContentValues instance) {
		notificationManager.notify(NOTIFICATION_ID, builder.build());
	}


	private List<ContentValues> getInstanceList(Context context, List<ContentValues> calendarAlertsList) {
		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
		}
		final String selection = CalendarAlerts.EVENT_ID + " = ? AND " + CalendarAlerts.BEGIN + " = ?";
		final String[] selectionArgs = new String[2];
		Cursor cursor = null;

		for (ContentValues calendarAlert : calendarAlertsList) {
			selectionArgs[0] = calendarAlert.getAsString(CalendarAlerts.EVENT_ID);
			selectionArgs[1] = calendarAlert.getAsString(CalendarAlerts.BEGIN);
			cursor = context.getContentResolver().query(CalendarAlerts.CONTENT_URI_BY_INSTANCE, null, selection,
					selectionArgs, null);

			while (cursor.moveToNext()) {
				//title,begin,end,location,color
				String[] columnNames = cursor.getColumnNames();
				for (String columnName : columnNames) {
					if (!cursor.isNull(cursor.getColumnIndex(columnName))) {
						calendarAlert.put(columnName, cursor.getString(cursor.getColumnIndex(columnName)));
					}
				}

				calendarAlert.put(Instances._ID, getInstanceId(context, calendarAlert.getAsLong(CalendarAlerts.EVENT_ID),
						cursor.getLong(cursor.getColumnIndex(CalendarAlerts.BEGIN)),
						cursor.getLong(cursor.getColumnIndex(CalendarAlerts.END))));
			}
			cursor.close();
		}
		return calendarAlertsList;
	}

	private long getInstanceId(Context context, long eventId, long begin, long end) {
		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
		}
		String[] projection = {Instances._ID, Instances.EVENT_ID, Instances.BEGIN, Instances.END};

		Cursor cursor = CalendarContract.Instances.query(context.getContentResolver(), projection, begin, end);
		long instanceId = 0L;
		while (cursor.moveToNext()) {
			if (cursor.getLong(1) == eventId && cursor.getLong(2) == begin && cursor.getLong(3) == end) {
				instanceId = cursor.getLong(0);
				break;
			}
		}
		cursor.close();

		return instanceId;
	}

	public static void createNotificationChannel(Context context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
					context.getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_HIGH);
			notificationChannel.enableLights(true);
			notificationChannel.setLightColor(Color.RED);
			notificationChannel.enableVibration(true);
			notificationChannel.setDescription(context.getString(R.string.notification_channel_description));

			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.createNotificationChannel(notificationChannel);
		}
	}

      /*
    public void init()
    {
        ComponentName receiver = new ComponentName(context, AppBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

     */
}
