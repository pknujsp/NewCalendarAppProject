package com.zerodsoft.scheduleweather.retrofit.queryresponse.kakaoplace.kakaostory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class KakaoStoryItem
{
    @Expose
    @SerializedName("body")
    private String body;

    @Expose
    @SerializedName("creator")
    private String creator;

    @Expose
    @SerializedName("date")
    private String date;

    @Expose
    @SerializedName("image")
    private String image;

    @Expose
    @SerializedName("imgcnt")
    private String imgCnt;

    @Expose
    @SerializedName("outlink")
    private String outlink;

    @Expose
    @SerializedName("storyid")
    private String storyId;

    public KakaoStoryItem()
    {
    }

    public String getBody()
    {
        return body;
    }

    public void setBody(String body)
    {
        this.body = body;
    }

    public String getCreator()
    {
        return creator;
    }

    public void setCreator(String creator)
    {
        this.creator = creator;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getImage()
    {
        return image;
    }

    public void setImage(String image)
    {
        this.image = image;
    }

    public String getImgCnt()
    {
        return imgCnt;
    }

    public void setImgCnt(String imgCnt)
    {
        this.imgCnt = imgCnt;
    }

    public String getOutlink()
    {
        return outlink;
    }

    public void setOutlink(String outlink)
    {
        this.outlink = outlink;
    }

    public String getStoryId()
    {
        return storyId;
    }

    public void setStoryId(String storyId)
    {
        this.storyId = storyId;
    }
}
