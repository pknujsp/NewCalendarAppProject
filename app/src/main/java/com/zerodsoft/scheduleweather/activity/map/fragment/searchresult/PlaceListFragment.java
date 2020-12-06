package com.zerodsoft.scheduleweather.activity.map.fragment.searchresult;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
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
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.interfaces.FragmentRemover;
import com.zerodsoft.scheduleweather.kakaomap.viewmodel.PlacesViewModel;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.scheduleinfo.placefragments.categoryview.adapter.PlaceItemsAdapters;

public class PlaceListFragment extends Fragment
{
    private String searchWord;
    private LocalApiPlaceParameter parameter;
    private RecyclerView itemRecyclerView;
    private PlacesViewModel viewModel;
    private PlaceItemsAdapters adapter;
    private FragmentRemover fragmentRemover;

    public PlaceListFragment(Fragment fragment, String searchWord, LocalApiPlaceParameter parameter)
    {
        this.fragmentRemover = (FragmentRemover) fragment;
        this.searchWord = searchWord;
        this.parameter = parameter;
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

        KakaoLocalApiCategoryUtil.setParameterQuery(parameter, searchWord);
        adapter = new PlaceItemsAdapters(getContext());

        viewModel.init(parameter);
        viewModel.getPagedListMutableLiveData().observe(getViewLifecycleOwner(), new Observer<PagedList<PlaceDocuments>>()
        {
            @Override
            public void onChanged(PagedList<PlaceDocuments> placeDocuments)
            {
                if (placeDocuments.isEmpty())
                {
                    fragmentRemover.removeFragment(PlaceListFragment.this);
                } else
                {
                    adapter.submitList(placeDocuments);
                }
            }
        });
        itemRecyclerView.setAdapter(adapter);
    }

}
