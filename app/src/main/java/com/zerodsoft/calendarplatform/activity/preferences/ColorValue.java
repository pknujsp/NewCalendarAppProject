package com.zerodsoft.calendarplatform.activity.preferences;

public class ColorValue
{
    private int color;
    private int colorKey;

    public ColorValue(int color, int colorKey)
    {
        this.color = color;
        this.colorKey = colorKey;
    }

    public int getColorKey()
    {
        return colorKey;
    }

    public int getColor()
    {
        return color;
    }
}
