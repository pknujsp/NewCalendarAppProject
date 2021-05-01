package com.zerodsoft.scheduleweather.navermap.fragment.searchresult;

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

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.OnProgressBarListener;
import com.zerodsoft.scheduleweather.databinding.FragmentLocationSearchResultBinding;
import com.zerodsoft.scheduleweather.etc.LocationType;
import com.zerodsoft.scheduleweather.navermap.bottomsheet.adapter.LocationItemViewPagerAdapter;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnClickedLocListItem;
import com.zerodsoft.scheduleweather.navermap.fragment.searchresult.interfaces.IViewPager;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapData;
import com.zerodsoft.scheduleweather.navermap.fragment.searchresult.adapter.AddressesAdapter;
import com.zerodsoft.scheduleweather.navermap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.navermap.viewmodel.AddressViewModel;
import com.zerodsoft.scheduleweather.navermap.PoiItemType;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnExtraListDataListener;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressResponseDocuments;

public class SearchResultAddressListFragment extends Fragment implements IViewPager, OnProgressBarListener, OnExtraListDataListener<LocationType>
{
    private FragmentLocationSearchResultBinding binding;

    private AddressViewModel viewModel;
    private AddressesAdapter adapter;

    private final IMapData iMapData;
    private final OnClickedLocListItem onClickedLocListItem;
    private final String SEARCH_WORD;

    public SearchResultAddressListFragment(String searchWord, IMapData iMapData, OnClickedLocListItem onClickedLocListItem)
    {
        this.SEARCH_WORD = searchWord;
        this.iMapData = iMapData;
        this.onClickedLocListItem = onClickedLocListItem;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = FragmentLocationSearchResultBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        binding.mapSearchResultHeader.setVisibility(View.GONE);
        binding.searchResultType.setText(getString(R.string.result_address));

        binding.searchResultRecyclerview.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false));
        binding.searchResultRecyclerview.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
        viewModel = new ViewModelProvider(this).get(AddressViewModel.class);

        requestAddresses(SEARCH_WORD);
    }

    private void requestAddresses(String searchWord)
    {
        LocalApiPlaceParameter parameter = LocalParameterUtil.getAddressParameter(searchWord, LocalApiPlaceParameter.DEFAULT_SIZE
                , LocalApiPlaceParameter.DEFAULT_PAGE);

        if (KakaoLocalApiCategoryUtil.isCategory(SEARCH_WORD))
        {
            binding.progressBar.setVisibility(View.GONE);
        } else
        {
            adapter = new AddressesAdapter(getContext(), iMapData, onClickedLocListItem);
            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver()
            {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount)
                {
                    super.onItemRangeInserted(positionStart, itemCount);
                    if (positionStart > 0)
                    {
                        iMapData.addPoiItems(adapter.getCurrentList().snapshot(), PoiItemType.SEARCH_RESULT);
                    } else
                    {
                        iMapData.createPoiItems(adapter.getCurrentList().snapshot(), PoiItemType.SEARCH_RESULT);
                    }
                }
            });

            binding.searchResultRecyclerview.setAdapter(adapter);
            viewModel.init(parameter, this);
            viewModel.getPagedListMutableLiveData().observe(getViewLifecycleOwner(), new Observer<PagedList<AddressResponseDocuments>>()
            {
                @Override
                public void onChanged(PagedList<AddressResponseDocuments> addressResponseDocuments)
                {
                    adapter.submitList(addressResponseDocuments);
                }
            });
        }
    }

    /*
    viewpager의 페이지가 변경된 경우 호출
     */
    @Override
    public void onChangedPage()
    {
        int poiItemSize = iMapData.getPoiItemSize(PoiItemType.SEARCH_RESULT);
        iMapData.setPlacesListAdapter(new LocationItemViewPagerAdapter(), PoiItemType.SEARCH_RESULT);

        if (poiItemSize > 0 && adapter != null)
        {
            if (adapter.getItemCount() > 0)
            {
                iMapData.removeAllPoiItems();
                iMapData.createPoiItems(adapter.getCurrentList().snapshot(), PoiItemType.SEARCH_RESULT);
            }
        }
    }

    @Override
    public void setProgressBarVisibility(int visibility)
    {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                binding.progressBar.setVisibility(visibility);
            }
        });
    }

    @Override
    public void loadExtraListData(LocationType e, RecyclerView.AdapterDataObserver adapterDataObserver)
    {

    }

    @Override
    public void loadExtraListData(RecyclerView.AdapterDataObserver adapterDataObserver)
    {
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver()
        {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount)
            {
                super.onItemRangeInserted(positionStart, itemCount);
                adapterDataObserver.onItemRangeInserted(positionStart, itemCount);
                adapter.unregisterAdapterDataObserver(this);
            }
        });
        binding.searchResultRecyclerview.scrollBy(0, 10000);
    }
}
