package com.zerodsoft.scheduleweather.retrofit.paremeters;

import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;

import java.util.HashMap;
import java.util.Map;

public class VilageFcstParameter implements Cloneable
{
    private String serviceKey = HttpCommunicationClient.VILAGE_FCST_INFO_SERVICE_SERVICE_KEY;
    private String numOfRows = "300";
    private String pageNo;
    private String dataType = HttpCommunicationClient.DATATYPE;
    private String baseDate;
    private String baseTime;
    private String nx;
    private String ny;
    private Map<String, String> map = new HashMap<>();

    public VilageFcstParameter()
    {

    }

    public VilageFcstParameter(String pageNo, String baseDate, String baseTime, String nx, String ny)
    {
        this.pageNo = pageNo;
        this.baseDate = baseDate;
        this.baseTime = baseTime;
        this.nx = nx;
        this.ny = ny;
    }

    public Map<String, String> getMap()
    {
        map.clear();

        map.put("serviceKey", serviceKey);
        map.put("numOfRows", numOfRows);
        map.put("pageNo", pageNo);
        map.put("dataType", dataType);
        map.put("base_date", baseDate);
        map.put("base_time", baseTime);
        map.put("nx", nx);
        map.put("ny", ny);

        return map;
    }


    public String getNumOfRows()
    {
        return numOfRows;
    }

    public VilageFcstParameter setNumOfRows(String numOfRows)
    {
        this.numOfRows = numOfRows;
        return this;
    }

    public String getPageNo()
    {
        return pageNo;
    }

    public VilageFcstParameter setPageNo(String pageNo)
    {
        this.pageNo = pageNo;
        return this;
    }

    public String getBaseDate()
    {
        return baseDate;
    }

    public VilageFcstParameter setBaseDate(String baseDate)
    {
        this.baseDate = baseDate;
        return this;
    }

    public String getBaseTime()
    {
        return baseTime;
    }

    public VilageFcstParameter setBaseTime(String baseTime)
    {
        this.baseTime = baseTime;
        return this;
    }

    public String getNx()
    {
        return nx;
    }

    public VilageFcstParameter setNx(String nx)
    {
        this.nx = nx;
        return this;
    }

    public String getNy()
    {
        return ny;
    }

    public VilageFcstParameter setNy(String ny)
    {
        this.ny = ny;
        return this;
    }

    public VilageFcstParameter deepCopy()
    {
        return new VilageFcstParameter(pageNo, baseDate, baseTime, nx, ny);
    }
}
