package com.zerodsoft.calendarplatform.retrofit.paremeters;

import com.zerodsoft.calendarplatform.retrofit.HttpCommunicationClient;

import java.util.HashMap;
import java.util.Map;

public class MsrstnAcctoRltmMesureDnstyParameter
{
    public static final String DATATERM_DAILY = "DAILY";
    public static final String DATATERM_MONTH = "MONTH";
    public static final String DATATERM_3MONTH = "3MONTH";

    private String serviceKey = HttpCommunicationClient.AIR_CONDITION_SERVICE_SERVICE_KEY;
    private String returnType = "json";
    private String numOfRows = "100";
    private String pageNo = "1";
    private String stationName;
    private String dataTerm;
    private String ver = "1.3";

    public MsrstnAcctoRltmMesureDnstyParameter()
    {
    }

    public Map<String, String> getMap()
    {
        Map<String, String> map = new HashMap<>();

        map.put("serviceKey", serviceKey);
        map.put("returnType", returnType);
        map.put("numOfRows", numOfRows);
        map.put("pageNo", pageNo);
        map.put("stationName", stationName);
        map.put("dataTerm", dataTerm);
        map.put("ver", ver);

        return map;
    }

    public String getStationName()
    {
        return stationName;
    }

    public void setStationName(String stationName)
    {
        this.stationName = stationName;
    }

    public String getDataTerm()
    {
        return dataTerm;
    }

    public void setDataTerm(String dataTerm)
    {
        this.dataTerm = dataTerm;
    }
}
