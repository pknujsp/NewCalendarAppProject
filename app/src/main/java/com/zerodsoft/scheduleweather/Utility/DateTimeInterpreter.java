package com.zerodsoft.scheduleweather.Utility;

import java.util.Calendar;

public interface DateTimeInterpreter
{
    String interpretDate(Calendar date);
    String interpretTime(int hour);
    String interpretDay(int date);
}
