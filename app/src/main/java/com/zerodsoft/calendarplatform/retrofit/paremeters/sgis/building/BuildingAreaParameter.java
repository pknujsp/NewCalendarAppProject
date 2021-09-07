package com.zerodsoft.calendarplatform.retrofit.paremeters.sgis.building;

import java.util.HashMap;
import java.util.Map;

public class BuildingAreaParameter
{
    private String accessToken;
    private String minX;
    private String minY;
    private String maxX;
    private String maxY;

    public BuildingAreaParameter()
    {
    }

    public Map<String, String> toMap()
    {
        Map<String, String> map = new HashMap<>();
        map.put("accessToken", accessToken);
        map.put("minx", minX);
        map.put("miny", minY);
        map.put("maxx", maxX);
        map.put("maxy", maxY);

        return map;
    }

    public String getAccessToken()
    {
        return accessToken;
    }

    public BuildingAreaParameter setAccessToken(String accessToken)
    {
        this.accessToken = accessToken;
        return this;
    }

    public String getMinX()
    {
        return minX;
    }

    public BuildingAreaParameter setMinX(String minX)
    {
        this.minX = minX;
        return this;
    }

    public String getMinY()
    {
        return minY;
    }

    public BuildingAreaParameter setMinY(String minY)
    {
        this.minY = minY;
        return this;
    }

    public String getMaxX()
    {
        return maxX;
    }

    public BuildingAreaParameter setMaxX(String maxX)
    {
        this.maxX = maxX;
        return this;
    }

    public String getMaxY()
    {
        return maxY;
    }

    public BuildingAreaParameter setMaxY(String maxY)
    {
        this.maxY = maxY;
        return this;
    }
}
