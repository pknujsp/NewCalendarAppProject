package com.zerodsoft.scheduleweather.Utility;

import android.graphics.Color;

public class AppSettings
{
    private static int googleEventColor = Color.GREEN;
    private static int localEventColor = Color.BLUE;

    public static int getGoogleEventColor()
    {
        return googleEventColor;
    }

    public static void setGoogleEventColor(int googleEventColor)
    {
        AppSettings.googleEventColor = googleEventColor;
    }

    public static int getLocalEventColor()
    {
        return localEventColor;
    }

    public static void setLocalEventColor(int localEventColor)
    {
        AppSettings.localEventColor = localEventColor;
    }
}
