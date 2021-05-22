package com.zerodsoft.scheduleweather.calendarview.month;

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

public class MonthFragment extends Fragment implements IRefreshView, OnDateTimeChangedListener {
	public static final String TAG = "MonthFragment";

	private final IControlEvent iControlEvent;
	private final IToolbar iToolbar;
	private final OnEventItemClickListener onEventItemClickListener;
	private final IConnectedCalendars iConnectedCalendars;
	private final OnEventItemLongClickListener onEventItemLongClickListener;

	private ViewPager2 viewPager;
	private MonthViewPagerAdapter viewPagerAdapter;
	private OnPageChangeCallback onPageChangeCallback;

	private int currentPosition = EventTransactionFragment.FIRST_VIEW_POSITION;

	public MonthFragment(Fragment fragment, IToolbar iToolbar, IConnectedCalendars iConnectedCalendars) {
		this.onEventItemLongClickListener = (OnEventItemLongClickListener) fragment;
		this.iControlEvent = (IControlEvent) fragment;
		this.onEventItemClickListener = (OnEventItemClickListener) fragment;
		this.iToolbar = iToolbar;
		this.iConnectedCalendars = iConnectedCalendars;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_month, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		viewPager = (ViewPager2) view.findViewById(R.id.month_viewpager);

		viewPagerAdapter = new MonthViewPagerAdapter(iControlEvent, onEventItemLongClickListener, onEventItemClickListener, iToolbar, iConnectedCalendars);
		viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
		viewPager.setAdapter(viewPagerAdapter);
		viewPager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION, false);

		onPageChangeCallback = new OnPageChangeCallback(viewPagerAdapter.getCALENDAR());
		viewPager.registerOnPageChangeCallback(onPageChangeCallback);
	}


	@Override
	public void onStart() {
		super.onStart();
		// 인스턴스 그리기
	}


	@Override
	public void refreshView() {
		viewPagerAdapter.notifyDataSetChanged();
	}

	public void goToToday() {
		if (!ClockUtil.areSameDate(System.currentTimeMillis(), viewPagerAdapter.getCALENDAR().getTimeInMillis())) {
			viewPagerAdapter = new MonthViewPagerAdapter(iControlEvent, onEventItemLongClickListener, onEventItemClickListener, iToolbar, iConnectedCalendars);
			viewPager.setAdapter(viewPagerAdapter);
			viewPager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION, true);

			onPageChangeCallback = new OnPageChangeCallback(viewPagerAdapter.getCALENDAR());
			viewPager.registerOnPageChangeCallback(onPageChangeCallback);
		} else {
			if (currentPosition != EventTransactionFragment.FIRST_VIEW_POSITION) {
				viewPager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION, true);
				refreshView();
			}
		}
	}


	@Override
	public void receivedTimeTick(Date date) {
		//month에서는 불필요
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
			copiedCalendar.add(Calendar.MONTH, currentPosition - EventTransactionFragment.FIRST_VIEW_POSITION);

			//error point-------------------------------------------------------------------------
			iToolbar.setMonth(copiedCalendar.getTime());
			super.onPageSelected(position);
		}
	}


}

