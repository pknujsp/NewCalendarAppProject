package com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtfcstresponse.UltraSrtFcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtncstresponse.UltraSrtNcstItem;

import java.util.List;

public class UltraSrtNcstData
{
    //기온
    private String temperature;
    //1시간 강수량
    private String precipitation1Hour;
    //습도
    private String humidity;
    //강수형태
    private String precipitationForm;
    //풍향
    private String windDirection;
    //풍속
    private String windSpeed;

    public UltraSrtNcstData(List<UltraSrtNcstItem> items)
    {
        for (UltraSrtNcstItem item : items)
        {
            if (item.getCategory().equals("T1H"))
            {
                temperature = item.getObsrValue();
            } else if (item.getCategory().equals("RN1"))
            {
                precipitation1Hour = item.getObsrValue();
            } else if (item.getCategory().equals("REH"))
            {
                humidity = item.getObsrValue();
            } else if (item.getCategory().equals("PTY"))
            {
                precipitationForm = WeatherDataConverter.convertPrecipitationForm(item.getObsrValue());
            } else if (item.getCategory().equals("VEC"))
            {
                windDirection = WeatherDataConverter.convertWindDirection(item.getObsrValue());
            } else if (item.getCategory().equals("WSD"))
            {
                windSpeed = item.getObsrValue();
            }
        }
    }

    public String getTemperature()
    {
        return temperature;
    }

    public UltraSrtNcstData setTemperature(String temperature)
    {
        this.temperature = temperature;
        return this;
    }

    public String getPrecipitation1Hour()
    {
        return precipitation1Hour;
    }

    public UltraSrtNcstData setPrecipitation1Hour(String precipitation1Hour)
    {
        this.precipitation1Hour = precipitation1Hour;
        return this;
    }

    public String getHumidity()
    {
        return humidity;
    }

    public UltraSrtNcstData setHumidity(String humidity)
    {
        this.humidity = humidity;
        return this;
    }

    public String getPrecipitationForm()
    {
        return precipitationForm;
    }

    public UltraSrtNcstData setPrecipitationForm(String precipitationForm)
    {
        this.precipitationForm = precipitationForm;
        return this;
    }

    public String getWindDirection()
    {
        return windDirection;
    }

    public UltraSrtNcstData setWindDirection(String windDirection)
    {
        this.windDirection = windDirection;
        return this;
    }

    public String getWindSpeed()
    {
        return windSpeed;
    }

    public UltraSrtNcstData setWindSpeed(String windSpeed)
    {
        this.windSpeed = windSpeed;
        return this;
    }
}
