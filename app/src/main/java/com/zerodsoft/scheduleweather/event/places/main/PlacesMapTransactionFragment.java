package com.zerodsoft.scheduleweather.event.places.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zerodsoft.scheduleweather.activity.placecategory.viewmodel.PlaceCategoryViewModel;
import com.zerodsoft.scheduleweather.databinding.FragmentPlacesMapTransactionBinding;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.places.map.PlacesMapFragmentKakao;
import com.zerodsoft.scheduleweather.event.places.map.PlacesMapFragmentNaver;
import com.zerodsoft.scheduleweather.event.places.interfaces.FragmentController;
import com.zerodsoft.scheduleweather.event.places.interfaces.PlaceCategory;
import com.zerodsoft.scheduleweather.event.places.placecategorylist.PlaceListFragment;
import com.zerodsoft.scheduleweather.kakaomap.model.CoordToAddressUtil;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;

import java.util.List;


public class PlacesMapTransactionFragment extends Fragment implements PlaceCategory
{
    public static final String TAG = "TestMapFragment";
    private FragmentPlacesMapTransactionBinding binding;
    private PlacesMapFragmentKakao placesMapFragmentKakao;
    private PlacesMapFragmentNaver placesMapFragmentNaver;

    private CoordToAddress coordToAddressResult;
    private LocationDTO selectedLocationDto;
    private List<PlaceCategoryDTO> placeCategoryList;
    private PlaceCategoryViewModel placeCategoryViewModel;

    private LocationViewModel locationViewModel;

    private Integer calendarId;
    private Long eventId;
    private Long instanceId;
    private Long begin;

    public PlacesMapTransactionFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();

        calendarId = bundle.getInt("calendarId");
        eventId = bundle.getLong("eventId");
        instanceId = bundle.getLong("instanceId");
        begin = bundle.getLong("begin");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = FragmentPlacesMapTransactionBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        placeCategoryViewModel = new ViewModelProvider(this).get(PlaceCategoryViewModel.class);
        initLocation();
    }

    private void initLocation()
    {
        locationViewModel.getLocation(calendarId, eventId, new CarrierMessagingService.ResultCallback<LocationDTO>()
        {
            @Override
            public void onReceiveResult(@NonNull LocationDTO location) throws RemoteException
            {
                if (location.getId() >= 0)
                {
                    selectedLocationDto = location;

                    placeCategoryViewModel.selectConvertedSelected(new CarrierMessagingService.ResultCallback<List<PlaceCategoryDTO>>()
                    {
                        @Override
                        public void onReceiveResult(@NonNull List<PlaceCategoryDTO> result) throws RemoteException
                        {
                            placeCategoryList = result;

                            getActivity().runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    LocalApiPlaceParameter localApiPlaceParameter = new LocalApiPlaceParameter();
                                    localApiPlaceParameter.setX(String.valueOf(location.getLongitude()));
                                    localApiPlaceParameter.setY(String.valueOf(location.getLatitude()));

                                    CoordToAddressUtil.coordToAddress(localApiPlaceParameter, new CarrierMessagingService.ResultCallback<DataWrapper<CoordToAddress>>()
                                    {
                                        @Override
                                        public void onReceiveResult(@NonNull DataWrapper<CoordToAddress> coordToAddressDataWrapper) throws RemoteException
                                        {
                                            getActivity().runOnUiThread(new Runnable()
                                            {
                                                @Override
                                                public void run()
                                                {
                                                    if (coordToAddressDataWrapper.getException() == null)
                                                    {
                                                        coordToAddressResult = coordToAddressDataWrapper.getData();
                                                    } else
                                                    {
                                                        Toast.makeText(getActivity(), coordToAddressDataWrapper.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }

                                                    /*
                                                    kakao
                                                               placesMapFragmentKakao = new PlacesMapFragmentKakao(TestMapFragment.this);
                                                    placesMapFragmentKakao.setSelectedLocationDto(selectedLocationDto);
                                                     */
                                                    placesMapFragmentNaver = new PlacesMapFragmentNaver(PlacesMapTransactionFragment.this);
                                                    placesMapFragmentNaver.setSelectedLocationDto(selectedLocationDto);
                                                    placesMapFragmentNaver.setCoordToAddressResult(coordToAddressResult);

                                                    getChildFragmentManager().beginTransaction().add(binding.placesMapFragmentContainer.getId(), placesMapFragmentNaver, PlacesMapFragmentNaver.TAG)
                                                            .commit();
                                                }
                                            });

                                        }
                                    });


                                }
                            });
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public List<PlaceCategoryDTO> getPlaceCategoryList()
    {
        return placeCategoryList;
    }
}