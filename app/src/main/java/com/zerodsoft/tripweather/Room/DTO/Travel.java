package com.zerodsoft.tripweather.Room.DTO;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "travel_table")
public class Travel implements Serializable
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "travel_id")
    private int id;

    @ColumnInfo(name = "travel_name")
    private String name;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
