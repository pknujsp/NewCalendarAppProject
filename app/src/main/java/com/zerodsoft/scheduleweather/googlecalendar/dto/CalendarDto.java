package com.zerodsoft.scheduleweather.googlecalendar.dto;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;

import org.mortbay.jetty.servlet.Context;

import java.util.List;

public class CalendarDto
{
    private String ALLOWED_ATTENDEE_TYPES;
    private String ALLOWED_AVAILABILITY;
    private String ALLOWED_REMINDERS;
    private int CALENDAR_ACCESS_LEVEL;
    private int CALENDAR_COLOR;
    private String CALENDAR_COLOR_KEY;
    private String CALENDAR_DISPLAY_NAME;
    private String CALENDAR_TIME_ZONE;
    private int CAL_ACCESS_CONTRIBUTOR;
    private int CAL_ACCESS_EDITOR;
    private int CAL_ACCESS_FREEBUSY;
    private int CAL_ACCESS_NONE;
    private int CAL_ACCESS_OVERRIDE;
    private int CAL_ACCESS_OWNER;
    private int CAL_ACCESS_READ;
    private int CAL_ACCESS_RESPOND;
    private int CAL_ACCESS_ROOT;
    private int CAN_MODIFY_TIME_ZONE;
    private int CAN_ORGANIZER_RESPOND;
    private String IS_PRIMARY;
    private int MAX_REMINDERS;
    private String OWNER_ACCOUNT;
    private int SYNC_EVENTS;
    private int VISIBLE;

    private String ACCOUNT_NAME;
    private String ACCOUNT_TYPE;
    private int CAN_PARTIALLY_UPDATE;
    private int DELETED;
    private int DIRTY;
    private String MUTATORS;
    private String _SYNC_ID;
    private long _ID;

    private String CAL_SYNC1;
    private String CAL_SYNC2;
    private String CAL_SYNC3;
    private String CAL_SYNC4;
    private String CAL_SYNC5;
    private String CAL_SYNC6;
    private String CAL_SYNC7;
    private String CAL_SYNC8;
    private String CAL_SYNC9;
    private String CAL_SYNC10;

    private List<EventDto> events;

    public CalendarDto()
    {
    }

    public CalendarDto setEvents(List<EventDto> events)
    {
        this.events = events;
        return this;
    }

    public List<EventDto> getEvents()
    {
        return events;
    }

    public String getALLOWED_ATTENDEE_TYPES()
    {
        return ALLOWED_ATTENDEE_TYPES;
    }

    public CalendarDto setALLOWED_ATTENDEE_TYPES(String ALLOWED_ATTENDEE_TYPES)
    {
        this.ALLOWED_ATTENDEE_TYPES = ALLOWED_ATTENDEE_TYPES;
        return this;
    }

    public String getALLOWED_AVAILABILITY()
    {
        return ALLOWED_AVAILABILITY;
    }

    public long get_ID()
    {
        return _ID;
    }

    public CalendarDto set_ID(long _ID)
    {
        this._ID = _ID;
        return this;
    }

    public CalendarDto setALLOWED_AVAILABILITY(String ALLOWED_AVAILABILITY)
    {
        this.ALLOWED_AVAILABILITY = ALLOWED_AVAILABILITY;
        return this;
    }

    public String getALLOWED_REMINDERS()
    {
        return ALLOWED_REMINDERS;
    }

    public CalendarDto setALLOWED_REMINDERS(String ALLOWED_REMINDERS)
    {
        this.ALLOWED_REMINDERS = ALLOWED_REMINDERS;
        return this;
    }

    public int getCALENDAR_ACCESS_LEVEL()
    {
        return CALENDAR_ACCESS_LEVEL;
    }

    public CalendarDto setCALENDAR_ACCESS_LEVEL(int CALENDAR_ACCESS_LEVEL)
    {
        this.CALENDAR_ACCESS_LEVEL = CALENDAR_ACCESS_LEVEL;
        return this;
    }

    public int getCALENDAR_COLOR()
    {
        return CALENDAR_COLOR;
    }

    public CalendarDto setCALENDAR_COLOR(int CALENDAR_COLOR)
    {
        this.CALENDAR_COLOR = CALENDAR_COLOR;
        return this;
    }

    public String getCALENDAR_COLOR_KEY()
    {
        return CALENDAR_COLOR_KEY;
    }

    public CalendarDto setCALENDAR_COLOR_KEY(String CALENDAR_COLOR_KEY)
    {
        this.CALENDAR_COLOR_KEY = CALENDAR_COLOR_KEY;
        return this;
    }

    public String getCALENDAR_DISPLAY_NAME()
    {
        return CALENDAR_DISPLAY_NAME;
    }

    public CalendarDto setCALENDAR_DISPLAY_NAME(String CALENDAR_DISPLAY_NAME)
    {
        this.CALENDAR_DISPLAY_NAME = CALENDAR_DISPLAY_NAME;
        return this;
    }

