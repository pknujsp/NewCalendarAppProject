package com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.responseresult;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtncstresponse.UltraSrtNcstItem;
import com.zerodsoft.scheduleweather.utility.WeatherDataConverter;

import java.util.List;

public class UltraSrtNcstData
{
    //nx
    private String nx;
    //ny
    private String ny;
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


    public UltraSrtNcstData(String nx, String ny, String temperature, String precipitation1Hour, String humidity, String precipitationForm, String windDirection, String windSpeed)
    {
        this.nx = nx;
        this.ny = ny;
        this.temperature = temperature;
        this.precipitation1Hour = precipitation1Hour;
        this.humidity = humidity;
        this.precipitationForm = precipitationForm;
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
    }

    public UltraSrtNcstData(List<UltraSrtNcstItem> items)
    {
        nx = items.get(0).getNx();
        ny = items.get(0).getNy();

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

    public String getNx()
    {
        return nx;
    }

    public String getNy()
    {
        return ny;
    }

    public UltraSrtNcstData deepCopy()
    {
        return new UltraSrtNcstData(nx, ny, temperature, precipitation1Hour, humidity, precipitationForm, windDirection, windSpeed);
    }
}
