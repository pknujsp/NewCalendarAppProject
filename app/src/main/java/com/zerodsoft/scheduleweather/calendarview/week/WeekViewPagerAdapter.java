package com.zerodsoft.scheduleweather.calendarview.week;

import android.content.ContentValues;
import android.util.ArraySet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendarview.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IControlEvent;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IToolbar;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnDateTimeChangedListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarview.interfaces.DateGetter;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemLongClickListener;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class WeekViewPagerAdapter extends RecyclerView.Adapter<WeekViewPagerAdapter.WeekViewPagerHolder> implements DateGetter, OnDateTimeChangedListener {
	private SparseArray<WeekViewPagerHolder> holderSparseArray = new SparseArray<>();
	private final Calendar CALENDAR;
	private final Date currentDateTime;
	private final IToolbar iToolbar;
	private final IControlEvent iControlEvent;
	private final OnEventItemClickListener onEventItemClickListener;
	private final IConnectedCalendars iConnectedCalendars;
	private final OnEventItemLongClickListener onEventItemLongClickListener;
	private Set<WeekViewPagerHolder> holderSet = new ArraySet<>();


	public WeekViewPagerAdapter(IControlEvent iControlEvent, OnEventItemLongClickListener onEventItemLongClickListener, OnEventItemClickListener onEventItemClickListener, IToolbar iToolbar, IConnectedCalendars iConnectedCalendars) {
		this.onEventItemLongClickListener = onEventItemLongClickListener;
		this.iControlEvent = iControlEvent;
		this.iToolbar = iToolbar;
		this.onEventItemClickListener = onEventItemClickListener;
		this.iConnectedCalendars = iConnectedCalendars;
		CALENDAR = Calendar.getInstance(ClockUtil.TIME_ZONE);
		currentDateTime = CALENDAR.getTime();

		// 날짜를 이번 주 일요일 0시 0분으로 설정
		int amount = -(CALENDAR.get(Calendar.DAY_OF_WEEK) - 1);

		CALENDAR.add(Calendar.DAY_OF_YEAR, amount);
		CALENDAR.set(Calendar.HOUR_OF_DAY, 0);
		CALENDAR.set(Calendar.MINUTE, 0);
		CALENDAR.set(Calendar.SECOND, 0);
		CALENDAR.set(Calendar.MILLISECOND, 0);

		iToolbar.setMonth(CALENDAR.getTime());
	}

	public Calendar getCALENDAR() {
		return (Calendar) CALENDAR.clone();
	}

	public Date getCurrentDateTime() {
		return currentDateTime;
	}

	@NonNull
	@Override
	public WeekViewPagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new WeekViewPagerHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.weekview_viewpager_item, parent, false));
	}

	@Override
	public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
		super.onAttachedToRecyclerView(recyclerView);
	}

	@Override
	public void onBindViewHolder(@NonNull WeekViewPagerHolder holder, int position) {
		holder.onBind();
		holderSparseArray.put(holder.getBindingAdapterPosition(), holder);
		holderSet.add(holder);
	}

	@Override
	public void onViewRecycled(@NonNull WeekViewPagerHolder holder) {
		holderSparseArray.remove(holder.getOldPosition());
		holderSet.remove(holder);
		super.onViewRecycled(holder);
	}

	@Override
	public int getItemCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public Date getDate(int position, int index) {
		return holderSparseArray.get(position).weekCalendarView.getDaysOfWeek()[index];
	}

	public void refresh(int position) {
		holderSparseArray.get(position).weekCalendarView.refresh();
	}

	@Override
	public void receivedTimeTick(Date date) {
		for (WeekViewPagerHolder holder : holderSet) {
			holder.weekView.receivedTimeTick(date);
		}
	}

	@Override
	public void receivedDateChanged(Date date) {
		receivedTimeTick(date);
	}

	class WeekViewPagerHolder extends RecyclerView.ViewHolder {
		private WeekCalendarView weekCalendarView;
		private WeekView weekView;

		public WeekViewPagerHolder(View view) {
			super(view);
			weekView = (WeekView) view.findViewById(R.id.week_view);
			WeekHeaderView weekHeaderView = (WeekHeaderView) view.findViewById(R.id.week_header);
			weekCalendarView = new WeekCalendarView(weekHeaderView, weekView);
		}

		public void onBind() {

			Calendar copiedCalendar = (Calendar) CALENDAR.clone();
			copiedCalendar.add(Calendar.WEEK_OF_YEAR, getBindingAdapterPosition() - EventTransactionFragment.FIRST_VIEW_POSITION);
			weekCalendarView.init(copiedCalendar, onEventItemLongClickListener, onEventItemClickListener, iControlEvent, iConnectedCalendars);
		}

	}

}