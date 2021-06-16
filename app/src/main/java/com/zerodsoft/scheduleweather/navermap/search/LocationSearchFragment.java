package com.zerodsoft.scheduleweather.navermap.search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.navermap.BottomSheetType;
import com.zerodsoft.scheduleweather.navermap.search.adapter.SearchLocationHistoryAdapter;
import com.zerodsoft.scheduleweather.navermap.searchheader.MapHeaderSearchFragment;
import com.zerodsoft.scheduleweather.navermap.interfaces.BottomSheetController;
import com.zerodsoft.scheduleweather.databinding.FragmentSearchBinding;
import com.zerodsoft.scheduleweather.navermap.viewmodel.MapSharedViewModel;
import com.zerodsoft.scheduleweather.navermap.viewmodel.SearchHistoryViewModel;
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
			((MapHeaderSearchFragment) getParentFragmentManager().findFragmentByTag(getString(R.string.tag_map_header_search_fragment))).setQuery(e.getValue(), true);
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
		binding.customProgressView.setContentView(binding.searchHistoryRecyclerview);
		binding.customProgressView.onSuccessfulProcessingData();

		binding.searchHistoryRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
		binding.searchHistoryRecyclerview.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

		searchLocationHistoryAdapter = new SearchLocationHistoryAdapter(searchHistoryDTOOnClickedListItem);
		searchLocationHistoryAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
				if (searchLocationHistoryAdapter.getItemCount() == 0) {
					binding.customProgressView.onFailedProcessingData(getString(R.string.not_search_history));
				} else {
					binding.customProgressView.onSuccessfulProcessingData();
				}
			}

			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				super.onItemRangeInserted(positionStart, itemCount);
				if (positionStart == 0) {
					binding.customProgressView.onSuccessfulProcessingData();
				}
			}

			@Override
			public void onItemRangeRemoved(int positionStart, int itemCount) {
				super.onItemRangeRemoved(positionStart, itemCount);
				if (searchLocationHistoryAdapter.getItemCount() == 0) {
					binding.customProgressView.onFailedProcessingData(getString(R.string.not_search_history));
				}
			}
		});
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