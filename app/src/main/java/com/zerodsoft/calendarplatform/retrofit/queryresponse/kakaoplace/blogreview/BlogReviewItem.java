package com.zerodsoft.calendarplatform.retrofit.queryresponse.kakaoplace.blogreview;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BlogReviewItem
{
    @Expose
    @SerializedName("blogname")
    private String blogName;

    @Expose
    @SerializedName("contents")
    private String contents;

    @Expose
    @SerializedName("date")
    private String date;

    @Expose
    @SerializedName("outlink")
    private String outlink;

    @Expose
    @SerializedName("reviewid")
    private String reviewId;

    @Expose
    @SerializedName("title")
    private String title;

    @Expose
    @SerializedName("photoList")
    private List<PhotoListItem> photoList;

    public BlogReviewItem()
    {
    }

    public String getBlogName()
    {
        return blogName;
    }

    public void setBlogName(String blogName)
    {
        this.blogName = blogName;
    }

    public String getContents()
    {
        return contents;
    }

    public void setContents(String contents)
    {
        this.contents = contents;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getOutlink()
    {
        return outlink;
    }

    public void setOutlink(String outlink)
    {
        this.outlink = outlink;
    }

    public String getReviewId()
    {
        return reviewId;
    }

    public void setReviewId(String reviewId)
    {
        this.reviewId = reviewId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
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
