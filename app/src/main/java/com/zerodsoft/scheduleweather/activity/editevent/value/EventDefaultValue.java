package com.zerodsoft.scheduleweather.activity.editevent.value;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract;

import com.zerodsoft.scheduleweather.R;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class EventDefaultValue
{
    private Context context;
    private Calendar calendar;

    public EventDefaultValue(Context context)
    {
        this.context = context;
        calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
    }

    public String getDefaultTitle()
    {
        return context.getString(R.string.default_event_title);
    }

    public ContentValues getDefaultCalendar()
    {
        final String[] PROJECTION = {CalendarContract.Calendars._ID, CalendarContract.Calendars.NAME,
                CalendarContract.Calendars.ACCOUNT_NAME, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CalendarContract.Calendars.OWNER_ACCOUNT,
                CalendarContract.Calendars.CALENDAR_COLOR, CalendarContract.Calendars.IS_PRIMARY};

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(CalendarContract.Calendars.CONTENT_URI, PROJECTION, null, null, null);

        final String GOOGLE_SECONDARY_CALENDAR = "@group.calendar.google.com";
        ContentValues calendar = new ContentValues();

        while (cursor.moveToNext())
        {
            if (cursor.getInt(5) == 1)
            {
                // another || google primary calendar
                calendar.put(CalendarContract.Calendars._ID, cursor.getLong(0));
                calendar.put(CalendarContract.Calendars.NAME, cursor.getString(1));
                calendar.put(CalendarContract.Calendars.ACCOUNT_NAME, cursor.getString(2));
                calendar.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, cursor.getString(3));
                calendar.put(CalendarContract.Calendars.OWNER_ACCOUNT, cursor.getString(4));
                calendar.put(CalendarContract.Calendars.CALENDAR_COLOR, cursor.getInt(5));

                break;
            } else if (cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.OWNER_ACCOUNT)).contains(GOOGLE_SECONDARY_CALENDAR))
            {
                calendar.put(CalendarContract.Calendars._ID, cursor.getLong(0));
                calendar.put(CalendarContract.Calendars.NAME, cursor.getString(1));
                calendar.put(CalendarContract.Calendars.ACCOUNT_NAME, cursor.getString(2));
                calendar.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, cursor.getString(3));
                calendar.put(CalendarContract.Calendars.OWNER_ACCOUNT, cursor.getString(4));
                calendar.put(CalendarContract.Calendars.CALENDAR_COLOR, cursor.getInt(5));

                break;
            }
        }
        cursor.close();
        return calendar;
    }

    public Date[] getDefaultDateTime()
    {
        // 설정에서 기본 일정 시간 길이 설정가능
        int defaultRange = 60;

        Date[] dates = new Date[2];
        dates[0] = calendar.getTime();
        calendar.add(Calendar.MINUTE, defaultRange);
        dates[1] = calendar.getTime();
        calendar.add(Calendar.MINUTE, -defaultRange);

        return dates;
    }

    public TimeZone getDefaultTimeZone()
    {
        return TimeZone.getDefault();
    }

    public int getDefaultAccessLevel()
    {
        return CalendarContract.Events.ACCESS_DEFAULT;
    }

    public int getDefaultAvailability()
    {
        return CalendarContract.Events.AVAILABILITY_BUSY;
    }

}
