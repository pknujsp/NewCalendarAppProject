package com.zerodsoft.scheduleweather.navermap.fragment.search;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.naver.maps.map.overlay.Marker;
import com.zerodsoft.scheduleweather.common.interfaces.OnBackPressedCallbackController;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.event.places.interfaces.PoiItemOnClickListener;
import com.zerodsoft.scheduleweather.navermap.BottomSheetType;
import com.zerodsoft.scheduleweather.navermap.fragment.search.adapter.SearchLocationHistoryAdapter;
import com.zerodsoft.scheduleweather.navermap.fragment.searchresult.LocationSearchResultFragment;
import com.zerodsoft.scheduleweather.navermap.interfaces.BottomSheetController;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapData;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnSelectedMapCategory;
import com.zerodsoft.scheduleweather.navermap.fragment.search.adapter.PlaceCategoriesAdapter;
import com.zerodsoft.scheduleweather.databinding.FragmentSearchBinding;
import com.zerodsoft.scheduleweather.navermap.interfaces.SearchFragmentController;
import com.zerodsoft.scheduleweather.navermap.viewmodel.MapSharedViewModel;
import com.zerodsoft.scheduleweather.navermap.viewmodel.SearchHistoryViewModel;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;
import com.zerodsoft.scheduleweather.room.dto.SearchHistoryDTO;

import java.util.List;

public class LocationSearchFragment extends Fragment implements OnSelectedMapCategory, OnClickedListItem<SearchHistoryDTO> {
	public static final String TAG = "LocationSearchFragment";

	private final PoiItemOnClickListener<Marker> poiItemOnClickListener;
	private FragmentSearchBinding binding;

	private PlaceCategoriesAdapter categoriesAdapter;
	private SearchLocationHistoryAdapter searchLocationHistoryAdapter;
	private SearchHistoryViewModel searchHistoryViewModel;

	private IMapPoint iMapPoint;
	private IMapData iMapData;
	private SearchFragmentController searchFragmentController;
	private MapSharedViewModel mapSharedViewModel;


	public LocationSearchFragment(PoiItemOnClickListener<Marker> poiItemOnClickListener) {
		this.poiItemOnClickListener = poiItemOnClickListener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mapSharedViewModel = new ViewModelProvider(getParentFragment()).get(MapSharedViewModel.class);
		searchHistoryViewModel = new ViewModelProvider(getParentFragment()).get(SearchHistoryViewModel.class);

		searchHistoryViewModel.getOnAddedHistoryDTOMutableLiveData().observe(this, new Observer<SearchHistoryDTO>() {
			@Override
			public void onChanged(SearchHistoryDTO searchHistoryDTO) {

			}
		});

		searchHistoryViewModel.getOnRemovedHistoryDTOMutableLiveData().observe(this, new Observer<Integer>() {
			@Override
			public void onChanged(Integer integer) {

			}
		});
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentSearchBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		binding.searchHistoryRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
		binding.searchHistoryRecyclerview.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

		categoriesAdapter = new PlaceCategoriesAdapter(this);
		binding.categoriesRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
		binding.categoriesRecyclerview.setAdapter(categoriesAdapter);

		searchLocationHistoryAdapter = new SearchLocationHistoryAdapter(LocationSearchFragment.this);
		binding.searchHistoryRecyclerview.setAdapter(searchLocationHistoryAdapter);

		searchHistoryViewModel.select(SearchHistoryDTO.LOCATION_SEARCH, new CarrierMessagingService.ResultCallback<List<SearchHistoryDTO>>() {
			@Override
			public void onReceiveResult(@NonNull List<SearchHistoryDTO> result) throws RemoteException {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						searchLocationHistoryAdapter.setHistoryList(result);
						searchLocationHistoryAdapter.notifyDataSetChanged();
					}
				});
			}
		});
	}


	@Override
	public void onSelectedMapCategory(PlaceCategoryDTO category) {
		searchBarController.setQuery(category.getCode(), true);
		searchBarController.changeViewTypeImg(SearchBarController.MAP);
	}

	@Override
	public void updateSearchHistoryList() {
		searchHistoryViewModel.select(SearchHistoryDTO.LOCATION_SEARCH, new CarrierMessagingService.ResultCallback<List<SearchHistoryDTO>>() {
			@Override
			public void onReceiveResult(@NonNull List<SearchHistoryDTO> result) throws RemoteException {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//최신 리스트의 크기와 어댑터내 리스트의 크기가 다르면 갱신
						if (searchLocationHistoryAdapter.getItemCount() != result.size()) {
							searchLocationHistoryAdapter.setHistoryList(result);
							searchLocationHistoryAdapter.notifyDataSetChanged();
						}
					}
				});
			}
		});
	}


	@Override
	public void onClickedListItem(SearchHistoryDTO e, int position) {
		searchBarController.setQuery(e.getValue(), true);
	}

	@Override
	public void deleteListItem(SearchHistoryDTO e, int position) {
		searchHistoryViewModel.delete(e.getId(), new CarrierMessagingService.ResultCallback<Boolean>() {
			@Override
			public void onReceiveResult(@NonNull Boolean result) throws RemoteException {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						searchLocationHistoryAdapter.getHistoryList().remove(position);
						searchLocationHistoryAdapter.notifyItemRemoved(position);
					}
				});

			}
		});

	}
}