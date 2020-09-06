package com.zerodsoft.scheduleweather.activity.mapactivity.Fragment;

import android.Manifest;
import android.app.Activity;
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
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.zerodsoft.scheduleweather.activity.mapactivity.MapActivity;
import com.zerodsoft.scheduleweather.etc.ViewPagerIndicator;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.recyclerviewadapter.SearchResultViewPagerAdapter;
import com.zerodsoft.scheduleweather.retrofit.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.LocationSearchResult;

import net.daum.mf.map.api.MapPoint;

import java.util.ArrayList;
import java.util.List;

public class SearchResultFragment extends Fragment
{
    public static final String TAG = "SearchResult Fragment";
    private static SearchResultFragment instance;

    private ViewPager2 viewPager2;
    private SearchResultViewPagerAdapter searchResultViewPagerAdapter;
    private TextView rescanMapCenter;
    private TextView rescanMyLocCenter;
    private LocalApiPlaceParameter parameter;
    private LocationManager locationManager;
    private Spinner sortSpinner;

    private ViewPagerIndicator viewPagerIndicator;
    private int indicatorLength;

    private OnPageCallback onPageCallback;

    private MapController.OnDownloadListener onDownloadListener;


    public interface OnControlViewPagerAdapter
    {
        void setRecyclerViewCurrentPage(int page);
    }

    public static SearchResultFragment getInstance(Activity activity)
    {
        if (instance == null)
        {
            instance = new SearchResultFragment(activity);
        }
        return instance;
    }

    public SearchResultFragment(Activity activity)
    {
        onDownloadListener = (MapController.OnDownloadListener) activity;
    }

    public void setInitialData(Bundle bundle)
    {
        parameter = bundle.getParcelable("parameter");
    }

    public void setDownloadedData(LocalApiPlaceParameter parameter, LocationSearchResult locationSearchResult)
    {
        this.parameter = parameter;
        searchResultViewPagerAdapter.setData(parameter, locationSearchResult);
        searchResultViewPagerAdapter.notifyDataSetChanged();
    }

    public void setDownloadedExtraData(LocalApiPlaceParameter parameter, int type, LocationSearchResult locationSearchResult)
    {
        this.parameter = parameter;
        searchResultViewPagerAdapter.addExtraData(parameter, type, locationSearchResult);
        searchResultViewPagerAdapter.notifyDataSetChanged();
    }

    private LocationListener locationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            parameter.setX(location.getLongitude());
            parameter.setY(location.getLatitude());
            parameter.setPage("1");
            // 자원해제
            locationManager.removeUpdates(locationListener);
            onDownloadListener.requestData(parameter, TAG);
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


    public SearchResultFragment()
    {
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
        viewPager2 = (ViewPager2) view.findViewById(R.id.search_result_viewpager);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        rescanMapCenter = (TextView) view.findViewById(R.id.search_result_map_center_rescan);
        rescanMyLocCenter = (TextView) view.findViewById(R.id.search_result_myloc_center_rescan);
        viewPagerIndicator = (ViewPagerIndicator) view.findViewById(R.id.search_result_view_pager_indicator);
        sortSpinner = (Spinner) view.findViewById(R.id.search_sort_spinner);

        viewPagerIndicator.createDot(0, indicatorLength);

        searchResultViewPagerAdapter = new SearchResultViewPagerAdapter(getActivity());

        viewPager2.setAdapter(searchResultViewPagerAdapter);

        onPageCallback = new OnPageCallback();
        viewPager2.registerOnPageChangeCallback(onPageCallback);

        onDownloadListener.requestData(parameter, TAG);

        rescanMapCenter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                MapPoint.GeoCoordinate mapPoint = ((MapActivity) getActivity()).getMapCenterPoint();

                parameter.setX(mapPoint.longitude);
                parameter.setY(mapPoint.latitude);
                parameter.setPage("1");

                onDownloadListener.requestData(parameter, TAG);
            }
        });

        rescanMyLocCenter.setOnClickListener(new View.OnClickListener()
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


        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart()
    {
        setSortSpinner();
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    public int getCurrentListType()
    {
        // header fragment에서 change 버튼 클릭 시 리스트의 타입을 가져오기 위해 사용
        return searchResultViewPagerAdapter.getCurrentListType(onPageCallback.finalPosition);
    }

    private void setSortSpinner()
    {
        List<String> sortList = new ArrayList<>();
        sortList.add("정확도순");
        sortList.add("거리순");

        SpinnerAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, sortList);
        sortSpinner.setAdapter(adapter);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            boolean isInitializing = true;

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                if (isInitializing)
                {
                    isInitializing = false;
                    return;
                }

                switch (i)
                {
                    case 0:
                        parameter.setSort(LocalApiPlaceParameter.SORT_ACCURACY);
                        break;
                    case 1:
                        parameter.setSort(LocalApiPlaceParameter.SORT_DISTANCE);
                        break;
                }
                parameter.setPage("1");

                onDownloadListener.requestData(parameter, TAG);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });
    }


    class OnPageCallback extends ViewPager2.OnPageChangeCallback
    {
        public int finalPosition;

        @Override
        public void onPageSelected(int position)
        {
            finalPosition = position;
            viewPagerIndicator.selectDot(position);
            super.onPageSelected(position);
        }
    }
}