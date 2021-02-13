package com.zerodsoft.scheduleweather.calendar;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.CalendarContract;

import androidx.annotation.Nullable;

public class CalendarDbHelper extends SQLiteOpenHelper
{
    public static final String DB_NAME = "calendar.db";

    public CalendarDbHelper(@Nullable Context context)
    {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {

    }


    public Cursor getInstance(long calendarId, long instanceId)
    {
        SQLiteDatabase db = getReadableDatabase();
        String selection = CalendarContract.Instances.CALENDAR_ID + "=? AND " + CalendarContract.Instances._ID + "=?";
        String[] selectionArgs = {String.valueOf(calendarId), String.valueOf(instanceId)};

        Cursor cursor = db.query("Instances", null, selection, selectionArgs, null, null, null);
        return cursor;
    }
}
