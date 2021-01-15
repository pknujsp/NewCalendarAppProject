package com.zerodsoft.scheduleweather.activity.map.fragment.searchresult;

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
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapData;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.adapter.PlacesAdapter;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.interfaces.FragmentRemover;
import com.zerodsoft.scheduleweather.activity.map.util.RequestLocationTimer;
import com.zerodsoft.scheduleweather.kakaomap.viewmodel.PlacesViewModel;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;

import java.util.Timer;

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
    private ProgressBar progressBar;
    private LocationManager locationManager;
    private Spinner sortSpinner;
    private ArrayAdapter<CharSequence> spinnerAdapter;

    private ConnectivityManager.NetworkCallback networkCallback;
    private ConnectivityManager connectivityManager;
    private Timer timer;

    private double mapLatitude;
    private double mapLongitude;
    private IMapData iMapData;
    private final String SEARCH_WORD;

    public PlaceListFragment(IMapPoint iMapPoint, FragmentRemover fragmentRemover, String searchWord, IMapData iMapData)
    {
        this.iMapPoint = iMapPoint;
        this.fragmentRemover = fragmentRemover;
        this.SEARCH_WORD = searchWord;
        this.iMapData = iMapData;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setNetworkCallback();
    }

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

        progressBar = (ProgressBar) view.findViewById(R.id.map_request_progress_bar);

        searchAroundMapCenterButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                enabledViews(false);
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
                enabledViews(false);
                currSearchMapPointCriteria = SEARCH_CRITERIA_MAP_POINT_CURRENT_LOCATION;
                replaceButtonStyle();
                requestPlaces();
            }
        });

        itemRecyclerView = (RecyclerView) view.findViewById(R.id.map_search_result_recyclerview);
        itemRecyclerView.setLayoutManager(new CustomGridLayoutManager(view.getContext(), RecyclerView.VERTICAL, false));
        itemRecyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
        viewModel = new ViewModelProvider(this).get(PlacesViewModel.class);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        enabledViews(false);
        requestPlaces();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        connectivityManager.unregisterNetworkCallback(networkCallback);
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
            enabledViews(false);
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


    private void setNetworkCallback()
    {
        connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkCallback = new ConnectivityManager.NetworkCallback()
        {
            @Override
            public void onAvailable(Network network)
            {
                super.onAvailable(network);
                Toast.makeText(getActivity(), "재 연결됨", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLost(Network network)
            {
                super.onLost(network);
                Toast.makeText(getActivity(), "연결 끊김", Toast.LENGTH_SHORT).show();
            }
        };
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
        builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);
        connectivityManager.registerNetworkCallback(builder.build(), networkCallback);
    }

    private boolean checkNetwork()
    {
        if (connectivityManager.getActiveNetwork() == null)
        {
            return false;
        } else
        {
            NetworkCapabilities nc = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());

            if (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
            {
                return true;
            } else
            {
                return false;
            }
        }
    }

    private void requestPlaces()
    {
        if (checkNetwork())
        {
            if (currSearchMapPointCriteria == SEARCH_CRITERIA_MAP_POINT_CURRENT_LOCATION)
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
                    enabledViews(true);
                    showRequestDialog();
                }
            } else
            {
                currSearchMapPointCriteria = SEARCH_CRITERIA_MAP_POINT_MAP_CENTER;
                requestPlacesNow();
            }
        } else
        {
            Toast.makeText(getActivity(), getString(R.string.map_network_not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    private void requestPlacesNow()
    {
        setParameter();
        adapter = new PlacesAdapter(getContext(), iMapData);
        adapter.registerAdapterDataObserver(adapterDataObserver);
        itemRecyclerView.removeAllViews();
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
            enabledViews(true);
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
                        replaceButtonStyle();
                        enabledViews(false);
                        requestPlacesNow();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void setParameter()
    {
        parameter.clear();
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

    private void enabledViews(boolean state)
    {
        searchAroundCurrentLocationButton.setClickable(state);
        searchAroundMapCenterButton.setClickable(state);
        sortSpinner.setClickable(state);
        sortSpinner.setEnabled(state);
        progressBar.setVisibility(state ? View.GONE : View.VISIBLE);
        ((CustomGridLayoutManager) itemRecyclerView.getLayoutManager()).setScrollEnabled(state);
        itemRecyclerView.setClickable(state);
    }

    private double getLatitude()
    {
        return currSearchMapPointCriteria == SEARCH_CRITERIA_MAP_POINT_CURRENT_LOCATION ? mapLatitude : iMapPoint.getLatitude();
    }

    private double getLongitude()
    {
        return currSearchMapPointCriteria == SEARCH_CRITERIA_MAP_POINT_CURRENT_LOCATION ? mapLongitude : iMapPoint.getLongitude();
    }

    class CustomGridLayoutManager extends LinearLayoutManager
    {
        private boolean isScrollEnabled = true;


        public CustomGridLayoutManager(Context context, int orientation, boolean reverseLayout)
        {
            super(context, orientation, reverseLayout);
        }

        public void setScrollEnabled(boolean flag)
        {
            this.isScrollEnabled = flag;
        }

        @Override
        public boolean canScrollVertically()
        {
            return isScrollEnabled && super.canScrollVertically();
        }
    }

    private final RecyclerView.AdapterDataObserver adapterDataObserver = new RecyclerView.AdapterDataObserver()
    {
        @Override
        public void onChanged()
        {
            super.onChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount)
        {
            super.onItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload)
        {
            super.onItemRangeChanged(positionStart, itemCount, payload);
            Log.e(getClass().getName(), "onItemRangeChanged");
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount)
        {
            super.onItemRangeInserted(positionStart, itemCount);
            Log.e(getClass().getName(), "onItemRangeInserted");
            iMapData.createPlacesPoiItems(adapter.getCurrentList().snapshot());
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount)
        {
            super.onItemRangeRemoved(positionStart, itemCount);
            Log.e(getClass().getName(), "onItemRangeRemoved");
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount)
        {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            Log.e(getClass().getName(), "onItemRangeMoved");
        }
    };
}
