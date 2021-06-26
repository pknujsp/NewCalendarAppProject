package com.zerodsoft.scheduleweather.calendarview.instancelistdaydialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.provider.CalendarContract;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.PopupMenu;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.CalendarInstanceUtil;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.calendar.CommonPopupMenu;
import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendarview.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarview.instancelistdaydialog.adapter.InstancesOfDayAdapter;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IControlEvent;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IRefreshView;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemLongClickListener;
import com.zerodsoft.scheduleweather.databinding.FragmentInstanceListOnADayBinding;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationHistoryViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationInfoViewModel;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import lombok.SneakyThrows;

public class InstanceListOnADayDialogFragment extends DialogFragment implements OnEventItemLongClickListener, IRefreshView, IControlEvent, InstancesOfDayView.InstanceDialogMenuListener
		, InstancesOfDayView.DeleteEventsListener {
	public static final String TAG = "InstanceListOnADayDialogFragment";

	private final IConnectedCalendars iConnectedCalendars;
	private final OnEventItemClickListener onEventItemClickListener;
	private final IRefreshView iRefreshView;

	private CalendarViewModel calendarViewModel;
	private LocationViewModel locationViewModel;
	private FoodCriteriaLocationHistoryViewModel foodCriteriaLocationHistoryViewModel;
	private FoodCriteriaLocationInfoViewModel foodCriteriaLocationInfoViewModel;
	private FragmentInstanceListOnADayBinding binding;
	private InstancesOfDayAdapter adapter;
	private CompositePageTransformer compositePageTransformer;

	private Long begin;
	private Long end;

	private final CommonPopupMenu commonPopupMenu = new CommonPopupMenu() {
		@Override
		public void onExceptedInstance(boolean isSuccessful) {
			if (isSuccessful) {
			}
		}

		@Override
		public void onDeletedEvent(boolean isSuccessful) {
			if (isSuccessful) {
			}
		}

		@Override
		public void onClickedModify(Fragment modificationFragment) {
			dismiss();
			getParentFragmentManager().beginTransaction().add(R.id.fragment_container, modificationFragment,
					getString(R.string.tag_modify_instance_fragment)).addToBackStack(getString(R.string.tag_modify_instance_fragment)).commit();
		}
	};

	public InstanceListOnADayDialogFragment(IConnectedCalendars iConnectedCalendars, Fragment fragment) {
		this.onEventItemClickListener = (OnEventItemClickListener) fragment;
		this.iRefreshView = (IRefreshView) fragment;
		this.iConnectedCalendars = iConnectedCalendars;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();

		begin = bundle.getLong("begin");
		end = bundle.getLong("end");

		calendarViewModel = new ViewModelProvider(requireActivity()).get(CalendarViewModel.class);
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		return new Dialog(requireContext(), R.style.DialogTransparent);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentInstanceListOnADayBinding.inflate(inflater);
		return binding.getRoot();
	}

	@SneakyThrows
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
		foodCriteriaLocationHistoryViewModel = new ViewModelProvider(this).get(FoodCriteriaLocationHistoryViewModel.class);
		foodCriteriaLocationInfoViewModel = new ViewModelProvider(this).get(FoodCriteriaLocationInfoViewModel.class);

		binding.goToTodayButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				goToToday();
			}
		});

		binding.goToFirstSelectedDayButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				goToFirstSelectedDay();
			}
		});

		binding.instancesDialogViewpager.setOffscreenPageLimit(1);
		binding.instancesDialogViewpager.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

		compositePageTransformer = new CompositePageTransformer();
		final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, getResources().getDisplayMetrics());
		compositePageTransformer.addTransformer(new MarginPageTransformer(margin));
		compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
			@Override
			public void transformPage(@NonNull View page, float position) {
				float r = 1 - Math.abs(position);
				page.setScaleY(0.8f + r * 0.2f);
			}
		});
		binding.instancesDialogViewpager.setPageTransformer(compositePageTransformer);
		binding.instancesDialogViewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
			}
		});

		adapter = new InstancesOfDayAdapter(begin, end, onEventItemClickListener, iConnectedCalendars, this);
		binding.instancesDialogViewpager.setAdapter(adapter);
		binding.instancesDialogViewpager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION, false);
	}

	@Override
	public void createInstancePopupMenu(ContentValues instance, View anchorView, int gravity) {
		commonPopupMenu.createInstancePopupMenu(instance, requireActivity(), anchorView, Gravity.CENTER
				, calendarViewModel, locationViewModel, foodCriteriaLocationInfoViewModel, foodCriteriaLocationHistoryViewModel);
	}


	@Override
	public void onResume() {
		super.onResume();

		Window window = getDialog().getWindow();
		window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
	}

	@Override
	public void refreshView() {
		int currentItem = binding.instancesDialogViewpager.getCurrentItem();
		binding.instancesDialogViewpager.getAdapter().notifyItemRangeChanged(currentItem - 3, 6);
	}

	@Override
	public Map<Integer, CalendarInstance> getInstances(long begin, long end) {
		return calendarViewModel.getInstances(begin, end);
	}

	@Override
	public void showPopupMenu(ContentValues instance, View anchorView, int gravity) {
		PopupMenu popupMenu = new PopupMenu(getContext(), anchorView, gravity);

		popupMenu.getMenuInflater().inflate(R.menu.instance_dialog_menu, popupMenu.getMenu());
		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@SuppressLint("NonConstantResourceId")
			@Override
			public boolean onMenuItemClick(MenuItem menuItem) {
				switch (menuItem.getItemId()) {
					case R.id.go_to_today_menu:
						goToToday();
						break;
					case R.id.go_to_first_selected_day_menu:
						goToFirstSelectedDay();
						break;
				}
				return true;
			}
		});

		popupMenu.show();
	}

	private void goToFirstSelectedDay() {
		refreshView();

		if (binding.instancesDialogViewpager.getCurrentItem() != EventTransactionFragment.FIRST_VIEW_POSITION) {
			binding.instancesDialogViewpager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION, true);
		} else {
		}
	}

	public void goToToday() {
		Date today = new Date(System.currentTimeMillis());
		Date firstSelectedDay = new Date(begin);
		int dayDifference = ClockUtil.calcDayDifference(today, firstSelectedDay);

		refreshView();
		if (binding.instancesDialogViewpager.getCurrentItem() != EventTransactionFragment.FIRST_VIEW_POSITION + dayDifference) {
			binding.instancesDialogViewpager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION + dayDifference
					, true);
		} else {

		}
	}

	@Override
	public void deleteEvents(Set<ContentValues> instanceSet) {
		for (ContentValues instance : instanceSet) {
			CalendarInstanceUtil.deleteEvent(calendarViewModel, locationViewModel
					, foodCriteriaLocationInfoViewModel, foodCriteriaLocationHistoryViewModel,
					instance.getAsLong(CalendarContract.Instances.EVENT_ID));
		}
		commonPopupMenu.onDeletedEvent(true);
	}


}
