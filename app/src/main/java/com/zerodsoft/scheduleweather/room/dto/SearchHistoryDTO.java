package com.zerodsoft.scheduleweather.room.dto;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "search_history_table")
public class SearchHistoryDTO
{
    public static final int LOCATION_SEARCH = 0;
    public static final int FOOD_RESTAURANT_SEARCH = 1;

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "type")
    private Integer type;

    @ColumnInfo(name = "value")
    private String value;

    public SearchHistoryDTO()
    {
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public Integer getType()
    {
        return type;
    }

    public void setType(Integer type)
    {
        this.type = type;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
}
