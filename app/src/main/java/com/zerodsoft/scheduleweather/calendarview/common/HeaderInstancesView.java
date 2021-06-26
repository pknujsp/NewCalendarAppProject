package com.zerodsoft.scheduleweather.calendarview.common;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Canvas;
import android.provider.CalendarContract;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemLongClickListener;
import com.zerodsoft.scheduleweather.calendarview.month.EventData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HeaderInstancesView extends ViewGroup {
	//row
	public final int ROWS_LIMIT;
	//spacing, margin
	public final int SPACING_BETWEEN_INSTANCE_VIEWS;
	public final int TEXT_LEFT_MARGIN;
	public final int TEXT_TOP_BOTTOM_MARGIN;
	//textsize
	public final int TEXT_SIZE;
	//view width,height
	public int VIEW_WIDTH;
	public final int VIEW_HEIGHT;

	private int totalRows;

	private Date startDate;
	private Date endDate;

	private OnEventItemClickListener onEventItemClickListener;
	private OnEventItemLongClickListener onEventItemLongClickListener;

	private List<EventData> eventCellsList = new ArrayList<>();

	public HeaderInstancesView(Context context, OnEventItemClickListener onEventItemClickListener, OnEventItemLongClickListener onEventItemLongClickListener) {
		super(context);
		this.onEventItemClickListener = onEventItemClickListener;
		this.onEventItemLongClickListener = onEventItemLongClickListener;
		ROWS_LIMIT = context.getResources().getInteger(R.integer.rows_limit);
		SPACING_BETWEEN_INSTANCE_VIEWS = (int) context.getResources().getDimension(R.dimen.spacing_between_instance_views);
		TEXT_LEFT_MARGIN = (int) context.getResources().getDimension(R.dimen.text_left_margin);
		TEXT_TOP_BOTTOM_MARGIN = (int) context.getResources().getDimension(R.dimen.text_top_bottom_margin);
		TEXT_SIZE = (int) context.getResources().getDimension(R.dimen.text_size);

		VIEW_HEIGHT = TEXT_SIZE + TEXT_TOP_BOTTOM_MARGIN * 2;

		setWillNotDraw(false);
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setEventCellsList(List<EventData> eventCellsList) {
		this.eventCellsList = eventCellsList;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
		final int CHILD_COUNT = getChildCount();

		for (int childIdx = 0; childIdx < CHILD_COUNT; childIdx++) {
			InstanceView childView = (InstanceView) getChildAt(childIdx);
			EventData eventData = eventCellsList.get(childIdx);

			int startIdx = eventData.getStartIndex();
			int endIdx = eventData.getEndIndex();
			int type = eventData.getType();

			final int row = eventData.getRow();
			int top = (VIEW_HEIGHT + SPACING_BETWEEN_INSTANCE_VIEWS) * row;
			int bottom = top + VIEW_HEIGHT;
			int left = type == EventData.DAY ? eventData.getLeftMargin() : (getWidth() / 7) * startIdx + eventData.getLeftMargin();
			int right = type == EventData.DAY ? getWidth() - eventData.getRightMargin() : (getWidth() / 7) * (endIdx + 1) - eventData.getRightMargin();

			childView.measure(right - left, bottom - top);
			childView.layout(left, top, right, bottom);
			childView.setClickable(true);
			childView.setLongClickable(true);
			childView.setOnClickListener(itemOnClickListener);
			childView.setOnLongClickListener(onLongClickListener);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
	}

	private final View.OnClickListener itemOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			ContentValues instance = ((InstanceView) view).getInstance();

			if (instance.size() == 0) {
				onEventItemClickListener.onClicked(startDate.getTime(), endDate.getTime());
			} else {
				onEventItemClickListener.onClicked(instance.getAsInteger(CalendarContract.Instances.CALENDAR_ID)
						, instance.getAsLong(CalendarContract.Instances._ID), instance.getAsLong(CalendarContract.Instances.EVENT_ID),
						instance.getAsLong(CalendarContract.Instances.BEGIN), instance.getAsLong(CalendarContract.Instances.END));
			}
		}
	};

	private final OnLongClickListener onLongClickListener = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View view) {
			onEventItemLongClickListener.createInstancePopupMenu(((InstanceView) view).getInstance(),
					view, Gravity.BOTTOM);
			return true;
		}
	};

}
