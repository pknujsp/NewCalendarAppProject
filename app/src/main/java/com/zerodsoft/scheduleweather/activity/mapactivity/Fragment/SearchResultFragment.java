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

import net.daum.mf.map.api.MapPoint;

import java.util.ArrayList;
import java.util.List;

public class SearchResultFragment extends Fragment
{
    /*
    스크롤 시에 추가 데이터를 가져오는 코드 보완 필요(데이터 불러오는데에 오류있음)
    map으로 갔다가 돌아올때 정확도/거리순 스피너가 초기화되어 있음
    viewpager의 페이지 수가 정확하지 않음
     */
    public static final String TAG = "SearchResult Fragment";
    private static SearchResultFragment instance;

    private ViewPager2 viewPager2;
    private SearchResultViewPagerAdapter searchResultViewPagerAdapter;
    private TextView rescanMapCenter;
    private TextView rescanMyLocCenter;
    private LocationManager locationManager;
    private Spinner sortSpinner;
    private SpinnerAdapter spinnerAdapter;
    private ViewPagerIndicator viewPagerIndicator;
    private int indicatorLength;

    private List<String> sortList;
    private boolean isFirstCreated = true;

    public static final int SORT_ACCURACY = 0;
    public static final int SORT_DISTANCE = 1;

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
        sortList = new ArrayList<>();
        sortList.add(SORT_ACCURACY, "정확도순");
        sortList.add(SORT_DISTANCE, "거리순");
    }

    public void setInitialData(Bundle bundle)
    {
        if (!bundle.isEmpty())
        {
        }
    }

    public void clearHolderSparseArr()
    {
        searchResultViewPagerAdapter.clearHolderSparseArr();
    }

    public void setDownloadedData(boolean refresh)
    {
        searchResultViewPagerAdapter.setListData(refresh);

        if (!refresh)
        {
            List<Integer> dataTypes = MapActivity.searchResult.getResultTypes();
            indicatorLength = 0;

            for (int dataType : dataTypes)
            {
                if (dataType == MapController.TYPE_PLACE_CATEGORY)
                {
                    ++indicatorLength;
                } else if (dataType == MapController.TYPE_PLACE_KEYWORD)
                {
                    ++indicatorLength;
                } else if (dataType == MapController.TYPE_ADDRESS)
                {
                    ++indicatorLength;
                }
            }
            viewPagerIndicator.createDot(0, indicatorLength);
        }
    }


    public void setDownloadedExtraData(int type)
    {
        searchResultViewPagerAdapter.addExtraData(type);
    }

    private LocationListener locationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            MapActivity.parameters.setX(location.getLongitude());
            MapActivity.parameters.setY(location.getLatitude());
            MapActivity.parameters.setPage("1");
            // 자원해제
            locationManager.removeUpdates(locationListener);
            onDownloadListener.requestData(MapController.TYPE_NOT, TAG, true);
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

        searchResultViewPagerAdapter = new SearchResultViewPagerAdapter(getActivity());
        onPageCallback = new OnPageCallback();

        spinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, sortList);
        sortSpinner.setAdapter(spinnerAdapter);
        sortSpinner.setOnItemSelectedListener(onItemSelectedListener);

        viewPager2.setAdapter(searchResultViewPagerAdapter);
        viewPager2.registerOnPageChangeCallback(onPageCallback);

        rescanMapCenter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                MapPoint.GeoCoordinate currentMapPoint = MapFragment.currentMapPoint.getMapPointGeoCoord();
                MapActivity.parameters.setX(currentMapPoint.longitude);
                MapActivity.parameters.setY(currentMapPoint.latitude);
                MapActivity.parameters.setPage("1");

                onDownloadListener.requestData(MapController.TYPE_NOT, TAG, true);
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
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onDestroy()
    {
        isFirstCreated = true;
        super.onDestroy();
    }

    public int getCurrentListType()
    {
        // header fragment에서 change 버튼 클릭 시 리스트의 타입을 가져오기 위해 사용
        return searchResultViewPagerAdapter.getCurrentListType(onPageCallback.finalPosition);
    }

    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener()
    {
        // 결과 프래그먼트에서 스피너로 선택 후 뒤로 간 다음 다시 검색하여 재 실행하면 오류발생
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l)
        {
            switch (index)
            {
                case SORT_ACCURACY:
                    MapActivity.parameters.setSort(LocalApiPlaceParameter.SORT_ACCURACY);
                    break;
                case SORT_DISTANCE:
                    MapActivity.parameters.setSort(LocalApiPlaceParameter.SORT_DISTANCE);
                    break;
            }
            MapActivity.parameters.setPage("1");
            onDownloadListener.requestData(MapController.TYPE_NOT, TAG, true);

            isFirstCreated = false;
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
            finalPosition = position;
            viewPagerIndicator.selectDot(position);
            super.onPageSelected(position);
        }
    }
}