    public String getCALENDAR_TIME_ZONE()
    {
        return CALENDAR_TIME_ZONE;
    }

    public CalendarDto setCALENDAR_TIME_ZONE(String CALENDAR_TIME_ZONE)
    {
        this.CALENDAR_TIME_ZONE = CALENDAR_TIME_ZONE;
        return this;
    }

    public int getCAL_ACCESS_CONTRIBUTOR()
    {
        return CAL_ACCESS_CONTRIBUTOR;
    }

    public CalendarDto setCAL_ACCESS_CONTRIBUTOR(int CAL_ACCESS_CONTRIBUTOR)
    {
        this.CAL_ACCESS_CONTRIBUTOR = CAL_ACCESS_CONTRIBUTOR;
        return this;
    }

    public int getCAL_ACCESS_EDITOR()
    {
        return CAL_ACCESS_EDITOR;
    }

    public CalendarDto setCAL_ACCESS_EDITOR(int CAL_ACCESS_EDITOR)
    {
        this.CAL_ACCESS_EDITOR = CAL_ACCESS_EDITOR;
        return this;
    }

    public int getCAL_ACCESS_FREEBUSY()
    {
        return CAL_ACCESS_FREEBUSY;
    }

    public CalendarDto setCAL_ACCESS_FREEBUSY(int CAL_ACCESS_FREEBUSY)
    {
        this.CAL_ACCESS_FREEBUSY = CAL_ACCESS_FREEBUSY;
        return this;
    }

    public int getCAL_ACCESS_NONE()
    {
        return CAL_ACCESS_NONE;
    }

    public CalendarDto setCAL_ACCESS_NONE(int CAL_ACCESS_NONE)
    {
        this.CAL_ACCESS_NONE = CAL_ACCESS_NONE;
        return this;
    }

    public int getCAL_ACCESS_OVERRIDE()
    {
        return CAL_ACCESS_OVERRIDE;
    }

    public CalendarDto setCAL_ACCESS_OVERRIDE(int CAL_ACCESS_OVERRIDE)
    {
        this.CAL_ACCESS_OVERRIDE = CAL_ACCESS_OVERRIDE;
        return this;
    }

    public int getCAL_ACCESS_OWNER()
    {
        return CAL_ACCESS_OWNER;
    }

    public CalendarDto setCAL_ACCESS_OWNER(int CAL_ACCESS_OWNER)
    {
        this.CAL_ACCESS_OWNER = CAL_ACCESS_OWNER;
        return this;
    }

    public int getCAL_ACCESS_READ()
    {
        return CAL_ACCESS_READ;
    }

    public CalendarDto setCAL_ACCESS_READ(int CAL_ACCESS_READ)
    {
        this.CAL_ACCESS_READ = CAL_ACCESS_READ;
        return this;
    }

    public int getCAL_ACCESS_RESPOND()
    {
        return CAL_ACCESS_RESPOND;
    }

    public CalendarDto setCAL_ACCESS_RESPOND(int CAL_ACCESS_RESPOND)
    {
        this.CAL_ACCESS_RESPOND = CAL_ACCESS_RESPOND;
        return this;
    }

    public int getCAL_ACCESS_ROOT()
    {
        return CAL_ACCESS_ROOT;
    }

    public CalendarDto setCAL_ACCESS_ROOT(int CAL_ACCESS_ROOT)
    {
        this.CAL_ACCESS_ROOT = CAL_ACCESS_ROOT;
        return this;
    }

    public int getCAN_MODIFY_TIME_ZONE()
    {
        return CAN_MODIFY_TIME_ZONE;
    }

    public CalendarDto setCAN_MODIFY_TIME_ZONE(int CAN_MODIFY_TIME_ZONE)
    {
        this.CAN_MODIFY_TIME_ZONE = CAN_MODIFY_TIME_ZONE;
        return this;
    }

    public int getCAN_ORGANIZER_RESPOND()
    {
        return CAN_ORGANIZER_RESPOND;
    }

    public CalendarDto setCAN_ORGANIZER_RESPOND(int CAN_ORGANIZER_RESPOND)
    {
        this.CAN_ORGANIZER_RESPOND = CAN_ORGANIZER_RESPOND;
        return this;
    }

    public String getIS_PRIMARY()
    {
        return IS_PRIMARY;
    }

    public CalendarDto setIS_PRIMARY(String IS_PRIMARY)
    {
        this.IS_PRIMARY = IS_PRIMARY;
        return this;
    }

    public int getMAX_REMINDERS()
    {
        return MAX_REMINDERS;
    }

    public CalendarDto setMAX_REMINDERS(int MAX_REMINDERS)
    {
        this.MAX_REMINDERS = MAX_REMINDERS;
        return this;
    }

    public String getOWNER_ACCOUNT()
    {
        return OWNER_ACCOUNT;
    }

