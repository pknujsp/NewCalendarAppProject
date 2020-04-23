package com.zerodsoft.tripweather.Utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class Clock
{
    public static Map<String, String> getCurrentDateTime()
    {
        Map<String, String> data = new HashMap<>();
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HHmm");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String[] result = simpleDateFormat.format(date).toString().split(" ");

        data.put("currentDate", result[0]);
        data.put("currentTime", result[1]);

        return data;
    }

    private static String getYesterdayDate()
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        Calendar calendar = Calendar.getInstance();

        calendar.add(calendar.DATE, -1);

        return simpleDateFormat.format(calendar.getTime());
    }

    public static void convertBaseDateTime(Map<String, String> currentDateTime)
    {
        String currentTime = currentDateTime.get("currentTime");

        final int currentHour = Integer.valueOf(currentTime.substring(0, 2)).intValue();
        final int currentMinute = Integer.valueOf(currentTime.substring(2)).intValue();

        if (currentMinute > 40)
        {
            currentTime = currentTime.substring(0, 2) + "00";
        } else
        {
            if (currentHour == 0)
            {
                // 현재 시간이 오전12시 인 경우, 그 전날 오후 11시로 변경
                currentTime = "2300";
                String currentDate = getYesterdayDate();

                currentDateTime.put("currentDate", currentDate);
            } else if (currentHour >= 11)
            {
                currentTime = String.valueOf(currentHour - 1) + "00";
            } else
            {
                currentTime = "0" + String.valueOf(currentHour - 1) + "00";
            }
        }
        currentDateTime.put("currentTime", currentTime);
    }

    public static String getYearMonth()
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));

        return Integer.toString(calendar.get(Calendar.YEAR)) + Integer.toString(calendar.get(Calendar.MONTH) + 1);
    }
}
