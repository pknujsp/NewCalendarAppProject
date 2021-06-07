package com.zerodsoft.scheduleweather.event.foods.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.common.interfaces.OnProgressBarListener;
import com.zerodsoft.scheduleweather.databinding.FragmentRestaurantListBinding;
import com.zerodsoft.scheduleweather.event.foods.adapter.RestaurantListAdapter;
import com.zerodsoft.scheduleweather.event.foods.favorite.RestaurantFavoritesHostFragment;
import com.zerodsoft.scheduleweather.event.foods.favorite.restaurant.FavoriteLocationViewModel;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnClickedFavoriteButtonListener;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnSetViewVisibility;
import com.zerodsoft.scheduleweather.event.foods.share.CriteriaLocationCloud;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.RestaurantSharedViewModel;
import com.zerodsoft.scheduleweather.navermap.interfaces.FavoriteLocationsListener;
import com.zerodsoft.scheduleweather.navermap.place.PlaceInfoWebFragment;
import com.zerodsoft.scheduleweather.navermap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.navermap.viewmodel.PlacesViewModel;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;

public class RestaurantListFragment extends Fragment implements OnClickedListItem<PlaceDocuments>, OnClickedFavoriteButtonListener
		, RestaurantListAdapter.OnContainsRestaurantListener {
	protected FragmentRestaurantListBinding binding;
	protected String query;
	protected PlacesViewModel placesViewModel;
	protected RestaurantListAdapter adapter;
	protected RecyclerView.AdapterDataObserver adapterDataObserver;
	protected FavoriteLocationViewModel favoriteRestaurantViewModel;
	protected RestaurantSharedViewModel sharedViewModel;
	protected OnSetViewVisibility onSetViewVisibility;

	public void setAdapterDataObserver(RecyclerView.AdapterDataObserver adapterDataObserver) {
		this.adapterDataObserver = adapterDataObserver;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		Bundle bundle = getArguments();
		query = bundle.getString("query");

		favoriteRestaurantViewModel = new ViewModelProvider(requireActivity()).get(FavoriteLocationViewModel.class);
		sharedViewModel = new ViewModelProvider(requireActivity()).get(RestaurantSharedViewModel.class);
		onSetViewVisibility = sharedViewModel.getOnSetViewVisibility();

		favoriteRestaurantViewModel.getAddedFavoriteLocationMutableLiveData().observe(this,
				new Observer<FavoriteLocationDTO>() {
					@Override
					public void onChanged(FavoriteLocationDTO favoriteLocationDTO) {
						try {
							Fragment primaryNavFragment = getParentFragment().getParentFragment().getParentFragmentManager().getPrimaryNavigationFragment();

							if (!(primaryNavFragment instanceof RestaurantMainHostFragment)) {
								if (getActivity() != null) {
									requireActivity().runOnUiThread(new Runnable() {
										@Override
										public void run() {
											adapter.notifyDataSetChanged();
										}
									});
								}
							}
						} catch (Exception e) {

						}


					}
				});

		favoriteRestaurantViewModel.getRemovedFavoriteLocationMutableLiveData().observe(this, new Observer<Integer>() {
			@Override
			public void onChanged(Integer integer) {
				try {
					Fragment primaryNavFragment = getParentFragment().getParentFragment().getParentFragmentManager().getPrimaryNavigationFragment();

					if (!(primaryNavFragment instanceof RestaurantMainHostFragment)) {
						if (getActivity() != null) {
							requireActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									adapter.notifyDataSetChanged();
								}
							});
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
		placesViewModel = new ViewModelProvider(this).get(PlacesViewModel.class);

		binding.recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false));
		binding.recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));

		adapter = new RestaurantListAdapter(getContext(), RestaurantListFragment.this, RestaurantListFragment.this, RestaurantListFragment.this);
		binding.recyclerView.setAdapter(adapter);

		requestRestaurantList(query);
		binding.errorText.setVisibility(View.GONE);
	}


	public void requestRestaurantList(String query) {
		this.query = query;

		final LocalApiPlaceParameter placeParameter = LocalParameterUtil.getPlaceParameter(query,
				CriteriaLocationCloud.getLatitude(), CriteriaLocationCloud.getLongitude(),
				LocalApiPlaceParameter.DEFAULT_SIZE, LocalApiPlaceParameter.DEFAULT_PAGE,
				LocalApiPlaceParameter.SEARCH_CRITERIA_SORT_TYPE_ACCURACY);
		placeParameter.setRadius(App.getPreference_key_radius_range());

		placesViewModel.init(placeParameter, new OnProgressBarListener() {
			@Override
			public void setProgressBarVisibility(int visibility) {

			}
		});

		placesViewModel.getPagedListMutableLiveData().observe(getViewLifecycleOwner(), new Observer<PagedList<PlaceDocuments>>() {
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
							binding.errorText.setVisibility(View.GONE);

							if (adapterDataObserver != null) {
								adapterDataObserver.onItemRangeInserted(0, itemCount);
							}
						}
					});
				} else {
					if (adapter.getCurrentList().snapshot().isEmpty()) {
						binding.errorText.setVisibility(View.VISIBLE);
					}
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
			// restaurant list tab fragment

			fragmentManager.beginTransaction().hide(parentFragment)
					.add(R.id.fragment_container, placeInfoWebFragment, tag)
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

	@Override
	public void onClickedFavoriteButton(KakaoLocalDocument kakaoLocalDocument, FavoriteLocationDTO favoriteLocationDTO, int groupPosition, int childPosition) {

	}

	@Override
	public void onClickedFavoriteButton(KakaoLocalDocument kakaoLocalDocument, FavoriteLocationDTO favoriteLocationDTO, int position) {
		favoriteRestaurantViewModel.contains(((PlaceDocuments) kakaoLocalDocument).getId()
				, null, null, null, new DbQueryCallback<FavoriteLocationDTO>() {
					@Override
					public void onResultSuccessful(FavoriteLocationDTO result) {
						favoriteRestaurantViewModel.delete(result.getId(),
								new CarrierMessagingService.ResultCallback<Boolean>() {
									@Override
									public void onReceiveResult(@NonNull Boolean isDeleted) throws RemoteException {
										requireActivity().runOnUiThread(new Runnable() {
											@Override
											public void run() {
												adapter.notifyItemChanged(position);
											}
										});

									}
								});

					}

					@Override
					public void onResultNoData() {
						PlaceDocuments placeDocuments = (PlaceDocuments) kakaoLocalDocument;
						FavoriteLocationDTO newFavoriteLocationDTO = new FavoriteLocationDTO();
						newFavoriteLocationDTO.setPlaceId(placeDocuments.getId());
						newFavoriteLocationDTO.setPlaceName(placeDocuments.getPlaceName());
						newFavoriteLocationDTO.setLatitude(String.valueOf(placeDocuments.getY()));
						newFavoriteLocationDTO.setLongitude(String.valueOf(placeDocuments.getX()));
						newFavoriteLocationDTO.setType(FavoriteLocationDTO.RESTAURANT);
						newFavoriteLocationDTO.setAddress(placeDocuments.getAddressName());
						newFavoriteLocationDTO.setAddedDateTime(String.valueOf(System.currentTimeMillis()));

						favoriteRestaurantViewModel.insert(newFavoriteLocationDTO, new CarrierMessagingService.ResultCallback<FavoriteLocationDTO>() {
							@Override
							public void onReceiveResult(@NonNull FavoriteLocationDTO insertedFavoriteLocationDTO) throws RemoteException {
								requireActivity().runOnUiThread(new Runnable() {
									@Override
									public void run() {
										adapter.notifyItemChanged(position);
									}
								});
							}
						});
					}
				});


	}


	@Override
	public void contains(String placeId, DbQueryCallback<FavoriteLocationDTO> callback) {
		favoriteRestaurantViewModel.contains(placeId, null, null
				, null, new DbQueryCallback<FavoriteLocationDTO>() {
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
}