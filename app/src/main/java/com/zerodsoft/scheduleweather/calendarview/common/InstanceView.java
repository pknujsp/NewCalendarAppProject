package com.zerodsoft.scheduleweather.calendarview.common;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.provider.CalendarContract;
import android.text.TextPaint;
import android.view.View;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.event.util.EventUtil;

public class InstanceView extends View {
	private ContentValues instance;

	private Paint instanceViewPaint;
	private TextPaint instanceTextPaint;

	//spacing, margin
	public final int TEXT_LEFT_MARGIN;
	public final int TEXT_TOP_BOTTOM_MARGIN;
	//textsize
	public final int TEXT_SIZE;

	private Integer TEXT_HEIGHT;


	public InstanceView(Context context) {
		super(context);

		TEXT_LEFT_MARGIN = (int) context.getResources().getDimension(R.dimen.text_left_margin);
		TEXT_TOP_BOTTOM_MARGIN = (int) context.getResources().getDimension(R.dimen.text_top_bottom_margin);
		TEXT_SIZE = (int) context.getResources().getDimension(R.dimen.text_size);
	}


	public void init(ContentValues instance) {
		this.instance = instance;

		instanceViewPaint = EventUtil.getEventColorPaint(instance.size() == 0 ?
				Color.RED : instance.getAsInteger(CalendarContract.Instances.EVENT_COLOR));
		instanceTextPaint = EventUtil.getEventTextPaint(TEXT_SIZE);

		Rect rect = new Rect();
		instanceTextPaint.getTextBounds("0", 0, 1, rect);

		TEXT_HEIGHT = new Integer(rect.height());
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
		canvas.drawRect(0, 0, getWidth(), getHeight(), instanceViewPaint);

		final float titleX = TEXT_LEFT_MARGIN;
		final float titleY = getHeight() / 2f + TEXT_HEIGHT.floatValue() / 2f;

		if (instance.size() > 0) {
			if (instance.getAsString(CalendarContract.Instances.TITLE) != null) {
				if (!instance.getAsString(CalendarContract.Instances.TITLE).isEmpty()) {
					canvas.drawText(instance.getAsString(CalendarContract.Instances.TITLE), titleX, titleY, instanceTextPaint);
				} else {
					canvas.drawText(getContext().getString(R.string.empty_title), titleX, titleY, instanceTextPaint);
				}
			} else {
				canvas.drawText(getContext().getString(R.string.empty_title), titleX, titleY, instanceTextPaint);
			}
		} else {
			canvas.drawText("More", titleX, titleY, instanceTextPaint);
		}
	}

	public ContentValues getInstance() {
		return instance;
	}
}
