package com.zerodsoft.scheduleweather.googlecalendar;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract;

import com.zerodsoft.scheduleweather.googlecalendar.dto.CalendarDto;
import com.zerodsoft.scheduleweather.googlecalendar.dto.EventDto;

import java.util.ArrayList;
import java.util.List;

public class GoogleCalendarProvider
{
    private static GoogleCalendarProvider instance;
    public static final int REQUEST_READ_CALENDAR = 200;

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
                    CalendarContract.Events.EXDATE
            };
    private final String EVENTS_QUERY = "((" + CalendarContract.Events.CALENDAR_DISPLAY_NAME + " = ? AND "
            + CalendarContract.Events.ACCOUNT_NAME + " = ? AND "
            + CalendarContract.Events.ACCOUNT_TYPE + " = ? AND "
            + CalendarContract.Events.CALENDAR_ID + " = ? AND "
            + CalendarContract.Events.OWNER_ACCOUNT + " = ?"
            + "))";
    private final String EVENT_QUERY = "((" + CalendarContract.Events.ORIGINAL_ID + " = ? AND "
            + CalendarContract.Events.OWNER_ACCOUNT + " = ? AND "
            + CalendarContract.Events.CALENDAR_ID + " = ? AND "
            + CalendarContract.Events.ORGANIZER + " = ?"
            + "))";

    public static GoogleCalendarProvider newInstance(Context context)
    {
        instance = new GoogleCalendarProvider(context);
        return instance;
    }

    public static GoogleCalendarProvider getInstance()
    {
        return instance;
    }

    public GoogleCalendarProvider(Context CONTEXT)
    {
        this.CONTEXT = CONTEXT;
    }

    public EventDto getEvent(int id, int calendarId, String ownerAccount, String organizer)
    {
        // 필요한 데이터 : ID, 캘린더 ID, 오너 계정, 조직자
        String[] selectionArgs = new String[]{Integer.toString(id), ownerAccount, Integer.toString(calendarId), organizer};

        ContentResolver contentResolver = CONTEXT.getContentResolver();
        Cursor cursor = contentResolver.query(CalendarContract.Events.CONTENT_URI, null, EVENT_QUERY, selectionArgs, null);
        EventDto eventDto = new EventDto();

        while (cursor.moveToNext())
        {
            eventDto.setTITLE(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.TITLE)));
        }
    }

    public List<EventDto> getEvents(String calendarDisplayName, String accountName, String accountType, int calendarId, String ownerAccount)
    {
        // 필요한 데이터 : 제목, 색상, 오너 관련, 일정 길이, 반복 관련
        String[] selectionArgs = new String[]{calendarDisplayName, accountName, accountType, Integer.toString(calendarId), ownerAccount};

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
}
