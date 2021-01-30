package com.zerodsoft.scheduleweather.calendar;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;

import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendar.interfaces.ICalendarProvider;
import com.zerodsoft.scheduleweather.calendarview.callback.EventCallback;

import java.util.ArrayList;
import java.util.List;

public class CalendarProvider implements ICalendarProvider
{
    private static CalendarProvider instance;
    public static final int REQUEST_READ_CALENDAR = 200;
    public static final int REQUEST_WRITE_CALENDAR = 300;
    public static final String SELECTED_CALENDARS = "SELECTED_CALENDARS";


    private final Context context;
    private final String[] EVENTS_PROJECTION =
            {
                    CalendarContract.Events.TITLE,
                    CalendarContract.Events.EVENT_COLOR_KEY,
                    CalendarContract.Events.ACCOUNT_NAME,
                    CalendarContract.Events.CALENDAR_ID,
                    CalendarContract.Events.ORGANIZER,
                    CalendarContract.Events.EVENT_END_TIMEZONE,
                    CalendarContract.Events.EVENT_TIMEZONE,
                    CalendarContract.Events.ACCOUNT_TYPE,
                    CalendarContract.Events.DTSTART,
                    CalendarContract.Events.DTEND,
                    CalendarContract.Events.RRULE,
                    CalendarContract.Events.RDATE,
                    CalendarContract.Events.EXRULE,
                    CalendarContract.Events.EXDATE,
                    CalendarContract.Events.EVENT_LOCATION,
                    CalendarContract.Events.AVAILABILITY,
                    CalendarContract.Events.ACCESS_LEVEL,
                    CalendarContract.Events.HAS_ATTENDEE_DATA
            };

    private final String[] CALENDAR_PROJECTION = {
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.OWNER_ACCOUNT,
            CalendarContract.Calendars.CALENDAR_COLOR,
            CalendarContract.Calendars.ACCOUNT_TYPE
    };

    private final String EVENTS_QUERY;
    private final String EVENT_QUERY;
    private final String CALENDARS_QUERY;
    private final String INSTANCE_QUERY;
    private final String ATTENDEE_QUERY;
    private final String REMINDER_QUERY;

    public static CalendarProvider newInstance(Context context)
    {
        instance = new CalendarProvider(context);
        return instance;
    }

    public static CalendarProvider getInstance()
    {
        return instance;
    }

    public CalendarProvider(Context context)
    {
        this.context = context;
        StringBuilder stringBuilder = new StringBuilder();

        INSTANCE_QUERY = stringBuilder.append("(((").append(CalendarContract.Instances.BEGIN).append(">=?")
                .append(" AND ").append(CalendarContract.Instances.BEGIN).append("<?")
                .append(") OR (").append(CalendarContract.Instances.END).append(">=?")
                .append(" AND ").append(CalendarContract.Instances.END).append("<?")
                .append(") OR (").append(CalendarContract.Instances.BEGIN).append("<?")
                .append(" AND ").append(CalendarContract.Instances.END).append(">?")
                .append(")) AND ").append(CalendarContract.Instances.CALENDAR_ID).append("=?")
                .append(")").toString();

        stringBuilder.delete(0, stringBuilder.length());

        CALENDARS_QUERY = stringBuilder.append(CalendarContract.Calendars._ID).append("=?").toString();

        stringBuilder.delete(0, stringBuilder.length());

        EVENTS_QUERY = stringBuilder.append("").append(CalendarContract.Events.CALENDAR_ID).append("=?").toString();

        stringBuilder.delete(0, stringBuilder.length());

        EVENT_QUERY = stringBuilder.append(CalendarContract.Events._ID).append("=? AND ")
                .append(CalendarContract.Events.CALENDAR_ID).append("=?").toString();

        stringBuilder.delete(0, stringBuilder.length());

        ATTENDEE_QUERY = stringBuilder.append(CalendarContract.Attendees.CALENDAR_ID).append("=? AND ")
                .append(CalendarContract.Attendees.EVENT_ID).append("=?").toString();

        stringBuilder.delete(0, stringBuilder.length());

        REMINDER_QUERY = stringBuilder.append(CalendarContract.Reminders.CALENDAR_ID).append("=? AND ")
                .append(CalendarContract.Reminders.EVENT_ID).append("=?").toString();

        stringBuilder.delete(0, stringBuilder.length());
    }

