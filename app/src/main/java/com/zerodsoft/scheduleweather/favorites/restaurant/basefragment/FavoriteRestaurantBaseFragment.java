package com.zerodsoft.scheduleweather.favorites.restaurant.basefragment;

import android.database.DataSetObserver;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.databinding.BaseFragmentFavoriteRestaurantBinding;
import com.zerodsoft.scheduleweather.event.foods.favorite.RestaurantFavoritesHostFragment;
import com.zerodsoft.scheduleweather.event.foods.favorite.restaurant.FavoriteLocationViewModel;
import com.zerodsoft.scheduleweather.event.foods.favorite.restaurant.FavoriteRestaurantListAdapter;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class FavoriteRestaurantBaseFragment extends Fragment implements OnClickedListItem<FavoriteLocationDTO> {
	protected BaseFragmentFavoriteRestaurantBinding binding;
	protected FavoriteLocationViewModel favoriteRestaurantViewModel;
	protected FavoriteRestaurantListAdapter adapter;
	protected boolean initializing = true;

	protected abstract void onRemovedFavoriteRestaurant(FavoriteLocationDTO removedFavoriteRestaurant);

	protected abstract void onAddedFavoriteRestaurant(FavoriteLocationDTO addedFavoriteRestaurant);


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		favoriteRestaurantViewModel.getAddedFavoriteLocationMutableLiveData().observe(this, new Observer<FavoriteLocationDTO>() {
			@Override
			public void onChanged(FavoriteLocationDTO addedFavoriteRestaurant) {
				if (!(getParentFragment().getParentFragmentManager().getPrimaryNavigationFragment() instanceof RestaurantFavoritesHostFragment)) {
					if (addedFavoriteRestaurant.getType() == FavoriteLocationDTO.RESTAURANT) {
						onAddedFavoriteRestaurant(addedFavoriteRestaurant);
						adapter.setPlaceIdSet();
						adapter.notifyDataSetChanged();
					}
				}
			}
		});

		favoriteRestaurantViewModel.getRemovedFavoriteLocationMutableLiveData().observe(this, new Observer<FavoriteLocationDTO>() {
			@Override
			public void onChanged(FavoriteLocationDTO removedFavoriteRestaurant) {
				if (!(getParentFragment().getParentFragmentManager().getPrimaryNavigationFragment() instanceof RestaurantFavoritesHostFragment)) {
					if (removedFavoriteRestaurant.getType() == FavoriteLocationDTO.RESTAURANT) {
						onRemovedFavoriteRestaurant(removedFavoriteRestaurant);

						adapter.setPlaceIdSet();
						adapter.notifyDataSetChanged();
					}
				}
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = BaseFragmentFavoriteRestaurantBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		binding.customProgressViewForFavoriteRestaurant.setContentView(binding.favoriteRestaurantList);

		binding.favoriteRestaurantList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
				return false;
			}
		});

		adapter = new FavoriteRestaurantListAdapter(getContext(), this, favoriteRestaurantViewModel);
		adapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
				if (adapter.getRestaurantListMap().isEmpty()) {
					binding.customProgressViewForFavoriteRestaurant.onFailedProcessingData(getString(R.string.not_data));
				} else {
					binding.customProgressViewForFavoriteRestaurant.onSuccessfulProcessingData();
				}
			}
		});
		binding.favoriteRestaurantList.setAdapter(adapter);
		binding.customProgressViewForFavoriteRestaurant.onStartedProcessingData();
		favoriteRestaurantViewModel.getFavoriteLocations(FavoriteLocationDTO.RESTAURANT, new DbQueryCallback<List<FavoriteLocationDTO>>() {
			@Override
			public void onResultSuccessful(List<FavoriteLocationDTO> savedFavoriteRestaurantList) {
				for (FavoriteLocationDTO favoriteRestaurant : savedFavoriteRestaurantList) {
					addFavoriteRestaurant(favoriteRestaurant);
				}
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						createListView();
					}
				});
			}

			@Override
			public void onResultNoData() {

			}
		});

	}

	@Override
	public void onStart() {
		super.onStart();
		initializing = false;
	}

	protected final void createListView() {
		adapter.setPlaceIdSet();
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClickedListItem(FavoriteLocationDTO e, int position) {

	}

	@Override
	public void deleteListItem(FavoriteLocationDTO e, int position) {
	}

	/*
장소를 카테고리 별로 분류해서 리스트 생성
key : categoryname의 값이 '음식점 > 한식 > 해물, 생선' 방식이므로
앞 2개로 구분을 짓는다
'음식점 > 한식'
 */
	protected final void addFavoriteRestaurant(FavoriteLocationDTO addedFavoriteRestaurant) {
		final String category = addedFavoriteRestaurant.getPlaceCategoryName().split(" > ")[1];
		ArrayMap<String, List<FavoriteLocationDTO>> restaurantListMap = adapter.getRestaurantListMap();

		if (!restaurantListMap.containsKey(category)) {
			restaurantListMap.put(category, new ArrayList<>());
		}
		restaurantListMap.get(category).add(addedFavoriteRestaurant);
	}


	protected final void removeFavoriteRestaurant(FavoriteLocationDTO removedFavoriteRestaurant) {
		ArrayMap<String, List<FavoriteLocationDTO>> restaurantListArrMap = adapter.getRestaurantListMap();

		final String removedPlaceId = removedFavoriteRestaurant.getPlaceId();

		for (int groupPosition = 0; groupPosition < restaurantListArrMap.size(); groupPosition++) {
			for (int childPosition = 0; childPosition < restaurantListArrMap.valueAt(groupPosition).size(); childPosition++) {
				if (restaurantListArrMap.valueAt(groupPosition).get(childPosition).getPlaceId().equals(removedPlaceId)) {
					restaurantListArrMap.valueAt(groupPosition).remove(childPosition);

					if (restaurantListArrMap.valueAt(groupPosition).isEmpty()) {
						restaurantListArrMap.removeAt(groupPosition);
						if (restaurantListArrMap.size() >= 1) {
							binding.favoriteRestaurantList.collapseGroup(groupPosition);
						}
					}
					break;
				}
			}
		}
	}

}