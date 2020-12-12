package com.zerodsoft.scheduleweather.activity.map.fragment.searchresult;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.zerodsoft.scheduleweather.activity.map.fragment.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.adapter.PlacesAdapter;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.interfaces.FragmentRemover;
import com.zerodsoft.scheduleweather.kakaomap.viewmodel.PlacesViewModel;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;

import java.util.Timer;
import java.util.TimerTask;

public class PlaceListFragment extends Fragment
{
    private LocalApiPlaceParameter parameter = new LocalApiPlaceParameter();

    public static final int SEARCH_CRITERIA_MAP_POINT_MAP_CENTER = 0;
    public static final int SEARCH_CRITERIA_MAP_POINT_CURRENT_LOCATION = 1;
    public static final int SEARCH_CRITERIA_SORT_TYPE_DISTANCE = 2;
    public static final int SEARCH_CRITERIA_SORT_TYPE_ACCURACY = 3;

    private static int currSearchMapPointCriteria = SEARCH_CRITERIA_MAP_POINT_CURRENT_LOCATION;
    private static int currSearchSortTypeCriteria = SEARCH_CRITERIA_SORT_TYPE_ACCURACY;

    private RecyclerView itemRecyclerView;
    private PlacesViewModel viewModel;
    private PlacesAdapter adapter;
    private FragmentRemover fragmentRemover;
    private IMapPoint iMapPoint;

    private Button searchAroundMapCenterButton;
    private Button searchAroundCurrentLocationButton;
    private LocationManager locationManager;
    private Spinner sortSpinner;
    private ArrayAdapter<CharSequence> spinnerAdapter;

    private double mapLatitude;
    private double mapLongitude;

    private final String SEARCH_WORD;

    public PlaceListFragment(IMapPoint iMapPoint, FragmentRemover fragmentRemover, String searchWord)
    {
        this.iMapPoint = iMapPoint;
        this.fragmentRemover = fragmentRemover;
        this.SEARCH_WORD = searchWord;
    }

    private final LocationListener locationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            locationManager.removeUpdates(locationListener);
            mapLongitude = location.getLongitude();
            mapLatitude = location.getLatitude();
            requestPlacesNow();
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
        ((TextView) view.findViewById(R.id.map_search_result_type)).setText(getString(R.string.result_place));
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        searchAroundMapCenterButton = (Button) view.findViewById(R.id.search_around_map_center);
        searchAroundCurrentLocationButton = (Button) view.findViewById(R.id.search_around_current_location);
        sortSpinner = (Spinner) view.findViewById(R.id.search_sort_spinner);

        spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.map_search_result_sort_spinner, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(spinnerAdapter);
        sortSpinner.setSelection(currSearchSortTypeCriteria == SEARCH_CRITERIA_SORT_TYPE_ACCURACY ? 1 : 0, false);
        sortSpinner.setOnItemSelectedListener(onItemSelectedListener);
        replaceButtonStyle();

        searchAroundMapCenterButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                clickableCriteriaSelector(false);
                currSearchMapPointCriteria = SEARCH_CRITERIA_MAP_POINT_MAP_CENTER;
                replaceButtonStyle();
                requestPlaces();
            }
        });

        searchAroundCurrentLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                clickableCriteriaSelector(false);
                currSearchMapPointCriteria = SEARCH_CRITERIA_MAP_POINT_CURRENT_LOCATION;
                replaceButtonStyle();
                requestPlaces();
            }
        });

        itemRecyclerView = (RecyclerView) view.findViewById(R.id.map_search_result_recyclerview);
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false));
        itemRecyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
        viewModel = new ViewModelProvider(this).get(PlacesViewModel.class);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        requestPlaces();
    }

    private void replaceButtonStyle()
    {
        switch (currSearchMapPointCriteria)
        {
            case SEARCH_CRITERIA_MAP_POINT_CURRENT_LOCATION:
                searchAroundCurrentLocationButton.setTextColor(getResources().getColor(R.color.gray_700, null));
                searchAroundMapCenterButton.setTextColor(getResources().getColor(R.color.gray_500, null));
                break;
            case SEARCH_CRITERIA_MAP_POINT_MAP_CENTER:
                searchAroundCurrentLocationButton.setTextColor(getResources().getColor(R.color.gray_500, null));
                searchAroundMapCenterButton.setTextColor(getResources().getColor(R.color.gray_700, null));
                break;
        }
    }

    private final AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l)
        {
            clickableCriteriaSelector(false);
            switch (index)
            {
                case 0:
                    //거리 순서
                    currSearchSortTypeCriteria = SEARCH_CRITERIA_SORT_TYPE_DISTANCE;
                    break;
                case 1:
                    //정확도 순서
                    currSearchSortTypeCriteria = SEARCH_CRITERIA_SORT_TYPE_ACCURACY;
                    break;
            }
            requestPlaces();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView)
        {

        }
    };

    private void requestPlaces()
    {
        if (currSearchMapPointCriteria == SEARCH_CRITERIA_MAP_POINT_CURRENT_LOCATION)
        {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            {
                int fineLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
                int coarseLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);

                //  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            } else
            {
                //gps켜달라고 요청하기
                showRequestDialog();
                clickableCriteriaSelector(true);
            }
        } else
        {
            currSearchMapPointCriteria = SEARCH_CRITERIA_MAP_POINT_MAP_CENTER;
            requestPlacesNow();
        }
    }

    private void requestPlacesNow()
    {
        setParameter();
        adapter = new PlacesAdapter(getContext());
        itemRecyclerView.setAdapter(adapter);
        viewModel.init(parameter);
        viewModel.getPagedListMutableLiveData().observe(getViewLifecycleOwner(), observer);
    }

    private final Observer<PagedList<PlaceDocuments>> observer = new Observer<PagedList<PlaceDocuments>>()
    {
        @Override
        public void onChanged(PagedList<PlaceDocuments> placeDocuments)
        {
            adapter.submitList(placeDocuments);
            clickableCriteriaSelector(true);
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
                        currSearchMapPointCriteria = SEARCH_CRITERIA_MAP_POINT_MAP_CENTER;
                        requestPlacesNow();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void setParameter()
    {
        parameter.setY(Double.toString(getLatitude())).setX(Double.toString(getLongitude()))
                .setSize(LocalApiPlaceParameter.DEFAULT_SIZE).setPage(LocalApiPlaceParameter.DEFAULT_PAGE);

        switch (currSearchSortTypeCriteria)
        {
            case SEARCH_CRITERIA_SORT_TYPE_ACCURACY:
                parameter.setSort(LocalApiPlaceParameter.SORT_ACCURACY);
                break;
            case SEARCH_CRITERIA_SORT_TYPE_DISTANCE:
                parameter.setSort(LocalApiPlaceParameter.SORT_DISTANCE);
                break;
        }

        if (KakaoLocalApiCategoryUtil.isCategory(SEARCH_WORD))
        {
            parameter.setCategoryGroupCode(KakaoLocalApiCategoryUtil.getName(Integer.parseInt(SEARCH_WORD)));
        } else
        {
            parameter.setQuery(SEARCH_WORD);
        }
    }

    private void clickableCriteriaSelector(boolean state)
    {
        searchAroundCurrentLocationButton.setClickable(state);
        searchAroundMapCenterButton.setClickable(state);
        sortSpinner.setClickable(state);
    }

    private double getLatitude()
    {
        return currSearchMapPointCriteria == SEARCH_CRITERIA_MAP_POINT_CURRENT_LOCATION ? mapLatitude : iMapPoint.getLatitude();
    }

    private double getLongitude()
    {
        return currSearchMapPointCriteria == SEARCH_CRITERIA_MAP_POINT_CURRENT_LOCATION ? mapLongitude : iMapPoint.getLongitude();
    }
}
