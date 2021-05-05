package com.zerodsoft.scheduleweather.weather.ultrasrtncst;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtncstresponse.UltraSrtNcstItem;
import com.zerodsoft.scheduleweather.utility.WeatherDataConverter;

import java.util.List;

public class UltraSrtNcstFinalData
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


    public UltraSrtNcstFinalData(String nx, String ny, String temperature, String precipitation1Hour, String humidity, String precipitationForm, String windDirection, String windSpeed)
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

    public UltraSrtNcstFinalData(List<UltraSrtNcstItem> items)
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

    public UltraSrtNcstFinalData setTemperature(String temperature)
    {
        this.temperature = temperature;
        return this;
    }

    public String getPrecipitation1Hour()
    {
        return precipitation1Hour;
    }

    public UltraSrtNcstFinalData setPrecipitation1Hour(String precipitation1Hour)
    {
        this.precipitation1Hour = precipitation1Hour;
        return this;
    }

    public String getHumidity()
    {
        return humidity;
    }

    public UltraSrtNcstFinalData setHumidity(String humidity)
    {
        this.humidity = humidity;
        return this;
    }

    public String getPrecipitationForm()
    {
        return precipitationForm;
    }

    public UltraSrtNcstFinalData setPrecipitationForm(String precipitationForm)
    {
        this.precipitationForm = precipitationForm;
        return this;
    }

    public String getWindDirection()
    {
        return windDirection;
    }

    public UltraSrtNcstFinalData setWindDirection(String windDirection)
    {
        this.windDirection = windDirection;
        return this;
    }

    public String getWindSpeed()
    {
        return windSpeed;
    }

    public UltraSrtNcstFinalData setWindSpeed(String windSpeed)
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

    public UltraSrtNcstFinalData deepCopy()
    {
        return new UltraSrtNcstFinalData(nx, ny, temperature, precipitation1Hour, humidity, precipitationForm, windDirection, windSpeed);
    }
}
