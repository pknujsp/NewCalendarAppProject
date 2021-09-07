package com.zerodsoft.calendarplatform.favorites.addressplace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.event.foods.favorite.restaurant.FavoriteLocationViewModel;
import com.zerodsoft.calendarplatform.favorites.addressplace.basefragment.FavoriteLocationsBaseFragment;
import com.zerodsoft.calendarplatform.room.dto.FavoriteLocationDTO;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AllFavoriteLocationsFragment extends FavoriteLocationsBaseFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		favoriteLocationViewModel = new ViewModelProvider(requireActivity()).get(FavoriteLocationViewModel.class);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		binding.showAllFavorites.setVisibility(View.GONE);
		spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
				R.array.all_favorite_locations_sort_spinner, android.R.layout.simple_spinner_item);

		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		binding.sortSpinnerForAddressPlace.setAdapter(spinnerAdapter);
		binding.sortSpinnerForAddressPlace.setSelection(0);
		binding.sortSpinnerForAddressPlace.setOnItemSelectedListener(spinnerItemSelectedListener);

		favoriteLocationAdapter.setDistanceVisibility(View.GONE);
		setFavoriteLocationList();
	}

	@Override
	public void onAddedFavoriteLocation(FavoriteLocationDTO addedFavoriteLocation) {
		List<FavoriteLocationDTO> list = favoriteLocationAdapter.getList();
		list.add(addedFavoriteLocation);
		sort(list);
		favoriteLocationAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onLoadedFavoriteLocationsList(List<FavoriteLocationDTO> list) {
		sort(list);
		favoriteLocationAdapter.setList(list);

		requireActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				favoriteLocationAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	protected void sort(List<FavoriteLocationDTO> list) {
		switch (binding.sortSpinnerForAddressPlace.getSelectedItemPosition()) {
			case 0:
				//등록순
				sortByAddedDateTime(list);
				break;
			case 1:
				//카테고리 별
				sortByAddedCategory(list);
				break;
		}
	}
}