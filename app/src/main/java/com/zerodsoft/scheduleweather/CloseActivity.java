package com.zerodsoft.scheduleweather;

import android.app.Activity;
import android.widget.Toast;

public class CloseActivity
{
    private long backPressedTime = 0L;
    private long interval = 2000L;
    private Toast toast;
    private Activity activity;

    public CloseActivity(Activity activity)
    {
        this.activity = activity;
    }

    public void closeActivity()
    {
        long currentTime = System.currentTimeMillis();

        if (currentTime > backPressedTime + interval)
        {
            backPressedTime = currentTime;
            toast = Toast.makeText(activity.getApplicationContext(), "뒤로가기 버튼을 한번 더 누르시면 종료됩니다", Toast.LENGTH_SHORT);
            toast.show();
        } else
        {
            activity.finish();
            toast.cancel();
        }
    }
}
