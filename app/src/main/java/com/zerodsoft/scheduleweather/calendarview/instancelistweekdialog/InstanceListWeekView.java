package com.zerodsoft.scheduleweather.calendarview.instancelistweekdialog;

import android.content.ContentValues;
import android.content.Context;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Instances;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendarview.instancelistdaydialog.InstancesOfDayView;
import com.zerodsoft.scheduleweather.calendarview.instancelistdaydialog.adapter.EventsInfoRecyclerViewAdapter;
import com.zerodsoft.scheduleweather.calendarview.interfaces.CalendarViewInitializer;
import com.zerodsoft.scheduleweather.calendarview.interfaces.DateGetter;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IControlEvent;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IRefreshView;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemLongClickListener;
import com.zerodsoft.scheduleweather.common.interfaces.OnViewPagerPageListener;
import com.zerodsoft.scheduleweather.common.view.CustomProgressView;
import com.zerodsoft.scheduleweather.common.view.RecyclerViewItemDecoration;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.room.dto.SelectedCalendarDTO;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InstanceListWeekView extends LinearLayout implements CalendarViewInitializer, OnViewPagerPageListener {
	private TextView monthTextView;
	private RecyclerView instanceRecyclerView;

	private InstanceListAdapter adapter;

	private OnEventItemClickListener onEventItemClickListener;
	private OnEventItemLongClickListener onEventItemLongClickListener;
	private IConnectedCalendars iConnectedCalendars;
	private IControlEvent iControlEvent;

	private Long begin;
	private Long end;

	public InstanceListWeekView(@NonNull @NotNull Context context, OnEventItemLongClickListener onEventItemLongClickListener, OnEventItemClickListener onEventItemClickListener
			, IControlEvent iControlEvent, IConnectedCalendars iConnectedCalendars) {
		super(context);
		this.onEventItemClickListener = onEventItemClickListener;
		this.onEventItemLongClickListener = onEventItemLongClickListener;
		this.iConnectedCalendars = iConnectedCalendars;
		this.iControlEvent = iControlEvent;

		initView();
	}


	private void initView() {
		setOrientation(VERTICAL);
		setClickable(true);
		setFocusable(false);
		setBackground(ContextCompat.getDrawable(getContext(), R.drawable.map_view_background));
		setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

		monthTextView = new TextView(getContext(), null, R.style.TextAppearance_MaterialComponents_Headline6);
		int monthTextViewPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, getResources().getDisplayMetrics());
		monthTextView.setPadding(monthTextViewPadding, monthTextViewPadding, monthTextViewPadding, monthTextViewPadding);
		monthTextView.setGravity(Gravity.CENTER);
		LinearLayout.LayoutParams monthTextViewLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		monthTextViewLayoutParams.gravity = Gravity.CENTER;

		addView(monthTextView, monthTextViewLayoutParams);

		instanceRecyclerView = new RecyclerView(getContext());
		instanceRecyclerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		instanceRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
		instanceRecyclerView.addItemDecoration(new RecyclerViewItemDecoration(getContext(), 14));

		addView(instanceRecyclerView);
	}

	public void init(Calendar copiedCalendar) {
		begin = copiedCalendar.getTimeInMillis();
		copiedCalendar.add(Calendar.DATE, 7);
		end = copiedCalendar.getTimeInMillis();

		copiedCalendar.setTimeInMillis(begin);
		String date = ClockUtil.YEAR_MONTH_FORMAT.format(copiedCalendar.getTime());
		date += " " + copiedCalendar.get(Calendar.WEEK_OF_YEAR) + getContext().getString(R.string.week);

		monthTextView.setText(date);

		final List<List<InstanceValues>> instancesList = new ArrayList<>();
		final List<Date> dateList = new ArrayList<>();

		for (int i = 0; i < 7; i++) {
			dateList.add(copiedCalendar.getTime());
			instancesList.add(new ArrayList<>());
			copiedCalendar.add(Calendar.DATE, 1);
		}

		dateList.add(copiedCalendar.getTime());

		List<SelectedCalendarDTO> connectedCalendars = iConnectedCalendars.getConnectedCalendars();
		Set<Integer> connectedCalendarIdSet = new HashSet<>();

		for (SelectedCalendarDTO selectedCalendarDTO : connectedCalendars) {
			connectedCalendarIdSet.add(selectedCalendarDTO.getCalendarId());
		}

		Map<Integer, CalendarInstance> integerCalendarInstanceMap = iControlEvent.getInstances(begin, end);

		Calendar beginCalendar = Calendar.getInstance();
		Calendar endCalendar = Calendar.getInstance();

		final int marginLR = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13f, getResources().getDisplayMetrics());
		int[] margins = null;
		boolean allDay = false;

		for (Integer calendarIdKey : connectedCalendarIdSet) {
			if (integerCalendarInstanceMap.containsKey(calendarIdKey)) {
				List<ContentValues> instanceList = integerCalendarInstanceMap.get(calendarIdKey).getInstanceList();

				for (ContentValues instance : instanceList) {
					beginCalendar.setTimeInMillis(instance.getAsLong(Instances.BEGIN));
					endCalendar.setTimeInMillis(instance.getAsLong(Instances.END));
					allDay = instance.getAsInteger(Instances.ALL_DAY) == 1;


					//일요일과의 일수차이 계산
					int startIndex = ClockUtil.calcBeginDayDifference(beginCalendar.getTimeInMillis(), begin);
					int endIndex = ClockUtil.calcEndDayDifference(endCalendar.getTimeInMillis(), begin, allDay);

					if (startIndex < 0) {
						startIndex = 0;
					}

					if (endIndex >= 7) {
						endIndex = 6;
					} else if (endIndex < 0) {
						continue;
					}

					for (int index = startIndex; index <= endIndex; index++) {
						margins = EventUtil.getViewSideMargin(beginCalendar.getTimeInMillis(),
								endCalendar.getTimeInMillis()
								, dateList.get(index).getTime()
								, dateList.get(index + 1).getTime(), marginLR, allDay);
						InstanceValues instanceValues = new InstanceValues(margins[0], margins[1], instance);
						instancesList.get(index).add(instanceValues);
					}


				}
			}
		}

		adapter = new InstanceListAdapter(onEventItemClickListener, onEventItemLongClickListener, begin, end);
		adapter.setInstancesList(instancesList);
		adapter.setDateList(dateList);
		instanceRecyclerView.setAdapter(adapter);
	}


	public void clear() {
	}

	@Override
	public void init(Calendar copiedCalendar, OnEventItemLongClickListener onEventItemLongClickListener, OnEventItemClickListener onEventItemClickListener, IControlEvent iControlEvent, IConnectedCalendars iConnectedCalendars) {

	}

	@Override
	public void setInstances(Map<Integer, CalendarInstance> resultMap) {

	}

	@Override
	public void setInstances(List<ContentValues> instances) {

	}

	@Override
	public void setEventTable() {

	}

	@Override
	public void refresh() {

	}

	@Override
	public void onPageChanged() {

	}

	@Override
	public void onPageChanged(int position) {

	}

	static class InstanceValues {
		final int leftMargin;
		final int rightMargin;
		final ContentValues instance;

		public InstanceValues(int leftMargin, int rightMargin, ContentValues instance) {
			this.leftMargin = leftMargin;
			this.rightMargin = rightMargin;
			this.instance = instance;
		}
	}
}


