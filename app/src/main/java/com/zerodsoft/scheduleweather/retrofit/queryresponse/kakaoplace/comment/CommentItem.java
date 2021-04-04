package com.zerodsoft.scheduleweather.retrofit.queryresponse.kakaoplace.comment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CommentItem
{
    @Expose
    @SerializedName("commentid")
    private String commentId;

    @Expose
    @SerializedName("contents")
    private String contents;

    @Expose
    @SerializedName("point")
    private String point;

    @Expose
    @SerializedName("username")
    private String userName;

    @Expose
    @SerializedName("profile")
    private String profile;

    @Expose
    @SerializedName("photoCnt")
    private String photoCnt;

    @Expose
    @SerializedName("likeCnt")
    private String likeCnt;

    @Expose
    @SerializedName("platform")
    private String platform;

    @Expose
    @SerializedName("date")
    private String date;

    @Expose
    @SerializedName("photoList")
    private List<String> photoList;

    public CommentItem()
    {
    }

    public String getCommentId()
    {
        return commentId;
    }

    public void setCommentId(String commentId)
    {
        this.commentId = commentId;
    }

    public String getContents()
    {
        return contents;
    }

    public void setContents(String contents)
    {
        this.contents = contents;
    }

    public String getPoint()
    {
        return point;
    }

    public void setPoint(String point)
    {
        this.point = point;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getProfile()
    {
        return profile;
    }

    public void setProfile(String profile)
    {
        this.profile = profile;
    }

    public String getPhotoCnt()
    {
        return photoCnt;
    }

    public void setPhotoCnt(String photoCnt)
    {
        this.photoCnt = photoCnt;
    }

    public String getLikeCnt()
    {
        return likeCnt;
    }

    public void setLikeCnt(String likeCnt)
    {
        this.likeCnt = likeCnt;
    }

    public String getPlatform()
    {
        return platform;
    }

    public void setPlatform(String platform)
    {
        this.platform = platform;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public List<String> getPhotoList()
    {
        return photoList;
    }

    public void setPhotoList(List<String> photoList)
    {
        this.photoList = photoList;
    }
}
