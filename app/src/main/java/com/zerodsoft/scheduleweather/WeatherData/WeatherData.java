package com.zerodsoft.scheduleweather.WeatherData;

import android.content.Context;

public class WeatherData implements Comparable
{
    private String areaName;
    private String areaCode;
    private String areaX;
    private String areaY;
    private String rainfall;
    private String eastWestWind;
    private String southNorthWind;
    private String humidity;
    private String precipitationForm;
    private String windDirection;
    private String windSpeed;
    private String temperature;
    private String chanceOfShower;
    private String precipitationRain;
    private String precipitationSnow;
    private String sky;
    private String threeHourTemp;
    private String morningMinTemp;
    private String dayMaxTemp;
    private String waveHeight;
    private String fcstDate;
    private String fcstTime;
    private String baseDate;
    private String baseTime;

    private Context context;

    public WeatherData(Context context)
    {
        this.context = context;
    }

    public String getAreaName()
    {
        return areaName;
    }

    public WeatherData setAreaName(String areaName)
    {
        this.areaName = areaName;
        return this;
    }

    public String getAreaCode()
    {
        return areaCode;
    }

    public WeatherData setAreaCode(String areaCode)
    {
        this.areaCode = areaCode;
        return this;
    }

    public String getAreaX()
    {
        return areaX;
    }

    public WeatherData setAreaX(String areaX)
    {
        this.areaX = areaX;
        return this;
    }

    public String getAreaY()
    {
        return areaY;
    }

    public WeatherData setAreaY(String areaY)
    {
        this.areaY = areaY;
        return this;
    }

    public String getRainfall()
    {
        return rainfall;
    }

    public WeatherData setRainfall(String rainfall)
    {
        this.rainfall = rainfall;
        return this;
    }

    public String getEastWestWind()
    {
        return eastWestWind;
    }

    public WeatherData setEastWestWind(String eastWestWind)
    {
        this.eastWestWind = eastWestWind;
        return this;
    }

    public String getSouthNorthWind()
    {
        return southNorthWind;
    }

    public WeatherData setSouthNorthWind(String southNorthWind)
    {
        this.southNorthWind = southNorthWind;
        return this;
    }

    public String getHumidity()
    {
        return humidity;
    }

    public WeatherData setHumidity(String humidity)
    {
        this.humidity = humidity;
        return this;
    }

    public String getPrecipitationForm()
    {
        return precipitationForm;
    }

    public WeatherData setPrecipitationForm(String precipitationForm)
    {
        this.precipitationForm = DataConverter.convertPrecipitationForm(precipitationForm, context);
        return this;
    }

    public String getWindDirection()
    {
        return windDirection;
    }


    public WeatherData setWindDirection(String windDirection)
    {
        this.windDirection = DataConverter.convertWindDirection(windDirection, context);
        return this;
    }

    public String getWindSpeed()
    {
        return windSpeed;
    }

    public WeatherData setWindSpeed(String windSpeed)
    {
        this.windSpeed = windSpeed;
        return this;
    }

    public String getTemperature()
    {
        return temperature;
    }

    public WeatherData setTemperature(String temperature)
    {
        this.temperature = temperature;
        return this;
    }

    public String getChanceOfShower()
    {
        return chanceOfShower;
    }

    public WeatherData setChanceOfShower(String chanceOfShower)
    {
        this.chanceOfShower = chanceOfShower;
        return this;
    }

    public String getPrecipitationRain()
    {
        return precipitationRain;
    }

    public WeatherData setPrecipitationRain(String precipitationRain)
    {
        this.precipitationRain = precipitationRain;
        return this;
    }

    public String getPrecipitationSnow()
    {
        return precipitationSnow;
    }

    public WeatherData setPrecipitationSnow(String precipitationSnow)
    {
        this.precipitationSnow = precipitationSnow;
        return this;
    }

    public String getSky()
    {
        return sky;
    }

    public WeatherData setSky(String sky)
    {
        this.sky = DataConverter.convertSky(sky, context);
        return this;
    }

    public String getThreeHourTemp()
    {
        return threeHourTemp;
    }

    public WeatherData setThreeHourTemp(String threeHourTemp)
    {
        this.threeHourTemp = threeHourTemp;
        return this;
    }

    public String getMorningMinTemp()
    {
        return morningMinTemp;
    }

    public WeatherData setMorningMinTemp(String morningMinTemp)
    {
        this.morningMinTemp = morningMinTemp;
        return this;
    }

    public String getDayMaxTemp()
    {
        return dayMaxTemp;
    }

    public WeatherData setDayMaxTemp(String dayMaxTemp)
    {
        this.dayMaxTemp = dayMaxTemp;
        return this;
    }

    public String getWaveHeight()
    {
        return waveHeight;
    }

    public WeatherData setWaveHeight(String waveHeight)
    {
        this.waveHeight = waveHeight;
        return this;
    }

    @Override
    public int compareTo(Object o)
    {
        WeatherData data = (WeatherData) o;
        return 0;
    }

    public String getFcstDate() throws NullPointerException
    {
        if (fcstDate == null)
        {
            throw new NullPointerException();
        } else
        {
            return fcstDate;
        }
    }

    public WeatherData setFcstDate(String fcstDate)
    {
        this.fcstDate = fcstDate;
        return this;
    }

    public String getFcstTime() throws NullPointerException
    {
        if (fcstTime == null)
        {
            throw new NullPointerException();
        } else
        {
            return fcstTime;
        }
    }

    public WeatherData setFcstTime(String fcstTime)
    {
        this.fcstTime = fcstTime;
        return this;
    }

    public String getBaseDate()
    {
        return baseDate;
    }

    public void setBaseDate(String baseDate)
    {
        this.baseDate = baseDate;
    }

    public String getBaseTime()
    {
        return baseTime;
    }

    public void setBaseTime(String baseTime)
    {
        this.baseTime = baseTime;
    }
}
