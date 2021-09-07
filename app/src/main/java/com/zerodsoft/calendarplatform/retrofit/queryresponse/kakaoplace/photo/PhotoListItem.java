package com.zerodsoft.calendarplatform.retrofit.queryresponse.kakaoplace.photo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PhotoListItem
{
    @Expose
    @SerializedName("photoid")
    private String photoId;

    @Expose
    @SerializedName("orgurl")
    private String orgUrl;

    public PhotoListItem()
    {
    }

    public String getPhotoId()
    {
        return photoId;
    }

    public void setPhotoId(String photoId)
    {
        this.photoId = photoId;
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
