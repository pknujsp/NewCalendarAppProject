package com.zerodsoft.scheduleweather.calendarview.week;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import android.graphics.RectF;
import android.provider.CalendarContract;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendarview.common.HeaderInstancesView;
import com.zerodsoft.scheduleweather.calendarview.common.InstanceView;
import com.zerodsoft.scheduleweather.calendarview.interfaces.CalendarViewInitializer;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IControlEvent;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemLongClickListener;
import com.zerodsoft.scheduleweather.calendarview.month.EventData;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.utility.DateHour;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


public class WeekHeaderView extends ViewGroup implements CalendarViewInitializer {
	private Calendar weekFirstDay;
	private Calendar weekLastDay;

	private Integer COLUMN_WIDTH;

	private final TextPaint DAY_TEXT_PAINT;
	private final TextPaint DATE_TEXT_PAINT;
	private final TextPaint WEEK_OF_YEAR_TEXT_PAINT;
	private final Paint WEEK_OF_YEAR_RECT_PAINT;

	private int START_INDEX;
	private int END_INDEX;
	private int ROWS_COUNT = 0;
	private final int DATE_DAY_SPACE_HEIGHT;
	private final int TEXT_SIZE;
	private final int SPACING_BETWEEN_TEXT;

	public static final int MAX_ROWS_COUNT = 8;
	public static final int MAX_INSTANCE_ROW_INDEX = 6;
	public static final int MORE_ROW_INDEX = 7;
	public static final int MAX_ROW_INDEX = 7;

	private Date[] daysOfWeek;

	private HeaderInstancesView headerInstancesView;

	private List<EventData> eventCellsList = new ArrayList<>();
	private OnEventItemClickListener onEventItemClickListener;
	private OnEventItemLongClickListener onEventItemLongClickListener;
	private SparseArray<ItemCell> ITEM_LAYOUT_CELLS = new SparseArray<>(7);
	private List<ContentValues> instances;

