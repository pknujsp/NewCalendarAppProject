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

import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.common.interfaces.OnProgressBarListener;
import com.zerodsoft.scheduleweather.databinding.FragmentRestaurantListBinding;
import com.zerodsoft.scheduleweather.event.foods.adapter.RestaurantListAdapter;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnClickedFavoriteButtonListener;
import com.zerodsoft.scheduleweather.event.foods.share.CriteriaLocationRepository;
import com.zerodsoft.scheduleweather.event.foods.share.FavoriteRestaurantCloud;
import com.zerodsoft.scheduleweather.navermap.place.PlaceInfoFragment;
import com.zerodsoft.scheduleweather.navermap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.navermap.viewmodel.PlacesViewModel;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnExtraListDataListener;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.FavoriteRestaurantDTO;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.interfaces.FavoriteRestaurantQuery;

import java.util.List;

public class RestaurantListFragment extends Fragment implements OnClickedListItem<PlaceDocuments>, OnClickedFavoriteButtonListener
        , FoodCategoryTabFragment.RefreshFavoriteState
{
    protected FavoriteRestaurantQuery favoriteRestaurantQuery;
    protected FragmentRestaurantListBinding binding;
    protected String CATEGORY_NAME;
    protected PlacesViewModel placesViewModel;
    protected RestaurantListAdapter adapter;

    protected RecyclerView.AdapterDataObserver adapterDataObserver;

    public RestaurantListFragment(String CATEGORY_NAME)
    {
        this.CATEGORY_NAME = CATEGORY_NAME;
    }

    public RestaurantListFragment(FavoriteRestaurantQuery favoriteRestaurantQuery, String CATEGORY_NAME)
    {
        this.favoriteRestaurantQuery = favoriteRestaurantQuery;
        this.CATEGORY_NAME = CATEGORY_NAME;
    }

    public void setAdapterDataObserver(RecyclerView.AdapterDataObserver adapterDataObserver)
    {
        this.adapterDataObserver = adapterDataObserver;
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
        binding = FragmentRestaurantListBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        placesViewModel = new ViewModelProvider(this).get(PlacesViewModel.class);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false));
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));

        adapter = new RestaurantListAdapter(getContext(), RestaurantListFragment.this, RestaurantListFragment.this);
        binding.recyclerView.setAdapter(adapter);

        requestRestaurantList(CATEGORY_NAME);
        binding.errorText.setVisibility(View.GONE);
    }


    protected void requestRestaurantList(String categoryName)
    {
        LocationDTO criteriaLocation = CriteriaLocationRepository.getRestaurantCriteriaLocation();

        final LocalApiPlaceParameter placeParameter = LocalParameterUtil.getPlaceParameter(categoryName, String.valueOf(criteriaLocation.getLatitude()),
                String.valueOf(criteriaLocation.getLongitude()), LocalApiPlaceParameter.DEFAULT_SIZE, LocalApiPlaceParameter.DEFAULT_PAGE,
                LocalApiPlaceParameter.SEARCH_CRITERIA_SORT_TYPE_ACCURACY);
        placeParameter.setRadius(App.getPreference_key_radius_range());

        placesViewModel.init(placeParameter, new OnProgressBarListener()
        {
            @Override
            public void setProgressBarVisibility(int visibility)
            {

            }
        });

        placesViewModel.getPagedListMutableLiveData().observe(getViewLifecycleOwner(), new Observer<PagedList<PlaceDocuments>>()
        {
            boolean isFirst = true;

            @Override
            public void onChanged(PagedList<PlaceDocuments> placeDocuments)
            {
                adapter.submitList(placeDocuments);

                if (isFirst)
                {
                    isFirst = false;
                    adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver()
                    {
                        @Override
                        public void onItemRangeInserted(int positionStart, int itemCount)
                        {
                            super.onItemRangeInserted(positionStart, itemCount);
                            binding.errorText.setVisibility(View.GONE);

                            if (adapterDataObserver != null)
                            {
                                adapterDataObserver.onItemRangeInserted(0, itemCount);
                            }
                        }
                    });
                } else
                {
                    if (adapter.getCurrentList().snapshot().isEmpty())
                    {
                        binding.errorText.setVisibility(View.VISIBLE);
                    }
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

    @Override
    public void onHiddenChanged(boolean hidden)
    {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onClickedFavoriteButton(String restaurantId, int groupPosition, int childPosition)
    {

    }

    @Override
    public void onClickedFavoriteButton(PlaceDocuments placeDocuments, int position)
    {
        if (FavoriteRestaurantCloud.getInstance().contains(placeDocuments.getId()))
        {
            favoriteRestaurantQuery.delete(placeDocuments.getId(), new CarrierMessagingService.ResultCallback<Boolean>()
            {
                @Override
                public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException
                {
                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            adapter.notifyItemChanged(position);
                        }
                    });

                }
            });
        } else
        {
            String id = placeDocuments.getId();
            String name = placeDocuments.getPlaceName();
            String latitude = String.valueOf(placeDocuments.getY());
            String longitude = String.valueOf(placeDocuments.getX());

            favoriteRestaurantQuery.insert(id, name, latitude, longitude, new CarrierMessagingService.ResultCallback<FavoriteRestaurantDTO>()
            {
                @Override
                public void onReceiveResult(@NonNull FavoriteRestaurantDTO favoriteRestaurantDTO) throws RemoteException
                {
                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            adapter.notifyItemChanged(position);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void refreshFavorites()
    {
        if (adapter != null)
        {
            adapter.notifyDataSetChanged();
        }
    }


}