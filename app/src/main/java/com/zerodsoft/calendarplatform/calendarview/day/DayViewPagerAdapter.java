package com.zerodsoft.calendarplatform.calendarview.day;

import android.util.ArraySet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.calendarview.EventTransactionFragment;
import com.zerodsoft.calendarplatform.calendarview.interfaces.DateGetter;
import com.zerodsoft.calendarplatform.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.calendarplatform.calendarview.interfaces.IControlEvent;
import com.zerodsoft.calendarplatform.calendarview.interfaces.IToolbar;
import com.zerodsoft.calendarplatform.calendarview.interfaces.OnDateTimeChangedListener;
import com.zerodsoft.calendarplatform.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.calendarplatform.calendarview.interfaces.OnEventItemLongClickListener;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;


public class DayViewPagerAdapter extends RecyclerView.Adapter<DayViewPagerAdapter.DayViewPagerHolder> implements DateGetter, OnDateTimeChangedListener {
	private final OnEventItemClickListener onEventItemClickListener;
	private final OnEventItemLongClickListener onEventItemLongClickListener;
	private final IControlEvent iControlEvent;
	private final IToolbar iToolbar;
	private final IConnectedCalendars iConnectedCalendars;
	private final Date currentDateTime;

	private SparseArray<DayViewPagerHolder> holderSparseArray = new SparseArray<>();
	private Set<DayViewPagerHolder> holderSet = new ArraySet<>();
	private final Calendar CALENDAR;

	public static final int FIRST_DAY = -1;
	public static final int LAST_DAY = -2;

	public DayViewPagerAdapter(IControlEvent iControlEvent, OnEventItemLongClickListener onEventItemLongClickListener, OnEventItemClickListener onEventItemClickListener, IToolbar iToolbar, IConnectedCalendars iConnectedCalendars) {
		this.onEventItemClickListener = onEventItemClickListener;
		this.iControlEvent = iControlEvent;
		this.iToolbar = iToolbar;
		this.iConnectedCalendars = iConnectedCalendars;
		this.onEventItemLongClickListener = onEventItemLongClickListener;

		CALENDAR = Calendar.getInstance();
		currentDateTime = CALENDAR.getTime();
		// 날짜를 오늘 0시0분0초로 설정
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
	public DayViewPagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new DayViewPagerHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dayview_viewpager_item, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull DayViewPagerHolder holder, int position) {
		holder.onBind();
		holderSparseArray.put(holder.getBindingAdapterPosition(), holder);
		holderSet.add(holder);
	}

	@Override
	public void onViewAttachedToWindow(@NonNull DayViewPagerHolder holder) {
		super.onViewAttachedToWindow(holder);
	}

	@Override
	public void onViewDetachedFromWindow(@NonNull DayViewPagerHolder holder) {
		super.onViewDetachedFromWindow(holder);
	}

	@Override
	public void onViewRecycled(@NonNull DayViewPagerHolder holder) {
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
		if (index == FIRST_DAY) {
			return holderSparseArray.get(position).dayCalendarView.getViewStartDate();
		} else {
			return holderSparseArray.get(position).dayCalendarView.getViewEndDate();
		}
	}

	@Override
	public void receivedTimeTick(Date date) {
		for (DayViewPagerHolder holder : holderSet) {
			holder.dayView.receivedTimeTick(date);
		}
	}

	@Override
	public void receivedDateChanged(Date date) {
		receivedTimeTick(date);
	}

	class DayViewPagerHolder extends RecyclerView.ViewHolder {
		private final DayCalendarView dayCalendarView;
		private final DayView dayView;

		public DayViewPagerHolder(View view) {
			super(view);
			dayView = (DayView) view.findViewById(R.id.dayview);
			dayCalendarView = new DayCalendarView((DayHeaderView) view.findViewById(R.id.dayheaderview), dayView);
		}

		public void onBind() {
			Calendar copiedCalendar = (Calendar) CALENDAR.clone();
			copiedCalendar.add(Calendar.DATE, getBindingAdapterPosition() - EventTransactionFragment.FIRST_VIEW_POSITION);
			dayCalendarView.init(copiedCalendar, onEventItemLongClickListener, onEventItemClickListener, iControlEvent, iConnectedCalendars);
		}

	}

}
