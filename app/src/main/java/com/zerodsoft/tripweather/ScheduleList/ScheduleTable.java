package com.zerodsoft.tripweather.ScheduleList;

import android.util.SparseArray;

import com.zerodsoft.tripweather.Room.DTO.Schedule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


public class ScheduleTable
{
    private ArrayList<ScheduleHeader> scheduleHeaders;

    public ScheduleTable(ArrayList<Schedule> schedules)
    {
        initializeTable(schedules);
    }

    public void addSchedule(Schedule schedule)
    {

    }

    private void initializeTable(ArrayList<Schedule> schedules)
    {
        int size = 0;

        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        SortedSet<Date> dateSet = new TreeSet<>();

        for (Schedule schedule : schedules)
        {
            startDate.clear();
            endDate.clear();

            String[] separatedStartDate = schedule.getStartDate().split("/");
            String[] separatedEndDate = schedule.getEndDate().split("/");

            startDate.set(Integer.parseInt(separatedStartDate[0]), Integer.parseInt(separatedStartDate[1]) - 1, Integer.parseInt(separatedStartDate[2]));
            endDate.set(Integer.parseInt(separatedEndDate[0]), Integer.parseInt(separatedEndDate[1]) - 1, Integer.parseInt(separatedEndDate[2]));

            while (startDate.before(endDate) || startDate.equals(endDate))
            {
                dateSet.add(startDate.getTime());
                size++;
                startDate.add(Calendar.DATE, 1);
            }
        }

        this.scheduleHeaders = new ArrayList<>(size);
        Iterator<Date> iterator = dateSet.iterator();

        while (iterator.hasNext())
        {
            Date date = iterator.next();
            scheduleHeaders.add(new ScheduleHeader().setDate(date).setSchedules(new ArrayList<>()));
        }
        size++;
    }
}
