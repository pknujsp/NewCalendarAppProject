package com.zerodsoft.scheduleweather.event.weather.weatherfragments.fragment;

import android.os.Bundle;
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
import com.zerodsoft.scheduleweather.event.weather.weatherfragments.repository.WeatherDownloader;
import com.zerodsoft.scheduleweather.retrofit.paremeters.MidFcstParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.VilageFcstParameter;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.event.location.placefragments.LocationInfo;
import com.zerodsoft.scheduleweather.event.weather.weatherfragments.SunSetRiseData;
import com.zerodsoft.scheduleweather.event.weather.weatherfragments.resultdata.WeatherData;
import com.zerodsoft.scheduleweather.event.weather.weatherfragments.viewmodel.WeatherViewModel;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.utility.LonLat;
import com.zerodsoft.scheduleweather.utility.LonLatConverter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class WeatherItemFragment extends Fragment
{
    public static final String TAG = "WeatherItemFragment";
    private WeatherData weatherData;
    private final LocationDTO locationDTO;

    private UltraSrtNcstFragment ultraSrtNcstFragment;
    private UltraSrtFcstFragment ultraSrtFcstFragment;
    private VilageFcstFragment vilageFcstFragment;
    private MidFcstFragment midFcstFragment;

    private WeatherViewModel viewModel;
    private List<SunSetRiseData> sunSetRiseList = new ArrayList<>();

    public WeatherItemFragment(LocationDTO locationDTO)
    {
        this.locationDTO = locationDTO;
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
        return inflater.inflate(R.layout.fragment_weather_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        FragmentManager fragmentManager = getChildFragmentManager();

        ultraSrtNcstFragment = (UltraSrtNcstFragment) fragmentManager.findFragmentById(R.id.ultra_srt_ncst_fragment);
        ultraSrtFcstFragment = (UltraSrtFcstFragment) fragmentManager.findFragmentById(R.id.ultra_srt_fcst_fragment);
        vilageFcstFragment = (VilageFcstFragment) fragmentManager.findFragmentById(R.id.vilage_fcst_fragment);
        midFcstFragment = (MidFcstFragment) fragmentManager.findFragmentById(R.id.mid_fcst_fragment);

        VilageFcstParameter vilageFcstParameter = new VilageFcstParameter();
        MidFcstParameter midLandFcstParameter = new MidFcstParameter();
        MidFcstParameter midTaParameter = new MidFcstParameter();

        final LonLat lonLat = LonLatConverter.convertGrid(locationDTO.getLongitude(), locationDTO.getLatitude());

        viewModel = new ViewModelProvider(this).get(WeatherViewModel.class);
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
                    WeatherAreaCodeDTO weatherAreaCode = weatherAreaCodes.get(index);

                    vilageFcstParameter.setNx(weatherAreaCode.getX()).setNy(weatherAreaCode.getY()).setNumOfRows("10").setPageNo("1");
                    midLandFcstParameter.setNumOfRows("10").setPageNo("1").setRegId(weatherAreaCode.getMidLandFcstCode());
                    midTaParameter.setNumOfRows("10").setPageNo("1").setRegId(weatherAreaCode.getMidTaCode());

                    // viewModel.getAllWeathersData(vilageFcstParameter, midLandFcstParameter, midTaParameter, weatherAreaCode);

                    WeatherDownloader weatherDownloader = new WeatherDownloader(getContext())
                    {
                        @Override
                        public void onSuccessful(WeatherData weatherData)
                        {
                            super.onSuccessful(weatherData);
                            setWeatherData(weatherData);
                        }

                        @Override
                        public void onFailure(Exception exception)
                        {
                            super.onFailure(exception);
                            Toast.makeText(getActivity(), "날씨 데이터 다운로드 실패", Toast.LENGTH_SHORT).show();
                        }
                    };

                    weatherDownloader.getWeatherData(vilageFcstParameter, midLandFcstParameter, midTaParameter, weatherAreaCode);
                }
            }
        });
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
        init();
        ultraSrtNcstFragment.setWeatherData(weatherData, sunSetRiseList.get(0));
        ultraSrtFcstFragment.setWeatherData(weatherData, sunSetRiseList);
        vilageFcstFragment.setWeatherData(weatherData, sunSetRiseList);
        midFcstFragment.setWeatherData(weatherData);
    }

    class LocationPoint
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
