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
	public static final String TAG = "PlaceInfoFragment";
	private PlaceInfoDialogFragmentBinding binding;
	private PlaceInfoWebFragment placeInfoWebFragment;
	private String placeId;

	public PlaceInfoWebDialogFragment() {
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		placeId = bundle.getString("placeId");
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
		fragmentManager.beginTransaction().add(binding.fragmentContainer.getId(), placeInfoWebFragment, "PlaceInfoWebFragment").commit();

		getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
				if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
					if (placeInfoWebFragment.webCanGoBack()) {
						placeInfoWebFragment.webGoBack();
					} else {
						dismiss();
					}
					return true;
				}
				return true;
			}
		});
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
