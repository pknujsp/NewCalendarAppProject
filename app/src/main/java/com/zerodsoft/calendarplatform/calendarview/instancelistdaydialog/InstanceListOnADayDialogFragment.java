package com.zerodsoft.calendarplatform.calendarview.instancelistdaydialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.PopupMenu;

import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.activity.editevent.interfaces.OnEditEventResultListener;
import com.zerodsoft.calendarplatform.calendar.AsyncQueryService;
import com.zerodsoft.calendarplatform.calendar.CalendarViewModel;
import com.zerodsoft.calendarplatform.calendar.EditEventPopupMenu;
import com.zerodsoft.calendarplatform.calendar.EventHelper;
import com.zerodsoft.calendarplatform.calendar.dto.CalendarInstance;
import com.zerodsoft.calendarplatform.calendarview.EventTransactionFragment;
import com.zerodsoft.calendarplatform.calendarview.instancelistdaydialog.adapter.InstancesOfDayAdapter;
import com.zerodsoft.calendarplatform.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.calendarplatform.calendarview.interfaces.IControlEvent;
import com.zerodsoft.calendarplatform.calendarview.interfaces.IRefreshView;
import com.zerodsoft.calendarplatform.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.calendarplatform.calendarview.interfaces.OnEventItemLongClickListener;
import com.zerodsoft.calendarplatform.databinding.FragmentInstanceListOnADayBinding;
import com.zerodsoft.calendarplatform.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.calendarplatform.utility.ClockUtil;

import java.util.ArrayList;
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
	private final OnEditEventResultListener onEditEventResultListener;

	private CalendarViewModel calendarViewModel;
	private LocationViewModel locationViewModel;
	private FragmentInstanceListOnADayBinding binding;
	private InstancesOfDayAdapter adapter;
	private CompositePageTransformer compositePageTransformer;

	private Long begin;
	private Long end;
	private boolean initializing = true;

	private PopupMenu eventPopupMenu;

	private final EditEventPopupMenu editEventPopupMenu = new EditEventPopupMenu() {

		@Override
		public void onClickedEditEvent(Fragment modificationFragment) {
			dismiss();
			getParentFragmentManager().beginTransaction().add(R.id.fragment_container, modificationFragment,
					getString(R.string.tag_modify_instance_fragment)).addToBackStack(getString(R.string.tag_modify_instance_fragment)).commit();
		}
	};

	public InstanceListOnADayDialogFragment(IConnectedCalendars iConnectedCalendars, Fragment fragment, OnEditEventResultListener onEditEventResultListener) {
		this.onEventItemClickListener = (OnEventItemClickListener) fragment;
		this.iRefreshView = (IRefreshView) fragment;
		this.iConnectedCalendars = iConnectedCalendars;
		this.onEditEventResultListener = onEditEventResultListener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();

		begin = bundle.getLong("begin");
		end = bundle.getLong("end");

		calendarViewModel = new ViewModelProvider(requireActivity()).get(CalendarViewModel.class);
		calendarViewModel.getOnRemovedEventLiveData().observe(this, new Observer<Boolean>() {
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

		calendarViewModel.getOnExceptedInstanceLiveData().observe(this, new Observer<Boolean>() {
			@Override
			public void onChanged(Boolean aBoolean) {
				if (!initializing) {
					refreshView();
				}
			}
		});

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
		binding.instancesDialogViewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
			int lastPosition = EventTransactionFragment.FIRST_VIEW_POSITION;

			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);

				if (eventPopupMenu != null) {
					eventPopupMenu.dismiss();
					eventPopupMenu = null;
				}

				adapter.notifyItemChanged(lastPosition);
				lastPosition = position;
			}
		});

		initializing = false;
	}

	@Override
	public void createInstancePopupMenu(ContentValues instance, View anchorView, int gravity) {
		eventPopupMenu = editEventPopupMenu.createEditEventPopupMenu(instance, requireActivity(), anchorView, Gravity.CENTER,
				new EditEventPopupMenu.OnEditedEventCallback() {
					@Override
					public void onRemoved() {

					}
				}, onEditEventResultListener);
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
		EventHelper eventHelper = new EventHelper(new AsyncQueryService(getContext(), calendarViewModel));
		ArrayList<ContentProviderOperation> ops = new ArrayList<>();

		for (ContentValues instance : instanceSet) {
			eventHelper.removeEvent(EventHelper.EventEditType.REMOVE_ALL_EVENTS, instance);
		}

	}


}

