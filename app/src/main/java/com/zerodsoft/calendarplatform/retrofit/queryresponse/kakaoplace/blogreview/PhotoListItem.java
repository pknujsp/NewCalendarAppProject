package com.zerodsoft.calendarplatform.retrofit.queryresponse.kakaoplace.blogreview;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PhotoListItem
{
    @Expose
    @SerializedName("orgurl")
    private String orgUrl;

    public PhotoListItem()
    {
    }

    public String getOrgUrl()
    {
        return orgUrl;
    }

    public void setOrgUrl(String orgUrl)
    {
        this.orgUrl = orgUrl;
    }
}
