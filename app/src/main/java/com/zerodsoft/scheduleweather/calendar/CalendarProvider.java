package com.zerodsoft.scheduleweather.calendar;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import com.zerodsoft.scheduleweather.calendar.dto.CalendarDto;
import com.zerodsoft.scheduleweather.calendar.dto.EventDto;

import java.util.ArrayList;
import java.util.List;

public class CalendarProvider
{
    private static CalendarProvider instance;
    public static final int REQUEST_READ_CALENDAR = 200;
    public static final int REQUEST_WRITE_CALENDAR = 300;
    public static final String SELECTED_CALENDARS = "SELECTED_CALENDARS";

    private final Context CONTEXT;
    private final String[] EVENTS_PROJECTION =
            {
                    CalendarContract.Events.TITLE,
                    CalendarContract.Events.EVENT_COLOR_KEY,
                    CalendarContract.Events.OWNER_ACCOUNT,
                    CalendarContract.Events.CALENDAR_ID,
                    CalendarContract.Events.ORGANIZER,
                    CalendarContract.Events.EVENT_END_TIMEZONE,
                    CalendarContract.Events.EVENT_TIMEZONE,
                    CalendarContract.Events.ACCOUNT_NAME,
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
    private final String EVENTS_QUERY = "((" + CalendarContract.Events.ACCOUNT_NAME + " = ? AND "
            + CalendarContract.Events.ACCOUNT_TYPE + " = ? AND "
            + CalendarContract.Events.CALENDAR_ID + " = ? AND "
            + CalendarContract.Events.OWNER_ACCOUNT + " = ?"
            + "))";
    private final String EVENT_QUERY = "((" + CalendarContract.Events.ORIGINAL_ID + " = ? AND "
            + CalendarContract.Events.CALENDAR_ID + " = ? AND"
            + CalendarContract.Events.OWNER_ACCOUNT + " = ?"
            + "))";

    public static CalendarProvider newInstance(Context context)
    {
        instance = new CalendarProvider(context);
        return instance;
    }

    public static CalendarProvider getInstance()
    {
        return instance;
    }

    public CalendarProvider(Context CONTEXT)
    {
        this.CONTEXT = CONTEXT;
    }

    public ContentValues getEvent(int calendarId, int eventId, String ownerAccount)
    {
        // 화면에 이벤트 정보를 표시하기 위해 기본적인 데이터만 가져온다.
        // 요청 매개변수 : ID, 캘린더 ID, 오너 계정, 조직자
        // 표시할 데이터 : 제목, 일정 기간, 반복, 위치, 알림, 설명, 소유 계정, 참석자, 바쁨/한가함, 공개 범위 참석 여부 확인 창, 색상
        String[] selectionArgs = new String[]{Integer.toString(eventId), Integer.toString(calendarId), ownerAccount};

        ContentResolver contentResolver = CONTEXT.getContentResolver();
        Cursor cursor = contentResolver.query(CalendarContract.Events.CONTENT_URI, null, EVENT_QUERY, selectionArgs, null);
        ContentValues contentValues = new ContentValues();

        while (cursor.moveToNext())
        {
            contentValues.put(CalendarContract.Events.TITLE, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.TITLE)));
            contentValues.put(CalendarContract.Events.CALENDAR_ID, cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.CALENDAR_ID)));
            contentValues.put(CalendarContract.Events.DTSTART, cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTSTART)));
            contentValues.put(CalendarContract.Events.DTEND, cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTEND)));
            contentValues.put(CalendarContract.Events.ALL_DAY, 1 == cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.ALL_DAY)));
            contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EVENT_TIMEZONE)));
            contentValues.put(CalendarContract.Events.EVENT_END_TIMEZONE, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EVENT_END_TIMEZONE)));
            contentValues.put(CalendarContract.Events.RDATE, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.RDATE)));
            contentValues.put(CalendarContract.Events.RRULE, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.RRULE)));
            contentValues.put(CalendarContract.Events.EXDATE, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EXDATE)));
            contentValues.put(CalendarContract.Events.EXRULE, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EXRULE)));
            contentValues.put(CalendarContract.Events.HAS_ATTENDEE_DATA, 1 == cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.HAS_ATTENDEE_DATA)));
            contentValues.put(CalendarContract.Events.EVENT_LOCATION, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EVENT_LOCATION)));
            contentValues.put(CalendarContract.Events.DESCRIPTION, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DESCRIPTION)));
            contentValues.put(CalendarContract.Events.ACCESS_LEVEL, cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.ACCESS_LEVEL)));
            contentValues.put(CalendarContract.Events.AVAILABILITY, cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.AVAILABILITY)));
        }
        cursor.close();
        return contentValues;
    }

    public List<EventDto> getEvents(String accountName, String accountType, int calendarId, String ownerAccount)
    {
        // 필요한 데이터 : 제목, 색상, 오너 관련, 일정 길이, 반복 관련
        String[] selectionArgs = new String[]{accountName, accountType, Integer.toString(calendarId), ownerAccount};

        ContentResolver contentResolver = CONTEXT.getContentResolver();
        Cursor cursor = contentResolver.query(CalendarContract.Events.CONTENT_URI, EVENTS_PROJECTION, EVENTS_QUERY, selectionArgs, null);
        List<EventDto> eventsList = new ArrayList<>();

        while (cursor.moveToNext())
        {
            EventDto eventDto = new EventDto();
            eventsList.add(eventDto);

            eventDto.setTITLE(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.TITLE)));
            eventDto.setEVENT_COLOR_KEY(cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.EVENT_COLOR_KEY)));
            eventDto.setOWNER_ACCOUNT(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.OWNER_ACCOUNT)));
            eventDto.setCALENDAR_ID(cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.CALENDAR_ID)));
            eventDto.setORGANIZER(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.ORGANIZER)));
            eventDto.setEVENT_END_TIMEZONE(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EVENT_END_TIMEZONE)));
            eventDto.setEVENT_TIMEZONE(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EVENT_TIMEZONE)));
            eventDto.setACCOUNT_NAME(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.ACCOUNT_NAME)));
            eventDto.setACCOUNT_TYPE(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.ACCOUNT_TYPE)));
            eventDto.setDTSTART(cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTSTART)));
            eventDto.setDTEND(cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTEND)));
            eventDto.setRRULE(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.RRULE)));
            eventDto.setRDATE(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.RDATE)));
            eventDto.setEXRULE(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EXRULE)));
            eventDto.setEXDATE(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EXDATE)));
        }
        cursor.close();
        return eventsList;
    }


    public List<CalendarDto> getAllCalendars()
    {
        ContentResolver contentResolver = CONTEXT.getContentResolver();
        Cursor cursor = contentResolver.query(CalendarContract.Calendars.CONTENT_URI, null, null, null, null);
        List<CalendarDto> calendarsList = new ArrayList<>();

        /*
        필요한 데이터 : 달력 색상, 달력 이름, 소유자 계정, 계정 이름, 계정 타입, ID
         */
        while (cursor.moveToNext())
        {
            CalendarDto calendarDto = new CalendarDto();
            calendarsList.add(calendarDto);

            calendarDto.set_ID(cursor.getLong(cursor.getColumnIndex(CalendarContract.Calendars._ID)));
            calendarDto.setCALENDAR_DISPLAY_NAME(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)));
            calendarDto.setACCOUNT_NAME(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME)));
            calendarDto.setOWNER_ACCOUNT(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.OWNER_ACCOUNT)));
            calendarDto.setCALENDAR_COLOR_KEY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_COLOR_KEY)));
            calendarDto.setACCOUNT_TYPE(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ACCOUNT_TYPE)));
        }
        cursor.close();
        return calendarsList;
    }

    /*
    public long addEvent(ContentValues eventDto)
    {
        ContentValues values = new ContentValues();

        values.put(CalendarContract.Events.DTSTART, eventDto.getDTSTART());
        values.put(CalendarContract.Events.DTEND, eventDto.getDTEND());
        values.put(CalendarContract.Events.RRULE, eventDto.getRRULE());
        values.put(CalendarContract.Events.TITLE, eventDto.getTITLE());
        values.put(CalendarContract.Events.EVENT_LOCATION, eventDto.getEVENT_LOCATION());
        values.put(CalendarContract.Events.CALENDAR_ID, eventDto.getCALENDAR_ID());
        values.put(CalendarContract.Events.EVENT_TIMEZONE, eventDto.getEVENT_TIMEZONE());
        values.put(CalendarContract.Events.DESCRIPTION,
                eventDto.getDESCRIPTION());
        values.put(CalendarContract.Events.ACCESS_LEVEL, eventDto.get());
        values.put(CalendarContract.Events.SELF_ATTENDEE_STATUS,
                eventDto.getSELF_ATTENDEE_STATUS());
        values.put(CalendarContract.Events.ALL_DAY, eventDto.isALL_DAY());
        values.put(CalendarContract.Events.ORGANIZER, eventDto.getORGANIZER());
        values.put(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, eventDto.isGUESTS_CAN_INVITE_OTHERS());
        values.put(CalendarContract.Events.GUESTS_CAN_MODIFY, eventDto.isGUESTS_CAN_MODIFY());
        values.put(CalendarContract.Events.AVAILABILITY, eventDto.getAVAILABILITY());

        ContentResolver contentResolver = CONTEXT.getContentResolver();
        Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values);

        long eventID = Long.parseLong(uri.getLastPathSegment());
        return eventID;
    }

     */

    public List<ContentValues> getReminder(long eventId)
    {
        ContentResolver contentResolver = CONTEXT.getContentResolver();
        Cursor cursor = CalendarContract.Reminders.query(contentResolver, eventId, null);
        List<ContentValues> reminders = new ArrayList<>();

        while (cursor.moveToNext())
        {
            ContentValues reminder = new ContentValues();
            reminders.add(reminder);

            /*
            String[] columnNames = cursor.getColumnNames();
            int columnIndex = 0;
            int dataType = 0;

            for (String column : columnNames)
            {
                columnIndex = cursor.getColumnIndex(column);
                dataType = cursor.getType(columnIndex);
                putValue(reminder, cursor, column, columnIndex, dataType);
            }

             */
            reminder.put(CalendarContract.Reminders._COUNT, cursor.getInt(cursor.getColumnIndex(CalendarContract.Reminders._COUNT)));
            reminder.put(CalendarContract.Reminders._ID, cursor.getLong(cursor.getColumnIndex(CalendarContract.Reminders._ID)));
            reminder.put(CalendarContract.Reminders.EVENT_ID, cursor.getInt(cursor.getColumnIndex(CalendarContract.Reminders.EVENT_ID)));
            reminder.put(CalendarContract.Reminders.METHOD, cursor.getInt(cursor.getColumnIndex(CalendarContract.Reminders.METHOD)));
            reminder.put(CalendarContract.Reminders.MINUTES, cursor.getInt(cursor.getColumnIndex(CalendarContract.Reminders.MINUTES)));
        }
        cursor.close();
        return reminders;
    }

    private Uri asSyncAdapter(Uri uri, String accountName, String accountType)
    {
        return uri.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, accountName)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, accountType).build();
    }


    public List<ContentValues> getCalendars()
    {
        final String[] PROJECTION = {CalendarContract.Calendars._ID, CalendarContract.Calendars.NAME,
                CalendarContract.Calendars.ACCOUNT_NAME, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CalendarContract.Calendars.OWNER_ACCOUNT,
                CalendarContract.Calendars.CALENDAR_COLOR, CalendarContract.Calendars.IS_PRIMARY};
        ContentResolver contentResolver = CONTEXT.getContentResolver();
        Cursor cursor = contentResolver.query(CalendarContract.Calendars.CONTENT_URI, PROJECTION, null, null, null);

        final String GOOGLE_SECONDARY_CALENDAR = "@group.calendar.google.com";
        List<ContentValues> calendarList = new ArrayList<>();

        while (cursor.moveToNext())
        {
            if (cursor.getInt(6) == 1)
            {
                // another || google primary calendar
                ContentValues calendar = new ContentValues();

                calendar.put(CalendarContract.Calendars._ID, cursor.getString(0));
                calendar.put(CalendarContract.Calendars.NAME, cursor.getString(1));
                calendar.put(CalendarContract.Calendars.ACCOUNT_NAME, cursor.getString(2));
                calendar.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, cursor.getString(3));
                calendar.put(CalendarContract.Calendars.OWNER_ACCOUNT, cursor.getString(4));
                calendar.put(CalendarContract.Calendars.CALENDAR_COLOR, cursor.getInt(5));

                calendarList.add(calendar);
            } else if (cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.OWNER_ACCOUNT)).contains(GOOGLE_SECONDARY_CALENDAR))
            {
                ContentValues calendar = new ContentValues();

                calendar.put(CalendarContract.Calendars._ID, cursor.getString(0));
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
        return calendarList;
    }

    public void modifyEvent(ContentValues event)
    {
    }

    public List<ContentValues> getInstances()
    {
        ContentResolver contentResolver = CONTEXT.getContentResolver();

        Cursor cursor = contentResolver.query(CalendarContract.Instances.CONTENT_URI)
    }
}
