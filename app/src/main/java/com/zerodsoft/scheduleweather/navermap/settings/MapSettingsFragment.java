package com.zerodsoft.scheduleweather.navermap.settings;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.naver.maps.map.NaverMap;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.databinding.FragmentMapSettingsBinding;

import org.jetbrains.annotations.NotNull;


public class MapSettingsFragment extends BottomSheetDialogFragment {
	private FragmentMapSettingsBinding binding;
	private BottomSheetBehavior bottomSheetBehavior;
	private OnChangedMapSettingsListener onChangedMapSettingsListener;

	public MapSettingsFragment(OnChangedMapSettingsListener onChangedMapSettingsListener) {
		this.onChangedMapSettingsListener = onChangedMapSettingsListener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);

		bottomSheetBehavior = ((BottomSheetDialog) dialog).getBehavior();
		bottomSheetBehavior.setDraggable(false);
		bottomSheetBehavior.setPeekHeight(0);
		bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

		return dialog;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentMapSettingsBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		/*
		View bottomSheet = getDialog().findViewById(R.id.design_bottom_sheet);
		bottomSheet.getLayoutParams().height = bottomSheetHeight;
		 */
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		NaverMap.MapType currentMapType = NaverMap.MapType.valueOf(preferences.getString(getString(R.string.preference_key_map_type),
				NaverMap.MapType.Basic.toString()));

		switch (currentMapType) {
			case Basic:
				binding.mapTypeToggleGroup.check(R.id.map_type_basic);
				break;
			case Hybrid:
				binding.mapTypeToggleGroup.check(R.id.map_type_satellite);
				break;
			case Terrain:
				binding.mapTypeToggleGroup.check(R.id.map_type_terrain);
				break;
		}

		binding.mapTypeToggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
			@Override
			public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
				if (isChecked) {
					setMapType();
				}
			}
		});
	}

	@SuppressLint("NonConstantResourceId")
	private void setMapType() {
		int checkedId = binding.mapTypeToggleGroup.getCheckedButtonId();
		NaverMap.MapType newMapType = null;

		switch (checkedId) {
			case R.id.map_type_basic:
				newMapType = NaverMap.MapType.Basic;
				break;
			case R.id.map_type_satellite:
				newMapType = NaverMap.MapType.Hybrid;
				break;
			case R.id.map_type_terrain:
				newMapType = NaverMap.MapType.Terrain;
				break;
		}
		App.setPreference_key_map_type(newMapType);

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(getString(R.string.preference_key_map_type), newMapType.toString());
		editor.apply();

		onChangedMapSettingsListener.onChangedMapType(newMapType);
	}

	public interface OnChangedMapSettingsListener {
		void onChangedMapType(NaverMap.MapType newMapType);
	}
}