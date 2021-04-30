package com.zerodsoft.scheduleweather.navermap.fragment.search;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.common.interfaces.OnBackPressedCallbackController;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.common.interfaces.SearchHistoryDataController;
import com.zerodsoft.scheduleweather.event.places.interfaces.PoiItemOnClickListener;
import com.zerodsoft.scheduleweather.navermap.fragment.search.adapter.SearchLocationHistoryAdapter;
import com.zerodsoft.scheduleweather.navermap.fragment.searchresult.LocationSearchResultFragment;
import com.zerodsoft.scheduleweather.etc.FragmentStateCallback;
import com.zerodsoft.scheduleweather.navermap.interfaces.BottomSheetController;
import com.zerodsoft.scheduleweather.navermap.interfaces.PlacesListBottomSheetController;
import com.zerodsoft.scheduleweather.navermap.interfaces.SearchBarController;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapData;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnSelectedMapCategory;
import com.zerodsoft.scheduleweather.navermap.fragment.search.adapter.PlaceCategoriesAdapter;
import com.zerodsoft.scheduleweather.databinding.FragmentSearchBinding;
import com.zerodsoft.scheduleweather.navermap.interfaces.SearchFragmentController;
import com.zerodsoft.scheduleweather.navermap.viewmodel.SearchHistoryViewModel;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;
import com.zerodsoft.scheduleweather.room.dto.SearchHistoryDTO;

import java.util.List;

