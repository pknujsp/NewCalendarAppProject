package com.zerodsoft.scheduleweather.navermap.util;

public class LocationUtil
{
    private LocationUtil()
    {
    }

    public static boolean isRestaurant(String placeCategoryStr)
    {
        return placeCategoryStr.contains("음식점");
    }
}
