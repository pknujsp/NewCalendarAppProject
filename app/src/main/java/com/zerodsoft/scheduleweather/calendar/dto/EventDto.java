package com.zerodsoft.scheduleweather.calendar.dto;

public class EventDto
{
    // 제목
    private String TITLE;
    // 시작 날짜
    private long DTSTART;
    // 종료 날짜
    private long DTEND;
    // 접근 수준 (참석자만, 공개, 기본)
    private int ACCESS_LEVEL;
    // 하루종일 일정인지 여부
    private boolean ALL_DAY;
    // 일정 상태 : 바쁨, 자유, 가변적
    private int AVAILABILITY;
    // 이벤트가 저장될 캘린더의 ID
    private int CALENDAR_ID;
    // 참석자 초대 가능여부
    private boolean CAN_INVITE_OTHERS;
    // 설명(내용)
    private String DESCRIPTION;
    // 이벤트 색상
    private int DISPLAY_COLOR;
    // 반복
    private String DURATION;
    // A secondary color for the individual event. This should only be updated by the sync adapter for a given account.
    private int EVENT_COLOR;
    private int EVENT_COLOR_KEY;
    // 이벤트 종료 시간의 시간대
    private String EVENT_END_TIMEZONE;
    // 위치
    private String EVENT_LOCATION;
    // 이벤트의 시간대
    private String EVENT_TIMEZONE;
    // 반복 예외
    private String EXDATE;
    // 반복 예외 규칙
    private String EXRULE;
    private String RDATE;
    private String RRULE;

    private String CUSTOM_APP_PACKAGE;
    private String CUSTOM_APP_URI;
    private boolean GUESTS_CAN_INVITE_OTHERS;
    private boolean GUESTS_CAN_SEE_GUESTS;
    private boolean GUESTS_CAN_MODIFY;
    private boolean HAS_ALARM;
    private boolean HAS_ATTENDEE_DATA;
    private boolean HAS_EXTENDED_PROPERTIES;
    private boolean LAST_SYNCED;
    private boolean ORIGINAL_ALL_DAY;
    private String IS_ORGANIZER;
    private String ORGANIZER;
    private String ORIGINAL_ID;
    private String ORIGINAL_SYNC_ID;
    private long LAST_DATE;
    private long ORIGINAL_INSTANCE_TIME;


    private String OWNER_ACCOUNT;
    private String ACCOUNT_NAME;
    private String ACCOUNT_TYPE;
    private int SELF_ATTENDEE_STATUS;
    private int STATUS;

    private int DIRTY;
    private String MUTATORS;
    private String _SYNC_ID;

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

    public EventDto()
    {
    }

    public String getTITLE()
    {
        return TITLE;
    }

    public EventDto setTITLE(String TITLE)
    {
        this.TITLE = TITLE;
        return this;
    }

    public String getOWNER_ACCOUNT()
    {
        return OWNER_ACCOUNT;
    }

    public EventDto setOWNER_ACCOUNT(String OWNER_ACCOUNT)
    {
        this.OWNER_ACCOUNT = OWNER_ACCOUNT;
        return this;
    }

    public String getACCOUNT_NAME()
    {
        return ACCOUNT_NAME;
    }

    public EventDto setACCOUNT_NAME(String ACCOUNT_NAME)
    {
        this.ACCOUNT_NAME = ACCOUNT_NAME;
        return this;
    }

    public String getACCOUNT_TYPE()
    {
        return ACCOUNT_TYPE;
    }

    public EventDto setACCOUNT_TYPE(String ACCOUNT_TYPE)
    {
        this.ACCOUNT_TYPE = ACCOUNT_TYPE;
        return this;
    }

    public long getDTSTART()
    {
        return DTSTART;
    }

    public EventDto setDTSTART(long DTSTART)
    {
        this.DTSTART = DTSTART;
        return this;
    }

    public long getDTEND()
    {
        return DTEND;
    }

    public EventDto setDTEND(long DTEND)
    {
        this.DTEND = DTEND;
        return this;
    }

    public int getACCESS_LEVEL()
    {
        return ACCESS_LEVEL;
    }

    public EventDto setACCESS_LEVEL(int ACCESS_LEVEL)
    {
        this.ACCESS_LEVEL = ACCESS_LEVEL;
        return this;
    }

