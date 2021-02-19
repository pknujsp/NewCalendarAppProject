package com.zerodsoft.scheduleweather.event.weather.weatherfragments.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.zerodsoft.scheduleweather.event.weather.weatherfragments.resultdata.WeatherData;
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

    private static final int TOTAL_REQUEST = 5;
    private int currentResponseCount = 0;
    private WeatherData weatherData;

    public WeatherDownloader(Context context)
    {
        downloadedCalendar = Calendar.getInstance(ClockUtil.TIME_ZONE);
    }


    public void onSuccessful(DataWrapper<WeatherItems> result)
    {
        if (result.getData() instanceof UltraSrtNcstItems)
        {
            weatherData.setUltraSrtNcstItems((UltraSrtNcstItems) result.getData());
        } else if (result.getData() instanceof UltraSrtFcstItems)
        {
            weatherData.setUltraSrtFcstItems((UltraSrtFcstItems) result.getData());

        } else if (result.getData() instanceof VilageFcstItems)
        {
            weatherData.setVilageFcstItems((VilageFcstItems) result.getData());

        } else if (result.getData() instanceof MidLandFcstItems)
        {
            weatherData.setMidLandFcstItems((MidLandFcstItems) result.getData());

        } else if (result.getData() instanceof MidTaItems)
        {
            weatherData.setMidTaItems((MidTaItems) result.getData());
        }

        if (++currentResponseCount == TOTAL_REQUEST)
        {
            weatherData.setFinalData();
            onSuccessful(weatherData);
            currentResponseCount = 0;
        }
    }

    public void onSuccessful(WeatherData weatherData)
    {

    }

    public void onFailure(Exception exception)
    {

    }


    public void getWeatherData(VilageFcstParameter vilageFcstParameter, MidFcstParameter midLandFcstParameter, MidFcstParameter midTaFcstParameter, WeatherAreaCodeDTO weatherAreaCode)
    {
        //시간 업데이트
        currentResponseCount = 0;
        downloadedCalendar.setTimeInMillis(System.currentTimeMillis());
        weatherData = new WeatherData(downloadedCalendar, weatherAreaCode);

        Call call1 = getUltraSrtNcstData(vilageFcstParameter.deepCopy());
        Call call2 = getUltraSrtFcstData(vilageFcstParameter.deepCopy());
        Call call3 = getVilageFcstData(vilageFcstParameter.deepCopy());
        Call call4 = getMidLandFcstData(midLandFcstParameter.deepCopy());
        Call call5 = getMidTaData(midTaFcstParameter.deepCopy());

        List<Call> calls = Arrays.asList(call1, call2, call3, call4, call5);

        // 10초 타이머 동작
        TimerTask timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                for (Call call : calls)
                {
                    if (call.isExecuted())
                    {
                        // 작업 취소
                        call.cancel();
                        onFailure(null);
                        break;
                    }
                }
            }
        };
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
                onSuccessful(new DataWrapper<WeatherItems>(data.getResponse().getBody().getItems()));
            }

            @Override
            protected void handleError(Response<UltraSrtNcstRoot> response)
            {
                WeatherDownloader.this.onFailure(null);
            }

            @Override
            protected void handleFailure(Exception e)
            {
                WeatherDownloader.this.onFailure(e);
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
                onSuccessful(new DataWrapper<WeatherItems>(data.getResponse().getBody().getItems()));
            }

            @Override
            protected void handleError(Response<UltraSrtFcstRoot> response)
            {
                WeatherDownloader.this.onFailure(null);
            }

            @Override
            protected void handleFailure(Exception e)
            {
                WeatherDownloader.this.onFailure(e);
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
                onSuccessful(new DataWrapper<WeatherItems>(data.getResponse().getBody().getItems()));
            }

            @Override
            protected void handleError(Response<VilageFcstRoot> response)
            {
                WeatherDownloader.this.onFailure(null);
            }

            @Override
            protected void handleFailure(Exception e)
            {
                WeatherDownloader.this.onFailure(e);
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
                onSuccessful(new DataWrapper<WeatherItems>(data.getResponse().getBody().getItems()));
            }

            @Override
            protected void handleError(Response<MidLandFcstRoot> response)
            {
                WeatherDownloader.this.onFailure(null);
            }

            @Override
            protected void handleFailure(Exception e)
            {
                WeatherDownloader.this.onFailure(e);
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
                onSuccessful(new DataWrapper<WeatherItems>(data.getResponse().getBody().getItems()));
            }

            @Override
            protected void handleError(Response<MidTaRoot> response)
            {
                WeatherDownloader.this.onFailure(null);
            }

            @Override
            protected void handleFailure(Exception e)
            {
                WeatherDownloader.this.onFailure(e);
            }
        });
        return call;
    }


}
