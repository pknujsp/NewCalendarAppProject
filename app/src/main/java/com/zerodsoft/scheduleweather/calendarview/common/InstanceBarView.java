package com.zerodsoft.scheduleweather.calendarview.common;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.provider.CalendarContract;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.View;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.event.util.EventUtil;

public class InstanceBarView extends View
{
    private ContentValues INSTANCE;
    private final Paint BAR_PAINT;
    private final TextPaint TITLE_PAINT;
    private final int PADDING;
    private int TEXT_HEIGHT;

    public InstanceBarView(Context context, ContentValues INSTANCE)
    {
        super(context);
        this.INSTANCE = INSTANCE;

        BAR_PAINT = new Paint();
        TITLE_PAINT = new TextPaint();
        PADDING = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, context.getResources().getDisplayMetrics());

        BAR_PAINT.setColor(EventUtil.getColor(INSTANCE.getAsInteger(CalendarContract.Instances.EVENT_COLOR)));
        TITLE_PAINT.setAntiAlias(true);
        TITLE_PAINT.setColor(Color.WHITE);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        TITLE_PAINT.setTextSize(getHeight() - (PADDING * 2));
        Rect rect = new Rect();
        TITLE_PAINT.getTextBounds("0", 0, 1, rect);
        TEXT_HEIGHT = rect.height();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        canvas.drawRect(0, 0, getWidth(), getHeight(), BAR_PAINT);

        float titleX = PADDING;
        float titleY = getHeight() / 2f + TEXT_HEIGHT / 2f;

        if (INSTANCE.getAsString(CalendarContract.Instances.TITLE) != null)
        {
            if (!INSTANCE.getAsString(CalendarContract.Instances.TITLE).isEmpty())
            {
                canvas.drawText(INSTANCE.getAsString(CalendarContract.Instances.TITLE), titleX, titleY, TITLE_PAINT);
            } else
            {
                canvas.drawText(getContext().getString(R.string.empty_title), titleX, titleY, TITLE_PAINT);
            }
        } else
        {
            canvas.drawText(getContext().getString(R.string.empty_title), titleX, titleY, TITLE_PAINT);
        }
    }

    public ContentValues getINSTANCE()
    {
        return INSTANCE;
    }
}
