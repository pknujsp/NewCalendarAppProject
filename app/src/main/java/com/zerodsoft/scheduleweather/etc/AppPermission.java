package com.zerodsoft.scheduleweather.etc;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppPermission implements IPermission
{
    public static boolean grantedPermissions(Context context, String... permissions)
    {
        List<String> deniedList = new ArrayList<>();

        for (int i = 0; i < permissions.length; i++)
        {
            int result = ContextCompat.checkSelfPermission(context, permissions[i]);
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
            return false;
        }
    }
}
