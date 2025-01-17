package com.zerodsoft.calendarplatform.event.foods.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.common.interfaces.OnClickedListItem;
import com.zerodsoft.calendarplatform.common.interfaces.DbQueryCallback;
import com.zerodsoft.calendarplatform.databinding.FragmentRestaurantListBinding;
import com.zerodsoft.calendarplatform.event.foods.adapter.RestaurantListAdapter;
import com.zerodsoft.calendarplatform.event.foods.favorite.restaurant.FavoriteLocationViewModel;
import com.zerodsoft.calendarplatform.event.foods.interfaces.IOnSetView;
import com.zerodsoft.calendarplatform.event.foods.interfaces.RestaurantListListener;
import com.zerodsoft.calendarplatform.event.foods.viewmodel.RestaurantSharedViewModel;
import com.zerodsoft.calendarplatform.kakaoplace.viewmodel.KakaoRestaurantsViewModel;
import com.zerodsoft.calendarplatform.navermap.places.PlaceInfoWebFragment;
import com.zerodsoft.calendarplatform.kakaoplace.LocalParameterUtil;
import com.zerodsoft.calendarplatform.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.calendarplatform.room.dto.FavoriteLocationDTO;
import com.zerodsoft.calendarplatform.room.interfaces.FavoriteLocationQuery;

import java.util.List;

public class RestaurantListFragment extends Fragment implements OnClickedListItem<PlaceDocuments> {
	protected FragmentRestaurantListBinding binding;
	protected String query;
	protected KakaoRestaurantsViewModel kakaoRestaurantsViewModel;
	protected RestaurantListAdapter adapter;
	protected RecyclerView.AdapterDataObserver adapterDataObserver;
	protected FavoriteLocationViewModel favoriteRestaurantViewModel;
	protected RestaurantSharedViewModel restaurantSharedViewModel;
	protected IOnSetView iOnSetView;
	protected RestaurantListListener restaurantListListenerInTab;

	protected String criteriaLatitude;
	protected String criteriaLongitude;

	public void setAdapterDataObserver(RecyclerView.AdapterDataObserver adapterDataObserver) {
		this.adapterDataObserver = adapterDataObserver;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		iOnSetView = (IOnSetView) getParentFragment().getParentFragment();

		Bundle bundle = getArguments();
		query = bundle.getString("query");
		criteriaLatitude = bundle.getString("criteriaLatitude");
		criteriaLongitude = bundle.getString("criteriaLongitude");

		if (getParentFragment() instanceof RestaurantListTabFragment) {
			restaurantListListenerInTab = (RestaurantListListener) getParentFragment();
		}

		favoriteRestaurantViewModel =
				new ViewModelProvider(requireActivity()).get(FavoriteLocationViewModel.class);
		restaurantSharedViewModel = new ViewModelProvider(getParentFragment().getParentFragment().getParentFragment()).get(RestaurantSharedViewModel.class);

		favoriteRestaurantViewModel.getAddedFavoriteLocationMutableLiveData().observe(this,
				new Observer<FavoriteLocationDTO>() {
					@Override
					public void onChanged(FavoriteLocationDTO addedFavoriteLocationDTO) {
						/*
						try {
							Fragment primaryNavFragment = getParentFragment().getParentFragment().getParentFragmentManager().getPrimaryNavigationFragment();

							if (!(primaryNavFragment instanceof RestaurantMainHostFragment)) {
								if (getActivity() != null) {
									getActivity().runOnUiThread(new Runnable() {
										@Override
										public void run() {
											try {
												adapter.notifyItemChanged(adapter.getItemPosition(addedFavoriteLocationDTO.getPlaceId()));
											} catch (Exception e) {
											}
										}
									});
								}
							} else {
								try {
									adapter.notifyItemChanged(adapter.getItemPosition(addedFavoriteLocationDTO.getPlaceId()));
								} catch (Exception e) {
								}
							}
						} catch (Exception e) {

						}

						 */
						adapter.notifyDataSetChanged();
					}
				});

		favoriteRestaurantViewModel.getRemovedFavoriteLocationMutableLiveData().observe(this, new Observer<FavoriteLocationDTO>() {
			@Override
			public void onChanged(FavoriteLocationDTO removedFavoriteLocationDTO) {
				/*
				try {
					Fragment primaryNavFragment = getParentFragment().getParentFragment().getParentFragmentManager().getPrimaryNavigationFragment();

					if (!(primaryNavFragment instanceof RestaurantMainHostFragment)) {
						if (getActivity() != null) {
							requireActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									try {
										adapter.notifyItemChanged(adapter.getItemPosition(removedFavoriteLocationDTO.getPlaceId()));
									} catch (Exception e) {
									}
								}
							});
						}
					} else {
						try {
							adapter.notifyItemChanged(adapter.getItemPosition(removedFavoriteLocationDTO.getPlaceId()));
						} catch (Exception e) {
						}
					}
				} catch (Exception e) {

				}

				 */
				adapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentRestaurantListBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		binding.customProgressView.setContentView(binding.recyclerView);
		binding.customProgressView.onSuccessfulProcessingData();

		kakaoRestaurantsViewModel = new ViewModelProvider(this).get(KakaoRestaurantsViewModel.class);

		binding.recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false));
		binding.recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));

