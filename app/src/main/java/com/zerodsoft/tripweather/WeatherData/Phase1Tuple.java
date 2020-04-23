package com.zerodsoft.tripweather.WeatherData;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Phase1Tuple
{
    @PrimaryKey
    private int area_id;

    @ColumnInfo(name = "phase_1")
    private String phase1;

    public int getArea_id()
    {
        return area_id;
    }

    public void setArea_id(int id)
    {
        this.area_id = id;
    }

    public String getPhase1()
    {
        return phase1;
    }

    public void setPhase1(String phase1)
    {
        this.phase1 = phase1;
    }
}
