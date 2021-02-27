package com.zerodsoft.scheduleweather.room.dto;

public class PlaceCategoryDTO
{
    private String description;
    private String code;
    private boolean isCustom;

    public PlaceCategoryDTO()
    {
    }

    public PlaceCategoryDTO(String description, String code)
    {
        this.description = description;
        this.code = code;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public boolean isCustom()
    {
        return isCustom;
    }

    public void setCustom(boolean custom)
    {
        isCustom = custom;
    }
}
