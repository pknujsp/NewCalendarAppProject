package com.zerodsoft.scheduleweather.CalendarView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
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
    private Paint weekTextBoxRectPaint;
    private Rect weekTextBoxRect;
    private int textBoxWidth;
    private int textBoxHeight;
    private String week = "12주";
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

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(widthMeasureSpec / 8, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        drawWeekDatesView(canvas);
    }

    private void drawWeekDatesView(Canvas canvas)
    {
        if (week != null)
        {
            float x = getWidth() / 2;
            float y = getHeight() / 2;
            canvas.drawRect(0, 0, getWidth(), getHeight(), weekBackgroundPaint);
            //    canvas.drawRect(x - textViewWidth / 2, y + textViewHeight / 2, x + textViewWidth / 2, y - textViewHeight / 2, weekTextBackgroundPaint);
            //   canvas.drawRect(weekTextBoxRect, weekTextBackgroundPaint);
            canvas.drawRect(x - textBoxWidth / 2 - 1, y + textBoxHeight / 2 - 1, x + textBoxWidth / 2 + 1, y - textBoxHeight / 2 + 1, weekTextBoxRectPaint);
            canvas.drawText(week, x, y + weekTextBoxRect.height() / 2, weekTextBoxPaint);
        }
    }

    @Override
    public void updateWeekDates(String week)
    {
        this.week = week;
        WeekDatesView.this.invalidate();
    }
}
