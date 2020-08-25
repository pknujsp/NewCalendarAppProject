package com.zerodsoft.scheduleweather.CalendarView.Week;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.zerodsoft.scheduleweather.CalendarFragment.WeekFragment;
import com.zerodsoft.scheduleweather.CalendarView.AccountType;
import com.zerodsoft.scheduleweather.CalendarView.CalendarType;
import com.zerodsoft.scheduleweather.CalendarView.EventDrawingInfo;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.Utility.AppSettings;

public class HeaderEventView extends View
{
    private int backgroundColor;
    private int textColor;
    private int textSize;

    private Paint backgroundPaint;
    private Paint textPaint;
    private Rect viewRect;
    private Rect textRect;

    private Context context;

    private String subject;
    private int width;
    private int height;
    private int row;
    private int startCol;
    private int endCol;

    private int parentViewWidth;
    private int parentViewHeight;

    private int viewWidth;
    private int viewHeight;


    public HeaderEventView(Context context, EventDrawingInfo eventDrawingInfo, int parentViewWidth, int parentViewHeight)
    {
        super(context);
        this.context = context;
        init(eventDrawingInfo);

        this.parentViewWidth = parentViewWidth;
        this.parentViewHeight = parentViewHeight;

    }

    private void init(EventDrawingInfo eventDrawingInfo)
    {
        switch (eventDrawingInfo.getAccountType())
        {
            case GOOGLE:
                backgroundColor = AppSettings.getGoogleEventBackgroundColor();
                textColor = AppSettings.getGoogleEventTextColor();
                break;
            case LOCAL:
                backgroundColor = AppSettings.getLocalEventBackgroundColor();
                textColor = AppSettings.getLocalEventTextColor();
                break;
        }
        textSize = context.getResources().getDimensionPixelSize(R.dimen.week_header_view_day_event_text_size);

        row = eventDrawingInfo.getRow();
        startCol = eventDrawingInfo.getStartCol();
        endCol = eventDrawingInfo.getEndCol();
        subject = eventDrawingInfo.getSchedule().getSubject();

        backgroundPaint = new Paint();
        backgroundPaint.setColor(backgroundColor);

        textRect = new Rect();
        textPaint = new Paint();
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        textPaint.getTextBounds("12", 0, 1, textRect);

        width = WeekFragment.getSpacingBetweenDay() * (endCol - startCol + 1) - 6;
        height = textRect.height() + 16;

        viewRect = new Rect();

        int left = WeekFragment.getSpacingBetweenDay() * startCol + 3;
        int right = WeekFragment.getSpacingBetweenDay() * (endCol + 1) - 3;
        int top = height * row + 3;
        int bottom = height * (row + 1) - 3;

        viewRect.set(left, top, right, bottom);
    }

    public Rect getViewRect()
    {
        return viewRect;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(parentViewWidth, parentViewHeight);
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
        drawEventView(canvas);
    }

    private void drawEventView(Canvas canvas)
    {
        canvas.drawRect(viewRect, backgroundPaint);
        canvas.drawText(subject, viewRect.left + 2, viewRect.top + (viewRect.height() / 2) + (textRect.height() / 2), textPaint);
    }

    public String getSubject()
    {
        return subject;
    }
}
