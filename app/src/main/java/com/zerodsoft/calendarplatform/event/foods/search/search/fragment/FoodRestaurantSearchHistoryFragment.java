package com.zerodsoft.calendarplatform.event.foods.search.search.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.calendarplatform.common.interfaces.DbQueryCallback;
import com.zerodsoft.calendarplatform.common.interfaces.OnClickedListItem;
import com.zerodsoft.calendarplatform.databinding.FragmentFoodRestaurantSearchHistoryBinding;
import com.zerodsoft.calendarplatform.etc.CustomRecyclerViewItemDecoration;
import com.zerodsoft.calendarplatform.event.foods.search.search.adapter.FoodRestaurantSearchHistoryAdapter;
import com.zerodsoft.calendarplatform.navermap.viewmodel.SearchHistoryViewModel;
import com.zerodsoft.calendarplatform.room.dto.SearchHistoryDTO;

import java.util.List;

public class FoodRestaurantSearchHistoryFragment extends Fragment implements OnClickedListItem<SearchHistoryDTO> {
	private FragmentFoodRestaurantSearchHistoryBinding binding;
	private OnClickedListItem<SearchHistoryDTO> onClickedListItem;
	private SearchHistoryViewModel searchHistoryViewModel;
	private FoodRestaurantSearchHistoryAdapter adapter;

	public FoodRestaurantSearchHistoryFragment(OnClickedListItem<SearchHistoryDTO> onClickedListItem) {
		this.onClickedListItem = onClickedListItem;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		searchHistoryViewModel = new ViewModelProvider(getParentFragment().getParentFragment()).get(SearchHistoryViewModel.class);

		searchHistoryViewModel.getOnAddedHistoryDTOMutableLiveData().observe(this, new Observer<SearchHistoryDTO>() {
			@Override
			public void onChanged(SearchHistoryDTO addedSearchHistoryDTO) {
				if (getActivity() != null) {
					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							adapter.getHistoryList().add(addedSearchHistoryDTO);
							adapter.notifyDataSetChanged();
						}
					});
				}
			}
		});

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentFoodRestaurantSearchHistoryBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		binding.notHistory.setVisibility(View.GONE);

		binding.searchHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
		binding.searchHistoryRecyclerView.addItemDecoration
				(new CustomRecyclerViewItemDecoration((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f,
						getResources().getDisplayMetrics())));

		adapter = new FoodRestaurantSearchHistoryAdapter(FoodRestaurantSearchHistoryFragment.this);
		adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
				if (adapter.getItemCount() > 0) {
					binding.searchHistoryRecyclerView.setVisibility(View.VISIBLE);
					binding.notHistory.setVisibility(View.GONE);
				} else {
					binding.searchHistoryRecyclerView.setVisibility(View.GONE);
					binding.notHistory.setVisibility(View.VISIBLE);
					binding.deleteAllSearchHistory.setVisibility(View.GONE);
				}
			}

			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				super.onItemRangeInserted(positionStart, itemCount);
				if (adapter.getItemCount() > 0) {
					binding.searchHistoryRecyclerView.setVisibility(View.VISIBLE);
					binding.notHistory.setVisibility(View.GONE);
					binding.deleteAllSearchHistory.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onItemRangeRemoved(int positionStart, int itemCount) {
				super.onItemRangeRemoved(positionStart, itemCount);
				if (adapter.getItemCount() > 0) {
					binding.searchHistoryRecyclerView.setVisibility(View.VISIBLE);
					binding.notHistory.setVisibility(View.GONE);
					binding.deleteAllSearchHistory.setVisibility(View.VISIBLE);
				} else {
					binding.searchHistoryRecyclerView.setVisibility(View.GONE);
					binding.notHistory.setVisibility(View.VISIBLE);
					binding.deleteAllSearchHistory.setVisibility(View.GONE);
				}
			}

		});

		binding.searchHistoryRecyclerView.setAdapter(adapter);
		searchHistoryViewModel.select(SearchHistoryDTO.FOOD_RESTAURANT_SEARCH, new DbQueryCallback<List<SearchHistoryDTO>>() {
			@Override
			public void onResultSuccessful(List<SearchHistoryDTO> result) {
				if (getActivity() != null) {
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							adapter.setHistoryList(result);
							adapter.notifyDataSetChanged();
						}
					});
				}
			}

			@Override
			public void onResultNoData() {

			}
		});


		binding.deleteAllSearchHistory.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (adapter.getItemCount() > 0) {
					searchHistoryViewModel.deleteAll(SearchHistoryDTO.FOOD_RESTAURANT_SEARCH, new DbQueryCallback<Boolean>() {
						@Override
						public void onResultSuccessful(Boolean result) {
							if (getActivity() != null) {
								getActivity().runOnUiThread(new Runnable() {
									@Override
									public void run() {
										adapter.getHistoryList().clear();
										adapter.notifyDataSetChanged();
									}
								});
							}
						}

						@Override
						public void onResultNoData() {

						}
					});
				}
			}
		});


	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}


	@Override
	public void onClickedListItem(SearchHistoryDTO e, int position) {
		onClickedListItem.onClickedListItem(e, position);
	}

	@Override
	public void deleteListItem(SearchHistoryDTO e, int position) {
		searchHistoryViewModel.delete(e.getId());
		adapter.getHistoryList().remove(position);
		adapter.notifyItemRemoved(position);
	}

}