package com.zerodsoft.scheduleweather.weather.ultrasrtncst;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.databinding.UltraSrtNcstFragmentBinding;
import com.zerodsoft.scheduleweather.retrofit.paremeters.UltraSrtNcstParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.WeatherItems;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtncstresponse.UltraSrtNcstRoot;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.weather.repository.WeatherDataDownloader;
import com.zerodsoft.scheduleweather.utility.WeatherDataConverter;

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
    private UltraSrtNcstFragmentBinding binding;
    private UltraSrtNcst ultraSrtNcst = new UltraSrtNcst();
    private WeatherAreaCodeDTO weatherAreaCode;
    private Date date;
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

    public UltraSrtNcstFragment(WeatherAreaCodeDTO weatherAreaCodeDTO, Date date)
    {
        this.weatherAreaCode = weatherAreaCodeDTO;
        this.date = date;
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
        getWeatherData();
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
        calendar.setTime(date);
        weatherDataDownloader.getUltraSrtNcstData(ultraSrtNcstParameter, calendar, new JsonDownloader<UltraSrtNcstRoot>()
        {
            @Override
            public void onResponseSuccessful(UltraSrtNcstRoot result)
            {
                setWeatherData(result, date);
            }

            @Override
            public void onResponseFailed(Exception e)
            {

            }
        });
    }

    public void setWeatherData(UltraSrtNcstRoot ultraSrtNcstRoot, Date downloadedDate)
    {
        ultraSrtNcst.setUltraSrtNcstFinalData(ultraSrtNcstRoot.getResponse().getBody().getItems(), downloadedDate);
        requireActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
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