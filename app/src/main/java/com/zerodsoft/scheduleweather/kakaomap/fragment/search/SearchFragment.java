package com.zerodsoft.scheduleweather.kakaomap.fragment.search;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.event.places.interfaces.PoiItemOnClickListener;
import com.zerodsoft.scheduleweather.event.places.interfaces.SearchViewController;
import com.zerodsoft.scheduleweather.kakaomap.fragment.search.adapter.SearchLocationHistoryAdapter;
import com.zerodsoft.scheduleweather.kakaomap.fragment.searchresult.SearchResultListFragment;
import com.zerodsoft.scheduleweather.etc.FragmentStateCallback;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.PlacesListBottomSheetController;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.SearchBottomSheetController;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapData;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.OnSelectedMapCategory;
import com.zerodsoft.scheduleweather.kakaomap.fragment.search.adapter.PlaceCategoriesAdapter;
import com.zerodsoft.scheduleweather.databinding.FragmentSearchBinding;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapToolbar;
import com.zerodsoft.scheduleweather.kakaomap.viewmodel.SearchHistoryViewModel;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;
import com.zerodsoft.scheduleweather.room.dto.SearchHistoryDTO;

import java.util.List;

public class SearchFragment extends Fragment implements OnSelectedMapCategory, OnClickedListItem<SearchHistoryDTO>
{
    public static final String TAG = "SearchFragment";
    private static SearchFragment instance;

    private FragmentSearchBinding binding;
    private PlaceCategoriesAdapter categoriesAdapter;
    private SearchLocationHistoryAdapter searchLocationHistoryAdapter;
    private SearchHistoryViewModel searchHistoryViewModel;

    private final IMapPoint iMapPoint;
    private final IMapData iMapData;
    private final IMapToolbar iMapToolbar;
    private final SearchViewController searchViewController;
    private final SearchBottomSheetController searchBottomSheetController;
    private final FragmentStateCallback fragmentStateCallback;
    private final PlacesListBottomSheetController placesListBottomSheetController;
    private final PoiItemOnClickListener poiItemOnClickListener;

    private OnBackPressedCallback onBackPressedCallback;

    public SearchFragment(Fragment fragment, FragmentStateCallback fragmentStateCallback)
    {
        this.iMapPoint = (IMapPoint) fragment;
        this.iMapData = (IMapData) fragment;
        this.iMapToolbar = (IMapToolbar) fragment;
        this.searchViewController = (SearchViewController) fragment;
        this.searchBottomSheetController = (SearchBottomSheetController) fragment;
        this.placesListBottomSheetController = (PlacesListBottomSheetController) fragment;
        this.poiItemOnClickListener = (PoiItemOnClickListener) fragment;
        this.fragmentStateCallback = fragmentStateCallback;
    }

    public static SearchFragment getInstance()
    {
        return instance;
    }

    public static SearchFragment newInstance(Fragment fragment, FragmentStateCallback fragmentStateCallback)
    {
        instance = new SearchFragment(fragment, fragmentStateCallback);
        return instance;
    }

    public static void close()
    {
        instance = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        onBackPressedCallback = new OnBackPressedCallback(true)
        {
            @Override
            public void handleOnBackPressed()
            {
                getParentFragmentManager().beginTransaction().remove(instance).commitNow();
                searchViewController.closeSearchView(SearchBottomSheetController.SEARCH_VIEW);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(SearchFragment.this, onBackPressedCallback);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        onBackPressedCallback.remove();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
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
                        searchLocationHistoryAdapter = new SearchLocationHistoryAdapter(SearchFragment.this);
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


    public void search(String searchWord, boolean isClickedOnList)
    {
        if (!isClickedOnList)
        {
            searchHistoryViewModel.insert(SearchHistoryDTO.LOCATION_SEARCH, searchWord, new CarrierMessagingService.ResultCallback<SearchHistoryDTO>()
            {
                @Override
                public void onReceiveResult(@NonNull SearchHistoryDTO result) throws RemoteException
                {
                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            searchLocationHistoryAdapter.getHistoryList().add(result);
                            searchLocationHistoryAdapter.notifyItemInserted(searchLocationHistoryAdapter.getItemCount() - 1);
                        }
                    });
                }
            });
        }

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.search_bottom_sheet_fragment_container
                , SearchResultListFragment.newInstance(searchWord, iMapPoint, iMapData, iMapToolbar
                        , searchBottomSheetController, placesListBottomSheetController, poiItemOnClickListener)
                , SearchResultListFragment.TAG).hide(SearchFragment.this).addToBackStack(SearchResultListFragment.TAG).commit();
    }

    @Override
    public void onSelectedMapCategory(PlaceCategoryDTO category)
    {
        iMapToolbar.setText(category.getDescription());
        search(category.getCode(), true);
    }

    private void updateSearchHistoryList()
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
        iMapToolbar.setText(e.getValue());
        search(e.getValue(), true);
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
}