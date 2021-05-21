package com.zerodsoft.scheduleweather.calendarview;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.editevent.activity.EditEventActivity;
import com.zerodsoft.scheduleweather.activity.editevent.value.EventDataController;
import com.zerodsoft.scheduleweather.activity.main.AppMainActivity;
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
import com.zerodsoft.scheduleweather.calendarview.interfaces.IstartActivity;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnDateTimeChangedListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemLongClickListener;
import com.zerodsoft.scheduleweather.calendarview.month.MonthFragment;
import com.zerodsoft.scheduleweather.calendarview.week.WeekFragment;
import com.zerodsoft.scheduleweather.common.broadcastreceivers.DateTimeTickReceiver;
import com.zerodsoft.scheduleweather.common.enums.CalendarViewType;
import com.zerodsoft.scheduleweather.databinding.FragmentCalendarBinding;
import com.zerodsoft.scheduleweather.etc.AppPermission;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationHistoryViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationInfoViewModel;
import com.zerodsoft.scheduleweather.event.main.NewInstanceMainActivity;
import com.zerodsoft.scheduleweather.event.main.NewInstanceMainFragment;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.utility.NetworkStatus;

import java.util.Date;
import java.util.Map;

import lombok.SneakyThrows;

import static android.app.Activity.RESULT_OK;


public class EventTransactionFragment extends Fragment implements IControlEvent, OnEventItemClickListener, IRefreshView, IToolbar,
		CalendarDateOnClickListener,
		OnEventItemLongClickListener, OnDateTimeChangedListener {
	// 달력 프래그먼트를 관리하는 프래그먼트
	public static final String TAG = "CalendarTransactionFragment";
	public static final int FIRST_VIEW_POSITION = Integer.MAX_VALUE / 2;

	private final IConnectedCalendars iConnectedCalendars;
	private final View.OnClickListener drawerLayoutOnClickListener;

	private CalendarViewModel calendarViewModel;
	private LocationViewModel locationViewModel;
	private FoodCriteriaLocationHistoryViewModel foodCriteriaLocationHistoryViewModel;
	private FoodCriteriaLocationInfoViewModel foodCriteriaLocationInfoViewModel;

	private Fragment currentFragment;
	private NetworkStatus networkStatus;
	private CalendarViewType calendarViewType;
	private TextView currMonthTextView;
	private Date currentCalendarDate;

	private FragmentCalendarBinding binding;

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
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		networkStatus = new NetworkStatus(getContext(), new ConnectivityManager.NetworkCallback() {
		});

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
		calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
		locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
		foodCriteriaLocationHistoryViewModel = new ViewModelProvider(this).get(FoodCriteriaLocationHistoryViewModel.class);
		foodCriteriaLocationInfoViewModel = new ViewModelProvider(this).get(FoodCriteriaLocationInfoViewModel.class);

		init();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (AppPermission.grantedPermissions(getContext(), Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR)) {
			requireActivity().getContentResolver().registerContentObserver(CalendarContract.Events.CONTENT_URI, true, contentObserver);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		networkStatus.unregisterNetworkCallback();
		requireActivity().unregisterReceiver(DateTimeTickReceiver.getInstance());
		requireActivity().getContentResolver().unregisterContentObserver(contentObserver);
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
		FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

		switch (fragmentTag) {
			case MonthFragment.TAG:
				currentFragment = new MonthFragment(this, this, iConnectedCalendars);
				fragmentTransaction.replace(R.id.calendar_container_view, currentFragment, MonthFragment.TAG);
				break;
			case WeekFragment.TAG:
				currentFragment = new WeekFragment(this, this, iConnectedCalendars);
				fragmentTransaction.replace(R.id.calendar_container_view, currentFragment, WeekFragment.TAG);
				break;
			case DayFragment.TAG:
				currentFragment = new DayFragment(this, this, iConnectedCalendars);
				fragmentTransaction.replace(R.id.calendar_container_view, currentFragment, DayFragment.TAG);
				break;
		}
		fragmentTransaction.setPrimaryNavigationFragment(currentFragment);
		fragmentTransaction.commitNow();
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
			Intent intent = new Intent(getActivity(), NewInstanceMainActivity.class);
			Bundle bundle = new Bundle();

			bundle.putInt(CalendarContract.Instances.CALENDAR_ID, calendarId);
			bundle.putLong(CalendarContract.Instances._ID, instanceId);
			bundle.putLong(CalendarContract.Instances.EVENT_ID, eventId);
			bundle.putLong(CalendarContract.Instances.BEGIN, viewBegin);
			bundle.putLong(CalendarContract.Instances.END, viewEnd);
			intent.putExtras(bundle);

			instanceActivityResultLauncher.launch(intent);
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
			((DayFragment) currentFragment).goToDay(date);
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
		if (fragment instanceof MonthFragment) {

		} else if (fragment instanceof WeekFragment) {

		} else if (fragment instanceof DayFragment) {

		}
	}

	@Override
	public void receivedDateChanged(Date date) {
		Fragment fragment = getChildFragmentManager().getPrimaryNavigationFragment();
		if (fragment instanceof MonthFragment) {
			((MonthFragment) fragment).receivedDateChanged(date);
		} else if (fragment instanceof WeekFragment) {

		} else if (fragment instanceof DayFragment) {

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
				Intent intent = new Intent(requireActivity(), EditEventActivity.class);
				intent.putExtra("requestCode", EventDataController.NEW_EVENT);
				newEventActivityResultLauncher.launch(intent);
				break;
			case R.id.go_to_today:
				goToToday();
				break;
			case R.id.refresh_calendar:
				Toast.makeText(getContext(), "working", Toast.LENGTH_SHORT).show();
				calendarViewModel.syncCalendars();
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
					switch (result.getResultCode()) {
						case NewInstanceMainFragment.RESULT_REMOVED_EVENT:
						case NewInstanceMainFragment.RESULT_EXCEPTED_INSTANCE:
							break;
					}
					refreshView();
				}
			}
	);

	private final ActivityResultLauncher<Intent> newEventActivityResultLauncher =
			registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
				@Override
				public void onActivityResult(ActivityResult result) {
					switch (result.getResultCode()) {
						case RESULT_OK:
							//새로운 일정이 추가됨 -> 달력 이벤트 갱신
							refreshView();
							break;
					}
				}
			});

	private final ContentObserver contentObserver = new ContentObserver(new Handler()) {
		@Override
		public boolean deliverSelfNotifications() {
			return true;
		}

		@SneakyThrows
		@Override
		public void onChange(boolean selfChange) {
			AccountManager accountManager = AccountManager.get(getContext());
			Account[] accounts = accountManager.getAccountsByType("com.google");

			boolean check = false;
			for (Account account : accounts) {
				if (ContentResolver.isSyncActive(account, CalendarContract.AUTHORITY)) {
					check = true;
				} else {
					break;
				}
			}

			if (check) {
				Toast.makeText(getContext(), "SYNCED", Toast.LENGTH_SHORT).show();
				refreshView();
			}
		}
	};

}

