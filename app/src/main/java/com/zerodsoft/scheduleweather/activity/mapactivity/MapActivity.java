package com.zerodsoft.scheduleweather.activity.mapactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.zerodsoft.scheduleweather.activity.ScheduleInfoActivity;
import com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.MapController;
import com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.MapFragment;
import com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.SearchFragment;
import com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.SearchResultFragment;
import com.zerodsoft.scheduleweather.fragment.SearchResultController;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.recyclerviewadapter.SearchResultViewPagerAdapter;
import com.zerodsoft.scheduleweather.retrofit.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.LocationSearchResult;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;

import java.util.List;

public class MapActivity extends AppCompatActivity implements SearchResultViewPagerAdapter.OnScrollResultList, MapController.OnDownloadedData
{
    public static int requestCode = 0;
    public static boolean isSelectedLocation = false;
    private final int fragmentViewId;

    private MapFragment mapFragment;
    private SearchFragment searchFragment;
    private SearchResultFragment searchResultFragment;

    private AddressDTO selectedAddress;
    private PlaceDTO selectedPlace;

    private MapController mapController;

    public MapActivity()
    {
        fragmentViewId = R.id.map_activity_fragment_layout;
        mapController = new MapController();
    }

    @Override
    public void onScroll(LocalApiPlaceParameter parameter)
    {
        mapController.selectLocation(parameter);
    }

    @Override
    public void onDownloadedData(LocalApiPlaceParameter parameter, LocationSearchResult locationSearchResult)
    {
        // 다운로드된 데이터를 전달

    }

    public interface OnBackPressedListener
    {
        void onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent intent = getIntent();
        requestCode = intent.getIntExtra("requestCode", 0);

        switch (requestCode)
        {
            ScheduleInfoActivity.ADD_LOCATION:
            isSelectedLocation = false;
            break;

            ScheduleInfoActivity.EDIT_LOCATION:
            isSelectedLocation = true;

            selectedPlace = intent.getParcelableExtra("place");
            selectedAddress = intent.getParcelableExtra("address");
            // 장소 검색 순서 : 장소의 위경도 내 10M 반경에서 장소 이름 검색(여러개 나올 경우 장소ID와 일치하는 장소를 선택
            // 주소 검색 순서 : 좌표로 주소 변환
            try
            {
                mapFragment.setSelectedPlace((PlaceDTO) selectedPlace.clone());
                mapFragment.setSelectedAddress((AddressDTO) selectedAddress.clone());
            } catch (CloneNotSupportedException e)
            {

            }
            break;
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    public void onFragmentChanged(Fragment fragment, Bundle bundle)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (fragment instanceof MapFragment)
        {
            ((MapFragment) fragment).setInitialData(bundle);
        } else if (fragment instanceof SearchFragment)
        {
            ((SearchFragment) fragment).setInitialData(bundle);
        } else if (fragment instanceof SearchResultFragment)
        {
            ((SearchResultFragment) fragment).setInitialData(bundle);
        }
        fragmentTransaction.replace(fragmentViewId, fragment).addToBackStack(null).commit();
    }

    public void onFragmentChanged(int type, Bundle bundle)
    {
        if (type != SEARCH_RESULT_FRAGMENT_UPDATE)
        {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            switch (type)
            {
                case SEARCH_FRAGMENT:
                    SearchFragment searchFragment = new SearchFragment();
                    searchFragment.setData(bundle);

                    fragmentTransaction.add(R.id.map_activity_root_layout, searchFragment, SearchFragment.TAG);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    setZoomGpsButtonVisibility(View.GONE);

                    break;
                case SEARCH_RESULT_FRAGMENT:
                    // fragment_search_layout에 헤더/리스트 프래그먼트를 추가
                    setResultData(bundle);

                    if (searchResultController == null)
                    {
                        searchResultController = new SearchResultController();
                    }
                    searchResultController.setResultData(bundle);

                    List<Fragment> fragments = fragmentManager.getFragments();

                    int i = 0;
                    for (; i < fragments.size(); i++)
                    {
                        if (fragments.get(i) instanceof SearchFragment)
                        {
                            break;
                        }
                    }
                    fragmentTransaction.hide(fragments.get(i));
                    fragmentTransaction.add(R.id.map_activity_root_layout, searchResultController, SearchResultController.TAG);
                    fragmentTransaction.commit();
                    break;
            }
        } else
        {
            // fragment_search_layout에 헤더/리스트 프래그먼트를 추가
            setResultData(bundle);
        }
    }
}