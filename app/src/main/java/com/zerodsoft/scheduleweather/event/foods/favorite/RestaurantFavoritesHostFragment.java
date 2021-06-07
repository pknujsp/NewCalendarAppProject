package com.zerodsoft.scheduleweather.event.foods.favorite;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentRestaurantFavoritesHostBinding;
import com.zerodsoft.scheduleweather.event.foods.favorite.restaurant.FavoriteRestaurantFragment;
import com.zerodsoft.scheduleweather.event.foods.interfaces.IOnSetView;

import org.jetbrains.annotations.NotNull;


public class RestaurantFavoritesHostFragment extends Fragment implements IOnSetView {
	private FragmentRestaurantFavoritesHostBinding binding;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentRestaurantFavoritesHostBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getChildFragmentManager().beginTransaction()
				.add(binding.contentFragmentContainer.getId(), new FavoriteRestaurantFragment()
						, getString(R.string.tag_favorite_restaurant_fragment))
				.commit();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);

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