    public boolean isALL_DAY()
    {
        return ALL_DAY;
    }

    public EventDto setALL_DAY(boolean ALL_DAY)
    {
        this.ALL_DAY = ALL_DAY;
        return this;
    }

    public int getAVAILABILITY()
    {
        return AVAILABILITY;
    }

    public EventDto setAVAILABILITY(int AVAILABILITY)
    {
        this.AVAILABILITY = AVAILABILITY;
        return this;
    }

    public int getCALENDAR_ID()
    {
        return CALENDAR_ID;
    }

    public EventDto setCALENDAR_ID(int CALENDAR_ID)
    {
        this.CALENDAR_ID = CALENDAR_ID;
        return this;
    }

    public boolean isCAN_INVITE_OTHERS()
    {
        return CAN_INVITE_OTHERS;
    }

    public EventDto setCAN_INVITE_OTHERS(boolean CAN_INVITE_OTHERS)
    {
        this.CAN_INVITE_OTHERS = CAN_INVITE_OTHERS;
        return this;
    }

    public String getDESCRIPTION()
    {
        return DESCRIPTION;
    }

    public EventDto setDESCRIPTION(String DESCRIPTION)
    {
        this.DESCRIPTION = DESCRIPTION;
        return this;
    }

    public int getDISPLAY_COLOR()
    {
        return DISPLAY_COLOR;
    }

    public EventDto setDISPLAY_COLOR(int DISPLAY_COLOR)
    {
        this.DISPLAY_COLOR = DISPLAY_COLOR;
        return this;
    }

    public String getDURATION()
    {
        return DURATION;
    }

    public EventDto setDURATION(String DURATION)
    {
        this.DURATION = DURATION;
        return this;
    }

    public int getEVENT_COLOR()
    {
        return EVENT_COLOR;
    }

    public EventDto setEVENT_COLOR(int EVENT_COLOR)
    {
        this.EVENT_COLOR = EVENT_COLOR;
        return this;
    }

    public int getEVENT_COLOR_KEY()
    {
        return EVENT_COLOR_KEY;
    }

    public EventDto setEVENT_COLOR_KEY(int EVENT_COLOR_KEY)
    {
        this.EVENT_COLOR_KEY = EVENT_COLOR_KEY;
        return this;
    }

    public String getEVENT_END_TIMEZONE()
    {
        return EVENT_END_TIMEZONE;
    }

    public EventDto setEVENT_END_TIMEZONE(String EVENT_END_TIMEZONE)
    {
        this.EVENT_END_TIMEZONE = EVENT_END_TIMEZONE;
        return this;
    }

    public String getEVENT_LOCATION()
    {
        return EVENT_LOCATION;
    }

    public EventDto setEVENT_LOCATION(String EVENT_LOCATION)
    {
        this.EVENT_LOCATION = EVENT_LOCATION;
        return this;
    }

    public String getEVENT_TIMEZONE()
    {
        return EVENT_TIMEZONE;
    }

    public EventDto setEVENT_TIMEZONE(String EVENT_TIMEZONE)
    {
        this.EVENT_TIMEZONE = EVENT_TIMEZONE;
        return this;
    }

    public String getEXDATE()
    {
        return EXDATE;
    }

    public EventDto setEXDATE(String EXDATE)
    {
        this.EXDATE = EXDATE;
        return this;
    }

    public String getEXRULE()
    {
        return EXRULE;
    }

    public EventDto setEXRULE(String EXRULE)
    {
        this.EXRULE = EXRULE;
        return this;
    }

    public String getRDATE()
    {
        return RDATE;
    }

    public EventDto setRDATE(String RDATE)
    {
        this.RDATE = RDATE;
        return this;
    }

    public String getRRULE()
    {
        return RRULE;
    }

    public EventDto setRRULE(String RRULE)
    {
        this.RRULE = RRULE;
        return this;
    }

    public String getCUSTOM_APP_PACKAGE()
    {
        return CUSTOM_APP_PACKAGE;
    }

    public EventDto setCUSTOM_APP_PACKAGE(String CUSTOM_APP_PACKAGE)
    {
        this.CUSTOM_APP_PACKAGE = CUSTOM_APP_PACKAGE;
        return this;
    }

