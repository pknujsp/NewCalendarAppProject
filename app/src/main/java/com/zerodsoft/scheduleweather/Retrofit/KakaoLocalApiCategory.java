package com.zerodsoft.scheduleweather.Retrofit;

public class KakaoLocalApiCategory
{
    private String name;
    private String description;

    public KakaoLocalApiCategory(String name, String description)
    {
        this.name = name;
        this.description = description;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }
}
