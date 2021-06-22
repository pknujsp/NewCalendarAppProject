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
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.activity.preferences.fragments.SettingsFragment;
import com.zerodsoft.scheduleweather.activity.preferences.fragments.SettingsMainFragment;
import com.zerodsoft.scheduleweather.calendar.selectedcalendar.SelectedCalendarViewModel;
import com.zerodsoft.scheduleweather.calendarview.SideBarCalendarListAdapter;
import com.zerodsoft.scheduleweather.calendarview.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarview.day.DayFragment;
import com.zerodsoft.scheduleweather.calendarview.interfaces.ICalendarCheckBox;
import com.zerodsoft.scheduleweather.calendarview.month.MonthFragment;
import com.zerodsoft.scheduleweather.calendarview.week.WeekFragment;
import com.zerodsoft.scheduleweather.common.enums.CalendarViewType;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.databinding.ActivityAppMainBinding;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.common.classes.AppPermission;
import com.zerodsoft.scheduleweather.favorites.AllFavoritesHostFragment;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;
import com.zerodsoft.scheduleweather.room.dto.SelectedCalendarDTO;
import com.zerodsoft.scheduleweather.weather.dataprocessing.WeatherDataConverter;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.security.MessageDigest;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AppMainActivity extends AppCompatActivity implements ICalendarCheckBox {
	private static int DISPLAY_WIDTH = 0;
	private static int DISPLAY_HEIGHT = 0;

	private ActivityAppMainBinding mainBinding;
	private CalendarViewModel calendarViewModel;
	private SelectedCalendarViewModel selectedCalendarViewModel;
	private SideBarCalendarListAdapter sideBarCalendarListAdapter;

	private EventTransactionFragment eventTransactionFragment;

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
		selectedCalendarViewModel = new ViewModelProvider(this).get(SelectedCalendarViewModel.class);

		Point point = new Point();
		getWindowManager().getDefaultDisplay().getRealSize(point);

		DISPLAY_WIDTH = point.x;
		DISPLAY_HEIGHT = point.y;

		sideBarCalendarListAdapter = new SideBarCalendarListAdapter(AppMainActivity.this, AppMainActivity.this);
		mainBinding.sideNavCalendarList.setAdapter(sideBarCalendarListAdapter);

		selectedCalendarViewModel.getOnListSelectedCalendarLiveData().observe(this, new Observer<List<SelectedCalendarDTO>>() {
			@Override
			public void onChanged(List<SelectedCalendarDTO> selectedCalendarDTOS) {
				initSideCalendars(selectedCalendarDTOS);
			}
		});

		if (AppPermission.grantedPermissions(getApplicationContext(), Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR)) {
			//권한 확인
			sideBarCalendarListAdapter.setCalendarList(calendarViewModel.getAllCalendars());
			selectedCalendarViewModel.getSelectedCalendarList();
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
		mainBinding.addAccountBtn.setOnClickListener(sideNavOnClickListener);
		mainBinding.sideNavCalendarTypes.calendarTypeDay.setOnClickListener(calendarTypeOnClickListener);
		mainBinding.sideNavCalendarTypes.calendarTypeWeek.setOnClickListener(calendarTypeOnClickListener);
		mainBinding.sideNavCalendarTypes.calendarTypeMonth.setOnClickListener(calendarTypeOnClickListener);

		SharedPreferences preferences = getSharedPreferences(getString(R.string.preferences_selected_calendar_type_key), Context.MODE_PRIVATE);
		int type = preferences.getInt(getString(R.string.preferences_selected_calendar_type_key), CalendarViewType.MONTH.value());
		setSelectedCalendarType(CalendarViewType.enumOf(type));

		eventTransactionFragment = new EventTransactionFragment(CalendarViewType.enumOf(type), drawLayoutBtnOnClickListener);

		getSupportFragmentManager().beginTransaction()
				.add(mainBinding.fragmentContainer.getId(), eventTransactionFragment,
						getString(R.string.tag_calendar_transaction_fragment)).commitNow();
	}


	private void initSideCalendars(List<SelectedCalendarDTO> selectedCalendarList) {
		List<ContentValues> allCalendarList = sideBarCalendarListAdapter.getCalendarList();
		//선택된 캘린더 중에 현재 없어진 캘린더는 없는지 확인하고 있으면 선택해제
		Set<Integer> calendarIdSet = new HashSet<>();
		for (ContentValues calendar : allCalendarList) {
			calendarIdSet.add(calendar.getAsInteger(CalendarContract.Calendars._ID));
		}

		Set<Integer> removeCalendarIdSet = new HashSet<>();
		for (SelectedCalendarDTO selectedCalendarDTO : selectedCalendarList) {
			if (!calendarIdSet.contains(selectedCalendarDTO.getCalendarId())) {
				removeCalendarIdSet.add(selectedCalendarDTO.getCalendarId());
			}
		}

		if (!removeCalendarIdSet.isEmpty()) {
			selectedCalendarViewModel.delete(new DbQueryCallback<Boolean>() {
				@Override
				public void onResultSuccessful(Boolean deleted) {
					selectedCalendarViewModel.getSelectedCalendarList();
				}

				@Override
				public void onResultNoData() {

				}
			}, removeCalendarIdSet.toArray(new Integer[removeCalendarIdSet.size()]));
		} else {
			sideBarCalendarListAdapter.setSelectedCalendarList(selectedCalendarList);
			sideBarCalendarListAdapter.notifyDataSetChanged();

			mainBinding.sideNavCalendarList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
				@Override
				public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
					return false;
				}
			});
		}
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
		String fragmentTag = null;

		setSelectedCalendarType(calendarViewType);
		if (calendarViewType == CalendarViewType.DAY) {
			fragmentTag = DayFragment.TAG;
		} else if (calendarViewType == CalendarViewType.WEEK) {
			fragmentTag = WeekFragment.TAG;
		} else if (calendarViewType == CalendarViewType.MONTH) {
			fragmentTag = MonthFragment.TAG;
		}

		if (eventTransactionFragment != null) {
			eventTransactionFragment.replaceFragment(fragmentTag);
		}
	}

	private void setSelectedCalendarType(CalendarViewType calendarViewType) {
		if (calendarViewType == CalendarViewType.DAY) {
			mainBinding.sideNavCalendarTypes.dayRadio.setVisibility(View.VISIBLE);
			mainBinding.sideNavCalendarTypes.weekRadio.setVisibility(View.GONE);
			mainBinding.sideNavCalendarTypes.monthRadio.setVisibility(View.GONE);
		} else if (calendarViewType == CalendarViewType.WEEK) {
			mainBinding.sideNavCalendarTypes.dayRadio.setVisibility(View.GONE);
			mainBinding.sideNavCalendarTypes.weekRadio.setVisibility(View.VISIBLE);
			mainBinding.sideNavCalendarTypes.monthRadio.setVisibility(View.GONE);
		} else if (calendarViewType == CalendarViewType.MONTH) {
			mainBinding.sideNavCalendarTypes.dayRadio.setVisibility(View.GONE);
			mainBinding.sideNavCalendarTypes.weekRadio.setVisibility(View.GONE);
			mainBinding.sideNavCalendarTypes.monthRadio.setVisibility(View.VISIBLE);
		}
	}

	private final View.OnClickListener sideNavOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
				case R.id.add_account_btn:
					Intent accountIntent = new Intent(Settings.ACTION_ADD_ACCOUNT);
					accountIntent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, new String[]{"com.google"});
					addAccountActivityResultLauncher.launch(accountIntent);
					break;
				case R.id.favorite_location:
					AllFavoritesHostFragment allFavoritesHostFragment = new AllFavoritesHostFragment();
					getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag(getString(R.string.tag_calendar_transaction_fragment)))
							.add(mainBinding.fragmentContainer.getId(), allFavoritesHostFragment,
									getString(R.string.tag_all_favorite_fragment)).addToBackStack(getString(R.string.tag_all_favorite_fragment))
							.commit();
					break;
				case R.id.settings:
					SettingsMainFragment settingsMainFragment = new SettingsMainFragment();
					getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag(getString(R.string.tag_calendar_transaction_fragment)))
							.add(mainBinding.fragmentContainer.getId(), settingsMainFragment,
									getString(R.string.tag_settings_fragment)).addToBackStack(getString(R.string.tag_settings_fragment))
							.commit();
					break;
			}
			mainBinding.drawerLayout.closeDrawer(mainBinding.sideNavigation);
		}
	};


	private final ActivityResultLauncher<Intent> addAccountActivityResultLauncher =
			registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
				@Override
				public void onActivityResult(ActivityResult result) {
					//아무것도 안하고 취소하면 resultCode == CANCELED
					//계정 등록해도 CANCELED//
					//추가된 계정이 있는지 확인
					List<ContentValues> newCalendarList = calendarViewModel.getAllCalendars();
					if (newCalendarList.size() != sideBarCalendarListAdapter.getCalendarList().size()) {
						sideBarCalendarListAdapter.setCalendarList(newCalendarList);
						sideBarCalendarListAdapter.notifyDataSetChanged();
					}
				}
			});


	private final ActivityResultLauncher<String[]> permissionsResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
			new ActivityResultCallback<Map<String, Boolean>>() {
				@Override
				public void onActivityResult(Map<String, Boolean> result) {
					if (result.get(Manifest.permission.READ_CALENDAR)) {
						// 권한 허용됨
						sideBarCalendarListAdapter.setCalendarList(calendarViewModel.getAllCalendars());
						selectedCalendarViewModel.getSelectedCalendarList();
						init();
					} else {
						// 권한 거부됨
						Toast.makeText(getApplicationContext(), getString(R.string.message_needs_calendar_permission), Toast.LENGTH_SHORT).show();
						finish();
					}
				}
			});


	@Override
	public void onCheckedBox(ContentValues calendar, boolean isChecked) {
		if (isChecked) {
			SelectedCalendarDTO selectedCalendarDTO = new SelectedCalendarDTO();
			selectedCalendarDTO.setAccountName(calendar.getAsString(CalendarContract.Calendars.ACCOUNT_NAME));
			selectedCalendarDTO.setCalendarId(calendar.getAsInteger(CalendarContract.Calendars._ID));
			selectedCalendarDTO.setOwnerAccount(calendar.getAsString(CalendarContract.Calendars.OWNER_ACCOUNT));
			selectedCalendarViewModel.add(selectedCalendarDTO);
		} else {
			selectedCalendarViewModel.delete(calendar.getAsInteger(CalendarContract.Calendars._ID));
		}
	}

	private final View.OnClickListener drawLayoutBtnOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			mainBinding.drawerLayout.openDrawer(mainBinding.sideNavigation);
		}
	};

}

