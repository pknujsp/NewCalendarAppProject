package com.zerodsoft.scheduleweather.Room;

import com.zerodsoft.scheduleweather.Room.DTO.ScheduleDTO;

import java.util.ArrayList;
import java.util.List;

public class DBController
{
    private static List<ScheduleDTO> scheduleList = new ArrayList<>();
    private static DBController dbController;

    private DBController()
    {
    }

    public static DBController getInstance()
    {
        if (dbController == null)
        {
            dbController = new DBController();
        }
        return dbController;
    }

    public static List<ScheduleDTO> getScheduleList()
    {
        return scheduleList;
    }

    public void updateScheduleData()
    {

    }

    public void selectScheduleData()
    {

    }
}
