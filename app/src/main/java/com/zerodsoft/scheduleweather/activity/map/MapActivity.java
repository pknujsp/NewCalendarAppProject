package com.zerodsoft.scheduleweather.activity.map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.zerodsoft.scheduleweather.activity.editschedule.ScheduleEditActivity;
import com.zerodsoft.scheduleweather.activity.map.fragment.interfaces.FragmentReplace;
import com.zerodsoft.scheduleweather.activity.map.fragment.interfaces.ICatchedLocation;
import com.zerodsoft.scheduleweather.activity.map.fragment.map.MapFragment;
import com.zerodsoft.scheduleweather.activity.map.fragment.search.SearchFragment;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.SearchResultFragmentController;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.SearchResultFragment;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.LocationSearchResult;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;

import java.util.List;

public class MapActivity extends AppCompatActivity implements FragmentReplace, ICatchedLocation
{
    private final int FRAGMENT_CONTAINER_ID;

    private MapFragment mapFragment;
    private SearchFragment searchFragment;
    private SearchResultFragment searchResultFragment;
    private FragmentManager fragmentManager;

    private LocationDTO selectedLocation;
    private AddressDTO selectedAddress;
    private PlaceDTO selectedPlace;

    public MapActivity()
    {
        FRAGMENT_CONTAINER_ID = R.id.map_activity_fragment_container;
    }

    @Override
    public void replaceFragment(String fragmentTag, Bundle bundle)
    {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (fragmentTag)
        {
            case MapFragment.TAG:
            {
                fragmentTransaction.add(FRAGMENT_CONTAINER_ID, mapFragment, MapFragment.TAG);
            }
            break;
            case SearchFragment.TAG:
            {
                searchFragment = new SearchFragment();
                fragmentTransaction.hide(mapFragment).add(FRAGMENT_CONTAINER_ID, searchFragment, SearchFragment.TAG)
                        .addToBackStack(SearchFragment.TAG);
            }
            break;
            case SearchResultFragment.TAG:
            {
                SearchResultFragmentController searchResultFragmentController = new SearchResultFragmentController(bundle);
                fragmentTransaction.hide(searchFragment).add(FRAGMENT_CONTAINER_ID, searchResultFragmentController, SearchResultFragmentController.TAG)
                        .addToBackStack(SearchResultFragmentController.TAG);
            }
            break;
        }

        fragmentTransaction.commit();
    }

    public void init(Bundle bundle)
    {
        if (!bundle.isEmpty())
        {
            selectedLocation = bundle.getParcelable("location");
            if (selectedLocation instanceof AddressDTO)
            {
                // 주소 검색 순서 : 좌표로 주소 변환
                selectedAddress = (AddressDTO) selectedLocation;
            } else if (selectedLocation instanceof PlaceDTO)
            {
                // 장소 검색 순서 : 장소의 위경도 내 10M 반경에서 장소 이름 검색(여러개 나올 경우 장소ID와 일치하는 장소를 선택)
                selectedPlace = (PlaceDTO) selectedLocation;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        fragmentManager = getSupportFragmentManager();

        //선택된 위치가 있는지 여부 확인
        init(getIntent().getExtras());
        // Map프래그먼트 추가/실행
        mapFragment = new MapFragment(this);
        fragmentManager.beginTransaction().add(FRAGMENT_CONTAINER_ID, mapFragment, MapFragment.TAG).commit();
    }

    @Override
    public void onBackPressed()
    {

    }

    @Override
    public LocationDTO getLocation()
    {
        return selectedLocation;
    }

    @Override
    public PlaceDTO getPlace()
    {
        return selectedPlace;
    }

    @Override
    public AddressDTO getAddress()
    {
        return selectedAddress;
    }
}