package com.zerodsoft.scheduleweather.etc;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.CalendarContract;

import java.util.Comparator;

public class CalendarUtil
{
    private CalendarUtil()
    {
    }

    public static final Comparator<ContentValues> INSTANCE_COMPARATOR = new Comparator<ContentValues>()
    {
        @Override
        public int compare(ContentValues t1, ContentValues t2)
        {
            // 양수이면 변경된다
            long t1Begin = t1.getAsLong(CalendarContract.Instances.BEGIN);
            long t1End = t1.getAsLong(CalendarContract.Instances.END);
            long t2Begin = t2.getAsLong(CalendarContract.Instances.BEGIN);
            long t2End = t2.getAsLong(CalendarContract.Instances.END);

            if ((t1End - t1Begin) < (t2End - t2Begin))
            {
                return 1;
            } else
            {
                return 0;
            }
        }
    };

    public static int getColor(int color)
    {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        return Color.HSVToColor(hsv);
    }


}
