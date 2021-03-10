package com.zerodsoft.scheduleweather.event.weather.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dao.WeatherAreaCodeDAO;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.utility.LonLat;

import java.util.List;

public class WeatherRepository
{
    /*
    서버로 부터 응답이 10초 이상 없는 경우 업데이트 취소 후 업데이트 실패 안내 메시지 표시
     */
    private LiveData<List<WeatherAreaCodeDTO>> areaCodeLiveData;
    private WeatherAreaCodeDAO weatherAreaCodeDAO;

    public WeatherRepository(Context context)
    {
        weatherAreaCodeDAO = AppDb.getInstance(context).weatherAreaCodeDAO();
    }

    public void selectAreaCode(LonLat lonLat)
    {
        areaCodeLiveData = weatherAreaCodeDAO.selectAreaCode(lonLat.getLatitude(), lonLat.getLongitude());
    }

    public LiveData<List<WeatherAreaCodeDTO>> getAreaCodeLiveData()
    {
        return areaCodeLiveData;
    }
}
