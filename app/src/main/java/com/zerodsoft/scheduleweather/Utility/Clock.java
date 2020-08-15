package com.zerodsoft.scheduleweather.Utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class Clock
{
    public static final int CURRENT_WEATHER = 0;
    public static final int N_FORECAST = 1;
    public static final TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    public static final SimpleDateFormat dateFormatSlash = new SimpleDateFormat("yyyy/MM/dd");
    public static final SimpleDateFormat timeFormat = new SimpleDateFormat("HHmm");
    public static final SimpleDateFormat timeFormat2 = new SimpleDateFormat("MM/dd HH:mm");
    public static final SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy년 M월 d일 E a h시 m분");
    public static final SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy년 M월 d일 E");
    public static final SimpleDateFormat DB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static
    {
        DB_DATE_FORMAT.setTimeZone(timeZone);
    }

    private Clock()
    {
    }

    public static Map<String, Object> getCurrentDateTime()
    {
        Map<String, Object> data = new HashMap<>();

        Calendar today = Calendar.getInstance(timeZone, Locale.KOREA);

        data.put("baseDate", dateFormat.format(today.getTime()));
        data.put("baseTime", timeFormat.format(today.getTime()));
        data.put("baseDateSlash", dateFormatSlash.format(today.getTime()));
        data.put("updatedTime", timeFormat2.format(today.getTime()));
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
}
