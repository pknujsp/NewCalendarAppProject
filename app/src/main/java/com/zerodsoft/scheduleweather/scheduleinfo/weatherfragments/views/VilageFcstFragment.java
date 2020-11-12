package com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
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
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.responseresult.VilageFcstData;
import com.zerodsoft.scheduleweather.utility.Clock;
import com.zerodsoft.scheduleweather.utility.WeatherDataConverter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class VilageFcstFragment extends Fragment
{
    private WeatherData weatherData;
    private List<SunSetRiseData> sunSetRiseDataList;
    private TableLayout table;
    private LinearLayout headerCol;

    public VilageFcstFragment()
    {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.vilage_fcst_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        table = (TableLayout) view.findViewById(R.id.vilage_fcst_table);
        headerCol = (LinearLayout) view.findViewById(R.id.vilage_fcst_header_col);
    }

    public void setWeatherData(WeatherData weatherData, List<SunSetRiseData> sunSetRiseDataList)
    {
        this.weatherData = weatherData;
        this.sunSetRiseDataList = sunSetRiseDataList;
        setTable();
    }


    private void setTable()
    {
        Context context = getContext();

        final int ITEM_WIDTH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f, getResources().getDisplayMetrics());
        final int DP48 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f, getResources().getDisplayMetrics());
        final int DP32 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32f, getResources().getDisplayMetrics());

        List<VilageFcstData> dataList = weatherData.getVilageFcstFinalData().getData();
        int dataSize = dataList.size();
        final int viewWidth = dataSize * ITEM_WIDTH;

        //시각, 하늘, 기온, 강수량, 강수확률, 바람, 습도 순으로 행 등록
        TableRow clockRow = new TableRow(context);
        TableRow skyRow = new TableRow(context);
        TableRow tempRow = new TableRow(context);
        TableRow rainfallRow = new TableRow(context);
        TableRow chanceOfShowerRow = new TableRow(context);
        TableRow windRow = new TableRow(context);
        TableRow humidityRow = new TableRow(context);

        //라벨 열 설정
        TextView clockLabel = new TextView(context);

        clockLabel.setTextColor(Color.GRAY);
        clockLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        clockLabel.setGravity(Gravity.CENTER);
        clockLabel.setText(getString(R.string.clock));

        LinearLayout.LayoutParams clockLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP48);
        clockLabelParams.gravity = Gravity.CENTER;
        headerCol.addView(clockLabel, clockLabelParams);

        TextView skyLabel = new TextView(context);

        skyLabel.setTextColor(Color.GRAY);
        skyLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        skyLabel.setGravity(Gravity.CENTER);
        skyLabel.setText(getString(R.string.sky));

        LinearLayout.LayoutParams skyLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP48);
        skyLabelParams.gravity = Gravity.CENTER;
        headerCol.addView(skyLabel, skyLabelParams);

        TextView tempLabel = new TextView(context);

        tempLabel.setTextColor(Color.GRAY);
        tempLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        tempLabel.setGravity(Gravity.CENTER);
        tempLabel.setText(getString(R.string.temperature));

        LinearLayout.LayoutParams tempLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP32);
        tempLabelParams.gravity = Gravity.CENTER;
        headerCol.addView(tempLabel, tempLabelParams);

        TextView rainfallLabel = new TextView(context);

        rainfallLabel.setTextColor(Color.GRAY);
        rainfallLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        rainfallLabel.setGravity(Gravity.CENTER);
        rainfallLabel.setText(getString(R.string.rainfall));

        LinearLayout.LayoutParams rainfallLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP32);
        rainfallLabelParams.gravity = Gravity.CENTER;
        headerCol.addView(rainfallLabel, rainfallLabelParams);

        TextView chanceOfShowerLabel = new TextView(context);

        chanceOfShowerLabel.setTextColor(Color.GRAY);
        chanceOfShowerLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        chanceOfShowerLabel.setGravity(Gravity.CENTER);
        chanceOfShowerLabel.setText(getString(R.string.chance_of_shower));

        LinearLayout.LayoutParams chanceOfShowerLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP32);
        chanceOfShowerLabelParams.gravity = Gravity.CENTER;
        headerCol.addView(chanceOfShowerLabel, chanceOfShowerLabelParams);

        TextView windLabel = new TextView(context);

        windLabel.setTextColor(Color.GRAY);
        windLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        windLabel.setGravity(Gravity.CENTER);
        windLabel.setText(getString(R.string.wind));

        LinearLayout.LayoutParams windLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP48);
        windLabelParams.gravity = Gravity.CENTER;
        headerCol.addView(windLabel, windLabelParams);

        TextView humidityLabel = new TextView(context);

        humidityLabel.setTextColor(Color.GRAY);
        humidityLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        humidityLabel.setGravity(Gravity.CENTER);
        humidityLabel.setText(getString(R.string.humidity));

        LinearLayout.LayoutParams humidityLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP32);
        humidityLabelParams.gravity = Gravity.CENTER;
        headerCol.addView(humidityLabel, humidityLabelParams);

        //시각 --------------------------------------------------------------------------
        String[] clockStrs = null;

        for (int col = 0; col < dataSize; col++)
        {
            TextView textView = new TextView(context);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            textView.setGravity(Gravity.CENTER);
            clockStrs = Clock.MdHH_FORMAT.format(dataList.get(col).getDateTime()).split(" ");
            textView.setText(clockStrs[0] + "\n" + clockStrs[1]);

            TableRow.LayoutParams textParams = new TableRow.LayoutParams(DP48, DP32);
            textParams.gravity = Gravity.CENTER;
            clockRow.addView(textView, textParams);
        }
        TableLayout.LayoutParams clockRowParams = new TableLayout.LayoutParams(viewWidth, DP48);
        clockRowParams.gravity = Gravity.CENTER;
        table.addView(clockRow, clockRowParams);

        //하늘 ---------------------------------------------------------------------------
        for (int col = 0; col < dataSize; col++)
        {
            ImageView imageView = new ImageView(context);
            imageView.setImageDrawable(getSkyImage(dataList.get(col)));
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(DP48, DP32);
            layoutParams.gravity = Gravity.CENTER;
            skyRow.addView(imageView, layoutParams);
        }
        TableLayout.LayoutParams skyRowParams = new TableLayout.LayoutParams(viewWidth, DP48);
        skyRowParams.gravity = Gravity.CENTER;
        table.addView(skyRow, skyRowParams);

        //기온 ------------------------------------------------------------------------------
        for (int col = 0; col < dataSize; col++)
        {
            TextView textView = new TextView(context);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            textView.setGravity(Gravity.CENTER);
            textView.setText(dataList.get(col).getTemp3Hour());

            TableRow.LayoutParams textParams = new TableRow.LayoutParams(DP48, DP32);
            textParams.gravity = Gravity.CENTER;
            tempRow.addView(textView, textParams);
        }
        TableLayout.LayoutParams tempRowParams = new TableLayout.LayoutParams(viewWidth, DP32);
        tempRowParams.gravity = Gravity.CENTER;
        table.addView(tempRow, tempRowParams);

        //강수량 ------------------------------------------------------------------------------
        for (int col = 0; col < dataSize; col++)
        {
            TextView textView = new TextView(context);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            textView.setGravity(Gravity.CENTER);
            textView.setText(dataList.get(col).getRainPrecipitation6Hour());

            TableRow.LayoutParams textParams = new TableRow.LayoutParams(DP48, DP32);
            textParams.gravity = Gravity.CENTER;
            rainfallRow.addView(textView, textParams);
        }
        TableLayout.LayoutParams rainfallRowParams = new TableLayout.LayoutParams(viewWidth, DP32);
        rainfallRowParams.gravity = Gravity.CENTER;
        table.addView(rainfallRow, rainfallRowParams);

        //강수확률 ------------------------------------------------------------------------------
        for (int col = 0; col < dataSize; col++)
        {
            TextView textView = new TextView(context);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            textView.setGravity(Gravity.CENTER);
            textView.setText(dataList.get(col).getChanceOfShower());

            TableRow.LayoutParams textParams = new TableRow.LayoutParams(DP48, DP32);
            textParams.gravity = Gravity.CENTER;
            chanceOfShowerRow.addView(textView, textParams);
        }
        TableLayout.LayoutParams chanceOfShowerRowParams = new TableLayout.LayoutParams(viewWidth, DP32);
        chanceOfShowerRowParams.gravity = Gravity.CENTER;
        table.addView(chanceOfShowerRow, chanceOfShowerRowParams);

        //바람 ------------------------------------------------------------------------------
        for (int col = 0; col < dataSize; col++)
        {
            TextView textView = new TextView(context);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            textView.setGravity(Gravity.CENTER);
            textView.setText(dataList.get(col).getWindSpeed() + "m/s, " + dataList.get(col).getWindDirection());

            TableRow.LayoutParams textParams = new TableRow.LayoutParams(DP48, DP32);
            textParams.gravity = Gravity.CENTER;
            windRow.addView(textView, textParams);
        }
        TableLayout.LayoutParams windRowParams = new TableLayout.LayoutParams(viewWidth, DP48);
        windRowParams.gravity = Gravity.CENTER;
        table.addView(windRow, windRowParams);

        //습도 ------------------------------------------------------------------------------
        for (int col = 0; col < dataSize; col++)
        {
            TextView textView = new TextView(context);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            textView.setGravity(Gravity.CENTER);
            textView.setText(dataList.get(col).getHumidity());

            TableRow.LayoutParams textParams = new TableRow.LayoutParams(DP48, DP32);
            textParams.gravity = Gravity.CENTER;
            humidityRow.addView(textView, textParams);
        }
        TableLayout.LayoutParams humidityRowParams = new TableLayout.LayoutParams(viewWidth, DP32);
        humidityRowParams.gravity = Gravity.CENTER;
        table.addView(humidityRow, humidityRowParams);
    }

    private Drawable getSkyImage(VilageFcstData data)
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
