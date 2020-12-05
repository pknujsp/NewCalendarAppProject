package com.zerodsoft.scheduleweather.retrofit;

public class KakaoLocalApiCategory
{
    private int id;
    private String name;
    private String description;

    public KakaoLocalApiCategory(String name, String description)
    {
        this.name = name;
        this.description = description;
    }

    public KakaoLocalApiCategory(int id, String name, String description)
    {
        this.id = id;
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

    public int getId()
    {
        return id;
    }
}
