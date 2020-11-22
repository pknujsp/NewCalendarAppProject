package com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.responseresult;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtfcstresponse.UltraSrtFcstItem;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.utility.WeatherDataConverter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UltraSrtFcstData
{
    //nx
    private String nx;
    //ny
    private String ny;

    //일자
    private Date dateTime;

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
        String date = items.get(0).getFcstDate();
        String time = items.get(0).getFcstTime().substring(0, 2);

        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(4, 6));
        int day = Integer.parseInt(date.substring(6, 8));
        int hour = Integer.parseInt(time.substring(0, 2));

        Calendar calendar = Calendar.getInstance(ClockUtil.TIME_ZONE);
        calendar.set(year, month - 1, day, hour, 0, 0);

        dateTime = calendar.getTime();

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

    public Date getDateTime()
    {
        return dateTime;
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
