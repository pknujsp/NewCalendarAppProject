package com.zerodsoft.scheduleweather.scheduleinfo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.retrofit.paremeters.MidFcstParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.VilageFcstParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.midlandfcstresponse.MidLandFcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.midtaresponse.MidTaItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtfcstresponse.UltraSrtFcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtncstresponse.UltraSrtNcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.vilagefcstresponse.VilageFcstItem;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.UltraSrtNcstData;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.WeatherDataConverter;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.WeatherViewModel;
import com.zerodsoft.scheduleweather.utility.LonLat;
import com.zerodsoft.scheduleweather.utility.LonLatConverter;

import java.util.List;


public class ScheduleWeatherFragment extends Fragment
{
    private WeatherViewModel viewModel;
    private PlaceDTO place;
    private AddressDTO address;

    public ScheduleWeatherFragment(PlaceDTO place, AddressDTO address)
    {
        this.place = place;
        this.address = address;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        WeatherDataConverter.context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule_weather, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        VilageFcstParameter vilageFcstParameter = new VilageFcstParameter();
        MidFcstParameter midLandFcstParameter = new MidFcstParameter();
        MidFcstParameter midTaParameter = new MidFcstParameter();

        double longitude = 0;
        double latitude = 0;

        if (place != null)
        {
            longitude = Double.valueOf(place.getLongitude());
            latitude = Double.valueOf(place.getLatitude());
        } else if (address != null)
        {
            longitude = Double.valueOf(address.getLongitude());
            latitude = Double.valueOf(address.getLatitude());
        }

        LonLat lonLat = LonLatConverter.lonLatToGridXY(longitude, latitude);

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
                    String midLandFcstRegId = weatherAreaCodes.get(0).getMidLandFcstCode();
                    String midTaRegId = weatherAreaCodes.get(0).getMidTaCode();

                    vilageFcstParameter.setNx(weatherAreaCodes.get(0).getX()).setNy(weatherAreaCodes.get(0).getY()).setNumOfRows("10").setPageNo("1");
                    midLandFcstParameter.setNumOfRows("10").setPageNo("1").setRegId(midLandFcstRegId);
                    midTaParameter.setNumOfRows("10").setPageNo("1").setRegId(midTaRegId);

                    viewModel.getUltraSrtNcstData(vilageFcstParameter.deepCopy())
                            .getUltraSrtFcstData(vilageFcstParameter.deepCopy()).getVilageFcstData(vilageFcstParameter.deepCopy())
                            .getMidLandFcstData(midLandFcstParameter.deepCopy()).getMidTaData(midTaParameter.deepCopy());
                }
            }
        });

        //초단기실황
        viewModel.getUltraSrtNcstLiveData().observe(getViewLifecycleOwner(), new Observer<List<UltraSrtNcstItem>>()
        {
            @Override
            public void onChanged(List<UltraSrtNcstItem> ultraSrtNcstItems)
            {
                UltraSrtNcstData ultraSrtNcstData = new UltraSrtNcstData(ultraSrtNcstItems);
                Toast.makeText(getActivity(), ultraSrtNcstData.getTemperature(), Toast.LENGTH_SHORT).show();
            }
        });

        //초단기예보
        viewModel.getUltraSrtFcstLiveData().
                observe(getViewLifecycleOwner(), new Observer<List<UltraSrtFcstItem>>()

                {
                    @Override
                    public void onChanged(List<UltraSrtFcstItem> ultraSrtFcstItems)
                    {

                    }
                });

        //동네예보
        viewModel.getVilageFcstLiveData().
                observe(getViewLifecycleOwner(), new Observer<List<VilageFcstItem>>()

                {
                    @Override
                    public void onChanged(List<VilageFcstItem> vilageFcstItems)
                    {

                    }
                });

        //중기육상예보
        viewModel.getMidLandFcstLiveData().
                observe(getViewLifecycleOwner(), new Observer<List<MidLandFcstItem>>()

                {
                    @Override
                    public void onChanged(List<MidLandFcstItem> midLandFcstItems)
                    {

                    }
                });

        //중기기온
        viewModel.getMidTaLiveData().
                observe(getViewLifecycleOwner(), new Observer<List<MidTaItem>>()

                {
                    @Override
                    public void onChanged(List<MidTaItem> midTaItems)
                    {
                    }
                });
    }


}