package com.zerodsoft.calendarplatform.retrofit.paremeters.sgis.building;

import java.util.HashMap;
import java.util.Map;

public class BuildingAttributeParameter
{
    private String accessToken;
    private String sufId;

    public BuildingAttributeParameter()
    {
    }

    public Map<String, String> toMap()
    {
        Map<String, String> map = new HashMap<>();
        map.put("accessToken", accessToken);
        map.put("sufid", sufId);

        return map;
    }

    public String getAccessToken()
    {
        return accessToken;
    }

    public BuildingAttributeParameter setAccessToken(String accessToken)
    {
        this.accessToken = accessToken;
        return this;
    }

    public String getSufId()
    {
        return sufId;
    }

    public BuildingAttributeParameter setSufId(String sufId)
    {
        this.sufId = sufId;
        return this;
    }
}
