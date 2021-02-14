package com.zerodsoft.scheduleweather.kakaomap.interfaces;

public interface IPermission
{
    public static final int REQUEST_CODE_LOCATION = 10000;

    void requestPermissions(int requestCode, String... permissions);
    boolean grantedGpsPermissions();
}
