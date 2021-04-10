package com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.building;

import java.util.HashMap;
import java.util.Map;

public class FloorCompanyInfoParameter
{
    private String accessToken;
    private String sufId;
    private String floorNo;

    public FloorCompanyInfoParameter()
    {
    }

    public Map<String, String> toMap()
    {
        Map<String, String> map = new HashMap<>();
        map.put("accessToken", accessToken);
        map.put("sufid", sufId);
        map.put("flr_no", floorNo);

        return map;
    }

    public String getAccessToken()
    {
        return accessToken;
    }

    public FloorCompanyInfoParameter setAccessToken(String accessToken)
    {
        this.accessToken = accessToken;
        return this;
    }

    public String getSufId()
    {
        return sufId;
    }

    public FloorCompanyInfoParameter setSufId(String sufId)
    {
        this.sufId = sufId;
        return this;
    }

    public String getFloorNo()
    {
        return floorNo;
    }

    public FloorCompanyInfoParameter setFloorNo(String floorNo)
    {
        this.floorNo = floorNo;
        return this;
    }
}
