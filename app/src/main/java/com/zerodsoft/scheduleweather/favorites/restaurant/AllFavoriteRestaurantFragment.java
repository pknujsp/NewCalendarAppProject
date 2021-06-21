package com.zerodsoft.scheduleweather.favorites.restaurant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.event.foods.favorite.RestaurantFavoritesHostFragment;
import com.zerodsoft.scheduleweather.event.foods.favorite.restaurant.FavoriteLocationViewModel;
import com.zerodsoft.scheduleweather.event.foods.interfaces.IOnSetView;
import com.zerodsoft.scheduleweather.favorites.DefaultMapFragment;
import com.zerodsoft.scheduleweather.favorites.addressplace.AllFavoriteLocationsFragment;
import com.zerodsoft.scheduleweather.favorites.restaurant.basefragment.FavoriteRestaurantBaseFragment;
import com.zerodsoft.scheduleweather.navermap.place.PlaceInfoWebFragment;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;

import org.jetbrains.annotations.NotNull;

public class AllFavoriteRestaurantFragment extends FavoriteRestaurantBaseFragment {
	@Override
	protected void onAddedFavoriteRestaurant(FavoriteLocationDTO addedFavoriteRestaurant) {
		addFavoriteRestaurant(addedFavoriteRestaurant);
	}

	@Override
	protected void onRemovedFavoriteRestaurant(FavoriteLocationDTO removedFavoriteRestaurant) {
		removeFavoriteRestaurant(removedFavoriteRestaurant);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		favoriteRestaurantViewModel =
				new ViewModelProvider(getParentFragment().getParentFragment()).get(FavoriteLocationViewModel.class);
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
	public void onClickedListItem(PlaceDocuments e, int position) {
		if (e != null) {
			PlaceInfoWebFragment placeInfoWebFragment = new PlaceInfoWebFragment();
			Bundle bundle = new Bundle();
			bundle.putString("placeId", ((PlaceDocuments) e).getId());
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
	public void deleteListItem(PlaceDocuments e, int position) {
	}

}
