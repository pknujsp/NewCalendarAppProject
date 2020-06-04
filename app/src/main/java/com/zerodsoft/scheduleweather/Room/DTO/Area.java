package com.zerodsoft.scheduleweather.Room.DTO;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@SuppressWarnings("serial")
@Entity(tableName = "area_table")
public class Area implements Serializable
{
    @PrimaryKey
    private int area_id;

    @ColumnInfo(name = "phase_1")
    private String phase1;


    @ColumnInfo(name = "phase_2")
    private String phase2;


    @ColumnInfo(name = "phase_3")
    private String phase3;


    @ColumnInfo(name = "x")
    private String x;


    @ColumnInfo(name = "y")
    private String y;

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

    public String getPhase2()
    {
        return phase2;
    }

    public void setPhase2(String phase2)
    {
        this.phase2 = phase2;
    }

    public String getPhase3()
    {
        return phase3;
    }

    public void setPhase3(String phase3)
    {
        this.phase3 = phase3;
    }

    public String getX()
    {
        return x;
    }

    public void setX(String x)
    {
        this.x = x;
    }

    public String getY()
    {
        return y;
    }

    public void setY(String y)
    {
        this.y = y;
    }

    public String toString()
    {
        return phase1 + " " + phase2 + " " + phase3;
    }
}
