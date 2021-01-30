package com.zerodsoft.scheduleweather.calendarview.common;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.provider.CalendarContract;
import android.text.TextPaint;
import android.view.View;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.etc.EventViewUtil;

public class InstanceView extends View
{
    private IInstanceView iInstanceView;
    private ContentValues instance;

    private Paint instanceViewPaint;
    private TextPaint instanceTextPaint;

    //spacing, margin
    public final int TEXT_LEFT_MARGIN;
    public final int TEXT_TOP_BOTTOM_MARGIN;
    //textsize
    public final int TEXT_SIZE;

    private Integer TEXT_HEIGHT;


    public InstanceView(Context context)
    {
        super(context);

        TEXT_LEFT_MARGIN = (int) context.getResources().getDimension(R.dimen.text_left_margin);
        TEXT_TOP_BOTTOM_MARGIN = (int) context.getResources().getDimension(R.dimen.text_top_bottom_margin);
        TEXT_SIZE = (int) context.getResources().getDimension(R.dimen.text_size);
    }


    public void init(ContentValues instance)
    {
        this.instance = instance;

        if (instance.size() > 0)
        {
            instanceViewPaint = EventViewUtil.getEventColorPaint(instance.getAsInteger(CalendarContract.Instances.EVENT_COLOR));
            instanceTextPaint = EventViewUtil.getEventTextPaint(TEXT_SIZE);

            Rect rect = new Rect();
            instanceTextPaint.getTextBounds("0", 0, 1, rect);

            TEXT_HEIGHT = new Integer(rect.height());
        }
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
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if (instance.size() > 0)
        {
            canvas.drawRect(0, 0, getWidth(), getHeight(), instanceViewPaint);
            canvas.drawText(instance.getAsString(CalendarContract.Instances.TITLE) != null ? instance.getAsString(CalendarContract.Instances.TITLE) : "empty"
                    , TEXT_LEFT_MARGIN, getHeight() / 2f + TEXT_HEIGHT.floatValue() / 2f, instanceTextPaint);
        } else
        {
            final Paint MORE_VIEW_PAINT = new Paint();
            MORE_VIEW_PAINT.setColor(Color.LTGRAY);

            final TextPaint MORE_VIEW_TEXT_PAINT = new TextPaint();
            MORE_VIEW_TEXT_PAINT.setColor(Color.WHITE);
            MORE_VIEW_TEXT_PAINT.setTextSize(TEXT_SIZE);
            MORE_VIEW_TEXT_PAINT.setTextAlign(Paint.Align.LEFT);

            canvas.drawRect(0, 0, getWidth(), getHeight(), MORE_VIEW_PAINT);
            canvas.drawText("MORE"
                    , TEXT_LEFT_MARGIN, getHeight() / 2f + TEXT_HEIGHT.floatValue() / 2f, MORE_VIEW_TEXT_PAINT);
        }
    }

    public ContentValues getInstance()
    {
        return instance;
    }
}
