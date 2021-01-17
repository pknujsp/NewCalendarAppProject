package com.zerodsoft.scheduleweather.kakaomap.model.callback;

import com.zerodsoft.scheduleweather.retrofit.DataWrapper;

import java.util.ArrayList;
import java.util.List;

public abstract class CheckerCallback<T>
{
    protected int responseCount = 0;
    protected int totalRequestCount = 0;
    protected List<T> list = new ArrayList<>();

    public void onResult()
    {

    }

    public void add(T e)
    {
        list.add(e);
    }

    public void addCount()
    {
        this.responseCount++;
    }

    public void setTotalRequestCount(int totalRequestCount)
    {
        this.totalRequestCount = totalRequestCount;
    }
}
