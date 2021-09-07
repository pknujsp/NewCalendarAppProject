package com.zerodsoft.calendarplatform.calendarview.week;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.calendarplatform.activity.main.AppMainActivity;
import com.zerodsoft.calendarplatform.calendar.CalendarViewModel;
import com.zerodsoft.calendarplatform.calendar.dto.CalendarInstance;
import com.zerodsoft.calendarplatform.calendar.selectedcalendar.SelectedCalendarViewModel;
import com.zerodsoft.calendarplatform.calendarview.EventTransactionFragment;
import com.zerodsoft.calendarplatform.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.calendarplatform.calendarview.interfaces.IControlEvent;
import com.zerodsoft.calendarplatform.calendarview.interfaces.IMoveViewpager;
import com.zerodsoft.calendarplatform.calendarview.interfaces.IRefreshView;
import com.zerodsoft.calendarplatform.calendarview.interfaces.IToolbar;
import com.zerodsoft.calendarplatform.calendarview.interfaces.OnDateTimeChangedListener;
import com.zerodsoft.calendarplatform.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.calendarview.interfaces.OnEventItemLongClickListener;
import com.zerodsoft.calendarplatform.room.dto.SelectedCalendarDTO;
import com.zerodsoft.calendarplatform.utility.ClockUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class WeekFragment extends Fragment implements IRefreshView, OnDateTimeChangedListener, IMoveViewpager {
	public static final String TAG = "WEEK_FRAGMENT";
	private ViewPager2 weekViewPager;
	private WeekViewPagerAdapter weekViewPagerAdapter;
	private int currentPosition = EventTransactionFragment.FIRST_VIEW_POSITION;

	private final IToolbar iToolbar;
	private final OnEventItemClickListener onEventItemClickListener;
	private final OnEventItemLongClickListener onEventItemLongClickListener;
	private CalendarViewModel calendarViewModel;
	private SelectedCalendarViewModel selectedCalendarViewModel;
	private List<SelectedCalendarDTO> selectedCalendarDTOList = new ArrayList<>();
	private boolean initializing = true;

	private static final int COLUMN_WIDTH = AppMainActivity.getDisplayWidth() / 8;

	private OnPageChangeCallback onPageChangeCallback;

	public WeekFragment(Fragment fragment, IToolbar iToolbar) {
		this.onEventItemClickListener = (OnEventItemClickListener) fragment;
		this.onEventItemLongClickListener = (OnEventItemLongClickListener) fragment;
		this.iToolbar = iToolbar;
	}

	public static int getColumnWidth() {
		return COLUMN_WIDTH;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
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
		selectedCalendarViewModel.getOnDeletedSelectedCalendarLiveData().observe(this, onDeletedSelectedCalendarObserver);
		selectedCalendarViewModel.getOnAddedSelectedCalendarLiveData().observe(this, onAddedSelectedCalendarObserver);
		selectedCalendarViewModel.getOnListSelectedCalendarLiveData().observe(this, onListSelectedCalendarObserver);
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_week, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		weekViewPager = (ViewPager2) view.findViewById(R.id.week_viewpager);
		weekViewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

		selectedCalendarViewModel.getSelectedCalendarList();
	}

	private void setViewPager() {
		weekViewPagerAdapter = new WeekViewPagerAdapter(new IControlEvent() {
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
		weekViewPager.setAdapter(weekViewPagerAdapter);
		weekViewPager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION, false);

		onPageChangeCallback = new OnPageChangeCallback(weekViewPagerAdapter.getCALENDAR());
		weekViewPager.registerOnPageChangeCallback(onPageChangeCallback);
	}

	@Override
	public void receivedTimeTick(Date date) {
		weekViewPagerAdapter.receivedTimeTick(date);
	}

	@Override
	public void receivedDateChanged(Date date) {
		weekViewPagerAdapter.receivedTimeTick(date);
	}


	class OnPageChangeCallback extends ViewPager2.OnPageChangeCallback {
		final Calendar calendar;
		Calendar copiedCalendar;

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
			super.onPageSelected(position);
			currentPosition = position;

			copiedCalendar = (Calendar) calendar.clone();
			copiedCalendar.add(Calendar.WEEK_OF_YEAR, currentPosition - EventTransactionFragment.FIRST_VIEW_POSITION);

			iToolbar.setMonth(copiedCalendar.getTime());
			// 일정 목록을 가져와서 표시함 header view, week view
		}
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
	public void moveCurrentViewForBegin(long begin) {
		refreshView();
		int movePosition = ClockUtil.calcWeekDifference(new Date(begin), weekViewPagerAdapter.getCALENDAR().getTime());
		if (movePosition != 0) {
			weekViewPager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION + movePosition, true);
		}
	}

	@Override
	public void refreshView() {
		if (!ClockUtil.areSameDate(System.currentTimeMillis(), weekViewPagerAdapter.getCurrentDateTime().getTime())) {
			setViewPager();
		} else {
			weekViewPagerAdapter.notifyDataSetChanged();
		}
	}

	@Override
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
}