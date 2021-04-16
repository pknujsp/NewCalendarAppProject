package com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.address;

import java.util.HashMap;
import java.util.Map;

public class ReverseGeoCodingParameter
{
    private String accessToken;
    private String xCoor;
    private String yCoor;
    private String addrType;

    public ReverseGeoCodingParameter()
    {
    }

    public String getAccessToken()
    {
        return accessToken;
    }

    public void setAccessToken(String accessToken)
    {
        this.accessToken = accessToken;
    }

    public String getxCoor()
    {
        return xCoor;
    }

    public void setxCoor(String xCoor)
    {
        this.xCoor = xCoor;
    }

    public String getyCoor()
    {
        return yCoor;
    }

    public void setyCoor(String yCoor)
    {
        this.yCoor = yCoor;
    }

    public String getAddrType()
    {
        return addrType;
    }

    public void setAddrType(String addrType)
    {
        this.addrType = addrType;
    }

    public Map<String, String> toMap()
    {
        Map<String, String> map = new HashMap<>();

        map.put("accessToken", accessToken);
        map.put("x_coor", xCoor);
        map.put("y_coor", yCoor);
        map.put("addr_type", addrType);

        return map;
    }
}
