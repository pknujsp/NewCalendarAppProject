package com.zerodsoft.scheduleweather.navermap.fragment.search;

import android.os.Bundle;

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
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.common.interfaces.OnSearchListener;
import com.zerodsoft.scheduleweather.event.places.interfaces.PoiItemOnClickListener;
import com.zerodsoft.scheduleweather.navermap.BottomSheetType;
import com.zerodsoft.scheduleweather.navermap.fragment.search.adapter.SearchLocationHistoryAdapter;
import com.zerodsoft.scheduleweather.navermap.interfaces.BottomSheetController;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapData;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.navermap.fragment.search.adapter.PlaceCategoriesAdapter;
import com.zerodsoft.scheduleweather.databinding.FragmentSearchBinding;
import com.zerodsoft.scheduleweather.navermap.interfaces.SearchFragmentController;
import com.zerodsoft.scheduleweather.navermap.viewmodel.MapSharedViewModel;
import com.zerodsoft.scheduleweather.navermap.viewmodel.SearchHistoryViewModel;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;
import com.zerodsoft.scheduleweather.room.dto.SearchHistoryDTO;

import java.util.List;

public class LocationSearchFragment extends Fragment {
	private FragmentSearchBinding binding;

	private SearchLocationHistoryAdapter searchLocationHistoryAdapter;
	private SearchHistoryViewModel searchHistoryViewModel;

	private BottomSheetController bottomSheetController;
	private MapSharedViewModel mapSharedViewModel;

	private final OnClickedListItem<SearchHistoryDTO> searchHistoryDTOOnClickedListItem = new OnClickedListItem<SearchHistoryDTO>() {
		@Override
		public void onClickedListItem(SearchHistoryDTO e, int position) {

		}

		@Override
		public void deleteListItem(SearchHistoryDTO e, int position) {
			searchHistoryViewModel.delete(e.getId());
			searchLocationHistoryAdapter.getHistoryList().remove(position);
			searchLocationHistoryAdapter.notifyItemRemoved(position);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mapSharedViewModel = new ViewModelProvider(getParentFragment()).get(MapSharedViewModel.class);
		bottomSheetController = mapSharedViewModel.getBottomSheetController();
		searchHistoryViewModel = new ViewModelProvider(getParentFragment()).get(SearchHistoryViewModel.class);

		searchHistoryViewModel.getOnAddedHistoryDTOMutableLiveData().observe(this, new Observer<SearchHistoryDTO>() {
			@Override
			public void onChanged(SearchHistoryDTO searchHistoryDTO) {
				searchLocationHistoryAdapter.getHistoryList().add(searchHistoryDTO);
				searchLocationHistoryAdapter.notifyItemInserted(searchLocationHistoryAdapter.getItemCount() - 1);
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

		searchLocationHistoryAdapter = new SearchLocationHistoryAdapter(searchHistoryDTOOnClickedListItem);
		binding.searchHistoryRecyclerview.setAdapter(searchLocationHistoryAdapter);

		searchHistoryViewModel.select(SearchHistoryDTO.LOCATION_SEARCH, new DbQueryCallback<List<SearchHistoryDTO>>() {
			@Override
			public void onResultSuccessful(List<SearchHistoryDTO> result) {
				searchLocationHistoryAdapter.setHistoryList(result);

				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						searchLocationHistoryAdapter.notifyDataSetChanged();
					}
				});
			}

			@Override
			public void onResultNoData() {

			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		bottomSheetController.setStateOfBottomSheet(BottomSheetType.SEARCH_LOCATION, BottomSheetBehavior.STATE_COLLAPSED);
	}
}