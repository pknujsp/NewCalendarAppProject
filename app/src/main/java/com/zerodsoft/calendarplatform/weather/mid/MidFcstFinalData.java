package com.zerodsoft.calendarplatform.weather.mid;

public class MidFcstFinalData
{
    private String date;
    private String amSky;
    private String pmSky;
    private String sky;
    private String amShowerOfChance;
    private String pmShowerOfChance;
    private String showerOfChance;
    private String tempMin;
    private String tempMax;

    public MidFcstFinalData(String date, String amSky, String pmSky, String amShowerOfChance, String pmShowerOfChance, String tempMin, String tempMax)
    {
        this.date = date;
        this.amSky = amSky;
        this.pmSky = pmSky;
        this.amShowerOfChance = amShowerOfChance;
        this.pmShowerOfChance = pmShowerOfChance;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
    }

    public MidFcstFinalData(String date, String sky, String showerOfChance, String tempMin, String tempMax)
    {
        this.date = date;
        this.sky = sky;
        this.showerOfChance = showerOfChance;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
    }

    public String getShowerOfChance()
    {
        return showerOfChance;
    }

    public String getSky()
    {
        return sky;
    }

    public String getDate()
    {
        return date;
    }

    public String getAmSky()
    {
        return amSky;
    }

    public String getPmSky()
    {
        return pmSky;
    }

    public String getAmShowerOfChance()
    {
        return amShowerOfChance;
    }

    public String getPmShowerOfChance()
    {
        return pmShowerOfChance;
    }

    public String getTempMin()
    {
        return tempMin;
    }

    public String getTempMax()
    {
        return tempMax;
    }
}
