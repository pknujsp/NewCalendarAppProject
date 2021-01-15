package com.zerodsoft.scheduleweather.activity.map.fragment.map;

import android.Manifest;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.map.MapActivity;
import com.zerodsoft.scheduleweather.etc.FragmentStateCallback;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.ICatchedLocation;
import com.zerodsoft.scheduleweather.activity.map.fragment.search.SearchFragment;
import com.zerodsoft.scheduleweather.activity.map.util.RequestLocationTimer;
import com.zerodsoft.scheduleweather.kakaomap.fragment.KakaoMapFragment;
import com.zerodsoft.scheduleweather.kakaomap.viewmodel.AddressViewModel;
import com.zerodsoft.scheduleweather.kakaomap.viewmodel.PlacesViewModel;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class MapFragment extends KakaoMapFragment
{
    // list에서 item클릭 시 poiitem이 선택되고 맵 중앙좌표가 해당item의 좌표로 변경되면서 하단 시트가 올라온다
    public static final String TAG = "MapFragment";
    private static MapFragment instance;

    private AddressViewModel addressViewModel;
    private PlacesViewModel placeViewModel;
    private ICatchedLocation iCatchedLocation;

    private AddressResponseDocuments selectedAddressDocument;
    private PlaceDocuments selectedPlaceDocument;

    private OnBackPressedCallback onBackPressedCallback;

    public MapFragment(ICatchedLocation iCatchedLocation)
    {
        this.iCatchedLocation = iCatchedLocation;
    }

    public MapFragment()
    {
    }

    public static MapFragment getInstance()
    {
        return instance;
    }

    public static MapFragment newInstance(ICatchedLocation iCatchedLocation)
    {
        instance = new MapFragment(iCatchedLocation);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);

        onBackPressedCallback = new OnBackPressedCallback(true)
        {
            @Override
            public void handleOnBackPressed()
            {
                ((MapActivity) getActivity()).getOnBackPressedDispatcher().onBackPressed();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        onBackPressedCallback.remove();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        if (checkNetwork())
        {
            LocationDTO selectedLocation = null;

            if (selectedLocation != null)
            {
                if (selectedLocation instanceof AddressDTO)
                {
                    // 주소 검색 순서 : 좌표로 주소 변환
                    AddressDTO address = iCatchedLocation.getAddress();
                    addressViewModel = new ViewModelProvider(this).get(AddressViewModel.class);

                    LocalApiPlaceParameter parameter = new LocalApiPlaceParameter();
                    parameter.setX(address.getLongitude()).setY(address.getLatitude());
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
                } else if (selectedLocation instanceof PlaceDTO)
                {
                    // 장소 검색 순서 : 장소의 위경도 내 10M 반경에서 장소 이름 검색(여러개 나올 경우 장소ID와 일치하는 장소를 선택)
                    PlaceDTO place = iCatchedLocation.getPlace();
                    placeViewModel = new ViewModelProvider(this).get(PlacesViewModel.class);

                    LocalApiPlaceParameter parameter = new LocalApiPlaceParameter();
                    parameter.setX(place.getLongitude()).setY(place.getLatitude()).setPage(LocalApiPlaceParameter.DEFAULT_PAGE)
                            .setSize(LocalApiPlaceParameter.DEFAULT_SIZE).setSort(LocalApiPlaceParameter.SORT_ACCURACY)
                            .setRadius("10").setQuery(place.getPlaceName());
                    placeViewModel.init(parameter);

                    placeViewModel.getPagedListMutableLiveData().observe(getViewLifecycleOwner(), new Observer<PagedList<PlaceDocuments>>()
                    {
                        @Override
                        public void onChanged(PagedList<PlaceDocuments> placeDocuments)
                        {
                            //찾는 장소의 ID와 일치하는 장소가 있는지 확인
                            List<PlaceDocuments> placeDocumentsList = placeDocuments.snapshot();
                            PlaceDTO place = iCatchedLocation.getPlace();

                            for (PlaceDocuments document : placeDocumentsList)
                            {
                                if (place.getId() == Integer.parseInt(document.getId()))
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

    }


}