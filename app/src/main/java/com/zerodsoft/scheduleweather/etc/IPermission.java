package com.zerodsoft.scheduleweather.etc;

public interface IPermission
{
    void requestPermissions(int requestCode, String... permissions);

    boolean grantedPermissions(int requestCode, String... permissions);
}