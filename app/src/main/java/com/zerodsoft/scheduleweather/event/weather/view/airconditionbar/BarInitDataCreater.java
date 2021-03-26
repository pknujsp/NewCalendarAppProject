package com.zerodsoft.scheduleweather.event.weather.view.airconditionbar;

import android.content.Context;
import android.graphics.Color;
import android.widget.ListView;

import com.zerodsoft.scheduleweather.R;

import java.util.ArrayList;
import java.util.List;

public class BarInitDataCreater
{
    public static final int FINEDUST = 0;
    public static final int ULTRA_FINEDUST = 1;
    public static final int SO2 = 2;
    public static final int CO = 3;
    public static final int O3 = 4;
    public static final int NO2 = 5;

    private BarInitDataCreater()
    {
    }

    public static List<BarInitData> getBarInitData(Context context, int type)
    {
        String[] references = null;

        switch (type)
        {
            case FINEDUST:
                references = context.getResources().getStringArray(R.array.finddust_reference);
                break;
            case ULTRA_FINEDUST:
                references = context.getResources().getStringArray(R.array.ultrafinddust_reference);
                break;
            case SO2:
                references = context.getResources().getStringArray(R.array.so2_reference);
                break;
            case CO:
                references = context.getResources().getStringArray(R.array.co_reference);
                break;
            case O3:
                references = context.getResources().getStringArray(R.array.o3_reference);
                break;
            case NO2:
                references = context.getResources().getStringArray(R.array.no2_reference);
                break;
        }

        BarInitData good = new BarInitData(Color.BLUE, Double.parseDouble(references[0]), Double.parseDouble(references[1]), context.getString(R.string.good));
        BarInitData normal = new BarInitData(Color.GREEN, Double.parseDouble(references[2]), Double.parseDouble(references[3]), context.getString(R.string.normal));
        BarInitData bad = new BarInitData(Color.CYAN, Double.parseDouble(references[4]), Double.parseDouble(references[5]), context.getString(R.string.bad));
        BarInitData veryBad = new BarInitData(Color.RED, Double.parseDouble(references[6]), Double.parseDouble(references[7]), context.getString(R.string.very_bad));

        List<BarInitData> barInitDataList = new ArrayList<>();

        barInitDataList.add(good);
        barInitDataList.add(normal);
        barInitDataList.add(bad);
        barInitDataList.add(veryBad);

        return barInitDataList;
    }


    public static String getGrade(String grade, Context context)
    {
        if (grade.equals("1"))
        {
            return context.getString(R.string.good);
        } else if (grade.equals("2"))
        {
            return context.getString(R.string.normal);
        } else if (grade.equals("3"))
        {
            return context.getString(R.string.bad);
        } else
        {
            return context.getString(R.string.very_bad);
        }
    }

    public static int getGradeColor(String grade)
    {
        if (grade.equals("1"))
        {
            return Color.BLUE;
        } else if (grade.equals("2"))
        {
            return Color.GREEN;
        } else if (grade.equals("3"))
        {
            return Color.CYAN;
        } else
        {
            return Color.RED;
        }
    }

}
