package com.zerodsoft.scheduleweather.retrofit.queryresponse.kakaoplace.menuinfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MenuInfo
{
    @Expose
    @SerializedName("menucount")
    private String menuCount;

    @Expose
    @SerializedName("menuList")
    private List<MenuItem> menuList;

    @Expose
    @SerializedName("productyn")
    private String productYn;

    @Expose
    @SerializedName("menuboardphotourlList")
    private List<String> menuBoardPhotoUrlList;

    @Expose
    @SerializedName("menuboardphotocount")
    private String menuBoardPhotoCount;

    @Expose
    @SerializedName("timeexp")
    private String timeExp;

    public MenuInfo()
    {
    }

    public String getMenuCount()
    {
        return menuCount;
    }

    public void setMenuCount(String menuCount)
    {
        this.menuCount = menuCount;
    }

    public List<MenuItem> getMenuList()
    {
        return menuList;
    }

    public void setMenuList(List<MenuItem> menuList)
    {
        this.menuList = menuList;
    }

    public String getProductYn()
    {
        return productYn;
    }

    public void setProductYn(String productYn)
    {
        this.productYn = productYn;
    }

    public List<String> getMenuBoardPhotoUrlList()
    {
        return menuBoardPhotoUrlList;
    }

    public void setMenuBoardPhotoUrlList(List<String> menuBoardPhotoUrlList)
    {
        this.menuBoardPhotoUrlList = menuBoardPhotoUrlList;
    }

    public String getMenuBoardPhotoCount()
    {
        return menuBoardPhotoCount;
    }

    public void setMenuBoardPhotoCount(String menuBoardPhotoCount)
    {
        this.menuBoardPhotoCount = menuBoardPhotoCount;
    }

    public String getTimeExp()
    {
        return timeExp;
    }

    public void setTimeExp(String timeExp)
    {
        this.timeExp = timeExp;
    }
}
