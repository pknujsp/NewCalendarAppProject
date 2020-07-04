package com.zerodsoft.scheduleweather.Room.DTO;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;


@Entity(tableName = "TB_SCHEDULE_CATEGORY")
public class ScheduleCategoryDTO implements Serializable
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @Ignore
    public static final int GOOGLE_SCHEDULE = 0;

    @Ignore
    public static final int LOCAL_SCHEDULE = 1;

    public int getId()
    {
        return id;
    }

    public ScheduleCategoryDTO setId(int id)
    {
        this.id = id;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public ScheduleCategoryDTO setName(String name)
    {
        this.name = name;
        return this;
    }
}
