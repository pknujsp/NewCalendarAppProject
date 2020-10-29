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

    private List<SunSetRiseData> sunSetRiseList;

    public WeatherItemView(Context context, WeatherData weatherData, WeatherAreaCodeDTO weatherAreaCode)
    {
        super(context);
        this.context = context;
        this.weatherData = weatherData;
        this.weatherAreaCode = weatherAreaCode;

        init();

        ultraSrtNcstView = new UltraSrtNcstView(context, weatherData.getUltraSrtNcstData(), weatherData.getUltraShortFcstDataList().get(0), sunSetRiseList.get(0));
        addView(ultraSrtNcstView);

        setWillNotDraw(false);
    }


    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3)
    {

    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
    }

    private void init()
    {
        sunSetRiseList = new ArrayList<>();

        //동네예보 마지막 날 까지의 일몰/일출 시간 데이터를 구성
        List<Calendar> dates = new ArrayList<>();

        Date firstDate = weatherData.getDownloadedDate().getTime();
        Calendar endDate = Calendar.getInstance(Clock.TIME_ZONE);
        endDate.setTime(weatherData.getVilageFcstDataList().get(weatherData.getVilageFcstDataList().size() - 1).getDateTime());

        Calendar calendar = Calendar.getInstance(Clock.TIME_ZONE);
        calendar.setTime(firstDate);

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
