package com.zerodsoft.scheduleweather.activity.main;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Point;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.ArraySet;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.activity.preferences.SettingsActivity;
import com.zerodsoft.scheduleweather.calendar.CalendarProvider;
import com.zerodsoft.scheduleweather.calendarview.SideBarCalendarListAdapter;
import com.zerodsoft.scheduleweather.calendarview.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarview.day.DayFragment;
import com.zerodsoft.scheduleweather.calendarview.interfaces.ICalendarCheckBox;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.scheduleweather.calendarview.month.MonthFragment;
import com.zerodsoft.scheduleweather.calendarview.week.WeekFragment;
import com.zerodsoft.scheduleweather.common.enums.CalendarViewType;
import com.zerodsoft.scheduleweather.databinding.ActivityAppMainBinding;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.calendar.dto.AccountDto;
import com.zerodsoft.scheduleweather.common.classes.AppPermission;
import com.zerodsoft.scheduleweather.favorites.AllFavoritesHostFragment;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;
import com.zerodsoft.scheduleweather.weather.dataprocessing.WeatherDataConverter;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AppMainActivity extends AppCompatActivity implements ICalendarCheckBox, IConnectedCalendars {
	private static int DISPLAY_WIDTH = 0;
	private static int DISPLAY_HEIGHT = 0;
	private static Map<String, ContentValues> connectedCalendarMap = new HashMap<>();
	private static List<ContentValues> connectedCalendarList = new ArrayList<>();
	private List<AccountDto> accountList;

	private ActivityAppMainBinding mainBinding;
	private CalendarViewModel calendarViewModel;
	private SideBarCalendarListAdapter sideBarCalendarListAdapter;

	public static int getDisplayHeight() {
		return DISPLAY_HEIGHT;
	}

	public static int getDisplayWidth() {
		return DISPLAY_WIDTH;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_app_main);
		calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);

		Point point = new Point();
		getWindowManager().getDefaultDisplay().getRealSize(point);

		DISPLAY_WIDTH = point.x;
		DISPLAY_HEIGHT = point.y;

		if (AppPermission.grantedPermissions(getApplicationContext(), Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR)) {
			//권한 확인
			List<ContentValues> calendars = calendarViewModel.getAllCalendars();
			initSideCalendars(calendars);
			init();
		} else {
			permissionsResultLauncher.launch(new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR});
		}
		// getAppKeyHash();
	}

	private void getAppKeyHash() {
		try {
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md;
				md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				String something = new String(Base64.encode(md.digest(), 0));
				Log.e("yyg", "key: " + something);
			}
		} catch (Exception e) {
		}
	}


	private void init() {
		WeatherDataConverter.context = getApplicationContext();
		App.setAppSettings(getApplicationContext());
		KakaoLocalApiCategoryUtil.loadCategories(getApplicationContext());

		mainBinding.sideNavMenu.favoriteLocation.setOnClickListener(sideNavOnClickListener);
		mainBinding.sideNavMenu.settings.setOnClickListener(sideNavOnClickListener);
		mainBinding.sideNavCalendarTypes.calendarTypeDay.setOnClickListener(calendarTypeOnClickListener);
		mainBinding.sideNavCalendarTypes.calendarTypeWeek.setOnClickListener(calendarTypeOnClickListener);
		mainBinding.sideNavCalendarTypes.calendarTypeMonth.setOnClickListener(calendarTypeOnClickListener);

		SharedPreferences preferences = getSharedPreferences(getString(R.string.preferences_selected_calendar_type_key), Context.MODE_PRIVATE);
		int type = preferences.getInt(getString(R.string.preferences_selected_calendar_type_key), CalendarViewType.MONTH.value());

		changeCalendar(CalendarViewType.enumOf(type));
		EventTransactionFragment eventTransactionFragment = new EventTransactionFragment(this, CalendarViewType.enumOf(type), drawLayoutBtnOnClickListener);

		getSupportFragmentManager().beginTransaction()
				.add(mainBinding.fragmentContainer.getId(), eventTransactionFragment,
						getString(R.string.tag_calendar_transaction_fragment)).commit();
	}


	private void initSideCalendars(List<ContentValues> calendarList) {
		// accountName을 기준으로 맵 구성
		Map<String, AccountDto> accountMap = new HashMap<>();

		for (ContentValues calendar : calendarList) {
			String accountName = calendar.getAsString(CalendarContract.Calendars.ACCOUNT_NAME);
			String ownerAccount = calendar.getAsString(CalendarContract.Calendars.OWNER_ACCOUNT);
			String accountType = calendar.getAsString(CalendarContract.Calendars.ACCOUNT_TYPE);

			if (accountMap.containsKey(accountName)) {
				accountMap.get(accountName).addCalendar(calendar);
			} else {
				AccountDto accountDto = new AccountDto();

				accountDto.setAccountName(accountName).setAccountType(accountType)
						.setOwnerAccount(ownerAccount).addCalendar(calendar);
				accountMap.put(accountName, accountDto);
			}
		}
		accountList = new ArrayList<>(accountMap.values());

		SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preferences_selected_calendars_key), Context.MODE_PRIVATE);
		Set<String> selectedCalendarSet = new ArraySet<>();
		Map<String, String> selectedCalendarsMap = (Map<String, String>) sharedPreferences.getAll();

		selectedCalendarSet.addAll(selectedCalendarsMap.values());

		final boolean[][] checkBoxStates = new boolean[accountList.size()][];

		for (int i = 0; i < accountList.size(); i++) {
			checkBoxStates[i] = new boolean[accountList.get(i).getCalendars().size()];
		}

		for (String key : selectedCalendarSet) {
			int group = 0;

			for (AccountDto accountDto : accountList) {
				List<ContentValues> selectedCalendarList = accountDto.getCalendars();
				int child = 0;

				for (ContentValues calendar : selectedCalendarList) {
					if (key.equals(calendar.getAsString(CalendarContract.Calendars.ACCOUNT_NAME) + "&"
							+ calendar.getAsString(CalendarContract.Calendars._ID))) {
						checkBoxStates[group][child] = true;
						connectedCalendarMap.put(key, calendar);
						connectedCalendarList.add(calendar);
						break;
					}
					child++;
				}
				group++;
			}
		}

		// 네비게이션 내 캘린더 리스트 구성
		sideBarCalendarListAdapter = new SideBarCalendarListAdapter(AppMainActivity.this, accountList, checkBoxStates);
		mainBinding.sideNavCalendarList.setAdapter(sideBarCalendarListAdapter);
		mainBinding.sideNavCalendarList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
				return false;
			}
		});
	}

	private final View.OnClickListener calendarTypeOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.preferences_selected_calendar_type_key), MODE_PRIVATE).edit();

			switch (view.getId()) {
				case R.id.calendar_type_day:
					editor.putInt(getString(R.string.preferences_selected_calendar_type_key), CalendarViewType.DAY.value()).apply();
					changeCalendar(CalendarViewType.DAY);
					break;
				case R.id.calendar_type_week:
					editor.putInt(getString(R.string.preferences_selected_calendar_type_key), CalendarViewType.WEEK.value()).apply();
					changeCalendar(CalendarViewType.WEEK);
					break;
				case R.id.calendar_type_month:
					editor.putInt(getString(R.string.preferences_selected_calendar_type_key), CalendarViewType.MONTH.value()).apply();
					changeCalendar(CalendarViewType.MONTH);
					break;
			}
			mainBinding.drawerLayout.closeDrawer(mainBinding.sideNavigation);
		}
	};

	private void changeCalendar(CalendarViewType calendarViewType) {
		EventTransactionFragment eventTransactionFragment = (EventTransactionFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.tag_calendar_transaction_fragment));
		String fragmentTag = null;

		if (calendarViewType == CalendarViewType.DAY) {
			mainBinding.sideNavCalendarTypes.dayRadio.setVisibility(View.VISIBLE);
			mainBinding.sideNavCalendarTypes.weekRadio.setVisibility(View.GONE);
			mainBinding.sideNavCalendarTypes.monthRadio.setVisibility(View.GONE);

			fragmentTag = DayFragment.TAG;
		} else if (calendarViewType == CalendarViewType.WEEK) {
			mainBinding.sideNavCalendarTypes.dayRadio.setVisibility(View.GONE);
			mainBinding.sideNavCalendarTypes.weekRadio.setVisibility(View.VISIBLE);
			mainBinding.sideNavCalendarTypes.monthRadio.setVisibility(View.GONE);

			fragmentTag = WeekFragment.TAG;
		} else if (calendarViewType == CalendarViewType.MONTH) {
			mainBinding.sideNavCalendarTypes.dayRadio.setVisibility(View.GONE);
			mainBinding.sideNavCalendarTypes.weekRadio.setVisibility(View.GONE);
			mainBinding.sideNavCalendarTypes.monthRadio.setVisibility(View.VISIBLE);

			fragmentTag = MonthFragment.TAG;
		}

		if (eventTransactionFragment != null) {
			eventTransactionFragment.replaceFragment(fragmentTag);
		}
	}

	private final View.OnClickListener sideNavOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
				case R.id.favorite_location:
					AllFavoritesHostFragment allFavoritesHostFragment = new AllFavoritesHostFragment();
					getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag(getString(R.string.tag_calendar_transaction_fragment)))
							.add(mainBinding.fragmentContainer.getId(), allFavoritesHostFragment,
									getString(R.string.tag_all_favorite_fragment)).addToBackStack(getString(R.string.tag_all_favorite_fragment))
							.commit();
					break;
				case R.id.settings:
					Intent intent = new Intent(AppMainActivity.this, SettingsActivity.class);
					settingsActivityResultLauncher.launch(intent);
					break;
			}
			mainBinding.drawerLayout.closeDrawer(mainBinding.sideNavigation);
		}
	};


	private final ActivityResultLauncher<Intent> settingsActivityResultLauncher =
			registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
				@Override
				public void onActivityResult(ActivityResult result) {

				}
			});

	@Override
	protected void onStart() {
		super.onStart();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		calendarViewModel = null;
		CalendarProvider.close();
	}

	private final ActivityResultLauncher<String[]> permissionsResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
			new ActivityResultCallback<Map<String, Boolean>>() {
				@Override
				public void onActivityResult(Map<String, Boolean> result) {
					if (result.get(Manifest.permission.READ_CALENDAR)) {
						// 권한 허용됨
						init();
						List<ContentValues> calendars = calendarViewModel.getAllCalendars();
						initSideCalendars(calendars);
					} else {
						// 권한 거부됨
						Toast.makeText(getApplicationContext(), getString(R.string.message_needs_calendar_permission), Toast.LENGTH_SHORT).show();
						moveTaskToBack(true); // 태스크를 백그라운드로 이동
						finishAndRemoveTask(); // 액티비티 종료 + 태스크 리스트에서 지우기
						android.os.Process.killProcess(android.os.Process.myPid()); // 앱 프로세스 종료
					}
				}
			});


	@Override
	public void onCheckedBox(String key, ContentValues calendar, boolean state) {
		EventTransactionFragment eventTransactionFragment =
				(EventTransactionFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.tag_calendar_transaction_fragment));

		SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preferences_selected_calendars_key), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();

		Set<String> selectedCalendarSet = new ArraySet<>();
		Map<String, String> selectedCalendarsMap = (Map<String, String>) sharedPreferences.getAll();

		selectedCalendarSet.addAll(selectedCalendarsMap.values());
		for (String v : selectedCalendarSet) {
			if (v.equals(key)) {
				if (!state) {
					// 같은 값을 가진 것이 이미 추가되어있는 경우 선택해제 하는 것이므로 삭제한다.
					connectedCalendarMap.remove(key);
					connectedCalendarList.clear();
					connectedCalendarList.addAll(connectedCalendarMap.values());

					editor.remove(key);
					editor.commit();

					eventTransactionFragment.refreshView();
				}
				return;
			}
		}
		connectedCalendarMap.put(key, calendar);
		connectedCalendarList.add(calendar);
		editor.putString(key, key);
		editor.commit();

		eventTransactionFragment.refreshView();
	}

	@Override
	public List<ContentValues> getConnectedCalendars() {
		return connectedCalendarList;
	}


	private final View.OnClickListener drawLayoutBtnOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			mainBinding.drawerLayout.openDrawer(mainBinding.sideNavigation);
		}
	};


}

