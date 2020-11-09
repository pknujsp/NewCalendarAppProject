package com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments;

import android.content.Context;
import android.graphics.Canvas;
import android.icu.util.ChineseCalendar;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.views.UltraSrtFcstView;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.views.UltraSrtNcstView;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.views.VilageFcstView;
import com.zerodsoft.scheduleweather.utility.Clock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class WeatherItemView extends LinearLayout
{
    private WeatherData weatherData;

    private UltraSrtNcstView ultraSrtNcstView;
    private UltraSrtFcstView ultraSrtFcstView;
    private VilageFcstView vilageFcstView;
    private MidFcstView midFcstView;
    private WeatherAreaCodeDTO weatherAreaCode;

    private List<SunSetRiseData> sunSetRiseList = new ArrayList<>();

    public WeatherItemView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);

        ultraSrtNcstView = new UltraSrtNcstView(context);
        ultraSrtFcstView = new UltraSrtFcstView(context);

        addView(ultraSrtNcstView, 0);
        addView(ultraSrtFcstView, 1);
        setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        for (int i = 0, count = getChildCount(); i < count; i++)
        {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE)
            {
                //Measurement Subview
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
            }
        }
        //In onMeasure, this method must be called to set the final measurement width and height.
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3)
    {
        int lastTop = 0;
        for (int index = 0, count = getChildCount(); index < count; index++)
        {
            final View child = getChildAt(index);
            if (child.getVisibility() != GONE)
            {
                child.layout(0, lastTop, child.getMeasuredWidth(), lastTop + child.getMeasuredHeight());
                lastTop += child.getMeasuredHeight();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas)
    {
        super.dispatchDraw(canvas);
    }

    private void init()
    {
        //동네예보 마지막 날 까지의 일몰/일출 시간 데이터를 구성
        sunSetRiseList.clear();

        List<Calendar> dates = new ArrayList<>();

        Calendar endDate = weatherData.getDownloadedDate();
        endDate.add(Calendar.DAY_OF_YEAR, 2);

        Calendar calendar = weatherData.getDownloadedDate();

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

        SunriseSunsetCalculator sunriseSunsetCalculator = new SunriseSunsetCalculator(new Location(weatherData.getWeatherAreaCode().getLatitudeSecondsDivide100(),
                weatherData.getWeatherAreaCode().getLongitudeSecondsDivide100()), Clock.TIME_ZONE);
        Calendar sunRise = null;
        Calendar sunSet = null;

        for (Calendar date : dates)
        {
            sunRise = sunriseSunsetCalculator.getOfficialSunriseCalendarForDate(date);
            sunSet = sunriseSunsetCalculator.getOfficialSunsetCalendarForDate(date);
            sunSetRiseList.add(new SunSetRiseData(date.getTime(), sunRise.getTime(), sunSet.getTime()));
        }
    }

    public void setWeatherData(WeatherData weatherData)
    {
        this.weatherData = weatherData;
        init();
        ultraSrtNcstView.setWeatherData(weatherData, sunSetRiseList.get(0));
        ultraSrtFcstView.setWeatherData(weatherData, sunSetRiseList);
        invalidate();
    }
}
