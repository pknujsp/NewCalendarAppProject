package com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;
import com.zerodsoft.scheduleweather.retrofit.Querys;
import com.zerodsoft.scheduleweather.retrofit.RetrofitCallback;
import com.zerodsoft.scheduleweather.retrofit.paremeters.MidFcstParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.VilageFcstParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.midlandfcstresponse.MidLandFcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.midlandfcstresponse.MidLandFcstItems;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.midlandfcstresponse.MidLandFcstRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.midtaresponse.MidTaItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.midtaresponse.MidTaItems;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.midtaresponse.MidTaRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtfcstresponse.UltraSrtFcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtfcstresponse.UltraSrtFcstItems;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtfcstresponse.UltraSrtFcstRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtncstresponse.UltraSrtNcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtncstresponse.UltraSrtNcstItems;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtncstresponse.UltraSrtNcstRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.vilagefcstresponse.VilageFcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.vilagefcstresponse.VilageFcstItems;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.vilagefcstresponse.VilageFcstRoot;
import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dao.WeatherAreaCodeDAO;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.WeatherData;
import com.zerodsoft.scheduleweather.utility.Clock;
import com.zerodsoft.scheduleweather.utility.LonLat;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherRepository
{
    /*
    서버로 부터 응답이 15초 이상 없는 경우 업데이트 취소 후 업데이트 실패 안내 메시지 표시
     */
    private MutableLiveData<List<WeatherData>> weatherDataLiveData = new MutableLiveData<>();
    private LiveData<List<WeatherAreaCodeDTO>> areaCodeLiveData;

    private WeatherAreaCodeDAO weatherAreaCodeDAO;
    private final Calendar downloadedCalendar;

    private static final String ULTRA_SRT_NCST = "ULTRA_SRT_NCST";
    private static final String ULTRA_SRT_FCST = "ULTRA_SRT_FCST";
    private static final String VILAGE_FCST = "VILAGE_FCST";
    private static final String MID_LAND_FCST = "MID_LAND_FCST";
    private static final String MID_TA_FCST = "MID_TA_FCST";

    private List<WeatherData> weatherDataList = new ArrayList<>();

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler()
    {
        private int currentDownloadCount = 0;

        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();

            int index = bundle.getInt("INDEX");
            String type = bundle.getString("TYPE");
            switch (type)
            {
                case ULTRA_SRT_NCST: // nx,ny가 일치하는 인덱스에 데이터 삽입
                    weatherDataList.get(index).setUltraSrtNcstItemsDataWrapper((DataWrapper<UltraSrtNcstItems>) msg.obj);
                    break;

                case ULTRA_SRT_FCST:
                    weatherDataList.get(index).setUltraSrtFcstItemsDataWrapper((DataWrapper<UltraSrtFcstItems>) msg.obj);
                    break;

                case VILAGE_FCST:
                    weatherDataList.get(index).setVilageFcstItemsDataWrapper((DataWrapper<VilageFcstItems>) msg.obj);
                    break;

                case MID_LAND_FCST:
                    weatherDataList.get(index).setMidLandFcstItemsDataWrapper((DataWrapper<MidLandFcstItems>) msg.obj);
                    break;

                case MID_TA_FCST:
                    weatherDataList.get(index).setMidTaItemsDataWrapper((DataWrapper<MidTaItems>) msg.obj);
                    break;
            }

            if (++currentDownloadCount == (index + 1) * 5)
            {
                for (WeatherData weatherData : weatherDataList)
                {
                    weatherData.setFinalData();
                }
                weatherDataLiveData.setValue(weatherDataList);
                currentDownloadCount = 0;
            }

        }
    };

    public WeatherRepository(Context context)
    {
        weatherAreaCodeDAO = AppDb.getInstance(context).weatherAreaCodeDAO();
        downloadedCalendar = Calendar.getInstance(Clock.TIME_ZONE);
    }

    public void selectAreaCode(LonLat lonLat)
    {
        areaCodeLiveData = weatherAreaCodeDAO.selectAreaCode(Integer.toString(lonLat.getLatitude_degree()),
                Integer.toString(lonLat.getLatitude_minutes()), Integer.toString(lonLat.getLongitude_degree()),
                Integer.toString(lonLat.getLongitude_minutes()));
    }

    public void getAllWeathersData(VilageFcstParameter vilageFcstParameter, MidFcstParameter midLandFcstParameter, MidFcstParameter midTaFcstParameter, WeatherAreaCodeDTO weatherAreaCode)
    {
        //시간 업데이트
        downloadedCalendar.setTimeInMillis(System.currentTimeMillis());
        weatherDataList.add(new WeatherData(weatherDataList.size(), downloadedCalendar, weatherAreaCode));

        int index = weatherDataList.get(weatherDataList.size() - 1).getIndex();

        getUltraSrtNcstData(vilageFcstParameter.deepCopy(), index);
        getUltraSrtFcstData(vilageFcstParameter.deepCopy(), index);
        getVilageFcstData(vilageFcstParameter.deepCopy(), index);
        getMidLandFcstData(midLandFcstParameter.deepCopy(), index);
        getMidTaData(midTaFcstParameter.deepCopy(), index);
    }

    public LiveData<List<WeatherAreaCodeDTO>> getAreaCodeLiveData()
    {
        return areaCodeLiveData;
    }

    public void getUltraSrtNcstData(VilageFcstParameter parameter, int index)
    {
        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.VILAGE_FCST);
        //basetime설정
        Calendar calendar = (Calendar) downloadedCalendar.clone();

        if (calendar.get(Calendar.MINUTE) < 40)
        {
            calendar.add(Calendar.HOUR_OF_DAY, -1);
        }
        parameter.setBaseDate(Clock.yyyyMMdd_FORMAT.format(calendar.getTime()));
        parameter.setBaseTime(Clock.WEATHER_TIME_FORMAT.format(calendar.getTime()) + "00");

        Call<UltraSrtNcstRoot> call = querys.getUltraSrtNcstData(parameter.getMap());

        call.enqueue(new RetrofitCallback<UltraSrtNcstRoot>()
        {
            @Override
            protected void handleResponse(UltraSrtNcstRoot data)
            {
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("TYPE", ULTRA_SRT_NCST);
                bundle.putInt("INDEX", index);
                message.setData(bundle);
                message.obj = new DataWrapper<UltraSrtNcstItems>(data.getResponse().getBody().getItems());
                handler.sendMessage(message);
            }

            @Override
            protected void handleError(Response<UltraSrtNcstRoot> response)
            {

            }

            @Override
            protected void handleFailure(Exception e)
            {
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("TYPE", ULTRA_SRT_NCST);
                bundle.putInt("INDEX", index);
                message.setData(bundle);
                message.obj = new DataWrapper<UltraSrtNcstItems>(e);
                handler.sendMessage(message);
            }
        });
    }

    public void getUltraSrtFcstData(VilageFcstParameter parameter, int index)
    {
        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.VILAGE_FCST);
        //basetime설정
        Calendar calendar = (Calendar) downloadedCalendar.clone();

        if (calendar.get(Calendar.MINUTE) < 45)
        {
            calendar.add(Calendar.HOUR_OF_DAY, -1);
        }
        parameter.setBaseDate(Clock.yyyyMMdd_FORMAT.format(calendar.getTime()));
        parameter.setBaseTime(Clock.WEATHER_TIME_FORMAT.format(calendar.getTime()) + "30");

        Call<UltraSrtFcstRoot> call = querys.getUltraSrtFcstData(parameter.getMap());

        call.enqueue(new RetrofitCallback<UltraSrtFcstRoot>()
        {
            @Override
            protected void handleResponse(UltraSrtFcstRoot data)
            {
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("TYPE", ULTRA_SRT_FCST);
                bundle.putInt("INDEX", index);
                message.setData(bundle);
                message.obj = new DataWrapper<UltraSrtFcstItems>(data.getResponse().getBody().getItems());
                handler.sendMessage(message);
            }

            @Override
            protected void handleError(Response<UltraSrtFcstRoot> response)
            {

            }

            @Override
            protected void handleFailure(Exception e)
            {
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("TYPE", ULTRA_SRT_FCST);
                bundle.putInt("INDEX", index);
                message.setData(bundle);
                message.obj = new DataWrapper<UltraSrtFcstItems>(e);
                handler.sendMessage(message);
            }
        });
    }

    public void getVilageFcstData(VilageFcstParameter parameter, int index)
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

        parameter.setBaseDate(Clock.yyyyMMdd_FORMAT.format(calendar.getTime()));
        parameter.setBaseTime(Clock.WEATHER_TIME_FORMAT.format(calendar.getTime()) + "00");

        Call<VilageFcstRoot> call = querys.getVilageFcstData(parameter.getMap());

        call.enqueue(new RetrofitCallback<VilageFcstRoot>()
        {
            @Override
            protected void handleResponse(VilageFcstRoot data)
            {
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("TYPE", VILAGE_FCST);
                bundle.putInt("INDEX", index);
                message.setData(bundle);
                message.obj = new DataWrapper<VilageFcstItems>(data.getResponse().getBody().getItems());
                handler.sendMessage(message);
            }

            @Override
            protected void handleError(Response<VilageFcstRoot> response)
            {

            }

            @Override
            protected void handleFailure(Exception e)
            {
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("TYPE", VILAGE_FCST);
                bundle.putInt("INDEX", index);
                message.setData(bundle);
                message.obj = new DataWrapper<VilageFcstItems>(e);
                handler.sendMessage(message);
            }
        });
    }

    public void getMidLandFcstData(MidFcstParameter parameter, int index)
    {
        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.MID_FCST);

        Calendar calendar = (Calendar) downloadedCalendar.clone();
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

        call.enqueue(new RetrofitCallback<MidLandFcstRoot>()
        {
            @Override
            protected void handleResponse(MidLandFcstRoot data)
            {
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("TYPE", MID_LAND_FCST);
                bundle.putInt("INDEX", index);
                message.setData(bundle);
                message.obj = new DataWrapper<MidLandFcstItems>(data.getResponse().getBody().getItems());
                handler.sendMessage(message);
            }

            @Override
            protected void handleError(Response<MidLandFcstRoot> response)
            {

            }

            @Override
            protected void handleFailure(Exception e)
            {
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("TYPE", MID_LAND_FCST);
                bundle.putInt("INDEX", index);
                message.setData(bundle);
                message.obj = new DataWrapper<MidLandFcstItems>(e);
                handler.sendMessage(message);
            }
        });
    }

    public void getMidTaData(MidFcstParameter parameter, int index)
    {
        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.MID_FCST);

        Calendar calendar = (Calendar) downloadedCalendar.clone();
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

        call.enqueue(new RetrofitCallback<MidTaRoot>()
        {
            @Override
            protected void handleResponse(MidTaRoot data)
            {
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("TYPE", MID_TA_FCST);
                bundle.putInt("INDEX", index);
                message.setData(bundle);
                message.obj = new DataWrapper<MidTaItems>(data.getResponse().getBody().getItems());
                handler.sendMessage(message);
            }

            @Override
            protected void handleError(Response<MidTaRoot> response)
            {

            }

            @Override
            protected void handleFailure(Exception e)
            {
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("TYPE", MID_TA_FCST);
                bundle.putInt("INDEX", index);
                message.setData(bundle);
                message.obj = new DataWrapper<MidTaItems>(e);
                handler.sendMessage(message);
            }
        });
    }


    public MutableLiveData<List<WeatherData>> getWeatherDataLiveData()
    {
        return weatherDataLiveData;
    }
}
