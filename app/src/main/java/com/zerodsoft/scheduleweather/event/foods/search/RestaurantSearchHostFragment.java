package com.zerodsoft.scheduleweather.event.foods.search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentRestaurantSearchHostBinding;
import com.zerodsoft.scheduleweather.event.foods.criterialocation.RestaurantCriteriaLocationSettingsFragment;
import com.zerodsoft.scheduleweather.event.foods.interfaces.IOnSetView;
import com.zerodsoft.scheduleweather.event.foods.search.search.fragment.SearchRestaurantFragment;
import com.zerodsoft.scheduleweather.navermap.place.PlaceInfoWebFragment;
import com.zerodsoft.scheduleweather.navermap.viewmodel.SearchHistoryViewModel;

import org.jetbrains.annotations.NotNull;

public class RestaurantSearchHostFragment extends Fragment implements IOnSetView {
	private FragmentRestaurantSearchHostBinding binding;

	private FragmentManager.FragmentLifecycleCallbacks fragmentLifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() {
		@Override
		public void onFragmentCreated(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
			super.onFragmentCreated(fm, f, savedInstanceState);

		}


		@Override
		public void onFragmentDestroyed(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f) {
			super.onFragmentDestroyed(fm, f);

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getChildFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true);
		new ViewModelProvider(this).get(SearchHistoryViewModel.class);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentRestaurantSearchHostBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		setFragmentContainerVisibility(IOnSetView.ViewType.HEADER, View.GONE);
		getChildFragmentManager().beginTransaction()
				.add(binding.contentFragmentContainer.getId(), new SearchRestaurantFragment(), getString(R.string.tag_search_restaurant_fragment))
				.commit();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		FragmentManager fragmentManager = getChildFragmentManager();
		Fragment showingContentFragment = fragmentManager.findFragmentById(binding.contentFragmentContainer.getId());

		if (showingContentFragment instanceof PlaceInfoWebFragment) {
			if (hidden) {
				((PlaceInfoWebFragment) showingContentFragment).onBackPressedCallback.remove();
			} else {
				requireActivity().getOnBackPressedDispatcher().addCallback(showingContentFragment
						, ((PlaceInfoWebFragment) showingContentFragment).onBackPressedCallback);
			}
		}
	}

	@Override
	public void setFragmentContainerVisibility(ViewType viewType, int visibility) {
		switch (viewType) {
			case HEADER:
				binding.headerFragmentContainer.setVisibility(visibility);
				break;
			case CONTENT:
				binding.contentFragmentContainer.setVisibility(visibility);
				((IOnSetView) getParentFragment()).setFragmentContainerVisibility(viewType, visibility);
				break;
		}
	}

	@Override
	public void setFragmentContainerHeight(int height) {
		binding.headerFragmentContainer.getLayoutParams().height = height;
		binding.headerFragmentContainer.requestLayout();
		binding.headerFragmentContainer.invalidate();
	}

}