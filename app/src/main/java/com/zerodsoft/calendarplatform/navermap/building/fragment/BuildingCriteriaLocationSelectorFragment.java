package com.zerodsoft.calendarplatform.navermap.building.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zerodsoft.calendarplatform.databinding.BuildingCriteriaLocationSelectorFragmentBinding;
import com.zerodsoft.calendarplatform.navermap.search.LocationSearchFragment;

import org.jetbrains.annotations.NotNull;

public class BuildingCriteriaLocationSelectorFragment extends Fragment {
	private BuildingCriteriaLocationSelectorFragmentBinding binding;
	private LocationSearchFragment.SearchBuildingsBtnOnClickedListener searchBuildingsBtnOnClickedListener;

	public BuildingCriteriaLocationSelectorFragment(LocationSearchFragment.SearchBuildingsBtnOnClickedListener searchBuildingsBtnOnClickedListener) {
		this.searchBuildingsBtnOnClickedListener = searchBuildingsBtnOnClickedListener;
	}

	@Override
	public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@org.jetbrains.annotations.Nullable
	@Override
	public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		binding = BuildingCriteriaLocationSelectorFragmentBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		binding.searchBuildingsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getParentFragmentManager().popBackStackImmediate();
				searchBuildingsBtnOnClickedListener.onClickedSearchBuildings();
			}
		});

		binding.closeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getParentFragmentManager().popBackStackImmediate();
			}
		});
	}

}
