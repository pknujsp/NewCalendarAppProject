package com.zerodsoft.calendarplatform.retrofit.queryresponse.weather.vilagefcstresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VilageFcstRoot
{
    @Expose
    @SerializedName("response")
    private VilageFcstResponse response;



    public void setResponse(VilageFcstResponse response)
    {
        this.response = response;
    }

    public VilageFcstResponse getResponse()
    {
        return response;
    }
}