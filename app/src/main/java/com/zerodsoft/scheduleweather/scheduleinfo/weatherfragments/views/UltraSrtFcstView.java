package com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.SunSetRiseData;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.WeatherData;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.responseresult.UltraSrtFcstData;
import com.zerodsoft.scheduleweather.utility.Clock;
import com.zerodsoft.scheduleweather.utility.WeatherDataConverter;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UltraSrtFcstView extends FrameLayout
{
    private HorizontalScrollView scrollView;
    private UltraSrtFcstItemView itemView;

    private final TextPaint labelTextPaint;
    private final int SP_13;
    private final int DP_24;
    private final int DP_8;
    private final int TEXT_MARGIN_TB;
    private final int TEXT_HEIGHT;
    private final int LABEL_TEXT_WIDTH;
    private final Map<String, String> labelsMap;

    private final Point clockPoint;
    private final Point cloudPoint;
    private final Point temperaturePoint;
    private final Point skyPoint;
    private final Point windPoint;
    private final Point humidityPoint;

    private final int SKY_IMAGE_SIZE;

    private int viewWidth;
    private final int ITEM_WIDTH;
    private final int VIEW_HEIGHT;

    private WeatherData weatherData;
    private List<SunSetRiseData> sunSetRiseDataList;

    public UltraSrtFcstView(Context context)
    {
        super(context);
        setWillNotDraw(false);

        // UltraSrtFcstView하위에 스크롤뷰와 데이터 라벨 뷰로 구성
        SP_13 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 13f, getResources().getDisplayMetrics());
        DP_24 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, getResources().getDisplayMetrics());
        DP_8 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, getResources().getDisplayMetrics());

        labelTextPaint = new TextPaint();
        labelTextPaint.setTextAlign(Paint.Align.CENTER);
        labelTextPaint.setColor(Color.BLACK);
        labelTextPaint.setTextSize(SP_13);

        Rect rect = new Rect();
        labelTextPaint.getTextBounds("시각", 0, 2, rect);
        TEXT_HEIGHT = rect.height();
        LABEL_TEXT_WIDTH = rect.width();

        TEXT_MARGIN_TB = TEXT_HEIGHT / 2;


        labelsMap = new HashMap<>();
        labelsMap.put("clock", getResources().getString(R.string.clock));
        labelsMap.put("cloud", getResources().getString(R.string.cloud));
        labelsMap.put("temperature", getResources().getString(R.string.temperature));
        labelsMap.put("sky", getResources().getString(R.string.sky));
        labelsMap.put("wind", getResources().getString(R.string.wind));
        labelsMap.put("humidity", getResources().getString(R.string.humidity));

        final int textAscent = (int) -labelTextPaint.ascent();
        final int textDescent = (int) labelTextPaint.descent();

        clockPoint = new Point(LABEL_TEXT_WIDTH / 2, TEXT_HEIGHT);
        cloudPoint = new Point(LABEL_TEXT_WIDTH / 2, clockPoint.y + textDescent + TEXT_MARGIN_TB * 2 + textAscent);
        temperaturePoint = new Point(LABEL_TEXT_WIDTH / 2, cloudPoint.y + textDescent + TEXT_MARGIN_TB * 2 + textAscent);
        skyPoint = new Point(LABEL_TEXT_WIDTH / 2, temperaturePoint.y + textDescent + TEXT_MARGIN_TB + textAscent);
        windPoint = new Point(LABEL_TEXT_WIDTH / 2, skyPoint.y + textDescent + TEXT_MARGIN_TB * 2 + textAscent);
        humidityPoint = new Point(LABEL_TEXT_WIDTH / 2, windPoint.y + textDescent + TEXT_MARGIN_TB * 2 + textAscent);

        SKY_IMAGE_SIZE = TEXT_HEIGHT * 2;

        String sampleText = "20.0m/s, 북서풍";
        labelTextPaint.getTextBounds(sampleText, 0, sampleText.length(), rect);

        // 스크롤뷰의 길이,높이 설정
        ITEM_WIDTH = rect.width();
        VIEW_HEIGHT = humidityPoint.y + textDescent + TEXT_MARGIN_TB;

        scrollView = new HorizontalScrollView(context);
        scrollView.setHorizontalScrollBarEnabled(true);

        addView(scrollView, 0, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        itemView = new UltraSrtFcstItemView(context);
        ((HorizontalScrollView) getChildAt(0)).addView(itemView, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(widthMeasureSpec, VIEW_HEIGHT);
        ((HorizontalScrollView) getChildAt(0)).getChildAt(0).measure(viewWidth, VIEW_HEIGHT);
        ((HorizontalScrollView) getChildAt(0)).measure(viewWidth, VIEW_HEIGHT);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        View child = ((HorizontalScrollView) getChildAt(0)).getChildAt(0);

        ((HorizontalScrollView) getChildAt(0)).layout(LABEL_TEXT_WIDTH + DP_8, 0, getWidth(), child.getHeight());
        child.layout(0, 0, child.getWidth(), child.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        //데이터 라벨 표시
        canvas.drawText(labelsMap.get("clock"), clockPoint.x, clockPoint.y, labelTextPaint);
        canvas.drawText(labelsMap.get("cloud"), cloudPoint.x, cloudPoint.y, labelTextPaint);
        canvas.drawText(labelsMap.get("temperature"), temperaturePoint.x, temperaturePoint.y, labelTextPaint);
        canvas.drawText(labelsMap.get("sky"), skyPoint.x, skyPoint.y, labelTextPaint);
        canvas.drawText(labelsMap.get("wind"), windPoint.x, windPoint.y, labelTextPaint);
        canvas.drawText(labelsMap.get("humidity"), humidityPoint.x, humidityPoint.y, labelTextPaint);
    }

    public UltraSrtFcstView setWeatherData(WeatherData weatherData, List<SunSetRiseData> sunSetRiseDataList)
    {
        this.weatherData = weatherData;
        this.sunSetRiseDataList = sunSetRiseDataList;
        viewWidth = ITEM_WIDTH * weatherData.getUltraSrtFcstFinalData().getData().size();
        requestLayout();
        invalidate();
        return this;
    }


    class UltraSrtFcstItemView extends View
    {
        public UltraSrtFcstItemView(Context context)
        {
            super(context);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
        {
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom)
        {
            super.onLayout(changed, left, top, right, bottom);
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            super.onDraw(canvas);
            if (weatherData != null)
            {
                drawTable(canvas);
            }
        }

        private void drawTable(Canvas canvas)
        {
            List<UltraSrtFcstData> list = weatherData.getUltraSrtFcstFinalData().getData();
            Drawable skyImage = null;
            int centerX = ITEM_WIDTH / 2;
            Rect skyImageRect = new Rect(centerX - (SKY_IMAGE_SIZE / 2), cloudPoint.y - (SKY_IMAGE_SIZE / 2),
                    centerX + (SKY_IMAGE_SIZE / 2), cloudPoint.y + (SKY_IMAGE_SIZE / 2));

            int i = 0;
            for (UltraSrtFcstData data : list)
            {
                centerX = ITEM_WIDTH * i + (ITEM_WIDTH / 2);
                //시각
                canvas.drawText(Clock.WEATHER_TIME_FORMAT.format(data.getDateTime()), centerX, clockPoint.y, labelTextPaint);
                //구름
                skyImage = getSkyImage(data);
                skyImageRect.offset(i == 0 ? 0 : ITEM_WIDTH, 0);
                skyImage.setBounds(skyImageRect);
                skyImage.draw(canvas);
                //기온
                canvas.drawText(data.getTemperature(), centerX, temperaturePoint.y, labelTextPaint);
                //하늘
                canvas.drawText(WeatherDataConverter.getSky(data.getPrecipitationForm(), data.getSky()), centerX, skyPoint.y, labelTextPaint);
                //바람
                canvas.drawText(data.getWindSpeed() + "m/s, " + data.getWindDirection(), centerX, windPoint.y, labelTextPaint);
                //습도
                canvas.drawText(data.getHumidity(), centerX, humidityPoint.y, labelTextPaint);

                i++;
            }
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
                    boolean day = calendar.after(sunSetRiseData.getSunset()) ? false : calendar.before(sunSetRiseData.getSunrise()) ? false : true;
                    drawable = getContext().getDrawable(WeatherDataConverter.getSkyDrawableId(data.getSky(), data.getPrecipitationForm(), day));
                }
            }
            return drawable;
        }
    }
}