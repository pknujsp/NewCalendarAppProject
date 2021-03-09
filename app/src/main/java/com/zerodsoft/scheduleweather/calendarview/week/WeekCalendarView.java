package com.zerodsoft.scheduleweather.calendarview.week;

import android.content.ContentValues;
import android.provider.CalendarContract;

import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendarview.interfaces.CalendarViewInitializer;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IControlEvent;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemLongClickListener;
import com.zerodsoft.scheduleweather.event.util.EventUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WeekCalendarView implements CalendarViewInitializer
{
    private WeekHeaderView weekHeaderView;
    private WeekView weekView;
    private IControlEvent iControlEvent;
    private IConnectedCalendars iConnectedCalendars;
    private Map<Integer, CalendarInstance> resultMap;
    private Date[] daysOfWeek = new Date[8];

    public WeekCalendarView(WeekHeaderView weekHeaderView, WeekView weekView)
    {
        this.weekHeaderView = weekHeaderView;
        this.weekView = weekView;
    }

    public WeekHeaderView getWeekHeaderView()
    {
        return weekHeaderView;
    }

    public WeekView getWeekView()
    {
        return weekView;
    }

    public Date[] getDaysOfWeek()
    {
        return daysOfWeek;
    }


    @Override
    public void init(Calendar copiedCalendar, OnEventItemLongClickListener onEventItemLongClickListener, OnEventItemClickListener onEventItemClickListener, IControlEvent iControlEvent, IConnectedCalendars iConnectedCalendars)
    {
        this.iConnectedCalendars = iConnectedCalendars;
        this.iControlEvent = iControlEvent;

        weekView.init(null, onEventItemLongClickListener, onEventItemClickListener, iControlEvent, null);
        weekHeaderView.init(null, onEventItemLongClickListener, onEventItemClickListener, iControlEvent, null);

        for (int i = 0; i < 8; i++)
        {
            daysOfWeek[i] = copiedCalendar.getTime();
            copiedCalendar.add(Calendar.DATE, 1);
        }

        weekView.setDaysOfWeek(daysOfWeek);
        weekHeaderView.setDaysOfWeek(daysOfWeek);

        setInstances(iControlEvent.getInstances(daysOfWeek[0].getTime(), daysOfWeek[7].getTime()));
        setEventTable();
    }

    @Override
    public void setInstances(Map<Integer, CalendarInstance> resultMap)
    {
        //선택되지 않은 캘린더는 제외
        this.resultMap = resultMap;
        List<ContentValues> connectedCalendars = iConnectedCalendars.getConnectedCalendars();
        Set<Integer> connectedCalendarIdSet = new HashSet<>();

        for (ContentValues calendar : connectedCalendars)
        {
            connectedCalendarIdSet.add(calendar.getAsInteger(CalendarContract.Calendars._ID));
        }

        List<ContentValues> instances = new ArrayList<>();
        for (Integer calendarIdKey : connectedCalendarIdSet)
        {
            if (resultMap.containsKey(calendarIdKey))
            {
                instances.addAll(resultMap.get(calendarIdKey).getInstanceList());
            }
        }

        // 데이터를 일정 길이의 내림차순으로 정렬
        instances.sort(EventUtil.INSTANCE_COMPARATOR);

        weekView.setInstances(instances);
        weekHeaderView.setInstances(instances);
    }

    @Override
    public void setInstances(List<ContentValues> instances)
    {

    }

    @Override
    public void setEventTable()
    {
        weekView.setEventTable();
        weekHeaderView.setEventTable();
    }

    public void refresh()
    {
        setInstances(iControlEvent.getInstances(daysOfWeek[0].getTime(), daysOfWeek[7].getTime()));
        setEventTable();
    }

}
