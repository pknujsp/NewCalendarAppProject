package com.zerodsoft.calendarplatform.retrofit.paremeters.sgis.building;

import java.util.HashMap;
import java.util.Map;

public class FloorEtcFacilityParameter
{
    private String accessToken;
    private String sufId;
    private String floorNo;

    public FloorEtcFacilityParameter()
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

    public FloorEtcFacilityParameter setAccessToken(String accessToken)
    {
        this.accessToken = accessToken;
        return this;
    }

    public String getSufId()
    {
        return sufId;
    }

    public FloorEtcFacilityParameter setSufId(String sufId)
    {
        this.sufId = sufId;
        return this;
    }

    public String getFloorNo()
    {
        return floorNo;
    }

    public FloorEtcFacilityParameter setFloorNo(String floorNo)
    {
        this.floorNo = floorNo;
        return this;
    }
}
