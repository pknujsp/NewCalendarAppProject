package com.zerodsoft.scheduleweather.notification.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class AppBootReceiver extends BroadcastReceiver
{
    private static AppBootReceiver instance;
    private Context context;

    public static AppBootReceiver newInstance(Context context)
    {
        instance = new AppBootReceiver(context);
        return instance;
    }

    public static AppBootReceiver getInstance()
    {
        return instance;
    }

    public AppBootReceiver(Context context)
    {
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            int v = 0;
        }
    }

    public void init()
    {
        ComponentName receiver = new ComponentName(context, AppBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
