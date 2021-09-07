package com.zerodsoft.calendarplatform.weather.sunsetrise;

import java.util.Date;

public class SunSetRiseData
{
    private Date date;
    private Date sunrise;
    private Date sunset;

    public SunSetRiseData(Date date, Date sunrise, Date sunset)
    {
        this.date = date;
        this.sunrise = sunrise;
        this.sunset = sunset;
    }

    public Date getDate()
    {
        return date;
    }

    public Date getSunrise()
    {
        return sunrise;
    }

    public Date getSunset()
    {
        return sunset;
    }
}
