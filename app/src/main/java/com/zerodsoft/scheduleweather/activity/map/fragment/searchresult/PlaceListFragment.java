package com.zerodsoft.scheduleweather.activity.map.fragment.searchresult;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.RecyclerViewItemDecoration;
import com.zerodsoft.scheduleweather.activity.map.fragment.dto.SearchData;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.adapter.PlacesAdapter;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.interfaces.FragmentRemover;
import com.zerodsoft.scheduleweather.kakaomap.viewmodel.PlacesViewModel;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.scheduleinfo.placefragments.categoryview.adapter.PlaceItemsAdapters;

public class PlaceListFragment extends Fragment
{
    private SearchData searchData;
    private RecyclerView itemRecyclerView;
    private PlacesViewModel viewModel;
    private PlacesAdapter adapter;
    private FragmentRemover fragmentRemover;

    public PlaceListFragment(FragmentRemover fragmentRemover, SearchData searchData)
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
        viewModel = new ViewModelProvider(this).get(PlacesViewModel.class);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        LocalApiPlaceParameter parameter = searchData.getParameter();

        if (KakaoLocalApiCategoryUtil.isCategory(searchData.getSearchWord()))
        {
            parameter.setCategoryGroupCode(KakaoLocalApiCategoryUtil.getName(Integer.parseInt(searchData.getSearchWord())));
        } else
        {
            parameter.setQuery(searchData.getSearchWord());
        }
        adapter = new PlacesAdapter(getContext());
        itemRecyclerView.setAdapter(adapter);

        viewModel.init(searchData.getParameter());
        viewModel.getPagedListMutableLiveData().observe(getViewLifecycleOwner(), new Observer<PagedList<PlaceDocuments>>()
        {
            @Override
            public void onChanged(PagedList<PlaceDocuments> placeDocuments)
            {
                //  fragmentRemover.removeFragment(PlaceListFragment.this);
                adapter.submitList(placeDocuments);
            }
        });
    }

}
