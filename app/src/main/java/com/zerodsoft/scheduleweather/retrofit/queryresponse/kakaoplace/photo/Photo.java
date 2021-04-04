package com.zerodsoft.scheduleweather.retrofit.queryresponse.kakaoplace.photo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Photo
{
    @Expose
    @SerializedName("photoList")
    private List<PhotoItem> photoList;

    public Photo()
    {
    }
    
    public List<PhotoItem> getPhotoList()
    {
        return photoList;
    }

    public void setPhotoList(List<PhotoItem> photoList)
    {
        this.photoList = photoList;
    }
}
