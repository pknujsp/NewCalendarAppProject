package com.zerodsoft.calendarplatform.activity.preferences.fragments;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.activity.preferences.ColorListAdapter;
import com.zerodsoft.calendarplatform.activity.preferences.custom.ColorPreference;
import com.zerodsoft.calendarplatform.activity.preferences.interfaces.PreferenceListener;
import com.zerodsoft.calendarplatform.calendar.CalendarViewModel;
import com.zerodsoft.calendarplatform.event.util.EventUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CalendarColorFragment extends PreferenceFragmentCompat implements PreferenceListener {
	private OnBackPressedCallback onBackPressedCallback;
	private CalendarViewModel calendarViewModel;
	private AlertDialog dialog;
	private Preference clickedPreference;
	private final ColorPreferenceInterface colorPreferenceInterface = new ColorPreferenceInterface() {
		@Override
		public void onCreatedPreferenceView(ContentValues calendar, ColorPreference colorPreference) {
			super.onCreatedPreferenceView(calendar, colorPreference);
		}
	};

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		setPreferencesFromResource(R.xml.app_settings_calendar_color_preference, rootKey);
	}


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private void init() {
		List<ContentValues> calendarList = calendarViewModel.getCalendars();
		// 계정 별로 나눈다.
		Map<String, List<ContentValues>> accountMap = new HashMap<>();

		for (ContentValues calendar : calendarList) {
			String accountName = calendar.getAsString(CalendarContract.Calendars.ACCOUNT_NAME);

			if (!accountMap.containsKey(accountName)) {
				accountMap.put(accountName, new ArrayList<>());
			}
			accountMap.get(accountName).add(calendar);
		}

		Set<String> accountSet = accountMap.keySet();
		PreferenceScreen preferenceScreen = getPreferenceScreen();

		for (String accountName : accountSet) {
			PreferenceCategory preferenceCategory = new PreferenceCategory(getContext());
			preferenceCategory.setTitle(accountName);
			preferenceScreen.addPreference(preferenceCategory);

			List<ContentValues> calendars = accountMap.get(accountName);

			for (ContentValues calendar : calendars) {
				ColorPreference preference = new ColorPreference(getContext(), colorPreferenceInterface, calendar);

				preference.setTitle(calendar.getAsString(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME));
				preference.setWidgetLayoutResource(R.layout.custom_preference_layout);
				preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						ContentValues currentColor = calendarViewModel.getCalendarColor(calendar.getAsInteger(CalendarContract.Calendars._ID));
						List<ContentValues> colors = calendarViewModel.getCalendarColors(calendar.getAsString(CalendarContract.Calendars.ACCOUNT_NAME),
								calendar.getAsString(CalendarContract.Calendars.ACCOUNT_TYPE));


						GridView gridView = new GridView(getContext());
						gridView.setAdapter(new ColorListAdapter(currentColor.getAsString(CalendarContract.Calendars.CALENDAR_COLOR_KEY), colors, getContext()));
						gridView.setNumColumns(5);
						gridView.setGravity(Gravity.CENTER);

						int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, getResources().getDisplayMetrics());
						gridView.setPadding(padding, padding, padding, padding);
						gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								int color = colors.get(position).getAsInteger(CalendarContract.Colors.COLOR);
								String colorKey = colors.get(position).getAsString(CalendarContract.Colors.COLOR_KEY);

								((ColorPreference) preference).setColor(EventUtil.getColor(color));
								calendarViewModel.updateCalendarColor(calendar.getAsInteger(CalendarContract.Calendars._ID), color, colorKey);
								dialog.dismiss();
							}
						});

						MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
						builder.setView(gridView);
						builder.setTitle(R.string.preference_dialog_title_calendar_color);
						dialog = builder.create();
						dialog.show();

						return true;
					}
				});
				preferenceCategory.addPreference(preference);
			}
		}
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
		init();
	}

	private final ActivityResultLauncher<String> initPermissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
			new ActivityResultCallback<Boolean>() {
				@SuppressLint("RestrictedApi")
				@Override
				public void onActivityResult(Boolean result) {
					if (result) {
						init();
					} else {
						Toast.makeText(getActivity(), getString(R.string.message_needs_calendar_permission), Toast.LENGTH_SHORT).show();
						onBackPressedCallback.handleOnBackPressed();
					}
				}
			});

	private final ActivityResultLauncher<String> permissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
			new ActivityResultCallback<Boolean>() {
				@SuppressLint("RestrictedApi")
				@Override
				public void onActivityResult(Boolean result) {
					if (result) {
						clickedPreference.performClick();
					}
				}
			});


	@Override
	public void onCreatedPreferenceView() {
		//캘린더 색상 설정
	}

	public abstract class ColorPreferenceInterface {
		public void onCreatedPreferenceView(ContentValues calendar, ColorPreference colorPreference) {

		}
	}
}