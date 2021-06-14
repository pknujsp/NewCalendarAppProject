package com.zerodsoft.scheduleweather.event.foods.main;

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

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.common.interfaces.OnProgressViewListener;
import com.zerodsoft.scheduleweather.databinding.FragmentRestaurantListBinding;
import com.zerodsoft.scheduleweather.event.foods.adapter.RestaurantListAdapter;
import com.zerodsoft.scheduleweather.event.foods.favorite.restaurant.FavoriteLocationViewModel;
import com.zerodsoft.scheduleweather.event.foods.interfaces.IOnSetView;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.RestaurantSharedViewModel;
import com.zerodsoft.scheduleweather.kakaoplace.viewmodel.KakaoRestaurantsViewModel;
import com.zerodsoft.scheduleweather.navermap.place.PlaceInfoWebFragment;
import com.zerodsoft.scheduleweather.navermap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;
import com.zerodsoft.scheduleweather.room.interfaces.FavoriteLocationQuery;

import java.util.List;

public class RestaurantListFragment extends Fragment implements OnClickedListItem<PlaceDocuments> {
	protected FragmentRestaurantListBinding binding;
	protected String query;
	protected KakaoRestaurantsViewModel kakaoRestaurantsViewModel;
	protected RestaurantListAdapter adapter;
	protected RecyclerView.AdapterDataObserver adapterDataObserver;
	protected FavoriteLocationViewModel favoriteRestaurantViewModel;
	protected RestaurantSharedViewModel sharedViewModel;
	protected IOnSetView iOnSetView;

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

		favoriteRestaurantViewModel =
				new ViewModelProvider(getParentFragment().getParentFragment().getParentFragment().getParentFragment()).get(FavoriteLocationViewModel.class);
		sharedViewModel = new ViewModelProvider(getParentFragment().getParentFragment().getParentFragment()).get(RestaurantSharedViewModel.class);

		favoriteRestaurantViewModel.getAddedFavoriteLocationMutableLiveData().observe(this,
				new Observer<FavoriteLocationDTO>() {
					@Override
					public void onChanged(FavoriteLocationDTO addedFavoriteLocationDTO) {
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


					}
				});

		favoriteRestaurantViewModel.getRemovedFavoriteLocationMutableLiveData().observe(this, new Observer<FavoriteLocationDTO>() {
			@Override
			public void onChanged(FavoriteLocationDTO removedFavoriteLocationDTO) {
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
		kakaoRestaurantsViewModel = new ViewModelProvider(this).get(KakaoRestaurantsViewModel.class);

		binding.recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false));
		binding.recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));

		adapter = new RestaurantListAdapter(getContext(), RestaurantListFragment.this, favoriteLocationQuery);
		binding.recyclerView.setAdapter(adapter);

		requestRestaurantList(query);
	}

	private final FavoriteLocationQuery favoriteLocationQuery = new FavoriteLocationQuery() {
		@Override
		public void addNewFavoriteLocation(FavoriteLocationDTO favoriteLocationDTO, DbQueryCallback<FavoriteLocationDTO> callback) {
			favoriteRestaurantViewModel.addNewFavoriteLocation(favoriteLocationDTO, callback);
		}

		@Override
		public void getFavoriteLocations(Integer type, DbQueryCallback<List<FavoriteLocationDTO>> callback) {

		}

		@Override
		public void getFavoriteLocation(Integer id, DbQueryCallback<FavoriteLocationDTO> callback) {

		}

		@Override
		public void delete(FavoriteLocationDTO favoriteLocationDTO, DbQueryCallback<Boolean> callback) {
			favoriteRestaurantViewModel.delete(favoriteLocationDTO, callback);
		}

		@Override
		public void deleteAll(Integer type, DbQueryCallback<Boolean> callback) {

		}

		@Override
		public void deleteAll(DbQueryCallback<Boolean> callback) {

		}

		@Override
		public void contains(String placeId, String latitude, String longitude, DbQueryCallback<FavoriteLocationDTO> callback) {
			favoriteRestaurantViewModel.contains(placeId, latitude, longitude, new DbQueryCallback<FavoriteLocationDTO>() {
				@Override
				public void onResultSuccessful(FavoriteLocationDTO result) {
					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							callback.onResultSuccessful(result);
						}
					});
				}

				@Override
				public void onResultNoData() {
					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							callback.onResultNoData();

						}
					});
				}
			});

		}
	};

	public void requestRestaurantList(String query) {
		this.query = query;

		final LocalApiPlaceParameter placeParameter = LocalParameterUtil.getPlaceParameter(query,
				criteriaLatitude, criteriaLongitude,
				LocalApiPlaceParameter.DEFAULT_SIZE, LocalApiPlaceParameter.DEFAULT_PAGE,
				LocalApiPlaceParameter.SEARCH_CRITERIA_SORT_TYPE_ACCURACY);
		placeParameter.setRadius("20000");

		kakaoRestaurantsViewModel.init(placeParameter, (OnProgressViewListener) binding.customProgressView);
		kakaoRestaurantsViewModel.getPagedListMutableLiveData().observe(getViewLifecycleOwner(), new Observer<PagedList<PlaceDocuments>>() {
			boolean isFirst = true;

			@Override
			public void onChanged(PagedList<PlaceDocuments> placeDocuments) {
				adapter.submitList(placeDocuments);

				if (isFirst) {
					isFirst = false;
					adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
						@Override
						public void onItemRangeInserted(int positionStart, int itemCount) {
							super.onItemRangeInserted(positionStart, itemCount);

							if (adapterDataObserver != null) {
								adapterDataObserver.onItemRangeInserted(0, itemCount);
							}
							binding.customProgressView.onSuccessfulProcessingData();
						}

					});
				}
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

			String tag = getString(R.string.tag_place_info_web_fragment);

			Fragment parentFragment = getParentFragment();
			FragmentManager fragmentManager = parentFragment.getParentFragmentManager();

			fragmentManager.beginTransaction().hide(parentFragment)
					.add(R.id.content_fragment_container, placeInfoWebFragment, tag)
					.addToBackStack(tag).commit();
		} else {

		}
	}

	@Override
	public void deleteListItem(PlaceDocuments e, int position) {

	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
	}

}