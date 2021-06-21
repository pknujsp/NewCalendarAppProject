package com.zerodsoft.scheduleweather.favorites.restaurant.basefragment;

import android.database.DataSetObserver;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.databinding.BaseFragmentFavoriteRestaurantBinding;
import com.zerodsoft.scheduleweather.event.foods.favorite.restaurant.FavoriteLocationViewModel;
import com.zerodsoft.scheduleweather.event.foods.favorite.restaurant.FavoriteRestaurantListAdapter;
import com.zerodsoft.scheduleweather.favorites.restaurant.AllFavoriteRestaurantHostFragment;
import com.zerodsoft.scheduleweather.kakaoplace.retrofit.KakaoPlaceDownloader;
import com.zerodsoft.scheduleweather.navermap.place.PlaceInfoWebFragment;
import com.zerodsoft.scheduleweather.navermap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceKakaoLocalResponse;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class FavoriteRestaurantBaseFragment extends Fragment implements OnClickedListItem<PlaceDocuments> {
	protected BaseFragmentFavoriteRestaurantBinding binding;

	protected FavoriteLocationViewModel favoriteRestaurantViewModel;
	protected FavoriteRestaurantListAdapter adapter;
	protected KakaoPlaceDownloader kakaoPlaceDownloader = new KakaoPlaceDownloader();

	protected abstract void onRemovedFavoriteRestaurant(FavoriteLocationDTO removedFavoriteRestaurant);

	protected abstract void onAddedFavoriteRestaurant(FavoriteLocationDTO addedFavoriteRestaurant);


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		favoriteRestaurantViewModel.getAddedFavoriteLocationMutableLiveData().observe(this, new Observer<FavoriteLocationDTO>() {
			@Override
			public void onChanged(FavoriteLocationDTO addedFavoriteRestaurant) {
				if (addedFavoriteRestaurant.getType() == FavoriteLocationDTO.RESTAURANT) {
					onAddedFavoriteRestaurant(addedFavoriteRestaurant);
				}
			}
		});

		favoriteRestaurantViewModel.getRemovedFavoriteLocationMutableLiveData().observe(this, new Observer<FavoriteLocationDTO>() {
			@Override
			public void onChanged(FavoriteLocationDTO removedFavoriteRestaurant) {
				if (removedFavoriteRestaurant.getType() == FavoriteLocationDTO.RESTAURANT) {
					onRemovedFavoriteRestaurant(removedFavoriteRestaurant);
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

		downloadPlaceDocuments();
	}

	protected final void downloadPlaceDocuments() {
		binding.customProgressViewForFavoriteRestaurant.onStartedProcessingData();
		favoriteRestaurantViewModel.getFavoriteLocations(FavoriteLocationDTO.RESTAURANT, new DbQueryCallback<List<FavoriteLocationDTO>>() {
			int responseCount;

			@Override
			public void onResultSuccessful(List<FavoriteLocationDTO> savedFavoriteRestaurantList) {
				if (savedFavoriteRestaurantList.isEmpty()) {
					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							binding.customProgressViewForFavoriteRestaurant.onFailedProcessingData(getString(R.string.not_data));
							adapter.notifyDataSetChanged();
						}
					});
					return;
				}

				final int requestCount = savedFavoriteRestaurantList.size();
				responseCount = 0;

				for (FavoriteLocationDTO favoriteRestaurant : savedFavoriteRestaurantList) {
					LocalApiPlaceParameter placeParameter = LocalParameterUtil.getPlaceParameterForSpecific(favoriteRestaurant.getPlaceName()
							, favoriteRestaurant.getLatitude(), favoriteRestaurant.getLongitude());

					kakaoPlaceDownloader.getPlacesForSpecific(placeParameter, new JsonDownloader<PlaceKakaoLocalResponse>() {
						@Override
						public void onResponseSuccessful(PlaceKakaoLocalResponse result) {
							//id값과 일치하는 장소 데이터 추출
							++responseCount;
							List<PlaceDocuments> placeDocumentsList = result.getPlaceDocuments();
							final String restaurantId = favoriteRestaurant.getPlaceId();

							int index = 0;
							for (; index < placeDocumentsList.size(); index++) {
								if (placeDocumentsList.get(index).getId().equals(restaurantId)) {
									break;
								}
							}

							createList(placeDocumentsList.get(index));

							if (requestCount == responseCount) {
								requireActivity().runOnUiThread(new Runnable() {
									@Override
									public void run() {
										createListView();
									}
								});
							}
						}

						@Override
						public void onResponseFailed(Exception e) {

						}
					});
				}
			}

			@Override
			public void onResultNoData() {

			}
		});
	}


	/*
	장소를 카테고리 별로 분류해서 리스트 생성
	key : categoryname의 값이 '음식점 > 한식 > 해물, 생선' 방식이므로
	앞 2개로 구분을 짓는다
	'음식점 > 한식'
	 */
	private final void createList(PlaceDocuments placeDocuments) {
		ArrayMap<String, List<PlaceDocuments>> restaurantListMap = adapter.getRestaurantListMap();

		final String category = placeDocuments.getCategoryName().split(" > ")[1];
		if (!restaurantListMap.containsKey(category)) {
			restaurantListMap.put(category, new ArrayList<>());
		}
		restaurantListMap.get(category).add(placeDocuments);
	}


	protected final void createListView() {
		adapter.setPlaceIdSet();
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClickedListItem(PlaceDocuments e, int position) {

	}

	@Override
	public void deleteListItem(PlaceDocuments e, int position) {
	}

	protected final void addFavoriteRestaurant(FavoriteLocationDTO addedFavoriteRestaurant) {
		LocalApiPlaceParameter placeParameter = LocalParameterUtil.getPlaceParameterForSpecific(addedFavoriteRestaurant.getPlaceName()
				, addedFavoriteRestaurant.getLatitude(), addedFavoriteRestaurant.getLongitude());

		kakaoPlaceDownloader.getPlacesForSpecific(placeParameter, new JsonDownloader<PlaceKakaoLocalResponse>() {
			@Override
			public void onResponseSuccessful(PlaceKakaoLocalResponse result) {
				//id값과 일치하는 장소 데이터 추출
				List<PlaceDocuments> downloadedPlaceDocumentsList = result.getPlaceDocuments();
				final String addedPlaceId = addedFavoriteRestaurant.getPlaceId();

				int index = 0;
				for (; index < downloadedPlaceDocumentsList.size(); index++) {
					if (downloadedPlaceDocumentsList.get(index).getId().equals(addedPlaceId)) {
						break;
					}
				}

				final String category = downloadedPlaceDocumentsList.get(index).getCategoryName().split(" > ")[1];

				ArrayMap<String, List<PlaceDocuments>> restaurantListMap = adapter.getRestaurantListMap();
				if (!restaurantListMap.containsKey(category)) {
					restaurantListMap.put(category, new ArrayList<>());
				}
				restaurantListMap.get(category).add(downloadedPlaceDocumentsList.get(index));

				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						adapter.setPlaceIdSet();
						adapter.notifyDataSetChanged();
					}
				});
			}

			@Override
			public void onResponseFailed(Exception e) {

			}
		});
	}


	protected final void removeFavoriteRestaurant(FavoriteLocationDTO removedFavoriteRestaurant) {
		ArrayMap<String, List<PlaceDocuments>> restaurantListArrMap = adapter.getRestaurantListMap();

		final String removedPlaceId = removedFavoriteRestaurant.getPlaceId();

		for (int groupPosition = 0; groupPosition < restaurantListArrMap.size(); groupPosition++) {
			for (int childPosition = 0; childPosition < restaurantListArrMap.valueAt(groupPosition).size(); childPosition++) {
				if (restaurantListArrMap.valueAt(groupPosition).get(childPosition).getId().equals(removedPlaceId)) {
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

		adapter.setPlaceIdSet();
		adapter.notifyDataSetChanged();
	}

}