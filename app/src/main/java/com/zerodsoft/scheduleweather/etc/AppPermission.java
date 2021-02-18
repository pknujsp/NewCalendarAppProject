package com.zerodsoft.scheduleweather.etc;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class AppPermission implements IPermission
{
    private Activity activity;

    public AppPermission(Activity activity)
    {
        this.activity = activity;
    }

    @Override
    public void requestPermissions(int requestCode, String... permissions)
    {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    @Override
    public boolean grantedPermissions(int requestCode, String... permissions)
    {
        List<String> deniedList = new ArrayList<>();

        for (int i = 0; i < permissions.length; i++)
        {
            int result = ContextCompat.checkSelfPermission(activity.getApplicationContext(), permissions[i]);
            if (result == PackageManager.PERMISSION_DENIED)
            {
                deniedList.add(permissions[i]);
            }
        }

        if (deniedList.isEmpty())
        {
            return true;
        } else
        {
            requestPermissions(requestCode, deniedList.toArray(new String[0]));
            return false;
        }
    }
}
