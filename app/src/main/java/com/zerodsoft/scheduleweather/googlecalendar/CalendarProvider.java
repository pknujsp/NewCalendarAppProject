package com.zerodsoft.scheduleweather.googlecalendar;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract;

import com.zerodsoft.scheduleweather.googlecalendar.dto.AccountDto;
import com.zerodsoft.scheduleweather.googlecalendar.dto.CalendarDto;
import com.zerodsoft.scheduleweather.googlecalendar.dto.EventDto;

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

    public EventDto getEvent(int id, int calendarId, String ownerAccount)
    {
        // 화면에 이벤트 정보를 표시하기 위해 기본적인 데이터만 가져온다.
        // 요청 매개변수 : ID, 캘린더 ID, 오너 계정, 조직자
        // 표시할 데이터 : 제목, 일정 기간, 반복, 위치, 알림, 설명, 소유 계정, 참석자, 바쁨/한가함, 공개 범위 참석 여부 확인 창, 색상
        String[] selectionArgs = new String[]{Integer.toString(id), Integer.toString(calendarId), ownerAccount};

        ContentResolver contentResolver = CONTEXT.getContentResolver();
        Cursor cursor = contentResolver.query(CalendarContract.Events.CONTENT_URI, null, EVENT_QUERY, selectionArgs, null);
        EventDto eventDto = new EventDto();

        while (cursor.moveToNext())
        {
            eventDto.setTITLE(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.TITLE)));
        }
        cursor.close();
        return eventDto;
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

}
