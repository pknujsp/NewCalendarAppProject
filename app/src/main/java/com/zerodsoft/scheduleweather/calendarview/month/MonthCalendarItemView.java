package com.zerodsoft.scheduleweather.calendarview.month;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;
import com.zerodsoft.scheduleweather.utility.AppSettings;
import com.zerodsoft.scheduleweather.utility.Clock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MonthCalendarItemView extends View
{

    private TextPaint DAY_TEXT_PAINT;
    private Paint GOOGLE_EVENT_PAINT;
    private Paint LOCAL_EVENT_PAINT;
    private Paint GOOGLE_EVENT_TEXT_PAINT;
    private Paint LOCAL_EVENT_TEXT_PAINT;

    private float EVENT_TEXT_HEIGHT;
    private static final int SPACING_BETWEEN_EVENT = 12;
    private static final int TEXT_MARGIN = 8;
    public static final int EVENT_COUNT = 5;

    private float x;
    private float y;
    private float eventStartY;
    private float eventHeight;

    private Date startDate;
    private Date endDate;
    private ItemLayoutCell itemLayoutCell;

    public MonthCalendarItemView(Context context, int dayTextColor)
    {
        super(context);

        DAY_TEXT_PAINT = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        DAY_TEXT_PAINT.setTextAlign(Paint.Align.CENTER);
        DAY_TEXT_PAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, context.getResources().getDisplayMetrics()));
        DAY_TEXT_PAINT.setColor(dayTextColor);

        GOOGLE_EVENT_PAINT = new Paint();
        LOCAL_EVENT_PAINT = new Paint();

        GOOGLE_EVENT_PAINT.setColor(AppSettings.getGoogleEventBackgroundColor());
        LOCAL_EVENT_PAINT.setColor(AppSettings.getLocalEventBackgroundColor());

        GOOGLE_EVENT_TEXT_PAINT = new Paint();
        LOCAL_EVENT_TEXT_PAINT = new Paint();

        GOOGLE_EVENT_TEXT_PAINT.setColor(AppSettings.getGoogleEventTextColor());
        GOOGLE_EVENT_TEXT_PAINT.setTextAlign(Paint.Align.LEFT);

        LOCAL_EVENT_TEXT_PAINT.setColor(AppSettings.getLocalEventTextColor());
        LOCAL_EVENT_TEXT_PAINT.setTextAlign(Paint.Align.LEFT);


        TypedValue backgroundValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, backgroundValue, true);
        setBackgroundResource(backgroundValue.resourceId);

        setClickable(true);
        setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });
    }

    public MonthCalendarItemView setDate(Date startDate, Date endDate)
    {
        this.startDate = startDate;
        this.endDate = endDate;
        return this;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        Rect rect = new Rect();
        DAY_TEXT_PAINT.getTextBounds("31", 0, 1, rect);

        x = getWidth() / 2;
        y = rect.height() + 8;

        eventStartY = y + 16;
        eventHeight = (getHeight() - eventStartY - SPACING_BETWEEN_EVENT * 4) / EVENT_COUNT;

        GOOGLE_EVENT_TEXT_PAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, eventHeight - TEXT_MARGIN, getContext().getResources().getDisplayMetrics()));
        LOCAL_EVENT_TEXT_PAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, eventHeight - TEXT_MARGIN, getContext().getResources().getDisplayMetrics()));
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        // 뷰를 그림
        drawView(canvas);
        if (itemLayoutCell != null)
        {
            drawEvent(canvas);
        }
    }

    private void drawView(Canvas canvas)
    {
        canvas.drawText(Clock.DAY_OF_MONTH_FORMAT.format(startDate), x, y, DAY_TEXT_PAINT);
    }

    private void drawEvent(Canvas canvas)
    {
        int leftMargin = 0;
        int rightMargin = 0;

        float left = 0;
        float right = 0;
        float top = 0;
        float bottom = 0;

        for (int count = 0; count < itemLayoutCell.rows.size(); count++)
        {
            ScheduleDTO schedule = itemLayoutCell.rows.get(count);
            if (!schedule.isEmpty())
            {
                top = eventStartY + ((eventHeight + SPACING_BETWEEN_EVENT) * count);
                bottom = top + eventHeight;

                if (count == EVENT_COUNT - 1)
                {
                    Paint extraPaint = new Paint();
                    extraPaint.setColor(Color.LTGRAY);

                    TextPaint textPaint = new TextPaint();
                    textPaint.setColor(Color.WHITE);
                    textPaint.setTextSize(LOCAL_EVENT_TEXT_PAINT.getTextSize());
                    textPaint.setTextAlign(Paint.Align.LEFT);

                    canvas.drawRect(2, top, getWidth() - 2, bottom, extraPaint);
                    canvas.drawText("More", 2 + TEXT_MARGIN, bottom - TEXT_MARGIN, textPaint);

                    break;
                } else
                {
                    // 시작/종료일이 date가 아니나, 일정에 포함되는 경우
                    if (schedule.getStartDate().before(startDate) && schedule.getEndDate().after(endDate))
                    {
                        leftMargin = 0;
                        rightMargin = 0;
                    }
                    // 시작일이 date인 경우
                    else if ((schedule.getEndDate().compareTo(startDate) >= 0 && schedule.getEndDate().before(endDate)) && schedule.getEndDate().after(endDate))
                    {
                        leftMargin = 4;
                        rightMargin = 0;
                    }
                    // 종료일이 date인 경우
                    else if ((schedule.getEndDate().compareTo(startDate) >= 0 && schedule.getEndDate().before(endDate)) && schedule.getStartDate().before(startDate))
                    {
                        leftMargin = 0;
                        rightMargin = 4;
                    }
                    // 시작/종료일이 date인 경우
                    else if ((schedule.getEndDate().compareTo(startDate) >= 0 && schedule.getEndDate().before(endDate)) && (schedule.getStartDate().compareTo(startDate) >= 0 && schedule.getStartDate().before(endDate)))
                    {
                        leftMargin = 4;
                        rightMargin = 4;
                    }

                    left = leftMargin;
                    right = getWidth() - rightMargin;

                    if (schedule.getCategory() == ScheduleDTO.GOOGLE_CATEGORY)
                    {
                        canvas.drawRect(left, top, right, bottom, GOOGLE_EVENT_PAINT);
                        canvas.drawText(schedule.getSubject(), left + TEXT_MARGIN, bottom - TEXT_MARGIN, GOOGLE_EVENT_TEXT_PAINT);
                    } else
                    {
                        canvas.drawRect(left, top, right, bottom, LOCAL_EVENT_PAINT);
                        canvas.drawText(schedule.getSubject(), left + TEXT_MARGIN, bottom - TEXT_MARGIN, LOCAL_EVENT_TEXT_PAINT);
                    }
                }
            }
        }
    }

    public void setItemLayoutCell(ItemLayoutCell itemLayoutCell)
    {
        this.itemLayoutCell = itemLayoutCell;
    }
}