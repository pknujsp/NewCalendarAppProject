package com.zerodsoft.scheduleweather.notification;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;

import androidx.core.app.ActivityCompat;

import com.zerodsoft.scheduleweather.notification.receiver.AppBootReceiver;
import com.zerodsoft.scheduleweather.notification.receiver.EventAlarmReceiver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventNotification
{
    private static EventNotification instance;
    public static final int REQUEST_CODE = 4000;

    public EventNotification()
    {
    }

    public static EventNotification newInstance()
    {
        instance = new EventNotification();
        return instance;
    }

    public static EventNotification getInstance()
    {
        return instance;
    }

    public void setEventNotifications(Context context)
    {
        /*
        List<ContentValues> reminderList = getEventReminders(context);
        List<ContentValues> eventList = getEventList(context, reminderList);
        List<ContentValues> instanceList = getInstanceList(context, eventList);

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);


        Intent intent = new Intent("com.zerodsoft.scheduleweather.EVENT_ALARM");
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent
                , PendingIntent.FLAG_UPDATE_CURRENT);

        if (alarmManager != null)
        {
            if (alarmManager.getNextAlarmClock() != null)
            {
                return;
            }
            alarmManager.cancel(alarmIntent);
        }

        Calendar calendar = Calendar.getInstance();
        for (ContentValues instance : instanceList)
        {
            calendar.setTimeInMillis(instance.getAsLong(CalendarContract.Instances.BEGIN));
            calendar.add(Calendar.MINUTE, -instance.getAsInteger(CalendarContract.Reminders.MINUTES));
            Date date = calendar.getTime();

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    alarmIntent);
        }
         */
    }

    private List<ContentValues> getEventReminders(Context context)
    {
        String selection = CalendarContract.Reminders.METHOD + " = ?";
        String[] selectionArgs = {String.valueOf(CalendarContract.Reminders.METHOD_ALERT)};
        //id, eventid, minutes, method

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED)
        {

        }
        Cursor cursor = context.getContentResolver().query(CalendarContract.Reminders.CONTENT_URI, null, selection, selectionArgs, null);
        List<ContentValues> eventList = new ArrayList<>();
        while (cursor.moveToNext())
        {
            ContentValues reminder = new ContentValues();
            eventList.add(reminder);

            reminder.put(CalendarContract.Reminders.EVENT_ID, cursor.getLong(cursor.getColumnIndex(CalendarContract.Reminders.EVENT_ID)));
            reminder.put(CalendarContract.Reminders._ID, cursor.getLong(cursor.getColumnIndex(CalendarContract.Reminders._ID)));
            reminder.put(CalendarContract.Reminders.METHOD, cursor.getInt(cursor.getColumnIndex(CalendarContract.Reminders.METHOD)));
            reminder.put(CalendarContract.Reminders.MINUTES, cursor.getInt(cursor.getColumnIndex(CalendarContract.Reminders.MINUTES)));
        }

        cursor.close();
        return eventList;
    }

    private List<ContentValues> getEventList(Context context, List<ContentValues> reminderList)
    {
        String selection = CalendarContract.Events._ID + " = ?";
        String[] selectionArgs = new String[1];

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED)
        {

        }
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = null;
        List<ContentValues> eventList = new ArrayList<>();

        for (ContentValues reminder : reminderList)
        {
            selectionArgs[0] = String.valueOf(reminder.getAsLong(CalendarContract.Reminders.EVENT_ID));
            cursor = contentResolver.query(CalendarContract.Events.CONTENT_URI, null, selection, selectionArgs, null);

            while (cursor.moveToNext())
            {
                ContentValues event = new ContentValues();
                eventList.add(event);

                event.put(CalendarContract.Events.LAST_DATE, cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.LAST_DATE)));
                event.put(CalendarContract.Events.DTSTART, cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTSTART)));
                event.put(CalendarContract.Events._ID, cursor.getLong(cursor.getColumnIndex(CalendarContract.Events._ID)));
                event.put(CalendarContract.Reminders.MINUTES, reminder.getAsInteger(CalendarContract.Reminders.MINUTES));
            }

            cursor.close();
        }
        return eventList;
    }

    private List<ContentValues> getInstanceList(Context context, List<ContentValues> eventList)
    {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED)
        {

        }
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = null;
        List<ContentValues> instanceList = new ArrayList<>();

        long eventId = 0L;
        long begin = 0L;
        long end = 0L;

        Set<Long> eventIdSet = new HashSet<>();
        for (ContentValues event : eventList)
        {
            eventIdSet.add(event.getAsLong(CalendarContract.Events._ID));
        }

        for (ContentValues event : eventList)
        {
            eventId = event.getAsLong(CalendarContract.Events._ID);
            begin = event.getAsLong(CalendarContract.Events.DTSTART);
            end = event.getAsLong(CalendarContract.Events.LAST_DATE);

            if (begin > end)
            {
                continue;
            }
            cursor = CalendarContract.Instances.query(contentResolver, null, begin, end);
            while (cursor.moveToNext())
            {
                if (eventIdSet.contains(cursor.getLong(cursor.getColumnIndex(CalendarContract.Instances.EVENT_ID))))
                {
                    ContentValues instance = new ContentValues();
                    instanceList.add(instance);

                    instance.put(CalendarContract.Instances.BEGIN, cursor.getLong(cursor.getColumnIndex(CalendarContract.Instances.BEGIN)));
                    instance.put(CalendarContract.Instances._ID, cursor.getLong(cursor.getColumnIndex(CalendarContract.Instances._ID)));
                    instance.put(CalendarContract.Reminders.MINUTES, event.getAsInteger(CalendarContract.Reminders.MINUTES));

                    Date date = new Date(instance.getAsLong(CalendarContract.Instances.BEGIN));
                    date.toString();
                    break;
                }
            }

            cursor.close();
        }
        return instanceList;
    }
}
