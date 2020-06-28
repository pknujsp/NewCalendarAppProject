package com.zerodsoft.scheduleweather.CalendarView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.zerodsoft.scheduleweather.DayFragment;
import com.zerodsoft.scheduleweather.R;

import java.util.Calendar;

public class WeekDatesView extends View implements DayFragment.OnUpdateWeekDatesListener
{
    private int weekTextColor;
    private int weekTextSize;
    private int weekBackgroundColor;
    private int weekHeaderEventTextSize;
    private int weekHeaderEventBoxHeight;
    private int eventMaxNum;
    private Paint weekBackgroundPaint;
    private Paint weekTextBoxPaint;
    private Paint weekTextBoxRectPaint;
    private Paint dividingPaint;
    private Rect weekTextBoxRect;
    private int textBoxWidth;
    private int textBoxHeight;
    private int viewWidth;
    private int eventsViewHeight;
    private int normalViewHeight;
    private int headerRowMargin;
    private String week = Integer.toString(WeekHeaderView.today.get(Calendar.WEEK_OF_YEAR)) + "주";
    private Context mContext;
    private float x;
    private float y;

    private Bitmap expandMoreBitmap;
    private Bitmap expandLessBitmap;

    private boolean isExpandedView = false;
    private boolean manyItems = false;
    private boolean haveEvents = false;

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
            weekHeaderEventTextSize = context.getResources().getDimensionPixelSize(R.dimen.week_header_view_day_event_text_size);
        } finally
        {
            a.recycle();
        }
        init();
    }


    private void init()
    {
        headerRowMargin = mContext.getResources().getDimensionPixelSize(R.dimen.week_header_view_day_row_margin);
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

        normalViewHeight = headerHeight;

        dividingPaint = new Paint();
        dividingPaint.setColor(Color.BLACK);

        expandMoreBitmap = convertDrawable(mContext.getResources().getDrawable(R.drawable.expand_more_icon, null));
        expandLessBitmap = convertDrawable(mContext.getResources().getDrawable(R.drawable.expand_less_icon, null));

        Paint eventBoxPaint = new Paint();
        eventBoxPaint.setTextSize(weekHeaderEventTextSize);
        eventBoxPaint.getTextBounds("1", 0, 1, rect);
        weekHeaderEventBoxHeight = rect.height();
    }

    public Bitmap convertDrawable(Drawable drawable)
    {
        if (drawable instanceof BitmapDrawable)
        {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        if (haveEvents)
        {
            setMeasuredDimension(widthMeasureSpec, eventsViewHeight);
        } else
        {
            setMeasuredDimension(widthMeasureSpec, normalViewHeight);
        }
    }

    @Override
    public void layout(int l, int t, int r, int b)
    {
        super.layout(l, t, r, b);
        x = getWidth() / 2;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        drawWeekDatesView(canvas);
        int height = getHeight();
        canvas.drawLine(0f, getHeight() - 1, getWidth(), getHeight() - 1, dividingPaint);

        if (manyItems)
        {
            showExpandBtn(canvas);
        }
    }

    public void showExpandBtn(Canvas canvas)
    {
        if (isExpandedView)
        {
            canvas.drawBitmap(expandMoreBitmap, getWidth() / 2 - expandMoreBitmap.getWidth() / 2, getHeight() - expandMoreBitmap.getHeight(), new Paint());
        } else
        {
            canvas.drawBitmap(expandLessBitmap, getWidth() / 2 - expandMoreBitmap.getWidth() / 2, getHeight() - expandMoreBitmap.getHeight(), new Paint());
        }
    }

    public WeekDatesView setManyItems(boolean manyItems)
    {
        this.manyItems = manyItems;
        this.isExpandedView = manyItems;
        return this;
    }

    public void updateViewHeight(int eventMaxNum)
    {
        this.haveEvents = true;
        this.eventMaxNum = eventMaxNum;
        eventsViewHeight = normalViewHeight;

        if (eventMaxNum > 2)
        {
            setManyItems(true);
            eventsViewHeight = eventsViewHeight + (weekHeaderEventBoxHeight + headerRowMargin) * 2 + headerRowMargin;
        } else
        {
            setManyItems(false);
            eventsViewHeight = eventsViewHeight + (weekHeaderEventBoxHeight + headerRowMargin) * eventMaxNum + headerRowMargin;
        }
        requestLayout();
        invalidate();
    }


    private void drawWeekDatesView(Canvas canvas)
    {
        canvas.drawRect(0, 0, getWidth(), getHeight(), weekBackgroundPaint);
        canvas.drawRect(x - textBoxWidth / 2 - 10, normalViewHeight / 2 - textBoxHeight / 2 - 10, x + textBoxWidth / 2 + 10, normalViewHeight / 2 + textBoxHeight / 2 + 10, weekTextBoxRectPaint);
        canvas.drawText(week, x, normalViewHeight / 2 + weekTextBoxRect.height() / 2, weekTextBoxPaint);
    }

    @Override
    public void updateWeekDates(String week)
    {
        this.week = week;
        invalidate();
    }

}