public class LocationSearchFragment extends Fragment implements OnSelectedMapCategory, OnClickedListItem<SearchHistoryDTO>, SearchHistoryDataController<SearchHistoryDTO>,
        OnBackPressedCallbackController
{
    public static final String TAG = "LocationSearchFragment";
    private FragmentSearchBinding binding;

    private PlaceCategoriesAdapter categoriesAdapter;
    private SearchLocationHistoryAdapter searchLocationHistoryAdapter;
    private SearchHistoryViewModel searchHistoryViewModel;

    private IMapPoint iMapPoint;
    private IMapData iMapData;
    private FragmentStateCallback fragmentStateCallback;
    private PlacesListBottomSheetController placesListBottomSheetController;
    private PoiItemOnClickListener poiItemOnClickListener;
    private SearchBarController searchBarController;
    private SearchFragmentController searchFragmentController;

    private final OnBackPressedCallbackController mainFragmentOnBackPressedCallbackController;
    private final BottomSheetController bottomSheetController;

    public OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true)
    {
        @Override
        public void handleOnBackPressed()
        {
            getParentFragmentManager().popBackStack();
            searchFragmentController.closeSearchFragments(LocationSearchFragment.TAG);
        }
    };

    @Override
    public void onHiddenChanged(boolean hidden)
    {
        super.onHiddenChanged(hidden);
        if (hidden)
        {
            removeOnBackPressedCallback();
            if (getParentFragmentManager().findFragmentByTag(LocationSearchResultFragment.TAG) == null)
            {
                mainFragmentOnBackPressedCallbackController.addOnBackPressedCallback();
                bottomSheetController.setStateOfBottomSheet(TAG, BottomSheetBehavior.STATE_COLLAPSED);
            }
        } else
        {
            addOnBackPressedCallback();
            if (getParentFragmentManager().findFragmentByTag(LocationSearchResultFragment.TAG) == null)
            {
                mainFragmentOnBackPressedCallbackController.removeOnBackPressedCallback();
                bottomSheetController.setStateOfBottomSheet(TAG, BottomSheetBehavior.STATE_EXPANDED);
            }
        }
    }

    public LocationSearchFragment(Fragment mapFragment, OnBackPressedCallbackController onBackPressedCallbackController
            , BottomSheetController bottomSheetController
            , FragmentStateCallback fragmentStateCallback)
    {
        this.iMapPoint = (IMapPoint) mapFragment;
        this.mainFragmentOnBackPressedCallbackController = onBackPressedCallbackController;
        this.bottomSheetController = bottomSheetController;
        this.iMapData = (IMapData) mapFragment;
        this.placesListBottomSheetController = (PlacesListBottomSheetController) mapFragment;
        this.poiItemOnClickListener = (PoiItemOnClickListener) mapFragment;
        this.fragmentStateCallback = fragmentStateCallback;
        this.searchFragmentController = (SearchFragmentController) mapFragment;
    }

    public void setSearchBarController(SearchBarController searchBarController)
    {
        this.searchBarController = searchBarController;
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
        binding = FragmentSearchBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        binding.searchHistoryRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.searchHistoryRecyclerview.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        searchHistoryViewModel = new ViewModelProvider(this).get(SearchHistoryViewModel.class);
        searchHistoryViewModel.select(SearchHistoryDTO.LOCATION_SEARCH, new CarrierMessagingService.ResultCallback<List<SearchHistoryDTO>>()
        {
            @Override
            public void onReceiveResult(@NonNull List<SearchHistoryDTO> result) throws RemoteException
            {
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        searchLocationHistoryAdapter = new SearchLocationHistoryAdapter(LocationSearchFragment.this);
                        searchLocationHistoryAdapter.setHistoryList(result);

                        binding.searchHistoryRecyclerview.setAdapter(searchLocationHistoryAdapter);
                    }
                });
            }
        });

        categoriesAdapter = new PlaceCategoriesAdapter(this);
        binding.categoriesRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        binding.categoriesRecyclerview.setAdapter(categoriesAdapter);
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onSelectedMapCategory(PlaceCategoryDTO category)
    {
        searchBarController.setQuery(category.getCode(), true);
        searchBarController.changeViewTypeImg(SearchBarController.MAP);
    }


    @Override
    public void updateSearchHistoryList()
    {
        searchHistoryViewModel.select(SearchHistoryDTO.LOCATION_SEARCH, new CarrierMessagingService.ResultCallback<List<SearchHistoryDTO>>()
        {
            @Override
            public void onReceiveResult(@NonNull List<SearchHistoryDTO> result) throws RemoteException
            {
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //최신 리스트의 크기와 어댑터내 리스트의 크기가 다르면 갱신
                        if (searchLocationHistoryAdapter.getItemCount() != result.size())
                        {
                            searchLocationHistoryAdapter.setHistoryList(result);
                            searchLocationHistoryAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        });
    }


    @Override
    public void onClickedListItem(SearchHistoryDTO e)
    {
        searchBarController.setQuery(e.getValue(), true);
    }

    @Override
    public void deleteListItem(SearchHistoryDTO e, int position)
    {
        searchHistoryViewModel.delete(e.getId(), new CarrierMessagingService.ResultCallback<Boolean>()
        {
            @Override
            public void onReceiveResult(@NonNull Boolean result) throws RemoteException
            {
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        searchLocationHistoryAdapter.getHistoryList().remove(position);
                        searchLocationHistoryAdapter.notifyItemRemoved(position);
                    }
                });

            }
        });

    }

    @Override
    public void insertValueToHistory(String value)
    {
        searchHistoryViewModel.insert(SearchHistoryDTO.LOCATION_SEARCH, value, new CarrierMessagingService.ResultCallback<SearchHistoryDTO>()
        {
            @Override
            public void onReceiveResult(@NonNull SearchHistoryDTO result) throws RemoteException
            {
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //list 갱신
                        searchLocationHistoryAdapter.getHistoryList().add(result);
                        searchLocationHistoryAdapter.notifyItemInserted(searchLocationHistoryAdapter.getItemCount() - 1);
                    }
                });
            }
        });
    }

    @Override
    public void addOnBackPressedCallback()
    {
        getActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    @Override
    public void removeOnBackPressedCallback()
    {
        onBackPressedCallback.remove();
    }
}