package com.zerodsoft.scheduleweather.CalendarView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.zerodsoft.scheduleweather.R;

public class WeekDatesView extends View implements WeekHeaderView.OnUpdateWeekDatesListener
{
    private int weekTextColor;
    private int weekTextSize;
    private int weekBackgroundColor;
    private Paint weekBackgroundPaint;
    private Paint weekTextBoxPaint;
    private Rect weekTextBoxRect;
    private int viewHeight = 0;
    private int viewWidth = 0;
    private int headerViewHeight;
    private int headerViewWidth;
    private String week = null;
    private Context mContext;


    public WeekDatesView(Context context)
    {
        super(context, null);
    }

    public WeekDatesView(Context context, @Nullable AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public WeekDatesView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        mContext = context;

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WeekHeaderView, 0, 0);
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

    public WeekDatesView setHeaderViewHeight(int headerViewHeight)
    {
        this.headerViewHeight = headerViewHeight;
        return this;
    }

    public WeekDatesView setHeaderViewWidth(int headerViewWidth)
    {
        this.headerViewWidth = headerViewWidth;
        return this;
    }

    private void init()
    {
        weekBackgroundPaint = new Paint();
        weekBackgroundPaint.setColor(weekBackgroundColor);

        weekTextBoxPaint = new Paint();
        weekTextBoxPaint.setColor(weekTextColor);
        weekTextBoxPaint.setAntiAlias(true);
        weekTextBoxPaint.setTextAlign(Paint.Align.CENTER);
        weekTextBoxPaint.setTextSize(weekTextSize);
        weekTextBoxRect = new Rect();
        weekTextBoxPaint.getTextBounds("22", 0, 2, weekTextBoxRect);

        viewHeight = headerViewHeight;
        viewWidth = headerViewWidth / 8;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(viewWidth, viewHeight);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        drawWeekDatesView(canvas);
    }

    private void drawWeekDatesView(Canvas canvas)
    {

        if (week == null)
        {

        }
    }

    @Override
    public void updateWeekDates(String week)
    {
        Toast.makeText(mContext, week, Toast.LENGTH_SHORT).show();
    }
}
