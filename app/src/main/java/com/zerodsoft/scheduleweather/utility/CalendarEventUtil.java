package com.zerodsoft.scheduleweather.utility;

import com.zerodsoft.scheduleweather.utility.model.ReminderDto;

public class CalendarEventUtil
{
    private CalendarEventUtil()
    {
    }

    public static ReminderDto convertAlarmMinutes(int minutes)
    {
        final int WEEK_1 = 10080;
        final int DAY_1 = 1440;
        final int HOUR_1 = 60;

        // 10일 - 14400, 4주 - 40320, (1주 - 10080, 1일 - 1440, 1시간 - 60)
        final int week = minutes / WEEK_1;
        int remainder = minutes - (WEEK_1 * week);

        final int day = remainder / DAY_1;
        remainder = remainder - (DAY_1 * day);

        final int hour = remainder / HOUR_1;
        remainder = remainder - (HOUR_1 * hour);

        final int minute = remainder;

        return new ReminderDto(week, day, hour, minute);
    }

    public static int convertReminderValues(ReminderDto reminderDto)
    {
        final int WEEK_1 = 10080;
        final int DAY_1 = 1440;
        final int HOUR_1 = 60;

        final int minutes = reminderDto.getWeek() * WEEK_1 + reminderDto.getDay() * DAY_1 +
                reminderDto.getHour() * HOUR_1 + reminderDto.getMinute();
        return minutes;
    }
}
