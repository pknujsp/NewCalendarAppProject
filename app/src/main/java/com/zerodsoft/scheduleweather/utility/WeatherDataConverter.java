package com.zerodsoft.scheduleweather.utility;

import android.content.Context;

import com.zerodsoft.scheduleweather.R;

public class WeatherDataConverter
{
    public static Context context;
    /*
- 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
- 강수형태(PTY) 코드 : 없음(0), 비(1), 비/눈/진눈깨비(2), 눈(3), 소나기(4), 빗방울(5), 빗방울/눈날림(6), 눈날림(7)
SKY와 PTY는 별개의 데이터
     */

    private WeatherDataConverter()
    {
    }

    public static int getSkyDrawableId(String sky, String precipitaionForm, boolean day)
    {
        int id = 0;

        switch (sky)
        {
            case "1":
                if (day)
                {
                    id = R.drawable.sunny_day_icon;
                } else
                {
                    id = R.drawable.sunny_night_icon;
                }
                break;
            case "3":
                if (day)
                {
                    id = R.drawable.cloud_day_icon;
                } else
                {
                    id = R.drawable.cloud_night_icon;
                }
                break;
            case "4":
                id = R.drawable.cloudy_icon;
                break;
        }

        switch (precipitaionForm)
        {
            case "1":
            case "5":
                id = R.drawable.rain_icon;
                break;
            case "2":
            case "6":
                id = R.drawable.sleet_icon;
                break;
            case "3":
            case "7":
                id = R.drawable.snow_icon;
                break;
            case "4":
                id = R.drawable.shower_icon;
                break;
        }

        return id;
    }

    public static String convertPrecipitationForm(String value)
    {
        String convertedValue = null;

        switch (value)
        {
            case "0":
                convertedValue = "";
                break;
            case "1":
            case "5":
                convertedValue = context.getString(R.string.rain);
                break;
            case "2":
            case "6":
                convertedValue = context.getString(R.string.sleet);
                break;
            case "3":
            case "7":
                convertedValue = context.getString(R.string.snow);
                break;
            case "4":
                convertedValue = context.getString(R.string.shower);
                break;
        }
        return convertedValue;
    }

    public static String convertWindDirection(String value)
    {
        final int windDirectionValue = (int) ((Integer.valueOf(value).intValue() + 22.5 * 0.5) / 22.5);

        String convertedValue = null;

        switch (windDirectionValue)
        {
            case 0:
                convertedValue = context.getString(R.string.n);
                break;
            case 1:
                convertedValue = context.getString(R.string.nne);
                break;
            case 2:
                convertedValue = context.getString(R.string.ne);
                break;
            case 3:
                convertedValue = context.getString(R.string.ene);
                break;
            case 4:
                convertedValue = context.getString(R.string.e);
                break;
            case 5:
                convertedValue = context.getString(R.string.ese);
                break;
            case 6:
                convertedValue = context.getString(R.string.se);
                break;
            case 7:
                convertedValue = context.getString(R.string.sse);
                break;
            case 8:
                convertedValue = context.getString(R.string.s);
                break;
            case 9:
                convertedValue = context.getString(R.string.ssw);
                break;
            case 10:
                convertedValue = context.getString(R.string.sw);
                break;
            case 11:
                convertedValue = context.getString(R.string.wsw);
                break;
            case 12:
                convertedValue = context.getString(R.string.w);
                break;
            case 13:
                convertedValue = context.getString(R.string.wnw);
                break;
            case 14:
                convertedValue = context.getString(R.string.nw);
                break;
            case 15:
                convertedValue = context.getString(R.string.nnw);
                break;
            case 16:
                convertedValue = context.getString(R.string.n);
                break;
        }

        return convertedValue;
    }

    public static String convertSky(String value)
    {
        String convertedValue = null;

        switch (value)
        {
            case "1":
                convertedValue = context.getString(R.string.sky_sunny);
                break;
            case "3":
                convertedValue = context.getString(R.string.sky_cloud);
                break;
            case "4":
                convertedValue = context.getString(R.string.sky_cloudy);
                break;
        }
        return convertedValue;
    }

    public static String getWindSpeedDescription(String windSpeed)
    {
        double speed = Double.valueOf(windSpeed);
        if (speed >= 14)
        {
            return "매우 강한";
        } else if (speed >= 9)
        {
            return "강한";
        } else if (speed >= 4)
        {
            return "약간 강한";
        } else
        {
            return "약한";
        }
    }
}