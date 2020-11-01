package com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.WeatherData;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.views.MidFcstView;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.views.UltraSrtNcstView;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.views.VilageFcstView;
import com.zerodsoft.scheduleweather.utility.Clock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class WeatherItemView extends ViewGroup
{
    private WeatherData weatherData;
    private Context context;

    private UltraSrtNcstView ultraSrtNcstView;
    private VilageFcstView vilageFcstView;
    private MidFcstView midFcstView;
    private WeatherAreaCodeDTO weatherAreaCode;

    private List<SunSetRiseData> sunSetRiseList = new ArrayList<>();

    public WeatherItemView(Context context, WeatherData weatherData)
    {
        super(context);
        this.context = context;
        this.weatherData = weatherData;

        init();

        ultraSrtNcstView = new UltraSrtNcstView(context, weatherData, sunSetRiseList.get(0));
        addView(ultraSrtNcstView);

        setWillNotDraw(false);
    }


    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3)
    {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
    }

    private void init()
    {
        //동네예보 마지막 날 까지의 일몰/일출 시간 데이터를 구성
        sunSetRiseList.clear();

        List<Calendar> dates = new ArrayList<>();

        Date firstDate = weatherData.getDownloadedDate().getTime();
        Calendar endDate = (Calendar) weatherData.getDownloadedDate().clone();
        endDate.add(2, Calendar.DAY_OF_YEAR);

        Calendar calendar = (Calendar) weatherData.getDownloadedDate().clone();

        int i = 0;
        boolean finished = false;

        while (!finished)
        {
            if (calendar.get(Calendar.YEAR) == endDate.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == endDate.get(Calendar.DAY_OF_YEAR))
            {
                finished = true;
            }
            dates.add((Calendar) calendar.clone());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        SunriseSunsetCalculator sunriseSunsetCalculator = new SunriseSunsetCalculator(new Location(weatherAreaCode.getLatitudeSecondsDivide100(), weatherAreaCode.getLongitudeSecondsDivide100())
                , Clock.TIME_ZONE);
        Calendar sunRise = null;
        Calendar sunSet = null;

        for (Calendar date : dates)
        {
            sunRise = sunriseSunsetCalculator.getOfficialSunriseCalendarForDate(date);
            sunSet = sunriseSunsetCalculator.getOfficialSunsetCalendarForDate(date);
            sunSetRiseList.add(new SunSetRiseData(date.getTime(), sunRise.getTime(), sunSet.getTime()));
        }
    }
}
