package com.zerodsoft.calendarplatform.calendarview.day;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Instances;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.Nullable;

import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.activity.App;
import com.zerodsoft.calendarplatform.calendar.dto.CalendarInstance;
import com.zerodsoft.calendarplatform.calendarview.common.CurrentTimeLineView;
import com.zerodsoft.calendarplatform.calendarview.interfaces.CalendarViewInitializer;
import com.zerodsoft.calendarplatform.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.calendarplatform.calendarview.interfaces.IControlEvent;
import com.zerodsoft.calendarplatform.calendarview.interfaces.OnDateTimeChangedListener;
import com.zerodsoft.calendarplatform.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.calendarplatform.calendarview.interfaces.OnEventItemLongClickListener;
import com.zerodsoft.calendarplatform.calendarview.week.WeekFragment;
import com.zerodsoft.calendarplatform.calendarview.hourside.HourEventsView;
import com.zerodsoft.calendarplatform.event.util.EventUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DayView extends HourEventsView implements CalendarViewInitializer, OnDateTimeChangedListener {
	private boolean createdAddScheduleRect = false;
	private boolean changingStartTime = false;
	private boolean changingEndTime = false;

	private Date viewStartDate;
	private Date viewEndDate;
	private List<ItemCell> itemCells = new ArrayList<>();
	private final int SPACING_BETWEEN_EVENTS = 5;

	private List<ContentValues> instances;
	private OnEventItemClickListener onEventItemClickListener;
	private OnEventItemLongClickListener onEventItemLongClickListener;


	public DayView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);

		Calendar calendar = Calendar.getInstance();
		if (!itemCells.isEmpty()) {
			float left = 0f;
			float top = 0f;
			float right = 0f;
			float bottom = 0f;
			float cellWidth = 0f;
			int childCount = getChildCount();
			if (currentTimeLineView != null) {
				childCount--;
			}

			for (int i = 0; i < childCount; i++) {
				DayItemView childView = (DayItemView) getChildAt(i);
				ItemCell itemCell = childView.itemCell;

				int column = itemCell.column;
				int columnCount = itemCell.columnCount;

				calendar.setTimeInMillis(itemCell.instance.getAsLong(CalendarContract.Instances.BEGIN));
				PointF startPoint = getPoint(calendar);
				calendar.setTimeInMillis(itemCell.instance.getAsLong(CalendarContract.Instances.END));
				PointF endPoint = getPoint(calendar);

				cellWidth = (getWidth() - WeekFragment.getColumnWidth() - (SPACING_BETWEEN_EVENTS * (columnCount - 1))) / columnCount;

				top = startPoint.y;
				bottom = endPoint.y;
				if (column == ItemCell.NOT_OVERLAP) {
					left = startPoint.x;
				} else {
					left = startPoint.x + ((cellWidth + SPACING_BETWEEN_EVENTS) * column);
				}
				right = left + cellWidth;

				int width = (int) (right - left);
				int height = (int) (bottom - top);

				childView.measure(width, height);
				childView.layout((int) left, (int) top, (int) right, (int) bottom);
			}
		}

		if (currentTimeLineView != null) {
			calendar.setTime((Date) currentTimeLineView.getTag());
			PointF startPoint = getPoint(calendar);

			final int lineViewHeight = context.getResources().getDimensionPixelSize(R.dimen.current_time_line_view_text_height);

			currentTimeLineView.measure(getWidth(), lineViewHeight);
			currentTimeLineView.layout(0, (int) startPoint.y - lineViewHeight / 2, getWidth(), (int) startPoint.y + lineViewHeight / 2);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawView(canvas);
	}

	private PointF getPoint(Calendar time) {
		// y
		int hour = time.get(Calendar.HOUR_OF_DAY);
		int minute = time.get(Calendar.MINUTE);

		float hourY = SPACING_BETWEEN_HOURS * hour + TABLE_TB_MARGIN;
		float heightPerMinute = SPACING_BETWEEN_HOURS / 60f;

		return new PointF(WeekFragment.getColumnWidth(), hourY + heightPerMinute * minute);
	}


	private void drawView(Canvas canvas) {
		if (createdAddScheduleRect) {
			// 일정 추가 사각형 코드
			rectStartPoint = getTimePoint(TIME_CATEGORY.START);
			rectEndPoint = getTimePoint(TIME_CATEGORY.END);

			NEW_SCHEDULE_RECT_DRAWABLE.draw(canvas);
		}
	}

	private PointF getTimePoint(TIME_CATEGORY timeCategory) {
		// START또는 END TIME에 해당하는 좌표값을 반환
		Calendar time = null;

		if (timeCategory == TIME_CATEGORY.START) {
			time = (Calendar) startTime.clone();
		} else {
			time = (Calendar) endTime.clone();
		}

		PointF point = new PointF(0f, 0f);

		// y
		float startHour = currentTouchedPoint.y + SPACING_BETWEEN_HOURS * time.get(Calendar.HOUR_OF_DAY);
		float endHour = currentTouchedPoint.y + SPACING_BETWEEN_HOURS * (time.get(Calendar.HOUR_OF_DAY) + 1);

		if (time.get(Calendar.HOUR_OF_DAY) == 0 && timeCategory == TIME_CATEGORY.END) {
			startHour = currentTouchedPoint.y + SPACING_BETWEEN_HOURS * 24;
			// 다음 날 오전1시
			endHour = currentTouchedPoint.y + SPACING_BETWEEN_HOURS * 25;
		}
		float minute15Height = (endHour - startHour) / 4f;

		for (int j = 0; j <= 3; j++) {
			if (time.get(Calendar.MINUTE) == j * 15) {
				point.y = startHour + minute15Height * j;
				break;
			}
		}
		return point;
	}

	private boolean isOverlapping(ContentValues i1, ContentValues i2) {
		long start1 = i1.getAsLong(Instances.BEGIN);
		long end1 = i1.getAsLong(Instances.END);

		long start2 = i2.getAsLong(Instances.BEGIN);
		long end2 = i2.getAsLong(Instances.END);

		return (start1 >= start2 && start1 <= end2) || (end1 >= start2 && end1 <= end2)
				|| (start1 <= start2 && end1 >= end2);
	}

	public void setInstances(List<ContentValues> instances) {
		// 이벤트 테이블에 데이터를 표시할 위치 설정
		// 데이터가 없는 경우 진행하지 않음
		this.instances = instances;
		itemCells.clear();
		setEventTable();
		requestLayout();
		invalidate();
	}


	@Override
	public void init(Calendar copiedCalendar, OnEventItemLongClickListener onEventItemLongClickListener, OnEventItemClickListener onEventItemClickListener, IControlEvent iControlEvent, IConnectedCalendars iConnectedCalendars) {
		this.onEventItemClickListener = onEventItemClickListener;
		this.onEventItemLongClickListener = onEventItemLongClickListener;
	}

	@Override
	public void setInstances(Map<Integer, CalendarInstance> resultMap) {

	}

	@Override
	public void setEventTable() {
		removeAllViews();
		itemCells.clear();

		boolean showCanceledInstance = App.isPreference_key_show_canceled_instances();

		// 저장된 데이터가 표시될 위치를 설정
		for (ContentValues instance : instances) {
			if (!showCanceledInstance) {
				if (instance.getAsInteger(Instances.STATUS) ==
						Instances.STATUS_CANCELED) {
					// 취소(초대 거부)된 인스턴스인 경우..
					continue;
				}
			}

			if (instance.getAsInteger(Instances.ALL_DAY) != 1 &&
					instance.getAsInteger(Instances.START_DAY).equals(instance.getAsInteger(Instances.END_DAY))) {
				ItemCell itemCell = new ItemCell(instance);
				itemCells.add(itemCell);
			}
		}

		for (int i = 0; i < itemCells.size() - 1; i++) {
			if (itemCells.get(i).column != ItemCell.NOT_OVERLAP) {
				continue;
			}
			int col = 0;
			int overlappingCount = 0;
			List<ItemCell> overlappingList = null;

			for (int j = i + 1; j < itemCells.size(); j++) {
				if (isOverlapping(itemCells.get(i).instance, itemCells.get(j).instance)) {
					// 시간이 겹치는 경우
					if (itemCells.get(i).column == ItemCell.NOT_OVERLAP) {
						itemCells.get(i).column = col++;
						overlappingList = new ArrayList<>();
						overlappingList.add(itemCells.get(i));
					}
					itemCells.get(j).column = col++;
					overlappingList.add(itemCells.get(j));
					overlappingCount++;
				}
			}

			if (overlappingCount == 0) {
				// 시간이 겹치지 않는 경우
				itemCells.get(i).column = ItemCell.NOT_OVERLAP;
			} else {
				for (ItemCell cell : overlappingList) {
					cell.columnCount = overlappingCount + 1;
				}
			}
		}

		for (int i = 0; i < itemCells.size(); i++) {
			DayItemView childView = new DayItemView(context, itemCells.get(i));

			childView.setOnClickListener(itemOnClickListener);
			childView.setOnLongClickListener(itemOnLongClickListener);
			childView.setLongClickable(true);
			childView.setClickable(true);

			if (childView.itemCell.instance.getAsInteger(Instances.CALENDAR_ACCESS_LEVEL) == Instances.CAL_ACCESS_READ) {
				childView.setLongClickable(false);
			} else {
				childView.setOnLongClickListener(itemOnLongClickListener);
				childView.setLongClickable(true);
			}

			addView(childView);
		}
		receivedTimeTick(new Date(System.currentTimeMillis()));
		requestLayout();
		invalidate();
	}


	@Override
	public void refresh() {

	}

	private final View.OnLongClickListener itemOnLongClickListener = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View view) {
			ContentValues instance = ((DayItemView) view).itemCell.instance;
			onEventItemLongClickListener.createInstancePopupMenu(instance, view, Gravity.CENTER);

			return true;
		}
	};

	private final View.OnClickListener itemOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			ContentValues instance = ((DayItemView) view).itemCell.instance;

			onEventItemClickListener.onClicked(instance.getAsInteger(Instances.CALENDAR_ID)
					, instance.getAsLong(Instances._ID), instance.getAsLong(Instances.EVENT_ID),
					instance.getAsLong(Instances.BEGIN), instance.getAsLong(Instances.END));
		}
	};

	public void setDates(Date viewStartDate, Date viewEndDate) {
		this.viewStartDate = viewStartDate;
		this.viewEndDate = viewEndDate;
	}

	@Override
	public void receivedTimeTick(Date date) {
		if (currentTimeLineView != null) {
			removeView(currentTimeLineView);
			currentTimeLineView = null;
		}

		if (date.compareTo(viewStartDate) >= 0 && date.compareTo(viewEndDate) < 0) {
			currentTimeLineView = new CurrentTimeLineView(getContext());
			currentTimeLineView.setTime(date);
			currentTimeLineView.setTag(date);
			addView(currentTimeLineView);
		}
	}

	@Override
	public void receivedDateChanged(Date date) {
		receivedTimeTick(date);
	}

	static class ItemCell {
		public static final int NOT_OVERLAP = -1;
		public int column;
		public int columnCount;
		public Paint eventColorPaint;
		public TextPaint eventTextPaint;
		public ContentValues instance;

		public ItemCell(ContentValues instance) {
			this.column = NOT_OVERLAP;
			this.columnCount = 1;
			this.instance = instance;
		}
	}

	class DayItemView extends View {
		public ItemCell itemCell;

		public DayItemView(Context context, ItemCell itemCell) {
			super(context);
			this.itemCell = itemCell;
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			itemCell.eventColorPaint = EventUtil.getEventColorPaint(itemCell.instance);
			itemCell.eventTextPaint = EventUtil.getEventTextPaint(EVENT_TEXT_HEIGHT);

			canvas.drawRect(0, 0, getWidth(), getHeight(), itemCell.eventColorPaint);

			final float titleX = TEXT_MARGIN;
			final float titleY = EVENT_TEXT_HEIGHT + TEXT_MARGIN;

			if (itemCell.instance.getAsString(CalendarContract.Instances.TITLE) != null) {
				if (!itemCell.instance.getAsString(CalendarContract.Instances.TITLE).isEmpty()) {
					canvas.drawText(itemCell.instance.getAsString(CalendarContract.Instances.TITLE), titleX, titleY, itemCell.eventTextPaint);
				} else {
					canvas.drawText(getContext().getString(R.string.empty_title), titleX, titleY, itemCell.eventTextPaint);
				}
			} else {
				canvas.drawText(getContext().getString(R.string.empty_title), titleX, titleY, itemCell.eventTextPaint);
			}
		}

	}

}
