package com.zerodsoft.scheduleweather.calendarview.month;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.calendar.interfaces.ICalendarProvider;
import com.zerodsoft.scheduleweather.calendar.selectedcalendar.SelectedCalendarViewModel;
import com.zerodsoft.scheduleweather.calendarview.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IControlEvent;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IMoveViewpager;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IRefreshView;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IToolbar;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnDateTimeChangedListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemLongClickListener;
import com.zerodsoft.scheduleweather.room.dto.SelectedCalendarDTO;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MonthFragment extends Fragment implements IRefreshView, OnDateTimeChangedListener, IMoveViewpager {
	public static final String TAG = "MonthFragment";

	private final IControlEvent iControlEvent;
	private final IToolbar iToolbar;
	private final OnEventItemClickListener onEventItemClickListener;
	private final OnEventItemLongClickListener onEventItemLongClickListener;
	private final ICalendarProvider iCalendarProvider;

	private CalendarViewModel calendarViewModel;
	private ViewPager2 viewPager;
	private MonthViewPagerAdapter viewPagerAdapter;
	private OnPageChangeCallback onPageChangeCallback;
	private SelectedCalendarViewModel selectedCalendarViewModel;
	private List<SelectedCalendarDTO> selectedCalendarDTOList;
	private boolean initializing = true;

	private int currentPosition = EventTransactionFragment.FIRST_VIEW_POSITION;

	public MonthFragment(Fragment fragment, IToolbar iToolbar, ICalendarProvider iCalendarProvider) {
		this.onEventItemLongClickListener = (OnEventItemLongClickListener) fragment;
		this.iControlEvent = (IControlEvent) fragment;
		this.onEventItemClickListener = (OnEventItemClickListener) fragment;
		this.iCalendarProvider = iCalendarProvider;
		this.iToolbar = iToolbar;
	}

	@Override
	public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		calendarViewModel = new ViewModelProvider(requireActivity()).get(CalendarViewModel.class);
		selectedCalendarViewModel = new ViewModelProvider(requireActivity()).get(SelectedCalendarViewModel.class);

		calendarViewModel.getOnAddedNewEventLiveData().observe(this, new Observer<Long>() {
			@Override
			public void onChanged(Long start) {
				moveCurrentViewForBegin(start);
			}
		});

		calendarViewModel.getOnModifiedEventLiveData().observe(this, new Observer<Long>() {
			@Override
			public void onChanged(Long start) {
				moveCurrentViewForBegin(start);
			}
		});

		calendarViewModel.getOnModifiedFutureInstancesLiveData().observe(this, new Observer<Long>() {
			@Override
			public void onChanged(Long begin) {
				moveCurrentViewForBegin(begin);
			}
		});

		calendarViewModel.getOnModifiedInstanceLiveData().observe(this, new Observer<Long>() {
			@Override
			public void onChanged(Long begin) {
				moveCurrentViewForBegin(begin);
			}
		});

		calendarViewModel.getOnExceptedInstanceLiveData().observe(this, new Observer<Boolean>() {
			@Override
			public void onChanged(Boolean aBoolean) {
				refreshView();
			}
		});

		calendarViewModel.getOnRemovedFutureInstancesLiveData().observe(this, new Observer<Boolean>() {
			@Override
			public void onChanged(Boolean aBoolean) {
				refreshView();
			}
		});

		calendarViewModel.getOnRemovedEventLiveData().observe(this, new Observer<Boolean>() {
			@Override
			public void onChanged(Boolean aBoolean) {
				refreshView();
			}
		});

		selectedCalendarViewModel.getOnListSelectedCalendarLiveData().observe(this, onListSelectedCalendarObserver);
		selectedCalendarViewModel.getOnAddedSelectedCalendarLiveData().observe(this, onAddedSelectedCalendarObserver);
		selectedCalendarViewModel.getOnDeletedSelectedCalendarLiveData().observe(this, onDeletedSelectedCalendarObserver);
	}

	private final Observer<List<SelectedCalendarDTO>> onDeletedSelectedCalendarObserver = new Observer<List<SelectedCalendarDTO>>() {
		@Override
		public void onChanged(List<SelectedCalendarDTO> selectedCalendarDTOS) {
			if (!initializing) {
				selectedCalendarDTOList = selectedCalendarDTOS;
				refreshView();
			}
		}
	};

	private final Observer<SelectedCalendarDTO> onAddedSelectedCalendarObserver = new Observer<SelectedCalendarDTO>() {
		@Override
		public void onChanged(SelectedCalendarDTO selectedCalendarDTO) {
			if (!initializing) {
				selectedCalendarDTOList.add(selectedCalendarDTO);
				refreshView();
			}
		}
	};

	private final Observer<List<SelectedCalendarDTO>> onListSelectedCalendarObserver = new Observer<List<SelectedCalendarDTO>>() {
		@Override
		public void onChanged(List<SelectedCalendarDTO> selectedCalendarDTOS) {
			initializing = false;
			selectedCalendarDTOList = selectedCalendarDTOS;
			setViewPager();
		}
	};

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_month, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		viewPager = (ViewPager2) view.findViewById(R.id.month_viewpager);
		viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

		selectedCalendarViewModel.getSelectedCalendarList();
	}



	private void setViewPager() {
		viewPagerAdapter = new MonthViewPagerAdapter(iControlEvent, onEventItemLongClickListener, onEventItemClickListener, iToolbar, new IConnectedCalendars() {
			@Override
			public List<SelectedCalendarDTO> getConnectedCalendars() {
				return selectedCalendarDTOList;
			}
		});
		viewPager.setAdapter(viewPagerAdapter);
		viewPager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION, false);

		onPageChangeCallback = new OnPageChangeCallback(viewPagerAdapter.getCALENDAR());
		viewPager.registerOnPageChangeCallback(onPageChangeCallback);
	}


	@Override
	public void refreshView() {
		if (!ClockUtil.areSameDate(System.currentTimeMillis(), viewPagerAdapter.getCurrentDateTime().getTime())) {
			setViewPager();
		} else {
			viewPagerAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void goToToday() {
		if (!ClockUtil.areSameDate(System.currentTimeMillis(), viewPagerAdapter.getCurrentDateTime().getTime())) {
			setViewPager();
		} else {
			if (viewPager.getCurrentItem() != EventTransactionFragment.FIRST_VIEW_POSITION) {
				viewPagerAdapter.notifyDataSetChanged();
				viewPager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION, true);
			}
		}
	}

	@Override
	public void moveCurrentViewForBegin(long begin) {
		refreshView();
		int movePosition = ClockUtil.calcMonthDifference(new Date(begin), viewPagerAdapter.getCALENDAR().getTime());
		if (movePosition != 0) {
			viewPager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION + movePosition, true);
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

