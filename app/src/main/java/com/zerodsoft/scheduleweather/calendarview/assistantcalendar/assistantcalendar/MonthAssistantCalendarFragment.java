package com.zerodsoft.scheduleweather.calendarview.assistantcalendar.assistantcalendar;

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

import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendar.selectedcalendar.SelectedCalendarViewModel;
import com.zerodsoft.scheduleweather.calendarview.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarview.interfaces.CalendarDateOnClickListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IControlEvent;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IRefreshView;
import com.zerodsoft.scheduleweather.databinding.FragmentMonthAssistantBinding;
import com.zerodsoft.scheduleweather.room.dto.SelectedCalendarDTO;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MonthAssistantCalendarFragment extends Fragment implements IRefreshView, IConnectedCalendars {
	public static final String TAG = "MonthAssistantCalendarFragment";
	private final CalendarDateOnClickListener calendarDateOnClickListener;

	private FragmentMonthAssistantBinding binding;
	private MonthAssistantCalendarListAdapter adapter;

	private CalendarViewModel calendarViewModel;
	private SelectedCalendarViewModel selectedCalendarViewModel;

	private List<SelectedCalendarDTO> selectedCalendarDTOList = new ArrayList<>();
	private boolean initializing = true;

	public MonthAssistantCalendarFragment(CalendarDateOnClickListener calendarDateOnClickListener) {
		this.calendarDateOnClickListener = calendarDateOnClickListener;
	}

	@Override
	public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		calendarViewModel = new ViewModelProvider(requireActivity()).get(CalendarViewModel.class);
		selectedCalendarViewModel = new ViewModelProvider(requireActivity()).get(SelectedCalendarViewModel.class);
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
			binding.currentMonth.setText(ClockUtil.YEAR_MONTH_FORMAT.format(adapter.getAsOfDate()));
		}
	};

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = FragmentMonthAssistantBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		binding.monthAssistantCalendarViewpager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
		binding.currentMonthButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//오늘 날짜로 이동
				goToToday();
			}
		});

		binding.previousMonthButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				binding.monthAssistantCalendarViewpager.setCurrentItem(binding.monthAssistantCalendarViewpager.getCurrentItem() - 1, true);
			}
		});

		binding.nextMonthButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				binding.monthAssistantCalendarViewpager.setCurrentItem(binding.monthAssistantCalendarViewpager.getCurrentItem() + 1, true);
			}
		});

		selectedCalendarViewModel.getOnDeletedSelectedCalendarLiveData().observe(getViewLifecycleOwner(), onDeletedSelectedCalendarObserver);
		selectedCalendarViewModel.getOnAddedSelectedCalendarLiveData().observe(getViewLifecycleOwner(), onAddedSelectedCalendarObserver);
		selectedCalendarViewModel.getOnListSelectedCalendarLiveData().observe(getViewLifecycleOwner(), onListSelectedCalendarObserver);
		selectedCalendarViewModel.getSelectedCalendarList();
	}


	private void setViewPager() {
		adapter = new MonthAssistantCalendarListAdapter(new IControlEvent() {
			@Override
			public Map<Integer, CalendarInstance> getInstances(long begin, long end) {
				return calendarViewModel.getInstances(begin, end);
			}
		}, calendarDateOnClickListener, this);
		binding.monthAssistantCalendarViewpager.setAdapter(adapter);
		binding.monthAssistantCalendarViewpager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION, false);
		binding.monthAssistantCalendarViewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
			private final Calendar calendar = Calendar.getInstance();

			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				calendar.setTime(adapter.getAsOfDate());
				calendar.add(Calendar.MONTH, position - EventTransactionFragment.FIRST_VIEW_POSITION);

				binding.currentMonth.setText(ClockUtil.YEAR_MONTH_FORMAT.format(calendar.getTime()));
			}
		});
	}

	/**
	 * 현재 년월 텍스트를 클릭하면 보조 캘린더의 날짜를 현재 month로 설정하고 표시
	 **/
	public void setCurrentMonth(Date date) {
		// 개월 수 차이 계산
		int monthDifference = ClockUtil.calcMonthDifference(date, adapter.getAsOfDate());
		if (monthDifference != 0) {
			binding.monthAssistantCalendarViewpager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION + monthDifference, false);
		}
	}

	@Override
	public void refreshView() {
		if (isVisible()) {
			if (!ClockUtil.areSameDate(System.currentTimeMillis(), adapter.getCurrentDateTime().getTime())) {
				setViewPager();
			} else {
				adapter.notifyDataSetChanged();
			}
		}
	}

	public void goToToday() {
		if (!ClockUtil.areSameDate(System.currentTimeMillis(), adapter.getCurrentDateTime().getTime())) {
			setViewPager();
		} else {
			if (binding.monthAssistantCalendarViewpager.getCurrentItem() != EventTransactionFragment.FIRST_VIEW_POSITION) {
				adapter.notifyDataSetChanged();
				binding.monthAssistantCalendarViewpager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION, true);
			}
		}
	}

	@Override
	public List<SelectedCalendarDTO> getConnectedCalendars() {
		return selectedCalendarDTOList;
	}
}
