package com.zerodsoft.calendarplatform.retrofit.paremeters.sgis.address;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.Utmk;

import java.util.HashMap;
import java.util.Map;

public class ReverseGeoCodingParameter
{
    /*
    도로명 : 10
행정동(읍면동) : 20
행정동(지번함) : 21
(default : 10)
     */
    public static class AddressType
    {
        public static final String ROAD_ADDRESS = "10";
        public static final String ADM_EUP_MYEON_DONG = "20";
        public static final String ADM_ZIBEON = "21";
    }

    private String accessToken;
    private String xCoor;
    private String yCoor;
    private String addrType;

    public ReverseGeoCodingParameter()
    {
    }

    public void setCoord(double latitude, double longitude)
    {
        LatLng latLng = new LatLng(latitude, longitude);
        Utmk utmk = Utmk.valueOf(latLng);
        xCoor = String.valueOf(utmk.x);
        yCoor = String.valueOf(utmk.y);
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
