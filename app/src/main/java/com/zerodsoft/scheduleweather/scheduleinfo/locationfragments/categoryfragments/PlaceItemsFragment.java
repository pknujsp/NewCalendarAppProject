package com.zerodsoft.scheduleweather.scheduleinfo.locationfragments.categoryfragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.ImprovedScrollingRecyclerView;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryCode;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.scheduleinfo.locationfragments.LocationInfo;
import com.zerodsoft.scheduleweather.scheduleinfo.locationfragments.LocationInfoGetter;
import com.zerodsoft.scheduleweather.scheduleinfo.locationfragments.categoryfragments.adapter.PlaceItemsAdapters;
import com.zerodsoft.scheduleweather.scheduleinfo.locationfragments.categoryfragments.viewmodel.AroundLocationViewModel;

public class PlaceItemsFragment extends Fragment
{
    private final String QUERY;
    private LocationInfo locationInfo;
    private LocationInfoGetter locationInfoGetter;
    private RecyclerView itemRecyclerView;
    private AroundLocationViewModel viewModel;
    private PlaceItemsAdapters adapter;

    public PlaceItemsFragment(String query, LocationInfoGetter locationInfoGetter)
    {
        QUERY = query;
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
        ((TextView) view.findViewById(R.id.map_category_name)).setText(QUERY);
        itemRecyclerView = (ImprovedScrollingRecyclerView) view.findViewById(R.id.map_category_itemsview);
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        itemRecyclerView.addItemDecoration(new RecyclerViewItemDecoration((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, getResources().getDisplayMetrics())));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        LocalApiPlaceParameter placeParameter = new LocalApiPlaceParameter();
        placeParameter.setPage(LocalApiPlaceParameter.DEFAULT_PAGE).setRadius(LocalApiPlaceParameter.DEFAULT_RADIUS)
                .setSize(LocalApiPlaceParameter.DEFAULT_SIZE).setSort(LocalApiPlaceParameter.SORT_ACCURACY)
                .setX(locationInfo.getLongitude()).setY(locationInfo.getLatitude());
        String categoryDescription = KakaoLocalApiCategoryCode.getDescription(QUERY);

        if (categoryDescription == null)
        {
            placeParameter.setQuery(QUERY);
        } else
        {
            placeParameter.setCategoryGroupCode(categoryDescription);
        }
        viewModel = new ViewModelProvider(this).get(AroundLocationViewModel.class);
        viewModel.init(placeParameter);

        adapter = new PlaceItemsAdapters();
        viewModel.getPagedListMutableLiveData().observe(getViewLifecycleOwner(), adapter::submitList);
        itemRecyclerView.setAdapter(adapter);
    }

    static class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration
    {
        private final int spacing;

        public RecyclerViewItemDecoration(int spacing)
        {
            this.spacing = spacing;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state)
        {
            super.getItemOffsets(outRect, view, parent, state);
            if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1)
            {
                outRect.right = spacing;
            }
        }
    }
}
