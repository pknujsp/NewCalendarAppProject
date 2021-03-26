package com.zerodsoft.scheduleweather.event.weather.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.event.weather.SunSetRiseData;
import com.zerodsoft.scheduleweather.event.weather.resultdata.responseresult.UltraSrtFcstData;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.utility.WeatherDataConverter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UltraSrtFcstFragment extends Fragment
{
    private List<SunSetRiseData> sunSetRiseDataList;
    private List<UltraSrtFcstData> dataList;
    private TableLayout table;
    private LinearLayout headerCol;

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
        headerCol = (LinearLayout) view.findViewById(R.id.ultra_srt_fcst_header_col);
    }

    public void setWeatherData(List<UltraSrtFcstData> dataList, List<SunSetRiseData> sunSetRiseDataList)
    {
        this.dataList = dataList;
        this.sunSetRiseDataList = sunSetRiseDataList;
        setTable();
    }

    public void clearViews()
    {
        headerCol.removeAllViews();
        table.removeAllViews();
    }

    private void setTable()
    {
        final int ITEM_WIDTH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45f, getResources().getDisplayMetrics());
        final int MARGIN = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, getResources().getDisplayMetrics());
        final int DP22 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22f, getResources().getDisplayMetrics());
        final int DP34 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 34f, getResources().getDisplayMetrics());

        final int DATA_SIZE = dataList.size();
        final int VIEW_WIDTH = DATA_SIZE * ITEM_WIDTH;

        clearViews();

        //시각, 하늘, 기온, 강수량, 강수확률, 바람, 습도 순으로 행 등록
        Context context = getContext();

        TableRow clockRow = new TableRow(context);
        TableRow skyRow = new TableRow(context);
        TableRow tempRow = new TableRow(context);
        TableRow windRow = new TableRow(context);
        TableRow humidityRow = new TableRow(context);

        //label column 설정
        TextView clockLabel = new TextView(context);
        TextView skyLabel = new TextView(context);
        TextView tempLabel = new TextView(context);
        TextView windLabel = new TextView(context);
        TextView humidityLabel = new TextView(context);

        setLabelTextView(clockLabel, getString(R.string.clock));
        setLabelTextView(skyLabel, getString(R.string.sky));
        setLabelTextView(tempLabel, getString(R.string.temperature));
        setLabelTextView(windLabel, getString(R.string.wind));
        setLabelTextView(humidityLabel, getString(R.string.humidity));

        LinearLayout.LayoutParams clockLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP22);
        clockLabelParams.topMargin = MARGIN;
        clockLabelParams.bottomMargin = MARGIN;
        LinearLayout.LayoutParams skyLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP22);
        skyLabelParams.topMargin = MARGIN;
        skyLabelParams.bottomMargin = MARGIN;
        LinearLayout.LayoutParams tempLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP34);
        tempLabelParams.topMargin = MARGIN;
        tempLabelParams.bottomMargin = MARGIN;
        LinearLayout.LayoutParams windLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP34);
        windLabelParams.topMargin = MARGIN;
        windLabelParams.bottomMargin = MARGIN;
        LinearLayout.LayoutParams humidityLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP34);
        humidityLabelParams.topMargin = MARGIN;
        humidityLabelParams.bottomMargin = MARGIN;

        clockLabelParams.gravity = Gravity.CENTER;
        skyLabelParams.gravity = Gravity.CENTER;
        tempLabelParams.gravity = Gravity.CENTER;
        windLabelParams.gravity = Gravity.CENTER;
        humidityLabelParams.gravity = Gravity.CENTER;

        headerCol.addView(clockLabel, clockLabelParams);
        headerCol.addView(skyLabel, skyLabelParams);
        headerCol.addView(tempLabel, tempLabelParams);
        headerCol.addView(windLabel, windLabelParams);
        headerCol.addView(humidityLabel, humidityLabelParams);

        //시각 --------------------------------------------------------------------------
        for (int col = 0; col < DATA_SIZE; col++)
        {
            TextView textView = new TextView(context);
            setValueTextView(textView, ClockUtil.H.format(dataList.get(col).getDateTime()));

            TableRow.LayoutParams textParams = new TableRow.LayoutParams(ITEM_WIDTH, DP22);
            textParams.gravity = Gravity.CENTER;
            clockRow.addView(textView, textParams);
        }

        //하늘 ---------------------------------------------------------------------------
        for (int col = 0; col < DATA_SIZE; col++)
        {
            ImageView sky = new ImageView(context);
            sky.setScaleType(ImageView.ScaleType.FIT_CENTER);

            sky.setImageDrawable(getSkyImage(dataList.get(col)));

            TableRow.LayoutParams params = new TableRow.LayoutParams(ITEM_WIDTH, DP22);
            params.gravity = Gravity.CENTER;
            skyRow.addView(sky, params);
        }

        //기온 ------------------------------------------------------------------------------
        for (int col = 0; col < DATA_SIZE; col++)
        {
            TextView textView = new TextView(context);
            setValueTextView(textView, dataList.get(col).getTemperature());

            TableRow.LayoutParams textParams = new TableRow.LayoutParams(ITEM_WIDTH, DP34);
            textParams.gravity = Gravity.CENTER;
            tempRow.addView(textView, textParams);
        }


        //바람 ------------------------------------------------------------------------------
        for (int col = 0; col < DATA_SIZE; col++)
        {
            TextView textView = new TextView(context);
            setValueTextView(textView, dataList.get(col).getWindSpeed() + "\n" + dataList.get(col).getWindDirection());

            TableRow.LayoutParams textParams = new TableRow.LayoutParams(ITEM_WIDTH, DP34);
            textParams.gravity = Gravity.CENTER;
            windRow.addView(textView, textParams);
        }

        //습도 ------------------------------------------------------------------------------
        for (int col = 0; col < DATA_SIZE; col++)
        {
            TextView textView = new TextView(context);
            setValueTextView(textView, dataList.get(col).getHumidity());

            TableRow.LayoutParams textParams = new TableRow.LayoutParams(ITEM_WIDTH, DP34);
            textParams.gravity = Gravity.CENTER;
            humidityRow.addView(textView, textParams);
        }

        TableLayout.LayoutParams clockRowParams = new TableLayout.LayoutParams(VIEW_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);
        clockRowParams.topMargin = MARGIN;
        clockRowParams.bottomMargin = MARGIN;
        TableLayout.LayoutParams skyRowParams = new TableLayout.LayoutParams(VIEW_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);
        skyRowParams.topMargin = MARGIN;
        skyRowParams.bottomMargin = MARGIN;
        TableLayout.LayoutParams tempRowParams = new TableLayout.LayoutParams(VIEW_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);
        tempRowParams.topMargin = MARGIN;
        tempRowParams.bottomMargin = MARGIN;
        TableLayout.LayoutParams windRowParams = new TableLayout.LayoutParams(VIEW_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);
        windRowParams.topMargin = MARGIN;
        windRowParams.bottomMargin = MARGIN;
        TableLayout.LayoutParams humidityRowParams = new TableLayout.LayoutParams(VIEW_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);
        humidityRowParams.topMargin = MARGIN;
        humidityRowParams.bottomMargin = MARGIN;

        table.addView(clockRow, clockRowParams);
        table.addView(skyRow, skyRowParams);
        table.addView(tempRow, tempRowParams);
        table.addView(windRow, windRowParams);
        table.addView(humidityRow, humidityRowParams);
    }

    private void setLabelTextView(TextView textView, String labelText)
    {
        textView.setTextColor(Color.GRAY);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        textView.setGravity(Gravity.CENTER);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setText(labelText);
    }

    private void setValueTextView(TextView textView, String value)
    {
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        textView.setGravity(Gravity.CENTER);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setText(value);
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
                Date calendarDate = calendar.getTime();
                boolean day = calendarDate.after(sunSetRiseData.getSunrise()) && calendarDate.before(sunSetRiseData.getSunset()) ? true : false;
                drawable = getContext().getDrawable(WeatherDataConverter.getSkyDrawableId(data.getSky(), data.getPrecipitationForm(), day));
            }
        }
        return drawable;
    }
}