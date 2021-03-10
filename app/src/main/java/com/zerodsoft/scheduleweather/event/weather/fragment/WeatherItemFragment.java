package com.zerodsoft.scheduleweather.event.weather.fragment;

import android.os.Bundle;
import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentWeatherItemBinding;
import com.zerodsoft.scheduleweather.event.common.interfaces.ILocation;
import com.zerodsoft.scheduleweather.event.weather.repository.WeatherDownloader;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.paremeters.MidFcstParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.VilageFcstParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.WeatherItems;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.midlandfcstresponse.MidLandFcstItems;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.midtaresponse.MidTaItems;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtfcstresponse.UltraSrtFcstItems;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtncstresponse.UltraSrtNcstItems;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.vilagefcstresponse.VilageFcstItems;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.event.weather.SunSetRiseData;
import com.zerodsoft.scheduleweather.event.weather.resultdata.WeatherData;
import com.zerodsoft.scheduleweather.event.weather.viewmodel.WeatherViewModel;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.utility.LonLat;
import com.zerodsoft.scheduleweather.utility.LonLatConverter;
import com.zerodsoft.scheduleweather.utility.WeatherDataConverter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class WeatherItemFragment extends Fragment
{
    public static final String TAG = "WeatherItemFragment";

    private FragmentWeatherItemBinding binding;
    private WeatherData weatherData;
    private LocationDTO locationDTO;
    private ILocation iLocation;

    private UltraSrtNcstFragment ultraSrtNcstFragment;
    private UltraSrtFcstFragment ultraSrtFcstFragment;
    private VilageFcstFragment vilageFcstFragment;
    private MidFcstFragment midFcstFragment;

    private WeatherViewModel viewModel;
    private List<SunSetRiseData> sunSetRiseList = new ArrayList<>();
    private WeatherAreaCodeDTO weatherAreaCode;

    private VilageFcstParameter vilageFcstParameter = new VilageFcstParameter();
    private MidFcstParameter midLandFcstParameter = new MidFcstParameter();
    private MidFcstParameter midTaParameter = new MidFcstParameter();

    private final WeatherDownloader weatherDownloader = new WeatherDownloader()
    {

        @Override
        public void onResponse(DataWrapper<? extends WeatherItems> result)
        {
            if (result.getException() == null)
            {
                //데이터 호출 성공한 경우
                if (result.getData() instanceof UltraSrtNcstItems)
                {
                    //데이터를 가공하고, 화면에 표시한다
                } else if (result.getData() instanceof UltraSrtFcstItems)
                {
                } else if (result.getData() instanceof VilageFcstItems)
                {
                } else if (result.getData() instanceof MidLandFcstItems)
                {
                    //land와 ta둘다 성공해야 화면에 표시가능
                } else if (result.getData() instanceof MidTaItems)
                {

                }
            } else
            {
                Toast.makeText(getActivity(), result.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    public WeatherItemFragment(ILocation iLocation)
    {
        this.iLocation = iLocation;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = FragmentWeatherItemBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        binding.refreshWeatherFab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                refreshWeatherData();
            }
        });

        FragmentManager fragmentManager = getChildFragmentManager();

        ultraSrtNcstFragment = (UltraSrtNcstFragment) fragmentManager.findFragmentById(R.id.ultra_srt_ncst_fragment);
        ultraSrtFcstFragment = (UltraSrtFcstFragment) fragmentManager.findFragmentById(R.id.ultra_srt_fcst_fragment);
        vilageFcstFragment = (VilageFcstFragment) fragmentManager.findFragmentById(R.id.vilage_fcst_fragment);
        midFcstFragment = (MidFcstFragment) fragmentManager.findFragmentById(R.id.mid_fcst_fragment);

        viewModel = new ViewModelProvider(this).get(WeatherViewModel.class);

        iLocation.getLocation(new CarrierMessagingService.ResultCallback<LocationDTO>()
        {
            @Override
            public void onReceiveResult(@NonNull LocationDTO locationDTO) throws RemoteException
            {
                WeatherItemFragment.this.locationDTO = locationDTO;
                final LonLat lonLat = LonLatConverter.convertGrid(locationDTO.getLongitude(), locationDTO.getLatitude());
                viewModel.init(getContext(), lonLat);

                viewModel.getAreaCodeLiveData().observe(getViewLifecycleOwner(), new Observer<List<WeatherAreaCodeDTO>>()
                {
                    @Override
                    public void onChanged(List<WeatherAreaCodeDTO> weatherAreaCodes)
                    {
                        if (weatherAreaCodes != null)
                        {
                            List<LocationPoint> locationPoints = new LinkedList<>();
                            for (WeatherAreaCodeDTO weatherAreaCodeDTO : weatherAreaCodes)
                            {
                                locationPoints.add(new LocationPoint(Double.parseDouble(weatherAreaCodeDTO.getLatitudeSecondsDivide100()), Double.parseDouble(weatherAreaCodeDTO.getLongitudeSecondsDivide100())));
                            }

                            int index = 0;
                            double minDistance = Double.MAX_VALUE;
                            double distance = 0;
                            // 점 사이의 거리 계산
                            for (int i = 0; i < locationPoints.size(); i++)
                            {
                                distance = Math.sqrt(Math.pow(locationDTO.getLongitude() - locationPoints.get(i).longitude, 2) + Math.pow(locationDTO.getLatitude() - locationPoints.get(i).latitude, 2));
                                if (distance < minDistance)
                                {
                                    minDistance = distance;
                                    index = i;
                                }
                            }
                            // regId설정하는 코드 작성
                            weatherAreaCode = weatherAreaCodes.get(index);

                            vilageFcstParameter.setNx(weatherAreaCode.getX()).setNy(weatherAreaCode.getY()).setNumOfRows("10").setPageNo("1");
                            midLandFcstParameter.setNumOfRows("10").setPageNo("1").setRegId(weatherAreaCode.getMidLandFcstCode());
                            midTaParameter.setNumOfRows("10").setPageNo("1").setRegId(weatherAreaCode.getMidTaCode());

                            // viewModel.getAllWeathersData(vilageFcstParameter, midLandFcstParameter, midTaParameter, weatherAreaCode);
                            refreshWeatherData();
                        }
                    }
                });
            }
        });


    }

    private void refreshWeatherData()
    {
        weatherDownloader.getWeatherData(vilageFcstParameter, midLandFcstParameter, midTaParameter, weatherAreaCode);
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    private void init()
    {
        //동네예보 마지막 날 까지의 일몰/일출 시간 데이터를 구성
        sunSetRiseList.clear();

        List<Calendar> dates = new ArrayList<>();

        Calendar endDate = weatherData.getDownloadedDate();
        endDate.add(Calendar.DAY_OF_YEAR, 2);

        Calendar calendar = weatherData.getDownloadedDate();

        boolean finished = false;

        while (!finished)
        {
            if (calendar.get(Calendar.YEAR) == endDate.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == endDate.get(Calendar.DAY_OF_YEAR))
            {
                finished = true;
            }
            dates.add((Calendar) calendar.clone());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        SunriseSunsetCalculator sunriseSunsetCalculator = new SunriseSunsetCalculator(new Location(weatherData.getWeatherAreaCode().getLatitudeSecondsDivide100(),
                weatherData.getWeatherAreaCode().getLongitudeSecondsDivide100()), ClockUtil.TIME_ZONE);
        Calendar sunRise = null;
        Calendar sunSet = null;

        for (Calendar date : dates)
        {
            sunRise = sunriseSunsetCalculator.getOfficialSunriseCalendarForDate(date);
            sunSet = sunriseSunsetCalculator.getOfficialSunsetCalendarForDate(date);
            sunSetRiseList.add(new SunSetRiseData(date.getTime(), sunRise.getTime(), sunSet.getTime()));
        }
    }

    public void setWeatherData(WeatherData weatherData)
    {
        this.weatherData = weatherData;

        binding.addressName.setText(weatherData.getAreaName());
        String updatedDateTime = ClockUtil.DB_DATE_FORMAT.format(weatherData.getDownloadedDate().getTime());
        binding.weatherUpdatedDatetime.setText("Updated : " + updatedDateTime);

        init();
        ultraSrtNcstFragment.setWeatherData(weatherData, sunSetRiseList.get(0));
        ultraSrtFcstFragment.setWeatherData(weatherData, sunSetRiseList);
        vilageFcstFragment.setWeatherData(weatherData, sunSetRiseList);
        midFcstFragment.setWeatherData(weatherData);
    }

    static class LocationPoint
    {
        private double latitude;
        private double longitude;

        public LocationPoint(double latitude, double longitude)
        {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude()
        {
            return latitude;
        }

        public double getLongitude()
        {
            return longitude;
        }
    }

}
