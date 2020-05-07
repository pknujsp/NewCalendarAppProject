package com.zerodsoft.tripweather.ScheduleList;

import android.content.pm.PackageManager;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.zerodsoft.tripweather.Room.DTO.Schedule;
import com.zerodsoft.tripweather.Utility.Clock;

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
    private ArrayList<Integer> viewTypeIntList;
    private HashMap<Integer, Long> headerDateMaps;
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
            // HEADER
            Date date = scheduleHeaders.get(viewType.getHeaderIndex()).getDate();
            return Clock.dateDayNameFormatSlash.format(date.getTime());
        } else
        {
            // CHILD
            return scheduleHeaders.get(viewType.getHeaderIndex()).getScheduleNode(viewType.getChildIndex());
        }
    }

    public ScheduleNode getNode(int headerIdx, int nodeIdx)
    {
        return scheduleHeaders.get(headerIdx).getScheduleNode(nodeIdx);
    }

    public String getHeaderDate(int position)
    {
        String date = null;

        if (viewTypeIntList.size() == 1)
        {
            date = Clock.dateFormat.format(headerDateMaps.get(0));
        } else
        {
            for (int i = 0; i < viewTypeIntList.size() - 1; i++)
            {
                int currentIdx = viewTypeIntList.get(i).intValue();
                int nextIdx = viewTypeIntList.get(i + 1).intValue();

                if (position > currentIdx && position < nextIdx)
                {
                    date = Clock.dateFormat.format(headerDateMaps.get(currentIdx));
                    break;
                } else if (i == viewTypeIntList.size() - 2)
                {
                    date = Clock.dateFormat.format(headerDateMaps.get(nextIdx));
                    break;
                }
            }
        }
        return date;
    }

    public int getNodesCount(int headerIdx)
    {
        return scheduleHeaders.get(headerIdx).getSchedules().size();
    }

    public int getSize()
    {
        return viewTypes.size();
    }

    public int getAllSchedulesSize()
    {
        return this.size;
    }

    public int getHeadersSize()
    {
        return scheduleHeaders.size();
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
        this.headerDateMaps = new HashMap<>();
        this.viewTypeIntList = new ArrayList<>();

        for (ScheduleHeader header : scheduleHeaders)
        {
            viewTypes.put(count, new ViewType().setViewType(ScheduleListAdapter.HEADER).setHeaderIndex(headerIndex));
            // header의 index를 저장
            viewTypeIntList.add(count);
            headerDateMaps.put(count, header.getDate().getTime());

            for (int i = 0; i < header.getSchedules().size(); i++)
            {
                count++;
                viewTypes.put(count, new ViewType().setViewType(ScheduleListAdapter.CHILD).setHeaderIndex(headerIndex).setChildIndex(i));
            }
            headerIndex++;
            count++;
        }
        this.size = viewTypes.size() - headerDateMaps.size();
    }

    public void addSchedules(ArrayList<Schedule> fixedScheduleList)
    {
        for (Schedule schedule : fixedScheduleList)
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

    private ArrayList<Schedule> initializeTable(ArrayList<Schedule> scheduleList)
    {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        SortedSet<Date> dateSet = new TreeSet<>();
        ArrayList<Schedule> fixedScheduleList = new ArrayList<>();

        for (Schedule schedule : scheduleList)
        {
            startDate.clear();
            endDate.clear();

            String[] separatedStartDate = schedule.getStartDate().split("/");
            String[] separatedEndDate = schedule.getEndDate().split("/");

            startDate.set(Integer.parseInt(separatedStartDate[0]), Integer.parseInt(separatedStartDate[1]) - 1, Integer.parseInt(separatedStartDate[2]));
            endDate.set(Integer.parseInt(separatedEndDate[0]), Integer.parseInt(separatedEndDate[1]) - 1, Integer.parseInt(separatedEndDate[2]));

            while (!startDate.after(endDate))
            {
                Schedule fixedSchedule = new Schedule();

                fixedSchedule.setDate(startDate.getTime());
                fixedSchedule.setSchedule_id(schedule.getSchedule_id());
                fixedSchedule.setParentId(schedule.getParentId());
                fixedSchedule.setAreaId(schedule.getAreaId());
                fixedSchedule.setAreaName(schedule.getAreaName());
                fixedSchedule.setAreaX(schedule.getAreaX());
                fixedSchedule.setAreaY(schedule.getAreaY());

                fixedScheduleList.add(fixedSchedule);
                dateSet.add(startDate.getTime());

                startDate.add(Calendar.DATE, 1);
            }
        }

        this.scheduleHeaders = new ArrayList<>();
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
