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

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.WeatherData;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.views.MidFcstView;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.views.UltraSrtNcstView;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.views.VilageFcstView;

public class WeatherItemView extends ViewGroup
{
    private WeatherData weatherData;
    private Context context;

    private UltraSrtNcstView ultraSrtNcstView;
    private VilageFcstView vilageFcstView;
    private MidFcstView midFcstView;

    public WeatherItemView(Context context, WeatherData weatherData)
    {
        super(context);
        this.context = context;
        this.weatherData = weatherData;

        ultraSrtNcstView = new UltraSrtNcstView(context, weatherData.getUltraSrtNcstData(), weatherData.getUltraShortFcstDataList().get(0));
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
}
