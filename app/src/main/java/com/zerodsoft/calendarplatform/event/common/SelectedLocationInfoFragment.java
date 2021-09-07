package com.zerodsoft.calendarplatform.event.common;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.calendarplatform.databinding.FragmentSelectedLocationInfoBinding;
import com.zerodsoft.calendarplatform.etc.LocationType;
import com.zerodsoft.calendarplatform.room.dto.LocationDTO;

import org.jetbrains.annotations.NotNull;

public class SelectedLocationInfoFragment extends Fragment {
	private FragmentSelectedLocationInfoBinding binding;

	private LocationDTO selectedLocationDto;

	private View.OnClickListener removeBackOnClickListener;
	private View.OnClickListener removeSearchOnClickListener;


	public SelectedLocationInfoFragment(View.OnClickListener removeBackOnClickListener, View.OnClickListener removeSearchOnClickListener) {
		this.removeBackOnClickListener = removeBackOnClickListener;
		this.removeSearchOnClickListener = removeSearchOnClickListener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle arguments = getArguments();
		selectedLocationDto = arguments.getParcelable("selectedLocationDto");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentSelectedLocationInfoBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		binding.removeSelectedLocationAndBack.setOnClickListener(removeBackOnClickListener);
		binding.removeSelectedLocationAndSearch.setOnClickListener(removeSearchOnClickListener);

		if (selectedLocationDto.getLocationType() == LocationType.PLACE) {
			binding.placeName.setText(selectedLocationDto.getPlaceName());
		} else {
			binding.placeNameLabel.setVisibility(View.GONE);
			binding.placeName.setVisibility(View.GONE);
		}

		binding.addressName.setText(selectedLocationDto.getAddressName());
	}
}