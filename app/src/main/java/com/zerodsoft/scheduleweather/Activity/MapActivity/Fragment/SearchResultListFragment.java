package com.zerodsoft.scheduleweather.Activity.MapActivity.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.zerodsoft.scheduleweather.Activity.MapActivity.MapActivity;
import com.zerodsoft.scheduleweather.Etc.ViewPagerIndicator;
import com.zerodsoft.scheduleweather.Fragment.SearchResultHeaderFragment;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.RecyclerVIewAdapter.SearchResultViewPagerAdapter;
import com.zerodsoft.scheduleweather.Retrofit.DownloadData;
import com.zerodsoft.scheduleweather.Retrofit.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressSearchResult;

import net.daum.mf.map.api.MapPoint;

import java.util.ArrayList;
import java.util.List;

public class SearchResultListFragment extends Fragment
{
    public static final String TAG = "SearchResult Fragment";

    private ViewPager2 viewPager2;
    private SearchResultViewPagerAdapter searchResultViewPagerAdapter;
    private AddressSearchResult result;
    private TextView rescanMapCenter;
    private TextView rescanMyLocCenter;
    private LocalApiPlaceParameter parameters;
    private LocationManager locationManager;
    private Spinner sortSpinner;

    private ViewPagerIndicator viewPagerIndicator;
    private int indicatorLength;

    private OnPageCallback onPageCallback;

    private String searchWord;

    private List<Integer> resultTypes;


    private LocationListener locationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            parameters.setX(location.getLongitude());
            parameters.setY(location.getLatitude());
            // 자원해제
            locationManager.removeUpdates(locationListener);

            for (int type : resultTypes)
            {
                if (type == DownloadData.PLACE_CATEGORY)
                {
                    DownloadData.searchPlaceCategory(handler, parameters);
                    break;
                } else if (type == DownloadData.ADDRESS)
                {
                    DownloadData.searchAddress(handler, parameters);
                } else if (type == DownloadData.PLACE_KEYWORD)
                {
                    DownloadData.searchPlaceKeyWord(handler, parameters);
                }
            }
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

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler()
    {
        private AddressSearchResult addressSearchResult = null;
        private int totalCallCount = 0;

        @Override
        public void handleMessage(Message msg)
        {
            ++totalCallCount;
            Bundle bundle = msg.getData();

            if (addressSearchResult == null)
            {
                addressSearchResult = new AddressSearchResult();
            }

            switch (msg.what)
            {
                case DownloadData.ADDRESS:
                    addressSearchResult.setAddressResponseDocuments(bundle.getParcelableArrayList("documents"));
                    addressSearchResult.setAddressResponseMeta(bundle.getParcelable("meta"));
                    break;
                case DownloadData.PLACE_KEYWORD:
                    addressSearchResult.setPlaceKeywordDocuments(bundle.getParcelableArrayList("documents"));
                    addressSearchResult.setPlaceKeywordMeta(bundle.getParcelable("meta"));
                    break;
                case DownloadData.PLACE_CATEGORY:
                    addressSearchResult.setPlaceCategoryDocuments(bundle.getParcelableArrayList("documents"));
                    addressSearchResult.setPlaceCategoryMeta(bundle.getParcelable("meta"));
                    break;
            }

            if (totalCallCount == indicatorLength)
            {
                result = addressSearchResult.clone();

                searchResultViewPagerAdapter.setAddressSearchResult(result);
                searchResultViewPagerAdapter.notifyDataSetChanged();

                Bundle dataBundle = new Bundle();
                dataBundle.putParcelable("result", result);
                dataBundle.putLong("downloadedTime", System.currentTimeMillis());

                ((MapActivity) getActivity()).onFragmentChanged(MapActivity.SEARCH_RESULT_FRAGMENT_UPDATE, dataBundle);

                totalCallCount = 0;
                addressSearchResult = null;
            }
        }
    };

    public SearchResultListFragment()
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
        View view = inflater.inflate(R.layout.fragment_search_result_list, container, false);

        viewPager2 = (ViewPager2) view.findViewById(R.id.search_result_viewpager);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        rescanMapCenter = (TextView) view.findViewById(R.id.search_result_map_center_rescan);
        rescanMyLocCenter = (TextView) view.findViewById(R.id.search_result_myloc_center_rescan);
        viewPagerIndicator = (ViewPagerIndicator) view.findViewById(R.id.search_result_view_pager_indicator);
        sortSpinner = (Spinner) view.findViewById(R.id.search_sort_spinner);

        setSortSpinner();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        viewPagerIndicator.createDot(0, indicatorLength);

        rescanMapCenter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                MapPoint.GeoCoordinate mapPoint = ((MapActivity) getActivity()).getMapCenterPoint();

                parameters.setX(mapPoint.longitude);
                parameters.setY(mapPoint.latitude);

                for (int type : resultTypes)
                {
                    if (type == DownloadData.PLACE_CATEGORY)
                    {
                        DownloadData.searchPlaceCategory(handler, parameters);
                        break;
                    } else if (type == DownloadData.ADDRESS)
                    {
                        DownloadData.searchAddress(handler, parameters);
                    } else if (type == DownloadData.PLACE_KEYWORD)
                    {
                        DownloadData.searchPlaceKeyWord(handler, parameters);
                    }
                }
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
        Bundle bundle = new Bundle();
        bundle.putParcelable("parameters", parameters);

        searchResultViewPagerAdapter = new SearchResultViewPagerAdapter(getActivity());
        searchResultViewPagerAdapter.setAddressSearchResult(result);
        searchResultViewPagerAdapter.setParameters(bundle);
        searchResultViewPagerAdapter.setSearchWord(searchWord);

        viewPager2.setAdapter(searchResultViewPagerAdapter);

        onPageCallback = new OnPageCallback();
        viewPager2.registerOnPageChangeCallback(onPageCallback);

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

    public void setData(Bundle bundle)
    {
        result = bundle.getParcelable("result");
        parameters = bundle.getParcelable("parameters");
        searchWord = bundle.getString("searchWord");
        indicatorLength = result.getResultNum();
        resultTypes = result.getResultTypes();
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
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                switch (i)
                {
                    case 0:
                        parameters.setSort(LocalApiPlaceParameter.SORT_ACCURACY);
                        break;
                    case 1:
                        parameters.setSort(LocalApiPlaceParameter.SORT_DISTANCE);
                        break;
                }

                for (int type : resultTypes)
                {
                    if (type == DownloadData.PLACE_CATEGORY)
                    {
                        DownloadData.searchPlaceCategory(handler, parameters);
                        break;
                    } else if (type == DownloadData.ADDRESS)
                    {
                        DownloadData.searchAddress(handler, parameters);
                    } else if (type == DownloadData.PLACE_KEYWORD)
                    {
                        DownloadData.searchPlaceKeyWord(handler, parameters);
                    }
                }
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