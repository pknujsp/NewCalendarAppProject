package com.zerodsoft.scheduleweather.event.foods.fragment;

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
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.common.interfaces.OnProgressBarListener;
import com.zerodsoft.scheduleweather.event.foods.adapter.RestaurantListAdapter;
import com.zerodsoft.scheduleweather.event.foods.interfaces.CriteriaLocationListener;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnClickedRestaurantItem;
import com.zerodsoft.scheduleweather.event.foods.searchlocation.adapter.PlacesListAdapter;
import com.zerodsoft.scheduleweather.kakaomap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.kakaomap.viewmodel.PlacesViewModel;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

public class RestaurantListFragment extends Fragment
{
    private final OnClickedRestaurantItem onClickedRestaurantItem;
    private final CriteriaLocationListener criteriaLocationListener;
    private final String CATEGORY_NAME;

    private RecyclerView restaurantRecyclerView;
    private PlacesViewModel placesViewModel;
    private RestaurantListAdapter adapter;

    public RestaurantListFragment(OnClickedRestaurantItem onClickedRestaurantItem, CriteriaLocationListener criteriaLocationListener, String CATEGORY_NAME)
    {
        this.onClickedRestaurantItem = onClickedRestaurantItem;
        this.criteriaLocationListener = criteriaLocationListener;
        this.CATEGORY_NAME = CATEGORY_NAME;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_restaurant_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        restaurantRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        restaurantRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false));
        restaurantRecyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
        placesViewModel = new ViewModelProvider(this).get(PlacesViewModel.class);

        requestRestaurantList(CATEGORY_NAME);
    }


    private void requestRestaurantList(String categoryName)
    {
        LocationDTO criteriaLocation = criteriaLocationListener.getCriteriaLocation();

        final LocalApiPlaceParameter placeParameter = LocalParameterUtil.getPlaceParameter(categoryName, String.valueOf(criteriaLocation.getLatitude()),
                String.valueOf(criteriaLocation.getLongitude()), LocalApiPlaceParameter.DEFAULT_SIZE, LocalApiPlaceParameter.DEFAULT_PAGE,
                LocalApiPlaceParameter.SEARCH_CRITERIA_SORT_TYPE_ACCURACY);
        placeParameter.setRadius(App.getPreference_key_radius_range());

        adapter = new RestaurantListAdapter(getActivity(), onClickedRestaurantItem);
        restaurantRecyclerView.setAdapter(adapter);
        placesViewModel.init(placeParameter, new OnProgressBarListener()
        {
            @Override
            public void setProgressBarVisibility(int visibility)
            {

            }
        });

        placesViewModel.getPagedListMutableLiveData().observe(getViewLifecycleOwner(), new Observer<PagedList<PlaceDocuments>>()
        {
            @Override
            public void onChanged(PagedList<PlaceDocuments> placeDocuments)
            {
                adapter.submitList(placeDocuments);
            }
        });

    }
}