package com.zerodsoft.scheduleweather.scheduleinfo.locationfragments.categoryfragments;

import android.os.Bundle;
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
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placecategoryresponse.PlaceCategory;
import com.zerodsoft.scheduleweather.scheduleinfo.locationfragments.LocationInfo;
import com.zerodsoft.scheduleweather.scheduleinfo.locationfragments.LocationInfoGetter;
import com.zerodsoft.scheduleweather.scheduleinfo.locationfragments.categoryfragments.adapters.AroundLocationRecyclerViewAdapter;

public class MapCategoryFragment extends Fragment
{
    private final String CATEGORY_NAME;
    private LocationInfo locationInfo;
    private LocationInfoGetter locationInfoGetter;
    private RecyclerView itemRecyclerView;
    private AroundLocationViewModel viewModel;
    private AroundLocationRecyclerViewAdapter adapter;

    public MapCategoryFragment(String categoryName, LocationInfoGetter locationInfoGetter)
    {
        CATEGORY_NAME = categoryName;
        this.locationInfoGetter = locationInfoGetter;
        locationInfo = locationInfoGetter.getLocationInfo();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_map_category_common, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ((TextView) view.findViewById(R.id.map_category_name)).setText(CATEGORY_NAME);
        itemRecyclerView = (RecyclerView) view.findViewById(R.id.map_category_itemsview);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AroundLocationViewModel.class);
        adapter = new AroundLocationRecyclerViewAdapter();
        viewModel.getPagedListMutableLiveData().observe(getViewLifecycleOwner(), adapter::submitList);
        itemRecyclerView.setAdapter(adapter);
    }
}
