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

	protected final void removeFavoriteRestaurant(FavoriteLocationDTO removedFavoriteRestaurant) {
		ArrayMap<String, List<PlaceDocuments>> restaurantListMap = adapter.getRestaurantListMap();

		int groupPosition = 0;
		int childPosition = 0;
		final String removedRestaurantId = removedFavoriteRestaurant.getPlaceId();
		String key = null;

		for (; groupPosition < restaurantListMap.size(); groupPosition++) {
			key = restaurantListMap.keyAt(groupPosition);
			for (childPosition = 0; childPosition < restaurantListMap.get(key).size(); childPosition++) {
				if (restaurantListMap.get(key).get(childPosition).getId().equals(removedRestaurantId)) {
					restaurantListMap.get(key).remove(childPosition);
					break;
				}
			}
		}

		if (restaurantListMap.get(key).isEmpty()) {
			restaurantListMap.remove(key);
		}

		if (restaurantListMap.size() >= 1 && !restaurantListMap.containsKey(key)) {
			binding.favoriteRestaurantList.collapseGroup(groupPosition);
		}

		adapter.getPlaceIdSet().remove(removedRestaurantId);
		adapter.notifyDataSetChanged();
	}

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
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						binding.customProgressViewForFavoriteRestaurant.onFailedProcessingData(getString(R.string.not_data));
						adapter.notifyDataSetChanged();
					}
				});
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


	protected final void refreshList() {
		//restaurantId비교
		//바뀐 부분만 수정
		binding.customProgressViewForFavoriteRestaurant.onStartedProcessingData();
		favoriteRestaurantViewModel.getFavoriteLocations(FavoriteLocationDTO.RESTAURANT, new DbQueryCallback<List<FavoriteLocationDTO>>() {
			int responseCount;

			@Override
			public void onResultSuccessful(List<FavoriteLocationDTO> savedFavoriteRestaurantList) {
				ArrayMap<String, List<PlaceDocuments>> restaurantListMap = adapter.getRestaurantListMap();
				Set<String> foodMenuKeySet = restaurantListMap.keySet();
				Set<String> restaurantIdSetInList = adapter.getPlaceIdSet();

				Set<String> restaurantIdSetInDb = new HashSet<>();
				for (FavoriteLocationDTO favoriteLocationDTO : savedFavoriteRestaurantList) {
					restaurantIdSetInDb.add(favoriteLocationDTO.getPlaceId());
				}

				Set<String> removedSet = new HashSet<>(restaurantIdSetInList);
				Set<String> addedSet = new HashSet<>(restaurantIdSetInDb);

				removedSet.removeAll(restaurantIdSetInDb);
				addedSet.removeAll(restaurantIdSetInList);

				if (removedSet.isEmpty() && addedSet.isEmpty()) {
					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							binding.customProgressViewForFavoriteRestaurant.onSuccessfulProcessingData();
						}
					});
				} else {
					if (!removedSet.isEmpty()) {
						for (String key : foodMenuKeySet) {
							for (int childPosition = restaurantListMap.get(key).size() - 1; childPosition >= 0; childPosition--) {
								if (removedSet.contains(restaurantListMap.get(key).get(childPosition).getId())) {
									restaurantIdSetInList.remove(restaurantListMap.get(key).get(childPosition).getId());
									restaurantListMap.get(key).remove(childPosition);
								}
							}
						}

						for (int i = restaurantListMap.size() - 1; i >= 0; i--) {
							if (restaurantListMap.get(restaurantListMap.keyAt(i)).isEmpty()) {
								restaurantListMap.removeAt(i);
							}
						}

						if (restaurantListMap.isEmpty()) {
							restaurantIdSetInList.clear();
						}
						requireActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								adapter.notifyDataSetChanged();
							}
						});
					}

					if (!addedSet.isEmpty()) {
						Set<FavoriteLocationDTO> addedRestaurantsSet = new HashSet<>();
						for (FavoriteLocationDTO favoriteLocationDTO : savedFavoriteRestaurantList) {
							if (addedSet.contains(favoriteLocationDTO.getPlaceId())) {
								addedRestaurantsSet.add(favoriteLocationDTO);
							}
						}

						final int requestCount = addedRestaurantsSet.size();
						responseCount = 0;

						for (FavoriteLocationDTO favoriteRestaurant : addedRestaurantsSet) {
							LocalApiPlaceParameter placeParameter = LocalParameterUtil.getPlaceParameterForSpecific(favoriteRestaurant.getPlaceName()
									, favoriteRestaurant.getLatitude(), favoriteRestaurant.getLongitude());

							kakaoPlaceDownloader.getPlacesForSpecific(placeParameter, new JsonDownloader<PlaceKakaoLocalResponse>() {
								@Override
								public void onResponseSuccessful(PlaceKakaoLocalResponse result) {
									//id값과 일치하는 장소 데이터 추출
									++responseCount;
									List<PlaceDocuments> placeDocumentsList = result.getPlaceDocuments();
									final String restaurantId = favoriteRestaurant.getPlaceId();

									int i = 0;
									for (; i < placeDocumentsList.size(); i++) {
										if (placeDocumentsList.get(i).getId().equals(restaurantId)) {
											break;
										}
									}

									createList(placeDocumentsList.get(i));

									if (requestCount == responseCount) {
										requireActivity().runOnUiThread(new Runnable() {
											@Override
											public void run() {
												restaurantIdSetInList.clear();
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

				}


			}

			@Override
			public void onResultNoData() {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						adapter.getPlaceIdSet().clear();
						adapter.getRestaurantListMap().clear();
						adapter.notifyDataSetChanged();
					}
				});
			}
		});

	}

}