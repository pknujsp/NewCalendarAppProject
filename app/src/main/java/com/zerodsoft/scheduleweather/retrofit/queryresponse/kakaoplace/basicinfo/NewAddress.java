package com.zerodsoft.scheduleweather.retrofit.queryresponse.kakaoplace.basicinfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewAddress
{
    @SerializedName("newaddrfull")
    @Expose
    private String newAddrFull;

    @SerializedName("bsizonno")
    @Expose
    private String bsizonno;

    public NewAddress()
    {
    }

    public String getNewAddrFull()
    {
        return newAddrFull;
    }

    public void setNewAddrFull(String newAddrFull)
    {
        this.newAddrFull = newAddrFull;
    }

    public String getBsizonno()
    {
        return bsizonno;
    }

    public void setBsizonno(String bsizonno)
    {
        this.bsizonno = bsizonno;
    }
}