		adapter = new RestaurantListAdapter(getContext(), RestaurantListFragment.this, favoriteLocationQuery);
		adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				super.onItemRangeInserted(positionStart, itemCount);
				binding.customProgressView.onSuccessfulProcessingData();

				if (restaurantListListenerInTab != null && itemCount > 0 && positionStart == 0) {
					restaurantListListenerInTab.onLoadedInitialRestaurantList(query, null);
				}
			}

		});
		binding.recyclerView.setAdapter(adapter);
		requestRestaurantList(query);
	}

	private final FavoriteLocationQuery favoriteLocationQuery = new FavoriteLocationQuery() {
		@Override
		public void addNewFavoriteLocation(FavoriteLocationDTO favoriteLocationDTO, @org.jetbrains.annotations.Nullable DbQueryCallback<FavoriteLocationDTO> callback) {
			favoriteRestaurantViewModel.addNewFavoriteLocation(favoriteLocationDTO, null);
		}

		@Override
		public void getFavoriteLocations(Integer type, DbQueryCallback<List<FavoriteLocationDTO>> callback) {

		}

		@Override
		public void getFavoriteLocation(Integer id, DbQueryCallback<FavoriteLocationDTO> callback) {

		}

		@Override
		public void delete(FavoriteLocationDTO favoriteLocationDTO, @org.jetbrains.annotations.Nullable DbQueryCallback<Boolean> callback) {
			favoriteRestaurantViewModel.delete(favoriteLocationDTO, null);
		}

		@Override
		public void deleteAll(Integer type, DbQueryCallback<Boolean> callback) {

		}

		@Override
		public void deleteAll(DbQueryCallback<Boolean> callback) {

		}

		@Override
		public void contains(String placeId, String latitude, String longitude, DbQueryCallback<FavoriteLocationDTO> callback) {
			favoriteRestaurantViewModel.contains(placeId, latitude, longitude, callback);
		}
	};

	public void requestRestaurantList(String query) {
		this.query = query;

		final LocalApiPlaceParameter placeParameter = LocalParameterUtil.getPlaceParameter(query,
				criteriaLatitude, criteriaLongitude,
				LocalApiPlaceParameter.DEFAULT_SIZE, LocalApiPlaceParameter.DEFAULT_PAGE,
				LocalApiPlaceParameter.SEARCH_CRITERIA_SORT_TYPE_ACCURACY);
		placeParameter.setRadius("7000");

		kakaoRestaurantsViewModel.init(placeParameter, new PagedList.BoundaryCallback<PlaceDocuments>() {
			@Override
			public void onZeroItemsLoaded() {
				super.onZeroItemsLoaded();
				if (restaurantListListenerInTab != null) {
					restaurantListListenerInTab.onLoadedInitialRestaurantList(query, null);
				}
				binding.customProgressView.onFailedProcessingData(getString(R.string.not_founded_search_result));
			}
		});
		kakaoRestaurantsViewModel.getPagedListMutableLiveData().observe(getViewLifecycleOwner(), new Observer<PagedList<PlaceDocuments>>() {
			@Override
			public void onChanged(PagedList<PlaceDocuments> placeDocuments) {
				adapter.submitList(placeDocuments);
			}
		});
	}

	@Override
	public void onClickedListItem(PlaceDocuments e, int position) {
		if (e != null) {
			PlaceInfoWebFragment placeInfoWebFragment = new PlaceInfoWebFragment();
			Bundle bundle = new Bundle();
			bundle.putString("placeId", ((PlaceDocuments) e).getId());
			placeInfoWebFragment.setArguments(bundle);

			Fragment parentFragment = getParentFragment();
			FragmentManager fragmentManager = parentFragment.getParentFragmentManager();

			String tag = getString(R.string.tag_place_info_web_fragment);
			fragmentManager.beginTransaction().hide(parentFragment)
					.add(R.id.content_fragment_container, placeInfoWebFragment, tag)
					.addToBackStack(tag).commit();
		} else {

		}
	}

	@Override
	public void deleteListItem(PlaceDocuments e, int position) {

	}


}