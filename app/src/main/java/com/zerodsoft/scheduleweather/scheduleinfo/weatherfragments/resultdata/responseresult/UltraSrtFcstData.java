package com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.responseresult;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtfcstresponse.UltraSrtFcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtncstresponse.UltraSrtNcstItem;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.WeatherDataConverter;

import java.util.List;

public class UltraSrtFcstData
{
    //nx
    private String nx;
    //ny
    private String ny;

    //일자
    private String date;
    //시각
    private String time;

    //구름상태 SKY
    private String sky;
    //기온
    private String temperature;
    //1시간 강수량
    private String precipitation1Hour;
    //습도
    private String humidity;
    //강수형태 PTY
    private String precipitationForm;
    //풍향
    private String windDirection;
    //풍속
    private String windSpeed;

    public UltraSrtFcstData(List<UltraSrtFcstItem> items)
    {
        nx = items.get(0).getNx();
        ny = items.get(0).getNy();
        date = items.get(0).getFcstDate();
        time = items.get(0).getFcstTime().substring(0, 2);

        for (UltraSrtFcstItem item : items)
        {
            if (item.getCategory().equals("T1H"))
            {
                temperature = item.getFcstValue();
            } else if (item.getCategory().equals("RN1"))
            {
                precipitation1Hour = item.getFcstValue();
            } else if (item.getCategory().equals("SKY"))
            {
                sky = WeatherDataConverter.convertSky(item.getFcstValue());
            } else if (item.getCategory().equals("REH"))
            {
                humidity = item.getFcstValue();
            } else if (item.getCategory().equals("PTY"))
            {
                precipitationForm = WeatherDataConverter.convertPrecipitationForm(item.getFcstValue());
            } else if (item.getCategory().equals("VEC"))
            {
                windDirection = WeatherDataConverter.convertWindDirection(item.getFcstValue());
            } else if (item.getCategory().equals("WSD"))
            {
                windSpeed = item.getFcstValue();
            }
        }
    }

    public String getNx()
    {
        return nx;
    }

    public String getNy()
    {
        return ny;
    }

    public String getDate()
    {
        return date;
    }

    public String getTime()
    {
        return time;
    }

    public String getSky()
    {
        return sky;
    }

    public String getTemperature()
    {
        return temperature;
    }

    public String getPrecipitation1Hour()
    {
        return precipitation1Hour;
    }

    public String getHumidity()
    {
        return humidity;
    }

    public String getPrecipitationForm()
    {
        return precipitationForm;
    }

    public String getWindDirection()
    {
        return windDirection;
    }

    public String getWindSpeed()
    {
        return windSpeed;
    }
}
