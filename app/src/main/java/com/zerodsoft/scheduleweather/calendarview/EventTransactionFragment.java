package com.zerodsoft.scheduleweather.calendarview;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SyncStatusObserver;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.editevent.activity.NewEventFragment;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.calendar.CommonPopupMenu;
import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendarview.assistantcalendar.assistantcalendar.MonthAssistantCalendarFragment;
import com.zerodsoft.scheduleweather.calendarview.day.DayFragment;
import com.zerodsoft.scheduleweather.calendarview.instancedialog.InstanceListOnADayDialogFragment;
import com.zerodsoft.scheduleweather.calendarview.interfaces.CalendarDateOnClickListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IControlEvent;
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
import com.zerodsoft.scheduleweather.databinding.FragmentCalendarBinding;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationHistoryViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationInfoViewModel;
import com.zerodsoft.scheduleweather.event.main.NewInstanceMainFragment;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.utility.NetworkStatus;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class EventTransactionFragment extends Fragment implements IControlEvent, OnEventItemClickListener, IRefreshView, IToolbar,
		CalendarDateOnClickListener,
		OnEventItemLongClickListener, OnDateTimeChangedListener {
	// 달력 프래그먼트를 관리하는 프래그먼트
	public static final int FIRST_VIEW_POSITION = Integer.MAX_VALUE / 2;

	private final IConnectedCalendars iConnectedCalendars;
	private final View.OnClickListener drawerLayoutOnClickListener;
	private final SyncCalendar syncCalendar = new SyncCalendar();

	private CalendarViewModel calendarViewModel;
	private LocationViewModel locationViewModel;
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
	private TextView currMonthTextView;
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

	private final CarrierMessagingService.ResultCallback<String> dateTimeReceiverCallback =
			new CarrierMessagingService.ResultCallback<String>() {
				@Override
				public void onReceiveResult(@NonNull String action) throws RemoteException {
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

	private final CommonPopupMenu commonPopupMenu = new CommonPopupMenu() {
		@Override
		public void onExceptedInstance(boolean isSuccessful) {
			if (isSuccessful) {
				refreshView();
			}
		}

		@Override
		public void onDeletedEvent(boolean isSuccessful) {
			if (isSuccessful) {
				refreshView();
			}
		}
	};

	private final View.OnClickListener currMonthOnClickListener = new View.OnClickListener() {
		/*
		캘린더의 타입에 따라 다른 정보를 보여준다.
		 */
		@Override
		public void onClick(View view) {
			FragmentManager fragmentManager = getChildFragmentManager();
			if (fragmentManager.findFragmentByTag(WeekFragment.TAG) != null ||
					fragmentManager.findFragmentByTag(DayFragment.TAG) != null) {
				MonthAssistantCalendarFragment monthAssistantCalendarFragment =
						(MonthAssistantCalendarFragment) fragmentManager.findFragmentByTag(MonthAssistantCalendarFragment.TAG);

				monthAssistantCalendarFragment.setCurrentMonth(currentCalendarDate);
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

				if (monthAssistantCalendarFragment.isHidden()) {
					binding.assistantCalendarContainer.setVisibility(View.VISIBLE);
					fragmentTransaction.show(monthAssistantCalendarFragment).commitNow();
				} else {
					binding.assistantCalendarContainer.setVisibility(View.GONE);
					fragmentTransaction.hide(monthAssistantCalendarFragment).commitNow();
				}
			}
		}

	};

	public EventTransactionFragment(Activity activity, CalendarViewType calendarViewType, View.OnClickListener drawerLayoutOnClickListener) {
		this.iConnectedCalendars = (IConnectedCalendars) activity;
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
		networkStatus = new NetworkStatus(getContext(), new ConnectivityManager.NetworkCallback());

		calendarViewModel = new ViewModelProvider(requireActivity()).get(CalendarViewModel.class);
		locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
		foodCriteriaLocationInfoViewModel = new ViewModelProvider(this).get(FoodCriteriaLocationInfoViewModel.class);
		foodCriteriaLocationHistoryViewModel = new ViewModelProvider(this).get(FoodCriteriaLocationHistoryViewModel.class);

		DateTimeTickReceiver dateTimeTickReceiver = DateTimeTickReceiver.newInstance(dateTimeReceiverCallback);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_TIME_TICK);
		intentFilter.addAction(Intent.ACTION_DATE_CHANGED);

		requireActivity().registerReceiver(dateTimeTickReceiver, intentFilter);
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
		init();
	}

	@Override
	public void onDestroy() {
		onBackPressedCallback.remove();
		networkStatus.unregisterNetworkCallback();
		requireActivity().unregisterReceiver(DateTimeTickReceiver.getInstance());
		super.onDestroy();
	}

	private void init() {
		binding.mainToolbar.calendarMonth.setOnClickListener(toolbarOnClickListener);
		binding.mainToolbar.addSchedule.setOnClickListener(toolbarOnClickListener);
		binding.mainToolbar.goToToday.setOnClickListener(toolbarOnClickListener);
		binding.mainToolbar.openNavigationDrawer.setOnClickListener(toolbarOnClickListener);
		binding.mainToolbar.refreshCalendar.setOnClickListener(toolbarOnClickListener);

		currMonthTextView = binding.mainToolbar.calendarMonth;
		currMonthTextView.setOnClickListener(currMonthOnClickListener);

		//보조 캘린더 프래그먼트 생성
		binding.assistantCalendarContainer.setVisibility(View.GONE);
		Fragment monthAssistantCalendarFragment = new MonthAssistantCalendarFragment(iConnectedCalendars, this);
		getChildFragmentManager().beginTransaction().add(binding.assistantCalendarContainer.getId(), monthAssistantCalendarFragment,
				MonthAssistantCalendarFragment.TAG).hide(monthAssistantCalendarFragment).commitNow();

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
				currentFragment = new MonthFragment(this, this, iConnectedCalendars, calendarViewModel);
				if (childFragmentManager.findFragmentByTag(MonthAssistantCalendarFragment.TAG).isVisible()) {
					binding.assistantCalendarContainer.setVisibility(View.GONE);
					childFragmentManager.beginTransaction().hide((MonthAssistantCalendarFragment) childFragmentManager.findFragmentByTag(MonthAssistantCalendarFragment.TAG)).commit();
				}
				break;
			case WeekFragment.TAG:
				currentFragment = new WeekFragment(this, this, iConnectedCalendars);
				break;
			case DayFragment.TAG:
				currentFragment = new DayFragment(this, this, iConnectedCalendars);
				break;
		}
		childFragmentManager.beginTransaction().replace(R.id.calendar_container_view, currentFragment,
				fragmentTag).setPrimaryNavigationFragment(currentFragment).commit();
	}

	@Override
	public Map<Integer, CalendarInstance> getInstances(long begin, long end) {
		// 선택된 캘린더 목록
		return calendarViewModel.getInstances(begin, end);
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
		// 이벤트 리스트 프래그먼트 다이얼로그 표시
		Bundle bundle = new Bundle();
		bundle.putLong("begin", viewBegin);
		bundle.putLong("end", viewEnd);

		InstanceListOnADayDialogFragment fragment = new InstanceListOnADayDialogFragment(iConnectedCalendars, this);
		fragment.setArguments(bundle);

		//현재 표시중인 프래그먼트를 숨기고, 인스턴스 프래그먼트를 표시
		fragment.show(getParentFragmentManager(), InstanceListOnADayDialogFragment.TAG);
	}

	@Override
	public void onClicked(int calendarId, long instanceId, long eventId, long viewBegin, long viewEnd) {
		// 이벤트 정보 액티비티로 전환
		if (networkStatus.networkAvailable()) {
			NewInstanceMainFragment newInstanceMainFragment = new NewInstanceMainFragment(calendarId, eventId, instanceId, viewBegin, viewEnd);
			newInstanceMainFragment.setPlaceBottomSheetSelectBtnVisibility(View.GONE);
			newInstanceMainFragment.setPlaceBottomSheetUnSelectBtnVisibility(View.GONE);
			getParentFragmentManager().beginTransaction().add(R.id.fragment_container, newInstanceMainFragment,
					getString(R.string.tag_instance_main_fragment)).addToBackStack(getString(R.string.tag_instance_main_fragment))
					.commit();

			DialogFragment instanceListOnADayDialogFragment = (DialogFragment) getParentFragmentManager().findFragmentByTag(InstanceListOnADayDialogFragment.TAG);
			if (instanceListOnADayDialogFragment != null) {
				instanceListOnADayDialogFragment.dismiss();
			}
		}
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
		commonPopupMenu.createInstancePopupMenu(instance, requireActivity(), anchorView, gravity
				, calendarViewModel, locationViewModel, foodCriteriaLocationInfoViewModel, foodCriteriaLocationHistoryViewModel);
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
		}
	}

	@Override
	public void setMonth(Date dateTime) {
		currentCalendarDate = dateTime;
		currMonthTextView.setText(ClockUtil.YEAR_MONTH_FORMAT.format(dateTime));
	}

	@Override
	public void onClickedDate(Date date) {
		Toast.makeText(getContext(), ClockUtil.YYYY_M_D_E.format(date), Toast.LENGTH_SHORT).show();
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
				NewEventFragment newEventFragment = new NewEventFragment(new NewEventFragment.OnNewEventResultListener() {
					@Override
					public void onSavedNewEvent(long eventId, long begin) {
						//새로운 일정이 추가됨 -> 달력 이벤트 갱신 -> 추가한 이벤트의 첫번째 인스턴스가 있는 날짜로 달력을 이동
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

	private final ActivityResultLauncher<Intent> instanceActivityResultLauncher = registerForActivityResult(
			new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
				@Override
				public void onActivityResult(ActivityResult result) {
					try {
						EventIntentCode resultCode = EventIntentCode.enumOf(result.getResultCode());
						switch (resultCode) {
							case RESULT_DELETED:
								refreshView();
							case RESULT_EXCEPTED_INSTANCE:
								refreshView();
								break;
							case RESULT_MODIFIED_AFTER_INSTANCE_INCLUDING_THIS_INSTANCE:
							case RESULT_MODIFIED_THIS_INSTANCE:
							case RESULT_MODIFIED_EVENT:
								refreshView();
								break;
						}
					} catch (IllegalArgumentException e) {

					}


				}
			}
	);

	private final ActivityResultLauncher<Intent> accountsResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult()
			, new ActivityResultCallback<ActivityResult>() {
				@Override
				public void onActivityResult(ActivityResult result) {
					if (result.getData() != null) {
						Account[] accounts = AccountManager.get(getContext()).getAccounts();
						int accountsSize = accounts.length;
					}
				}
			});

	final class SyncCalendar {
		private Account[] accounts;

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
				for (Account account : accounts) {
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

				// We haven't finished processing sync states for all accounts yet
				if (accounts.length != mAccountSyncState.size())
					return;

				// Check if any accounts are not finished syncing yet. If so bail
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
			syncCallback.onSyncStarted();
			//Intent intent = AccountManager.newChooseAccountIntent(null, null, new String[]{"com.google"}, null, null, null, null);
			//accountsResultLauncher.launch(intent);

			CalendarSyncStatusObserver calendarSyncStatusObserver = new CalendarSyncStatusObserver();
			calendarSyncStatusObserver.setProviderHandle(ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE |
					ContentResolver.SYNC_OBSERVER_TYPE_PENDING, calendarSyncStatusObserver));
			calendarSyncStatusObserver.setSyncCallback(syncCallback);

			accounts = AccountManager.get(getContext()).getAccountsByType("com.google");

			for (Account account : accounts) {
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
}

