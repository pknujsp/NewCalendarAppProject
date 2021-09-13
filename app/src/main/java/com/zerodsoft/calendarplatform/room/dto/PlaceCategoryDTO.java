package com.zerodsoft.calendarplatform.room.dto;

import androidx.annotation.Keep;

import java.util.Objects;

@Keep
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PlaceCategoryDTO that = (PlaceCategoryDTO) o;
        return isCustom == that.isCustom &&
                Objects.equals(description, that.description) &&
                Objects.equals(code, that.code);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(description, code, isCustom);
    }
}
