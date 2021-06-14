package com.zerodsoft.scheduleweather.event.foods.favorite.restaurant;

import android.database.DataSetObserver;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import com.zerodsoft.scheduleweather.databinding.FragmentFavoriteRestaurantBinding;
import com.zerodsoft.scheduleweather.event.foods.favorite.RestaurantFavoritesHostFragment;
import com.zerodsoft.scheduleweather.event.foods.interfaces.IOnSetView;
import com.zerodsoft.scheduleweather.navermap.place.PlaceInfoWebFragment;
import com.zerodsoft.scheduleweather.navermap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.kakaoplace.retrofit.KakaoPlaceDownloader;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceKakaoLocalResponse;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoriteRestaurantFragment extends Fragment implements OnClickedListItem<PlaceDocuments> {
	private FragmentFavoriteRestaurantBinding binding;

	private FavoriteLocationViewModel favoriteRestaurantViewModel;
	private FavoriteRestaurantListAdapter adapter;
	private IOnSetView iOnSetView;

	private KakaoPlaceDownloader kakaoPlaceDownloader = new KakaoPlaceDownloader();

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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getParentFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true);

		favoriteRestaurantViewModel =
				new ViewModelProvider(getParentFragment().getParentFragment().getParentFragment()).get(FavoriteLocationViewModel.class);
		iOnSetView = (IOnSetView) getParentFragment();
		iOnSetView.setFragmentContainerVisibility(IOnSetView.ViewType.HEADER, View.GONE);

		favoriteRestaurantViewModel.getAddedFavoriteLocationMutableLiveData().observe(this, new Observer<FavoriteLocationDTO>() {
			@Override
			public void onChanged(FavoriteLocationDTO favoriteLocationDTO) {
				Fragment primaryNavFragment = getParentFragment().getParentFragmentManager().getPrimaryNavigationFragment();

				if (!(primaryNavFragment instanceof RestaurantFavoritesHostFragment)) {
					try {
						if (getActivity() != null) {
							refreshList();
						}
					} catch (Exception e) {
					}

				} else {

				}
			}
		});

		favoriteRestaurantViewModel.getRemovedFavoriteLocationMutableLiveData().observe(this, new Observer<FavoriteLocationDTO>() {
			@Override
			public void onChanged(FavoriteLocationDTO removedFavoriteLocationDTO) {
				Fragment primaryNavFragment = getParentFragment().getParentFragmentManager().getPrimaryNavigationFragment();

				if (!(primaryNavFragment instanceof RestaurantFavoritesHostFragment)) {
					try {
						if (getActivity() != null) {
							refreshList();
						}
					} catch (Exception e) {

					}

				} else {
					if (adapter.getRestaurantListMap().isEmpty()) {
						return;
					}
					ArrayMap<String, List<PlaceDocuments>> restaurantListMap = adapter.getRestaurantListMap();

					int groupPosition = 0;
					int childPosition = 0;
					final String removedRestaurantId = removedFavoriteLocationDTO.getPlaceId();
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
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentFavoriteRestaurantBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

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
					binding.customProgressView.onFailedProcessingData(getString(R.string.not_data));
				} else {
					binding.customProgressView.onSuccessfulProcessingData();
				}
			}

			@Override
			public void onInvalidated() {
				super.onInvalidated();
			}
		});
		binding.favoriteRestaurantList.setAdapter(adapter);

		binding.customProgressView.setContentView(binding.favoriteRestaurantList);
		downloadPlaceDocuments();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getParentFragmentManager().unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks);
	}

	private void downloadPlaceDocuments() {
		binding.customProgressView.onStartedProcessingData();
		favoriteRestaurantViewModel.getFavoriteLocations(FavoriteLocationDTO.RESTAURANT, new DbQueryCallback<List<FavoriteLocationDTO>>() {
			int responseCount;

			@Override
			public void onResultSuccessful(List<FavoriteLocationDTO> savedFavoriteRestaurantList) {
				if (savedFavoriteRestaurantList.isEmpty()) {
					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
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
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
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
	private void createList(PlaceDocuments placeDocuments) {
		ArrayMap<String, List<PlaceDocuments>> restaurantListMap = adapter.getRestaurantListMap();

		final String category = placeDocuments.getCategoryName().split(" > ")[1];
		if (!restaurantListMap.containsKey(category)) {
			restaurantListMap.put(category, new ArrayList<>());
		}
		restaurantListMap.get(category).add(placeDocuments);
	}


	private void createListView() {
		adapter.setPlaceIdSet();
		adapter.notifyDataSetChanged();
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
					.add(R.id.content_fragment_container, placeInfoWebFragment, tag)
					.addToBackStack(tag)
					.commit();
		} else {

		}
	}

	@Override
	public void deleteListItem(PlaceDocuments e, int position) {
	}


	public void refreshList() {
		//restaurantId비교
		//바뀐 부분만 수정
		binding.customProgressView.onStartedProcessingData();
		favoriteRestaurantViewModel.getFavoriteLocations(FavoriteLocationDTO.RESTAURANT, new DbQueryCallback<List<FavoriteLocationDTO>>() {
			int responseCount;

			@Override
			public void onResultSuccessful(List<FavoriteLocationDTO> savedFavoriteRestaurantList) {
				if (savedFavoriteRestaurantList.isEmpty()) {
					adapter.getPlaceIdSet().clear();
					adapter.getRestaurantListMap().clear();
					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							adapter.notifyDataSetChanged();
						}
					});
				} else {

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
								binding.customProgressView.onSuccessfulProcessingData();
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

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
	}

}