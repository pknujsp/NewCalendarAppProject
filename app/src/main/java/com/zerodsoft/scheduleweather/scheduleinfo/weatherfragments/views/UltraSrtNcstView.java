package com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.View;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import com.zerodsoft.scheduleweather.AppMainActivity;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.SunSetRiseData;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.WeatherData;
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

    private PointF areaNamePoint;
    private PointF skyDrawablePoint;
    private PointF tempPoint;
    private PointF skyPoint;
    private PointF humidityLabelPoint;
    private PointF windLabelPoint;
    private PointF humidityPoint;
    private PointF windPoint;

    private final int AREA_NAME_TEXT_HEIGHT;
    private final int TEMP_TEXT_HEIGHT;
    private final int SKY_TEXT_HEIGHT;
    private final int LABEL_TEXT_HEIGHT;
    private final int HUMIDITY_WIND_TEXT_HEIGHT;

    private final int TEMP_TEXT_WIDTH;
    private final int SKY_TEXT_WIDTH;
    private final int LABEL_TEXT_WIDTH;
    private final int HUMIDITY_WIND_TEXT_WIDTH;

    private UltraSrtNcstData ultraSrtNcstData;
    private SunSetRiseData sunSetRiseData;
    private WeatherData weatherData;

    public UltraSrtNcstView(Context context, WeatherData weatherData, SunSetRiseData sunSetRiseData)
    {
        super(context);
        Rect rect = new Rect();

        AREA_NAME_PAINT = new TextPaint();
        AREA_NAME_PAINT.setColor(Color.BLACK);
        AREA_NAME_PAINT.setTextAlign(Paint.Align.CENTER);
        AREA_NAME_PAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 19f, context.getResources().getDisplayMetrics()));
        AREA_NAME_PAINT.getTextBounds("1", 0, 1, rect);
        AREA_NAME_TEXT_HEIGHT = rect.height();

        TEMP_PAINT = new TextPaint();
        TEMP_PAINT.setColor(Color.BLACK);
        TEMP_PAINT.setTextAlign(Paint.Align.LEFT);
        TEMP_PAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14f, context.getResources().getDisplayMetrics()));
        String testValue = "22.0 C";
        TEMP_PAINT.getTextBounds(testValue, 0, testValue.length(), rect);
        TEMP_TEXT_HEIGHT = rect.height();
        TEMP_TEXT_WIDTH = rect.width();

        SKY_PAINT = new TextPaint();
        SKY_PAINT.setColor(Color.BLACK);
        SKY_PAINT.setTextAlign(Paint.Align.LEFT);
        SKY_PAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14f, context.getResources().getDisplayMetrics()));
        testValue = "맑음";
        SKY_PAINT.getTextBounds(testValue, 0, testValue.length(), rect);
        SKY_TEXT_HEIGHT = rect.height();
        SKY_TEXT_WIDTH = rect.width();

        LABEL_PAINT = new TextPaint();
        LABEL_PAINT.setColor(Color.GRAY);
        LABEL_PAINT.setTextAlign(Paint.Align.CENTER);
        LABEL_PAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 13f, context.getResources().getDisplayMetrics()));
        testValue = "습도";
        LABEL_PAINT.getTextBounds(testValue, 0, testValue.length(), rect);
        LABEL_TEXT_HEIGHT = rect.height();
        LABEL_TEXT_WIDTH = rect.width();

        HUMIDITY_WIND_PAINT = new TextPaint();
        HUMIDITY_WIND_PAINT.setColor(Color.BLACK);
        HUMIDITY_WIND_PAINT.setTextAlign(Paint.Align.CENTER);
        HUMIDITY_WIND_PAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14f, context.getResources().getDisplayMetrics()));
        testValue = "70%";
        HUMIDITY_WIND_PAINT.getTextBounds(testValue, 0, testValue.length(), rect);
        HUMIDITY_WIND_TEXT_HEIGHT = rect.height();
        HUMIDITY_WIND_TEXT_WIDTH = rect.width();

        MARGIN = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, context.getResources().getDisplayMetrics());

        setBackgroundColor(Color.WHITE);

        this.weatherData = weatherData;
        this.ultraSrtNcstData = weatherData.getUltraSrtNcstFinalData().getData();
        this.sunSetRiseData = sunSetRiseData;

        init();
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

        final int width = getWidth();
        final int height = getHeight();

        // 지역명, 이미지, 습도|바람 레이블과 값 텍스트뷰의 높이로 뷰의 높이를 지정한다
        int skyImgWidth = width / 8;
        int skyImgHeight = skyImgWidth;
        int x = width / 8;
        int y = (int) (AREA_NAME_TEXT_HEIGHT + MARGIN * 2.8f);

        skyDrawable.setBounds(x, y, x + skyImgWidth, y + skyImgHeight);

        Rect skyDrawableRect = skyDrawable.getBounds();

        areaNamePoint = new PointF(width / 2, MARGIN - AREA_NAME_PAINT.ascent());
        skyDrawablePoint = new PointF(skyDrawableRect.left, skyDrawableRect.top);
        tempPoint = new PointF(skyDrawablePoint.x + skyDrawableRect.width() + MARGIN * 0.5f, skyDrawablePoint.y + 8);
        skyPoint = new PointF(skyDrawablePoint.x + skyDrawableRect.width() + MARGIN * 0.5f, tempPoint.y + TEMP_TEXT_HEIGHT * 1.4f);
        humidityLabelPoint = new PointF(tempPoint.x + TEMP_TEXT_WIDTH * 2, skyDrawablePoint.y - 8);
        windLabelPoint = new PointF(humidityLabelPoint.x + LABEL_TEXT_WIDTH * 3, skyDrawablePoint.y - 8);
        humidityPoint = new PointF(tempPoint.x, humidityLabelPoint.y + LABEL_TEXT_HEIGHT * 1.4f);
        windPoint = new PointF(humidityLabelPoint.x, humidityLabelPoint.y + LABEL_TEXT_HEIGHT * 1.4f);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        //지역명
        canvas.drawText(weatherData.getAreaName(), areaNamePoint.x, areaNamePoint.y, AREA_NAME_PAINT);
        //구름이미지
        skyDrawable.draw(canvas);
        //기온
        canvas.drawText(ultraSrtNcstData.getTemperature() + "ºC", tempPoint.x, tempPoint.y, TEMP_PAINT);
        //하늘상태
        canvas.drawText(ultraSrtNcstData.getPrecipitationForm(), skyPoint.x, skyPoint.y, SKY_PAINT);
        //습도, 바람 레이블
        canvas.drawText("습도", humidityLabelPoint.x, humidityLabelPoint.y, LABEL_PAINT);
        canvas.drawText("바람", windLabelPoint.x, windLabelPoint.y, LABEL_PAINT);
        //습도, 바람 값
        canvas.drawText(ultraSrtNcstData.getHumidity(), humidityPoint.x, humidityPoint.y, HUMIDITY_WIND_PAINT);
        canvas.drawText(ultraSrtNcstData.getWindSpeed() + "m/s, " + ultraSrtNcstData.getWindDirection() + "\n"
                + WeatherDataConverter.getWindSpeedDescription(ultraSrtNcstData.getWindSpeed()), humidityPoint.x, humidityPoint.y, HUMIDITY_WIND_PAINT);
    }

    private void init()
    {
        //SKY IMG설정
        boolean day = sunSetRiseData.getDate().after(sunSetRiseData.getSunset()) ? false : sunSetRiseData.getDate().before(sunSetRiseData.getSunrise()) ? false : true;
        skyDrawable = getContext().getDrawable(WeatherDataConverter.getSkyDrawableId(weatherData.getUltraSrtFcstFinalData().getData().get(0).getSky(), ultraSrtNcstData.getPrecipitationForm(), day));
    }
}