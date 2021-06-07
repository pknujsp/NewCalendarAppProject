package com.zerodsoft.scheduleweather.event.foods.favorite.restaurant;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.common.interfaces.OnProgressBarListener;
import com.zerodsoft.scheduleweather.databinding.FragmentFavoriteRestaurantBinding;
import com.zerodsoft.scheduleweather.event.foods.favorite.RestaurantFavoritesHostFragment;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnClickedFavoriteButtonListener;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnSetViewVisibility;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.RestaurantSharedViewModel;
import com.zerodsoft.scheduleweather.navermap.interfaces.FavoriteLocationsListener;
import com.zerodsoft.scheduleweather.navermap.place.PlaceInfoWebDialogFragment;
import com.zerodsoft.scheduleweather.navermap.place.PlaceInfoWebFragment;
import com.zerodsoft.scheduleweather.navermap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.kakaoplace.retrofit.KakaoPlaceDownloader;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceKakaoLocalResponse;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;
import com.zerodsoft.scheduleweather.weather.common.ViewProgress;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FavoriteRestaurantFragment extends Fragment implements OnClickedListItem<PlaceDocuments>, OnClickedFavoriteButtonListener {
	private FragmentFavoriteRestaurantBinding binding;

	private FavoriteLocationViewModel favoriteRestaurantViewModel;
	private RestaurantSharedViewModel restaurantSharedViewModel;
	private FavoriteRestaurantListAdapter adapter;

	private ArrayMap<String, List<PlaceDocuments>> restaurantListMap = new ArrayMap<>();
	private List<FavoriteLocationDTO> favoriteLocationDTOList = new ArrayList<>();
	private Map<String, FavoriteLocationDTO> favoriteRestaurantDTOMap = new HashMap<>();
	private OnSetViewVisibility onSetViewVisibility;

	private KakaoPlaceDownloader kakaoPlaceDownloader = new KakaoPlaceDownloader();

	private final FragmentManager.FragmentLifecycleCallbacks fragmentLifecycleCallbacks =
			new FragmentManager.FragmentLifecycleCallbacks() {
				@Override
				public void onFragmentCreated(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
					super.onFragmentCreated(fm, f, savedInstanceState);
					if (f instanceof PlaceInfoWebFragment) {
						onSetViewVisibility.setVisibility(OnSetViewVisibility.ViewType.HEADER, View.GONE);
					}
				}

				@Override
				public void onFragmentDestroyed(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f) {
					super.onFragmentDestroyed(fm, f);
					if (f instanceof PlaceInfoWebFragment) {
						onSetViewVisibility.setVisibility(OnSetViewVisibility.ViewType.HEADER, View.GONE);
					}
				}
			};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getParentFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true);

		favoriteRestaurantViewModel = new ViewModelProvider(requireActivity()).get(FavoriteLocationViewModel.class);
		restaurantSharedViewModel = new ViewModelProvider(requireActivity()).get(RestaurantSharedViewModel.class);
		onSetViewVisibility = restaurantSharedViewModel.getOnSetViewVisibility();
		onSetViewVisibility.setVisibility(OnSetViewVisibility.ViewType.HEADER, View.GONE);

		favoriteRestaurantViewModel.getAddedFavoriteLocationMutableLiveData().observe(this, new Observer<FavoriteLocationDTO>() {
			@Override
			public void onChanged(FavoriteLocationDTO favoriteLocationDTO) {
				Fragment primaryNavFragment = getParentFragment().getParentFragmentManager().getPrimaryNavigationFragment();

				if (!(primaryNavFragment instanceof RestaurantFavoritesHostFragment)) {
					try {
						requireActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								refreshList();
							}
						});
					} catch (Exception e) {
					}

				}
			}
		});

		favoriteRestaurantViewModel.getRemovedFavoriteLocationMutableLiveData().observe(this, new Observer<Integer>() {
			@Override
			public void onChanged(Integer integer) {
				Fragment primaryNavFragment = getParentFragment().getParentFragmentManager().getPrimaryNavigationFragment();

				if (!(primaryNavFragment instanceof RestaurantFavoritesHostFragment)) {
					try {
						requireActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								refreshList();
							}
						});
					} catch (Exception e) {
					}
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

		adapter = new FavoriteRestaurantListAdapter(getContext(), this, this, restaurantListMap);
		binding.favoriteRestaurantList.setAdapter(adapter);

		binding.customProgressView.setContentView(binding.favoriteRestaurantList);
		binding.customProgressView.onStartedProcessingData();

		downloadPlaceDocuments();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getParentFragmentManager().unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks);
	}

	private void downloadPlaceDocuments() {
		favoriteRestaurantViewModel.select(FavoriteLocationDTO.RESTAURANT, new DbQueryCallback<List<FavoriteLocationDTO>>() {
			int responseCount;

			@Override
			public void onResultSuccessful(List<FavoriteLocationDTO> favoriteLocationResultDtos) {
				favoriteRestaurantDTOMap.clear();
				favoriteLocationDTOList.clear();

				if (!restaurantListMap.isEmpty()) {
					restaurantListMap.clear();
					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							adapter.notifyDataSetChanged();
						}
					});
				} else {
					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							binding.customProgressView.onFailedProcessingData(getString(R.string.not_data));
						}
					});
				}

				favoriteLocationDTOList.addAll(favoriteLocationResultDtos);

				for (FavoriteLocationDTO favoriteLocationDTO : favoriteLocationDTOList) {
					favoriteRestaurantDTOMap.put(favoriteLocationDTO.getPlaceId(), favoriteLocationDTO);
				}

				final int requestCount = favoriteLocationResultDtos.size();
				responseCount = 0;

				for (FavoriteLocationDTO favoriteRestaurant : favoriteLocationResultDtos) {
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
						binding.customProgressView.onFailedProcessingData(getString(R.string.not_data));
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
		final String category = placeDocuments.getCategoryName().split(" > ")[1];
		if (!restaurantListMap.containsKey(category)) {
			restaurantListMap.put(category, new ArrayList<>());
		}
		restaurantListMap.get(category).add(placeDocuments);
	}


	private void createListView() {
		binding.customProgressView.onSuccessfulProcessingData();
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
					.add(R.id.fragment_container, placeInfoWebFragment, tag)
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
		favoriteRestaurantViewModel.select(FavoriteLocationDTO.RESTAURANT, new DbQueryCallback<List<FavoriteLocationDTO>>() {
			int responseCount;

			@Override
			public void onResultSuccessful(List<FavoriteLocationDTO> favoriteLocationResultDtos) {
				if (favoriteLocationResultDtos.isEmpty()) {
					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							adapter.getRestaurantListMap().clear();
							adapter.notifyDataSetChanged();
							binding.customProgressView.onFailedProcessingData(getString(R.string.not_data));
						}
					});
					return;
				}

				Set<String> restaurantIdSetInDb = new HashSet<>();
				for (FavoriteLocationDTO favoriteLocationDTO : favoriteLocationResultDtos) {
					restaurantIdSetInDb.add(favoriteLocationDTO.getPlaceId());
				}
				Set<String> restaurantIdSetInList = new HashSet<>();

				final Set<String> keySet = restaurantListMap.keySet();
				for (String key : keySet) {
					for (int i = 0; i < restaurantListMap.get(key).size(); i++) {
						restaurantIdSetInList.add(restaurantListMap.get(key).get(i).getId());
					}
				}

				Set<String> removedSet = new HashSet<>(restaurantIdSetInList);
				Set<String> addedSet = new HashSet<>(restaurantIdSetInDb);

				removedSet.removeAll(restaurantIdSetInDb);
				addedSet.removeAll(restaurantIdSetInList);

				if (removedSet.isEmpty() && addedSet.isEmpty()) {
					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (adapter.getRestaurantListMap().isEmpty()) {
								binding.customProgressView.onFailedProcessingData(getString(R.string.not_data));
							} else {
								binding.customProgressView.onSuccessfulProcessingData();
							}
						}
					});
					return;

				} else {
					if (!removedSet.isEmpty()) {
						for (String id : removedSet) {
							for (String key : keySet) {
								for (int i = restaurantListMap.get(key).size() - 1; i >= 0; i--) {
									if (restaurantListMap.get(key).get(i).getId().equals(id)) {
										restaurantListMap.get(key).remove(i);
									}
								}
							}
						}

						for (int i = restaurantListMap.size() - 1; i >= 0; i--) {
							if (restaurantListMap.get(restaurantListMap.keyAt(i)).isEmpty()) {
								restaurantListMap.removeAt(i);
							}
						}

						if (addedSet.isEmpty()) {
							requireActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									adapter.notifyDataSetChanged();
									if (adapter.getRestaurantListMap().isEmpty()) {
										binding.customProgressView.onFailedProcessingData(getString(R.string.not_data));
									} else {
										binding.customProgressView.onSuccessfulProcessingData();

									}
								}
							});
						}
					}

					if (!addedSet.isEmpty()) {
						Set<FavoriteLocationDTO> restaurantDTOSet = new HashSet<>();
						for (FavoriteLocationDTO favoriteLocationDTO : favoriteLocationResultDtos) {
							if (addedSet.contains(favoriteLocationDTO.getPlaceId())) {
								restaurantDTOSet.add(favoriteLocationDTO);
							}
						}

						final int requestCount = restaurantDTOSet.size();
						responseCount = 0;

						for (FavoriteLocationDTO favoriteRestaurant : restaurantDTOSet) {
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

			}
		});

	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onClickedFavoriteButton(KakaoLocalDocument kakaoLocalDocument, FavoriteLocationDTO favoriteLocationDTO,
	                                    int groupPosition, int childPosition) {
		final String placeId = ((PlaceDocuments) kakaoLocalDocument).getId();

		favoriteRestaurantViewModel.contains(placeId, null, null, null, new DbQueryCallback<FavoriteLocationDTO>() {
			@Override
			public void onResultSuccessful(FavoriteLocationDTO result) {
				favoriteRestaurantViewModel.delete(result.getId(),
						new CarrierMessagingService.ResultCallback<Boolean>() {
							@Override
							public void onReceiveResult(@NonNull Boolean isDeleted) throws RemoteException {
								requireActivity().runOnUiThread(new Runnable() {
									@Override
									public void run() {
										String key = restaurantListMap.keyAt(groupPosition);
										restaurantListMap.get(key).remove(childPosition);

										if (restaurantListMap.get(key).isEmpty()) {
											restaurantListMap.remove(key);
										}

										if (restaurantListMap.size() >= 1 && !restaurantListMap.containsKey(key)) {
											binding.favoriteRestaurantList.collapseGroup(groupPosition);
										}
										adapter.notifyDataSetChanged();
									}
								});

							}
						});
			}

			@Override
			public void onResultNoData() {

			}
		});

	}


	@Override
	public void onClickedFavoriteButton(KakaoLocalDocument kakaoLocalDocument, FavoriteLocationDTO favoriteLocationDTO, int position) {

	}
}