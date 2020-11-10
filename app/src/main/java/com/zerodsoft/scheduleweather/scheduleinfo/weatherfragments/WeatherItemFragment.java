package com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.retrofit.paremeters.MidFcstParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.VilageFcstParameter;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.WeatherData;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.views.MidFcstView;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.views.UltraSrtFcstView;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.views.UltraSrtNcstFragment;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.views.VilageFcstView;
import com.zerodsoft.scheduleweather.utility.Clock;
import com.zerodsoft.scheduleweather.utility.LonLat;
import com.zerodsoft.scheduleweather.utility.LonLatConverter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WeatherItemFragment extends Fragment
{
    private WeatherData weatherData;

    private final double LATITUDE;
    private final double LONGITUDE;
    private final String ADDRESSNAME;

    private UltraSrtNcstFragment ultraSrtNcstFragment;
    private UltraSrtFcstView ultraSrtFcstView;
    private VilageFcstView vilageFcstView;
    private MidFcstView midFcstView;
    private WeatherAreaCodeDTO weatherAreaCode;

    private WeatherViewModel viewModel;
    private PlaceDTO place;
    private AddressDTO address;

    private List<SunSetRiseData> sunSetRiseList = new ArrayList<>();

    public WeatherItemFragment(double latitude, double longitude, String addressName)
    {
        LATITUDE = latitude;
        LONGITUDE = longitude;
        ADDRESSNAME = addressName;
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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        VilageFcstParameter vilageFcstParameter = new VilageFcstParameter();
        MidFcstParameter midLandFcstParameter = new MidFcstParameter();
        MidFcstParameter midTaParameter = new MidFcstParameter();

        LonLat lonLat = LonLatConverter.lonLatToGridXY(LONGITUDE, LATITUDE);

        viewModel = new ViewModelProvider(this).get(WeatherViewModel.class);
        viewModel.init(getContext(), lonLat.getX(), lonLat.getY());
        viewModel.getAreaCodeLiveData().observe(getViewLifecycleOwner(), new Observer<List<WeatherAreaCodeDTO>>()
        {
            @Override
            public void onChanged(List<WeatherAreaCodeDTO> weatherAreaCodes)
            {
                if (weatherAreaCodes != null)
                {
                    // regId설정하는 코드 작성
                    WeatherAreaCodeDTO weatherAreaCode = weatherAreaCodes.get(0);

                    vilageFcstParameter.setNx(weatherAreaCode.getX()).setNy(weatherAreaCode.getY()).setNumOfRows("10").setPageNo("1");
                    midLandFcstParameter.setNumOfRows("10").setPageNo("1").setRegId(weatherAreaCode.getMidLandFcstCode());
                    midTaParameter.setNumOfRows("10").setPageNo("1").setRegId(weatherAreaCode.getMidTaCode());

                    viewModel.getAllWeathersData(vilageFcstParameter, midLandFcstParameter, midTaParameter, weatherAreaCode);
                }
            }
        });

        viewModel.getWeatherDataLiveData().observe(getViewLifecycleOwner(), new Observer<List<WeatherData>>()
        {
            @Override
            public void onChanged(List<WeatherData> weatherDataList)
            {
                if (weatherDataList != null)
                {
                    setWeatherData(weatherDataList.get(0));
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

        int i = 0;
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
                weatherData.getWeatherAreaCode().getLongitudeSecondsDivide100()), Clock.TIME_ZONE);
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
      //  ultraSrtFcstView.setWeatherData(weatherData, sunSetRiseList);
    }

}
