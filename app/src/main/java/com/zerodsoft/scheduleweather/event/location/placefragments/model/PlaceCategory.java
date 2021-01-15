package com.zerodsoft.scheduleweather.event.location.placefragments.model;

public class PlaceCategory
{
    private final int index;
    private final String categoryName;
    private final String categoryCode;

    public PlaceCategory(int index, String categoryName, String categoryCode)
    {
        this.index = index;
        this.categoryName = categoryName;
        this.categoryCode = categoryCode;
    }

    public PlaceCategory(int index, String categoryName)
    {
        this(index, categoryName, null);
    }

    public int getIndex()
    {
        return index;
    }

    public String getCategoryName()
    {
        return categoryName;
    }

    public String getCategoryCode()
    {
        return categoryCode;
    }
}
