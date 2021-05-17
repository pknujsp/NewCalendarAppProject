package com.zerodsoft.scheduleweather.notification.receiver;

import android.Manifest;
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
import android.provider.CalendarContract;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.database.CursorWindowCompat;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.activity.main.AppMainActivity;
import com.zerodsoft.scheduleweather.event.main.NewInstanceMainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EventAlarmReceiver extends BroadcastReceiver
{
    public static final String CHANNEL_ID = "channel_id";
    public static final int NOTIFICATION_ID = 500;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        //alarmTime, android.intent.extra.ALARM_COUNT

        if (intent.getAction().equals("android.intent.action.EVENT_REMINDER"))
        {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED)
            {
                return;
            }
            String selection = CalendarContract.CalendarAlerts.ALARM_TIME + " = ?";
            String[] selectionArgs = {String.valueOf(intent.getExtras().getLong(CalendarContract.CalendarAlerts.ALARM_TIME))};
            Cursor cursor = context.getContentResolver().query(CalendarContract.CalendarAlerts.CONTENT_URI, null, selection, selectionArgs, null);
            List<ContentValues> contentValuesList = new ArrayList<>();

            while (cursor.moveToNext())
            {
                ContentValues contentValues = new ContentValues();
                contentValuesList.add(contentValues);

                contentValues.put(CalendarContract.CalendarAlerts.ALARM_TIME, cursor.getLong(cursor.getColumnIndex(CalendarContract.CalendarAlerts.ALARM_TIME)));
                contentValues.put(CalendarContract.CalendarAlerts.EVENT_ID, cursor.getLong(cursor.getColumnIndex(CalendarContract.CalendarAlerts.EVENT_ID)));
                contentValues.put(CalendarContract.CalendarAlerts.BEGIN, cursor.getLong(cursor.getColumnIndex(CalendarContract.CalendarAlerts.BEGIN)));
                contentValues.put(CalendarContract.CalendarAlerts.CALENDAR_ID, cursor.getInt(cursor.getColumnIndex(CalendarContract.CalendarAlerts.CALENDAR_ID)));
            }
            cursor.close();

            List<ContentValues> instanceList = getInstanceList(context, contentValuesList);
            setNotifications(context, instanceList);
        }
        /*
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            App.initNotifications(context);
        } else if (intent.getAction().equals("com.zerodsoft.scheduleweather.EVENT_ALARM"))
        {
            ContentValues instanceData = intent.getParcelableExtra("instance_data");
            Log.e("NOTIFICATION", "NOTIFIED");
            if (instanceData == null)
            {
                return;
            }

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent activityIntent = new Intent(context, AppMainActivity.class);
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle(context.getString(R.string.event_notification_title))
                    .setContentText(context.getString(R.string.event_notification_text))
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }

         */
    }

    private void setNotifications(Context context, List<ContentValues> instanceList)
    {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        for (ContentValues instance : instanceList)
        {
            Intent activityIntent = new Intent(context, NewInstanceMainActivity.class);
            Bundle bundle = new Bundle();

            bundle.putInt(CalendarContract.Instances.CALENDAR_ID, instance.getAsInteger(CalendarContract.CalendarAlerts.CALENDAR_ID));
            bundle.putLong(CalendarContract.Instances._ID, instance.getAsLong(CalendarContract.Instances._ID));
            bundle.putLong(CalendarContract.Instances.EVENT_ID, instance.getAsLong(CalendarContract.CalendarAlerts.EVENT_ID));
            bundle.putLong(CalendarContract.Instances.BEGIN, instance.getAsLong(CalendarContract.CalendarAlerts.BEGIN));
            bundle.putLong(CalendarContract.Instances.END, instance.getAsLong(CalendarContract.CalendarAlerts.END));
            activityIntent.putExtras(bundle);

            activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle(instance.getAsString(CalendarContract.CalendarAlerts.TITLE).isEmpty() ? context.getString(R.string.empty_title)
                            : instance.getAsString(CalendarContract.CalendarAlerts.TITLE))
                    .setContentText(instance.containsKey(CalendarContract.CalendarAlerts.EVENT_LOCATION) ? instance.getAsString(CalendarContract.CalendarAlerts.EVENT_LOCATION)
                            : "위치 미설정")
                    .setSmallIcon(R.drawable.sunny_day_icon)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    private List<ContentValues> getInstanceList(Context context, List<ContentValues> contentValuesList)
    {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED)
        {
        }
        String selection = CalendarContract.CalendarAlerts.BEGIN + " = ? AND " +
                CalendarContract.CalendarAlerts.EVENT_ID + " = ?";
        String[] selectionArgs = new String[2];
        Cursor cursor = null;

        for (ContentValues contentValues : contentValuesList)
        {
            selectionArgs[0] = contentValues.getAsString(CalendarContract.CalendarAlerts.BEGIN);
            selectionArgs[1] = contentValues.getAsString(CalendarContract.CalendarAlerts.EVENT_ID);
            cursor = context.getContentResolver().query(CalendarContract.CalendarAlerts.CONTENT_URI_BY_INSTANCE, null, selection, selectionArgs, null);

            while (cursor.moveToNext())
            {
                //title,begin,end,location,color
                contentValues.put(CalendarContract.CalendarAlerts.TITLE, cursor.getString(cursor.getColumnIndex(CalendarContract.CalendarAlerts.TITLE)));
                contentValues.put(CalendarContract.CalendarAlerts.END, cursor.getLong(cursor.getColumnIndex(CalendarContract.CalendarAlerts.END)));
                contentValues.put(CalendarContract.CalendarAlerts.EVENT_LOCATION, cursor.getString(cursor.getColumnIndex(CalendarContract.CalendarAlerts.EVENT_LOCATION)));
                contentValues.put(CalendarContract.CalendarAlerts.EVENT_COLOR, cursor.getInt(cursor.getColumnIndex(CalendarContract.CalendarAlerts.EVENT_COLOR)));
                contentValues.put(CalendarContract.Instances._ID, getInstanceId(context, contentValues.getAsLong(CalendarContract.CalendarAlerts.EVENT_ID)
                        , contentValues.getAsLong(CalendarContract.CalendarAlerts.BEGIN)
                        , contentValues.getAsLong(CalendarContract.CalendarAlerts.BEGIN) + 100000L));
            }
            cursor.close();
        }
        return contentValuesList;
    }

    private long getInstanceId(Context context, long eventId, long begin, long end)
    {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED)
        {
        }
        String[] projection = {CalendarContract.Instances._ID, CalendarContract.Instances.EVENT_ID};

        Cursor cursor = CalendarContract.Instances.query(context.getContentResolver(), projection, begin, end);
        long instanceId = 0L;
        while (cursor.moveToNext())
        {
            if (cursor.getLong(1) == eventId)
            {
                instanceId = cursor.getLong(0);
            }
        }
        cursor.close();

        return instanceId;
    }

    public static void createNotificationChannel(Context context)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, context.getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_DEFAULT);
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