	public WeekHeaderView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);

		setBackgroundColor(Color.WHITE);
		setWillNotDraw(false);

		SPACING_BETWEEN_TEXT = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, getResources().getDisplayMetrics());
		TEXT_SIZE = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13f, getResources().getDisplayMetrics());
		DATE_DAY_SPACE_HEIGHT = TEXT_SIZE * 2 + SPACING_BETWEEN_TEXT * 3;

		// 날짜 paint
		DATE_TEXT_PAINT = new TextPaint();
		DATE_TEXT_PAINT.setTextAlign(Paint.Align.CENTER);
		DATE_TEXT_PAINT.setColor(Color.GRAY);
		DATE_TEXT_PAINT.setTextSize(TEXT_SIZE);

		// 요일 paint
		DAY_TEXT_PAINT = new TextPaint();
		DAY_TEXT_PAINT.setTextAlign(Paint.Align.CENTER);
		DAY_TEXT_PAINT.setColor(Color.GRAY);
		DAY_TEXT_PAINT.setTextSize(TEXT_SIZE);

		// 주차 paint
		WEEK_OF_YEAR_TEXT_PAINT = new TextPaint();
		WEEK_OF_YEAR_TEXT_PAINT.setTextAlign(Paint.Align.CENTER);
		WEEK_OF_YEAR_TEXT_PAINT.setColor(Color.WHITE);
		WEEK_OF_YEAR_TEXT_PAINT.setTextSize(TEXT_SIZE);

		WEEK_OF_YEAR_RECT_PAINT = new Paint();
		WEEK_OF_YEAR_RECT_PAINT.setColor(Color.GRAY);
		WEEK_OF_YEAR_RECT_PAINT.setAntiAlias(true);
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = (int) (DATE_DAY_SPACE_HEIGHT + headerInstancesView.VIEW_HEIGHT * ROWS_COUNT);
		if (ROWS_COUNT >= 2) {
			height += headerInstancesView.SPACING_BETWEEN_INSTANCE_VIEWS * (ROWS_COUNT - 1);
		}

		setMeasuredDimension(widthMeasureSpec, height);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		if (getChildCount() > 0) {
			getChildAt(0).measure(getWidth() - getWidth() / 8, (int) (getHeight() - DATE_DAY_SPACE_HEIGHT));
			getChildAt(0).layout(getWidth() / 8, (int) DATE_DAY_SPACE_HEIGHT, getWidth(), getHeight());
		}
		COLUMN_WIDTH = getWidth() / 8;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawHeaderView(canvas);
	}

	private void drawHeaderView(Canvas canvas) {
		// 몇 번째 주인지 표시
		if (App.isPreference_key_show_week_of_year()) {
			RectF weekOfYearRect = new RectF();
			weekOfYearRect.left = COLUMN_WIDTH * (1f / 4f);
			weekOfYearRect.right = COLUMN_WIDTH * (3f / 4f);
			weekOfYearRect.top = (DATE_DAY_SPACE_HEIGHT - TEXT_SIZE) / 2;
			weekOfYearRect.bottom = weekOfYearRect.top + TEXT_SIZE;

			Rect rect = new Rect();
			WEEK_OF_YEAR_TEXT_PAINT.getTextBounds("0", 0, 1, rect);

			canvas.drawRoundRect(weekOfYearRect, 10, 10, WEEK_OF_YEAR_RECT_PAINT);
			canvas.drawText(Integer.toString(weekFirstDay.get(Calendar.WEEK_OF_YEAR)),
					weekOfYearRect.centerX(), weekOfYearRect.centerY() + rect.height() / 2f, WEEK_OF_YEAR_TEXT_PAINT);
		}

		final float dayY = SPACING_BETWEEN_TEXT + TEXT_SIZE - DAY_TEXT_PAINT.descent();
		final float dateY = dayY + DAY_TEXT_PAINT.descent() + SPACING_BETWEEN_TEXT + TEXT_SIZE - DATE_TEXT_PAINT.descent();

		// 요일, 날짜를 그림
		for (int i = 0; i < 7; i++) {
			canvas.drawText(DateHour.getDayString(i), COLUMN_WIDTH + COLUMN_WIDTH / 2 + COLUMN_WIDTH * i, dayY, DAY_TEXT_PAINT);
			canvas.drawText(DateHour.getDate(daysOfWeek[i]), COLUMN_WIDTH + COLUMN_WIDTH / 2 + COLUMN_WIDTH * i, dateY, DATE_TEXT_PAINT);
		}
	}


	public void setDaysOfWeek(Date[] daysOfWeek) {
		// 뷰 설정
		// 이번 주의 날짜 배열 생성
		this.daysOfWeek = daysOfWeek;
		// 주 첫번째 요일(일요일)과 마지막 요일(토요일) 설정
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(daysOfWeek[0]);
		weekFirstDay = (Calendar) calendar.clone();

		calendar.setTime(daysOfWeek[7]);
		weekLastDay = (Calendar) calendar.clone();
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
		// 이벤트 테이블에 데이터를 표시할 위치 설정
		this.instances = instances;
	}

	@Override
	public void setEventTable() {
		ITEM_LAYOUT_CELLS.clear();
		eventCellsList.clear();
		ROWS_COUNT = 0;
		removeAllViews();

		headerInstancesView = new HeaderInstancesView(getContext(), onEventItemClickListener, onEventItemLongClickListener);
		headerInstancesView.setStartDate(weekFirstDay.getTime());
		headerInstancesView.setEndDate(weekLastDay.getTime());
		addView(headerInstancesView);

		START_INDEX = Integer.MAX_VALUE;
		END_INDEX = Integer.MIN_VALUE;
		final boolean showCanceledInstance = App.isPreference_key_show_canceled_instances();

		// 달력 뷰의 셀에 아이템을 삽입
		for (ContentValues instance : instances) {
			if (!showCanceledInstance) {
				if (instance.getAsInteger(CalendarContract.Instances.STATUS) ==
						CalendarContract.Instances.STATUS_CANCELED) {
					// 취소(초대 거부)된 인스턴스인 경우
					continue;
				}
			}
			//일요일과의 일수차이 계산
			int startIndex = ClockUtil.calcBeginDayDifference(instance.getAsLong(CalendarContract.Instances.BEGIN), weekFirstDay.getTimeInMillis());
			int endIndex = ClockUtil.calcEndDayDifference(instance.getAsLong(CalendarContract.Instances.END), weekFirstDay.getTimeInMillis(), instance.getAsBoolean(CalendarContract.Instances.ALL_DAY));

			if (startIndex < 0) {
				startIndex = 0;
			}

			if (endIndex >= 7) {
				endIndex = 6;
			} else if (endIndex < 0) {
				continue;
			}

			int[] margin = EventUtil.getViewSideMargin(instance.getAsLong(CalendarContract.Instances.BEGIN)
					, instance.getAsLong(CalendarContract.Instances.END)
					, daysOfWeek[startIndex].getTime()
					, daysOfWeek[endIndex + 1].getTime(), 8, instance.getAsBoolean(CalendarContract.Instances.ALL_DAY));

			if (START_INDEX >= startIndex) {
				START_INDEX = startIndex;
			}
			if (END_INDEX <= endIndex) {
				END_INDEX = endIndex;
			}
			// 이벤트를 위치시킬 알맞은 행을 지정
			// startDate부터 endDate까지 공통적으로 비어있는 행을 지정한다.
			TreeSet<Integer> rowSet = new TreeSet<>();

			for (int index = startIndex; index <= endIndex; index++) {
				if (ITEM_LAYOUT_CELLS.get(index) == null) {
					ITEM_LAYOUT_CELLS.put(index, new ItemCell());
				}
				// 이벤트 개수 증가
				ITEM_LAYOUT_CELLS.get(index).eventsCount++;

				Set<Integer> set = new HashSet<>();

				for (int row = 0; row <= MAX_ROW_INDEX; row++) {
					if (!ITEM_LAYOUT_CELLS.get(index).rows[row]) {
						set.add(row);
					}
				}

				if (index == startIndex) {
					rowSet.addAll(set);
				} else {
					rowSet.retainAll(set);
				}
			}

			if (!rowSet.isEmpty()) {
				final int bestRow = rowSet.first();

				if (bestRow >= ROWS_COUNT) {
					ROWS_COUNT = bestRow;
				}

				if (!rowSet.isEmpty()) {
					// 셀에 삽입된 아이템의 위치를 알맞게 조정
					// 같은 일정은 같은 위치의 셀에 있어야 한다.
					// row가 MonthCalendarItemView.EVENT_COUNT - 1인 경우 빈 객체를 저장
					for (int i = startIndex; i <= endIndex; i++) {
						ITEM_LAYOUT_CELLS.get(i).rows[bestRow] = true;
					}

					if (bestRow == MORE_ROW_INDEX) {
						margin[0] = 0;
						margin[1] = 0;
					}

					EventData eventData = new EventData(instance, startIndex, endIndex, bestRow);
					eventData.setType(EventData.WEEK);
					eventData.setLeftMargin(margin[0]);
					eventData.setRightMargin(margin[1]);
					eventCellsList.add(eventData);

					InstanceView instanceView = new InstanceView(getContext());

					instanceView.init(bestRow == MORE_ROW_INDEX ? new ContentValues() : eventData.getEvent());
					headerInstancesView.addView(instanceView);
				}
			}
		}
		headerInstancesView.setEventCellsList(eventCellsList);
		if (ROWS_COUNT != 0) {
			ROWS_COUNT++;
		} else if (headerInstancesView.getChildCount() > 0) {
			ROWS_COUNT++;
		}

		requestLayout();
		invalidate();
	}

	@Override
	public void refresh() {

	}


	class ItemCell {
		boolean[] rows;
		int eventsCount;

		public ItemCell() {
			rows = new boolean[MAX_ROWS_COUNT];
		}
	}
}
