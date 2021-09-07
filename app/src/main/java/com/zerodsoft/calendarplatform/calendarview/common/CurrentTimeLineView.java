package com.zerodsoft.calendarplatform.calendarview.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.View;

import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.calendarview.week.WeekFragment;
import com.zerodsoft.calendarplatform.utility.ClockUtil;

import java.util.Date;

public class CurrentTimeLineView extends View {
	private final Paint LINE_PAINT;
	private final TextPaint TIME_TEXT_PAINT;
	private final int TEXT_WIDTH;
	private final int TEXT_HEIGHT;
	private final int MARGIN;
	private final int LINE_HEIGHT;
	private final int CELL_WIDTH;
	private String time;

	public CurrentTimeLineView(Context context) {
		super(context);
		LINE_PAINT = new Paint();
		LINE_PAINT.setColor(Color.BLUE);

		TIME_TEXT_PAINT = new TextPaint();
		TIME_TEXT_PAINT.setColor(Color.BLACK);
		TIME_TEXT_PAINT.setTextAlign(Paint.Align.LEFT);
		TIME_TEXT_PAINT.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.current_time_line_view_text_height));

		Rect rect = new Rect();
		TIME_TEXT_PAINT.getTextBounds("00:00", 0, "00:00".length(), rect);
		TEXT_WIDTH = rect.width();
		TEXT_HEIGHT = rect.height();

		MARGIN = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, context.getResources().getDisplayMetrics());
		LINE_HEIGHT = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.3f, context.getResources().getDisplayMetrics());
		CELL_WIDTH = WeekFragment.getColumnWidth();
	}

	public void setTime(Date date) {
		time = ClockUtil.HOURS_24.format(date);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		final float lineLeft = CELL_WIDTH - MARGIN;
		final float lineTop = getHeight() / 2f - LINE_HEIGHT / 2f;
		final float lineRight = getWidth();
		final float lineBottom = lineTop + LINE_HEIGHT;

		final float textStartX = lineLeft - MARGIN * 2 - TEXT_WIDTH;
		final float textStartY = getHeight() / 2f + TIME_TEXT_PAINT.descent();

		canvas.drawText(time, textStartX, textStartY, TIME_TEXT_PAINT);
		canvas.drawRect(lineLeft, lineTop, lineRight, lineBottom, LINE_PAINT);
	}
}
