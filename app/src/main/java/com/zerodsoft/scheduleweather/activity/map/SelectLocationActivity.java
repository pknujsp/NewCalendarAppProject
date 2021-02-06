package com.zerodsoft.scheduleweather.activity.map;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;

import android.Manifest;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.CalendarContract;

import com.zerodsoft.scheduleweather.activity.map.util.RequestLocationTimer;
import com.zerodsoft.scheduleweather.kakaomap.activity.KakaoMapActivity;

import com.zerodsoft.scheduleweather.kakaomap.model.CustomPoiItem;
import com.zerodsoft.scheduleweather.kakaomap.viewmodel.AddressViewModel;
import com.zerodsoft.scheduleweather.kakaomap.viewmodel.PlacesViewModel;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import net.daum.mf.map.api.MapPOIItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class SelectLocationActivity extends KakaoMapActivity
{
    private OnBackPressedCallback onBackPressedCallback;
    private String eventLocation;

    public SelectLocationActivity()
    {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        eventLocation = getIntent().getStringExtra(CalendarContract.Events.EVENT_LOCATION);
        getIntent().removeExtra(CalendarContract.Events.EVENT_LOCATION);

        onBackPressedCallback = new OnBackPressedCallback(true)
        {
            @Override
            public void handleOnBackPressed()
            {
                // 맵 기본화면에서 뒤로가기하는 경우 취소로 판단
                setResult(RESULT_CANCELED);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

        // 위치가 이미 선택되어 있는 경우 해당 위치 정보를 표시함 (삭제 버튼 추가)
        /*
        if (checkNetwork())
        {
            LocationDTO selectedLocation = null;

            if (selectedLocation != null)
            {
                if (selectedLocation.getAddressName() != null)
                {
                    // 주소 검색 순서 : 좌표로 주소 변환
                    addressViewModel = new ViewModelProvider(this).get(AddressViewModel.class);

                    LocalApiPlaceParameter parameter = new LocalApiPlaceParameter();
                    parameter.setX(String.valueOf(selectedLocation.getLongitude())).setY(String.valueOf(selectedLocation.getLatitude()));
                    addressViewModel.init(parameter);

                    addressViewModel.getPagedListMutableLiveData().observe(getViewLifecycleOwner(), new Observer<PagedList<AddressResponseDocuments>>()
                    {
                        @Override
                        public void onChanged(PagedList<AddressResponseDocuments> addressResponseDocuments)
                        {
                            //주소는 바로 나온다, 해당 좌표를 설정
                            try
                            {
                                selectedAddressDocument = (AddressResponseDocuments) addressResponseDocuments.get(0).clone();
                            } catch (CloneNotSupportedException e)
                            {
                                e.printStackTrace();
                            }
                            List<AddressResponseDocuments> document = new ArrayList<>();
                            document.add(selectedAddressDocument);
                            createAddressesPoiItems(document);
                            selectPoiItem(0);
                        }
                    });
                } else if (selectedLocation.getPlaceId() != null)
                {
                    // 장소 검색 순서 : 장소의 위경도 내 10M 반경에서 장소 이름 검색(여러개 나올 경우 장소ID와 일치하는 장소를 선택)
                    placeViewModel = new ViewModelProvider(this).get(PlacesViewModel.class);

                    LocalApiPlaceParameter parameter = new LocalApiPlaceParameter();
                    parameter.setX(String.valueOf(selectedLocation.getLongitude())).setY(String.valueOf(selectedLocation.getLatitude()))
                            .setPage(LocalApiPlaceParameter.DEFAULT_PAGE)
                            .setSize(LocalApiPlaceParameter.DEFAULT_SIZE).setSort(LocalApiPlaceParameter.SORT_ACCURACY)
                            .setRadius("10").setQuery(selectedLocation.getPlaceName());
                    placeViewModel.init(parameter);

                    placeViewModel.getPagedListMutableLiveData().observe(getViewLifecycleOwner(), new Observer<PagedList<PlaceDocuments>>()
                    {
                        @Override
                        public void onChanged(PagedList<PlaceDocuments> placeDocuments)
                        {
                            //찾는 장소의 ID와 일치하는 장소가 있는지 확인
                            List<PlaceDocuments> placeDocumentsList = placeDocuments.snapshot();

                            for (PlaceDocuments document : placeDocumentsList)
                            {
                                if (iCatchedLocation.getLocation().getPlaceId().equals(document.getId()))
                                {
                                    try
                                    {
                                        selectedPlaceDocument = (PlaceDocuments) document.clone();
                                    } catch (CloneNotSupportedException e)
                                    {
                                        e.printStackTrace();
                                    }
                                    break;
                                }
                            }
                            List<PlaceDocuments> document = new ArrayList<>();
                            document.add(selectedPlaceDocument);
                            createPlacesPoiItems(document);
                            selectPoiItem(0);
                        }
                    });
                }
            } else
            {
                boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                int fineLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
                int coarseLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

                if (isGpsEnabled && isNetworkEnabled)
                {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Timer timer = new Timer();
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
                } else if (!isGpsEnabled)
                {
                    showRequestGpsDialog();
                }
            }
        }

         */
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        onBackPressedCallback.remove();
    }

    @Override
    public void onSelectLocation()
    {
        // 선택된 poiitem의 리스트내 인덱스를 가져온다.
        int poiItemIndex = kakaoMapFragment.getSelectedPoiItemIndex();
        MapPOIItem[] poiItems = kakaoMapFragment.mapView.getPOIItems();
        // 인덱스로 아이템을 가져온다.
        CustomPoiItem item = (CustomPoiItem) poiItems[poiItemIndex];

        String location = null;

        // 주소인지 장소인지를 구분한다.
        if (item.getPlaceDocument() != null)
        {
            location = item.getPlaceDocument().getPlaceName();
        } else if (item.getAddressDocument() != null)
        {
            location = item.getAddressDocument().getAddressName();
        }

        //선택된 위치를 DB에 등록
        getIntent().putExtra(CalendarContract.Events.EVENT_LOCATION, location);
        setResult(RESULT_OK, getIntent());
        finish();
    }

    @Override
    public void onRemoveLocation()
    {
        setResult(RESULT_OK, getIntent());
        finish();
    }
}