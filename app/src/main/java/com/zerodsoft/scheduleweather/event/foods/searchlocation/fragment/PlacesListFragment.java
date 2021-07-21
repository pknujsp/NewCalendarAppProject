package com.zerodsoft.scheduleweather.event.foods.searchlocation.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.zerodsoft.scheduleweather.databinding.FragmentLocationSearchResultBinding;
import com.zerodsoft.scheduleweather.event.foods.searchlocation.adapter.PlacesListAdapter;
import com.zerodsoft.scheduleweather.event.foods.searchlocation.interfaces.OnClickedLocationItem;
import com.zerodsoft.scheduleweather.kakaoplace.LocalParameterUtil;
import com.zerodsoft.scheduleweather.navermap.viewmodel.PlacesViewModel;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;

public class PlacesListFragment extends Fragment {
	private PlacesViewModel viewModel;
	private PlacesListAdapter adapter;
	private FragmentLocationSearchResultBinding binding;

	private final OnClickedLocationItem onClickedLocationItem;
	private final String searchWord;

	public PlacesListFragment(Fragment fragment, String searchWord) {
		this.onClickedLocationItem = (OnClickedLocationItem) fragment;
		this.searchWord = searchWord;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = FragmentLocationSearchResultBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		binding.searchResultType.setText(getString(R.string.result_place));

		binding.mapSearchResultHeader.setVisibility(View.GONE);
		binding.searchResultRecyclerview.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false));
		binding.searchResultRecyclerview.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
		viewModel = new ViewModelProvider(this).get(PlacesViewModel.class);

		requestPlaces(searchWord);
	}

	private void requestPlaces(String searchWord) {
		final LocalApiPlaceParameter placeParameter = LocalParameterUtil.getPlaceParameter(searchWord, null, null,
				LocalApiPlaceParameter.DEFAULT_SIZE, LocalApiPlaceParameter.DEFAULT_PAGE, LocalApiPlaceParameter.SEARCH_CRITERIA_SORT_TYPE_ACCURACY);

		adapter = new PlacesListAdapter(onClickedLocationItem);
		binding.searchResultRecyclerview.setAdapter(adapter);
		viewModel.init(placeParameter, new PagedList.BoundaryCallback<PlaceDocuments>() {
			@Override
			public void onZeroItemsLoaded() {
				super.onZeroItemsLoaded();
			}
		});

		viewModel.getPagedListMutableLiveData().observe(getViewLifecycleOwner(), new Observer<PagedList<PlaceDocuments>>() {
			@Override
			public void onChanged(PagedList<PlaceDocuments> placeDocuments) {
				adapter.submitList(placeDocuments);
			}
		});

	}


}