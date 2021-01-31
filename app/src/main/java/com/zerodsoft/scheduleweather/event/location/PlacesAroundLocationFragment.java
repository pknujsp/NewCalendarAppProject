package com.zerodsoft.scheduleweather.event.location;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentScheduleAroundLocationBinding;
import com.zerodsoft.scheduleweather.event.common.interfaces.ILocation;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.event.location.placefragments.LocationInfo;
import com.zerodsoft.scheduleweather.event.location.placefragments.fragment.PlacesFragment;


public class PlacesAroundLocationFragment extends Fragment
{
    // 이벤트의 위치 값으로 정확한 위치를 지정하기 위해 위치 지정 액티비티 생성(카카오맵 검색 값 기반)
    private ILocation iLocation;
    private FragmentScheduleAroundLocationBinding binding;

    public PlacesAroundLocationFragment(ILocation iLocation)
    {
        this.iLocation = iLocation;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentScheduleAroundLocationBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        binding.eventRegisterDetailLocation.registerDetailLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                iLocation.showRequestLocDialog();
            }
        });
        initLocation();
    }

    private void initLocation()
    {
        if (iLocation.hasSimpleLocation())
        {
            iLocation.hasDetailLocation(new CarrierMessagingService.ResultCallback<Boolean>()
            {
                @Override
                public void onReceiveResult(@NonNull Boolean hasDetailLocation) throws RemoteException
                {
                    if (hasDetailLocation)
                    {
                        binding.placesAroundLocationFragmentContainer.setVisibility(View.VISIBLE);
                        binding.eventRegisterDetailLocation.getRoot().setVisibility(View.GONE);

                        iLocation.getLocation(new CarrierMessagingService.ResultCallback<LocationDTO>()
                        {
                            @Override
                            public void onReceiveResult(@NonNull LocationDTO location) throws RemoteException
                            {
                                Fragment fragment = null;

                                if (location.getPlaceId() != null)
                                {
                                    fragment = new PlacesFragment(new LocationInfo(location.getLatitude(), location.getLongitude(), location.getPlaceName()));
                                } else
                                {
                                    fragment = new PlacesFragment(new LocationInfo(location.getLatitude(), location.getLongitude(), location.getAddressName()));
                                }
                                FragmentManager fragmentManager = getChildFragmentManager();
                                fragmentManager.beginTransaction().add(R.id.places_around_location_fragment_container, fragment).commit();
                            }
                        });

                    } else
                    {
                        binding.placesAroundLocationFragmentContainer.setVisibility(View.GONE);
                        binding.eventRegisterDetailLocation.getRoot().setVisibility(View.VISIBLE);
                        binding.eventRegisterDetailLocation.locationStatusDescription.setText(getString(R.string.need_register_detail_location));
                        // 상세 위치 정보가 설정되지 않음
                    }
                }
            });


        } else
        {
            binding.placesAroundLocationFragmentContainer.setVisibility(View.GONE);
            binding.eventRegisterDetailLocation.getRoot().setVisibility(View.VISIBLE);
            binding.eventRegisterDetailLocation.locationStatusDescription.setText(getString(R.string.not_selected_location_in_event));
            // 이벤트에서 위치가 지정되지 않음
        }
    }
}