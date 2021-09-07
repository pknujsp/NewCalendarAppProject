package com.zerodsoft.calendarplatform.retrofit.queryresponse.kakaoplace.menuinfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MenuItem
{
    @Expose
    @SerializedName("price")
    private String price;

    @Expose
    @SerializedName("menu")
    private String menu;

    @Expose
    @SerializedName("desc")
    private String desc;

    @Expose
    @SerializedName("image")
    private String image;

    public MenuItem()
    {
    }

    public String getPrice()
    {
        return price;
    }

    public void setPrice(String price)
    {
        this.price = price;
    }

    public String getMenu()
    {
        return menu;
    }

    public void setMenu(String menu)
    {
        this.menu = menu;
    }

    public String getDesc()
    {
        return desc;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }

    public String getImage()
    {
        return image;
    }

    public void setImage(String image)
    {
        this.image = image;
    }
}
