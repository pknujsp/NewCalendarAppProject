package com.zerodsoft.calendarplatform.favorites.restaurant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.event.foods.favorite.restaurant.FavoriteLocationViewModel;
import com.zerodsoft.calendarplatform.favorites.restaurant.basefragment.FavoriteRestaurantBaseFragment;
import com.zerodsoft.calendarplatform.navermap.places.PlaceInfoWebFragment;
import com.zerodsoft.calendarplatform.room.dto.FavoriteLocationDTO;

import org.jetbrains.annotations.NotNull;

public class AllFavoriteRestaurantFragment extends FavoriteRestaurantBaseFragment {
	private PlaceInfoWebFragment placeInfoWebFragment;

	@Override
	protected void onAddedFavoriteRestaurant(FavoriteLocationDTO addedFavoriteRestaurant) {
	}

	@Override
	protected void onRemovedFavoriteRestaurant(FavoriteLocationDTO removedFavoriteRestaurant) {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		favoriteRestaurantViewModel =
				new ViewModelProvider(requireActivity()).get(FavoriteLocationViewModel.class);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		binding.moreFavoriteRestaurantList.setVisibility(View.GONE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}


	@Override
	public void onClickedListItem(FavoriteLocationDTO e, int position) {
		if (e != null) {
			placeInfoWebFragment = new PlaceInfoWebFragment();
			Bundle bundle = new Bundle();
			bundle.putString("placeId", e.getPlaceId());
			placeInfoWebFragment.setArguments(bundle);

			String tag = getString(R.string.tag_place_info_web_fragment);

			getParentFragmentManager().beginTransaction().hide(this)
					.add(R.id.fragment_container, placeInfoWebFragment, tag)
					.addToBackStack(tag)
					.commit();
		} else {

		}
	}

	@Override
	public void deleteListItem(FavoriteLocationDTO e, int position) {
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
	}
}
