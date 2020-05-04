package com.zerodsoft.tripweather.Room;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.zerodsoft.tripweather.Room.DAO.NforecastDao;
import com.zerodsoft.tripweather.Room.DTO.Nforecast;
import com.zerodsoft.tripweather.Room.DTO.ScheduleNForecast;
import com.zerodsoft.tripweather.Utility.Actions;
import com.zerodsoft.tripweather.WeatherData.ForecastAreaData;
import com.zerodsoft.tripweather.WeatherData.WeatherData;

import java.util.ArrayList;

public class WeatherDataThread extends Thread
{
    private int action = 0;
    ArrayList<ForecastAreaData> nForecastDataList;
    Handler handler;
    AppDb appDb;

    public WeatherDataThread(ArrayList<ForecastAreaData> nForecastDataList, Handler handler, Context context, int action)
    {
        this.nForecastDataList = nForecastDataList;
        this.handler = handler;
        this.appDb = AppDb.getInstance(context);
        this.action = action;
    }

    @Override
    public void run()
    {
        switch (action)
        {
            case Actions.INSERT_NFORECAST_DATA:
                NforecastDao nforecastDao = appDb.nforecastDao();
                ArrayList<Nforecast> nForecastList = new ArrayList<>();
                final int travelId = nForecastDataList.get(0).getTravelId();

                for (ForecastAreaData forecastAreaData : nForecastDataList)
                {
                    int scheduleId = forecastAreaData.getScheduleId();

                    for (WeatherData weatherData : forecastAreaData.getForecastData())
                    {
                        Nforecast nForecast = new Nforecast();

                        nForecast.setParentId(scheduleId);
                        nForecast.setDate(weatherData.getFcstDate());
                        nForecast.setTime(weatherData.getFcstTime());
                        nForecast.setChanceOfShower(weatherData.getChanceOfShower());
                        nForecast.setPrecipitationForm(weatherData.getPrecipitationForm());
                        nForecast.setSixHourPrecipitation(weatherData.getPrecipitationRain());
                        nForecast.setHumidity(weatherData.getHumidity());
                        nForecast.setSixHourFreshSnowCover(weatherData.getPrecipitationSnow());
                        nForecast.setSky(weatherData.getSky());
                        nForecast.setThreeHourTemp(weatherData.getThreeHourTemp());
                        nForecast.setMorningMinTemp(weatherData.getMorningMinTemp());
                        nForecast.setDayMaxTemp(weatherData.getDayMaxTemp());
                        nForecast.setWindDirection(weatherData.getWindDirection());
                        nForecast.setWindSpeed(weatherData.getWindSpeed());

                        nForecastList.add(nForecast);
                        nforecastDao.insertNforecastData(nForecast);
                    }
                }
                ArrayList<ScheduleNForecast> savedNForecastDataList = (ArrayList<ScheduleNForecast>) nforecastDao.getNForecastData(travelId);

                Message msg = handler.obtainMessage();
                Bundle bundle = new Bundle();

                bundle.putSerializable("savedNForecastDataList", savedNForecastDataList);
                msg.setData(bundle);
                msg.what = Actions.REFRESH_ADAPTER;

                handler.sendMessage(msg);
                break;
        }


    }

}
