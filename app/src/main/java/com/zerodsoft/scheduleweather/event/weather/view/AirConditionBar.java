package com.zerodsoft.scheduleweather.event.weather.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.event.weather.view.airconditionbar.BarInitData;
import com.zerodsoft.scheduleweather.event.weather.view.airconditionbar.BarInitDataCreater;

import java.util.List;

import retrofit2.http.PATCH;

public class AirConditionBar extends View
{
    private final List<BarInitData> barInitDataList;
    private final int ARROW_COLOR;
    private final float ARROW_WIDTH;
    private final float PADDING;
    private final int DATA_TYPE;
    private final TextPaint REFERENCE_TEXTPAINT;
    private final TextPaint REFERENCE_STATUS_TEXTPAINT;
    private final Paint BAR_PAINT;
    private final Paint VALUE_ARROW_PAINT;
    private final int REFERENCE_TEXT_HEIGHT;

    private double dataValue;


    public AirConditionBar(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AirConditionBar, 0, 0);
        try
        {
            ARROW_COLOR = a.getInteger(R.styleable.AirConditionBar_arrow_color, 0);
            ARROW_WIDTH = a.getDimension(R.styleable.AirConditionBar_arrow_width, 0f);
            PADDING = a.getDimension(R.styleable.AirConditionBar_padding, 0f);
            DATA_TYPE = a.getInteger(R.styleable.AirConditionBar_data_type, 0);
            barInitDataList = BarInitDataCreater.getBarInitData(context, DATA_TYPE);
        } finally
        {
            a.recycle();
        }

        REFERENCE_TEXTPAINT = new TextPaint();
        REFERENCE_TEXTPAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10f, getResources().getDisplayMetrics()));
        REFERENCE_TEXTPAINT.setColor(Color.GRAY);
        REFERENCE_TEXTPAINT.setTextAlign(Paint.Align.CENTER);

        Rect rect = new Rect();
        REFERENCE_TEXTPAINT.getTextBounds("0", 0, 1, rect);
        REFERENCE_TEXT_HEIGHT = rect.height();

        REFERENCE_STATUS_TEXTPAINT = new TextPaint();
        REFERENCE_STATUS_TEXTPAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10f, getResources().getDisplayMetrics()));
        REFERENCE_STATUS_TEXTPAINT.setColor(Color.WHITE);
        REFERENCE_STATUS_TEXTPAINT.setTextAlign(Paint.Align.CENTER);

        BAR_PAINT = new Paint();

        VALUE_ARROW_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
        VALUE_ARROW_PAINT.setColor(ARROW_COLOR);
    }

    public void setDataValue(double dataValue)
    {
        this.dataValue = dataValue;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left + (int) PADDING, top, right + (int) PADDING, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        final int viewHeight = getHeight();

        final float barWidth = getWidth() - ((PADDING / 3) * 2);
        final float barHeight = viewHeight / 3f;
        final float maxRef = (float) barInitDataList.get(barInitDataList.size() - 1).getReferenceValueEnd();

        final float top = barHeight;
        final float bottom = viewHeight - barHeight;

        double beginRef = 0;
        double endRef = 0;
        float left = 0;
        float right = 0;

        RectF rect = new RectF(left, top, right, bottom);

        for (int i = 0; i < barInitDataList.size(); i++)
        {
            beginRef = barInitDataList.get(i).getReferenceValueBegin();
            endRef = barInitDataList.get(i).getReferenceValueEnd();

            left = (float) (barWidth * (beginRef / maxRef));
            right = (float) (barWidth * (endRef / maxRef));
            rect.left = left;
            rect.right = right;

            BAR_PAINT.setColor(barInitDataList.get(i).getColor());

            //바 그리기
            canvas.drawRect(rect, BAR_PAINT);
            //기준값 표시
            canvas.drawText(String.valueOf(beginRef), left, top - REFERENCE_TEXT_HEIGHT / 2, REFERENCE_TEXTPAINT);
            //상태 표시
            canvas.drawText(barInitDataList.get(i).getStatusName(), rect.centerX(), rect.centerY() + REFERENCE_TEXT_HEIGHT / 2, REFERENCE_STATUS_TEXTPAINT);
        }

        //데이터 값 표시
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, getResources().getDisplayMetrics());

        float arrowLeft = (int) (barWidth * (dataValue / maxRef)) - (ARROW_WIDTH / 2);
        float arrowTop = top - size;
        float arrowRight = arrowLeft + ARROW_WIDTH;
        float arrowBottom = bottom + size;

        canvas.drawRect(arrowLeft, arrowTop, arrowRight, arrowBottom, VALUE_ARROW_PAINT);
    }
}
