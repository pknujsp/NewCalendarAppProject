package com.zerodsoft.scheduleweather.event.foods.categorylist;

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
import android.widget.TextView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.common.interfaces.OnProgressBarListener;
import com.zerodsoft.scheduleweather.event.foods.adapter.RestaurantListAdapter;
import com.zerodsoft.scheduleweather.event.foods.favorite.restaurant.FavoriteRestaurantViewModel;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnClickedRestaurantItem;
import com.zerodsoft.scheduleweather.event.foods.share.CriteriaLocationRepository;
import com.zerodsoft.scheduleweather.kakaomap.place.PlaceInfoFragment;
import com.zerodsoft.scheduleweather.kakaomap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.kakaomap.viewmodel.PlacesViewModel;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.interfaces.FavoriteRestaurantQuery;

public class RestaurantListFragment extends Fragment implements OnClickedListItem<PlaceDocuments>
{
    protected final OnClickedRestaurantItem onClickedRestaurantItem;
    protected FavoriteRestaurantQuery favoriteRestaurantQuery;
    protected String CATEGORY_NAME;
    protected TextView errorTextView;

    protected RecyclerView restaurantRecyclerView;
    protected PlacesViewModel placesViewModel;
    protected RestaurantListAdapter adapter;

    public RestaurantListFragment(OnClickedRestaurantItem onClickedRestaurantItem, FavoriteRestaurantQuery favoriteRestaurantQuery, String CATEGORY_NAME)
    {
        this.onClickedRestaurantItem = onClickedRestaurantItem;
        this.favoriteRestaurantQuery = favoriteRestaurantQuery;
        this.CATEGORY_NAME = CATEGORY_NAME;
    }

    public RestaurantListFragment(OnClickedRestaurantItem onClickedRestaurantItem, String CATEGORY_NAME)
    {
        this.onClickedRestaurantItem = onClickedRestaurantItem;
        this.CATEGORY_NAME = CATEGORY_NAME;
    }

    public void setFavoriteRestaurantQuery(FavoriteRestaurantQuery favoriteRestaurantQuery)
    {
        this.favoriteRestaurantQuery = favoriteRestaurantQuery;
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

        errorTextView = (TextView) view.findViewById(R.id.error_text);
        restaurantRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        restaurantRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false));
        restaurantRecyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
        placesViewModel = new ViewModelProvider(this).get(PlacesViewModel.class);

        requestRestaurantList(CATEGORY_NAME);
        errorTextView.setVisibility(View.GONE);
    }


    protected void requestRestaurantList(String categoryName)
    {
        if (adapter != null)
        {
            restaurantRecyclerView.setAdapter(null);
            adapter = null;
        }

        LocationDTO criteriaLocation = CriteriaLocationRepository.getRestaurantCriteriaLocation();

        final LocalApiPlaceParameter placeParameter = LocalParameterUtil.getPlaceParameter(categoryName, String.valueOf(criteriaLocation.getLatitude()),
                String.valueOf(criteriaLocation.getLongitude()), LocalApiPlaceParameter.DEFAULT_SIZE, LocalApiPlaceParameter.DEFAULT_PAGE,
                LocalApiPlaceParameter.SEARCH_CRITERIA_SORT_TYPE_ACCURACY);
        placeParameter.setRadius(App.getPreference_key_radius_range());

        adapter = new RestaurantListAdapter(getActivity(), RestaurantListFragment.this, favoriteRestaurantQuery);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver()
        {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount)
            {
                super.onItemRangeInserted(positionStart, itemCount);
                errorTextView.setVisibility(View.GONE);
            }
        });

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
                if (adapter.getCurrentList().snapshot().isEmpty())
                {
                    errorTextView.setVisibility(View.VISIBLE);
                }
            }
        });

    }


    @Override
    public void onClickedListItem(PlaceDocuments e)
    {
        if (e instanceof PlaceDocuments)
        {
            PlaceInfoFragment placeInfoFragment = new PlaceInfoFragment();
            Bundle bundle = new Bundle();
            bundle.putString("placeId", ((PlaceDocuments) e).getId());
            placeInfoFragment.setArguments(bundle);

            placeInfoFragment.show(getChildFragmentManager(), PlaceInfoFragment.TAG);
        } else
        {

        }
    }

    @Override
    public void deleteListItem(PlaceDocuments e, int position)
    {

    }
}