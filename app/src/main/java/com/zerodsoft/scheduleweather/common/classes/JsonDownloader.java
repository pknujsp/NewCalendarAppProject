package com.zerodsoft.scheduleweather.common.classes;

import com.zerodsoft.scheduleweather.event.weather.repository.SgisTranscoord;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.AirConditionRoot;

import retrofit2.Response;

public abstract class JsonDownloader<T>
{
    public abstract void onResponseSuccessful(T result);

    public abstract void onResponseFailed(Exception e);

    public void processResult(Response<? extends T> response)
    {
        if (response.body() != null)
        {
            onResponseSuccessful(response.body());
        } else
        {
            onResponseFailed(new Exception(response.message()));
        }
    }

    public void processResult(Throwable t)
    {
        onResponseFailed(new Exception(t));
    }
}
