package com.zerodsoft.scheduleweather.Room;

import com.zerodsoft.scheduleweather.Room.DTO.GoogleScheduleDTO;
import com.zerodsoft.scheduleweather.Room.DTO.LocalScheduleDTO;

import java.util.ArrayList;
import java.util.List;

public class DBController
{
    private static List<GoogleScheduleDTO> googleScheduleList;
    private static List<LocalScheduleDTO> localScheduleList;
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

    public List<GoogleScheduleDTO> getGoogleScheduleList()
    {
        return googleScheduleList;
    }

    public List<LocalScheduleDTO> getLocalScheduleList()
    {
        return localScheduleList;
    }

    public void updateScheduleData()
    {

    }

    public void selectScheduleData()
    {

    }
}
