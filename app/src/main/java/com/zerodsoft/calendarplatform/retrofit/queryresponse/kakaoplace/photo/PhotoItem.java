package com.zerodsoft.calendarplatform.retrofit.queryresponse.kakaoplace.photo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PhotoItem
{
    @Expose
    @SerializedName("photoCount")
    private String photoCount;

    @Expose
    @SerializedName("categoryName")
    private String categoryName;

    @Expose
    @SerializedName("list")
    private List<PhotoListItem> photoList;

    public PhotoItem()
    {

    }

    public String getPhotoCount()
    {
        return photoCount;
    }

    public void setPhotoCount(String photoCount)
    {
        this.photoCount = photoCount;
    }

    public String getCategoryName()
    {
        return categoryName;
    }

    public void setCategoryName(String categoryName)
    {
        this.categoryName = categoryName;
    }

    public List<PhotoListItem> getPhotoList()
    {
        return photoList;
    }

    public void setPhotoList(List<PhotoListItem> photoList)
    {
        this.photoList = photoList;
    }
}
