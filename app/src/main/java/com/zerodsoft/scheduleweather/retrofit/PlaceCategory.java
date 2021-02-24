package com.zerodsoft.scheduleweather.retrofit;

public class PlaceCategory
{
    private final int id;
    private final String description;
    private final String code;
    private boolean isDefault;

    public PlaceCategory(int id, String description, String code)
    {
        this.id = id;
        this.description = description;
        this.code = code;
    }

    public PlaceCategory(int id, String description)
    {
        this(id, description, null);
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
