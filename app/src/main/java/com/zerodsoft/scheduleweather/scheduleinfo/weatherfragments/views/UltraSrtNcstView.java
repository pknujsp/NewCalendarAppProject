package com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.View;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.SunSetRiseData;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.responseresult.UltraSrtFcstData;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.responseresult.UltraSrtNcstData;
import com.zerodsoft.scheduleweather.utility.Clock;
import com.zerodsoft.scheduleweather.utility.WeatherDataConverter;

import java.util.Calendar;


public class UltraSrtNcstView extends View
{
    private final TextPaint AREA_NAME_PAINT;
    private final TextPaint TEMP_PAINT;
    private final TextPaint SKY_PAINT;
    private final TextPaint LABEL_PAINT;
    private final TextPaint HUMIDITY_WIND_PAINT;
    private final int MARGIN;
    private Drawable skyDrawable;

    private UltraSrtNcstData ncstData;
    private UltraSrtFcstData fcstData;
    private String areaName;

    private PointF areaNamePoint;
    private PointF skyDrawablePoint;
    private PointF tempPoint;
    private PointF skyPoint;
    private PointF humidityLabelPoint;
    private PointF windLabelPoint;
    private PointF humidityPoint;
    private PointF windPoint;

    private SunSetRiseData sunSetRiseData;

    public UltraSrtNcstView(Context context, UltraSrtNcstData ncstData,
                            UltraSrtFcstData fcstData, SunSetRiseData sunSetRiseData)
    {
        super(context);

        AREA_NAME_PAINT = new TextPaint();
        AREA_NAME_PAINT.setColor(Color.BLACK);
        AREA_NAME_PAINT.setTextAlign(Paint.Align.CENTER);
        AREA_NAME_PAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18f, context.getResources().getDisplayMetrics()));

        TEMP_PAINT = new TextPaint();
        TEMP_PAINT.setColor(Color.BLACK);
        TEMP_PAINT.setTextAlign(Paint.Align.LEFT);
        TEMP_PAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 24f, context.getResources().getDisplayMetrics()));

        SKY_PAINT = new TextPaint();
        SKY_PAINT.setColor(Color.BLACK);
        SKY_PAINT.setTextAlign(Paint.Align.LEFT);
        SKY_PAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16f, context.getResources().getDisplayMetrics()));

        LABEL_PAINT = new TextPaint();
        LABEL_PAINT.setColor(Color.BLACK);
        LABEL_PAINT.setTextAlign(Paint.Align.CENTER);
        LABEL_PAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14f, context.getResources().getDisplayMetrics()));

        HUMIDITY_WIND_PAINT = new TextPaint();
        HUMIDITY_WIND_PAINT.setColor(Color.BLACK);
        HUMIDITY_WIND_PAINT.setTextAlign(Paint.Align.CENTER);
        HUMIDITY_WIND_PAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16f, context.getResources().getDisplayMetrics()));

        MARGIN = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32f, context.getResources().getDisplayMetrics());

        setBackgroundColor(Color.WHITE);

        this.ncstData = ncstData;
        this.fcstData = fcstData;
        this.sunSetRiseData = sunSetRiseData;

        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int height = (int) (MARGIN + AREA_NAME_PAINT.ascent() - AREA_NAME_PAINT.descent() + MARGIN + TEMP_PAINT.ascent()
                - TEMP_PAINT.descent() + MARGIN + SKY_PAINT.ascent()
                - SKY_PAINT.descent() + MARGIN + LABEL_PAINT.ascent() - LABEL_PAINT.descent()
                + MARGIN + HUMIDITY_WIND_PAINT.ascent() - HUMIDITY_WIND_PAINT.descent() + MARGIN);
        setMeasuredDimension(widthMeasureSpec, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);

        areaNamePoint = new PointF(getWidth() / 2, MARGIN - AREA_NAME_PAINT.ascent() + AREA_NAME_PAINT.descent());
        skyDrawablePoint = new PointF(getWidth() / 3 + MARGIN * 2, areaNamePoint.y + MARGIN + skyDrawable.getBounds().height());
        tempPoint = new PointF((getWidth() - skyDrawablePoint.x / 2) + skyDrawablePoint.x, skyDrawablePoint.y);
        skyPoint = new PointF(tempPoint.x, tempPoint.y + MARGIN * 2);
        humidityLabelPoint = new PointF(getWidth() / 3, skyPoint.y + MARGIN * 2);
        windLabelPoint = new PointF(getWidth() - getWidth() / 3, skyPoint.y + MARGIN * 2);
        humidityPoint = new PointF(humidityLabelPoint.x, humidityLabelPoint.y + MARGIN);
        windPoint = new PointF(windLabelPoint.x, windLabelPoint.y + MARGIN);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        //지역명
        canvas.drawText(areaName, areaNamePoint.x, areaNamePoint.y, AREA_NAME_PAINT);
        //구름이미지

        //기온
        canvas.drawText(ncstData.getTemperature() + "ºC", tempPoint.x, tempPoint.y, TEMP_PAINT);
    }

    private void init()
    {
        //SKY IMG설정
        boolean day = sunSetRiseData.getDate().after(sunSetRiseData.getSunset()) ? false : sunSetRiseData.getDate().before(sunSetRiseData.getSunrise()) ? false : true;
        skyDrawable = getContext().getDrawable(WeatherDataConverter.getSkyDrawableId(fcstData.getSky(), ncstData.getPrecipitationForm(), day));
    }
}