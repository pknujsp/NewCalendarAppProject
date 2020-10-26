package com.zerodsoft.scheduleweather.retrofit.paremeters;

import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;

import java.util.HashMap;
import java.util.Map;

public class MidFcstParameter implements Cloneable
{
    private String serviceKey = HttpCommunicationClient.MID_FCST_INFO_SERVICE_SERVICE_KEY;
    private String numOfRows;
    private String pageNo;
    private String dataType = HttpCommunicationClient.DATATYPE;
    private String regId;
    private String tmFc;
    private Map<String, String> map = new HashMap<>();


    public MidFcstParameter()
    {
    }

    public MidFcstParameter(String numOfRows, String pageNo, String regId, String tmFc)
    {
        this.numOfRows = numOfRows;
        this.pageNo = pageNo;
        this.regId = regId;
        this.tmFc = tmFc;
    }

    public Map<String, String> getMap()
    {
        map.clear();

        map.put("serviceKey", serviceKey);
        map.put("numOfRows", numOfRows);
        map.put("pageNo", pageNo);
        map.put("dataType", dataType);
        map.put("regId", regId);
        map.put("tmFc", tmFc);

        return map;
    }


    public String getNumOfRows()
    {
        return numOfRows;
    }

    public MidFcstParameter setNumOfRows(String numOfRows)
    {
        this.numOfRows = numOfRows;
        return this;
    }

    public String getPageNo()
    {
        return pageNo;
    }

    public MidFcstParameter setPageNo(String pageNo)
    {
        this.pageNo = pageNo;
        return this;
    }


    public String getRegId()
    {
        return regId;
    }

    public MidFcstParameter setRegId(String regId)
    {
        this.regId = regId;
        return this;
    }

    public String getTmFc()
    {
        return tmFc;
    }

    public MidFcstParameter setTmFc(String tmFc)
    {
        this.tmFc = tmFc;
        return this;
    }

    public MidFcstParameter deepCopy()
    {
        return new MidFcstParameter(numOfRows, pageNo, regId, tmFc);
    }
}
