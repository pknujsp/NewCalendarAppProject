package com.zerodsoft.scheduleweather.calendar;

import android.content.ContentValues;
import android.provider.CalendarContract;

public class CalendarInstanceUtil
{
    public static boolean exceptThisInstance(ContentValues instance)
    {
        CalendarProvider calendarProvider = CalendarProvider.getInstance();
        int result = calendarProvider.deleteInstance(instance.getAsLong(CalendarContract.Instances.BEGIN),
                instance.getAsLong(CalendarContract.Instances.EVENT_ID));

        return true;
    }

    public static boolean deleteEvent(ContentValues instance)
    {
        // 참석자 - 알림 - 이벤트 순으로 삭제 (외래키 때문)
        // db column error
        CalendarProvider calendarProvider = CalendarProvider.getInstance();
        calendarProvider.deleteEvent(instance.getAsInteger(CalendarContract.Instances.CALENDAR_ID), instance.getAsLong(CalendarContract.Instances.EVENT_ID));
        // 삭제 완료 후 캘린더 화면으로 나가고, 새로고침한다.
        return true;
    }
}
