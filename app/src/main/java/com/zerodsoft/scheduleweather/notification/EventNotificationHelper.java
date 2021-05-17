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
import android.provider.CalendarContract;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.AlarmManagerCompat;

import com.zerodsoft.scheduleweather.notification.receiver.EventAlarmReceiver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventNotificationHelper
{
    private static EventNotificationHelper instance;

    public EventNotificationHelper()
    {
    }

    public static EventNotificationHelper newInstance()
    {
        instance = new EventNotificationHelper();
        return instance;
    }

    public static EventNotificationHelper getInstance()
    {
        return instance;
    }

    public List<ContentValues> checkEventNotificationsIsSet(Context context, List<ContentValues> instanceList)
    {
        List<ContentValues> notSetList = new ArrayList<>();

        for (ContentValues instance : instanceList)
        {
            int requestCode = instance.getAsInteger(CalendarContract.Reminders._ID);
            if (PendingIntent.getBroadcast(context, requestCode,
                    new Intent("com.zerodsoft.scheduleweather.EVENT_ALARM"),
                    PendingIntent.FLAG_NO_CREATE) == null)
            {
                notSetList.add(instance);
            }
        }
        return notSetList;
    }

    public void setEventNotifications(Context context)
    {
        List<ContentValues> reminderList = getReminders(context);
        List<ContentValues> eventList = getEventList(context, reminderList);
        List<ContentValues> instanceList = getInstanceList(context, eventList);

        List<ContentValues> notSetInstanceList = checkEventNotificationsIsSet(context, instanceList);
        setEventNotifications(context, notSetInstanceList);
    }

    public void setEventNotifications(Context context, long eventId)
    {
        List<ContentValues> reminderList = getReminders(context, eventId);
        List<ContentValues> eventList = getEventList(context, reminderList);
        List<ContentValues> instanceList = getInstanceList(context, eventList);

        setEventNotifications(context, instanceList);
    }

    public void setEventNotifications(Context context, List<ContentValues> instanceList)
    {
        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        StringBuilder date = new StringBuilder("Added : \n");

        Calendar calendar = Calendar.getInstance();
        for (ContentValues instance : instanceList)
        {
            calendar.setTimeInMillis(instance.getAsLong(CalendarContract.Instances.BEGIN));
            calendar.add(Calendar.MINUTE, -instance.getAsInteger(CalendarContract.Reminders.MINUTES));

            Intent intent = new Intent(context, EventAlarmReceiver.class);
            intent.setAction("com.zerodsoft.scheduleweather.EVENT_ALARM");
            intent.putExtra("instance_data", instance);

            int requestCode = instance.getAsInteger(CalendarContract.Reminders._ID);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, requestCode, intent
                    , PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManagerCompat.setExactAndAllowWhileIdle(alarmManager, AlarmManager.RTC_WAKEUP
                    , calendar.getTimeInMillis(), alarmIntent);

            Date date1 = calendar.getTime();

            date.append(date1.toString()).append("\n");
        }
        Toast.makeText(context, date.toString(), Toast.LENGTH_SHORT).show();
    }


    private List<ContentValues> getReminders(Context context, String selection, String[]
            selectionArgs)
    {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED)
        {

        }

        Cursor cursor = context.getContentResolver().query(CalendarContract.Reminders.CONTENT_URI, null, selection, selectionArgs, null);
        List<ContentValues> reminderList = new ArrayList<>();
        while (cursor.moveToNext())
        {
            ContentValues reminder = new ContentValues();
            reminderList.add(reminder);

            reminder.put(CalendarContract.Reminders.EVENT_ID, cursor.getLong(cursor.getColumnIndex(CalendarContract.Reminders.EVENT_ID)));
            reminder.put(CalendarContract.Reminders._ID, cursor.getLong(cursor.getColumnIndex(CalendarContract.Reminders._ID)));
            reminder.put(CalendarContract.Reminders.METHOD, cursor.getInt(cursor.getColumnIndex(CalendarContract.Reminders.METHOD)));
            reminder.put(CalendarContract.Reminders.MINUTES, cursor.getInt(cursor.getColumnIndex(CalendarContract.Reminders.MINUTES)));
        }

        cursor.close();
        return reminderList;
    }

    private List<ContentValues> getReminders(Context context, long eventId)
    {
        String selection = CalendarContract.Reminders.METHOD + " = ? AND "
                + CalendarContract.Reminders.EVENT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(CalendarContract.Reminders.METHOD_ALERT), String.valueOf(eventId)};
        //id, eventid, minutes, method

        return getReminders(context, selection, selectionArgs);
    }

    private List<ContentValues> getReminders(Context context)
    {
        String selection = CalendarContract.Reminders.METHOD + " = ?";
        String[] selectionArgs = {String.valueOf(CalendarContract.Reminders.METHOD_ALERT)};
        //id, eventid, minutes, method

        return getReminders(context, selection, selectionArgs);
    }

    private List<ContentValues> getEventList(Context
                                                     context, List<ContentValues> reminderList)
    {
        String selection = CalendarContract.Events._ID + " = ?";
        String[] selectionArgs = new String[1];

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED)
        {

        }
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = null;

        for (ContentValues reminder : reminderList)
        {
            selectionArgs[0] = String.valueOf(reminder.getAsLong(CalendarContract.Reminders.EVENT_ID));
            cursor = contentResolver.query(CalendarContract.Events.CONTENT_URI, null, selection, selectionArgs, null);

            while (cursor.moveToNext())
            {
                reminder.put(CalendarContract.Events.LAST_DATE, cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.LAST_DATE)));
                reminder.put(CalendarContract.Events.DTSTART, cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTSTART)));
            }

            cursor.close();
        }
        return reminderList;
    }

    private List<ContentValues> getInstanceList(Context
                                                        context, List<ContentValues> eventList)
    {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED)
        {

        }

        Set<Long> eventIdSet = new HashSet<>();
        for (ContentValues event : eventList)
        {
            eventIdSet.add(event.getAsLong(CalendarContract.Reminders.EVENT_ID));
        }

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = null;

        long eventId = 0L;
        final long systemTime = System.currentTimeMillis();
        long end = 0L;

        List<ContentValues> instanceList = new ArrayList<>();
        for (ContentValues event : eventList)
        {
            eventId = event.getAsLong(CalendarContract.Reminders.EVENT_ID);
            end = event.getAsLong(CalendarContract.Events.LAST_DATE);

            if (systemTime >= end)
            {
                continue;
            }

            cursor = CalendarContract.Instances.query(contentResolver, null, systemTime, end);
            while (cursor.moveToNext())
            {
                if (eventIdSet.contains(cursor.getLong(cursor.getColumnIndex(CalendarContract.Instances.EVENT_ID))))
                {
                    ContentValues instance = new ContentValues();
                    instanceList.add(instance);
                    instance.putAll(event);
                    instance.put(CalendarContract.Instances.BEGIN, cursor.getLong(cursor.getColumnIndex(CalendarContract.Instances.BEGIN)));
                    instance.put(CalendarContract.Instances._ID, cursor.getLong(cursor.getColumnIndex(CalendarContract.Instances._ID)));
                    break;
                }
            }

            cursor.close();
        }

        return instanceList;
    }


}
