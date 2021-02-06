package com.zerodsoft.scheduleweather.kakaomap.model.callback;

import com.zerodsoft.scheduleweather.retrofit.DataWrapper;

import java.util.ArrayList;
import java.util.List;

public abstract class CheckerCallback<T>
{
    protected volatile int responseCount = 0;
    protected volatile int totalRequestCount = 0;
    protected volatile List<T> list = new ArrayList<>();

    public void onResult()
    {

    }

    public int getResponseCount()
    {
        return responseCount;
    }

    public int getTotalRequestCount()
    {
        return totalRequestCount;
    }

    public void add(T e)
    {
        this.responseCount++;
        list.add(e);
    }


    public void setTotalRequestCount(int totalRequestCount)
    {
        this.totalRequestCount = totalRequestCount;
    }
}
