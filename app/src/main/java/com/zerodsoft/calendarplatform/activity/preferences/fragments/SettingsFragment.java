package com.zerodsoft.calendarplatform.activity.preferences.fragments;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.activity.App;
import com.zerodsoft.calendarplatform.activity.editevent.fragments.TimeZoneFragment;
import com.zerodsoft.calendarplatform.activity.placecategory.PlaceCategorySettingsFragment;
import com.zerodsoft.calendarplatform.activity.preferences.custom.RadiusPreference;
import com.zerodsoft.calendarplatform.activity.preferences.custom.SearchBuildingRangeRadiusPreference;
import com.zerodsoft.calendarplatform.activity.preferences.custom.TimeZonePreference;
import com.zerodsoft.calendarplatform.activity.preferences.customfoodmenu.fragment.CustomFoodMenuSettingsFragment;
import com.zerodsoft.calendarplatform.activity.preferences.interfaces.PreferenceListener;
import com.zerodsoft.calendarplatform.common.interfaces.DbQueryCallback;
import com.zerodsoft.calendarplatform.common.interfaces.IFragmentTitle;
import com.zerodsoft.calendarplatform.event.foods.favorite.restaurant.FavoriteLocationViewModel;
import com.zerodsoft.calendarplatform.room.dto.FavoriteLocationDTO;
import com.zerodsoft.calendarplatform.weather.viewmodel.WeatherDbViewModel;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.TimeZone;

public class SettingsFragment extends PreferenceFragmentCompat implements PreferenceListener {
	private final IFragmentTitle iFragmentTitle;
	private SharedPreferences preferences;

	private SwitchPreference useDefaultTimeZoneSwitchPreference;
	private TimeZonePreference customTimeZonePreference;
	private SwitchPreference weekOfYearSwitchPreference;
	private SwitchPreference showCanceledInstanceSwitchPreference;
	private Preference calendarColorListPreference;
	private SwitchPreference hourSystemSwitchPreference;

	private RadiusPreference searchMapCategoryRangeRadiusPreference;
	private SearchBuildingRangeRadiusPreference searchBuildingRangeRadiusPreference;
	private Preference placesCategoryPreference;
	private Preference customFoodPreference;
	private Preference resetAppDbPreference;
	private Preference appInfoPreference;

	private FavoriteLocationViewModel favoriteLocationViewModel;
	private WeatherDbViewModel weatherDbViewModel;

	public SettingsFragment(IFragmentTitle iFragmentTitle) {
		this.iFragmentTitle = iFragmentTitle;
	}

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		setPreferencesFromResource(R.xml.app_settings_main_preference, rootKey);

		preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		preferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);

		useDefaultTimeZoneSwitchPreference = findPreference(getString(R.string.preference_key_using_timezone_of_device));
		weekOfYearSwitchPreference = findPreference(getString(R.string.preference_key_show_week_of_year));
		showCanceledInstanceSwitchPreference = findPreference(getString(R.string.preference_key_show_canceled_instances));
		hourSystemSwitchPreference = findPreference(getString(R.string.preference_key_using_24_hour_system));
		calendarColorListPreference = findPreference(getString(R.string.preference_key_calendar_color));
		placesCategoryPreference = findPreference(getString(R.string.preference_key_places_category));
		customFoodPreference = findPreference(getString(R.string.preference_key_custom_food_menu));
		resetAppDbPreference = findPreference(getString(R.string.preference_key_reset_app_db_data));
		appInfoPreference = findPreference(getString(R.string.preference_key_app_info));

		initPreference();
		initValue();

		useDefaultTimeZoneSwitchPreference.setOnPreferenceChangeListener(preferenceChangeListener);
		weekOfYearSwitchPreference.setOnPreferenceChangeListener(preferenceChangeListener);
		showCanceledInstanceSwitchPreference.setOnPreferenceChangeListener(preferenceChangeListener);
		hourSystemSwitchPreference.setOnPreferenceChangeListener(preferenceChangeListener);

		useDefaultTimeZoneSwitchPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				customTimeZonePreference.setEnabled(!useDefaultTimeZoneSwitchPreference.isChecked());
				return true;
			}
		});

		resetAppDbPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				CharSequence[] resetArr = getResources().getTextArray(R.array.reset_app_db_arr);
				//날씨, 주소/장소 즐겨찾기, 음식점 즐겨찾기

				new MaterialAlertDialogBuilder(requireActivity())
						.setTitle(R.string.preference_title_reset_app_db_data)
						.setItems(resetArr, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								switch (which) {
									case 0: {
										weatherDbViewModel.deleteAll(new DbQueryCallback<Boolean>() {
											@Override
											public void onResultSuccessful(Boolean result) {
												requireActivity().runOnUiThread(new Runnable() {
													@Override
													public void run() {
														Toast.makeText(getContext(), R.string.deleted_all_weather_data, Toast.LENGTH_SHORT).show();
													}
												});
											}

											@Override
											public void onResultNoData() {

											}
										});
										break;
									}

									case 1: {
										favoriteLocationViewModel.deleteAll(FavoriteLocationDTO.ADDRESS, new DbQueryCallback<Boolean>() {
											@Override
											public void onResultSuccessful(Boolean result) {
												favoriteLocationViewModel.deleteAll(FavoriteLocationDTO.PLACE, new DbQueryCallback<Boolean>() {
													@Override
													public void onResultSuccessful(Boolean result) {
														requireActivity().runOnUiThread(new Runnable() {
															@Override
															public void run() {
																Toast.makeText(getContext(), R.string.deleted_all_favorite_locations, Toast.LENGTH_SHORT).show();
															}
														});
													}

													@Override
													public void onResultNoData() {

													}
												});
											}

											@Override
											public void onResultNoData() {

											}
										});
										break;
									}

									case 2: {
										favoriteLocationViewModel.deleteAll(FavoriteLocationDTO.RESTAURANT, new DbQueryCallback<Boolean>() {
											@Override
											public void onResultSuccessful(Boolean result) {
												requireActivity().runOnUiThread(new Runnable() {
													@Override
													public void run() {
														Toast.makeText(getContext(), R.string.deleted_all_favorite_restaurants, Toast.LENGTH_SHORT).show();
													}
												});
											}

											@Override
											public void onResultNoData() {

											}
										});
										break;
									}
								}

							}
						}).create().show();
				return true;
			}
		});
	}


	private final SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			//값 변경완료시 호출됨
			// 범위

		}
	};

	private void initPreference() {
		//캘린더 색상
		calendarColorListPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				iFragmentTitle.setTitle(getString(R.string.preference_title_calendar_color));

				CalendarColorFragment calendarColorFragment = new CalendarColorFragment();
				getParentFragmentManager().beginTransaction().hide(SettingsFragment.this)
						.add(R.id.fragment_container, calendarColorFragment, getString(R.string.tag_calendar_color_fragment))
						.addToBackStack(getString(R.string.tag_calendar_color_fragment)).commit();

				return true;
			}
		});

		//장소 카테고리
		placesCategoryPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				iFragmentTitle.setTitle(getString(R.string.preference_title_places_category));

				PlaceCategorySettingsFragment placeCategorySettingsFragment = new PlaceCategorySettingsFragment();
				getParentFragmentManager().beginTransaction().hide(SettingsFragment.this)
						.add(R.id.fragment_container, placeCategorySettingsFragment, getString(R.string.tag_place_category_settings_fragment))
						.addToBackStack(getString(R.string.tag_place_category_settings_fragment)).commit();
				return true;
			}
		});

		//음식 메뉴
		customFoodPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				iFragmentTitle.setTitle(getString(R.string.preference_title_custom_food_menu));

				CustomFoodMenuSettingsFragment customFoodMenuSettingsFragment = new CustomFoodMenuSettingsFragment();
				getParentFragmentManager().beginTransaction().hide(SettingsFragment.this)
						.add(R.id.fragment_container, customFoodMenuSettingsFragment, getString(R.string.tag_custom_food_menu_settings_fragment))
						.addToBackStack(getString(R.string.tag_custom_food_menu_settings_fragment)).commit();

				return true;
			}
		});


		//커스텀 시간대
		customTimeZonePreference = new TimeZonePreference(getContext());
		customTimeZonePreference.setKey(getString(R.string.preference_key_custom_timezone));
		customTimeZonePreference.setSummary(R.string.preference_summary_custom_timezone);
		customTimeZonePreference.setTitle(R.string.preference_title_custom_timezone);
		customTimeZonePreference.setWidgetLayoutResource(R.layout.custom_preference_layout);
		customTimeZonePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				TimeZoneFragment timeZoneFragment = new TimeZoneFragment(new TimeZoneFragment.OnTimeZoneResultListener() {
					@Override
					public void onResult(TimeZone timeZone) {
						//수동 시간대 설정이 완료된 경우
						TimeZone currentTimeZone = customTimeZonePreference.getTimeZone();

						if (currentTimeZone.getID().equals(timeZone.getID())) {
							Toast.makeText(getActivity(), "이미 선택된 시간대 입니다", Toast.LENGTH_SHORT).show();
						} else {
							customTimeZonePreference.setTimeZone(timeZone);
							SharedPreferences.Editor editor = preferences.edit();
							editor.putString(getString(R.string.preference_key_custom_timezone), timeZone.getID()).apply();
							App.setPreference_key_custom_timezone(timeZone);
						}
						getParentFragmentManager().popBackStackImmediate();
					}
				});

				getParentFragmentManager().beginTransaction().hide(SettingsFragment.this)
						.add(R.id.fragment_container, timeZoneFragment, getString(R.string.tag_timezone_fragment))
						.addToBackStack(getString(R.string.tag_timezone_fragment)).commit();

				return true;
			}
		});

		((PreferenceCategory) getPreferenceManager().findPreference(getString(R.string.preference_calendar_category_title)))
				.addPreference(customTimeZonePreference);

		//카테고리 검색범위 반지름
		searchMapCategoryRangeRadiusPreference = new RadiusPreference(getContext());
		searchMapCategoryRangeRadiusPreference.setKey(getString(R.string.preference_key_radius_range));
		searchMapCategoryRangeRadiusPreference.setSummary(R.string.preference_summary_radius_range);
		searchMapCategoryRangeRadiusPreference.setTitle(R.string.preference_title_radius_range);
		searchMapCategoryRangeRadiusPreference.setWidgetLayoutResource(R.layout.custom_preference_layout);
		searchMapCategoryRangeRadiusPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				ViewGroup sliderView = (ViewGroup) getLayoutInflater().inflate(R.layout.slider_view, null);

				TextView valueTextView = (TextView) sliderView.findViewById(R.id.value_textview);
				Slider slider = (Slider) sliderView.findViewById(R.id.slider);

				DecimalFormat decimalFormat = new DecimalFormat("#.#");
				float value = Math.round((Float.parseFloat(App.getPreference_key_radius_range()) / 1000f) * 10) / 10f;
				slider.setValue(Float.parseFloat(decimalFormat.format(value)));
				slider.setValueFrom(0.1f);
				slider.setValueTo(20.0f);
				slider.setStepSize(0.1f);

				valueTextView.setText(decimalFormat.format(value) + " km");

				slider.addOnChangeListener(new Slider.OnChangeListener() {

					@Override
					public void onValueChange(@NonNull @NotNull Slider slider, float value, boolean fromUser) {
						valueTextView.setText(decimalFormat.format(value) + " km");
					}
				});

				new MaterialAlertDialogBuilder(requireActivity())
						.setTitle(searchMapCategoryRangeRadiusPreference.getTitle())
						.setView(sliderView)
						.setPositiveButton(R.string.check, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								String value = String.valueOf((int) (slider.getValue() * 1000));
								searchMapCategoryRangeRadiusPreference.callChangeListener(value);
								dialogInterface.dismiss();
							}
						})
						.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								dialogInterface.dismiss();
							}
						}).create().show();
				return true;
			}
		});

		searchMapCategoryRangeRadiusPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preferences.edit().putString(searchMapCategoryRangeRadiusPreference.getKey(), (String) newValue).commit();
				App.setPreference_key_radius_range((String) newValue);
				searchMapCategoryRangeRadiusPreference.setValue();
				return true;
			}
		});

		((PreferenceCategory) getPreferenceManager().findPreference(getString(R.string.preference_place_category_title)))
				.addPreference(searchMapCategoryRangeRadiusPreference);

		//빌딩 검색범위 반지름
		searchBuildingRangeRadiusPreference = new SearchBuildingRangeRadiusPreference(getContext());
		searchBuildingRangeRadiusPreference.setKey(getString(R.string.preference_key_range_meter_for_search_buildings));
		searchBuildingRangeRadiusPreference.setSummary(R.string.preference_summary_range_meter_for_search_buildings);
		searchBuildingRangeRadiusPreference.setTitle(R.string.preference_title_range_meter_for_search_buildings);
		searchBuildingRangeRadiusPreference.setWidgetLayoutResource(R.layout.custom_preference_layout);
		searchBuildingRangeRadiusPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preferences.edit().putString(searchBuildingRangeRadiusPreference.getKey(), (String) newValue).commit();
				App.setPreference_key_range_meter_for_search_buildings((String) newValue);
				searchBuildingRangeRadiusPreference.setValue();
				return true;
			}
		});
		searchBuildingRangeRadiusPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				ViewGroup sliderView = (ViewGroup) getLayoutInflater().inflate(R.layout.slider_view, null);

				TextView valueTextView = (TextView) sliderView.findViewById(R.id.value_textview);
				Slider slider = (Slider) sliderView.findViewById(R.id.slider);

				float value = Float.parseFloat(App.getPreference_key_range_meter_for_search_buildings());

				TypedValue outValue = new TypedValue();
				getResources().getValue(R.integer.search_buildings_min_range, outValue, true);
				slider.setValueFrom(outValue.getFloat());

				getResources().getValue(R.integer.search_buildings_max_range, outValue, true);
				slider.setValueTo(outValue.getFloat());

				getResources().getValue(R.integer.search_buildings_slider_step_size, outValue, true);
				slider.setStepSize(outValue.getFloat());

				slider.setValue(value);

				valueTextView.setText((int) value + " m");

				slider.addOnChangeListener(new Slider.OnChangeListener() {

					@Override
					public void onValueChange(@NonNull @NotNull Slider slider, float value, boolean fromUser) {
						valueTextView.setText((int) value + " m");
					}
				});

				new MaterialAlertDialogBuilder(requireActivity())
						.setTitle(searchBuildingRangeRadiusPreference.getTitle())
						.setView(sliderView)
						.setPositiveButton(R.string.check, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								String newValue = String.valueOf((int) slider.getValue());
								searchBuildingRangeRadiusPreference.callChangeListener(newValue);
								dialogInterface.dismiss();
							}
						})
						.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								dialogInterface.dismiss();
							}
						}).create().show();
				return true;
			}
		});

		((PreferenceCategory) getPreferenceManager().findPreference(getString(R.string.preference_place_category_title)))
				.addPreference(searchBuildingRangeRadiusPreference);

		//앱 정보
		appInfoPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				iFragmentTitle.setTitle(getString(R.string.preference_title_app_info));

				AppInfoFragment appInfoFragment = new AppInfoFragment();
				getParentFragmentManager().beginTransaction().hide(SettingsFragment.this)
						.add(R.id.fragment_container, appInfoFragment, getString(R.string.tag_app_info_fragment))
						.addToBackStack(getString(R.string.tag_app_info_fragment)).commit();

				return true;
			}
		});
	}

	private final Preference.OnPreferenceChangeListener preferenceChangeListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			//주차
			if (preference.getKey().equals(weekOfYearSwitchPreference.getKey())) {
				boolean value = (Boolean) newValue;
				if (value != weekOfYearSwitchPreference.isChecked()) {
					App.setPreference_key_show_week_of_year(value);
					return true;
				} else {
					return false;
				}
			}

			// 기기 기본 시간 사용
			else if (preference.getKey().equals(useDefaultTimeZoneSwitchPreference.getKey())) {
				boolean value = (Boolean) newValue;

				if (value != useDefaultTimeZoneSwitchPreference.isChecked()) {
					App.setPreference_key_using_timezone_of_device(value);
					return true;
				} else {
					return false;
				}
			}

			// 거절한 일정 표시
			else if (preference.getKey().equals(showCanceledInstanceSwitchPreference.getKey())) {
				boolean value = (Boolean) newValue;

				if (value != showCanceledInstanceSwitchPreference.isChecked()) {
					App.setPreference_key_show_canceled_instances(value);
					return true;
				} else {
					return false;
				}
			}

			// 시간제
			else if (preference.getKey().equals(hourSystemSwitchPreference.getKey())) {
				boolean value = (Boolean) newValue;

				if (value != hourSystemSwitchPreference.isChecked()) {
					App.setPreference_key_settings_hour_system(value);
					return true;
				} else {
					return false;
				}
			}
			return false;
		}
	};

	private void initValue() {
		// 사용하지 않더라도 커스텀 시간대는 설정해놓는다.
		String customTimeZoneId = preferences.getString(getString(R.string.preference_key_custom_timezone), "");
		TimeZone timeZone = TimeZone.getTimeZone(customTimeZoneId);
		customTimeZonePreference.setTimeZone(timeZone);

		//기기 기본 시간대 사용 여부
		boolean usingDeviceTimeZone = preferences.getBoolean(getString(R.string.preference_key_using_timezone_of_device), false);
		if (usingDeviceTimeZone) {
			useDefaultTimeZoneSwitchPreference.setChecked(true);
			customTimeZonePreference.setEnabled(false);
		} else {
			useDefaultTimeZoneSwitchPreference.setChecked(false);
			customTimeZonePreference.setEnabled(true);
		}

		//주차 표시
		boolean showingWeekOfYear = preferences.getBoolean(getString(R.string.preference_key_show_week_of_year), false);
		weekOfYearSwitchPreference.setChecked(showingWeekOfYear);

		//거절한 일정 표시
		boolean showingCanceledInstance = preferences.getBoolean(getString(R.string.preference_key_show_canceled_instances), false);
		showCanceledInstanceSwitchPreference.setChecked(showingCanceledInstance);

		//24시간제 사용
		boolean using24HourSystem = preferences.getBoolean(getString(R.string.preference_key_using_24_hour_system), false);
		hourSystemSwitchPreference.setChecked(using24HourSystem);

		//지도 카테고리 검색 반지름 범위
		String searchMapCategoryRadius = preferences.getString(getString(R.string.preference_key_radius_range), "");
		float convertedRadius = Float.parseFloat(searchMapCategoryRadius) / 1000f;
		searchMapCategoryRangeRadiusPreference.setDefaultValue(String.valueOf(convertedRadius));

		//지도 빌딩 검색 반지름 범위
		String searchBuildingRangeRadius = preferences.getString(getString(R.string.preference_key_range_meter_for_search_buildings), "");
		searchBuildingRangeRadiusPreference.setDefaultValue(searchBuildingRangeRadius);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		favoriteLocationViewModel = new ViewModelProvider(requireActivity()).get(FavoriteLocationViewModel.class);
		weatherDbViewModel = new ViewModelProvider(this).get(WeatherDbViewModel.class);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onCreatedPreferenceView() {
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			iFragmentTitle.setTitle(getString(R.string.settings));
		}
	}

}
