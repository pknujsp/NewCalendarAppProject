package com.zerodsoft.scheduleweather.activity.map.fragment.searchresult;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.map.fragment.dto.SearchData;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.adapter.AddressesAdapter;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.interfaces.FragmentRemover;
import com.zerodsoft.scheduleweather.kakaomap.viewmodel.AddressViewModel;
import com.zerodsoft.scheduleweather.kakaomap.viewmodel.PlacesViewModel;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.scheduleinfo.placefragments.categoryview.adapter.PlaceItemsAdapters;

public class AddressListFragment extends Fragment
{
    private SearchData searchData;
    private RecyclerView itemRecyclerView;
    private AddressViewModel viewModel;
    private AddressesAdapter adapter;
    private FragmentRemover fragmentRemover;

    public AddressListFragment(FragmentRemover fragmentRemover, SearchData searchData)
    {
        this.fragmentRemover = fragmentRemover;
        this.searchData = new SearchData(searchData.getSearchWord(), searchData.getParameter().copy());
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
        itemRecyclerView = (RecyclerView) view.findViewById(R.id.map_search_result_recyclerview);
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false));
        viewModel = new ViewModelProvider(this).get(AddressViewModel.class);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        LocalApiPlaceParameter parameter = searchData.getParameter();
        if (KakaoLocalApiCategoryUtil.isCategory(searchData.getSearchWord()))
        {
            // fragmentRemover.removeFragment(this);
        } else
        {
            parameter.setQuery(searchData.getSearchWord());
            adapter = new AddressesAdapter(getContext());
            itemRecyclerView.setAdapter(adapter);

            viewModel.init(searchData.getParameter());
            viewModel.getPagedListMutableLiveData().observe(getViewLifecycleOwner(), new CustomLiveDataObserver<PagedList<AddressResponseDocuments>>()
            {
                @Override
                public void onChanged(PagedList<AddressResponseDocuments> addressResponseDocuments)
                {
                    // 검색 결과가 없으면 이 프래그먼트를 삭제한다.
                    // fragmentRemover.removeFragment(AddressListFragment.this);
                    adapter.submitList(addressResponseDocuments);
                }
            });
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
