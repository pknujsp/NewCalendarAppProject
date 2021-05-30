package com.zerodsoft.scheduleweather.event.foods.favorite.restaurant;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.common.interfaces.OnProgressBarListener;
import com.zerodsoft.scheduleweather.databinding.FragmentFavoriteRestaurantBinding;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnClickedFavoriteButtonListener;
import com.zerodsoft.scheduleweather.navermap.interfaces.FavoriteLocationsListener;
import com.zerodsoft.scheduleweather.navermap.place.PlaceInfoFragment;
import com.zerodsoft.scheduleweather.navermap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.kakaoplace.retrofit.KakaoPlaceDownloader;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceKakaoLocalResponse;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FavoriteRestaurantFragment extends Fragment implements OnClickedListItem<PlaceDocuments>, OnProgressBarListener, OnClickedFavoriteButtonListener {
	public static final String TAG = "FavoriteRestaurantFragment";
	private FragmentFavoriteRestaurantBinding binding;
	private FavoriteLocationViewModel favoriteRestaurantViewModel;
	private FavoriteRestaurantListAdapter adapter;
	private final FavoriteLocationsListener favoriteLocationsListener;

	private ArrayMap<String, List<PlaceDocuments>> restaurantListMap = new ArrayMap<>();
	private List<FavoriteLocationDTO> favoriteLocationDTOList = new ArrayList<>();
	private Map<String, FavoriteLocationDTO> favoriteRestaurantDTOMap = new HashMap<>();

	private final KakaoPlaceDownloader kakaoPlaceDownloader = new KakaoPlaceDownloader(this::setProgressBarVisibility) {

		@Override
		public void onResponseSuccessful(KakaoLocalResponse result) {

		}

		@Override
		public void onResponseFailed(Exception e) {

		}

	};

	public FavoriteRestaurantFragment(FavoriteLocationsListener favoriteLocationsListener) {
		this.favoriteLocationsListener = favoriteLocationsListener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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

		favoriteRestaurantViewModel = new ViewModelProvider(this).get(FavoriteLocationViewModel.class);

		binding.favoriteRestaurantList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
				return false;
			}
		});

		adapter = new FavoriteRestaurantListAdapter(getContext(), this, this, restaurantListMap);
		binding.favoriteRestaurantList.setAdapter(adapter);

		downloadPlaceDocuments();
	}

	private void downloadPlaceDocuments() {
		favoriteRestaurantViewModel.select(FavoriteLocationDTO.RESTAURANT, new CarrierMessagingService.ResultCallback<List<FavoriteLocationDTO>>() {
			int responseCount;

			@Override
			public void onReceiveResult(@NonNull List<FavoriteLocationDTO> list) throws RemoteException {
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
				}

				favoriteLocationDTOList.addAll(list);

				for (FavoriteLocationDTO favoriteLocationDTO : favoriteLocationDTOList) {
					favoriteRestaurantDTOMap.put(favoriteLocationDTO.getPlaceId(), favoriteLocationDTO);
				}

				final int requestCount = list.size();
				responseCount = 0;

				for (FavoriteLocationDTO favoriteRestaurant : list) {
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
		setProgressBarVisibility(View.GONE);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClickedListItem(PlaceDocuments e, int position) {
		if (e instanceof PlaceDocuments) {
			PlaceInfoFragment placeInfoFragment = new PlaceInfoFragment();
			Bundle bundle = new Bundle();
			bundle.putString("placeId", ((PlaceDocuments) e).getId());
			placeInfoFragment.setArguments(bundle);

			placeInfoFragment.show(getChildFragmentManager(), PlaceInfoFragment.TAG);
		} else {

		}
	}

	@Override
	public void deleteListItem(PlaceDocuments e, int position) {

	}

	@Override
	public void setProgressBarVisibility(int visibility) {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				binding.progressBar.setVisibility(visibility);
			}
		});
	}

	public void refreshList() {
		//restaurantId비교
		//바뀐 부분만 수정
		setProgressBarVisibility(View.VISIBLE);

		favoriteRestaurantViewModel.select(FavoriteLocationDTO.RESTAURANT, new CarrierMessagingService.ResultCallback<List<FavoriteLocationDTO>>() {
			int responseCount;

			@Override
			public void onReceiveResult(@NonNull List<FavoriteLocationDTO> favoriteLocationDTOS) throws RemoteException {
				Set<String> restaurantIdSetInDb = new HashSet<>();
				for (FavoriteLocationDTO favoriteLocationDTO : favoriteLocationDTOS) {
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
					setProgressBarVisibility(View.GONE);
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

						requireActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								adapter.notifyDataSetChanged();
								setProgressBarVisibility(View.GONE);
							}
						});

					}

					if (!addedSet.isEmpty()) {
						setProgressBarVisibility(View.VISIBLE);

						Set<FavoriteLocationDTO> restaurantDTOSet = new HashSet<>();
						for (FavoriteLocationDTO favoriteLocationDTO : favoriteLocationDTOS) {
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
		});

	}

	@Override
	public void onClickedFavoriteButton(KakaoLocalDocument kakaoLocalDocument, FavoriteLocationDTO favoriteLocationDTO, int groupPosition, int childPosition) {
		final String placeId = ((PlaceDocuments) kakaoLocalDocument).getId();

		favoriteRestaurantViewModel.contains(placeId, null, null, null, new CarrierMessagingService.ResultCallback<FavoriteLocationDTO>() {
			@Override
			public void onReceiveResult(@NonNull FavoriteLocationDTO favoriteLocationDTO) throws RemoteException {
				favoriteRestaurantViewModel.delete(favoriteLocationDTO.getId(),
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

										adapter.notifyDataSetChanged();
										if (restaurantListMap.size() >= 1 && !restaurantListMap.containsKey(key)) {
											binding.favoriteRestaurantList.collapseGroup(groupPosition);
										}
										favoriteLocationsListener.removeFavoriteLocationsPoiItem(favoriteLocationDTO);
									}
								});

							}
						});
			}
		});

	}


	@Override
	public void onClickedFavoriteButton(KakaoLocalDocument kakaoLocalDocument, FavoriteLocationDTO favoriteLocationDTO, int position) {

	}
}