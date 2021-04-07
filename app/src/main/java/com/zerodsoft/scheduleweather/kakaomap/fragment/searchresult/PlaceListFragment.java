package com.zerodsoft.scheduleweather.kakaomap.fragment.searchresult;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentLocationSearchResultBinding;
import com.zerodsoft.scheduleweather.kakaomap.bottomsheet.adapter.PlaceItemInMapViewAdapter;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.OnClickedLocListItem;
import com.zerodsoft.scheduleweather.kakaomap.fragment.searchresult.interfaces.IViewPager;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapData;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.kakaomap.fragment.searchresult.adapter.PlacesAdapter;
import com.zerodsoft.scheduleweather.kakaomap.util.RequestLocationTimer;
import com.zerodsoft.scheduleweather.kakaomap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.kakaomap.viewmodel.PlacesViewModel;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.utility.NetworkStatus;

import java.util.Timer;

public class PlaceListFragment extends Fragment implements IViewPager
{
    private static int currSearchMapPointCriteria = LocalApiPlaceParameter.SEARCH_CRITERIA_MAP_POINT_MAP_CENTER;
    private static int currSearchSortTypeCriteria = LocalApiPlaceParameter.SEARCH_CRITERIA_SORT_TYPE_ACCURACY;

    private FragmentLocationSearchResultBinding binding;

    private PlacesViewModel viewModel;
    private PlacesAdapter adapter;

    private LocationManager locationManager;
    private ArrayAdapter<CharSequence> spinnerAdapter;

    private NetworkStatus networkStatus;
    private Timer timer;

    private double mapLatitude;
    private double mapLongitude;

    private final IMapPoint iMapPoint;
    private final IMapData iMapData;
    private final OnClickedLocListItem onClickedLocListItem;
    private final String SEARCH_WORD;

    public PlaceListFragment(IMapPoint iMapPoint, String searchWord, IMapData iMapData, OnClickedLocListItem onClickedLocListItem)
    {
        this.iMapPoint = iMapPoint;
        this.SEARCH_WORD = searchWord;
        this.iMapData = iMapData;
        this.onClickedLocListItem = onClickedLocListItem;
    }

