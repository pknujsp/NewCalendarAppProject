package com.zerodsoft.scheduleweather.CalendarView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.zerodsoft.scheduleweather.CalendarFragment.WeekFragment;
import com.zerodsoft.scheduleweather.CalendarView.Week.WeekView;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.Utility.DateHour;

public class HoursView extends View implements WeekView.OnRefreshHoursViewListener
{
    private int textColor;
    private int textSize;
    private int spacingLine;
    private float spacingBetweenDay;
    private int backgroundColor;
    private int textHeight;
    private Paint textPaint;
    private Paint backgroundPaint;

    private Context context;


    public HoursView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init()
    {
        textColor = Color.BLACK;
        textSize = context.getResources().getDimensionPixelSize(R.dimen.weekview_textsize);
        backgroundColor = Color.WHITE;
        Rect rect = new Rect();

        textPaint = new Paint();
        textPaint.setTextSize(textSize);
        textPaint.getTextBounds("오전 12", 0, "오전 12".length(), rect);
        spacingLine = rect.height() * 4;
        textHeight = rect.height();

        backgroundPaint = new Paint();
        backgroundPaint.setColor(backgroundColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(WeekFragment.SPACING_BETWEEN_DAY, heightMeasureSpec);
    }

    @Override
    public void layout(int l, int t, int r, int b)
    {
        super.layout(l, t, r, b);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        drawView(canvas);
    }

    private void drawView(Canvas canvas)
    {
        canvas.drawRect(0f, 0f, getWidth(), (float) getHeight(), backgroundPaint);

        for (int i = 0; i < 24; i++)
        {
            // 이번 주 시간
            canvas.drawText(DateHour.getHourString(i), 0f, WeekView.currentCoordinate.y + (spacingLine * i) + textHeight / 2, textPaint);
        }
    }


    @Override
    public void computeScroll()
    {
        super.computeScroll();
    }

    @Override
    public void refreshHoursView()
    {
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void updateLayout()
    {
        requestLayout();
        invalidate();
    }
}
