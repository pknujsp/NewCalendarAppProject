package com.zerodsoft.scheduleweather.calendarview;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SyncStatusObserver;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Parcel;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.editevent.activity.NewEventFragment;
import com.zerodsoft.scheduleweather.activity.editevent.interfaces.OnEditEventResultListener;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.calendar.EditEventPopupMenu;
import com.zerodsoft.scheduleweather.calendar.selectedcalendar.SelectedCalendarViewModel;
import com.zerodsoft.scheduleweather.calendarview.assistantcalendar.assistantcalendar.AssistantForMonthFragment;
import com.zerodsoft.scheduleweather.calendarview.assistantcalendar.assistantcalendar.MonthAssistantCalendarFragment;
import com.zerodsoft.scheduleweather.calendarview.common.CalendarSharedViewModel;
import com.zerodsoft.scheduleweather.calendarview.day.DayFragment;
import com.zerodsoft.scheduleweather.calendarview.instancelistdaydialog.InstanceListOnADayDialogFragment;
import com.zerodsoft.scheduleweather.calendarview.instancelistweekdialog.InstanceListWeekDialogFragment;
import com.zerodsoft.scheduleweather.calendarview.interfaces.CalendarDateOnClickListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IRefreshView;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IToolbar;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnDateTimeChangedListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemLongClickListener;
import com.zerodsoft.scheduleweather.calendarview.month.MonthFragment;
import com.zerodsoft.scheduleweather.calendarview.week.WeekFragment;
import com.zerodsoft.scheduleweather.common.broadcastreceivers.DateTimeTickReceiver;
import com.zerodsoft.scheduleweather.common.classes.CloseWindow;
import com.zerodsoft.scheduleweather.common.enums.CalendarViewType;
import com.zerodsoft.scheduleweather.common.enums.EventIntentCode;
import com.zerodsoft.scheduleweather.common.interfaces.BroadcastReceiverCallback;
import com.zerodsoft.scheduleweather.databinding.FragmentCalendarBinding;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationHistoryViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationInfoViewModel;
import com.zerodsoft.scheduleweather.event.main.NewInstanceMainFragment;
import com.zerodsoft.scheduleweather.room.dto.SelectedCalendarDTO;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.utility.NetworkStatus;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class EventTransactionFragment extends Fragment implements OnEventItemClickListener, IRefreshView, IToolbar,
		CalendarDateOnClickListener,
		OnEventItemLongClickListener, OnDateTimeChangedListener {
	// 달력 프래그먼트를 관리하는 프래그먼트
	public static final int FIRST_VIEW_POSITION = Integer.MAX_VALUE / 2;
	private final View.OnClickListener drawerLayoutOnClickListener;
	private final SyncCalendar syncCalendar = new SyncCalendar();

	private CalendarViewModel calendarViewModel;
	private LocationViewModel locationViewModel;
	private SelectedCalendarViewModel selectedCalendarViewModel;
	private CalendarSharedViewModel calendarSharedViewModel;

	private List<SelectedCalendarDTO> selectedCalendarDTOList;
	private DateTimeTickReceiver dateTimeTickReceiver;

	private FoodCriteriaLocationHistoryViewModel foodCriteriaLocationHistoryViewModel;
	private FoodCriteriaLocationInfoViewModel foodCriteriaLocationInfoViewModel;
	private CloseWindow closeWindow = new CloseWindow(new CloseWindow.OnBackKeyDoubleClickedListener() {
		@Override
		public void onDoubleClicked() {
			requireActivity().finish();
		}
	});

	private Fragment currentFragment;
	private NetworkStatus networkStatus;
	private CalendarViewType calendarViewType;
	private Date currentCalendarDate;

	private FragmentCalendarBinding binding;

	private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
		@Override
		public void handleOnBackPressed() {
			FragmentManager parentFragmentManager = getParentFragmentManager();
			int fragmentSize = parentFragmentManager.getBackStackEntryCount();
			if (fragmentSize > 0) {
				parentFragmentManager.popBackStackImmediate();
			} else {
				fragmentSize = getChildFragmentManager().getBackStackEntryCount();
				if (fragmentSize > 0) {
					getChildFragmentManager().popBackStackImmediate();
				} else {
					closeWindow.clicked(getActivity());
				}
			}

		}
	};

	private final BroadcastReceiverCallback<String> dateTimeReceiverCallback = new BroadcastReceiverCallback<String>() {
		@Override
		public void onReceived(String action) {
			Date date = new Date(System.currentTimeMillis());

			switch (action) {
				case Intent.ACTION_TIME_TICK:
					if (!ClockUtil.HHmm.format(date).equals(ClockUtil.HHmm.toPattern())) {
						receivedTimeTick(date);
					}
					break;
				case Intent.ACTION_DATE_CHANGED:
					receivedDateChanged(date);
					break;
			}
		}
	};

	private final EditEventPopupMenu editEventPopupMenu = new EditEventPopupMenu() {

		@Override
		public void onClickedEditEvent(Fragment modificationFragment) {
			getParentFragmentManager().beginTransaction().add(R.id.fragment_container, modificationFragment,
					getString(R.string.tag_modify_instance_fragment)).addToBackStack(getString(R.string.tag_modify_instance_fragment)).commit();
		}
	};

	private final View.OnClickListener currMonthOnClickListener = new View.OnClickListener() {
		/*
		캘린더의 타입에 따라 다른 정보를 보여준다.
		 */
		@Override
		public void onClick(View view) {
			FragmentManager fragmentManager = getChildFragmentManager();

			MonthAssistantCalendarFragment monthAssistantCalendarFragment =
					(MonthAssistantCalendarFragment) fragmentManager.findFragmentByTag(getString(R.string.tag_assistant_calendar_for_day_week_fragment));
			AssistantForMonthFragment assistantForMonthFragment =
					(AssistantForMonthFragment) fragmentManager.findFragmentByTag(getString(R.string.tag_assistant_for_month_fragment));

			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

			if (fragmentManager.findFragmentByTag(WeekFragment.TAG) != null ||
					fragmentManager.findFragmentByTag(DayFragment.TAG) != null) {

				monthAssistantCalendarFragment.setCurrentMonth(currentCalendarDate);

				if (assistantForMonthFragment.isVisible()) {
					fragmentTransaction.hide(assistantForMonthFragment);
				}

				if (monthAssistantCalendarFragment.isHidden()) {
					binding.assistantCalendarContainer.setVisibility(View.VISIBLE);
					binding.mainToolbar.assistantCalendarControlImg.setImageDrawable(ContextCompat.getDrawable(getContext(),
							R.drawable.expand_less_icon));
					fragmentTransaction.show(monthAssistantCalendarFragment).commitNow();
				} else {
					binding.assistantCalendarContainer.setVisibility(View.GONE);
					binding.mainToolbar.assistantCalendarControlImg.setImageDrawable(ContextCompat.getDrawable(getContext(),
							R.drawable.expand_more_icon));
					fragmentTransaction.hide(monthAssistantCalendarFragment).commitNow();
				}
			} else if (fragmentManager.findFragmentByTag(MonthFragment.TAG) != null) {
				assistantForMonthFragment.setCurrentDate(currentCalendarDate);

				if (monthAssistantCalendarFragment.isVisible()) {
					fragmentTransaction.hide(monthAssistantCalendarFragment);
				}

				if (assistantForMonthFragment.isHidden()) {
					binding.assistantCalendarContainer.setVisibility(View.VISIBLE);
					binding.mainToolbar.assistantCalendarControlImg.setImageDrawable(ContextCompat.getDrawable(getContext(),
							R.drawable.expand_less_icon));
					fragmentTransaction.show(assistantForMonthFragment).commitNow();
				} else {
					binding.assistantCalendarContainer.setVisibility(View.GONE);
					binding.mainToolbar.assistantCalendarControlImg.setImageDrawable(ContextCompat.getDrawable(getContext(),
							R.drawable.expand_more_icon));
					fragmentTransaction.hide(assistantForMonthFragment).commitNow();
				}
			}
		}

	};

	private final FragmentManager.FragmentLifecycleCallbacks fragmentLifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() {
		@Override
		public void onFragmentAttached(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f, @NonNull @NotNull Context context) {
			super.onFragmentAttached(fm, f, context);
			if (f instanceof DayFragment) {
				binding.mainToolbar.assistantCalendarControlImg.setVisibility(View.VISIBLE);
				binding.mainToolbar.openList.setVisibility(View.VISIBLE);
			} else if (f instanceof WeekFragment) {
				binding.mainToolbar.assistantCalendarControlImg.setVisibility(View.VISIBLE);
				binding.mainToolbar.openList.setVisibility(View.VISIBLE);
			} else if (f instanceof MonthFragment) {
				binding.mainToolbar.assistantCalendarControlImg.setVisibility(View.VISIBLE);
				binding.mainToolbar.openList.setVisibility(View.GONE);
			}
		}

		@Override
		public void onFragmentDestroyed(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f) {
			super.onFragmentDestroyed(fm, f);
			if (f instanceof DayFragment) {

			} else if (f instanceof WeekFragment) {

			} else if (f instanceof MonthFragment) {

			}
		}
	};

	public EventTransactionFragment(CalendarViewType calendarViewType, View.OnClickListener drawerLayoutOnClickListener) {
		this.calendarViewType = calendarViewType;
		this.drawerLayoutOnClickListener = drawerLayoutOnClickListener;
	}

	@Override
	public void onAttach(@NonNull @NotNull Context context) {
		super.onAttach(context);
		requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getChildFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, false);
		networkStatus = new NetworkStatus(getContext(), new ConnectivityManager.NetworkCallback());

		dateTimeTickReceiver = new DateTimeTickReceiver(dateTimeReceiverCallback);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_TIME_TICK);
		intentFilter.addAction(Intent.ACTION_DATE_CHANGED);

		requireActivity().registerReceiver(dateTimeTickReceiver, intentFilter);

		selectedCalendarViewModel = new ViewModelProvider(requireActivity()).get(SelectedCalendarViewModel.class);
		calendarViewModel = new ViewModelProvider(requireActivity()).get(CalendarViewModel.class);
		locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
		foodCriteriaLocationInfoViewModel = new ViewModelProvider(this).get(FoodCriteriaLocationInfoViewModel.class);
		foodCriteriaLocationHistoryViewModel = new ViewModelProvider(this).get(FoodCriteriaLocationHistoryViewModel.class);
		calendarSharedViewModel = new ViewModelProvider(requireActivity()).get(CalendarSharedViewModel.class);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = FragmentCalendarBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		selectedCalendarViewModel.getOnListSelectedCalendarLiveData().observe(getViewLifecycleOwner(), new Observer<List<SelectedCalendarDTO>>() {
			@Override
			public void onChanged(List<SelectedCalendarDTO> selectedCalendarDTOS) {
				selectedCalendarDTOList = selectedCalendarDTOS;
			}
		});

		selectedCalendarViewModel.getOnAddedSelectedCalendarLiveData().observe(getViewLifecycleOwner(), new Observer<SelectedCalendarDTO>() {
			@Override
			public void onChanged(SelectedCalendarDTO selectedCalendarDTO) {
				selectedCalendarDTOList.add(selectedCalendarDTO);
			}
		});

		selectedCalendarViewModel.getOnDeletedSelectedCalendarLiveData().observe(getViewLifecycleOwner(), new Observer<List<SelectedCalendarDTO>>() {
			@Override
			public void onChanged(List<SelectedCalendarDTO> selectedCalendarDTOS) {
				selectedCalendarDTOList = selectedCalendarDTOS;
			}
		});
		init();
	}

	@Override
	public void onDestroy() {
		onBackPressedCallback.remove();
		networkStatus.unregisterNetworkCallback();
		requireActivity().unregisterReceiver(dateTimeTickReceiver);
		super.onDestroy();
	}

	private void init() {
		binding.mainToolbar.calendarMonth.setOnClickListener(toolbarOnClickListener);
		binding.mainToolbar.addSchedule.setOnClickListener(toolbarOnClickListener);
		binding.mainToolbar.goToToday.setOnClickListener(toolbarOnClickListener);
		binding.mainToolbar.openNavigationDrawer.setOnClickListener(toolbarOnClickListener);
		binding.mainToolbar.refreshCalendar.setOnClickListener(toolbarOnClickListener);

		binding.mainToolbar.openList.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Fragment primaryFragment = getChildFragmentManager().getPrimaryNavigationFragment();
				long[] times = null;

				if (primaryFragment instanceof DayFragment) {
					times = ((DayFragment) primaryFragment).getCurrentDate();
				} else if (primaryFragment instanceof WeekFragment) {
					times = ((WeekFragment) primaryFragment).getCurrentDate();
				}

				onClicked(times[0], times[1]);
			}
		});

		binding.mainToolbar.calendarMonth.setOnClickListener(currMonthOnClickListener);
		binding.mainToolbar.monthLayout.setOnClickListener(currMonthOnClickListener);

		//보조 캘린더(day, week) 프래그먼트 생성
		//보조 캘린더(month) 프래그먼트 생성

		MonthAssistantCalendarFragment monthAssistantCalendarFragment = new MonthAssistantCalendarFragment(this);
		AssistantForMonthFragment assistantForMonthFragment = new AssistantForMonthFragment(this);

		getChildFragmentManager().beginTransaction()
				.add(binding.assistantCalendarContainer.getId(), monthAssistantCalendarFragment,
						getString(R.string.tag_assistant_calendar_for_day_week_fragment))
				.add(binding.assistantCalendarContainer.getId(), assistantForMonthFragment,
						getString(R.string.tag_assistant_for_month_fragment))
				.hide(monthAssistantCalendarFragment)
				.hide(assistantForMonthFragment).commit();


		switch (calendarViewType) {
			case DAY:
				replaceFragment(DayFragment.TAG);
				break;
			case WEEK:
				replaceFragment(WeekFragment.TAG);
				break;
			case MONTH:
				replaceFragment(MonthFragment.TAG);
				break;
		}
	}

	public void replaceFragment(String fragmentTag) {
		FragmentManager childFragmentManager = getChildFragmentManager();

		switch (fragmentTag) {
			case MonthFragment.TAG:
				currentFragment = new MonthFragment(this, this, calendarViewModel);
				break;
			case WeekFragment.TAG:
				currentFragment = new WeekFragment(this, this);
				break;
			case DayFragment.TAG:
				currentFragment = new DayFragment(this, this);
				break;
		}

		if (binding.assistantCalendarContainer.getVisibility() == View.VISIBLE) {
			binding.mainToolbar.monthLayout.callOnClick();
		}

		childFragmentManager.beginTransaction().replace(R.id.calendar_container_view, currentFragment,
				fragmentTag).setPrimaryNavigationFragment(currentFragment).commit();
	}

	public void goToToday() {
		if (currentFragment instanceof MonthFragment) {
			((MonthFragment) currentFragment).goToToday();
		} else if (currentFragment instanceof WeekFragment) {
			((WeekFragment) currentFragment).goToToday();
		} else if (currentFragment instanceof DayFragment) {
			((DayFragment) currentFragment).goToToday();
		}
	}


	@Override
	public void onClicked(long viewBegin, long viewEnd) {
		// 이벤트 리스트 프래그먼트 다이얼로그 중복 호출 방지
		FragmentManager fragmentManager = getParentFragmentManager();
		if (fragmentManager.findFragmentByTag(InstanceListOnADayDialogFragment.TAG) != null ||
				fragmentManager.findFragmentByTag(getString(R.string.tag_instance_list_week_dialog_fragment)) != null) {
			return;
		}

		// 이벤트 리스트 프래그먼트 다이얼로그 표시
		Bundle bundle = new Bundle();
		bundle.putLong("begin", viewBegin);
		bundle.putLong("end", viewEnd);

		int difference = ClockUtil.calcDayDifference(new Date(viewBegin), new Date(viewEnd));
		if (difference == -1) {
			InstanceListOnADayDialogFragment fragment = new InstanceListOnADayDialogFragment(instanceListDialogIConnectedCalendars, this);
			fragment.setArguments(bundle);

			//현재 표시중인 프래그먼트를 숨기고, 인스턴스 프래그먼트를 표시
			fragment.show(fragmentManager, InstanceListOnADayDialogFragment.TAG);
		} else {
			InstanceListWeekDialogFragment fragment = new InstanceListWeekDialogFragment(instanceListDialogIConnectedCalendars, this);
			fragment.setArguments(bundle);

			//현재 표시중인 프래그먼트를 숨기고, 인스턴스 프래그먼트를 표시
			fragment.show(fragmentManager, getString(R.string.tag_instance_list_week_dialog_fragment));
		}
	}

	private final IConnectedCalendars instanceListDialogIConnectedCalendars = new IConnectedCalendars() {
		@Override
		public List<SelectedCalendarDTO> getConnectedCalendars() {
			return selectedCalendarDTOList;
		}
	};

	@Override
	public void onClicked(int calendarId, long instanceId, long eventId, long viewBegin, long viewEnd) {
		// 이벤트 정보 액티비티로 전환
		if (networkStatus.networkAvailable()) {
			Bundle bundle = new Bundle();
			bundle.putLong(CalendarContract.Instances._ID, instanceId);
			bundle.putLong(CalendarContract.Instances.EVENT_ID, eventId);
			bundle.putInt(CalendarContract.Instances.CALENDAR_ID, calendarId);
			bundle.putLong(CalendarContract.Instances.BEGIN, viewBegin);
			bundle.putLong(CalendarContract.Instances.END, viewEnd);

			openEventInfoFragment(bundle);

			DialogFragment instanceListOnADayDialogFragment = (DialogFragment) getParentFragmentManager().findFragmentByTag(InstanceListOnADayDialogFragment.TAG);
			DialogFragment instanceListWeekDialogFragment =
					(DialogFragment) getParentFragmentManager().findFragmentByTag(getString(R.string.tag_instance_list_week_dialog_fragment));

			if (instanceListOnADayDialogFragment != null) {
				instanceListOnADayDialogFragment.dismiss();
			} else if (instanceListWeekDialogFragment != null) {
				instanceListWeekDialogFragment.dismiss();
			}
		}
	}

	public void openEventInfoFragment(Bundle bundle) {
		NewInstanceMainFragment newInstanceMainFragment = new NewInstanceMainFragment();
		newInstanceMainFragment.setArguments(bundle);

		newInstanceMainFragment.setPlaceBottomSheetSelectBtnVisibility(View.GONE);
		newInstanceMainFragment.setPlaceBottomSheetUnSelectBtnVisibility(View.GONE);
		getParentFragmentManager().beginTransaction().add(R.id.fragment_container, newInstanceMainFragment,
				getString(R.string.tag_instance_main_fragment)).addToBackStack(getString(R.string.tag_instance_main_fragment))
				.commit();
	}

	@Override
	public void onClickedOnDialog(int calendarId, long instanceId, long eventId, long viewBegin, long viewEnd) {
		onClicked(calendarId, instanceId, eventId, viewBegin, viewEnd);
		DialogFragment fragment = (DialogFragment) getParentFragmentManager().findFragmentByTag(InstanceListOnADayDialogFragment.TAG);
		fragment.dismiss();
	}

	public void changeDate(Date date) {
		if (currentFragment instanceof WeekFragment) {
			((WeekFragment) currentFragment).goToWeek(date);
			//선택된 날짜에 해당 하는 주로 이동 (parameter : 2020년 2주차 -> 2020년 2주차로 이동)
		} else if (currentFragment instanceof DayFragment) {
			((DayFragment) currentFragment).goToToday(date);
		} else if (currentFragment instanceof MonthFragment) {
			((MonthFragment) currentFragment).goToDate(date);
		}
	}

	@Override
	public void refreshView() {
		//일정이 추가/삭제되면 영향을 받은 일정의 시작날짜에 해당하는 달력의 위치로 이동한다.
		if (currentFragment instanceof MonthFragment) {
			((MonthFragment) currentFragment).refreshView();
		} else if (currentFragment instanceof WeekFragment) {
			((WeekFragment) currentFragment).refreshView();
		} else if (currentFragment instanceof DayFragment) {
			((DayFragment) currentFragment).refreshView();
		}

		MonthAssistantCalendarFragment monthAssistantCalendarFragment =
				(MonthAssistantCalendarFragment) getChildFragmentManager().findFragmentByTag(MonthAssistantCalendarFragment.TAG);
		monthAssistantCalendarFragment.refreshView();
	}

	@Override
	public void createInstancePopupMenu(ContentValues instance, View anchorView, int gravity) {
		editEventPopupMenu.createEditEventPopupMenu(instance, requireActivity(), anchorView, gravity
				, calendarViewModel);
	}

	@Override
	public void receivedTimeTick(Date date) {
		Fragment fragment = getChildFragmentManager().getPrimaryNavigationFragment();
		if (fragment instanceof WeekFragment) {
			((WeekFragment) fragment).receivedTimeTick(date);
		} else if (fragment instanceof DayFragment) {
			((DayFragment) fragment).receivedTimeTick(date);
		}
	}

	@Override
	public void receivedDateChanged(Date date) {
		Fragment fragment = getChildFragmentManager().getPrimaryNavigationFragment();
		if (fragment instanceof WeekFragment) {
			((WeekFragment) fragment).receivedDateChanged(date);
		} else if (fragment instanceof DayFragment) {
			((DayFragment) fragment).receivedDateChanged(date);
		} else if (fragment instanceof MonthFragment) {
			((MonthFragment) fragment).receivedDateChanged(date);
		}
	}

	@Override
	public void setMonth(Date dateTime) {
		currentCalendarDate = dateTime;
		binding.mainToolbar.calendarMonth.setText(ClockUtil.YEAR_MONTH_FORMAT.format(dateTime));
	}

	@Override
	public void onClickedDate(Date date) {
		changeDate(date);
	}

	@Override
	public void onClickedMonth(Date date) {

	}

	@SuppressLint("NonConstantResourceId")
	public void onClickToolbar(View view) {

		switch (view.getId()) {
			case R.id.open_navigation_drawer:
				drawerLayoutOnClickListener.onClick(view);
				break;

			case R.id.add_schedule:
				NewEventFragment newEventFragment = new NewEventFragment(new OnEditEventResultListener() {
					@Override
					public void onSavedNewEvent(long dtStart) {
						getParentFragmentManager().popBackStackImmediate();
					}

					@Override
					public void onUpdatedOnlyThisEvent(long dtStart) {

					}

					@Override
					public void onUpdatedFollowingEvents(long dtStart) {

					}

					@Override
					public void onUpdatedAllEvents(long dtStart) {

					}

					@Override
					public void onRemovedAllEvents() {

					}

					@Override
					public void onRemovedFollowingEvents() {

					}

					@Override
					public void onRemovedOnlyThisEvents() {

					}

					@Override
					public int describeContents() {
						return 0;
					}

					@Override
					public void writeToParcel(Parcel dest, int flags) {

					}
				});

				Bundle bundle = new Bundle();
				bundle.putInt("requestCode", EventIntentCode.REQUEST_NEW_EVENT.value());
				newEventFragment.setArguments(bundle);

				getParentFragmentManager().beginTransaction().hide(EventTransactionFragment.this)
						.add(R.id.fragment_container, newEventFragment, getString(R.string.tag_new_event_fragment))
						.addToBackStack(getString(R.string.tag_new_event_fragment)).commit();
				break;

			case R.id.go_to_today:
				goToToday();
				break;

			case R.id.refresh_calendar:
				syncCalendar.syncCalendars(new SyncCallback() {
					@Override
					public void onSyncStarted() {
						requireActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								binding.mainToolbar.refreshCalendar.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.rotate));
								Toast.makeText(requireActivity(), R.string.sync_started, Toast.LENGTH_SHORT).show();
							}
						});
					}

					@Override
					public void onSyncFinished() {
						requireActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								refreshView();
								binding.mainToolbar.refreshCalendar.clearAnimation();
								Toast.makeText(requireActivity(), R.string.sync_finished, Toast.LENGTH_SHORT).show();
							}
						});

					}
				});
				break;
		}
	}

	private final View.OnClickListener toolbarOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			onClickToolbar(view);
		}
	};


	final class SyncCalendar {
		private List<Account> accountList = new ArrayList<>();

		class CalendarSyncStatusObserver implements SyncStatusObserver {
			private final int PENDING = 0;
			private final int PENDING_ACTIVE = 10;
			private final int ACTIVE = 20;
			private final int FINISHED = 30;

			private SyncCallback syncCallback;

			private Object mProviderHandle;

			public void setProviderHandle(@NonNull final Object providerHandle) {
				mProviderHandle = providerHandle;
			}

			public void setSyncCallback(@NonNull SyncCallback syncCallback) {
				this.syncCallback = syncCallback;
			}

			private final Map<Account, Integer> mAccountSyncState =
					Collections.synchronizedMap(new HashMap<Account, Integer>());

			private final String mCalendarAuthority = CalendarContract.AUTHORITY;

			@Override
			public void onStatusChanged(int which) {
				for (Account account : accountList) {
					if (which == ContentResolver.SYNC_OBSERVER_TYPE_PENDING) {
						if (ContentResolver.isSyncPending(account, mCalendarAuthority)) {
							// There is now a pending sync.
							mAccountSyncState.put(account, PENDING);
						} else {
							// There is no longer a pending sync.
							mAccountSyncState.put(account, PENDING_ACTIVE);
						}
					} else if (which == ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE) {
						if (ContentResolver.isSyncActive(account, mCalendarAuthority)) {
							// There is now an active sync.
							mAccountSyncState.put(account, ACTIVE);
						} else {
							// There is no longer an active sync.
							mAccountSyncState.put(account, FINISHED);
						}
					}
				}

				// We haven't finished processing sync states for all accountList yet
				if (accountList.size() != mAccountSyncState.size())
					return;

				// Check if any accountList are not finished syncing yet. If so bail
				for (Integer syncState : mAccountSyncState.values()) {
					if (syncState != FINISHED)
						return;
				}

				//finished
				if (mProviderHandle != null) {
					ContentResolver.removeStatusChangeListener(mProviderHandle);
					mProviderHandle = null;
				}
				if (syncCallback != null) {
					syncCallback.onSyncFinished();
					syncCallback = null;
				}
				mAccountSyncState.clear();
			}

		}

		public void syncCalendars(SyncCallback syncCallback) {
/*
			accountList = AccountManager.get(getContext()).getAccountsByType("com.google");
			List<ContentValues> allGoogleAccountList = calendarViewModel.getGoogleAccounts();
			Set<String> allGoogleAccountEmailSet = new HashSet<>();

			for (ContentValues googleAccount : allGoogleAccountList) {
				allGoogleAccountEmailSet.add(googleAccount.getAsString(CalendarContract.Calendars.ACCOUNT_NAME));
			}

			if (accountList.length != allGoogleAccountEmailSet.size()) {
				Intent intent = AccountManager.newChooseAccountIntent(null, null, new String[]{"com.google"}, null, null, null, null);
				accountsResultLauncher.launch(intent);
			} else {


				try {
					GoogleAuthUtil.requestGoogleAccountsAccess(getContext());
				} catch (Exception e) {

				}
			}
*/
			//-------------------------------------------------------------------------------------------
			syncCallback.onSyncStarted();

			accountList.clear();
			List<ContentValues> allGoogleAccountList = calendarViewModel.getGoogleAccounts();
			Set<String> accountNameSet = new HashSet<>();

			for (ContentValues contentValues : allGoogleAccountList) {
				if (contentValues.getAsString(CalendarContract.Calendars.ACCOUNT_TYPE).equals("com.google")) {
					if (!accountNameSet.contains(contentValues.getAsString(CalendarContract.Calendars.ACCOUNT_NAME))) {
						Account account = new Account(contentValues.getAsString(CalendarContract.Calendars.ACCOUNT_NAME)
								, contentValues.getAsString(CalendarContract.Calendars.ACCOUNT_TYPE));

						accountNameSet.add(account.name);
						accountList.add(account);
					}
				}
			}
			CalendarSyncStatusObserver calendarSyncStatusObserver = new CalendarSyncStatusObserver();
			calendarSyncStatusObserver.setProviderHandle(ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE |
					ContentResolver.SYNC_OBSERVER_TYPE_PENDING, calendarSyncStatusObserver));
			calendarSyncStatusObserver.setSyncCallback(syncCallback);

			for (Account account : accountList) {
				Bundle extras = new Bundle();
				extras.putBoolean(
						ContentResolver.SYNC_EXTRAS_MANUAL, true);
				extras.putBoolean(
						ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
				ContentResolver.requestSync(account, CalendarContract.AUTHORITY, extras);
			}

		}
	}

	interface SyncCallback {
		void onSyncStarted();

		void onSyncFinished();
	}

	public interface OnOpenListBtnListener {
		void onClicked(long begin, long end);
	}
}

