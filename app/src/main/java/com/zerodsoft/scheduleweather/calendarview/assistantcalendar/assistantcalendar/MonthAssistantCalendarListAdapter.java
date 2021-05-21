package com.zerodsoft.scheduleweather.calendarview.assistantcalendar.assistantcalendar;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendarview.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarview.interfaces.CalendarDateOnClickListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IControlEvent;
import com.zerodsoft.scheduleweather.calendarview.month.MonthCalendarView;
import com.zerodsoft.scheduleweather.calendarview.month.MonthViewPagerAdapter;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.SneakyThrows;

public class MonthAssistantCalendarListAdapter extends RecyclerView.Adapter<MonthAssistantCalendarListAdapter.MonthAssistantViewHolder> {
	private final IControlEvent iControlEvent;
	private final CalendarDateOnClickListener calendarDateOnClickListener;
	private final Calendar CALENDAR;
	private final IConnectedCalendars iConnectedCalendars;

	public MonthAssistantCalendarListAdapter(IControlEvent iControlEvent, CalendarDateOnClickListener calendarDateOnClickListener, IConnectedCalendars iConnectedCalendars) {
		this.iControlEvent = iControlEvent;
		this.calendarDateOnClickListener = calendarDateOnClickListener;
		this.iConnectedCalendars = iConnectedCalendars;
		CALENDAR = Calendar.getInstance(ClockUtil.TIME_ZONE);

		// 날짜를 이번 달 1일 0시 0분으로 설정
		CALENDAR.set(Calendar.DAY_OF_MONTH, 1);
		CALENDAR.set(Calendar.HOUR_OF_DAY, 0);
		CALENDAR.set(Calendar.MINUTE, 0);
		CALENDAR.set(Calendar.SECOND, 0);
	}

	public Date getAsOfDate() {
		return CALENDAR.getTime();
	}

	@Override
	public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
		super.onAttachedToRecyclerView(recyclerView);
	}

	@NonNull
	@Override
	public MonthAssistantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new MonthAssistantViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.month_assistant_itemview, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull MonthAssistantViewHolder holder, int position) {
		holder.onBind();
	}

	@Override
	public void onViewRecycled(@NonNull MonthAssistantViewHolder holder) {
		super.onViewRecycled(holder);
	}

	@Override
	public int getItemCount() {
		return Integer.MAX_VALUE;
	}


	class MonthAssistantViewHolder extends RecyclerView.ViewHolder {
		private MonthAssistantCalendarView monthCalendarView;

		public MonthAssistantViewHolder(View view) {
			super(view);
			monthCalendarView = (MonthAssistantCalendarView) view.findViewById(R.id.month_assistant_calendar_view);
		}

		public void onBind() {
			Calendar copiedCalendar = (Calendar) CALENDAR.clone();
			copiedCalendar.add(Calendar.MONTH, getBindingAdapterPosition() - EventTransactionFragment.FIRST_VIEW_POSITION);
			monthCalendarView.init(copiedCalendar, calendarDateOnClickListener, iControlEvent, iConnectedCalendars);
		}

	}
}
