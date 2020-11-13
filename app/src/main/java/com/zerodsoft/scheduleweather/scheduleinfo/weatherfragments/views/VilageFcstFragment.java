package com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextPaint;
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

import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
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

        final int ITEM_WIDTH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50f, getResources().getDisplayMetrics());
        final int MARGIN = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, getResources().getDisplayMetrics());
        final int DP24 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, getResources().getDisplayMetrics());
        final int DP34 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 34f, getResources().getDisplayMetrics());
        final int DP90 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90f, getResources().getDisplayMetrics());

        List<VilageFcstData> dataList = weatherData.getVilageFcstFinalData().getData();
        final int DATA_SIZE = dataList.size();
        final int VIEW_WIDTH = DATA_SIZE * ITEM_WIDTH;

        //시각, 하늘, 기온, 강수량, 강수확률, 바람, 습도 순으로 행 등록
        TableRow clockRow = new TableRow(context);
        TableRow skyRow = new TableRow(context);
        TempRow tempRow = new TempRow(context);
        TableRow rainfallRow = new TableRow(context);
        TableRow chanceOfShowerRow = new TableRow(context);
        TableRow windRow = new TableRow(context);
        TableRow humidityRow = new TableRow(context);

        //label column 설정
        TextView clockLabel = new TextView(context);
        TextView skyLabel = new TextView(context);
        TextView tempLabel = new TextView(context);
        TextView rainfallLabel = new TextView(context);
        TextView chanceOfShowerLabel = new TextView(context);
        TextView windLabel = new TextView(context);
        TextView humidityLabel = new TextView(context);

        setLabelTextView(clockLabel, getString(R.string.clock));
        setLabelTextView(skyLabel, getString(R.string.sky));
        setLabelTextView(tempLabel, getString(R.string.temperature));
        setLabelTextView(rainfallLabel, getString(R.string.rainfall));
        setLabelTextView(chanceOfShowerLabel, getString(R.string.chance_of_shower));
        setLabelTextView(windLabel, getString(R.string.wind));
        setLabelTextView(humidityLabel, getString(R.string.humidity));

        LinearLayout.LayoutParams clockLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP34);
        clockLabelParams.bottomMargin = MARGIN;
        LinearLayout.LayoutParams skyLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP24);
        skyLabelParams.bottomMargin = MARGIN;
        LinearLayout.LayoutParams tempLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP90);
        tempLabelParams.bottomMargin = MARGIN;
        LinearLayout.LayoutParams rainfallLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP24);
        rainfallLabelParams.bottomMargin = MARGIN;
        LinearLayout.LayoutParams chanceOfShowerLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP24);
        chanceOfShowerLabelParams.bottomMargin = MARGIN;
        LinearLayout.LayoutParams windLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP34);
        windLabelParams.bottomMargin = MARGIN;
        LinearLayout.LayoutParams humidityLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP24);

        clockLabelParams.gravity = Gravity.CENTER;
        skyLabelParams.gravity = Gravity.CENTER;
        tempLabelParams.gravity = Gravity.CENTER;
        rainfallLabelParams.gravity = Gravity.CENTER;
        chanceOfShowerLabelParams.gravity = Gravity.CENTER;
        windLabelParams.gravity = Gravity.CENTER;
        humidityLabelParams.gravity = Gravity.CENTER;

        headerCol.addView(clockLabel, clockLabelParams);
        headerCol.addView(skyLabel, skyLabelParams);
        headerCol.addView(tempLabel, tempLabelParams);
        headerCol.addView(rainfallLabel, rainfallLabelParams);
        headerCol.addView(chanceOfShowerLabel, chanceOfShowerLabelParams);
        headerCol.addView(windLabel, windLabelParams);
        headerCol.addView(humidityLabel, humidityLabelParams);

        //시각 --------------------------------------------------------------------------
        String[] clockStrs = null;

        for (int col = 0; col < DATA_SIZE; col++)
        {
            TextView textView = new TextView(context);
            clockStrs = Clock.MdHH_FORMAT.format(dataList.get(col).getDateTime()).split(" ");
            setValueTextView(textView, clockStrs[0] + "\n" + clockStrs[1]);

            TableRow.LayoutParams textParams = new TableRow.LayoutParams(ITEM_WIDTH, DP34);
            textParams.gravity = Gravity.CENTER;
            clockRow.addView(textView, textParams);
        }

        //하늘 ---------------------------------------------------------------------------
        for (int col = 0; col < DATA_SIZE; col++)
        {
            ImageView imageView = new ImageView(context);
            imageView.setImageDrawable(getSkyImage(dataList.get(col)));
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(ITEM_WIDTH, DP24);
            layoutParams.gravity = Gravity.CENTER;
            skyRow.addView(imageView, layoutParams);
        }

        //기온 ------------------------------------------------------------------------------
        tempRow.setTempList(dataList);
        tempRow.measure(ITEM_WIDTH, DP90);

        //강수량 ------------------------------------------------------------------------------
        for (int col = 0; col < DATA_SIZE; col++)
        {
            TextView textView = new TextView(context);
            setValueTextView(textView, dataList.get(col).getRainPrecipitation6Hour());

            TableRow.LayoutParams textParams = new TableRow.LayoutParams(ITEM_WIDTH, DP24);
            textParams.gravity = Gravity.CENTER;
            rainfallRow.addView(textView, textParams);
        }

        //강수확률 ------------------------------------------------------------------------------
        for (int col = 0; col < DATA_SIZE; col++)
        {
            TextView textView = new TextView(context);
            setValueTextView(textView, dataList.get(col).getChanceOfShower());

            TableRow.LayoutParams textParams = new TableRow.LayoutParams(ITEM_WIDTH, DP24);
            textParams.gravity = Gravity.CENTER;
            chanceOfShowerRow.addView(textView, textParams);
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

            TableRow.LayoutParams textParams = new TableRow.LayoutParams(ITEM_WIDTH, DP24);
            textParams.gravity = Gravity.CENTER;
            humidityRow.addView(textView, textParams);
        }

        TableLayout.LayoutParams clockRowParams = new TableLayout.LayoutParams(VIEW_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);
        clockRowParams.bottomMargin = MARGIN;
        TableLayout.LayoutParams skyRowParams = new TableLayout.LayoutParams(VIEW_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);
        skyRowParams.bottomMargin = MARGIN;
        TableLayout.LayoutParams tempRowParams = new TableLayout.LayoutParams(VIEW_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);
        tempRowParams.bottomMargin = MARGIN;
        TableLayout.LayoutParams rainfallRowParams = new TableLayout.LayoutParams(VIEW_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);
        rainfallRowParams.bottomMargin = MARGIN;
        TableLayout.LayoutParams chanceOfShowerRowParams = new TableLayout.LayoutParams(VIEW_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);
        chanceOfShowerRowParams.bottomMargin = MARGIN;
        TableLayout.LayoutParams windRowParams = new TableLayout.LayoutParams(VIEW_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);
        windRowParams.bottomMargin = MARGIN;
        TableLayout.LayoutParams humidityRowParams = new TableLayout.LayoutParams(VIEW_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);

        table.addView(clockRow, clockRowParams);
        table.addView(skyRow, skyRowParams);
        table.addView(tempRow, tempRowParams);
        table.addView(rainfallRow, rainfallRowParams);
        table.addView(chanceOfShowerRow, chanceOfShowerRowParams);
        table.addView(windRow, windRowParams);
        table.addView(humidityRow, humidityRowParams);

        table.invalidate();
        tempRow.requestLayout();
        tempRow.invalidate();
    }

    private void setLabelTextView(TextView textView, String labelText)
    {
        textView.setTextColor(Color.GRAY);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        textView.setGravity(Gravity.CENTER);
        textView.setText(labelText);
    }

    private void setValueTextView(TextView textView, String value)
    {
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        textView.setGravity(Gravity.CENTER);
        textView.setText(value);
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

    class TempRow extends TableRow
    {
        private List<String> tempList;
        private double maxTemp;
        private double minTemp;
        private final TextPaint TEMP_PAINT;
        private final Paint LINE_PAINT;
        private final Paint CIRCLE_PAINT;

        public TempRow(Context context)
        {
            super(context);
            TEMP_PAINT = new TextPaint();
            TEMP_PAINT.setTextAlign(Paint.Align.CENTER);
            TEMP_PAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, getResources().getDisplayMetrics()));
            TEMP_PAINT.setColor(Color.BLACK);

            LINE_PAINT = new Paint();
            LINE_PAINT.setAntiAlias(true);
            LINE_PAINT.setColor(Color.GRAY);

            CIRCLE_PAINT = new Paint();
            CIRCLE_PAINT.setAntiAlias(true);
            CIRCLE_PAINT.setColor(Color.GRAY);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
        {
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b)
        {
            super.onLayout(changed, l, t, r, b);
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            super.onDraw(canvas);
            drawGraph(canvas);
        }

        private void drawGraph(Canvas canvas)
        {
            final int width = getWidth();
            final int itemWidth = width / tempList.size();
            final int SPACING = (int) ((-width) / ((maxTemp - minTemp) * 10));
            double temp = 0;
            float x = 0f;
            float y = 0f;
            final float RADIUS = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, getResources().getDisplayMetrics());

            int index = 0;
            for (String value : tempList)
            {
                temp = Double.parseDouble(value);
                x = itemWidth / 2 + itemWidth * index;
                y = (float) ((10 * (maxTemp - temp)) * SPACING);
                canvas.drawCircle(x, y, RADIUS, CIRCLE_PAINT);
                canvas.drawText(value, x, y + 20, TEMP_PAINT);
            }
        }

        public void setTempList(List<VilageFcstData> dataList)
        {
            tempList = new LinkedList<>();

            maxTemp = Double.MIN_VALUE;
            minTemp = Double.MAX_VALUE;
            double temp = 0;

            for (VilageFcstData data : dataList)
            {
                temp = Double.parseDouble(data.getTemp3Hour());
                tempList.add(data.getTemp3Hour());

                // 최대,최소 기온 구하기
                if (temp > maxTemp)
                {
                    maxTemp = temp;
                } else if (temp < minTemp)
                {
                    minTemp = temp;
                }
            }
        }
    }
}
