package com.zerodsoft.scheduleweather.googlecalendar;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.EntityIterator;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.zerodsoft.scheduleweather.googlecalendar.dto.CalendarDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GoogleCalendarProvider
{
    public static final int REQUEST_READ_CALENDAR = 200;
    private final Context CONTEXT;

    private static GoogleCalendarProvider instance;

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

    public List<Events> getAllEvents(List<Integer> calendarIds)
    {
        final String[] EVENT_PROJECTION =
                {
                        CalendarContract.Calendars.ALLOWED_ATTENDEE_TYPES,
                        CalendarContract.Calendars.ACCOUNT_NAME,
                        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                        CalendarContract.Calendars.CALENDAR_LOCATION,
                        CalendarContract.Calendars.CALENDAR_TIME_ZONE
                };

        final String SELECTION = "((" + CalendarContract.Events.CALENDAR_ID + " = ?))";

        String[] selectionArgs = new String[1];

        Uri uri = CalendarContract.Events.CONTENT_URI;
        ContentResolver contentResolver = CONTEXT.getContentResolver();
        Cursor cursor = null;

        List<Events> customGoogleCalendarList = new ArrayList<>();

        EventDateTime startDateTime = new EventDateTime();
        EventDateTime endDateTime = new EventDateTime();

        for (int i = 0; i < calendarIds.size(); i++)
        {
            selectionArgs[0] = calendarIds.get(i).toString();

            cursor = contentResolver.query(uri, null, SELECTION, selectionArgs, null);

            Events events = new Events();
            List<Event> eventList = new ArrayList<>();


            while (cursor.moveToNext())
            {
                Event event = new Event();

                startDateTime.setTimeZone(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EVENT_TIMEZONE)));
                endDateTime.setTimeZone(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EVENT_END_TIMEZONE)));
                startDateTime.setDateTime(new DateTime(cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTSTART))));
                endDateTime.setDateTime(new DateTime(cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTEND))));

                List<String> recurrences = new ArrayList<>();
                recurrences.add(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.RRULE)));
                recurrences.add(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EXRULE)));
                recurrences.add(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.RDATE)));
                recurrences.add(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EXDATE)));

                event.setSummary(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.TITLE)));
                event.setStart(startDateTime.clone());
                event.setEnd(endDateTime.clone());
                event.setRecurrence(recurrences);
                event.setId(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.ORIGINAL_ID)));
                event.setStatus(CalendarContract.Events.STATUS);
                event.setDescription(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DESCRIPTION)));
                event.setLocation(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EVENT_LOCATION)));
                event.setColorId(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EVENT_COLOR_KEY)));

                Event.Organizer organizer = new Event.Organizer();
                organizer.setEmail(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.ORGANIZER)));
                organizer.setSelf(cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.IS_ORGANIZER)) == 1);
                event.setOrganizer(organizer);

                Event.Creator creator = new Event.Creator();
                //  CalendarContract.Events.
            }
            cursor.close();
        }

        /*
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR}, REQUEST_READ_CALENDAR);
        }
         */
        return customGoogleCalendarList;
    }

    public List<CalendarDto> getAllCalendars()
    {
        final String[] EVENT_PROJECTION =
                {
                        CalendarContract.Calendars.ALLOWED_ATTENDEE_TYPES,
                        CalendarContract.Calendars.ACCOUNT_NAME,
                        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                        CalendarContract.Calendars.CALENDAR_LOCATION,
                        CalendarContract.Calendars.CALENDAR_TIME_ZONE
                };

        final String SELECTION = "((" + CalendarContract.Calendars.OWNER_ACCOUNT + " = ? AND"
                + CalendarContract.Calendars.ACCOUNT_NAME + " = ?"
                + "))";


        ContentResolver contentResolver = CONTEXT.getContentResolver();
        Cursor cursor = contentResolver.query(CalendarContract.Calendars.CONTENT_URI, null, null, null, null);
        List<CalendarDto> list = new ArrayList<>();

        while (cursor.moveToNext())
        {
            CalendarDto calendarDto = new CalendarDto();

            calendarDto.setALLOWED_ATTENDEE_TYPES(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_ATTENDEE_TYPES)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));
            calendarDto.setALLOWED_AVAILABILITY(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY)));


            ContentValues values = new ContentValues();
            // content values 사용하기


            calendarDto.set_ID(cursor.getLong(cursor.getColumnIndex(CalendarContract.Calendars._ID)));
            calendarDto.setCALENDAR_DISPLAY_NAME(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)));
            calendarDto.setACCOUNT_NAME(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME)));
            calendarDto.setOWNER_ACCOUNT(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.OWNER_ACCOUNT)));


        }
        return list;
    }
}
