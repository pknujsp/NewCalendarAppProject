package com.zerodsoft.scheduleweather.calendarview.week;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.provider.CalendarContract;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendarview.common.CurrentTimeLineView;
import com.zerodsoft.scheduleweather.calendarview.hourside.HourEventsView;
import com.zerodsoft.scheduleweather.calendarview.interfaces.CalendarViewInitializer;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IControlEvent;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IEvent;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnDateTimeChangedListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemLongClickListener;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WeekView extends HourEventsView implements CalendarViewInitializer, OnDateTimeChangedListener {
	private boolean createdAddScheduleRect = false;
	private boolean changingStartTime = false;
	private boolean changingEndTime = false;

	private OnEventItemClickListener onEventItemClickListener;
	private OnEventItemLongClickListener onEventItemLongClickListener;

	private Date[] daysOfWeek;
	private List<ContentValues> instances;
	private SparseArray<List<ItemCell>> eventSparseArr = new SparseArray<>(7);
	private final int SPACING_BETWEEN_EVENTS = 5;

	public WeekView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}


	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		Calendar calendar = Calendar.getInstance();
		float left = 0f;
		float top = 0f;
		float right = 0f;
		float bottom = 0f;

		if (eventSparseArr.size() >= 1) {
			float cellWidth = 0f;
			int childCount = getChildCount();
			if (currentTimeLineView != null) {
				childCount--;
			}

			for (int i = 0; i < childCount; i++) {
				WeekItemView childView = (WeekItemView) getChildAt(i);
				ItemCell itemCell = childView.itemCell;

				int column = itemCell.column;
				int index = itemCell.index;
				int columnCount = itemCell.columnCount;

				calendar.setTimeInMillis(itemCell.instance.getAsLong(CalendarContract.Instances.BEGIN));
				PointF startPoint = getPoint(calendar, index);
				calendar.setTimeInMillis(itemCell.instance.getAsLong(CalendarContract.Instances.END));
				PointF endPoint = getPoint(calendar, index);

				cellWidth = (WeekFragment.getColumnWidth() - (SPACING_BETWEEN_EVENTS * (columnCount - 1))) / columnCount;

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
			int index = ClockUtil.calcBeginDayDifference(((Date) currentTimeLineView.getTag()).getTime(), daysOfWeek[0].getTime());
			PointF startPoint = getPoint(calendar, index);

			final int lineViewHeight = context.getResources().getDimensionPixelSize(R.dimen.current_time_line_view_text_height);

			top = startPoint.y - lineViewHeight / 2f;
			left = startPoint.x - WeekFragment.getColumnWidth();
			right = startPoint.x + WeekFragment.getColumnWidth();
			bottom = startPoint.y + lineViewHeight / 2f;

			currentTimeLineView.measure((int) (right - left), lineViewHeight);
			currentTimeLineView.layout((int) left, (int) top, (int) right, (int) bottom);
		}
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		for (int i = 2; i <= 7; i++) {
			// 세로 선
			canvas.drawLine(WeekFragment.getColumnWidth() * i, 0, WeekFragment.getColumnWidth() * i, VIEW_HEIGHT - TABLE_TB_MARGIN, DIVIDING_LINE_PAINT);
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
	}


	private PointF getPoint(Calendar time, int index) {
		PointF point = new PointF(0f, 0f);

		// x
		point.x = WeekFragment.getColumnWidth() * (index + 1);

		// y
		int hour = time.get(Calendar.HOUR_OF_DAY);
		int minute = time.get(Calendar.MINUTE);

		float hourY = SPACING_BETWEEN_HOURS * hour + TABLE_TB_MARGIN;
		float heightPerMinute = SPACING_BETWEEN_HOURS / 60f;
		point.y = hourY + heightPerMinute * minute;

		return point;
	}


	public void setDaysOfWeek(Date[] daysOfWeek) {
		this.daysOfWeek = daysOfWeek;
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
	public void setInstances(List<ContentValues> instances) {
		// 1일 이하의 일정만 표시
		this.instances = new ArrayList<>();
		for (ContentValues instance : instances) {
			if (ClockUtil.areSameDate(instance.getAsLong(CalendarContract.Instances.BEGIN), instance.getAsLong(CalendarContract.Instances.END))) {
				this.instances.add(instance);
			}
		}
	}

	@Override
	public void setEventTable() {
		eventSparseArr.clear();
		removeAllViews();

		boolean showCanceledInstance = App.isPreference_key_show_canceled_instances();

		// 데이터를 리스트에 저장
		for (ContentValues instance : instances) {
			if (!showCanceledInstance) {
				if (instance.getAsInteger(CalendarContract.Instances.STATUS) ==
						CalendarContract.Instances.STATUS_CANCELED) {
					// 취소(초대 거부)된 인스턴스인 경우..
					continue;
				}
			}


			if (instance.getAsInteger(CalendarContract.Instances.ALL_DAY) != 1 &&
					instance.getAsInteger(CalendarContract.Instances.START_DAY).equals(instance.getAsInteger(CalendarContract.Instances.END_DAY))) {
				int index = ClockUtil.calcBeginDayDifference(instance.getAsLong(CalendarContract.Instances.BEGIN), daysOfWeek[0].getTime());
				if (eventSparseArr.get(index) == null) {
					eventSparseArr.put(index, new ArrayList<>());
				}

				ItemCell itemCell = new ItemCell(instance, index);
				eventSparseArr.get(index).add(itemCell);
			}
		}

		// 저장된 데이터가 표시될 위치를 설정
		if (!instances.isEmpty()) {
			for (int index = 0; index < 7; index++) {
				List<ItemCell> itemCells = eventSparseArr.get(index);

				if (itemCells != null) {
					if (itemCells.size() >= 2) {
						// 일정 길이의 내림차순으로 정렬
						Collections.sort(itemCells, cellComparator);

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
					}
				}
			}

			for (int index = 0; index < 7; index++) {
				List<ItemCell> itemCells = eventSparseArr.get(index);

				if (itemCells != null) {
					for (int i = 0; i < itemCells.size(); i++) {
						WeekItemView child = new WeekItemView(context, itemCells.get(i));
						child.setClickable(true);
						child.setOnClickListener(itemOnClickListener);

						if (child.itemCell.instance.getAsInteger(CalendarContract.Instances.CALENDAR_ACCESS_LEVEL) == CalendarContract.Instances.CAL_ACCESS_READ) {
							child.setLongClickable(false);
						} else {
							child.setLongClickable(true);
							child.setOnLongClickListener(onLongClickListener);
						}
						this.addView(child);
					}
				}
			}
		}
		receivedTimeTick(new Date(System.currentTimeMillis()));
		requestLayout();
		invalidate();
	}

	@Override
	public void refresh() {

	}

	private boolean isOverlapping(ContentValues event1, ContentValues event2) {
		long start1 = event1.getAsLong(CalendarContract.Instances.BEGIN);
		long end1 = event1.getAsLong(CalendarContract.Instances.END);

		long start2 = event2.getAsLong(CalendarContract.Instances.BEGIN);
		long end2 = event2.getAsLong(CalendarContract.Instances.END);

		return (start1 >= start2 && start1 <= end2) || (end1 >= start2 && end1 <= end2)
				|| (start1 <= start2 && end1 >= end2);
	}


	@Override
	public void receivedTimeTick(Date date) {
		if (currentTimeLineView != null) {
			removeView(currentTimeLineView);
			currentTimeLineView = null;
		}

		if (date.compareTo(daysOfWeek[0]) >= 0 && date.compareTo(daysOfWeek[7]) < 0) {
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
		public int index;
		public ContentValues instance;
		public Paint eventColorPaint;
		public TextPaint eventTextPaint;

		public ItemCell(ContentValues instance, int index) {
			this.column = NOT_OVERLAP;
			this.columnCount = 1;
			this.index = index;
			this.instance = instance;
		}
	}

	private final Comparator<ItemCell> cellComparator = new Comparator<ItemCell>() {
		@Override
		public int compare(ItemCell t1, ItemCell t2) {
			long start1 = t1.instance.getAsLong(CalendarContract.Instances.BEGIN);
			long end1 = t1.instance.getAsLong(CalendarContract.Instances.END);

			long start2 = t2.instance.getAsLong(CalendarContract.Instances.BEGIN);
			long end2 = t2.instance.getAsLong(CalendarContract.Instances.END);

			if ((end1 - start1) <=
					(end2 - start2)) {
				return 1;
			} else {
				return -1;
			}
		}
	};

	private View.OnClickListener itemOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			ContentValues instance = ((WeekItemView) view).itemCell.instance;

			onEventItemClickListener.onClicked(instance.getAsInteger(CalendarContract.Instances.CALENDAR_ID)
					, instance.getAsLong(CalendarContract.Instances._ID), instance.getAsLong(CalendarContract.Instances.EVENT_ID),
					instance.getAsLong(CalendarContract.Instances.BEGIN), instance.getAsLong(CalendarContract.Instances.END));
		}
	};

	private final View.OnLongClickListener onLongClickListener = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View view) {
			ContentValues instance = ((WeekItemView) view).itemCell.instance;
			onEventItemLongClickListener.createInstancePopupMenu(instance, view, Gravity.CENTER);
			return true;
		}
	};

	class WeekItemView extends View {
		public ItemCell itemCell;

		public WeekItemView(Context context, ItemCell itemCell) {
			super(context);
			this.itemCell = itemCell;
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			itemCell.eventColorPaint = EventUtil.getEventColorPaint(itemCell.instance);
			itemCell.eventTextPaint = EventUtil.getEventTextPaint(EVENT_TEXT_HEIGHT);

			canvas.drawRect(EVENT_RECT_MARGIN, 0, getWidth() - EVENT_RECT_MARGIN, getHeight(), itemCell.eventColorPaint);

			final float titleX = TEXT_MARGIN + EVENT_RECT_MARGIN;
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