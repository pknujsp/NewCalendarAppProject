package com.zerodsoft.scheduleweather.event.weather.repository;

import android.content.Context;

import com.zerodsoft.scheduleweather.event.weather.resultdata.WeatherData;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;
import com.zerodsoft.scheduleweather.retrofit.Querys;
import com.zerodsoft.scheduleweather.retrofit.RetrofitCallback;
import com.zerodsoft.scheduleweather.retrofit.paremeters.MidFcstParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.VilageFcstParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.WeatherItems;
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
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Response;

public abstract class WeatherDownloader
{
    /*
   서버로 부터 응답이 10초 이상 없는 경우 업데이트 취소 후 업데이트 실패 안내 메시지 표시
    */
    private final Calendar downloadedCalendar;

    public static final String ULTRA_SRT_NCST = "ULTRA_SRT_NCST";
    public static final String ULTRA_SRT_FCST = "ULTRA_SRT_FCST";
    public static final String VILAGE_FCST = "VILAGE_FCST";
    public static final String MID_LAND_FCST = "MID_LAND_FCST";
    public static final String MID_TA_FCST = "MID_TA_FCST";


    public WeatherDownloader()
    {
        downloadedCalendar = Calendar.getInstance(ClockUtil.TIME_ZONE);
    }

    public abstract void onResponse(DataWrapper<? extends WeatherItems> result);

    public void getWeatherData(VilageFcstParameter vilageFcstParameter, MidFcstParameter midLandFcstParameter, MidFcstParameter midTaFcstParameter)
    {
        //시간 업데이트
        downloadedCalendar.setTimeInMillis(System.currentTimeMillis());

        //초단기 실황
        Call call1 = getUltraSrtNcstData(vilageFcstParameter.deepCopy());
        //초단기 예보
        Call call2 = getUltraSrtFcstData(vilageFcstParameter.deepCopy());
        //동네예보
        Call call3 = getVilageFcstData(vilageFcstParameter.deepCopy());
        //중기 예보 육상, 기온
        Call call4 = getMidLandFcstData(midLandFcstParameter.deepCopy());
        Call call5 = getMidTaData(midTaFcstParameter.deepCopy());
    }

    /**
     * 초단기 실황
     *
     * @param parameter
     */
    public Call<UltraSrtNcstRoot> getUltraSrtNcstData(VilageFcstParameter parameter)
    {
        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.VILAGE_FCST);
        //basetime설정
        Calendar calendar = (Calendar) downloadedCalendar.clone();

        if (calendar.get(Calendar.MINUTE) < 40)
        {
            calendar.add(Calendar.HOUR_OF_DAY, -1);
        }
        parameter.setBaseDate(ClockUtil.yyyyMMdd.format(calendar.getTime()));
        parameter.setBaseTime(ClockUtil.HH.format(calendar.getTime()) + "00");

        Call<UltraSrtNcstRoot> call = querys.getUltraSrtNcstData(parameter.getMap());
        call.enqueue(new RetrofitCallback<UltraSrtNcstRoot>()
        {
            @Override
            protected void handleResponse(UltraSrtNcstRoot data)
            {
                WeatherDownloader.this.onResponse(new DataWrapper<UltraSrtNcstItems>(data.getResponse().getBody().getItems()));
            }

            @Override
            protected void handleError(Response<UltraSrtNcstRoot> response)
            {
                WeatherDownloader.this.onResponse(new DataWrapper<UltraSrtNcstItems>(new Exception(response.message())));
            }

            @Override
            protected void handleFailure(Exception e)
            {
                WeatherDownloader.this.onResponse(new DataWrapper<UltraSrtNcstItems>(e));
            }
        });