    // event - crud

    /**
     * 하나의 이벤트에 대한 구체적인 정보를 가져온다.
     */
    @Override
    public ContentValues getEvent(int calendarId, long eventId)
    {
        // 화면에 이벤트 정보를 표시하기 위해 기본적인 데이터만 가져온다.
        // 요청 매개변수 : ID, 캘린더 ID, 오너 계정, 조직자
        // 표시할 데이터 : 제목, 일정 기간, 반복, 위치, 알림, 설명, 소유 계정, 참석자, 바쁨/한가함, 공개 범위 참석 여부 확인 창, 색상
        String[] selectionArgs = {String.valueOf(eventId), String.valueOf(calendarId)};

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(CalendarContract.Events.CONTENT_URI, null, EVENT_QUERY, selectionArgs, null);
        ContentValues event = new ContentValues();

        if (cursor != null)
        {
            while (cursor.moveToNext())
            {
                event.put(CalendarContract.Events.TITLE, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.TITLE)));
                event.put(CalendarContract.Events.CALENDAR_ID, cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.CALENDAR_ID)));
                event.put(CalendarContract.Events._ID, cursor.getLong(cursor.getColumnIndex(CalendarContract.Events._ID)));
                event.put(CalendarContract.Events.DTSTART, cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTSTART)));
                event.put(CalendarContract.Events.DTEND, cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTEND)));
                event.put(CalendarContract.Events.ALL_DAY, cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.ALL_DAY)));
                event.put(CalendarContract.Events.EVENT_TIMEZONE, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EVENT_TIMEZONE)));
                event.put(CalendarContract.Events.EVENT_END_TIMEZONE, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EVENT_END_TIMEZONE)));
                event.put(CalendarContract.Events.RDATE, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.RDATE)));
                event.put(CalendarContract.Events.RRULE, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.RRULE)));
                event.put(CalendarContract.Events.EXDATE, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EXDATE)));
                event.put(CalendarContract.Events.EXRULE, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EXRULE)));
                event.put(CalendarContract.Events.HAS_ATTENDEE_DATA, 1 == cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.HAS_ATTENDEE_DATA)));
                event.put(CalendarContract.Events.EVENT_LOCATION, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EVENT_LOCATION)));
                event.put(CalendarContract.Events.DESCRIPTION, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DESCRIPTION)));
                event.put(CalendarContract.Events.ACCESS_LEVEL, cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.ACCESS_LEVEL)));
                event.put(CalendarContract.Events.AVAILABILITY, cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.AVAILABILITY)));
                event.put(CalendarContract.Events.HAS_ALARM, cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.HAS_ALARM)));
            }
            cursor.close();
        }
        return event;
    }

    /**
     * 특정 캘린더의 모든 이벤트를 가져온다.
     */
    @Override
    public List<ContentValues> getEvents(int calendarId)
    {
        // 필요한 데이터 : 제목, 색상, 오너 관련, 일정 길이, 반복 관련
        String[] selectionArgs = {Integer.toString(calendarId)};

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(CalendarContract.Events.CONTENT_URI, EVENTS_PROJECTION, EVENTS_QUERY, selectionArgs, null);
        List<ContentValues> eventList = new ArrayList<>();

        if (cursor != null)
        {
            while (cursor.moveToNext())
            {
                ContentValues event = new ContentValues();
                eventList.add(event);

                event.put(CalendarContract.Events.TITLE, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.TITLE)));
                event.put(CalendarContract.Events.EVENT_COLOR_KEY, cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.EVENT_COLOR_KEY)));
                event.put(CalendarContract.Events.ACCOUNT_NAME, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.ACCOUNT_NAME)));
                event.put(CalendarContract.Events.CALENDAR_ID, cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.CALENDAR_ID)));
                event.put(CalendarContract.Events._ID, cursor.getLong(cursor.getColumnIndex(CalendarContract.Events._ID)));
                event.put(CalendarContract.Events.ORGANIZER, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.ORGANIZER)));
                event.put(CalendarContract.Events.EVENT_END_TIMEZONE, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EVENT_END_TIMEZONE)));
                event.put(CalendarContract.Events.EVENT_TIMEZONE, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EVENT_TIMEZONE)));
                event.put(CalendarContract.Events.DTSTART, cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTSTART)));
                event.put(CalendarContract.Events.ACCOUNT_TYPE, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.ACCOUNT_TYPE)));
                event.put(CalendarContract.Events.DTEND, cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTEND)));
                event.put(CalendarContract.Events.RRULE, cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.RRULE)));
                event.put(CalendarContract.Events.RDATE, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.RDATE)));
                event.put(CalendarContract.Events.EXRULE, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EXRULE)));
                event.put(CalendarContract.Events.EXDATE, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EXDATE)));
                event.put(CalendarContract.Events.EVENT_LOCATION, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EVENT_LOCATION)));
                event.put(CalendarContract.Events.AVAILABILITY, cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.AVAILABILITY)));
                event.put(CalendarContract.Events.ACCESS_LEVEL, cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.ACCESS_LEVEL)));
                event.put(CalendarContract.Events.HAS_ATTENDEE_DATA, cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.HAS_ATTENDEE_DATA)));
                event.put(CalendarContract.Events.HAS_ALARM, cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.HAS_ALARM)));
            }
            cursor.close();
        }
        return eventList;
    }

    /**
     * 이벤트를 추가한다.
     */
    @Override
    public void addEvent(ContentValues event)
    {
        context.getContentResolver().insert(CalendarContract.Events.CONTENT_URI, event);
    }

    /**
     * 이벤트를 제거한다.
     **/
    @Override
    public int deleteEvent(int calendarId, long eventId)
    {
        String where = "(" + CalendarContract.Events.CALENDAR_ID + "=? AND " + CalendarContract.Events._ID + "=?)";
        String[] selectionArgs = {String.valueOf(calendarId), String.valueOf(eventId)};

        return context.getContentResolver().delete(CalendarContract.Events.CONTENT_URI, where, selectionArgs);
    }

    /**
     * 이벤트들을 삭제한다.
     *
     * @return 삭제된 이벤트의 개수
     */
    @Override
    public int deleteEvents(int calendarId, long[] eventIds)
    {
        String where = "(" + CalendarContract.Events.CALENDAR_ID + "=? AND " + CalendarContract.Events._ID + "=?)";
        String[] selectionArgs = new String[2];
        selectionArgs[0] = String.valueOf(calendarId);

        ContentResolver contentResolver = context.getContentResolver();
        int deletedRows = 0;

        for (long eventId : eventIds)
        {
            selectionArgs[1] = String.valueOf(eventId);
            deletedRows += contentResolver.delete(CalendarContract.Events.CONTENT_URI, where, selectionArgs);
        }
        return deletedRows;
    }

    /**
     * 이벤트를 갱신한다.
     **/
    @Override
    public int updateEvent(ContentValues event)
    {
        ContentResolver contentResolver = context.getContentResolver();
        return contentResolver.update(CalendarContract.Events.CONTENT_URI, event, "", new String[1]);
    }

    // calendar - select

    /**
     * 기기내의 모든 캘린더를 가져온다.
     **/
    @Override
    public List<ContentValues> getAllCalendars()
    {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(CalendarContract.Calendars.CONTENT_URI, CALENDAR_PROJECTION, null, null, null);
        List<ContentValues> calendarList = new ArrayList<>();
        /*
        필요한 데이터 : 달력 색상, 달력 이름, 소유자 계정, 계정 이름, 계정 타입, ID
         */
        if (cursor != null)
        {
            while (cursor.moveToNext())
            {
                ContentValues calendar = new ContentValues();
                calendarList.add(calendar);

                calendar.put(CalendarContract.Calendars._ID, cursor.getLong(cursor.getColumnIndex(CalendarContract.Calendars._ID)));
                calendar.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)));
                calendar.put(CalendarContract.Calendars.ACCOUNT_NAME, cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME)));
                calendar.put(CalendarContract.Calendars.OWNER_ACCOUNT, cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.OWNER_ACCOUNT)));
                calendar.put(CalendarContract.Calendars.CALENDAR_COLOR, cursor.getInt(cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_COLOR)));
                calendar.put(CalendarContract.Calendars.ACCOUNT_TYPE, cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ACCOUNT_TYPE)));
            }
            cursor.close();
        }
        return calendarList;
    }

    /**
     * 공휴일, 생일 등을 제외한 주요 캘린더 모두를 가져온다.
     **/
    @Override
    public List<ContentValues> getCalendars()
    {
        final String[] PROJECTION = {CalendarContract.Calendars._ID, CalendarContract.Calendars.NAME,
                CalendarContract.Calendars.ACCOUNT_NAME, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CalendarContract.Calendars.OWNER_ACCOUNT,
                CalendarContract.Calendars.CALENDAR_COLOR, CalendarContract.Calendars.IS_PRIMARY};
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(CalendarContract.Calendars.CONTENT_URI, PROJECTION, null, null, null);

        final String GOOGLE_SECONDARY_CALENDAR = "@group.calendar.google.com";
        List<ContentValues> calendarList = new ArrayList<>();

        if (cursor != null)
        {
            while (cursor.moveToNext())
            {
                if (cursor.getInt(6) == 1)
                {
                    // another || google primary calendar
                    ContentValues calendar = new ContentValues();

                    calendar.put(CalendarContract.Calendars._ID, cursor.getLong(0));
                    calendar.put(CalendarContract.Calendars.NAME, cursor.getString(1));
                    calendar.put(CalendarContract.Calendars.ACCOUNT_NAME, cursor.getString(2));
                    calendar.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, cursor.getString(3));
                    calendar.put(CalendarContract.Calendars.OWNER_ACCOUNT, cursor.getString(4));
                    calendar.put(CalendarContract.Calendars.CALENDAR_COLOR, cursor.getInt(5));

                    calendarList.add(calendar);
                } else if (cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.OWNER_ACCOUNT)).contains(GOOGLE_SECONDARY_CALENDAR))
                {
                    ContentValues calendar = new ContentValues();

                    calendar.put(CalendarContract.Calendars._ID, cursor.getLong(0));
                    calendar.put(CalendarContract.Calendars.NAME, cursor.getString(1));
                    calendar.put(CalendarContract.Calendars.ACCOUNT_NAME, cursor.getString(2));
                    calendar.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, cursor.getString(3));
                    calendar.put(CalendarContract.Calendars.OWNER_ACCOUNT, cursor.getString(4));
                    calendar.put(CalendarContract.Calendars.CALENDAR_COLOR, cursor.getInt(5));

                    calendarList.add(calendar);
                    break;
                }
            }
            cursor.close();
        }
        return calendarList;
    }

    /**
     * 하나의 캘린더에 대한 정보를 가져온다.
     */
    @Override
    public ContentValues getCalendar(int calendarId)
    {
        ContentResolver contentResolver = context.getContentResolver();
        String[] selectionArgs = {String.valueOf(calendarId)};
        Cursor cursor = contentResolver.query(CalendarContract.Calendars.CONTENT_URI, null, CALENDARS_QUERY, selectionArgs, null);

        ContentValues calendar = null;
        if (cursor != null)
        {
            while (cursor.moveToNext())
            {
                calendar = new ContentValues();

                calendar.put(CalendarContract.Calendars._ID, cursor.getLong(cursor.getColumnIndex(CalendarContract.Calendars._ID)));
                calendar.put(CalendarContract.Calendars.NAME, cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.NAME)));
                calendar.put(CalendarContract.Calendars.ACCOUNT_NAME, cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME)));
                calendar.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, cursor.getInt(cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)));
                calendar.put(CalendarContract.Calendars.OWNER_ACCOUNT, cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.OWNER_ACCOUNT)));
                calendar.put(CalendarContract.Calendars.CALENDAR_COLOR, cursor.getInt(cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_COLOR)));
            }
            cursor.close();
        }
        return calendar;
    }

    // reminder - crud

    /**
     * 하나의 이벤트에 대한 알림 모두를 가져온다.
     */
    @Override
    public List<ContentValues> getReminders(int calendarId, long eventId)
    {
        ContentResolver contentResolver = context.getContentResolver();
        String[] selectionArgs = {String.valueOf(calendarId), String.valueOf(eventId)};
        Cursor cursor = contentResolver.query(CalendarContract.Reminders.CONTENT_URI, null, REMINDER_QUERY, selectionArgs, null);
        List<ContentValues> reminders = new ArrayList<>();
        if (cursor != null)
        {
            while (cursor.moveToNext())
            {
                ContentValues reminder = new ContentValues();
                reminders.add(reminder);

                reminder.put(CalendarContract.Reminders._ID, cursor.getLong(cursor.getColumnIndex(CalendarContract.Reminders._ID)));
                reminder.put(CalendarContract.Reminders.EVENT_ID, cursor.getInt(cursor.getColumnIndex(CalendarContract.Reminders.EVENT_ID)));
                reminder.put(CalendarContract.Reminders.METHOD, cursor.getInt(cursor.getColumnIndex(CalendarContract.Reminders.METHOD)));
                reminder.put(CalendarContract.Reminders.MINUTES, cursor.getInt(cursor.getColumnIndex(CalendarContract.Reminders.MINUTES)));
            }
            cursor.close();
        }
        return reminders;
    }

    /**
     * 알림값을 갱신한다.
     **/
    @Override
    public int updateReminder(ContentValues reminder)
    {
        final String where = CalendarContract.Reminders._ID + "=? AND " +
                CalendarContract.Reminders.EVENT_ID + "=? AND " +
                CalendarContract.Reminders.CALENDAR_ID + "=?";

        String[] selectionArgs = {
                reminder.getAsLong(CalendarContract.Reminders._ID).toString()
                , reminder.getAsLong(CalendarContract.Reminders.EVENT_ID).toString()
                , reminder.getAsInteger(CalendarContract.Reminders.CALENDAR_ID).toString()};

        ContentResolver contentResolver = context.getContentResolver();
        int updatedRows = 0;
        updatedRows += contentResolver.update(CalendarContract.Reminders.CONTENT_URI, reminder, where, selectionArgs);

        return updatedRows;
    }

    /**
     * 알림을 삭제한다.
     */
    @Override
    public int deleteReminders(int calendarId, long eventId, int[] reminderIds)
    {
        String where = "(" + CalendarContract.Reminders.CALENDAR_ID + "=? AND "
                + CalendarContract.Reminders.EVENT_ID + "=? AND "
                + CalendarContract.Reminders._ID + "=?)";
        String[] selectionArgs = new String[3];
        selectionArgs[0] = String.valueOf(calendarId);
        selectionArgs[1] = String.valueOf(eventId);

        ContentResolver contentResolver = context.getContentResolver();
        int deletedRows = 0;

        for (int reminderId : reminderIds)
        {
            selectionArgs[2] = String.valueOf(reminderId);
            deletedRows += contentResolver.delete(CalendarContract.Reminders.CONTENT_URI, where, selectionArgs);
        }
        return deletedRows;
    }

    @Override
    public int deleteAllReminders(int calendarId, long eventId)
    {
        String where = CalendarContract.Reminders.CALENDAR_ID + "=? AND " + CalendarContract.Reminders.EVENT_ID + "=?";
        String[] selectionArgs = {String.valueOf(calendarId), String.valueOf(eventId)};
        return context.getContentResolver().delete(CalendarContract.Reminders.CONTENT_URI, where, selectionArgs);
    }

    /**
     * 알림을 추가한다.
     */
    @Override
    public int addReminders(List<ContentValues> reminders)
    {
        ContentResolver contentResolver = context.getContentResolver();
        int addedRows = 0;

        for (ContentValues reminder : reminders)
        {
            contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, reminder);
            addedRows++;
        }
        return addedRows;
    }

    // instance - select

    /**
     * 각각의 캘린더들 내에 특정한 기간 사이에 있는 인스턴스들을 가져온다.
     **/
    @Override
    public void getInstances(List<ContentValues> calendarList, long startDate, long endDate, EventCallback<List<CalendarInstance>> callback)
    {
        List<CalendarInstance> calendarInstances = new ArrayList<>();

        if (calendarList.isEmpty())
        {
            callback.onResult(calendarInstances);
        }
        ContentResolver contentResolver = context.getContentResolver();
        final String startMilliSec = String.valueOf(startDate);
        final String endMilliSec = String.valueOf(endDate);
        final String[] selectionArgs = new String[7];

        selectionArgs[0] = startMilliSec;
        selectionArgs[1] = endMilliSec;
        selectionArgs[2] = startMilliSec;
        selectionArgs[3] = endMilliSec;
        selectionArgs[4] = startMilliSec;
        selectionArgs[5] = endMilliSec;

        String selection = CalendarContract.Instances.CALENDAR_ID + "=?";
        final String[] selectionArg = new String[1];


        for (ContentValues calendar : calendarList)
        {
            selectionArg[0] = String.valueOf(calendar.getAsInteger(CalendarContract.Calendars._ID));

            Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
            ContentUris.appendId(builder, startDate);
            ContentUris.appendId(builder, endDate);

            Cursor cursor = contentResolver.query(builder.build(), null, selection, selectionArg, null);

            List<ContentValues> instances = new ArrayList<>();

            if (cursor != null)
            {
                while (cursor.moveToNext())
                {
                    ContentValues instance = new ContentValues();
                    instances.add(instance);

                    instance.put(CalendarContract.Instances.EVENT_COLOR, cursor.getInt(cursor.getColumnIndex(CalendarContract.Instances.EVENT_COLOR)));
                    instance.put(CalendarContract.Instances._ID, cursor.getLong(cursor.getColumnIndex(CalendarContract.Instances._ID)));
                    instance.put(CalendarContract.Instances.BEGIN, cursor.getLong(cursor.getColumnIndex(CalendarContract.Instances.BEGIN)));
                    instance.put(CalendarContract.Instances.END, cursor.getLong(cursor.getColumnIndex(CalendarContract.Instances.END)));
                    instance.put(CalendarContract.Instances.DTSTART, cursor.getLong(cursor.getColumnIndex(CalendarContract.Instances.DTSTART)));
                    instance.put(CalendarContract.Instances.DTEND, cursor.getLong(cursor.getColumnIndex(CalendarContract.Instances.DTEND)));
                    instance.put(CalendarContract.Instances.TITLE, cursor.getString(cursor.getColumnIndex(CalendarContract.Instances.TITLE)));
                    instance.put(CalendarContract.Instances.EVENT_ID, cursor.getLong(cursor.getColumnIndex(CalendarContract.Instances.EVENT_ID)));
                    instance.put(CalendarContract.Instances.CALENDAR_ID, cursor.getInt(cursor.getColumnIndex(CalendarContract.Instances.CALENDAR_ID)));
                    instance.put(CalendarContract.Instances.ALL_DAY, cursor.getInt(cursor.getColumnIndex(CalendarContract.Instances.ALL_DAY)));
                }
                cursor.close();
            }

            calendarInstances.add(new CalendarInstance(instances, calendar.getAsLong(CalendarContract.Calendars._ID)));
        }
        callback.onResult(calendarInstances);
    }


    // attendee - crud

    /**
     * 참석자들을 추가한다.
     */
    @Override
    public int addAttendees(List<ContentValues> attendeeList)
    {
        ContentResolver contentResolver = context.getContentResolver();
        int addedRows = 0;
        for (ContentValues attendee : attendeeList)
        {
            contentResolver.insert(CalendarContract.Attendees.CONTENT_URI, attendee);
            addedRows++;
        }
        return addedRows;
    }

    /**
     * 하나의 이벤트에 대한 참석자 정보들을 가져온다.
     */
    @Override
    public List<ContentValues> getAttendees(int calendarId, long eventId)
    {
        ContentResolver contentResolver = context.getContentResolver();
        String[] selectionArgs = {String.valueOf(calendarId),
                String.valueOf(eventId)};

        List<ContentValues> attendees = new ArrayList<>();
        Cursor cursor = contentResolver.query(CalendarContract.Attendees.CONTENT_URI, null, ATTENDEE_QUERY, selectionArgs, null);

        if (cursor != null)
        {
            while (cursor.moveToNext())
            {
                ContentValues attendee = new ContentValues();
                attendees.add(attendee);

                attendee.put(CalendarContract.Attendees._ID, cursor.getLong(cursor.getColumnIndex(CalendarContract.Attendees._ID)));
                attendee.put(CalendarContract.Attendees.EVENT_ID, cursor.getInt(cursor.getColumnIndex(CalendarContract.Attendees.EVENT_ID)));
                attendee.put(CalendarContract.Attendees.CALENDAR_ID, cursor.getInt(cursor.getColumnIndex(CalendarContract.Attendees.CALENDAR_ID)));
                attendee.put(CalendarContract.Attendees.ATTENDEE_EMAIL, cursor.getString(cursor.getColumnIndex(CalendarContract.Attendees.ATTENDEE_EMAIL)));
                attendee.put(CalendarContract.Attendees.ATTENDEE_ID_NAMESPACE, cursor.getString(cursor.getColumnIndex(CalendarContract.Attendees.ATTENDEE_ID_NAMESPACE)));
                attendee.put(CalendarContract.Attendees.ATTENDEE_IDENTITY, cursor.getString(cursor.getColumnIndex(CalendarContract.Attendees.ATTENDEE_IDENTITY)));
                attendee.put(CalendarContract.Attendees.ATTENDEE_NAME, cursor.getString(cursor.getColumnIndex(CalendarContract.Attendees.ATTENDEE_NAME)));
                attendee.put(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP, cursor.getInt(cursor.getColumnIndex(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP)));
                attendee.put(CalendarContract.Attendees.ATTENDEE_STATUS, cursor.getInt(cursor.getColumnIndex(CalendarContract.Attendees.ATTENDEE_STATUS)));
                attendee.put(CalendarContract.Attendees.ATTENDEE_TYPE, cursor.getInt(cursor.getColumnIndex(CalendarContract.Attendees.ATTENDEE_TYPE)));
            }
            cursor.close();
        }
        return attendees;
    }

    /**
     * 참석자 정보를 갱신한다.
     *
     * @return 갱신된 행의 개수 반환
     */
    @Override
    public int updateAttendees(List<ContentValues> attendeeList)
    {
        ContentResolver contentResolver = context.getContentResolver();
        int updatedRows = 0;
        final int eventId = attendeeList.get(0).getAsInteger(CalendarContract.Attendees.EVENT_ID);
        final int calendarId = attendeeList.get(0).getAsInteger(CalendarContract.Attendees.CALENDAR_ID);

        final String where = CalendarContract.Attendees._ID + "=? AND " +
                CalendarContract.Attendees.EVENT_ID + "=? AND " +
                CalendarContract.Attendees.CALENDAR_ID + "=?";

        String[] selectionArgs = new String[3];
        selectionArgs[1] = String.valueOf(eventId);
        selectionArgs[2] = String.valueOf(calendarId);

        for (ContentValues attendee : attendeeList)
        {
            selectionArgs[0] = attendee.getAsLong(CalendarContract.Attendees._ID).toString();
            updatedRows += contentResolver.update(CalendarContract.Attendees.CONTENT_URI, attendee, where, selectionArgs);
        }
        return updatedRows;
    }

    /**
     * 참석자 정보를 모두 삭제한다.
     *
     * @return 삭제된 행의 개수 반환
     */
    @Override
    public int deleteAllAttendees(int calendarId, long eventId)
    {
        ContentResolver contentResolver = context.getContentResolver();
        int updatedRows = 0;
        String where = CalendarContract.Attendees.CALENDAR_ID + "=? AND " + CalendarContract.Attendees.EVENT_ID + "=?";
        String[] selectionArgs = {String.valueOf(calendarId), String.valueOf(eventId)};

        updatedRows += contentResolver.delete(CalendarContract.Attendees.CONTENT_URI, where, selectionArgs);
        return updatedRows;
    }

    /**
     * 특정 참석자 정보를 삭제한다.
     *
     * @return 삭제된 행의 개수 반환
     */
    @Override
    public int deleteAttendees(int calendarId, long eventId, int[] attendeeIds)
    {
        final String where = CalendarContract.Attendees.CALENDAR_ID + "=? AND " +
                CalendarContract.Attendees.EVENT_ID + "=? AND " +
                CalendarContract.Attendees._ID + "=?";

        String[] selectionArgs = new String[3];
        selectionArgs[0] = String.valueOf(calendarId);
        selectionArgs[1] = String.valueOf(eventId);

        ContentResolver contentResolver = context.getContentResolver();
        int updatedRows = 0;

        for (int attendeeId : attendeeIds)
        {
            selectionArgs[2] = String.valueOf(attendeeId);
            updatedRows += contentResolver.delete(CalendarContract.Attendees.CONTENT_URI, where, selectionArgs);
        }
        return updatedRows;
    }

    public void syncCalendars()
    {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        final Account[] accounts = accountManager.getAccountsByType("com.google");
        final String authority = CalendarContract.AUTHORITY;

        for (Account account : accounts)
        {

            Bundle extras = new Bundle();
            extras.putBoolean(
                    ContentResolver.SYNC_EXTRAS_MANUAL, true);
            extras.putBoolean(
                    ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

            ContentResolver.requestSync(account, authority, extras);
        }
    }
}
