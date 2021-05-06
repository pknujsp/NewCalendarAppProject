package com.zerodsoft.scheduleweather.weather.ultrasrtncst;

import android.os.Bundle;
import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.databinding.UltraSrtNcstFragmentBinding;
import com.zerodsoft.scheduleweather.retrofit.paremeters.UltraSrtNcstParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.WeatherItems;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtfcstresponse.UltraSrtFcstRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtncstresponse.UltraSrtNcstRoot;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.room.dto.WeatherDataDTO;
import com.zerodsoft.scheduleweather.weather.interfaces.OnDownloadedTimeListener;
import com.zerodsoft.scheduleweather.weather.repository.WeatherDataDownloader;
import com.zerodsoft.scheduleweather.utility.WeatherDataConverter;
import com.zerodsoft.scheduleweather.weather.viewmodel.WeatherDbViewModel;

import java.util.Calendar;
import java.util.Date;


public class UltraSrtNcstFragment extends Fragment
{
    /*
    - 초단기 실황 -
    기온
    1시간 강수량
    동서바람성분(미 표시)
    남북바람성분(미 표시)
    습도
    강수형태
    풍향
    풍속
     */
    private final OnDownloadedTimeListener onDownloadedTimeListener;

    private UltraSrtNcstFragmentBinding binding;
    private UltraSrtNcst ultraSrtNcst = new UltraSrtNcst();
    private WeatherAreaCodeDTO weatherAreaCode;
    private WeatherDbViewModel weatherDbViewModel;

    private final WeatherDataDownloader weatherDataDownloader = new WeatherDataDownloader()
    {
        @Override
        public void onResponseSuccessful(WeatherItems result)
        {

        }

        @Override
        public void onResponseFailed(Exception e)
        {

        }
    };

    public UltraSrtNcstFragment(WeatherAreaCodeDTO weatherAreaCodeDTO, OnDownloadedTimeListener onDownloadedTimeListener)
    {
        this.weatherAreaCode = weatherAreaCodeDTO;
        this.onDownloadedTimeListener = onDownloadedTimeListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = UltraSrtNcstFragmentBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        clearViews();
        weatherDbViewModel = new ViewModelProvider(this).get(WeatherDbViewModel.class);
        weatherDbViewModel.getWeatherData(weatherAreaCode.getY(), weatherAreaCode.getX(), WeatherDataDTO.ULTRA_SRT_NCST, new CarrierMessagingService.ResultCallback<WeatherDataDTO>()
        {
            @Override
            public void onReceiveResult(@NonNull WeatherDataDTO ultraSrtNcstWeatherDataDTO) throws RemoteException
            {
                if (ultraSrtNcstWeatherDataDTO == null)
                {
                    getWeatherData();
                } else
                {
                    Gson gson = new Gson();
                    UltraSrtNcstRoot ultraSrtNcstRoot = gson.fromJson(ultraSrtNcstWeatherDataDTO.getJson(), UltraSrtNcstRoot.class);
                    Date downloadedDate = new Date(Long.parseLong(ultraSrtNcstWeatherDataDTO.getDownloadedDate()));

                    ultraSrtNcst.setUltraSrtNcstFinalData(ultraSrtNcstRoot.getResponse().getBody().getItems(), downloadedDate);
                    requireActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            onDownloadedTimeListener.setDownloadedTime(downloadedDate, WeatherDataDTO.ULTRA_SRT_NCST);
                            setValue();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    private void setValue()
    {
        UltraSrtNcstFinalData ultraSrtNcstFinalData = ultraSrtNcst.getUltraSrtNcstFinalData();
        //기온
        binding.ultraSrtNcstTemp.setText(ultraSrtNcstFinalData.getTemperature() + "ºC");
        //강수형태
        binding.ultraSrtNcstPty.setText(WeatherDataConverter.convertPrecipitationForm(ultraSrtNcstFinalData.getPrecipitationForm()));
        //습도
        binding.ultraSrtNcstHumidity.setText(ultraSrtNcstFinalData.getHumidity());
        //바람
        binding.ultraSrtNcstWind.setText(ultraSrtNcstFinalData.getWindSpeed() + "m/s, " + ultraSrtNcstFinalData.getWindDirection() + "\n" +
                WeatherDataConverter.getWindSpeedDescription(ultraSrtNcstFinalData.getWindSpeed()));
        //시간 강수량
        binding.ultraSrtNcstRn1.setText(ultraSrtNcstFinalData.getPrecipitation1Hour());
    }

    public void getWeatherData()
    {
        UltraSrtNcstParameter ultraSrtNcstParameter = new UltraSrtNcstParameter();
        ultraSrtNcstParameter.setNx(weatherAreaCode.getX()).setNy(weatherAreaCode.getY()).setNumOfRows("250").setPageNo("1");

        Calendar calendar = Calendar.getInstance();
        weatherDataDownloader.getUltraSrtNcstData(ultraSrtNcstParameter, calendar, new JsonDownloader<JsonObject>()
        {
            @Override
            public void onResponseSuccessful(JsonObject result)
            {
                setWeatherData(result, calendar.getTime());
            }

            @Override
            public void onResponseFailed(Exception e)
            {

            }
        });
    }

    public void setWeatherData(JsonObject result, Date downloadedDate)
    {
        Gson gson = new Gson();
        UltraSrtNcstRoot ultraSrtNcstRoot = gson.fromJson(result.toString(), UltraSrtNcstRoot.class);

        WeatherDataDTO ultraSrtNcstWeatherDataDTO = new WeatherDataDTO();
        ultraSrtNcstWeatherDataDTO.setLatitude(weatherAreaCode.getY());
        ultraSrtNcstWeatherDataDTO.setLongitude(weatherAreaCode.getX());
        ultraSrtNcstWeatherDataDTO.setDataType(WeatherDataDTO.ULTRA_SRT_NCST);
        ultraSrtNcstWeatherDataDTO.setJson(result.toString());
        ultraSrtNcstWeatherDataDTO.setDownloadedDate(String.valueOf(System.currentTimeMillis()));

        weatherDbViewModel.insert(ultraSrtNcstWeatherDataDTO, new CarrierMessagingService.ResultCallback<WeatherDataDTO>()
        {
            @Override
            public void onReceiveResult(@NonNull WeatherDataDTO weatherDataDTO) throws RemoteException
            {

            }
        });


        ultraSrtNcst.setUltraSrtNcstFinalData(ultraSrtNcstRoot.getResponse().getBody().getItems(), downloadedDate);
        requireActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                onDownloadedTimeListener.setDownloadedTime(downloadedDate, WeatherDataDTO.ULTRA_SRT_NCST);
                setValue();
            }
        });
    }

    public void clearViews()
    {
        binding.ultraSrtNcstTemp.setText("");
        binding.ultraSrtNcstPty.setText("");
        binding.ultraSrtNcstHumidity.setText("");
        binding.ultraSrtNcstWind.setText("");
        binding.ultraSrtNcstRn1.setText("");
    }
}