        return call;
    }

    /**
     * 초단기예보
     *
     * @param parameter
     */
    public Call<UltraSrtFcstRoot> getUltraSrtFcstData(VilageFcstParameter parameter)
    {
        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.VILAGE_FCST);
        //basetime설정
        Calendar calendar = (Calendar) downloadedCalendar.clone();

        if (calendar.get(Calendar.MINUTE) < 45)
        {
            calendar.add(Calendar.HOUR_OF_DAY, -1);
        }
        parameter.setBaseDate(ClockUtil.yyyyMMdd.format(calendar.getTime()));
        parameter.setBaseTime(ClockUtil.HH.format(calendar.getTime()) + "30");

        Call<UltraSrtFcstRoot> call = querys.getUltraSrtFcstData(parameter.getMap());

        call.enqueue(new RetrofitCallback<UltraSrtFcstRoot>()
        {
            @Override
            protected void handleResponse(UltraSrtFcstRoot data)
            {
                WeatherDownloader.this.onResponse(new DataWrapper<UltraSrtFcstItems>(data.getResponse().getBody().getItems()));
            }

            @Override
            protected void handleError(Response<UltraSrtFcstRoot> response)
            {
                WeatherDownloader.this.onResponse(new DataWrapper<UltraSrtFcstItems>(new Exception(response.message())));
            }

            @Override
            protected void handleFailure(Exception e)
            {
                WeatherDownloader.this.onResponse(new DataWrapper<UltraSrtFcstItems>(e));
            }
        });
        return call;
    }

    /**
     * 동네예보
     *
     * @param parameter
     */
    public Call<VilageFcstRoot> getVilageFcstData(VilageFcstParameter parameter)
    {
        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.VILAGE_FCST);
        //basetime설정
        Calendar calendar = (Calendar) downloadedCalendar.clone();

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

        parameter.setBaseDate(ClockUtil.yyyyMMdd.format(calendar.getTime()));
        parameter.setBaseTime(ClockUtil.HH.format(calendar.getTime()) + "00");

        Call<VilageFcstRoot> call = querys.getVilageFcstData(parameter.getMap());

        call.enqueue(new RetrofitCallback<VilageFcstRoot>()
        {
            @Override
            protected void handleResponse(VilageFcstRoot data)
            {
                WeatherDownloader.this.onResponse(new DataWrapper<VilageFcstItems>(data.getResponse().getBody().getItems()));
            }

            @Override
            protected void handleError(Response<VilageFcstRoot> response)
            {
                WeatherDownloader.this.onResponse(new DataWrapper<VilageFcstItems>(new Exception(response.message())));
            }

            @Override
            protected void handleFailure(Exception e)
            {
                WeatherDownloader.this.onResponse(new DataWrapper<UltraSrtFcstItems>(e));

            }
        });
        return call;
    }

    /**
     * 중기육상예보
     *
     * @param parameter
     */
    public Call<MidLandFcstRoot> getMidLandFcstData(MidFcstParameter parameter)
    {
        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.MID_FCST);

        Calendar calendar = (Calendar) downloadedCalendar.clone();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        if (hour >= 18 && minute >= 1)
        {
            parameter.setTmFc(ClockUtil.yyyyMMdd.format(calendar.getTime()) + "1800");
        } else if (hour >= 6 && minute >= 1)
        {
            parameter.setTmFc(ClockUtil.yyyyMMdd.format(calendar.getTime()) + "0600");
        } else
        {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            parameter.setTmFc(ClockUtil.yyyyMMdd.format(calendar.getTime()) + "1800");
        }

        Call<MidLandFcstRoot> call = querys.getMidLandFcstData(parameter.getMap());

        call.enqueue(new RetrofitCallback<MidLandFcstRoot>()
        {
            @Override
            protected void handleResponse(MidLandFcstRoot data)
            {
                WeatherDownloader.this.onResponse(new DataWrapper<MidLandFcstItems>(data.getResponse().getBody().getItems()));
            }

            @Override
            protected void handleError(Response<MidLandFcstRoot> response)
            {
                WeatherDownloader.this.onResponse(new DataWrapper<MidLandFcstItems>(new Exception(response.message())));
            }

            @Override
            protected void handleFailure(Exception e)
            {
                WeatherDownloader.this.onResponse(new DataWrapper<MidLandFcstItems>(e));
            }
        });
        return call;
    }

    /**
     * 중기기온조회
     *
     * @param parameter
     */
    public Call<MidTaRoot> getMidTaData(MidFcstParameter parameter)
    {
        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.MID_FCST);

        Calendar calendar = (Calendar) downloadedCalendar.clone();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        if (hour >= 18 && minute >= 1)
        {
            parameter.setTmFc(ClockUtil.yyyyMMdd.format(calendar.getTime()) + "1800");
        } else if (hour >= 6 && minute >= 1)
        {
            parameter.setTmFc(ClockUtil.yyyyMMdd.format(calendar.getTime()) + "0600");
        } else
        {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            parameter.setTmFc(ClockUtil.yyyyMMdd.format(calendar.getTime()) + "1800");
        }

        Call<MidTaRoot> call = querys.getMidTaData(parameter.getMap());

        call.enqueue(new RetrofitCallback<MidTaRoot>()
        {
            @Override
            protected void handleResponse(MidTaRoot data)
            {
                WeatherDownloader.this.onResponse(new DataWrapper<MidTaItems>(data.getResponse().getBody().getItems()));
            }

            @Override
            protected void handleError(Response<MidTaRoot> response)
            {
                WeatherDownloader.this.onResponse(new DataWrapper<MidTaItems>(new Exception(response.message())));
            }

            @Override
            protected void handleFailure(Exception e)
            {
                WeatherDownloader.this.onResponse(new DataWrapper<MidTaItems>(e));
            }
        });
        return call;
    }


}
