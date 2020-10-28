package com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;

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
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.WeatherData;
import com.zerodsoft.scheduleweather.utility.Clock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherRepository
{
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

            switch (bundle.getString("TYPE"))
            {
                case ULTRA_SRT_NCST: // nx,ny가 일치하는 인덱스에 데이터 삽입
                    List<UltraSrtNcstItem> ultraSrtNcstItems = bundle.getParcelableArrayList(ULTRA_SRT_NCST);
                    weatherDataList.get(index).setUltraSrtNcstData(ultraSrtNcstItems);
                    break;

                case ULTRA_SRT_FCST:
                    List<UltraSrtFcstItem> ultraSrtFcstItems = bundle.getParcelableArrayList(ULTRA_SRT_FCST);
                    weatherDataList.get(index).setUltraShortFcstDataList(ultraSrtFcstItems);
                    break;

                case VILAGE_FCST:
                    List<VilageFcstItem> vilageFcstItems = bundle.getParcelableArrayList(VILAGE_FCST);
                    weatherDataList.get(index).setVilageFcstDataList(vilageFcstItems);
                    break;

                case MID_LAND_FCST:
                    List<MidLandFcstItem> midLandFcstItems = bundle.getParcelableArrayList(MID_LAND_FCST);
                    weatherDataList.get(index).setMidLandFcstData(midLandFcstItems.get(0));
                    break;

                case MID_TA_FCST:
                    List<MidTaItem> midTaItems = bundle.getParcelableArrayList(MID_TA_FCST);
                    weatherDataList.get(index).setMidTaFcstData(midTaItems.get(0));
                    break;
            }

            if (++currentDownloadCount == (index + 1) * 5)
            {
                for (WeatherData weatherData : weatherDataList)
                {
                    weatherData.setMidFcstDataList();
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

    public void selectAreaCode(int x, int y)
    {
        areaCodeLiveData = weatherAreaCodeDAO.selectAreaCode(Integer.toString(x), Integer.toString(y));
    }

    public void getAllWeathersData(VilageFcstParameter vilageFcstParameter, MidFcstParameter midLandFcstParameter, MidFcstParameter midTaFcstParameter, WeatherAreaCodeDTO weatherAreaCode)
    {
        weatherDataList.add(new WeatherData(weatherDataList.size(), weatherAreaCode.getPhase1() + " " + weatherAreaCode.getPhase2() + " " + weatherAreaCode.getPhase3(),
                weatherAreaCode.getX(), weatherAreaCode.getY(), weatherAreaCode.getMidLandFcstCode(), weatherAreaCode.getMidTaCode(), (Calendar) downloadedCalendar.clone()));

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

        call.enqueue(new Callback<UltraSrtNcstRoot>()
        {
            @Override
            public void onResponse(Call<UltraSrtNcstRoot> call, Response<UltraSrtNcstRoot> response)
            {
                List<UltraSrtNcstItem> items = response.body().getResponse().getBody().getItems().getItem();
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(ULTRA_SRT_NCST, (ArrayList<? extends Parcelable>) items);
                bundle.putString("TYPE", ULTRA_SRT_NCST);
                bundle.putInt("INDEX", index);
                message.setData(bundle);
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(Call<UltraSrtNcstRoot> call, Throwable t)
            {

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

        call.enqueue(new Callback<UltraSrtFcstRoot>()
        {
            @Override
            public void onResponse(Call<UltraSrtFcstRoot> call, Response<UltraSrtFcstRoot> response)
            {
                List<UltraSrtFcstItem> items = response.body().getResponse().getBody().getItems().getItem();
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(ULTRA_SRT_FCST, (ArrayList<? extends Parcelable>) items);
                bundle.putString("TYPE", ULTRA_SRT_FCST);
                bundle.putInt("INDEX", index);
                message.setData(bundle);
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(Call<UltraSrtFcstRoot> call, Throwable t)
            {

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

        call.enqueue(new Callback<VilageFcstRoot>()
        {
            @Override
            public void onResponse(Call<VilageFcstRoot> call, Response<VilageFcstRoot> response)
            {
                List<VilageFcstItem> items = response.body().getResponse().getBody().getItems().getItem();
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(VILAGE_FCST, (ArrayList<? extends Parcelable>) items);
                bundle.putString("TYPE", VILAGE_FCST);
                bundle.putInt("INDEX", index);
                message.setData(bundle);
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(Call<VilageFcstRoot> call, Throwable t)
            {

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

        call.enqueue(new Callback<MidLandFcstRoot>()
        {
            @Override
            public void onResponse(Call<MidLandFcstRoot> call, Response<MidLandFcstRoot> response)
            {
                List<MidLandFcstItem> items = response.body().getResponse().getBody().getItems().getItem();
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(MID_LAND_FCST, (ArrayList<? extends Parcelable>) items);
                bundle.putString("TYPE", MID_LAND_FCST);
                bundle.putInt("INDEX", index);
                message.setData(bundle);
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(Call<MidLandFcstRoot> call, Throwable t)
            {

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

        call.enqueue(new Callback<MidTaRoot>()
        {
            @Override
            public void onResponse(Call<MidTaRoot> call, Response<MidTaRoot> response)
            {
                List<MidTaItem> items = response.body().getResponse().getBody().getItems().getItem();
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(MID_TA_FCST, (ArrayList<? extends Parcelable>) items);
                bundle.putString("TYPE", MID_TA_FCST);
                bundle.putInt("INDEX", index);
                message.setData(bundle);
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(Call<MidTaRoot> call, Throwable t)
            {

            }
        });
    }


    public MutableLiveData<List<WeatherData>> getWeatherDataLiveData()
    {
        return weatherDataLiveData;
    }
}
