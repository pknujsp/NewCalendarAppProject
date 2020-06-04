package com.zerodsoft.scheduleweather.Room;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.zerodsoft.scheduleweather.ProcessingType;
import com.zerodsoft.scheduleweather.Room.DAO.NforecastDao;
import com.zerodsoft.scheduleweather.Room.DAO.WeatherUpdateTimeDao;
import com.zerodsoft.scheduleweather.Room.DTO.Nforecast;
import com.zerodsoft.scheduleweather.Room.DTO.ScheduleNForecast;
import com.zerodsoft.scheduleweather.Room.DTO.WeatherUpdateTime;
import com.zerodsoft.scheduleweather.Utility.Actions;
import com.zerodsoft.scheduleweather.WeatherData.ForecastAreaData;
import com.zerodsoft.scheduleweather.WeatherData.WeatherData;

import java.util.ArrayList;

public class WeatherDataThread extends Thread
{
    private ProcessingType processingType;
    ArrayList<ForecastAreaData> nForecastDataList;
    Handler handler;
    AppDb appDb;
    Bundle bundle;

    public WeatherDataThread(ArrayList<ForecastAreaData> nForecastDataList, Bundle bundle, Handler handler, Context context, ProcessingType processingType)
    {
        this.nForecastDataList = nForecastDataList;
        this.handler = handler;
        this.bundle = bundle;
        this.appDb = AppDb.getInstance(context);
        this.processingType = processingType;
    }

    public WeatherDataThread(Bundle bundle, Handler handler, Context context, ProcessingType processingType)
    {
        this.handler = handler;
        this.appDb = AppDb.getInstance(context);
        this.processingType = processingType;
        this.bundle = bundle;
    }

    @Override
    public void run()
    {
        switch (processingType.getAction())
        {
            case Actions.INSERT_NFORECAST_DATA:
                insertNForecastData();
                break;

            case Actions.SELECT_NFORECAST_DATA:
                selectNForecastData(bundle.getInt("travelId"));
                break;
        }


    }

    private void insertNForecastData()
    {
        NforecastDao nforecastDao = appDb.nforecastDao();
        ArrayList<Nforecast> nForecastList = new ArrayList<>();
        final int travelId = nForecastDataList.get(0).getTravelId();
        final String baseDate = bundle.getString("baseDate");
        final String baseTime = bundle.getString("baseTime");

        for (ForecastAreaData forecastAreaData : nForecastDataList)
        {
            int scheduleId = forecastAreaData.getScheduleId();

            if (processingType.getProcessingType() == Actions.UPDATE)
            {
                nforecastDao.deleteNforecastData(scheduleId);
            }
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

        if (processingType.getProcessingType() == Actions.INSERT)
        {
            insertUpdateTimeInfo(travelId, baseDate, baseTime);
        } else if (processingType.getProcessingType() == Actions.UPDATE)
        {
            updateUpdatedTimeInfo(travelId, baseDate, baseTime);
        }
        selectNForecastData(travelId);
    }

    private void selectNForecastData(int travelId)
    {
        NforecastDao nforecastDao = appDb.nforecastDao();

        ArrayList<ScheduleNForecast> savedNForecastDataList = (ArrayList<ScheduleNForecast>) nforecastDao.getNForecastData(travelId);
        WeatherUpdateTime weatherUpdateTime = selectUpdatedTimeInfo(travelId);

        Message msg = handler.obtainMessage();
        Bundle bundle = new Bundle();

        bundle.putSerializable("savedNForecastDataList", savedNForecastDataList);
        bundle.putString("updatedDate", weatherUpdateTime.getUpdatedDate());
        bundle.putString("updatedTime", weatherUpdateTime.getUpdatedTime());
        msg.setData(bundle);
        msg.what = Actions.REFRESH_ADAPTER;

        handler.sendMessage(msg);
    }

    private void insertUpdateTimeInfo(int travelId, String baseDate, String baseTime)
    {
        WeatherUpdateTimeDao dao = appDb.weatherUpdateTimeDao();
        WeatherUpdateTime dto = new WeatherUpdateTime();

        dto.setTravelParentId(travelId);
        dto.setForecastDate(baseDate);
        dto.setForecastTime(baseTime);
        dto.setUpdatedDate(bundle.getString("updatedDate"));
        dto.setUpdatedTime(bundle.getString("updatedTime"));

        dao.insertUpdateData(dto);

    }

    private void updateUpdatedTimeInfo(int travelId, String baseDate, String baseTime)
    {
        WeatherUpdateTimeDao dao = appDb.weatherUpdateTimeDao();
        WeatherUpdateTime dto = new WeatherUpdateTime();

        dto.setTravelParentId(travelId);
        dto.setForecastDate(baseDate);
        dto.setForecastTime(baseTime);
        dto.setUpdatedDate(bundle.getString("updatedDate"));
        dto.setUpdatedTime(bundle.getString("updatedTime"));

        dao.updateUpdateInfo(baseDate, baseTime, bundle.getString("updatedDate"), bundle.getString("updatedTime"), travelId);
    }

    private WeatherUpdateTime selectUpdatedTimeInfo(int travelId)
    {
        WeatherUpdateTimeDao dao = appDb.weatherUpdateTimeDao();

        return dao.getTravelUpdateInfo(travelId);
    }

}