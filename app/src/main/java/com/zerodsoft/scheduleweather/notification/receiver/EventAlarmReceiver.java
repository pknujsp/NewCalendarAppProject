package com.zerodsoft.scheduleweather.notification.receiver;

import android.Manifest;
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
import android.provider.CalendarContract;
import android.widget.RemoteViews;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.etc.LocationType;
import com.zerodsoft.scheduleweather.event.common.repository.LocationRepository;
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

	@Override
	public void onReceive(Context context, Intent intent) {
		//alarmTime, android.intent.extra.ALARM_COUNT

		if (intent.getAction().equals("android.intent.action.EVENT_REMINDER")) {
			if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
				return;
			}
			final String selection = CalendarContract.CalendarAlerts.ALARM_TIME + " = ?";
			final String[] selectionArgs = {String.valueOf(intent.getExtras().getLong(CalendarContract.CalendarAlerts.ALARM_TIME))};
			Cursor cursor = context.getContentResolver().query(CalendarContract.CalendarAlerts.CONTENT_URI, null, selection, selectionArgs,
					null);
			List<ContentValues> contentValuesList = new ArrayList<>();

			while (cursor.moveToNext()) {
				ContentValues contentValues = new ContentValues();
				contentValuesList.add(contentValues);

				contentValues.put(CalendarContract.CalendarAlerts.EVENT_ID,
						cursor.getLong(cursor.getColumnIndex(CalendarContract.CalendarAlerts.EVENT_ID)));
				contentValues.put(CalendarContract.CalendarAlerts.BEGIN,
						cursor.getLong(cursor.getColumnIndex(CalendarContract.CalendarAlerts.BEGIN)));
				contentValues.put(CalendarContract.CalendarAlerts.CALENDAR_ID,
						cursor.getInt(cursor.getColumnIndex(CalendarContract.CalendarAlerts.CALENDAR_ID)));
				contentValues.put(CalendarContract.CalendarAlerts.ALARM_TIME,
						cursor.getLong(cursor.getColumnIndex(CalendarContract.CalendarAlerts.ALARM_TIME)));
			}
			cursor.close();

			List<ContentValues> instanceList = getInstanceList(context, contentValuesList);
			setNotifications(context, instanceList);
		}
	}

	private void setNotifications(Context context, List<ContentValues> instanceList) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		final long alarmTime = instanceList.get(0).getAsLong(CalendarContract.CalendarAlerts.ALARM_TIME);
		int requestCode = (int) System.currentTimeMillis();

		for (ContentValues instance : instanceList) {
			Intent activityIntent = new Intent(context, null);
			activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

			activityIntent.putExtra(CalendarContract.Instances.CALENDAR_ID, instance.getAsInteger(CalendarContract.CalendarAlerts.CALENDAR_ID));
			activityIntent.putExtra(CalendarContract.Instances._ID, instance.getAsLong(CalendarContract.Instances._ID));
			activityIntent.putExtra(CalendarContract.Instances.EVENT_ID, instance.getAsLong(CalendarContract.CalendarAlerts.EVENT_ID));
			activityIntent.putExtra(CalendarContract.Instances.BEGIN, instance.getAsLong(CalendarContract.CalendarAlerts.BEGIN));
			activityIntent.putExtra(CalendarContract.Instances.END, instance.getAsLong(CalendarContract.CalendarAlerts.END));

			PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode++, activityIntent,
					PendingIntent.FLAG_ONE_SHOT);

			NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID).setSmallIcon(
					R.drawable.sunny_day_icon).setContentIntent(pendingIntent).setWhen(
					alarmTime).setAutoCancel(true)
					.setContentTitle(instance.getAsString(CalendarContract.CalendarAlerts.TITLE).isEmpty() ? context.getString(
							R.string.empty_title) : instance.getAsString(CalendarContract.CalendarAlerts.TITLE)).setContentText(
							instance.containsKey(CalendarContract.CalendarAlerts.EVENT_LOCATION) ? instance.getAsString(
									CalendarContract.CalendarAlerts.EVENT_LOCATION) : "위치 미설정")
					.setPriority(Notification.PRIORITY_MAX)
					.setFullScreenIntent(pendingIntent, true)
					.setDefaults(NotificationCompat.DEFAULT_ALL);

			if (instance.getAsString(CalendarContract.CalendarAlerts.EVENT_LOCATION) == null) {
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
		}
	}

	private void notifyNotificationHasLocation(NotificationManager notificationManager, NotificationCompat.Builder builder, Context context,
	                                           RemoteViews smallView, RemoteViews bigView, LocationDTO locationDTO, ContentValues instance) {
		setInstanceData(instance, locationDTO, smallView, bigView, context);
		builder.setStyle(new NotificationCompat.DecoratedCustomViewStyle());
		builder.setCustomBigContentView(smallView);

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
		String title = contentValues.getAsString(CalendarContract.CalendarAlerts.TITLE).isEmpty() ? context.getString(
				R.string.empty_title) : contentValues.getAsString(CalendarContract.CalendarAlerts.TITLE);

		Date beginDate = new Date(contentValues.getAsLong(CalendarContract.CalendarAlerts.BEGIN));
		Date endDate = new Date(contentValues.getAsLong(CalendarContract.CalendarAlerts.END));

		String begin = contentValues.getAsBoolean(CalendarContract.CalendarAlerts.ALL_DAY) ? ClockUtil.YYYY_M_D_E.format(beginDate)
				: ClockUtil.DATE_FORMAT_NOT_ALLDAY.format(beginDate);
		String end = contentValues.getAsBoolean(CalendarContract.CalendarAlerts.ALL_DAY) ? ClockUtil.YYYY_M_D_E.format(endDate)
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


	private List<ContentValues> getInstanceList(Context context, List<ContentValues> contentValuesList) {
		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
		}
		final String selection = CalendarContract.CalendarAlerts.EVENT_ID + " = ? AND " + CalendarContract.CalendarAlerts.BEGIN + " = ?";
		final String[] selectionArgs = new String[2];
		Cursor cursor = null;

		for (ContentValues contentValues : contentValuesList) {
			selectionArgs[0] = contentValues.getAsString(CalendarContract.CalendarAlerts.EVENT_ID);
			selectionArgs[1] = contentValues.getAsString(CalendarContract.CalendarAlerts.BEGIN);
			cursor = context.getContentResolver().query(CalendarContract.CalendarAlerts.CONTENT_URI_BY_INSTANCE, null, selection,
					selectionArgs, null);

			while (cursor.moveToNext()) {
				//title,begin,end,location,color
				contentValues.put(CalendarContract.CalendarAlerts.TITLE,
						cursor.getString(cursor.getColumnIndex(CalendarContract.CalendarAlerts.TITLE)));
				contentValues.put(CalendarContract.CalendarAlerts.ALL_DAY,
						cursor.getInt(cursor.getColumnIndex(CalendarContract.CalendarAlerts.ALL_DAY)));
				contentValues.put(CalendarContract.CalendarAlerts.EVENT_LOCATION,
						cursor.getString(cursor.getColumnIndex(CalendarContract.CalendarAlerts.EVENT_LOCATION)));
				contentValues.put(CalendarContract.CalendarAlerts.EVENT_COLOR,
						cursor.getInt(cursor.getColumnIndex(CalendarContract.CalendarAlerts.EVENT_COLOR)));
				contentValues.put(CalendarContract.CalendarAlerts.BEGIN,
						cursor.getLong(cursor.getColumnIndex(CalendarContract.CalendarAlerts.BEGIN)));
				contentValues.put(CalendarContract.CalendarAlerts.END,
						cursor.getLong(cursor.getColumnIndex(CalendarContract.CalendarAlerts.END)));
				contentValues.put(CalendarContract.Instances._ID,
						getInstanceId(context, contentValues.getAsLong(CalendarContract.CalendarAlerts.EVENT_ID),
								cursor.getLong(cursor.getColumnIndex(CalendarContract.CalendarAlerts.BEGIN)),
								cursor.getLong(cursor.getColumnIndex(CalendarContract.CalendarAlerts.END))));
			}
			cursor.close();
		}
		return contentValuesList;
	}

	private long getInstanceId(Context context, long eventId, long begin, long end) {
		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
		}
		String[] projection = {CalendarContract.Instances._ID, CalendarContract.Instances.EVENT_ID, CalendarContract.Instances.BEGIN,
				CalendarContract.Instances.END};

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
