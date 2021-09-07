package com.zerodsoft.calendarplatform.retrofit.paremeters;

import com.zerodsoft.calendarplatform.retrofit.HttpCommunicationClient;

import java.util.HashMap;
import java.util.Map;

public class CtprvnRltmMesureDnstyParameter
{
    private String serviceKey = HttpCommunicationClient.AIR_CONDITION_SERVICE_SERVICE_KEY;
    private String returnType = "json";
    private String numOfRows = "1";
    private String pageNo = "100";
    private String sidoName;
    private String ver = "1.3";

    public CtprvnRltmMesureDnstyParameter()
    {
    }

    public Map<String, String> getMap()
    {
        Map<String, String> map = new HashMap<>();

        map.put("serviceKey", serviceKey);
        map.put("returnType", returnType);
        map.put("numOfRows", numOfRows);
        map.put("pageNo", pageNo);
        map.put("sidoName", sidoName);
        map.put("ver", ver);
        return map;
    }

    public String getSidoName()
    {
        return sidoName;
    }

    public void setSidoName(String sidoName)
    {
        this.sidoName = sidoName;
    }
}
