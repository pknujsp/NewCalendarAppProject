package com.zerodsoft.scheduleweather.weather.mid;

import com.google.gson.JsonObject;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.midlandfcstresponse.MidLandFcstRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.midtaresponse.MidTaRoot;

public class MidFcstRoot
{
    volatile int count = 0;
    private volatile JsonObject midTa;
    private volatile JsonObject midLandFcst;
    private volatile Exception exception;

    public JsonObject getMidTa()
    {
        return midTa;
    }

    public void setMidTa(JsonObject midTa)
    {
        ++this.count;
        this.midTa = midTa;
    }

    public JsonObject getMidLandFcst()
    {
        return midLandFcst;
    }

    public void setMidLandFcst(JsonObject midLandFcst)
    {
        ++this.count;
        this.midLandFcst = midLandFcst;
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
