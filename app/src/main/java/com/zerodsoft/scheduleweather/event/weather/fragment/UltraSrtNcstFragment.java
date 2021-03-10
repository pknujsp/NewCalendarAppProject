package com.zerodsoft.scheduleweather.event.weather.fragment;

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
import com.zerodsoft.scheduleweather.event.weather.SunSetRiseData;
import com.zerodsoft.scheduleweather.event.weather.resultdata.responseresult.UltraSrtNcstData;
import com.zerodsoft.scheduleweather.utility.WeatherDataConverter;


public class UltraSrtNcstFragment extends Fragment
{
    /*
    기온
    1시간 강수량
    동서바람성분(미 표시)
    남북바람성분(미 표시)
    습도
    강수형태
    풍향
    풍속
     */
    private UltraSrtNcstData ultraSrtNcstData;

    private TextView temp;
    private TextView pty;
    private TextView humidity;
    private TextView wind;
    private TextView rn1;

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

        temp = (TextView) view.findViewById(R.id.ultra_srt_ncst_temp);
        pty = (TextView) view.findViewById(R.id.ultra_srt_ncst_pty);
        humidity = (TextView) view.findViewById(R.id.ultra_srt_ncst_humidity);
        wind = (TextView) view.findViewById(R.id.ultra_srt_ncst_wind);
        rn1 = (TextView) view.findViewById(R.id.ultra_srt_ncst_rn1);
    }

    private void setValue()
    {
        //기온
        temp.setText(ultraSrtNcstData.getTemperature());
        //강수형태
        pty.setText(WeatherDataConverter.convertPrecipitationForm(ultraSrtNcstData.getPrecipitationForm()));
        //습도
        humidity.setText(ultraSrtNcstData.getHumidity());
        //바람
        wind.setText(ultraSrtNcstData.getWindSpeed() + "m/s, " + ultraSrtNcstData.getWindDirection() + "\n" +
                WeatherDataConverter.getWindSpeedDescription(ultraSrtNcstData.getWindSpeed()));
        //시간 강수량
        rn1.setText(ultraSrtNcstData.getPrecipitation1Hour());
    }


    public void setWeatherData(UltraSrtNcstData ultraSrtNcstData)
    {
        this.ultraSrtNcstData = ultraSrtNcstData;
        setValue();
    }

    public void clearViews()
    {
        //기온
        temp.setText("");
        //강수형태
        pty.setText("");
        //습도
        humidity.setText("");
        //바람
        wind.setText("");
        //시간 강수량
        rn1.setText("");
    }
}