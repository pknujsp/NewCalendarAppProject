package com.zerodsoft.calendarplatform.calendarview.day;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.provider.CalendarContract.Instances;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.zerodsoft.calendarplatform.activity.App;
import com.zerodsoft.calendarplatform.calendar.dto.CalendarInstance;
import com.zerodsoft.calendarplatform.calendarview.common.HeaderInstancesView;
import com.zerodsoft.calendarplatform.calendarview.common.InstanceView;
import com.zerodsoft.calendarplatform.calendarview.interfaces.CalendarViewInitializer;
import com.zerodsoft.calendarplatform.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.calendarplatform.calendarview.interfaces.IControlEvent;
import com.zerodsoft.calendarplatform.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.calendarplatform.calendarview.interfaces.OnEventItemLongClickListener;
import com.zerodsoft.calendarplatform.calendarview.month.EventData;
import com.zerodsoft.calendarplatform.event.util.EventUtil;
import com.zerodsoft.calendarplatform.utility.ClockUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DayHeaderView extends ViewGroup implements CalendarViewInitializer {
	private final Paint DAY_DATE_TEXT_PAINT;
	private final float DAY_DATE_SPACE_HEIGHT;
	private final int DAY_DATE_TEXT_HEIGHT;

	public static final int MAX_ROWS_COUNT = 8;
	public static final int MAX_INSTANCE_ROW_INDEX = 6;
	public static final int MORE_ROW_INDEX = 7;
	public static final int MAX_ROW_INDEX = 7;
	private int rowNum;

	private Date viewStartDate;
	private Date viewEndDate;

	private List<EventData> eventCellsList = new ArrayList<>();
	private OnEventItemClickListener onEventItemClickListener;
	private OnEventItemLongClickListener onEventItemLongClickListener;

	private List<ContentValues> instances;
	private HeaderInstancesView headerInstancesView;

	public DayHeaderView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		// 날짜, 요일 paint
		DAY_DATE_TEXT_PAINT = new Paint();
		DAY_DATE_TEXT_PAINT.setTextAlign(Paint.Align.CENTER);
		DAY_DATE_TEXT_PAINT.setColor(Color.BLACK);
		DAY_DATE_TEXT_PAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, context.getResources().getDisplayMetrics()));

		Rect rect = new Rect();
		DAY_DATE_TEXT_PAINT.getTextBounds("1", 0, 1, rect);

		DAY_DATE_TEXT_HEIGHT = rect.height();
		DAY_DATE_SPACE_HEIGHT = DAY_DATE_TEXT_HEIGHT * 2.5f;
		// set background
		setBackgroundColor(Color.WHITE);

		setWillNotDraw(false);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = (int) (DAY_DATE_SPACE_HEIGHT + headerInstancesView.VIEW_HEIGHT * rowNum);
		if (rowNum >= 2) {
			height += headerInstancesView.SPACING_BETWEEN_INSTANCE_VIEWS * (rowNum - 1);
		}
		setMeasuredDimension(widthMeasureSpec, height);
	}

	@Override
	protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
		if (getChildCount() > 0) {
			getChildAt(0).measure(getWidth(), (int) (getHeight() - DAY_DATE_SPACE_HEIGHT));
			getChildAt(0).layout(0, (int) DAY_DATE_SPACE_HEIGHT, getWidth(), getHeight());
		}
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 날짜와 요일 그리기
		canvas.drawText(ClockUtil.D_E.format(viewStartDate), getWidth() / 2, DAY_DATE_SPACE_HEIGHT / 2 + DAY_DATE_TEXT_HEIGHT / 2,
				DAY_DATE_TEXT_PAINT);
	}


	public void setInstances(List<ContentValues> instances) {
		this.instances = instances;
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
		eventCellsList.clear();

		rowNum = 0;
		int availableRow = 0;

		headerInstancesView = new HeaderInstancesView(getContext(), onEventItemClickListener, onEventItemLongClickListener);
		headerInstancesView.setStartDate(viewStartDate);
		headerInstancesView.setEndDate(viewEndDate);
		headerInstancesView.setClickable(true);
		addView(headerInstancesView);

		boolean showCanceledInstance = App.isPreference_key_show_canceled_instances();

		// 달력 뷰의 셀에 아이템을 삽입
		for (ContentValues instance : instances) {
			if (!showCanceledInstance) {
				if (instance.getAsInteger(Instances.STATUS) == Instances.STATUS_CANCELED) {
					// 취소(초대 거부)된 인스턴스인 경우..
					continue;
				}
			}

			if (instance.getAsBoolean(Instances.ALL_DAY)) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(instance.getAsLong(Instances.END));
				calendar.add(Calendar.DAY_OF_YEAR, -1);

				if (viewStartDate.after(calendar.getTime())) {
					continue;
				}
			}
			// 이벤트를 위치시킬 알맞은 행을 지정
			// 비어있는 행을 지정한다.
			// row 추가
			rowNum++;
			if (availableRow < MORE_ROW_INDEX) {
				// 셀에 삽입된 아이템의 위치를 알맞게 조정
				// 같은 일정은 같은 위치의 셀에 있어야 한다.

				int[] margin = EventUtil.getViewSideMargin(instance.getAsLong(Instances.BEGIN)
						, instance.getAsLong(Instances.END)
						, viewStartDate.getTime()
						, viewEndDate.getTime(), 16, instance.getAsBoolean(Instances.ALL_DAY));

				int leftMargin = margin[0];
				int rightMargin = margin[1];

				EventData eventData = new EventData(instance, availableRow);
				eventData.setLeftMargin(leftMargin);
				eventData.setRightMargin(rightMargin);

				eventCellsList.add(eventData);
			} else {
				eventCellsList.add(new EventData(new ContentValues(), availableRow));
				break;
			}
			availableRow++;
		}

		headerInstancesView.setEventCellsList(eventCellsList);

		for (EventData eventData : eventCellsList) {
			InstanceView instanceView = new InstanceView(getContext());
			instanceView.init(eventData.getEvent());
			headerInstancesView.addView(instanceView);
		}

		requestLayout();
		invalidate();
	}

	@Override
	public void refresh() {

	}

	public void setDates(Date viewStartDate, Date viewEndDate) {
		this.viewStartDate = viewStartDate;
		this.viewEndDate = viewEndDate;
	}
}



