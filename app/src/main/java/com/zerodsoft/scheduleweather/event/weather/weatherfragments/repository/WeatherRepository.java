package com.zerodsoft.scheduleweather.event.weather.weatherfragments.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;
import com.zerodsoft.scheduleweather.retrofit.Querys;
import com.zerodsoft.scheduleweather.retrofit.RetrofitCallback;
import com.zerodsoft.scheduleweather.retrofit.paremeters.MidFcstParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.VilageFcstParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.midlandfcstresponse.MidLandFcstItems;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.midlandfcstresponse.MidLandFcstRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.midtaresponse.MidTaItems;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.midtaresponse.MidTaRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtfcstresponse.UltraSrtFcstItems;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtfcstresponse.UltraSrtFcstRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtncstresponse.UltraSrtNcstItems;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtncstresponse.UltraSrtNcstRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.vilagefcstresponse.VilageFcstItems;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.vilagefcstresponse.VilageFcstRoot;
import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dao.WeatherAreaCodeDAO;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.event.weather.weatherfragments.resultdata.WeatherData;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.utility.LonLat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

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
