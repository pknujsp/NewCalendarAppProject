package com.zerodsoft.tripweather.ScheduleList;

import android.util.SparseArray;
import android.util.SparseIntArray;

import com.zerodsoft.tripweather.Room.DTO.Schedule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


public class ScheduleTable
{
    private ArrayList<ScheduleHeader> scheduleHeaders;
    private SparseArray<ViewType> viewTypes;
    private int size;

    public ScheduleTable(ArrayList<Schedule> schedules)
    {
        addSchedules(initializeTable(schedules));
        calcSchedulesCount();
    }

    public Object get(int position)
    {
        ViewType viewType = viewTypes.get(position);

        if (viewType.getViewType() == ScheduleListAdapter.HEADER)
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd EEE");
            Date date = scheduleHeaders.get(viewType.getHeaderIndex()).getDate();

            return dateFormat.format(date);
        } else
        {
            return scheduleHeaders.get(viewType.getHeaderIndex()).getScheduleNode(viewType.getChildIndex());
        }
    }

    public int getSize()
    {
        return size;
    }

    public SparseArray<ViewType> getViewTypes()
    {
        return viewTypes;
    }

    public int getViewType(int position)
    {
        return viewTypes.get(position).getViewType();
    }

    public int getHeaderIndex(int position)
    {
        return viewTypes.get(position).getHeaderIndex();
    }

    private void calcSchedulesCount()
    {
        int count = 0;
        int headerIndex = 0;
        this.viewTypes = new SparseArray<>();

        for (ScheduleHeader header : scheduleHeaders)
        {
            viewTypes.put(count, new ViewType().setViewType(ScheduleListAdapter.HEADER).setHeaderIndex(headerIndex));
            count++;

            for (int i = 0; i < header.getSchedules().size(); ++i)
            {
                viewTypes.put(count, new ViewType().setViewType(ScheduleListAdapter.CHILD).setHeaderIndex(headerIndex).setChildIndex(i));
                count++;
            }
            headerIndex++;
        }

        this.size = count;
    }

    public void addSchedules(ArrayList<Schedule> schedules)
    {
        for (Schedule schedule : schedules)
        {
            for (int i = 0; i < scheduleHeaders.size(); i++)
            {
                if (schedule.getDate().equals(scheduleHeaders.get(i).getDate()))
                {
                    scheduleHeaders.get(i).addSchedule(new ScheduleNode().setSchedule(schedule).setViewType(ScheduleListAdapter.CHILD));
                    break;
                }
            }
        }
    }

    private ArrayList<Schedule> initializeTable(ArrayList<Schedule> schedules)
    {
        int size = 0;

        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        SortedSet<Date> dateSet = new TreeSet<>();
        ArrayList<Schedule> fixedScheduleList = new ArrayList<>();

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
                Schedule fixedSchedule = new Schedule();

                fixedSchedule.setDate(startDate.getTime());
                fixedSchedule.setSchedule_id(schedule.getSchedule_id());
                fixedSchedule.setParentId(schedule.getParentId());
                fixedSchedule.setAreaName(schedule.getAreaName());
                fixedSchedule.setAreaX(schedule.getAreaX());
                fixedSchedule.setAreaY(schedule.getAreaY());

                fixedScheduleList.add(fixedSchedule);
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
            scheduleHeaders.add(new ScheduleHeader().setDate(date).setSchedules(new ArrayList<>()).setViewType(ScheduleListAdapter.HEADER));
        }

        return fixedScheduleList;
    }

    public class ViewType
    {
        private int viewType;
        private int headerIndex;
        private int childIndex;

        public int getViewType()
        {
            return viewType;
        }

        public ViewType setViewType(int viewType)
        {
            this.viewType = viewType;
            return this;
        }

        public int getHeaderIndex()
        {
            return headerIndex;
        }

        public ViewType setHeaderIndex(int headerIndex)
        {
            this.headerIndex = headerIndex;
            return this;
        }

        public int getChildIndex()
        {
            return childIndex;
        }

        public ViewType setChildIndex(int childIndex)
        {
            this.childIndex = childIndex;
            return this;
        }
    }
}
