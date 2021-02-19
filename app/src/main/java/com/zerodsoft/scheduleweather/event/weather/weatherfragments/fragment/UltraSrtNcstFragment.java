package com.zerodsoft.scheduleweather.event.weather.weatherfragments.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.event.weather.weatherfragments.SunSetRiseData;
import com.zerodsoft.scheduleweather.event.weather.weatherfragments.resultdata.WeatherData;
import com.zerodsoft.scheduleweather.event.weather.weatherfragments.resultdata.responseresult.UltraSrtNcstData;
import com.zerodsoft.scheduleweather.utility.WeatherDataConverter;


public class UltraSrtNcstFragment extends Fragment
{
    private UltraSrtNcstData ultraSrtNcstData;
    private SunSetRiseData sunSetRiseData;
    private WeatherData weatherData;

    private TextView areaName;
    private TextView temp;
    private TextView sky;
    private TextView humidity;
    private TextView wind;
    private ImageView skyImage;
    private Drawable skyDrawable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.ultra_srt_ncst_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        areaName = (TextView) view.findViewById(R.id.ultra_srt_ncst_areaname);
        temp = (TextView) view.findViewById(R.id.ultra_srt_ncst_temp);
        sky = (TextView) view.findViewById(R.id.ultra_srt_ncst_sky);
        humidity = (TextView) view.findViewById(R.id.ultra_srt_ncst_humidity);
        wind = (TextView) view.findViewById(R.id.ultra_srt_ncst_wind);
        skyImage = (ImageView) view.findViewById(R.id.ultra_srt_ncst_skyimage);
    }

    private void setValue()
    {
        //지역명
        areaName.setText(weatherData.getAreaName());
        //하늘 이미지
        skyImage.setImageDrawable(skyDrawable);
        //기온
        temp.setText(ultraSrtNcstData.getTemperature());
        //하늘상태
        sky.setText(WeatherDataConverter.getSky(ultraSrtNcstData.getPrecipitationForm(), weatherData.getUltraSrtFcstFinalData().get(0).getSky()));
        //습도
        humidity.setText(ultraSrtNcstData.getHumidity());
        //바람
        wind.setText(ultraSrtNcstData.getWindSpeed() + "m/s, " + ultraSrtNcstData.getWindDirection() + "\n" +
                WeatherDataConverter.getWindSpeedDescription(ultraSrtNcstData.getWindSpeed()));
    }

    private void init()
    {
        //SKY IMG설정
        boolean day = sunSetRiseData.getDate().after(sunSetRiseData.getSunset()) ? false : sunSetRiseData.getDate().before(sunSetRiseData.getSunrise()) ? false : true;
        skyDrawable = getContext().getDrawable(WeatherDataConverter.getSkyDrawableId(weatherData.getUltraSrtFcstFinalData().get(0).getSky(), ultraSrtNcstData.getPrecipitationForm(), day));
    }

    public void setWeatherData(WeatherData weatherData, SunSetRiseData sunSetRiseData)
    {
        this.weatherData = weatherData;
        this.ultraSrtNcstData = weatherData.getUltraSrtNcstFinalData();
        this.sunSetRiseData = sunSetRiseData;
        init();
        setValue();
    }
}