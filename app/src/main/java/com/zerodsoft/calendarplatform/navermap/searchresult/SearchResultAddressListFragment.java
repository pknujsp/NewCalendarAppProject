package com.zerodsoft.calendarplatform.navermap.searchresult;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.common.interfaces.OnClickedListItem;
import com.zerodsoft.calendarplatform.databinding.FragmentLocationSearchResultBinding;
import com.zerodsoft.calendarplatform.etc.LocationType;
import com.zerodsoft.calendarplatform.navermap.interfaces.IMapData;
import com.zerodsoft.calendarplatform.navermap.searchresult.adapter.AddressesAdapter;
import com.zerodsoft.calendarplatform.kakaoplace.LocalParameterUtil;
import com.zerodsoft.calendarplatform.navermap.viewmodel.AddressViewModel;
import com.zerodsoft.calendarplatform.navermap.MarkerType;
import com.zerodsoft.calendarplatform.navermap.interfaces.OnExtraListDataListener;
import com.zerodsoft.calendarplatform.navermap.viewmodel.MapSharedViewModel;
import com.zerodsoft.calendarplatform.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.addressresponse.AddressResponseDocuments;

public class SearchResultAddressListFragment extends Fragment implements OnExtraListDataListener<LocationType> {
	private final String QUERY;
	private final OnClickedListItem<AddressResponseDocuments> addressResponseDocumentsOnClickedListItem;

	private FragmentLocationSearchResultBinding binding;

	private AddressViewModel addressViewModel;
	private AddressesAdapter adapter;
	private MapSharedViewModel mapSharedViewModel;

	private IMapData iMapData;

	public SearchResultAddressListFragment(String query, OnClickedListItem<AddressResponseDocuments> addressResponseDocumentsOnClickedListItem) {
		this.QUERY = query;
		this.addressResponseDocumentsOnClickedListItem = addressResponseDocumentsOnClickedListItem;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mapSharedViewModel = new ViewModelProvider(getParentFragment().getParentFragment()).get(MapSharedViewModel.class);
		iMapData = mapSharedViewModel.getiMapData();
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
		binding.customProgressView.setContentView(binding.searchResultRecyclerview);
		binding.customProgressView.onSuccessfulProcessingData();

		binding.mapSearchResultHeader.setVisibility(View.GONE);
		binding.searchResultType.setText(getString(R.string.result_address));

		binding.searchResultRecyclerview.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false));
		binding.searchResultRecyclerview.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
		addressViewModel = new ViewModelProvider(this).get(AddressViewModel.class);

		adapter = new AddressesAdapter(getContext(), addressResponseDocumentsOnClickedListItem);
		adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				super.onItemRangeInserted(positionStart, itemCount);

				if (positionStart > 0) {
					iMapData.addExtraMarkers(adapter.getCurrentList().snapshot(), MarkerType.SEARCH_RESULT_ADDRESS);
				} else {
					if (itemCount > 0) {
						iMapData.createMarkers(adapter.getCurrentList().snapshot(), MarkerType.SEARCH_RESULT_ADDRESS);
					}
				}
			}
		});
		binding.searchResultRecyclerview.setAdapter(adapter);
		LocalApiPlaceParameter parameter = LocalParameterUtil.getAddressParameter(QUERY, LocalApiPlaceParameter.DEFAULT_SIZE
				, LocalApiPlaceParameter.DEFAULT_PAGE);

		addressViewModel.init(parameter);
		addressViewModel.getPagedListMutableLiveData().observe(getViewLifecycleOwner(), new Observer<PagedList<AddressResponseDocuments>>() {
			@Override
			public void onChanged(PagedList<AddressResponseDocuments> addressResponseDocuments) {
				adapter.submitList(addressResponseDocuments);
			}
		});
	}


	@Override
	public void loadExtraListData(LocationType e, RecyclerView.AdapterDataObserver adapterDataObserver) {

	}

	@Override
	public void loadExtraListData(RecyclerView.AdapterDataObserver adapterDataObserver) {
		adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				super.onItemRangeInserted(positionStart, itemCount);
				adapterDataObserver.onItemRangeInserted(positionStart, itemCount);
				adapter.unregisterAdapterDataObserver(this);
			}
		});
		binding.searchResultRecyclerview.scrollBy(0, 10000);
	}

}
