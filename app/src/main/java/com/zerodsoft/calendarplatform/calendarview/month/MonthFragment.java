package com.zerodsoft.calendarplatform.calendarview.month;

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

import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.calendar.CalendarViewModel;
import com.zerodsoft.calendarplatform.calendar.dto.CalendarInstance;
import com.zerodsoft.calendarplatform.calendar.interfaces.ICalendarProvider;
import com.zerodsoft.calendarplatform.calendar.selectedcalendar.SelectedCalendarViewModel;
import com.zerodsoft.calendarplatform.calendarview.EventTransactionFragment;
import com.zerodsoft.calendarplatform.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.calendarplatform.calendarview.interfaces.IControlEvent;
import com.zerodsoft.calendarplatform.calendarview.interfaces.IMoveViewpager;
import com.zerodsoft.calendarplatform.calendarview.interfaces.IRefreshView;
import com.zerodsoft.calendarplatform.calendarview.interfaces.IToolbar;
import com.zerodsoft.calendarplatform.calendarview.interfaces.OnDateTimeChangedListener;
import com.zerodsoft.calendarplatform.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.calendarplatform.calendarview.interfaces.OnEventItemLongClickListener;
import com.zerodsoft.calendarplatform.room.dto.SelectedCalendarDTO;
import com.zerodsoft.calendarplatform.utility.ClockUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MonthFragment extends Fragment implements IRefreshView, OnDateTimeChangedListener, IMoveViewpager {
	public static final String TAG = "MonthFragment";

	private final IToolbar iToolbar;
	private final OnEventItemClickListener onEventItemClickListener;
	private final OnEventItemLongClickListener onEventItemLongClickListener;
	private final ICalendarProvider iCalendarProvider;

	private CalendarViewModel calendarViewModel;
	private ViewPager2 viewPager;
	private MonthViewPagerAdapter viewPagerAdapter;
	private OnPageChangeCallback onPageChangeCallback;
	private SelectedCalendarViewModel selectedCalendarViewModel;
	private List<SelectedCalendarDTO> selectedCalendarDTOList = new ArrayList<>();
	private boolean initializing = true;

	private int currentPosition = EventTransactionFragment.FIRST_VIEW_POSITION;

	public MonthFragment(Fragment fragment, IToolbar iToolbar, ICalendarProvider iCalendarProvider) {
		this.onEventItemLongClickListener = (OnEventItemLongClickListener) fragment;
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
				if (!initializing) {
					moveCurrentViewForBegin(start);
				}
			}
		});

		calendarViewModel.getOnUpdatedAllEventsLiveData().observe(this, new Observer<Long>() {
			@Override
			public void onChanged(Long start) {
				if (!initializing) {
					refreshView();
				}
			}
		});

		calendarViewModel.getOnUpdatedFollowingEventsLiveData().observe(this, new Observer<Long>() {
			@Override
			public void onChanged(Long begin) {
				if (!initializing) {
					refreshView();
				}
			}
		});

		calendarViewModel.getOnUpdatedOnlyThisEventLiveData().observe(this, new Observer<Long>() {
			@Override
			public void onChanged(Long begin) {
				if (!initializing) {
					refreshView();
				}
			}
		});

		calendarViewModel.getOnExceptedInstanceLiveData().observe(this, new Observer<Boolean>() {
			@Override
			public void onChanged(Boolean aBoolean) {
				if (!initializing) {
					refreshView();
				}
			}
		});

		calendarViewModel.getOnRemovedFutureInstancesLiveData().observe(this, new Observer<Boolean>() {
			@Override
			public void onChanged(Boolean aBoolean) {
				if (!initializing) {
					refreshView();
				}
			}
		});

		calendarViewModel.getOnRemovedEventLiveData().observe(this, new Observer<Boolean>() {
			@Override
			public void onChanged(Boolean aBoolean) {
				if (!initializing) {
					refreshView();
				}
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
				selectedCalendarDTOList.clear();
				selectedCalendarDTOList.addAll(selectedCalendarDTOS);
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
			selectedCalendarDTOList.addAll(selectedCalendarDTOS);
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
		viewPagerAdapter = new MonthViewPagerAdapter(new IControlEvent() {
			@Override
			public Map<Integer, CalendarInstance> getInstances(long begin, long end) {
				return calendarViewModel.getInstances(begin, end);
			}
		}, onEventItemLongClickListener, onEventItemClickListener, iToolbar,
				new IConnectedCalendars() {
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

	public long[] getCurrentDate() {
		Calendar calendar = onPageChangeCallback.getCurrentDate();

		long[] times = new long[2];
		times[0] = calendar.getTimeInMillis();
		calendar.add(Calendar.DATE, 7);
		times[1] = calendar.getTimeInMillis();

		return times;
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

	public void goToDate(Date date) {
		int monthDifference = ClockUtil.calcMonthDifference(date, onPageChangeCallback.copiedCalendar.getTime());
		viewPager.setCurrentItem(viewPager.getCurrentItem() + monthDifference, true);
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
			this.copiedCalendar = (Calendar) calendar.clone();
		}

		public Calendar getCurrentDate() {
			return (Calendar) copiedCalendar.clone();
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

