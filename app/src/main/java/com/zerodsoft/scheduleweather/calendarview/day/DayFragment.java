package com.zerodsoft.scheduleweather.calendarview.day;

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

public class DayFragment extends Fragment implements IRefreshView, OnDateTimeChangedListener, IMoveViewpager {
	public static final String TAG = "DAY_FRAGMENT";

	private final IControlEvent iControlEvent;
	private final OnEventItemClickListener onEventItemClickListener;
	private final IToolbar iToolbar;
	private final OnEventItemLongClickListener onEventItemLongClickListener;
	private CalendarViewModel calendarViewModel;
	private SelectedCalendarViewModel selectedCalendarViewModel;

	private List<SelectedCalendarDTO> selectedCalendarDTOList;
	private ViewPager2 dayViewPager;
	private DayViewPagerAdapter dayViewPagerAdapter;

	private OnPageChangeCallback onPageChangeCallback;
	private int currentPosition = EventTransactionFragment.FIRST_VIEW_POSITION;
	private boolean initializing = true;


	public DayFragment(Fragment fragment, IToolbar iToolbar) {
		this.iControlEvent = (IControlEvent) fragment;
		this.onEventItemClickListener = (OnEventItemClickListener) fragment;
		this.onEventItemLongClickListener = (OnEventItemLongClickListener) fragment;
		this.iToolbar = iToolbar;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
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


	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_day, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		dayViewPager = (ViewPager2) view.findViewById(R.id.day_viewpager);
		dayViewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

		selectedCalendarViewModel.getSelectedCalendarList();
	}

	private void setViewPager() {
		dayViewPagerAdapter = new DayViewPagerAdapter(iControlEvent, onEventItemLongClickListener, onEventItemClickListener, iToolbar,
				new IConnectedCalendars() {
					@Override
					public List<SelectedCalendarDTO> getConnectedCalendars() {
						return selectedCalendarDTOList;
					}
				});
		dayViewPager.setAdapter(dayViewPagerAdapter);
		dayViewPager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION, false);

		onPageChangeCallback = new OnPageChangeCallback(dayViewPagerAdapter.getCALENDAR());
		dayViewPager.registerOnPageChangeCallback(onPageChangeCallback);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		selectedCalendarViewModel.getOnListSelectedCalendarLiveData().removeObserver(onListSelectedCalendarObserver);
		selectedCalendarViewModel.getOnDeletedSelectedCalendarLiveData().removeObserver(onDeletedSelectedCalendarObserver);
		selectedCalendarViewModel.getOnAddedSelectedCalendarLiveData().removeObserver(onAddedSelectedCalendarObserver);
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
		if (!ClockUtil.areSameDate(System.currentTimeMillis(), dayViewPagerAdapter.getCurrentDateTime().getTime())) {
			setViewPager();
		} else {
			dayViewPagerAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void moveCurrentViewForBegin(long begin) {
		refreshView();
		int movePosition = ClockUtil.calcDayDifference(new Date(begin), dayViewPagerAdapter.getCALENDAR().getTime());
		if (movePosition != 0) {
			dayViewPager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION + movePosition, true);
		}
	}

	public void goToToday() {
		if (!ClockUtil.areSameDate(System.currentTimeMillis(), dayViewPagerAdapter.getCurrentDateTime().getTime())) {
			setViewPager();
		} else {
			if (dayViewPager.getCurrentItem() != EventTransactionFragment.FIRST_VIEW_POSITION) {
				dayViewPagerAdapter.notifyDataSetChanged();
				dayViewPager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION, true);
			}
		}
	}

}
