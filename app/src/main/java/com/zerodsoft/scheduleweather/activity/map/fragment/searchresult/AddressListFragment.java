package com.zerodsoft.scheduleweather.activity.map.fragment.searchresult;

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
import com.zerodsoft.scheduleweather.activity.map.fragment.interfaces.IMapData;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.adapter.AddressesAdapter;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.interfaces.FragmentRemover;
import com.zerodsoft.scheduleweather.kakaomap.viewmodel.AddressViewModel;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseDocuments;

public class AddressListFragment extends Fragment
{
    private LocalApiPlaceParameter parameter = new LocalApiPlaceParameter();
    private RecyclerView itemRecyclerView;
    private AddressViewModel viewModel;
    private AddressesAdapter adapter;
    private FragmentRemover fragmentRemover;
    private ProgressBar progressBar;
    private IMapData iMapData;
    private final String SEARCH_WORD;

    public AddressListFragment(FragmentRemover fragmentRemover, String searchWord, IMapData iMapData)
    {
        this.fragmentRemover = fragmentRemover;
        this.SEARCH_WORD = searchWord;
        this.iMapData = iMapData;
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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        progressBar.setVisibility(View.VISIBLE);

        parameter.setQuery(SEARCH_WORD).setPage(LocalApiPlaceParameter.DEFAULT_PAGE)
                .setSize(LocalApiPlaceParameter.DEFAULT_SIZE);
        if (KakaoLocalApiCategoryUtil.isCategory(SEARCH_WORD))
        {
            // fragmentRemover.removeFragment(this);
            progressBar.setVisibility(View.GONE);
        } else
        {
            parameter.setQuery(SEARCH_WORD);
            adapter = new AddressesAdapter(getContext());
            itemRecyclerView.setAdapter(adapter);

            viewModel.init(parameter);
            viewModel.getPagedListMutableLiveData().observe(getViewLifecycleOwner(), new CustomLiveDataObserver<PagedList<AddressResponseDocuments>>()
            {
                @Override
                public void onChanged(PagedList<AddressResponseDocuments> addressResponseDocuments)
                {
                    // 검색 결과가 없으면 이 프래그먼트를 삭제한다.
                    // fragmentRemover.removeFragment(AddressListFragment.this);
                    adapter.submitList(addressResponseDocuments);
                    progressBar.setVisibility(View.GONE);
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
