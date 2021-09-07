package com.zerodsoft.calendarplatform.event.foods.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.databinding.FragmentRestaurantMainHostBinding;
import com.zerodsoft.calendarplatform.event.foods.interfaces.IOnSetView;
import com.zerodsoft.calendarplatform.navermap.places.PlaceInfoWebFragment;

public class RestaurantMainHostFragment extends Fragment implements IOnSetView {
	private FragmentRestaurantMainHostBinding binding;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentRestaurantMainHostBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getChildFragmentManager().beginTransaction().add(binding.contentFragmentContainer.getId(), new FoodsMenuListFragment(),
				getString(R.string.tag_food_menus_fragment)).commit();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		FragmentManager fragmentManager = getChildFragmentManager();
		Fragment showingContentFragment = fragmentManager.findFragmentById(binding.contentFragmentContainer.getId());
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		if (fragmentManager.findFragmentByTag(getString(R.string.tag_header_food_menu_list_fragment)) != null) {
			if (hidden) {
				fragmentTransaction.hide(fragmentManager.findFragmentByTag(getString(R.string.tag_header_food_menu_list_fragment)));
			} else {
				fragmentTransaction.show(fragmentManager.findFragmentByTag(getString(R.string.tag_header_food_menu_list_fragment)));
			}
		}
		if (showingContentFragment instanceof PlaceInfoWebFragment) {
			if (hidden) {
				((PlaceInfoWebFragment) showingContentFragment).onBackPressedCallback.remove();
			} else {
				requireActivity().getOnBackPressedDispatcher().addCallback(showingContentFragment
						, ((PlaceInfoWebFragment) showingContentFragment).onBackPressedCallback);
			}
		}

		if (!fragmentTransaction.isEmpty()) {
			fragmentTransaction.commit();
		} else {
			fragmentTransaction = null;
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