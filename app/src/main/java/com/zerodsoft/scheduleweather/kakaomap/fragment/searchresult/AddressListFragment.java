package com.zerodsoft.scheduleweather.kakaomap.fragment.searchresult;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.zerodsoft.scheduleweather.kakaomap.bottomsheet.adapter.PlaceItemInMapViewAdapter;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.OnClickedLocListItem;
import com.zerodsoft.scheduleweather.kakaomap.fragment.searchresult.interfaces.IViewPager;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapData;
import com.zerodsoft.scheduleweather.kakaomap.fragment.searchresult.adapter.AddressesAdapter;
import com.zerodsoft.scheduleweather.kakaomap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.kakaomap.viewmodel.AddressViewModel;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseDocuments;

public class AddressListFragment extends Fragment implements IViewPager
{
    private RecyclerView itemRecyclerView;
    private AddressViewModel viewModel;
    private AddressesAdapter adapter;
    private ProgressBar progressBar;

    private final IMapData iMapData;
    private final OnClickedLocListItem onClickedLocListItem;
    private final String SEARCH_WORD;

    public AddressListFragment(String searchWord, IMapData iMapData, OnClickedLocListItem onClickedLocListItem)
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
        return inflater.inflate(R.layout.map_search_result_viewpager_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ((RelativeLayout) view.findViewById(R.id.map_search_result_header)).setVisibility(View.GONE);
        ((RelativeLayout) view.findViewById(R.id.map_search_result_header)).setEnabled(false);
        ((TextView) view.findViewById(R.id.map_search_result_type)).setText(getString(R.string.result_address));

        progressBar = (ProgressBar) view.findViewById(R.id.map_request_progress_bar);
        itemRecyclerView = (RecyclerView) view.findViewById(R.id.map_search_result_recyclerview);
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false));
        itemRecyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
        viewModel = new ViewModelProvider(this).get(AddressViewModel.class);
        progressBar.setVisibility(View.VISIBLE);

        LocalApiPlaceParameter parameter = LocalParameterUtil.getAddressParameter(SEARCH_WORD, LocalApiPlaceParameter.DEFAULT_SIZE
                , LocalApiPlaceParameter.DEFAULT_PAGE);

        if (KakaoLocalApiCategoryUtil.isCategory(SEARCH_WORD))
        {
            progressBar.setVisibility(View.GONE);
        } else
        {
            adapter = new AddressesAdapter(getContext(), iMapData, onClickedLocListItem);
            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver()
            {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount)
                {
                    super.onItemRangeInserted(positionStart, itemCount);
                    iMapData.createAddressesPoiItems(adapter.getCurrentList().snapshot());
                }
            });
            itemRecyclerView.setAdapter(adapter);
            viewModel.init(parameter);
            viewModel.getPagedListMutableLiveData().observe(getViewLifecycleOwner(), new CustomLiveDataObserver<PagedList<AddressResponseDocuments>>()
            {
                @Override
                public void onChanged(PagedList<AddressResponseDocuments> addressResponseDocuments)
                {
                    adapter.submitList(addressResponseDocuments);
                    progressBar.setVisibility(View.GONE);
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
        int poiItemSize = iMapData.getPoiItemSize();
        iMapData.setPlacesListAdapter(new PlaceItemInMapViewAdapter());

        if (poiItemSize > 0 && adapter != null)
        {
            if (adapter.getItemCount() > 0)
            {
                iMapData.removeAllPoiItems();
                iMapData.createAddressesPoiItems(adapter.getCurrentList().snapshot());
            }
        }
    }

    abstract class CustomLiveDataObserver<T> implements Observer<T>
    {
        @Override
        public void onChanged(T element)
        {

        }
    }
}
