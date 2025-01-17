package com.zerodsoft.calendarplatform.event.foods.searchlocation.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.calendarplatform.databinding.FragmentLocationItemDetailDialogBinding;
import com.zerodsoft.calendarplatform.etc.LocationType;
import com.zerodsoft.calendarplatform.event.foods.searchlocation.interfaces.OnSelectedNewLocation;
import com.zerodsoft.calendarplatform.navermap.places.selectedlocation.SelectedLocationMapFragmentNaver;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.addressresponse.AddressResponseDocuments;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.calendarplatform.room.dto.LocationDTO;


public class LocationItemDetailDialogFragment extends DialogFragment {
	private FragmentLocationItemDetailDialogBinding binding;
	private final KakaoLocalDocument kakaoLocalDocument;
	private final OnSelectedNewLocation onSelectedNewLocation;
	private LocationDTO selectedLocationDto;

	public LocationItemDetailDialogFragment(OnSelectedNewLocation onSelectedNewLocation, KakaoLocalDocument kakaoLocalDocument) {
		this.onSelectedNewLocation = onSelectedNewLocation;
		this.kakaoLocalDocument = kakaoLocalDocument;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentLocationItemDetailDialogBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		binding.locationInfo.deleteButton.setVisibility(View.GONE);
		selectedLocationDto = new LocationDTO();

		if (kakaoLocalDocument instanceof PlaceDocuments) {
			PlaceDocuments placeDocuments = (PlaceDocuments) kakaoLocalDocument;

			selectedLocationDto.setLatitude(placeDocuments.getY());
			selectedLocationDto.setLongitude(placeDocuments.getX());
			selectedLocationDto.setPlaceName(placeDocuments.getPlaceName());
			selectedLocationDto.setAddressName(placeDocuments.getAddressName());
			selectedLocationDto.setLocationType(LocationType.PLACE);

			binding.locationInfo.placeName.setText(placeDocuments.getPlaceName());
			binding.locationInfo.addressName.setText(placeDocuments.getAddressName());
		} else {
			AddressResponseDocuments addressResponseDocuments = (AddressResponseDocuments) kakaoLocalDocument;

			selectedLocationDto.setLatitude(addressResponseDocuments.getY());
			selectedLocationDto.setLongitude(addressResponseDocuments.getX());
			selectedLocationDto.setAddressName(addressResponseDocuments.getAddressName());
			selectedLocationDto.setLocationType(LocationType.ADDRESS);

			binding.locationInfo.placeName.setVisibility(View.GONE);
			binding.locationInfo.addressName.setText(addressResponseDocuments.getAddressName());
		}

		SelectedLocationMapFragmentNaver selectedLocationMapFragmentNaver = new SelectedLocationMapFragmentNaver(selectedLocationDto, null);
		getChildFragmentManager().beginTransaction().add(binding.locationMap.getId(),
				selectedLocationMapFragmentNaver, SelectedLocationMapFragmentNaver.TAG).commit();

		binding.cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});

		binding.okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dismiss();
				onSelectedNewLocation.onSelectedNewLocation(selectedLocationDto);
			}
		});
	}
}