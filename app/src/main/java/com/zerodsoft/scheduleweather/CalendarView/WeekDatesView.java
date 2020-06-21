package com.zerodsoft.scheduleweather.CalendarView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.zerodsoft.scheduleweather.R;

public class WeekDatesView extends View implements WeekHeaderView.OnUpdateWeekDatesListener
{
    private int weekTextColor;
    private int weekTextSize;
    private int weekBackgroundColor;
    private Paint weekBackgroundPaint;
    private Paint weekTextBoxPaint;
    private Paint weekTextBoxRectPaint;
    private Paint dividingPaint;
    private Rect weekTextBoxRect;
    private int textBoxWidth;
    private int textBoxHeight;
    private int viewWidth;
    private int viewHeight;
    private String week = "";
    private Context mContext;
    private float x;
    private float y;

    public WeekDatesView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        mContext = context;

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WeekDatesView, 0, 0);
        try
        {
            weekTextColor = a.getColor(R.styleable.WeekDatesView_WeekTextColor, weekTextColor);
            weekBackgroundColor = a.getColor(R.styleable.WeekDatesView_WeekBackgroundColor, weekBackgroundColor);
            weekTextSize = a.getDimensionPixelSize(R.styleable.WeekDatesView_WeekTextSize, weekTextSize);
        } finally
        {
            a.recycle();
        }
        init();
    }


    private void init()
    {
        int headerRowMargin = mContext.getResources().getDimensionPixelSize(R.dimen.week_header_view_day_row_margin);
        int headerDayTextSize = mContext.getResources().getDimensionPixelSize(R.dimen.week_header_view_day_text_size);
        int headerDateTextSize = mContext.getResources().getDimensionPixelSize(R.dimen.week_header_view_date_text_size);


        Rect rect = new Rect();
        Paint headerDatePaint = new Paint();
        headerDatePaint.setTextSize(headerDateTextSize);
        headerDatePaint.setTypeface(Typeface.DEFAULT_BOLD);
        headerDatePaint.getTextBounds("10", 0, 2, rect);
        int headerDateHeight = rect.height();

        Paint headerDayPaint = new Paint();
        headerDayPaint.setTextSize(headerDayTextSize);
        headerDayPaint.setTypeface(Typeface.DEFAULT_BOLD);
        headerDayPaint.getTextBounds("일", 0, "일".length(), rect);
        int headerDayHeight = rect.height();

        int headerHeight = headerDayHeight + headerDateHeight + headerRowMargin * 3;

        weekBackgroundPaint = new Paint();
        weekBackgroundPaint.setColor(weekBackgroundColor);

        weekTextBoxPaint = new Paint();
        weekTextBoxPaint.setColor(weekTextColor);
        weekTextBoxPaint.setAntiAlias(true);
        weekTextBoxPaint.setTextAlign(Paint.Align.CENTER);
        weekTextBoxPaint.setTextSize(weekTextSize);
        weekTextBoxRect = new Rect();
        weekTextBoxPaint.getTextBounds("22주", 0, "22주".length(), weekTextBoxRect);

        textBoxHeight = weekTextBoxRect.height();
        textBoxWidth = weekTextBoxRect.width();

        weekTextBoxRectPaint = new Paint();
        weekTextBoxRectPaint.setColor(Color.GRAY);
        weekTextBoxRectPaint.setAntiAlias(true);

        viewHeight = headerHeight;


        dividingPaint = new Paint();
        dividingPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(widthMeasureSpec, viewHeight);

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
        drawWeekDatesView(canvas);
        canvas.drawLine(0f, getHeight() - 1, getWidth(), getHeight() - 1, dividingPaint);

    }

    private void drawWeekDatesView(Canvas canvas)
    {
        if (week != null)
        {
            x = getWidth() / 2;
            y = getHeight() / 2;
            canvas.drawRect(0, 0, getWidth(), getHeight(), weekBackgroundPaint);
            canvas.drawRect(x - textBoxWidth / 2 - 10, y - textBoxHeight / 2 - 10, x + textBoxWidth / 2 + 10, y + textBoxHeight / 2 + 10, weekTextBoxRectPaint);
            canvas.drawText(week, x, y + weekTextBoxRect.height() / 2, weekTextBoxPaint);
        }
    }

    @Override
    public void updateWeekDates(String week)
    {
        this.week = week;
        invalidate();
    }

}
