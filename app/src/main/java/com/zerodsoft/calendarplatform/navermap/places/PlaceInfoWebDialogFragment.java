package com.zerodsoft.calendarplatform.navermap.places;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.databinding.PlaceInfoDialogFragmentBinding;

import org.jetbrains.annotations.NotNull;

public class PlaceInfoWebDialogFragment extends BottomSheetDialogFragment {
	private PlaceInfoDialogFragmentBinding binding;
	private PlaceInfoWebFragment placeInfoWebFragment;

	private BottomSheetBehavior bottomSheetBehavior;
	private int bottomSheetHeight;

	public PlaceInfoWebDialogFragment() {
	}

	private FragmentManager.FragmentLifecycleCallbacks fragmentLifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() {
		@Override
		public void onFragmentDestroyed(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f) {
			super.onFragmentDestroyed(fm, f);
			//dismiss();
		}
	};

	@Override
	public void onDismiss(@NonNull @NotNull DialogInterface dialog) {
		super.onDismiss(dialog);
	}

	@Override
	public void onCancel(@NonNull @NotNull DialogInterface dialog) {
		super.onCancel(dialog);
	}

	@Override
	public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getChildFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, false);
		Bundle arguments = getArguments();
		bottomSheetHeight = arguments.getInt("bottomSheetHeight");
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);

		bottomSheetBehavior = ((BottomSheetDialog) dialog).getBehavior();
		bottomSheetBehavior.setDraggable(false);
		bottomSheetBehavior.setPeekHeight(0);
		bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent keyEvent) {
				if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
					if (placeInfoWebFragment.webCanGoBack()) {
						placeInfoWebFragment.webGoBack();
					} else {
						dismiss();
						return false;
					}
				}
				return true;
			}
		});
		return dialog;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = PlaceInfoDialogFragmentBinding.inflate(inflater);
		return binding.getRoot();
	}


	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		View bottomSheet = getDialog().findViewById(R.id.design_bottom_sheet);
		bottomSheet.getLayoutParams().height = bottomSheetHeight;

		placeInfoWebFragment = new PlaceInfoWebFragment();
		placeInfoWebFragment.setArguments(getArguments());
		getChildFragmentManager().beginTransaction().add(binding.fragmentContainer.getId(), placeInfoWebFragment,
				getString(R.string.tag_place_info_web_fragment)).addToBackStack(null).commit();
	}

	@Override
	public void onResume() {
		super.onResume();
		/*
		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

		// Copy the alert dialog window attributes to new layout parameter instance
		layoutParams.copyFrom(getDialog().getWindow().getAttributes());

		// Set the width and height for the layout parameters
		// This will bet the width and height of alert dialog
		layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

		// Apply the newly created layout parameters to the alert dialog window
		getDialog().getWindow().setAttributes(layoutParams);

		 */
	}

}
