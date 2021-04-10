package com.zerodsoft.scheduleweather.common.classes;

import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.AirConditionRoot;

public abstract class JsonDownloader<T>
{
    public abstract void onResponse(DataWrapper<? extends T> result);
}
