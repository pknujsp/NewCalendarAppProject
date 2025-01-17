package com.zerodsoft.calendarplatform.event.foods.favorite.restaurant;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.common.interfaces.OnClickedListItem;
import com.zerodsoft.calendarplatform.event.foods.interfaces.IOnSetView;
import com.zerodsoft.calendarplatform.favorites.restaurant.basefragment.FavoriteRestaurantBaseFragment;
import com.zerodsoft.calendarplatform.navermap.places.PlaceInfoWebFragment;
import com.zerodsoft.calendarplatform.room.dto.FavoriteLocationDTO;

import org.jetbrains.annotations.NotNull;

public class FavoriteRestaurantFragment extends FavoriteRestaurantBaseFragment implements OnClickedListItem<FavoriteLocationDTO> {
	private IOnSetView iOnSetView;

	private final FragmentManager.FragmentLifecycleCallbacks fragmentLifecycleCallbacks =
			new FragmentManager.FragmentLifecycleCallbacks() {
				@Override
				public void onFragmentCreated(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
					super.onFragmentCreated(fm, f, savedInstanceState);
					if (f instanceof PlaceInfoWebFragment) {
						iOnSetView.setFragmentContainerVisibility(IOnSetView.ViewType.HEADER, View.GONE);
					}
				}

				@Override
				public void onFragmentDestroyed(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f) {
					super.onFragmentDestroyed(fm, f);
					if (f instanceof PlaceInfoWebFragment) {
						iOnSetView.setFragmentContainerVisibility(IOnSetView.ViewType.HEADER, View.GONE);
					}
				}
			};

	@Override
	protected void onAddedFavoriteRestaurant(FavoriteLocationDTO addedFavoriteRestaurant) {
	}

	@Override
	protected void onRemovedFavoriteRestaurant(FavoriteLocationDTO removedFavoriteRestaurant) {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		getParentFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, false);

		iOnSetView = (IOnSetView) getParentFragment();
		iOnSetView.setFragmentContainerVisibility(IOnSetView.ViewType.HEADER, View.GONE);
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
		getParentFragmentManager().unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks);
	}


	@Override
	public void onClickedListItem(FavoriteLocationDTO e, int position) {
		if (e != null) {
			PlaceInfoWebFragment placeInfoWebFragment = new PlaceInfoWebFragment();
			Bundle bundle = new Bundle();
			bundle.putString("placeId", e.getPlaceId());
			placeInfoWebFragment.setArguments(bundle);

			String tag = getString(R.string.tag_place_info_web_fragment);

			getParentFragmentManager().beginTransaction().hide(this)
					.add(R.id.content_fragment_container, placeInfoWebFragment, tag)
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