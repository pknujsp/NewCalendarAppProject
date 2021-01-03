package com.zerodsoft.scheduleweather.activity.map;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.zerodsoft.scheduleweather.activity.editevent.EventActivity;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.ICatchedLocation;
import com.zerodsoft.scheduleweather.activity.map.fragment.map.MapFragment;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;

public class MapActivity extends AppCompatActivity implements ICatchedLocation
{
    private final int FRAGMENT_CONTAINER_ID;
    private FragmentManager fragmentManager;

    private LocationDTO selectedLocation;
    private AddressDTO selectedAddress;
    private PlaceDTO selectedPlace;
    private OnBackPressedCallback onBackPressedCallback;

    public MapActivity()
    {
        FRAGMENT_CONTAINER_ID = R.id.map_activity_fragment_container;
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
        fragmentManager.beginTransaction().add(FRAGMENT_CONTAINER_ID, MapFragment.newInstance(this), MapFragment.TAG).commit();

        onBackPressedCallback = new OnBackPressedCallback(true)
        {
            @Override
            public void handleOnBackPressed()
            {
                setResult(RESULT_CANCELED);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        onBackPressedCallback.remove();
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

    @Override
    public void choiceLocation(LocationDTO locationDTO)
    {
        Bundle bundle = new Bundle();
        if (locationDTO instanceof AddressDTO)
        {
            bundle.putParcelable("address", (AddressDTO) locationDTO);
        } else
        {
            bundle.putParcelable("place", (PlaceDTO) locationDTO);
        }
        getIntent().putExtras(bundle);
        setResult(EventActivity.LOCATION_SELECTED, getIntent());
        finish();
    }
}