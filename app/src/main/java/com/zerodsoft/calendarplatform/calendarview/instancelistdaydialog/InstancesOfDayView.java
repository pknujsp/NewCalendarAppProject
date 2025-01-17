package com.zerodsoft.calendarplatform.calendarview.instancelistdaydialog;

import android.content.ContentValues;
import android.content.Context;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.calendar.dto.CalendarInstance;
import com.zerodsoft.calendarplatform.calendarview.instancelistdaydialog.adapter.EventsInfoRecyclerViewAdapter;
import com.zerodsoft.calendarplatform.calendarview.interfaces.CalendarViewInitializer;
import com.zerodsoft.calendarplatform.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.calendarplatform.calendarview.interfaces.IControlEvent;
import com.zerodsoft.calendarplatform.calendarview.interfaces.IRefreshView;
import com.zerodsoft.calendarplatform.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.calendarplatform.calendarview.interfaces.OnEventItemLongClickListener;
import com.zerodsoft.calendarplatform.common.interfaces.OnViewPagerPageListener;
import com.zerodsoft.calendarplatform.common.view.CustomProgressView;
import com.zerodsoft.calendarplatform.common.view.RecyclerViewItemDecoration;
import com.zerodsoft.calendarplatform.event.util.EventUtil;
import com.zerodsoft.calendarplatform.room.dto.SelectedCalendarDTO;
import com.zerodsoft.calendarplatform.utility.ClockUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InstancesOfDayView implements CalendarViewInitializer, OnViewPagerPageListener {
	private EventsInfoRecyclerViewAdapter adapter;
	private TextView dayTextView;
	private RecyclerView recyclerView;
	private ImageButton moreButton;
	private TextView deleteInstancesBtn;
	private Context context;
	private CustomProgressView customProgressView;

	private OnEventItemClickListener onEventItemClickListener;
	private OnEventItemLongClickListener onEventItemLongClickListener;
	private IConnectedCalendars iConnectedCalendars;
	private IControlEvent iControlEvent;
	private InstanceDialogMenuListener instanceDialogMenuListener;
	private IRefreshView iRefreshView;
	private DeleteEventsListener deleteEventsListener;

	private boolean moreBtnChecked = false;

	private Long begin;
	private Long end;

	private Set<ContentValues> checkedInstanceSet = new HashSet<>();

	@Override
	public void onPageChanged() {
		if (moreBtnChecked) {
			moreButton.callOnClick();
		}
	}

	@Override
	public void onPageChanged(int position) {

	}

	public InstancesOfDayView(View view) {
		context = view.getContext();
		dayTextView = (TextView) view.findViewById(R.id.events_info_day);
		moreButton = (ImageButton) view.findViewById(R.id.more_button);
		recyclerView = (RecyclerView) view.findViewById(R.id.events_info_events_list);
		deleteInstancesBtn = (TextView) view.findViewById(R.id.delete_instances);
		customProgressView = (CustomProgressView) view.findViewById(R.id.custom_progress_view);

		customProgressView.setContentView(recyclerView);
		customProgressView.onSuccessfulProcessingData();

		recyclerView.addItemDecoration(new RecyclerViewItemDecoration(context, 8));
		recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

		deleteInstancesBtn.setVisibility(View.GONE);

		moreButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (adapter == null) {
					return;
				}

				if (adapter.getInstances().isEmpty()) {
					Toast.makeText(view.getContext(), R.string.empty_events, Toast.LENGTH_SHORT).show();
					return;
				}
				moreBtnChecked = !moreBtnChecked;

				if (moreBtnChecked) {
					deleteInstancesBtn.setVisibility(View.VISIBLE);
					adapter.setCheckBoxVisibility(View.VISIBLE);
				} else {
					deleteInstancesBtn.setVisibility(View.GONE);
					adapter.setCheckBoxVisibility(View.GONE);
				}
				checkedInstanceSet.clear();
				adapter.notifyDataSetChanged();
			}
		});

		deleteInstancesBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (checkedInstanceSet.isEmpty()) {
					Toast.makeText(context, R.string.not_checked_favorite_locations, Toast.LENGTH_SHORT).show();
				} else {
					//db에서 삭제
					deleteEventsListener.deleteEvents(checkedInstanceSet);
					moreButton.callOnClick();
				}
			}
		});

	}

	public void init(OnEventItemLongClickListener onEventItemLongClickListener, OnEventItemClickListener onEventItemClickListener
			, IControlEvent iControlEvent, IConnectedCalendars iConnectedCalendars, InstanceDialogMenuListener instanceDialogMenuListener, IRefreshView iRefreshView, DeleteEventsListener deleteEventsListener) {
		this.onEventItemClickListener = onEventItemClickListener;
		this.onEventItemLongClickListener = onEventItemLongClickListener;
		this.iConnectedCalendars = iConnectedCalendars;
		this.iControlEvent = iControlEvent;
		this.instanceDialogMenuListener = instanceDialogMenuListener;
		this.iRefreshView = iRefreshView;
		this.deleteEventsListener = deleteEventsListener;
	}

	@Override
	public void init(Calendar copiedCalendar, OnEventItemLongClickListener onEventItemLongClickListener, OnEventItemClickListener onEventItemClickListener, IControlEvent iControlEvent, IConnectedCalendars iConnectedCalendars) {

	}

	public void init(Calendar copiedCalendar) {
		begin = copiedCalendar.getTimeInMillis();
		copiedCalendar.add(Calendar.DATE, 1);
		end = copiedCalendar.getTimeInMillis();

		if (moreBtnChecked) {
			moreBtnChecked = false;
			deleteInstancesBtn.setVisibility(View.GONE);
			adapter.setCheckBoxVisibility(View.GONE);
			checkedInstanceSet.clear();
		}

		dayTextView.setText(ClockUtil.YYYY_M_D_E.format(begin));
		adapter = new EventsInfoRecyclerViewAdapter(onEventItemLongClickListener, onEventItemClickListener, checkBoxOnCheckedChangeListener, begin, end);
		adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
				if (adapter.getItemCount() == 0) {
					customProgressView.onFailedProcessingData(context.getString(R.string.empty_events));
				} else {
					customProgressView.onSuccessfulProcessingData();
				}
			}

		});
		recyclerView.setAdapter(adapter);

		setData(iControlEvent.getInstances(begin, end));
		checkedInstanceSet.clear();
	}

	private final CompoundButton.OnCheckedChangeListener checkBoxOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
			EventsInfoRecyclerViewAdapter.InstanceTagHolder holder = (EventsInfoRecyclerViewAdapter.InstanceTagHolder) compoundButton.getTag();

			if (b) {
				checkedInstanceSet.add(holder.instance);
			} else {
				checkedInstanceSet.remove(holder.instance);
			}
		}
	};

	@Override
	public void setInstances(Map<Integer, CalendarInstance> resultMap) {

	}

	@Override
	public void setInstances(List<ContentValues> instances) {

	}

	@Override
	public void setEventTable() {

	}

	private void setData(Map<Integer, CalendarInstance> resultMap) {
         /* 현재 날짜가 20201010이고, 20201009에 allday 인스턴스가 있는 경우에 이 인스턴스의 end값이 20201010 0시 0분
              이라서 20201010의 인스턴스로 잡힌다.
         */
		//선택되지 않은 캘린더는 제외
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
		List<Integer> removeIndexList = new ArrayList<>();

		for (int i = 0; i < instances.size(); i++) {
			if (instances.get(i).getAsBoolean(CalendarContract.Instances.ALL_DAY)) {
				if (ClockUtil.areSameDate(instances.get(i).getAsLong(CalendarContract.Instances.END),
						begin) || ClockUtil.areSameDate(instances.get(i).getAsLong(CalendarContract.Instances.BEGIN), end)) {
					removeIndexList.add(i);
				}
			} else {
				if (ClockUtil.areSameHourMinute(instances.get(i).getAsLong(CalendarContract.Instances.END), begin)
						|| ClockUtil.areSameHourMinute(instances.get(i).getAsLong(CalendarContract.Instances.BEGIN), end)) {
					removeIndexList.add(i);
				}
			}
		}

		for (int i = removeIndexList.size() - 1; i >= 0; i--) {
			instances.remove(removeIndexList.get(i).intValue());
		}

		Collections.sort(instances, EventUtil.INSTANCE_COMPARATOR);
		adapter.setInstances(instances);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void refresh() {
	}


	public interface InstanceDialogMenuListener {
		void showPopupMenu(ContentValues instance, View anchorView, int gravity);
	}

	public interface DeleteEventsListener {
		void deleteEvents(Set<ContentValues> instanceSet);
	}
}