    private final LocationListener locationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            timer.cancel();
            locationManager.removeUpdates(locationListener);
            mapLongitude = location.getLongitude();
            mapLatitude = location.getLatitude();
            requestPlacesNow(SEARCH_WORD);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle)
        {

        }

        @Override
        public void onProviderEnabled(String s)
        {

        }

        @Override
        public void onProviderDisabled(String s)
        {

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = FragmentLocationSearchResultBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        binding.searchResultType.setText(getString(R.string.result_place));

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.map_search_result_sort_spinner, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.searchSortSpinner.setAdapter(spinnerAdapter);
        binding.searchSortSpinner.setSelection(currSearchSortTypeCriteria == LocalApiPlaceParameter.SEARCH_CRITERIA_SORT_TYPE_ACCURACY ? 1 : 0, false);
        binding.searchSortSpinner.setOnItemSelectedListener(onItemSelectedListener);

        binding.searchResultRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.searchResultRecyclerview.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        viewModel = new ViewModelProvider(this).get(PlacesViewModel.class);

        replaceButtonStyle();

        binding.searchAroundMapCenter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                currSearchMapPointCriteria = LocalApiPlaceParameter.SEARCH_CRITERIA_MAP_POINT_MAP_CENTER;
                replaceButtonStyle();
                requestPlaces(SEARCH_WORD);
            }
        });

        binding.searchAroundCurrentLocation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                currSearchMapPointCriteria = LocalApiPlaceParameter.SEARCH_CRITERIA_MAP_POINT_CURRENT_LOCATION;
                replaceButtonStyle();
                requestPlaces(SEARCH_WORD);
            }
        });


        requestPlaces(SEARCH_WORD);
    }


    private void replaceButtonStyle()
    {
        switch (currSearchMapPointCriteria)
        {
            case LocalApiPlaceParameter.SEARCH_CRITERIA_MAP_POINT_CURRENT_LOCATION:
                binding.searchAroundCurrentLocation.setTextColor(getResources().getColor(R.color.gray_700, null));
                binding.searchAroundMapCenter.setTextColor(getResources().getColor(R.color.gray_500, null));
                break;

            case LocalApiPlaceParameter.SEARCH_CRITERIA_MAP_POINT_MAP_CENTER:
                binding.searchAroundCurrentLocation.setTextColor(getResources().getColor(R.color.gray_500, null));
                binding.searchAroundMapCenter.setTextColor(getResources().getColor(R.color.gray_700, null));
                break;
        }
    }

    private final AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l)
        {
            switch (index)
            {
                case 0:
                    //거리 순서
                    currSearchSortTypeCriteria = LocalApiPlaceParameter.SEARCH_CRITERIA_SORT_TYPE_DISTANCE;
                    break;
                case 1:
                    //정확도 순서
                    currSearchSortTypeCriteria = LocalApiPlaceParameter.SEARCH_CRITERIA_SORT_TYPE_ACCURACY;
                    break;
            }
            requestPlaces(SEARCH_WORD);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView)
        {

        }
    };

    private void requestPlaces(String searchWord)
    {
        if (currSearchMapPointCriteria == LocalApiPlaceParameter.SEARCH_CRITERIA_MAP_POINT_CURRENT_LOCATION)
        {
            boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (isGpsEnabled && isNetworkEnabled)
            {
                int fineLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
                int coarseLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                timer = new Timer();
                timer.schedule(new RequestLocationTimer()
                {
                    @Override
                    public void run()
                    {
                        timer.cancel();
                        getActivity().runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                locationManager.removeUpdates(locationListener);
                                int fineLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
                                int coarseLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
                                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                            }
                        });

                    }
                }, 2000);
            } else
            {
                //gps켜달라고 요청하기
                showRequestDialog();
            }
        } else
        {
            currSearchMapPointCriteria = LocalApiPlaceParameter.SEARCH_CRITERIA_MAP_POINT_MAP_CENTER;
            requestPlacesNow(SEARCH_WORD);
        }

    }

    private void requestPlacesNow(String searchWord)
    {
        LocalApiPlaceParameter parameter = LocalParameterUtil.getPlaceParameter(searchWord, String.valueOf(getLatitude())
                , String.valueOf(getLongitude()), LocalApiPlaceParameter.DEFAULT_SIZE, LocalApiPlaceParameter.DEFAULT_PAGE, currSearchSortTypeCriteria);

        adapter = new PlacesAdapter(getContext(), iMapData, onClickedLocListItem);
        adapter.registerAdapterDataObserver(adapterDataObserver);

        binding.searchResultRecyclerview.removeAllViews();
        binding.searchResultRecyclerview.setAdapter(adapter);

        viewModel.init(parameter);
        viewModel.getPagedListMutableLiveData().observe(getViewLifecycleOwner(), observer);
    }

    private final Observer<PagedList<PlaceDocuments>> observer = new Observer<PagedList<PlaceDocuments>>()
    {
        @Override
        public void onChanged(PagedList<PlaceDocuments> placeDocuments)
        {
            adapter.submitList(placeDocuments);
            binding.progressBar.setVisibility(View.GONE);
        }
    };

    private void showRequestDialog()
    {
        new AlertDialog.Builder(getActivity())
                .setMessage(getString(R.string.request_to_make_gps_on))
                .setPositiveButton(getString(R.string.check), new
                        DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt)
                            {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        currSearchMapPointCriteria = LocalApiPlaceParameter.SEARCH_CRITERIA_MAP_POINT_MAP_CENTER;
                        replaceButtonStyle();
                        requestPlacesNow(SEARCH_WORD);
                    }
                })
                .setCancelable(false)
                .show();
    }

    private double getLatitude()
    {
        return currSearchMapPointCriteria == LocalApiPlaceParameter.SEARCH_CRITERIA_MAP_POINT_CURRENT_LOCATION ? mapLatitude : iMapPoint.getLatitude();
    }

    private double getLongitude()
    {
        return currSearchMapPointCriteria == LocalApiPlaceParameter.SEARCH_CRITERIA_MAP_POINT_CURRENT_LOCATION ? mapLongitude : iMapPoint.getLongitude();
    }

    @Override
    public void onChangedPage()
    {
        int poiItemSize = iMapData.getPoiItemSize();
        iMapData.setPlacesListAdapter(new PlaceItemInMapViewAdapter());

        if (poiItemSize > 0 && adapter != null)
        {
            if (adapter.getItemCount() > 0)
            {
                iMapData.removeAllPoiItems();
                iMapData.createPlacesPoiItems(adapter.getCurrentList().snapshot());
            }
        }
    }

    private final RecyclerView.AdapterDataObserver adapterDataObserver = new RecyclerView.AdapterDataObserver()
    {

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount)
        {
            super.onItemRangeInserted(positionStart, itemCount);
            iMapData.createPlacesPoiItems(adapter.getCurrentList().snapshot());
        }

    };
}
