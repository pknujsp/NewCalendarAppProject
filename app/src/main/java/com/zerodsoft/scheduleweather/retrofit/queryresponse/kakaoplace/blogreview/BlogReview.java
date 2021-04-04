package com.zerodsoft.scheduleweather.retrofit.queryresponse.kakaoplace.blogreview;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BlogReview
{
    @Expose
    @SerializedName("placenamefull")
    private String placeNameFull;

    @Expose
    @SerializedName("moreId")
    private String moreId;

    @Expose
    @SerializedName("blogrvwcnt")
    private String blogRvwCnt;

    @Expose
    @SerializedName("list")
    private List<BlogReviewItem> list;

    public BlogReview()
    {
    }

    public String getPlaceNameFull()
    {
        return placeNameFull;
    }

    public void setPlaceNameFull(String placeNameFull)
    {
        this.placeNameFull = placeNameFull;
    }

    public String getMoreId()
    {
        return moreId;
    }

    public void setMoreId(String moreId)
    {
        this.moreId = moreId;
    }

    public String getBlogRvwCnt()
    {
        return blogRvwCnt;
    }

    public void setBlogRvwCnt(String blogRvwCnt)
    {
        this.blogRvwCnt = blogRvwCnt;
    }

    public List<BlogReviewItem> getList()
    {
        return list;
    }

    public void setList(List<BlogReviewItem> list)
    {
        this.list = list;
    }
}
