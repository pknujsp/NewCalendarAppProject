package com.zerodsoft.scheduleweather.calendarview.week;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.activity.main.AppMainActivity;
import com.zerodsoft.scheduleweather.calendarview.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IControlEvent;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IRefreshView;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IToolbar;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnDateTimeChangedListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEditedEventListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemLongClickListener;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.Calendar;
import java.util.Date;


public class WeekFragment extends Fragment implements IRefreshView, OnDateTimeChangedListener, OnEditedEventListener {
	public static final String TAG = "WEEK_FRAGMENT";
	private ViewPager2 weekViewPager;
	private WeekViewPagerAdapter weekViewPagerAdapter;
	private int currentPosition = EventTransactionFragment.FIRST_VIEW_POSITION;

	private final IControlEvent iControlEvent;
	private final IToolbar iToolbar;
	private final OnEventItemClickListener onEventItemClickListener;
	private final OnEventItemLongClickListener onEventItemLongClickListener;
	private final IConnectedCalendars iConnectedCalendars;

	private static final int COLUMN_WIDTH = AppMainActivity.getDisplayWidth() / 8;

	private OnPageChangeCallback onPageChangeCallback;

	public WeekFragment(Fragment fragment, IToolbar iToolbar, IConnectedCalendars iConnectedCalendars) {
		this.iControlEvent = (IControlEvent) fragment;
		this.onEventItemClickListener = (OnEventItemClickListener) fragment;
		this.onEventItemLongClickListener = (OnEventItemLongClickListener) fragment;
		this.iToolbar = iToolbar;
		this.iConnectedCalendars = iConnectedCalendars;
	}

	public static int getColumnWidth() {
		return COLUMN_WIDTH;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_week, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		weekViewPager = (ViewPager2) view.findViewById(R.id.week_viewpager);
		weekViewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

		setViewPager();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	private void setViewPager() {
		weekViewPagerAdapter = new WeekViewPagerAdapter(iControlEvent, onEventItemLongClickListener, onEventItemClickListener, iToolbar, iConnectedCalendars);
		weekViewPager.setAdapter(weekViewPagerAdapter);
		weekViewPager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION, false);

		onPageChangeCallback = new OnPageChangeCallback(weekViewPagerAdapter.getCALENDAR());
		weekViewPager.registerOnPageChangeCallback(onPageChangeCallback);
	}

	@Override
	public void receivedTimeTick(Date date) {
		refreshView();
	}

	@Override
	public void receivedDateChanged(Date date) {
		refreshView();
	}

	@Override
	public void onSavedNewEvent(long eventId, long begin) {
		moveCurrentView(begin);
	}

	@Override
	public void onModifiedEvent(long eventId, long begin) {
		moveCurrentView(begin);
	}

	@Override
	public void onModifiedInstance(long instanceId, long begin) {
		moveCurrentView(begin);
	}

	@Override
	public void moveCurrentView(long begin) {
		refreshView();
		int movePosition = ClockUtil.calcWeekDifference(new Date(begin), weekViewPagerAdapter.getCALENDAR().getTime());
		if (movePosition != 0) {
			weekViewPager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION + movePosition, true);
		}
	}

	class OnPageChangeCallback extends ViewPager2.OnPageChangeCallback {
		final Calendar calendar;
		Calendar copiedCalendar;

		public OnPageChangeCallback(Calendar calendar) {
			this.calendar = calendar;
			this.copiedCalendar = (Calendar) calendar.clone();
		}


		@Override
		public void onPageScrollStateChanged(int state) {
			super.onPageScrollStateChanged(state);
		}


		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			// 오른쪽(이전 주)으로 드래그시 positionOffset의 값이 작아짐 0.99999 -> 0.0
			// 왼쪽(다음 주)으로 드래그시 positionOffset의 값이 커짐 0.00001 -> 1.0
			super.onPageScrolled(position, positionOffset, positionOffsetPixels);
		}

		@Override
		public void onPageSelected(int position) {
			// drag 성공 시에만 SETTLING 직후 호출
			super.onPageSelected(position);
			currentPosition = position;

			copiedCalendar = (Calendar) calendar.clone();
			copiedCalendar.add(Calendar.WEEK_OF_YEAR, currentPosition - EventTransactionFragment.FIRST_VIEW_POSITION);

			iToolbar.setMonth(copiedCalendar.getTime());
			// 일정 목록을 가져와서 표시함 header view, week view
		}
	}

	public void goToToday() {
		if (!ClockUtil.areSameDate(System.currentTimeMillis(), weekViewPagerAdapter.getCurrentDateTime().getTime())) {
			setViewPager();
		} else {
			if (weekViewPager.getCurrentItem() != EventTransactionFragment.FIRST_VIEW_POSITION) {
				weekViewPagerAdapter.notifyDataSetChanged();
				weekViewPager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION, true);
			}
		}
	}

	public void goToWeek(Date date) {
		int weekDifference = ClockUtil.calcWeekDifference(date, onPageChangeCallback.copiedCalendar.getTime());
		weekViewPager.setCurrentItem(weekViewPager.getCurrentItem() + weekDifference, true);
	}


	@Override
	public void refreshView() {
		if (!ClockUtil.areSameDate(System.currentTimeMillis(), weekViewPagerAdapter.getCurrentDateTime().getTime())) {
			setViewPager();
		} else {
			weekViewPagerAdapter.notifyDataSetChanged();
		}
	}
}