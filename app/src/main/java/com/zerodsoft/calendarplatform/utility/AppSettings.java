package com.zerodsoft.calendarplatform.utility;

import android.graphics.Color;

public class AppSettings
{
    private static int googleEventBackgroundColor = Color.GRAY;
    private static int localEventBackgroundColor = Color.BLUE;
    private static int localEventTextColor = Color.WHITE;
    private static int googleEventTextColor = Color.WHITE;

    public static int getGoogleEventBackgroundColor()
    {
        return googleEventBackgroundColor;
    }

    public static void setGoogleEventBackgroundColor(int googleEventBackgroundColor)
    {
        AppSettings.googleEventBackgroundColor = googleEventBackgroundColor;
    }

    public static int getLocalEventBackgroundColor()
    {
        return localEventBackgroundColor;
    }

    public static void setLocalEventBackgroundColor(int localEventBackgroundColor)
    {
        AppSettings.localEventBackgroundColor = localEventBackgroundColor;
    }

    public static int getLocalEventTextColor()
    {
        return localEventTextColor;
    }

    public static void setLocalEventTextColor(int localEventTextColor)
    {
        AppSettings.localEventTextColor = localEventTextColor;
    }

    public static int getGoogleEventTextColor()
    {
        return googleEventTextColor;
    }

    public static void setGoogleEventTextColor(int googleEventTextColor)
    {
        AppSettings.googleEventTextColor = googleEventTextColor;
    }
}
