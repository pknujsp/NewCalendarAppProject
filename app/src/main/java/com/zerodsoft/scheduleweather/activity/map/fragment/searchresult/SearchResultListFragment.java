package com.zerodsoft.scheduleweather.activity.map.fragment.searchresult;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.zerodsoft.scheduleweather.activity.map.fragment.dto.SearchData;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.adapter.SearchResultListAdapter;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.interfaces.IMapSearch;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.interfaces.IndicatorCreater;
import com.zerodsoft.scheduleweather.etc.ViewPagerIndicator;
import com.zerodsoft.scheduleweather.R;

public class SearchResultListFragment extends Fragment implements IndicatorCreater, IMapSearch
{
    public static final String TAG = "SearchResultFragment";
    private static SearchResultListFragment instance;

    public static final int SEARCH_CRITERIA_MAP_POINT_MAP_CENTER = 0;
    public static final int SEARCH_CRITERIA_MAP_POINT_CURRENT_LOCATION = 1;
    public static final int SEARCH_CRITERIA_SORT_TYPE_DISTANCE = 2;
    public static final int SEARCH_CRITERIA_SORT_TYPE_ACCURACY = 3;

    private static int searchMapPointCriteria = SEARCH_CRITERIA_MAP_POINT_CURRENT_LOCATION;
    private static int searchSortTypeCriteria = SEARCH_CRITERIA_SORT_TYPE_ACCURACY;

    private ViewPager2 fragmentsViewPager;
    private SearchResultListAdapter searchResultListAdapter;
    private Button searchAroundMapCenter;
    private Button searchAroundCurrentLocation;
    private LocationManager locationManager;
    private Spinner sortSpinner;
    private ArrayAdapter<CharSequence> spinnerAdapter;
    private ViewPagerIndicator viewPagerIndicator;
    private SearchData searchData;

    private OnPageCallback onPageCallback;

    private double mapLatitude;
    private double mapLongitude;


    @Override
    public void setIndicator(int fragmentSize)
    {
        viewPagerIndicator.createDot(0, fragmentSize);
    }


    private LocationListener locationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            mapLatitude = location.getLatitude();
            mapLongitude = location.getLongitude();
            // 자원해제
            locationManager.removeUpdates(locationListener);
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


    public SearchResultListFragment(SearchData searchData)
    {
        this.searchData = searchData;
    }

    public static SearchResultListFragment getInstance()
    {
        return instance;
    }

    public static SearchResultListFragment newInstance(SearchData searchData)
    {
        instance = new SearchResultListFragment(searchData);
        return instance;
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
        return inflater.inflate(R.layout.fragment_search_result_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        fragmentsViewPager = (ViewPager2) view.findViewById(R.id.map_search_result_viewpager);
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        searchAroundMapCenter = (Button) view.findViewById(R.id.search_around_map_center);
        searchAroundCurrentLocation = (Button) view.findViewById(R.id.search_around_current_location);
        viewPagerIndicator = (ViewPagerIndicator) view.findViewById(R.id.map_result_view_pager_indicator);
        sortSpinner = (Spinner) view.findViewById(R.id.search_sort_spinner);

        spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.map_search_result_sort_spinner, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(spinnerAdapter);

        searchResultListAdapter = new SearchResultListAdapter(this, searchData);
        onPageCallback = new OnPageCallback();

        fragmentsViewPager.setAdapter(searchResultListAdapter);
        fragmentsViewPager.registerOnPageChangeCallback(onPageCallback);
        viewPagerIndicator.createDot(0, 2);

        searchAroundMapCenter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                searchMapPointCriteria = SEARCH_CRITERIA_MAP_POINT_MAP_CENTER;
            }
        });

        searchAroundCurrentLocation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                searchMapPointCriteria = SEARCH_CRITERIA_MAP_POINT_CURRENT_LOCATION;

                boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                int fineLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
                int coarseLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);

                if (isGpsEnabled && isNetworkEnabled)
                {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                }
            }
        });
    }


    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener()
    {
        // 결과 프래그먼트에서 스피너로 선택 후 뒤로 간 다음 다시 검색하여 재 실행하면 오류발생
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l)
        {
            switch (index)
            {
                case 0:
                    //거리 순서
                    searchSortTypeCriteria = SEARCH_CRITERIA_SORT_TYPE_DISTANCE;
                    break;
                case 1:
                    //정확도 순서
                    searchSortTypeCriteria = SEARCH_CRITERIA_SORT_TYPE_ACCURACY;
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView)
        {

        }
    };

    @Override
    public int getMapPointCriteria()
    {
        return searchMapPointCriteria;
    }

    @Override
    public int getSortCriteria()
    {
        return searchSortTypeCriteria;
    }

    @Override
    public double getLatitude()
    {
        return mapLatitude;
        //인터페이스로 MapFragment에서 맵 센터 좌표를 가져온다
    }

    @Override
    public double getLongitude()
    {
        return mapLongitude;
    }

    class OnPageCallback extends ViewPager2.OnPageChangeCallback
    {
        public int lastPosition;

        @Override
        public void onPageSelected(int position)
        {
            super.onPageSelected(position);
            lastPosition = position;
            viewPagerIndicator.selectDot(position);
        }
    }

}