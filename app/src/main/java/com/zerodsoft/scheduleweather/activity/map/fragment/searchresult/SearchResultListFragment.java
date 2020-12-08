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
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.interfaces.IndicatorCreater;
import com.zerodsoft.scheduleweather.etc.ViewPagerIndicator;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;

public class SearchResultListFragment extends Fragment implements IndicatorCreater
{
    public static final String TAG = "SearchResultFragment";
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
    private LocalApiPlaceParameter parameter;

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
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        searchAroundMapCenter = (Button) view.findViewById(R.id.search_around_map_center);
        searchAroundCurrentLocation = (Button) view.findViewById(R.id.search_around_current_location);
        viewPagerIndicator = (ViewPagerIndicator) view.findViewById(R.id.map_result_view_pager_indicator);
        sortSpinner = (Spinner) view.findViewById(R.id.search_sort_spinner);

        searchResultListAdapter = new SearchResultListAdapter(this, searchData);
        onPageCallback = new OnPageCallback();

        spinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.map_search_result_sort_spinner, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(spinnerAdapter);

        fragmentsViewPager.setAdapter(searchResultListAdapter);
        fragmentsViewPager.registerOnPageChangeCallback(onPageCallback);

        searchAroundMapCenter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });

        searchAroundCurrentLocation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
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
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView)
        {

        }
    };

    class OnPageCallback extends ViewPager2.OnPageChangeCallback
    {
        public int finalPosition;

        @Override
        public void onPageSelected(int position)
        {
            super.onPageSelected(position);
            finalPosition = position;
            viewPagerIndicator.selectDot(position);
        }
    }
}