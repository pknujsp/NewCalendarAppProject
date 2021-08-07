package com.zerodsoft.scheduleweather.calendarview.instancelistweekdialog;

import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
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

import android.provider.CalendarContract;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.PopupMenu;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.AsyncQueryService;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.calendar.EditEventPopupMenu;
import com.zerodsoft.scheduleweather.calendar.EventHelper;
import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendarview.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarview.instancelistdaydialog.InstancesOfDayView;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IControlEvent;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IRefreshView;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemLongClickListener;
import com.zerodsoft.scheduleweather.databinding.FragmentInstanceListWeekDialogBinding;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationHistoryViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationInfoViewModel;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;


public class InstanceListWeekDialogFragment extends DialogFragment implements OnEventItemLongClickListener, IRefreshView, IControlEvent
		, InstancesOfDayView.DeleteEventsListener {
	private final IConnectedCalendars iConnectedCalendars;
	private final OnEventItemClickListener onEventItemClickListener;

	private FragmentInstanceListWeekDialogBinding binding;

	private CalendarViewModel calendarViewModel;
	private LocationViewModel locationViewModel;
	private instanceListWeekViewPagerAdapter adapter;
	private CompositePageTransformer compositePageTransformer;

	private Long weekBegin;
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

	public InstanceListWeekDialogFragment(IConnectedCalendars iConnectedCalendars, OnEventItemClickListener onEventItemClickListener) {
		this.onEventItemClickListener = onEventItemClickListener;
		this.iConnectedCalendars = iConnectedCalendars;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		return new Dialog(requireContext(), R.style.DialogTransparent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();

		weekBegin = bundle.getLong("begin");

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

		calendarViewModel.getOnModifiedInstanceLiveData().observe(this, new Observer<Long>() {
			@Override
			public void onChanged(Long aLong) {
				if (!initializing) {
					refreshView();
				}
			}
		});

		calendarViewModel.getOnModifiedFutureInstancesLiveData().observe(this, new Observer<Long>() {
			@Override
			public void onChanged(Long aLong) {
				if (!initializing) {
					refreshView();
				}
			}
		});

		calendarViewModel.getOnModifiedEventLiveData().observe(this, new Observer<Long>() {
			@Override
			public void onChanged(Long aLong) {
				if (!initializing) {
					refreshView();
				}
			}
		});

		calendarViewModel.getOnAddedNewEventLiveData().observe(this, new Observer<Long>() {
			@Override
			public void onChanged(Long aLong) {
				if (!initializing) {
					refreshView();
				}
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentInstanceListWeekDialogBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

		binding.goToThisWeekButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				goToThisWeek();
			}
		});

		binding.goToFirstSelectedWeekButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				goToFirstSelectedWeek();
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

		adapter = new instanceListWeekViewPagerAdapter(weekBegin, onEventItemClickListener, iConnectedCalendars, this);
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
				calendarViewModel);
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


	private void goToFirstSelectedWeek() {
		refreshView();

		if (binding.instancesDialogViewpager.getCurrentItem() != EventTransactionFragment.FIRST_VIEW_POSITION) {
			binding.instancesDialogViewpager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION, true);
		} else {
		}
	}

	private void goToThisWeek() {
		Date today = new Date(System.currentTimeMillis());
		Date firstSelectedWeek = new Date(weekBegin);
		int weekDifference = ClockUtil.calcWeekDifference(today, firstSelectedWeek);

		refreshView();
		if (binding.instancesDialogViewpager.getCurrentItem() != EventTransactionFragment.FIRST_VIEW_POSITION + weekDifference) {
			binding.instancesDialogViewpager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION + weekDifference
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