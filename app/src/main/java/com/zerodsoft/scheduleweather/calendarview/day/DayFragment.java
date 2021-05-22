package com.zerodsoft.scheduleweather.calendarview.day;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarview.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IControlEvent;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IRefreshView;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IToolbar;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnDateTimeChangedListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemLongClickListener;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.Calendar;
import java.util.Date;

public class DayFragment extends Fragment implements IRefreshView, OnDateTimeChangedListener {
	public static final String TAG = "DAY_FRAGMENT";

	private final IControlEvent iControlEvent;
	private final OnEventItemClickListener onEventItemClickListener;
	private final IToolbar iToolbar;
	private final IConnectedCalendars iConnectedCalendars;
	private final OnEventItemLongClickListener onEventItemLongClickListener;

	private ViewPager2 dayViewPager;
	private DayViewPagerAdapter dayViewPagerAdapter;

	private OnPageChangeCallback onPageChangeCallback;
	private int currentPosition = EventTransactionFragment.FIRST_VIEW_POSITION;


	public DayFragment(Fragment fragment, IToolbar iToolbar, IConnectedCalendars iConnectedCalendars) {
		this.iControlEvent = (IControlEvent) fragment;
		this.onEventItemClickListener = (OnEventItemClickListener) fragment;
		this.onEventItemLongClickListener = (OnEventItemLongClickListener) fragment;
		this.iToolbar = iToolbar;
		this.iConnectedCalendars = iConnectedCalendars;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_day, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		dayViewPager = (ViewPager2) view.findViewById(R.id.day_viewpager);

		dayViewPagerAdapter = new DayViewPagerAdapter(iControlEvent, onEventItemLongClickListener, onEventItemClickListener, iToolbar, iConnectedCalendars);
		dayViewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
		dayViewPager.setAdapter(dayViewPagerAdapter);
		dayViewPager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION, false);

		onPageChangeCallback = new OnPageChangeCallback(dayViewPagerAdapter.getCALENDAR());
		dayViewPager.registerOnPageChangeCallback(onPageChangeCallback);

		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	public void goToToday(Date date) {
		int dayDifference = ClockUtil.calcDayDifference(date, onPageChangeCallback.copiedCalendar.getTime());
		dayViewPager.setCurrentItem(dayViewPager.getCurrentItem() + dayDifference, true);
	}

	@Override
	public void receivedTimeTick(Date date) {
		refreshView();
	}

	@Override
	public void receivedDateChanged(Date date) {
		refreshView();
	}

	class OnPageChangeCallback extends ViewPager2.OnPageChangeCallback {
		private final Calendar calendar;
		private Calendar copiedCalendar;

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
			currentPosition = position;

			copiedCalendar = (Calendar) calendar.clone();
			copiedCalendar.add(Calendar.DAY_OF_YEAR, currentPosition - EventTransactionFragment.FIRST_VIEW_POSITION);

			iToolbar.setMonth(copiedCalendar.getTime());
			super.onPageSelected(position);
		}
	}

	@Override
	public void refreshView() {
		dayViewPagerAdapter.notifyDataSetChanged();
	}

	public void goToToday() {
		if (!ClockUtil.areSameDate(System.currentTimeMillis(), dayViewPagerAdapter.getCALENDAR().getTimeInMillis())) {
			dayViewPagerAdapter = new DayViewPagerAdapter(iControlEvent, onEventItemLongClickListener, onEventItemClickListener, iToolbar, iConnectedCalendars);
			dayViewPager.setAdapter(dayViewPagerAdapter);
			dayViewPager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION, true);

			onPageChangeCallback = new OnPageChangeCallback(dayViewPagerAdapter.getCALENDAR());
			dayViewPager.registerOnPageChangeCallback(onPageChangeCallback);
		} else {
			if (currentPosition != EventTransactionFragment.FIRST_VIEW_POSITION) {
				dayViewPager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION, true);
				refreshView();
			}
		}
	}
}
