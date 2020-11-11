package com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.SunSetRiseData;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.WeatherData;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.responseresult.UltraSrtFcstData;
import com.zerodsoft.scheduleweather.utility.Clock;
import com.zerodsoft.scheduleweather.utility.WeatherDataConverter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UltraSrtFcstFragment extends Fragment
{
    private WeatherData weatherData;
    private List<SunSetRiseData> sunSetRiseDataList;
    private TableLayout table;

    public UltraSrtFcstFragment()
    {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.ultra_srt_fcst_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        table = (TableLayout) view.findViewById(R.id.ultra_srt_fcst_table);
    }

    public void setWeatherData(WeatherData weatherData, List<SunSetRiseData> sunSetRiseDataList)
    {
        this.weatherData = weatherData;
        this.sunSetRiseDataList = sunSetRiseDataList;
        setScrollView();
    }

    private void setScrollView()
    {
        final int DP48 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f, getResources().getDisplayMetrics());
        final int DP32 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32f, getResources().getDisplayMetrics());
        final List<UltraSrtFcstData> dataList = weatherData.getUltraSrtFcstFinalData().getData();
        final int dataSize = dataList.size();
        final int viewWidth = dataSize * DP48;
        //시각, 하늘, 기온, 바람, 습도 순으로 행 등록
        Context context = getContext();

        // 시각 --------------------------------------------------------------------------
        TableRow clockRow = new TableRow(context);
        TextView clockLabel = new TextView(context);

        clockLabel.setTextColor(Color.GRAY);
        clockLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
        clockLabel.setGravity(Gravity.CENTER);
        clockLabel.setText(getString(R.string.clock));
        clockRow.addView(clockLabel, new TableRow.LayoutParams(DP48, ViewGroup.LayoutParams.WRAP_CONTENT));

        for (int col = 0; col < dataSize; col++)
        {
            TextView textView = new TextView(context);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
            textView.setGravity(Gravity.CENTER);
            textView.setText(Clock.WEATHER_TIME_FORMAT.format(dataList.get(col).getDateTime()));
            clockRow.addView(textView, new TableRow.LayoutParams(DP48, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        table.addView(clockRow, new TableLayout.LayoutParams(viewWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

        //하늘 ---------------------------------------------------------------------------
        TableRow skyRow = new TableRow(context);
        TextView skyLabel = new TextView(context);

        skyLabel.setTextColor(Color.GRAY);
        skyLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
        skyLabel.setGravity(Gravity.CENTER);
        skyLabel.setText(getString(R.string.sky));
        skyRow.addView(skyLabel, new TableRow.LayoutParams(DP48, ViewGroup.LayoutParams.MATCH_PARENT));

        for (int col = 0; col < dataSize; col++)
        {
            ImageView imageView = new ImageView(context);
            imageView.setImageDrawable(getSkyImage(dataList.get(col)));
            skyRow.addView(imageView, new TableRow.LayoutParams(DP32, DP32));
        }

        table.addView(skyRow, new TableLayout.LayoutParams(viewWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

        //기온 ------------------------------------------------------------------------------
        TableRow tempRow = new TableRow(context);
        TextView tempLabel = new TextView(context);

        tempLabel.setTextColor(Color.GRAY);
        tempLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
        tempLabel.setGravity(Gravity.CENTER);
        tempLabel.setText(getString(R.string.temperature));
        tempRow.addView(tempLabel, new TableRow.LayoutParams(DP48, ViewGroup.LayoutParams.WRAP_CONTENT));

        for (int col = 0; col < dataSize; col++)
        {
            TextView textView = new TextView(context);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
            textView.setGravity(Gravity.CENTER);
            textView.setText(dataList.get(col).getTemperature());
            tempRow.addView(textView, new TableRow.LayoutParams(DP48, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        table.addView(tempRow, new TableLayout.LayoutParams(viewWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

        //바람 ------------------------------------------------------------------------------
        TableRow windRow = new TableRow(context);
        TextView windLabel = new TextView(context);

        windLabel.setTextColor(Color.GRAY);
        windLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
        windLabel.setGravity(Gravity.CENTER);
        windLabel.setText(getString(R.string.wind));
        windRow.addView(windLabel, new TableRow.LayoutParams(DP48, ViewGroup.LayoutParams.MATCH_PARENT));

        for (int col = 0; col < dataSize; col++)
        {
            TextView textView = new TextView(context);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
            textView.setGravity(Gravity.CENTER);
            textView.setText(dataList.get(col).getWindSpeed() + "m/s, " + dataList.get(col).getWindDirection());
            windRow.addView(textView, new TableRow.LayoutParams(DP48, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        table.addView(windRow, new TableLayout.LayoutParams(viewWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

        //습도 ------------------------------------------------------------------------------
        TableRow humidityRow = new TableRow(context);
        TextView humidityLabel = new TextView(context);

        humidityLabel.setTextColor(Color.GRAY);
        humidityLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
        humidityLabel.setGravity(Gravity.CENTER);
        humidityLabel.setText(getString(R.string.humidity));
        humidityRow.addView(humidityLabel, new TableRow.LayoutParams(DP48, ViewGroup.LayoutParams.WRAP_CONTENT));

        for (int col = 0; col < dataSize; col++)
        {
            TextView textView = new TextView(context);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
            textView.setGravity(Gravity.CENTER);
            textView.setText(dataList.get(col).getHumidity());
            humidityRow.addView(textView, new TableRow.LayoutParams(DP48, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        table.addView(humidityRow, new TableLayout.LayoutParams(viewWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
        table.invalidate();
    }

    private Drawable getSkyImage(UltraSrtFcstData data)
    {
        Calendar sunSetRiseCalendar = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(data.getDateTime());

        Drawable drawable = null;

        for (SunSetRiseData sunSetRiseData : sunSetRiseDataList)
        {
            sunSetRiseCalendar.setTime(sunSetRiseData.getDate());
            if (sunSetRiseCalendar.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR) &&
                    sunSetRiseCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR))
            {
                boolean day = calendar.after(sunSetRiseData.getSunrise()) && calendar.before(sunSetRiseData.getSunset());
                drawable = getContext().getDrawable(WeatherDataConverter.getSkyDrawableId(data.getSky(), data.getPrecipitationForm(), day));
            }
        }
        return drawable;
    }
}