    public String getCUSTOM_APP_URI()
    {
        return CUSTOM_APP_URI;
    }

    public EventDto setCUSTOM_APP_URI(String CUSTOM_APP_URI)
    {
        this.CUSTOM_APP_URI = CUSTOM_APP_URI;
        return this;
    }

    public boolean isGUESTS_CAN_INVITE_OTHERS()
    {
        return GUESTS_CAN_INVITE_OTHERS;
    }

    public EventDto setGUESTS_CAN_INVITE_OTHERS(boolean GUESTS_CAN_INVITE_OTHERS)
    {
        this.GUESTS_CAN_INVITE_OTHERS = GUESTS_CAN_INVITE_OTHERS;
        return this;
    }

    public boolean isGUESTS_CAN_SEE_GUESTS()
    {
        return GUESTS_CAN_SEE_GUESTS;
    }

    public EventDto setGUESTS_CAN_SEE_GUESTS(boolean GUESTS_CAN_SEE_GUESTS)
    {
        this.GUESTS_CAN_SEE_GUESTS = GUESTS_CAN_SEE_GUESTS;
        return this;
    }

    public boolean isGUESTS_CAN_MODIFY()
    {
        return GUESTS_CAN_MODIFY;
    }

    public EventDto setGUESTS_CAN_MODIFY(boolean GUESTS_CAN_MODIFY)
    {
        this.GUESTS_CAN_MODIFY = GUESTS_CAN_MODIFY;
        return this;
    }

    public boolean isHAS_ALARM()
    {
        return HAS_ALARM;
    }

    public EventDto setHAS_ALARM(boolean HAS_ALARM)
    {
        this.HAS_ALARM = HAS_ALARM;
        return this;
    }

    public boolean isHAS_ATTENDEE_DATA()
    {
        return HAS_ATTENDEE_DATA;
    }

    public EventDto setHAS_ATTENDEE_DATA(boolean HAS_ATTENDEE_DATA)
    {
        this.HAS_ATTENDEE_DATA = HAS_ATTENDEE_DATA;
        return this;
    }

    public boolean isHAS_EXTENDED_PROPERTIES()
    {
        return HAS_EXTENDED_PROPERTIES;
    }

    public EventDto setHAS_EXTENDED_PROPERTIES(boolean HAS_EXTENDED_PROPERTIES)
    {
        this.HAS_EXTENDED_PROPERTIES = HAS_EXTENDED_PROPERTIES;
        return this;
    }

    public boolean isLAST_SYNCED()
    {
        return LAST_SYNCED;
    }

    public EventDto setLAST_SYNCED(boolean LAST_SYNCED)
    {
        this.LAST_SYNCED = LAST_SYNCED;
        return this;
    }

    public boolean isORIGINAL_ALL_DAY()
    {
        return ORIGINAL_ALL_DAY;
    }

    public EventDto setORIGINAL_ALL_DAY(boolean ORIGINAL_ALL_DAY)
    {
        this.ORIGINAL_ALL_DAY = ORIGINAL_ALL_DAY;
        return this;
    }

    public String getIS_ORGANIZER()
    {
        return IS_ORGANIZER;
    }

    public EventDto setIS_ORGANIZER(String IS_ORGANIZER)
    {
        this.IS_ORGANIZER = IS_ORGANIZER;
        return this;
    }

    public String getORGANIZER()
    {
        return ORGANIZER;
    }

    public EventDto setORGANIZER(String ORGANIZER)
    {
        this.ORGANIZER = ORGANIZER;
        return this;
    }

    public String getORIGINAL_ID()
    {
        return ORIGINAL_ID;
    }

    public EventDto setORIGINAL_ID(String ORIGINAL_ID)
    {
        this.ORIGINAL_ID = ORIGINAL_ID;
        return this;
    }

    public String getORIGINAL_SYNC_ID()
    {
        return ORIGINAL_SYNC_ID;
    }

    public EventDto setORIGINAL_SYNC_ID(String ORIGINAL_SYNC_ID)
    {
        this.ORIGINAL_SYNC_ID = ORIGINAL_SYNC_ID;
        return this;
    }

