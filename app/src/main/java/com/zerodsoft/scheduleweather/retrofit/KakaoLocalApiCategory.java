package com.zerodsoft.scheduleweather.retrofit;

public class KakaoLocalApiCategory
{
    private int id;
    private String description;
    private String name;

    public KakaoLocalApiCategory(String name, String description)
    {
        this.name = name;
        this.description = description;
    }

    public KakaoLocalApiCategory(int id, String description, String name)
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
