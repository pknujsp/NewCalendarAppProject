package com.zerodsoft.calendarplatform.calendarview.day;

import android.content.ContentValues;

import com.zerodsoft.calendarplatform.calendar.dto.CalendarInstance;
import com.zerodsoft.calendarplatform.calendarview.interfaces.CalendarViewInitializer;
import com.zerodsoft.calendarplatform.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.calendarplatform.calendarview.interfaces.IControlEvent;
import com.zerodsoft.calendarplatform.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.calendarplatform.calendarview.interfaces.OnEventItemLongClickListener;
import com.zerodsoft.calendarplatform.event.util.EventUtil;
import com.zerodsoft.calendarplatform.room.dto.SelectedCalendarDTO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DayCalendarView implements CalendarViewInitializer {
	private DayHeaderView dayHeaderView;
	private DayView dayView;
	private IControlEvent iControlEvent;
	private IConnectedCalendars iConnectedCalendars;
	private Map<Integer, CalendarInstance> resultMap;

	private Date viewStartDate;
	private Date viewEndDate;

	public DayCalendarView(DayHeaderView dayHeaderView, DayView dayView) {
		this.dayHeaderView = dayHeaderView;
		this.dayView = dayView;
	}

	public Date getViewStartDate() {
		return viewStartDate;
	}

	public Date getViewEndDate() {
		return viewEndDate;
	}


	@Override
	public void init(Calendar copiedCalendar, OnEventItemLongClickListener onEventItemLongClickListener, OnEventItemClickListener onEventItemClickListener, IControlEvent iControlEvent, IConnectedCalendars iConnectedCalendars) {
		this.iConnectedCalendars = iConnectedCalendars;
		this.iControlEvent = iControlEvent;

		dayHeaderView.init(null, onEventItemLongClickListener, onEventItemClickListener, null, null);
		dayView.init(null, onEventItemLongClickListener, onEventItemClickListener, null, null);

		viewStartDate = copiedCalendar.getTime();
		copiedCalendar.add(Calendar.DATE, 1);
		viewEndDate = copiedCalendar.getTime();

		dayHeaderView.setDates(viewStartDate, viewEndDate);
		dayView.setDates(viewStartDate, viewEndDate);

		setInstances(iControlEvent.getInstances(viewStartDate.getTime(), viewEndDate.getTime()));
		setEventTable();
	}

	@Override
	public void setInstances(Map<Integer, CalendarInstance> resultMap) {
		//선택되지 않은 캘린더는 제외
		this.resultMap = resultMap;
		List<SelectedCalendarDTO> connectedCalendars = iConnectedCalendars.getConnectedCalendars();
		Set<Integer> connectedCalendarIdSet = new HashSet<>();

		for (SelectedCalendarDTO calendar : connectedCalendars) {
			connectedCalendarIdSet.add(calendar.getCalendarId());
		}

		List<ContentValues> instances = new ArrayList<>();
		for (Integer calendarIdKey : connectedCalendarIdSet) {
			if (resultMap.containsKey(calendarIdKey)) {
				instances.addAll(resultMap.get(calendarIdKey).getInstanceList());
			}
		}

		// 데이터를 일정 길이의 내림차순으로 정렬
		instances.sort(EventUtil.INSTANCE_COMPARATOR);

		dayHeaderView.setInstances(instances);
		dayView.setInstances(instances);
	}

	@Override
	public void setInstances(List<ContentValues> instances) {

	}

	@Override
	public void setEventTable() {
		dayHeaderView.setEventTable();
		dayView.setEventTable();
	}

	public void refresh() {
		setInstances(iControlEvent.getInstances(viewStartDate.getTime(), viewEndDate.getTime()));
		setEventTable();
	}
}
