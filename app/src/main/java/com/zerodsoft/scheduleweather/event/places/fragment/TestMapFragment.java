package com.zerodsoft.scheduleweather.event.places.fragment;

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
import com.zerodsoft.scheduleweather.calendarview.interfaces.IstartActivity;
import com.zerodsoft.scheduleweather.databinding.FragmentTestMapBinding;
import com.zerodsoft.scheduleweather.event.common.interfaces.ILocation;
import com.zerodsoft.scheduleweather.event.places.interfaces.FragmentController;
import com.zerodsoft.scheduleweather.event.places.interfaces.PlaceCategory;
import com.zerodsoft.scheduleweather.kakaomap.model.CoordToAddressUtil;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;

import java.util.List;


public class TestMapFragment extends Fragment implements PlaceCategory, FragmentController
{
    private FragmentTestMapBinding binding;
    private PlacesMapFragment placesMapFragment;
    private PlaceListFragment placeListFragment;

    private final ILocation iLocation;
    private final IstartActivity istartActivity;
    private CoordToAddress coordToAddressResult;
    private LocationDTO selectedLocationDto;
    private List<PlaceCategoryDTO> placeCategoryList;
    private PlaceCategoryViewModel placeCategoryViewModel;

    public TestMapFragment(ILocation iLocation, IstartActivity istartActivity)
    {
        this.iLocation = iLocation;
        this.istartActivity = istartActivity;
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
        binding = FragmentTestMapBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        placeCategoryViewModel = new ViewModelProvider(this).get(PlaceCategoryViewModel.class);
        initLocation();
    }

    private void initLocation()
    {
        iLocation.getLocation(new CarrierMessagingService.ResultCallback<LocationDTO>()
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

                                                    placesMapFragment = new PlacesMapFragment(TestMapFragment.this);
                                                    placesMapFragment.setIstartActivity(istartActivity);
                                                    placesMapFragment.setSelectedLocationDto(selectedLocationDto);

                                                    placeListFragment = new PlaceListFragment(TestMapFragment.this);
                                                    placeListFragment.setOnClickedPlacesListListener(placesMapFragment);
                                                    placeListFragment.setSelectedLocationDto(selectedLocationDto);
                                                    placeListFragment.setCoordToAddressResult(coordToAddressResult);

                                                    placesMapFragment.setPlaceItemsGetter(placeListFragment);

                                                    FragmentManager fragmentManager = getChildFragmentManager();
                                                    fragmentManager.beginTransaction().add(binding.placesMapFragmentContainer.getId(), placesMapFragment, PlacesMapFragment.TAG)
                                                            .add(binding.placesListFragment.getId(), placeListFragment, PlaceListFragment.TAG)
                                                            .hide(placeListFragment)
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

    @Override
    public void replaceFragment(String tag)
    {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        if (tag.equals(PlaceListFragment.TAG))
        {
            fragmentTransaction.hide(placesMapFragment).show(placeListFragment).commit();
            placeListFragment.setOnBackPressedCallback();
        } else if (tag.equals(PlacesMapFragment.TAG))
        {
            fragmentTransaction.hide(placeListFragment).show(placesMapFragment).commit();
        }
    }

}