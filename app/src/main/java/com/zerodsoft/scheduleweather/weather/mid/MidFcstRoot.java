package com.zerodsoft.scheduleweather.weather.mid;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.midlandfcstresponse.MidLandFcstRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.midtaresponse.MidTaRoot;

public class MidFcstRoot
{
    volatile int count = 0;
    private volatile MidTaRoot midTaRoot;
    private volatile MidLandFcstRoot midLandFcstRoot;
    private volatile Exception exception;

    public MidTaRoot getMidTaRoot()
    {
        return midTaRoot;
    }

    public void setMidTaRoot(MidTaRoot midTaRoot)
    {
        this.midTaRoot = midTaRoot;
        ++this.count;
    }

    public MidLandFcstRoot getMidLandFcstRoot()
    {
        return midLandFcstRoot;
    }

    public void setMidLandFcstRoot(MidLandFcstRoot midLandFcstRoot)
    {
        this.midLandFcstRoot = midLandFcstRoot;
        ++this.count;

    }

    public void setException(Exception exception)
    {
        this.exception = exception;
        ++this.count;

    }

    public Exception getException()
    {
        return exception;
    }

    public int getCount()
    {
        return count;
    }
}
