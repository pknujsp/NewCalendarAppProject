package com.zerodsoft.scheduleweather.navermap.place;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.PlaceInfoDialogFragmentBinding;

import org.jetbrains.annotations.NotNull;

public class PlaceInfoWebDialogFragment extends DialogFragment {
	private PlaceInfoDialogFragmentBinding binding;
	private PlaceInfoWebFragment placeInfoWebFragment;

	public PlaceInfoWebDialogFragment() {
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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

		FragmentManager fragmentManager = getChildFragmentManager();

		placeInfoWebFragment = new PlaceInfoWebFragment();
		placeInfoWebFragment.setArguments(getArguments());
		fragmentManager.beginTransaction().add(binding.fragmentContainer.getId(), placeInfoWebFragment, getString(R.string.tag_place_info_web_fragment)).commit();
	}

	@Override
	public void onResume() {
		super.onResume();

		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

		// Copy the alert dialog window attributes to new layout parameter instance
		layoutParams.copyFrom(getDialog().getWindow().getAttributes());

		// Set the width and height for the layout parameters
		// This will bet the width and height of alert dialog
		layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

		// Apply the newly created layout parameters to the alert dialog window
		getDialog().getWindow().setAttributes(layoutParams);
	}

}
