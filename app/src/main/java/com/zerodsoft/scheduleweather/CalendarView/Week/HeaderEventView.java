package com.zerodsoft.scheduleweather.CalendarView.Week;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

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

    private Context context;

    private String subject;
    private int width;
    private int height;
    private int row;
    private int startCol;
    private int endCol;

    private int parentViewWidth;
    private int parentViewHeight;

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
        parentViewWidth = WeekFragment.getDisplayWidth() - WeekFragment.getSpacingBetweenDay();
        parentViewHeight = 0;

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

        Rect rect = new Rect();
        textPaint = new Paint();
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        textPaint.getTextBounds("12", 0, 1, rect);

        width = WeekFragment.getSpacingBetweenDay() * (endCol - startCol + 1);
        height = rect.height() + 6;

        viewRect = new Rect();

        int left = WeekFragment.getSpacingBetweenDay() * startCol;
        int right = left + width;
        int top = height * row;
        int bottom = top + height;
        viewRect.set(left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(parentViewWidth, parentViewHeight);
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
        drawEventView(canvas);
    }

    private void drawEventView(Canvas canvas)
    {
        canvas.drawRect(viewRect, backgroundPaint);
        canvas.drawText(subject, (viewRect.right - viewRect.left) / 2, viewRect.top, textPaint);
    }
}
