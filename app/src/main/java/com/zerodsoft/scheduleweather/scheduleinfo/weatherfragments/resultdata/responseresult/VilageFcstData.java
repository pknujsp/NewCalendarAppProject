package com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.responseresult;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.vilagefcstresponse.VilageFcstItem;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.utility.WeatherDataConverter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class VilageFcstData
{
    //nx
    private String nx;
    //ny
    private String ny;

    private Date dateTime;

    //강수확률 POP
    private String chanceOfShower;
    //강수형태 PTY
    private String precipitationForm;
    //6시간 강수량 R06
    private String rainPrecipitation6Hour;
    //습도 REH
    private String humidity;
    //6시간 신적설 S06
    private String snowPrecipitation6Hour;
    //구름상태 SKY
    private String sky;
    //3시간 기온 T3H
    private String temp3Hour;
    //최저기온 TMN
    private String tempMin;
    //최고기온 TMX
    private String tempMax;
    //풍향 VEC
    private String windDirection;
    //풍속 WSD
    private String windSpeed;

    public VilageFcstData(List<VilageFcstItem> items)
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

        for (VilageFcstItem item : items)
        {
            if (item.getCategory().equals("POP"))
            {
                chanceOfShower = item.getFcstValue();
            } else if (item.getCategory().equals("PTY"))
            {
                precipitationForm = WeatherDataConverter.convertPrecipitationForm(item.getFcstValue());
            } else if (item.getCategory().equals("R06"))
            {
                rainPrecipitation6Hour = item.getFcstValue();
            } else if (item.getCategory().equals("REH"))
            {
                humidity = item.getFcstValue();
            } else if (item.getCategory().equals("S06"))
            {
                snowPrecipitation6Hour = item.getFcstValue();
            } else if (item.getCategory().equals("SKY"))
            {
                sky = WeatherDataConverter.convertSky(item.getFcstValue());
            } else if (item.getCategory().equals("T3H"))
            {
                temp3Hour = item.getFcstValue();
            } else if (item.getCategory().equals("TMN"))
            {
                tempMin = item.getFcstValue();
            } else if (item.getCategory().equals("TMX"))
            {
                tempMax = item.getFcstValue();
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

    public String getChanceOfShower()
    {
        return chanceOfShower;
    }

    public String getPrecipitationForm()
    {
        return precipitationForm;
    }

    public String getRainPrecipitation6Hour()
    {
        return rainPrecipitation6Hour;
    }

    public String getHumidity()
    {
        return humidity;
    }

    public String getSnowPrecipitation6Hour()
    {
        return snowPrecipitation6Hour;
    }

    public String getSky()
    {
        return sky;
    }

    public String getTemp3Hour()
    {
        return temp3Hour;
    }

    public String getTempMin()
    {
        return tempMin;
    }

    public String getTempMax()
    {
        return tempMax;
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
