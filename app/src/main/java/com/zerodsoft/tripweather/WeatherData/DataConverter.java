package com.zerodsoft.tripweather.WeatherData;

import android.content.Context;

import com.zerodsoft.tripweather.R;

public class DataConverter {
    protected static String convertPrecipitationForm(String value, Context context) {
        String convertedValue = null;

        switch (value) {
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

    protected static String convertWindDirection(String value, Context context) {
        final int windDirectionValue = (int) ((Integer.valueOf(value).intValue() + 22.5 * 0.5) / 22.5);

        String convertedValue = null;

        switch (windDirectionValue) {
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
}
