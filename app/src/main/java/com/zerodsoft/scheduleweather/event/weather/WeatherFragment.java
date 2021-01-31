package com.zerodsoft.scheduleweather.event.weather;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentScheduleWeatherBinding;
import com.zerodsoft.scheduleweather.event.common.interfaces.ILocation;
import com.zerodsoft.scheduleweather.event.weather.weatherfragments.fragment.WeatherItemFragment;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.utility.WeatherDataConverter;


public class WeatherFragment extends Fragment
{
    // 이벤트의 위치 값으로 날씨를 표시할 정확한 위치를 지정하기 위해 위치 지정 액티비티 생성(기상청 지역 리스트 기반)
    private ILocation iLocation;
    private FragmentScheduleWeatherBinding binding;

    public WeatherFragment(ILocation iLocation)
    {
        this.iLocation = iLocation;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        WeatherDataConverter.context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = FragmentScheduleWeatherBinding.inflate(getLayoutInflater());
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

    public void rotateRefreshButton(boolean value)
    {
        if (value)
        {
            RotateAnimation rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
            // binding.. startAnimation(rotateAnimation);
        } else
        {
            //  refreshButton.getAnimation().cancel();
        }
    }

    public void initLocation()
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
                        getActivity().runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                binding.weatherFragmentContainerView.setVisibility(View.VISIBLE);
                                binding.eventRegisterDetailLocation.rootLayout.setVisibility(View.GONE);
                            }
                        });

                        iLocation.getLocation(new CarrierMessagingService.ResultCallback<LocationDTO>()
                        {
                            @Override
                            public void onReceiveResult(@NonNull LocationDTO locationDTO) throws RemoteException
                            {
                                getActivity().runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        if (locationDTO.getAddressName() != null)
                                        {
                                            getParentFragmentManager().beginTransaction().add(R.id.weather_fragment_container_view,
                                                    new WeatherItemFragment(locationDTO), WeatherItemFragment.class.getName())
                                                    .commit();
                                        }
                                    }
                                });

                            }
                        });

                    } else
                    {
                        getActivity().runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                // 상세 위치 정보가 설정되지 않음
                                binding.weatherFragmentContainerView.setVisibility(View.GONE);
                                binding.eventRegisterDetailLocation.rootLayout.setVisibility(View.VISIBLE);
                                binding.eventRegisterDetailLocation.locationStatusDescription.setText(getString(R.string.need_register_detail_location));
                            }
                        });

                    }
                }
            });

        } else
        {
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    // 이벤트에서 위치가 지정되지 않음
                    binding.eventRegisterDetailLocation.registerDetailLocationButton.setVisibility(View.GONE);
                    binding.weatherFragmentContainerView.setVisibility(View.GONE);
                    binding.eventRegisterDetailLocation.rootLayout.setVisibility(View.VISIBLE);
                    binding.eventRegisterDetailLocation.locationStatusDescription.setText(getString(R.string.not_selected_location_in_event));
                }
            });

        }
    }

}