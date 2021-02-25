package com.zerodsoft.scheduleweather.retrofit;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "place_category_table")
public class PlaceCategory
{
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "description")
    private String description;

    @Ignore
    private String code;

    @Ignore
    private boolean isDefault;

    public PlaceCategory(String description, String code)
    {
        this.description = description;
        this.code = code;
    }

    public PlaceCategory(String description, boolean isDefault)
    {
        this.description = description;
        this.isDefault = isDefault;
    }

    public void setDefault(boolean aDefault)
    {
        isDefault = aDefault;
    }

    public boolean isDefault()
    {
        return isDefault;
    }

    public int getId()
    {
        return id;
    }

    public String getDescription()
    {
        return description;
    }

    public String getCode()
    {
        return code;
    }
}
