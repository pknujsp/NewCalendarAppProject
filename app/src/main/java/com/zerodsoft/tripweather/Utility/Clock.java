package com.zerodsoft.tripweather.Utility;

import org.w3c.dom.CDATASection;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class Clock
{
    public static final int CURRENT_WEATHER = 0;
    public static final int N_FORECAST = 1;
    private static final TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HHmm");

    public static Map<String, Object> getCurrentDateTime()
    {
        Map<String, Object> data = new HashMap<>();

        Calendar today = Calendar.getInstance(timeZone);
        // String baseDate = Integer.toString(today.get(Calendar.YEAR)) + Integer.toString(today.get(Calendar.MONTH) + 1) + Integer.toString(today.get(Calendar.DATE));
        // String baseTime = Integer.toString(today.get(Calendar.HOUR_OF_DAY)) + Integer.toString(today.get(Calendar.MINUTE));

        data.put("baseDate", dateFormat.format(today.getTime()));
        data.put("baseTime", timeFormat.format(today.getTime()));
        data.put("today", today);

        return data;
    }


    public static void convertBaseDateTime(Map<String, Object> currentDate, int type)
    {
        Calendar today = (Calendar) currentDate.get("today");

        switch (type)
        {
            case CURRENT_WEATHER:
                convertCWeather(today);
                break;
            case N_FORECAST:
                convertNForecast(today);
                break;
        }

        currentDate.put("baseDate", dateFormat.format(today.getTime()));
        currentDate.put("baseTime", timeFormat.format(today.getTime()));

    }

    private static void convertCWeather(Calendar date)
    {
        int hour = date.get(Calendar.HOUR_OF_DAY);
        int minute = date.get(Calendar.MINUTE);

        // renewal
        if (minute > 40)
        {
            date.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE), date.get(Calendar.HOUR_OF_DAY), 0);
        } else
        {
            if (hour == 0)
            {
                // 현재 시간이 오전12시 인 경우, 그 전날 오후 11시로 변경
                date.add(Calendar.DATE, -1);
                date.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE), 23, 0);
            } else
            {
                date.add(Calendar.HOUR_OF_DAY, -1);
                date.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE), date.get(Calendar.HOUR_OF_DAY), 0);
            }
        }
    }

    private static void convertNForecast(Calendar date)
    {
        final int hour = date.get(Calendar.HOUR_OF_DAY);
        final int minute = date.get(Calendar.MINUTE);

        if (minute > 10)
        {
            if (hour <= 1)
            {
                date.add(Calendar.DATE, -1);
                date.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE), 23, 0);
            } else if (hour >= 23)
            {
                date.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE), 23, 0);
            } else
            {
                for (int i = 2; i <= 20; i += 3)
                {
                    if (hour <= i + 3 && hour >= i)
                    {
                        date.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE), i, 0);
                        break;
                    }
                }
            }
        } else
        {
            if (hour <= 2)
            {
                date.add(Calendar.DATE, -1);
                date.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE), 23, 0);
            } else
            {
                for (int i = 2; i <= 20; i += 3)
                {
                    if (hour <= i + 3 && hour >= i + 1)
                    {
                        date.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE), i, 0);
                        break;
                    } else if (hour == i)
                    {
                        date.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE), i - 3, 0);
                        break;
                    }
                }
            }
        }
    }

    public static String getYearMonth()
    {
        Calendar calendar = Calendar.getInstance(timeZone);

        return Integer.toString(calendar.get(Calendar.YEAR)) + Integer.toString(calendar.get(Calendar.MONTH) + 1);
    }
}