    public long getLAST_DATE()
    {
        return LAST_DATE;
    }

    public EventDto setLAST_DATE(long LAST_DATE)
    {
        this.LAST_DATE = LAST_DATE;
        return this;
    }

    public long getORIGINAL_INSTANCE_TIME()
    {
        return ORIGINAL_INSTANCE_TIME;
    }

    public EventDto setORIGINAL_INSTANCE_TIME(long ORIGINAL_INSTANCE_TIME)
    {
        this.ORIGINAL_INSTANCE_TIME = ORIGINAL_INSTANCE_TIME;
        return this;
    }

    public int getSELF_ATTENDEE_STATUS()
    {
        return SELF_ATTENDEE_STATUS;
    }

    public EventDto setSELF_ATTENDEE_STATUS(int SELF_ATTENDEE_STATUS)
    {
        this.SELF_ATTENDEE_STATUS = SELF_ATTENDEE_STATUS;
        return this;
    }

    public int getSTATUS()
    {
        return STATUS;
    }

    public EventDto setSTATUS(int STATUS)
    {
        this.STATUS = STATUS;
        return this;
    }

    public int getDIRTY()
    {
        return DIRTY;
    }

    public EventDto setDIRTY(int DIRTY)
    {
        this.DIRTY = DIRTY;
        return this;
    }

    public String getMUTATORS()
    {
        return MUTATORS;
    }

    public EventDto setMUTATORS(String MUTATORS)
    {
        this.MUTATORS = MUTATORS;
        return this;
    }

    public String get_SYNC_ID()
    {
        return _SYNC_ID;
    }

    public EventDto set_SYNC_ID(String _SYNC_ID)
    {
        this._SYNC_ID = _SYNC_ID;
        return this;
    }

    public String getCAL_SYNC1()
    {
        return CAL_SYNC1;
    }

    public EventDto setCAL_SYNC1(String CAL_SYNC1)
    {
        this.CAL_SYNC1 = CAL_SYNC1;
        return this;
    }

    public String getCAL_SYNC2()
    {
        return CAL_SYNC2;
    }

    public EventDto setCAL_SYNC2(String CAL_SYNC2)
    {
        this.CAL_SYNC2 = CAL_SYNC2;
        return this;
    }

    public String getCAL_SYNC3()
    {
        return CAL_SYNC3;
    }

    public EventDto setCAL_SYNC3(String CAL_SYNC3)
    {
        this.CAL_SYNC3 = CAL_SYNC3;
        return this;
    }

    public String getCAL_SYNC4()
    {
        return CAL_SYNC4;
    }

    public EventDto setCAL_SYNC4(String CAL_SYNC4)
    {
        this.CAL_SYNC4 = CAL_SYNC4;
        return this;
    }

    public String getCAL_SYNC5()
    {
        return CAL_SYNC5;
    }

    public EventDto setCAL_SYNC5(String CAL_SYNC5)
    {
        this.CAL_SYNC5 = CAL_SYNC5;
        return this;
    }

    public String getCAL_SYNC6()
    {
        return CAL_SYNC6;
    }

    public EventDto setCAL_SYNC6(String CAL_SYNC6)
    {
        this.CAL_SYNC6 = CAL_SYNC6;
        return this;
    }

    public String getCAL_SYNC7()
    {
        return CAL_SYNC7;
    }

    public EventDto setCAL_SYNC7(String CAL_SYNC7)
    {
        this.CAL_SYNC7 = CAL_SYNC7;
        return this;
    }

    public String getCAL_SYNC8()
    {
        return CAL_SYNC8;
    }

    public EventDto setCAL_SYNC8(String CAL_SYNC8)
    {
        this.CAL_SYNC8 = CAL_SYNC8;
        return this;
    }

    public String getCAL_SYNC9()
    {
        return CAL_SYNC9;
    }

    public EventDto setCAL_SYNC9(String CAL_SYNC9)
    {
        this.CAL_SYNC9 = CAL_SYNC9;
        return this;
    }

    public String getCAL_SYNC10()
    {
        return CAL_SYNC10;
    }

    public EventDto setCAL_SYNC10(String CAL_SYNC10)
    {
        this.CAL_SYNC10 = CAL_SYNC10;
        return this;
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
