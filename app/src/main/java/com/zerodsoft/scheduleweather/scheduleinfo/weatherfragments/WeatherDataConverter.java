package com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments;

import android.content.Context;

import com.zerodsoft.scheduleweather.R;

public class WeatherDataConverter
{
    public static Context context;
    /*
- 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
- 강수형태(PTY) 코드 : 없음(0), 비(1), 비/눈,진눈개비비2), 눈(3), 소나기(4), 빗방울(5), 빗방울/눈날림(6), 눈날림(7)

SKY와 PTY는 별개의 데이터
     */

    private WeatherDataConverter()
    {
    }

    public static int getSkyDrawableId(String sky, String precipitaionForm)
    {
        int id = 0;

        switch (sky)
        {
            case "1":
                id = R.drawable.fine_icon;
                break;
            case "3":
                id = R.drawable.partly_cloudy_icon;
                break;
            case "4":
                id = R.drawable.cloudy_icon;
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
                convertedValue = context.getString(R.string.precipitation_form_code_0);
                break;

            case "1":
                convertedValue = context.getString(R.string.precipitation_form_code_1);
                break;

            case "2":
                convertedValue = context.getString(R.string.precipitation_form_code_2);
                break;

            case "3":
                convertedValue = context.getString(R.string.precipitation_form_code_3);
                break;

            case "4":
                convertedValue = context.getString(R.string.precipitation_form_code_4);
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
                convertedValue = context.getString(R.string.wind_direction_value_0);
                break;
            case 1:
                convertedValue = context.getString(R.string.wind_direction_value_1);
                break;
            case 2:
                convertedValue = context.getString(R.string.wind_direction_value_2);
                break;
            case 3:
                convertedValue = context.getString(R.string.wind_direction_value_3);
                break;
            case 4:
                convertedValue = context.getString(R.string.wind_direction_value_4);
                break;
            case 5:
                convertedValue = context.getString(R.string.wind_direction_value_5);
                break;
            case 6:
                convertedValue = context.getString(R.string.wind_direction_value_6);
                break;
            case 7:
                convertedValue = context.getString(R.string.wind_direction_value_7);
                break;
            case 8:
                convertedValue = context.getString(R.string.wind_direction_value_8);
                break;
            case 9:
                convertedValue = context.getString(R.string.wind_direction_value_9);
                break;
            case 10:
                convertedValue = context.getString(R.string.wind_direction_value_10);
                break;
            case 11:
                convertedValue = context.getString(R.string.wind_direction_value_11);
                break;
            case 12:
                convertedValue = context.getString(R.string.wind_direction_value_12);
                break;
            case 13:
                convertedValue = context.getString(R.string.wind_direction_value_13);
                break;
            case 14:
                convertedValue = context.getString(R.string.wind_direction_value_14);
                break;
            case 15:
                convertedValue = context.getString(R.string.wind_direction_value_15);
                break;
            case 16:
                convertedValue = context.getString(R.string.wind_direction_value_16);
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
                convertedValue = context.getString(R.string.sky_clear);
                break;

            case "3":
                convertedValue = context.getString(R.string.sky_partly_cloudy);
                break;

            case "4":
                convertedValue = context.getString(R.string.sky_cloudy);
                break;

        }
        return convertedValue;
    }
}