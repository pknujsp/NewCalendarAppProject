package com.zerodsoft.scheduleweather.retrofit.queryresponse.kakaoplace.basicinfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Region
{
    @SerializedName("name3")
    @Expose
    private String name3;

    @SerializedName("fullname")
    @Expose
    private String fullName;

    @SerializedName("newaddrfullname")
    @Expose
    private String newAddrFullName;

    public Region()
    {
    }

    public String getName3()
    {
        return name3;
    }

    public void setName3(String name3)
    {
        this.name3 = name3;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public String getNewAddrFullName()
    {
        return newAddrFullName;
    }

    public void setNewAddrFullName(String newAddrFullName)
    {
        this.newAddrFullName = newAddrFullName;
    }
}
