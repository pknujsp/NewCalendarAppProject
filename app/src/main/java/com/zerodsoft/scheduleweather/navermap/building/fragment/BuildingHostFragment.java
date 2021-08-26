package com.zerodsoft.scheduleweather.navermap.building.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.overlay.CircleOverlay;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.databinding.FragmentBuildingBinding;
import com.zerodsoft.scheduleweather.databinding.FragmentBuildingHostBinding;
import com.zerodsoft.scheduleweather.navermap.BottomSheetType;

import org.jetbrains.annotations.NotNull;


public class BuildingHostFragment extends BottomSheetDialogFragment {
	private final BuildingListFragment.IDrawCircleOnMap iDrawCircleOnMap;
	private FragmentBuildingHostBinding binding;
	private BottomSheetBehavior bottomSheetBehavior;
	private Integer bottomSheetHeight;
	private Bundle arguments;
	private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback;

	private final FragmentManager.FragmentLifecycleCallbacks fragmentLifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() {
		@Override
		public void onFragmentViewCreated(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f, @NonNull @NotNull View v, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
			super.onFragmentViewCreated(fm, f, v, savedInstanceState);
			if (f instanceof BuildingListFragment) {
				bottomSheetCallback.onStateChanged(getDialog().findViewById(R.id.design_bottom_sheet), BottomSheetBehavior.STATE_EXPANDED);
			}
		}
	};

	public BuildingHostFragment(BuildingListFragment.IDrawCircleOnMap iDrawCircleOnMap, BottomSheetBehavior.BottomSheetCallback bottomSheetCallback) {
		this.iDrawCircleOnMap = iDrawCircleOnMap;
		this.bottomSheetCallback = bottomSheetCallback;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getChildFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, false);

		arguments = getArguments();
		bottomSheetHeight = arguments.getInt("bottomSheetHeight");
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);

		dialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
		dialog.getWindow().setDimAmount(0.0f);

		bottomSheetBehavior = ((BottomSheetDialog) dialog).getBehavior();
		bottomSheetBehavior.setDraggable(false);
		bottomSheetBehavior.setPeekHeight(0);
		bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent keyEvent) {
				if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
					if (!getChildFragmentManager().popBackStackImmediate()) {
						dismiss();
						return false;
					}
				}
				return true;
			}
		});
		return dialog;
	}

	@Override
	public void dismiss() {
		bottomSheetCallback.onStateChanged(getDialog().findViewById(R.id.design_bottom_sheet), BottomSheetBehavior.STATE_COLLAPSED);
		super.dismiss();
	}

	@Override
	public void onCancel(@NonNull @NotNull DialogInterface dialog) {
		super.onCancel(dialog);
		bottomSheetCallback.onStateChanged(getDialog().findViewById(R.id.design_bottom_sheet), BottomSheetBehavior.STATE_COLLAPSED);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentBuildingHostBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		View bottomSheet = getDialog().findViewById(R.id.design_bottom_sheet);
		bottomSheet.getLayoutParams().height = bottomSheetHeight;

		BuildingListFragment buildingListFragment = new BuildingListFragment(iDrawCircleOnMap);
		buildingListFragment.setArguments(arguments);

		getChildFragmentManager().beginTransaction().add(binding.fragmentContainer.getId(), buildingListFragment,
				getString(R.string.tag_building_list_fragment))
				.commitNow();
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}
}