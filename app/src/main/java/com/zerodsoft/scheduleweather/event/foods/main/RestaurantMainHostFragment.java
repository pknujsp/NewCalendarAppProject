package com.zerodsoft.scheduleweather.event.foods.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentRestaurantMainHostBinding;
import com.zerodsoft.scheduleweather.event.foods.criterialocation.RestaurantCriteriaLocationSettingsFragment;
import com.zerodsoft.scheduleweather.event.foods.header.HeaderCriteriaLocationFragment;

public class RestaurantMainHostFragment extends Fragment {
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
				getString(R.string.tag_food_menus_fragment)).commitNow();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		FragmentManager fragmentManager = getChildFragmentManager();
		if (fragmentManager.findFragmentByTag(getString(R.string.tag_restaurant_list_tab_fragment)) != null) {
			fragmentManager.findFragmentByTag(getString(R.string.tag_restaurant_list_tab_fragment)).onHiddenChanged(hidden);
		}
	}
}