    public CalendarDto setOWNER_ACCOUNT(String OWNER_ACCOUNT)
    {
        this.OWNER_ACCOUNT = OWNER_ACCOUNT;
        return this;
    }

    public int getSYNC_EVENTS()
    {
        return SYNC_EVENTS;
    }

    public CalendarDto setSYNC_EVENTS(int SYNC_EVENTS)
    {
        this.SYNC_EVENTS = SYNC_EVENTS;
        return this;
    }

    public int getVISIBLE()
    {
        return VISIBLE;
    }

    public CalendarDto setVISIBLE(int VISIBLE)
    {
        this.VISIBLE = VISIBLE;
        return this;
    }

    public String getACCOUNT_NAME()
    {
        return ACCOUNT_NAME;
    }

    public CalendarDto setACCOUNT_NAME(String ACCOUNT_NAME)
    {
        this.ACCOUNT_NAME = ACCOUNT_NAME;
        return this;
    }

    public String getACCOUNT_TYPE()
    {
        return ACCOUNT_TYPE;
    }

    public CalendarDto setACCOUNT_TYPE(String ACCOUNT_TYPE)
    {
        this.ACCOUNT_TYPE = ACCOUNT_TYPE;
        return this;
    }

    public int getCAN_PARTIALLY_UPDATE()
    {
        return CAN_PARTIALLY_UPDATE;
    }

    public CalendarDto setCAN_PARTIALLY_UPDATE(int CAN_PARTIALLY_UPDATE)
    {
        this.CAN_PARTIALLY_UPDATE = CAN_PARTIALLY_UPDATE;
        return this;
    }

    public int getDELETED()
    {
        return DELETED;
    }

    public CalendarDto setDELETED(int DELETED)
    {
        this.DELETED = DELETED;
        return this;
    }

    public int getDIRTY()
    {
        return DIRTY;
    }

    public CalendarDto setDIRTY(int DIRTY)
    {
        this.DIRTY = DIRTY;
        return this;
    }

    public String getMUTATORS()
    {
        return MUTATORS;
    }

    public CalendarDto setMUTATORS(String MUTATORS)
    {
        this.MUTATORS = MUTATORS;
        return this;
    }

    public String get_SYNC_ID()
    {
        return _SYNC_ID;
    }

    public CalendarDto set_SYNC_ID(String _SYNC_ID)
    {
        this._SYNC_ID = _SYNC_ID;
        return this;
    }

    public String getCAL_SYNC1()
    {
        return CAL_SYNC1;
    }

    public CalendarDto setCAL_SYNC1(String CAL_SYNC1)
    {
        this.CAL_SYNC1 = CAL_SYNC1;
        return this;
    }

    public String getCAL_SYNC2()
    {
        return CAL_SYNC2;
    }

    public CalendarDto setCAL_SYNC2(String CAL_SYNC2)
    {
        this.CAL_SYNC2 = CAL_SYNC2;
        return this;
    }

    public String getCAL_SYNC3()
    {
        return CAL_SYNC3;
    }

    public CalendarDto setCAL_SYNC3(String CAL_SYNC3)
    {
        this.CAL_SYNC3 = CAL_SYNC3;
        return this;
    }

    public String getCAL_SYNC4()
    {
        return CAL_SYNC4;
    }

    public CalendarDto setCAL_SYNC4(String CAL_SYNC4)
    {
        this.CAL_SYNC4 = CAL_SYNC4;
        return this;
    }

    public String getCAL_SYNC5()
    {
        return CAL_SYNC5;
    }

    public CalendarDto setCAL_SYNC5(String CAL_SYNC5)
    {
        this.CAL_SYNC5 = CAL_SYNC5;
        return this;
    }

    public String getCAL_SYNC6()
    {
        return CAL_SYNC6;
    }

    public CalendarDto setCAL_SYNC6(String CAL_SYNC6)
    {
        this.CAL_SYNC6 = CAL_SYNC6;
        return this;
    }

    public String getCAL_SYNC7()
    {
        return CAL_SYNC7;
    }

    public CalendarDto setCAL_SYNC7(String CAL_SYNC7)
    {
        this.CAL_SYNC7 = CAL_SYNC7;
        return this;
    }

    public String getCAL_SYNC8()
    {
        return CAL_SYNC8;
    }

    public CalendarDto setCAL_SYNC8(String CAL_SYNC8)
    {
        this.CAL_SYNC8 = CAL_SYNC8;
        return this;
    }

    public String getCAL_SYNC9()
    {
        return CAL_SYNC9;
    }

    public CalendarDto setCAL_SYNC9(String CAL_SYNC9)
    {
        this.CAL_SYNC9 = CAL_SYNC9;
        return this;
    }

    public String getCAL_SYNC10()
    {
        return CAL_SYNC10;
    }

    public CalendarDto setCAL_SYNC10(String CAL_SYNC10)
    {
        this.CAL_SYNC10 = CAL_SYNC10;
        return this;
    }
}
