package com.zerodsoft.scheduleweather.retrofit.queryresponse.kakaoplace.kakaostory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class KakaoStory
{
    @Expose
    @SerializedName("placenamefull")
    private String placeNameFull;

    @Expose
    @SerializedName("kascnt")
    private String kasCnt;

    @Expose
    @SerializedName("list")
    private List<KakaoStoryItem> list;

    public KakaoStory()
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

    public String getKasCnt()
    {
        return kasCnt;
    }

    public void setKasCnt(String kasCnt)
    {
        this.kasCnt = kasCnt;
    }

    public List<KakaoStoryItem> getList()
    {
        return list;
    }

    public void setList(List<KakaoStoryItem> list)
    {
        this.list = list;
    }
}
