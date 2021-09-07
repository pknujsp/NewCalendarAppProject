package com.zerodsoft.calendarplatform.retrofit.queryresponse.kakaoplace;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.kakaoplace.basicinfo.BasicInfo;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.kakaoplace.blogreview.BlogReview;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.kakaoplace.comment.Comment;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.kakaoplace.kakaostory.KakaoStory;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.kakaoplace.menuinfo.MenuInfo;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.kakaoplace.photo.Photo;

public class KakaoPlaceJsonRoot
{
    @SerializedName("basicInfo")
    @Expose
    private BasicInfo basicInfo;

    @SerializedName("blogReview")
    @Expose
    private BlogReview blogReview;

    @SerializedName("kakaoStory")
    @Expose
    private KakaoStory kakaoStory;

    @SerializedName("comment")
    @Expose
    private Comment comment;

    @SerializedName("menuInfo")
    @Expose
    private MenuInfo menuInfo;

    @SerializedName("photo")
    @Expose
    private Photo photo;

    public KakaoPlaceJsonRoot()
    {
    }

    public BasicInfo getBasicInfo()
    {
        return basicInfo;
    }

    public void setBasicInfo(BasicInfo basicInfo)
    {
        this.basicInfo = basicInfo;
    }

    public BlogReview getBlogReview()
    {
        return blogReview;
    }

    public void setBlogReview(BlogReview blogReview)
    {
        this.blogReview = blogReview;
    }

    public KakaoStory getKakaoStory()
    {
        return kakaoStory;
    }

    public void setKakaoStory(KakaoStory kakaoStory)
    {
        this.kakaoStory = kakaoStory;
    }

    public Comment getComment()
    {
        return comment;
    }

    public void setComment(Comment comment)
    {
        this.comment = comment;
    }

    public MenuInfo getMenuInfo()
    {
        return menuInfo;
    }

    public void setMenuInfo(MenuInfo menuInfo)
    {
        this.menuInfo = menuInfo;
    }

    public Photo getPhoto()
    {
        return photo;
    }

    public void setPhoto(Photo photo)
    {
        this.photo = photo;
    }
}
