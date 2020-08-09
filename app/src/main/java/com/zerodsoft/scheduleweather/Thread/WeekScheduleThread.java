package com.zerodsoft.scheduleweather.Thread;

import android.os.Handler;
import android.os.Message;

public class WeekScheduleThread extends Thread
{
    private Handler handler;
    private static WeekScheduleThread weekScheduleThread;

    public static WeekScheduleThread getInstance()
    {
        if (weekScheduleThread == null)
        {
            weekScheduleThread = new WeekScheduleThread();
        }
        return weekScheduleThread;
    }


    @Override
    public void run()
    {

        Message msg = handler.obtainMessage();
        handler.sendMessage(msg);
    }
}
