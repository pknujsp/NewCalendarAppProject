package com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;
import com.zerodsoft.scheduleweather.retrofit.Querys;
import com.zerodsoft.scheduleweather.retrofit.paremeters.MidFcstParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.VilageFcstParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.midlandfcstresponse.MidLandFcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.midlandfcstresponse.MidLandFcstRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.midtaresponse.MidTaItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.midtaresponse.MidTaRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtfcstresponse.UltraSrtFcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtfcstresponse.UltraSrtFcstRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtncstresponse.UltraSrtNcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtncstresponse.UltraSrtNcstRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.vilagefcstresponse.VilageFcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.vilagefcstresponse.VilageFcstRoot;
import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dao.WeatherAreaCodeDAO;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.utility.Clock;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherRepository
{
    private MutableLiveData<List<UltraSrtNcstItem>> ultraSrtNcstLiveData = new MutableLiveData<>();
    private MutableLiveData<List<UltraSrtFcstItem>> ultraSrtFcstLiveData = new MutableLiveData<>();
    private MutableLiveData<List<VilageFcstItem>> vilageFcstLiveData = new MutableLiveData<>();
    private MutableLiveData<List<MidLandFcstItem>> midLandFcstLiveData = new MutableLiveData<>();
    private MutableLiveData<List<MidTaItem>> midTaLiveData = new MutableLiveData<>();

    private LiveData<List<WeatherAreaCodeDTO>> areaCodeLiveData;

    private WeatherAreaCodeDAO weatherAreaCodeDAO;

    public WeatherRepository(Context context)
    {
        weatherAreaCodeDAO = AppDb.getInstance(context).weatherAreaCodeDAO();
    }

    public void selectAreaCode(int x, int y)
    {
        areaCodeLiveData = weatherAreaCodeDAO.selectAreaCode(Integer.toString(x), Integer.toString(y));
    }

    public LiveData<List<WeatherAreaCodeDTO>> getAreaCodeLiveData()
    {
        return areaCodeLiveData;
    }

    public void getUltraSrtNcstData(VilageFcstParameter parameter)
    {
        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.VILAGE_FCST);
        //basetime설정
        Calendar calendar = Calendar.getInstance(Clock.TIME_ZONE);

        if (calendar.get(Calendar.MINUTE) < 40)
        {
            calendar.add(Calendar.HOUR_OF_DAY, -1);
        }
        parameter.setBaseDate(Clock.yyyyMMdd_FORMAT.format(calendar.getTime()));
        parameter.setBaseTime(Clock.WEATHER_TIME_FORMAT.format(calendar.getTime()) + "00");

        Call<UltraSrtNcstRoot> call = querys.getUltraSrtNcstData(parameter.getMap());

        call.enqueue(new Callback<UltraSrtNcstRoot>()
        {
            @Override
            public void onResponse(Call<UltraSrtNcstRoot> call, Response<UltraSrtNcstRoot> response)
            {
                List<UltraSrtNcstItem> items = response.body().getResponse().getBody().getItems().getItem();
                ultraSrtNcstLiveData.setValue(items);
            }

            @Override
            public void onFailure(Call<UltraSrtNcstRoot> call, Throwable t)
            {

            }
        });
    }

    public void getUltraSrtFcstData(VilageFcstParameter parameter)
    {
        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.VILAGE_FCST);
        //basetime설정
        Calendar calendar = Calendar.getInstance(Clock.TIME_ZONE);

        if (calendar.get(Calendar.MINUTE) < 45)
        {
            calendar.add(Calendar.HOUR_OF_DAY, -1);
        }
        parameter.setBaseDate(Clock.yyyyMMdd_FORMAT.format(calendar.getTime()));
        parameter.setBaseTime(Clock.WEATHER_TIME_FORMAT.format(calendar.getTime()) + "30");

        Call<UltraSrtFcstRoot> call = querys.getUltraSrtFcstData(parameter.getMap());

        call.enqueue(new Callback<UltraSrtFcstRoot>()
        {
            @Override
            public void onResponse(Call<UltraSrtFcstRoot> call, Response<UltraSrtFcstRoot> response)
            {
                List<UltraSrtFcstItem> items = response.body().getResponse().getBody().getItems().getItem();
                ultraSrtFcstLiveData.setValue(items);
            }

            @Override
            public void onFailure(Call<UltraSrtFcstRoot> call, Throwable t)
            {

            }
        });
    }

    public void getVilageFcstData(VilageFcstParameter parameter)
    {
        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.VILAGE_FCST);
        //basetime설정
        Calendar calendar = Calendar.getInstance(Clock.TIME_ZONE);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int i = hour >= 0 && hour <= 2 ? 7 : hour / 3 - 1;
        int baseHour = 0;

        if (calendar.get(Calendar.MINUTE) > 10 && (hour - 2) % 3 == 0)
        {
            // ex)1411인 경우
            baseHour = 3 * ((hour - 2) / 3) + 2;
            i = 0;
        } else
        {
            baseHour = 3 * i + 2;
        }

        if (i == 7)
        {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
        } else
        {
            calendar.set(Calendar.HOUR_OF_DAY, baseHour);
        }

        parameter.setBaseDate(Clock.yyyyMMdd_FORMAT.format(calendar.getTime()));
        parameter.setBaseTime(Clock.WEATHER_TIME_FORMAT.format(calendar.getTime()) + "00");

        Call<VilageFcstRoot> call = querys.getVilageFcstData(parameter.getMap());

        call.enqueue(new Callback<VilageFcstRoot>()
        {
            @Override
            public void onResponse(Call<VilageFcstRoot> call, Response<VilageFcstRoot> response)
            {
                List<VilageFcstItem> items = response.body().getResponse().getBody().getItems().getItem();
                vilageFcstLiveData.setValue(items);
            }

            @Override
            public void onFailure(Call<VilageFcstRoot> call, Throwable t)
            {

            }
        });
    }

    public void getMidLandFcstData(MidFcstParameter parameter)
    {
        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.MID_FCST);

        Calendar calendar = Calendar.getInstance(Clock.TIME_ZONE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        if (hour >= 18 && minute >= 1)
        {
            parameter.setTmFc(Clock.yyyyMMdd_FORMAT.format(calendar.getTime()) + "1800");
        } else if (hour >= 6 && minute >= 1)
        {
            parameter.setTmFc(Clock.yyyyMMdd_FORMAT.format(calendar.getTime()) + "0600");
        } else
        {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            parameter.setTmFc(Clock.yyyyMMdd_FORMAT.format(calendar.getTime()) + "1800");
        }

        Call<MidLandFcstRoot> call = querys.getMidLandFcstData(parameter.getMap());

        call.enqueue(new Callback<MidLandFcstRoot>()
        {
            @Override
            public void onResponse(Call<MidLandFcstRoot> call, Response<MidLandFcstRoot> response)
            {
                List<MidLandFcstItem> items = response.body().getResponse().getBody().getItems().getItem();
                midLandFcstLiveData.setValue(items);
            }

            @Override
            public void onFailure(Call<MidLandFcstRoot> call, Throwable t)
            {

            }
        });
    }

    public void getMidTaData(MidFcstParameter parameter)
    {
        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.MID_FCST);

        Calendar calendar = Calendar.getInstance(Clock.TIME_ZONE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        if (hour >= 18 && minute >= 1)
        {
            parameter.setTmFc(Clock.yyyyMMdd_FORMAT.format(calendar.getTime()) + "1800");
        } else if (hour >= 6 && minute >= 1)
        {
            parameter.setTmFc(Clock.yyyyMMdd_FORMAT.format(calendar.getTime()) + "0600");
        } else
        {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            parameter.setTmFc(Clock.yyyyMMdd_FORMAT.format(calendar.getTime()) + "1800");
        }

        Call<MidTaRoot> call = querys.getMidTaData(parameter.getMap());

        call.enqueue(new Callback<MidTaRoot>()
        {
            @Override
            public void onResponse(Call<MidTaRoot> call, Response<MidTaRoot> response)
            {
                List<MidTaItem> items = response.body().getResponse().getBody().getItems().getItem();
                midTaLiveData.setValue(items);
            }

            @Override
            public void onFailure(Call<MidTaRoot> call, Throwable t)
            {

            }
        });
    }

    public MutableLiveData<List<UltraSrtNcstItem>> getUltraSrtNcstLiveData()
    {
        return ultraSrtNcstLiveData;
    }

    public MutableLiveData<List<UltraSrtFcstItem>> getUltraSrtFcstLiveData()
    {
        return ultraSrtFcstLiveData;
    }

    public MutableLiveData<List<VilageFcstItem>> getVilageFcstLiveData()
    {
        return vilageFcstLiveData;
    }

    public MutableLiveData<List<MidLandFcstItem>> getMidLandFcstLiveData()
    {
        return midLandFcstLiveData;
    }

    public MutableLiveData<List<MidTaItem>> getMidTaLiveData()
    {
        return midTaLiveData;
    }
}
