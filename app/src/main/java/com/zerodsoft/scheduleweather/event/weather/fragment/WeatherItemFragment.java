package com.zerodsoft.scheduleweather.event.weather.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentWeatherItemBinding;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.room.dto.WeatherDataDTO;
import com.zerodsoft.scheduleweather.weather.aircondition.AirConditionFragment;
import com.zerodsoft.scheduleweather.weather.interfaces.OnDownloadedTimeListener;
import com.zerodsoft.scheduleweather.weather.mid.MidFcstFragment;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.weather.sunsetrise.SunSetRiseData;
import com.zerodsoft.scheduleweather.weather.ultrasrtfcst.UltraSrtFcstFragment;
import com.zerodsoft.scheduleweather.weather.ultrasrtncst.UltraSrtNcstFragment;
import com.zerodsoft.scheduleweather.weather.viewmodel.WeatherViewModel;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.utility.LonLat;
import com.zerodsoft.scheduleweather.utility.LonLatConverter;
import com.zerodsoft.scheduleweather.weather.vilagefcst.VilageFcstFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class WeatherItemFragment extends BottomSheetDialogFragment implements OnDownloadedTimeListener
{
    public static final String TAG = "WeatherItemFragment";

    private FragmentWeatherItemBinding binding;
    private LocationDTO locationDTO;

    //초단기 실황
    private UltraSrtNcstFragment ultraSrtNcstFragment;
    //초단기 예보
    private UltraSrtFcstFragment ultraSrtFcstFragment;
    //동네예보
    private VilageFcstFragment vilageFcstFragment;
    //중기 예보
    private MidFcstFragment midFcstFragment;
    //대기 상태
    private AirConditionFragment airConditionFragment;

    private WeatherViewModel weatherViewModel;
    private LocationViewModel locationViewModel;
    private List<SunSetRiseData> sunSetRiseList = new ArrayList<>();
    private WeatherAreaCodeDTO weatherAreaCode;

    private Integer calendarId;
    private Long eventId;
    private Long instanceId;
    private Long begin;

    private final int VIEW_HEIGHT;

    private static WeatherItemFragment instance;

    private BottomSheetBehavior bottomSheetBehavior;

    public static WeatherItemFragment newInstance(int viewHeight)
    {
        instance = new WeatherItemFragment(viewHeight);
        return instance;
    }

    public static WeatherItemFragment getInstance()
    {
        return instance;
    }

    public WeatherItemFragment(int VIEW_HEIGHT)
    {
        this.VIEW_HEIGHT = VIEW_HEIGHT;
    }

    @Override
    public void dismiss()
    {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog)
    {
        super.onDismiss(dialog);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        calendarId = bundle.getInt(CalendarContract.Instances.CALENDAR_ID);
        eventId = bundle.getLong(CalendarContract.Instances.EVENT_ID);
        instanceId = bundle.getLong(CalendarContract.Instances._ID);
        begin = bundle.getLong(CalendarContract.Instances.BEGIN);

        /*
         isEditing = savedInstanceState?.getBoolean(IS_EDITING_KEY, false)
    randomGoodDeed = savedInstanceState?.getString(RANDOM_GOOD_DEED_KEY)
            ?: viewModel.generateRandomGoodDeed()
         */
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
        /*
            outState.putBoolean(IS_EDITING_KEY, isEditing)
    outState.putString(RANDOM_GOOD_DEED_KEY, randomGoodDeed)
         */
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        bottomSheetBehavior = ((BottomSheetDialog) dialog).getBehavior();
        bottomSheetBehavior.setDraggable(false);
        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        return dialog;
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

        View bottomSheet = getDialog().findViewById(R.id.design_bottom_sheet);
        bottomSheet.getLayoutParams().height = VIEW_HEIGHT;

        binding.refreshWeatherFab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ultraSrtNcstFragment.getWeatherData();
                ultraSrtFcstFragment.getWeatherData();
                vilageFcstFragment.getWeatherData();
                midFcstFragment.getWeatherData();
                airConditionFragment.refresh();
            }
        });

        binding.scrollview.setOnScrollChangeListener(new View.OnScrollChangeListener()
        {
            @Override
            public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY)
            {
                if (scrollY - oldScrollY > 0)
                {
                    // 아래로 스크롤
                    binding.refreshWeatherFab.setVisibility(View.GONE);
                } else if (scrollY - oldScrollY < 0)
                {
                    // 위로 스크롤
                    binding.refreshWeatherFab.setVisibility(View.VISIBLE);
                }
            }
        });

        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        locationViewModel.getLocation(calendarId, eventId, new CarrierMessagingService.ResultCallback<LocationDTO>()
        {
            @Override
            public void onReceiveResult(@NonNull LocationDTO selectedLocationDTO) throws RemoteException
            {
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        locationDTO = selectedLocationDTO;
                        final LonLat lonLat = LonLatConverter.convertGrid(selectedLocationDTO.getLongitude(), selectedLocationDTO.getLatitude());

                        weatherViewModel.init(getContext(), lonLat);
                        weatherViewModel.getAreaCodeLiveData().observe(getViewLifecycleOwner(), new Observer<List<WeatherAreaCodeDTO>>()
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
                                        distance = Math.sqrt(Math.pow(selectedLocationDTO.getLongitude() - locationPoints.get(i).longitude, 2) + Math.pow(selectedLocationDTO.getLatitude() - locationPoints.get(i).latitude, 2));
                                        if (distance < minDistance)
                                        {
                                            minDistance = distance;
                                            index = i;
                                        }
                                    }
                                    // regId설정하는 코드 작성
                                    weatherAreaCode = weatherAreaCodes.get(index);

                                    refreshWeatherData();
                                    createFragments();
                                }
                            }
                        });
                    }
                });

            }
        });


    }

    private void createFragments()
    {
        ultraSrtNcstFragment = new UltraSrtNcstFragment(weatherAreaCode, this);
        ultraSrtFcstFragment = new UltraSrtFcstFragment(weatherAreaCode, sunSetRiseList, this);
        vilageFcstFragment = new VilageFcstFragment(weatherAreaCode, sunSetRiseList, this);
        midFcstFragment = new MidFcstFragment(weatherAreaCode, this);
        airConditionFragment = new AirConditionFragment(locationDTO.getLatitude(), locationDTO.getLongitude(), this);

        getChildFragmentManager().beginTransaction()
                .add(binding.ultraSrtNcstFragmentContainer.getId(), ultraSrtNcstFragment, "0")
                .add(binding.ultraSrtFcstFragmentContainer.getId(), ultraSrtFcstFragment, "1")
                .add(binding.vilageFcstFragmentContainer.getId(), vilageFcstFragment, "2")
                .add(binding.midFcstFragmentContainer.getId(), midFcstFragment, "3")
                .add(binding.airConditionFragmentContainer.getId(), airConditionFragment, "4")
                .commit();
    }

    private void refreshWeatherData()
    {
        String addressName = weatherAreaCode.getPhase1() + " " + weatherAreaCode.getPhase2() + " " + weatherAreaCode.getPhase3();
        binding.addressName.setText(addressName);
        init();
    }

    private void init()
    {
        //동네예보 마지막 날 까지의 일몰/일출 시간 데이터를 구성
        sunSetRiseList.clear();

        List<Calendar> dates = new ArrayList<>();

        Calendar endDate = Calendar.getInstance();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate.getTime());
        endDate.add(Calendar.DAY_OF_YEAR, 2);


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

        SunriseSunsetCalculator sunriseSunsetCalculator = new SunriseSunsetCalculator(new Location(weatherAreaCode.getLatitudeSecondsDivide100(),
                weatherAreaCode.getLongitudeSecondsDivide100()), ClockUtil.TIME_ZONE);
        Calendar sunRise = null;
        Calendar sunSet = null;

        for (Calendar date : dates)
        {
            sunRise = sunriseSunsetCalculator.getOfficialSunriseCalendarForDate(date);
            sunSet = sunriseSunsetCalculator.getOfficialSunsetCalendarForDate(date);
            sunSetRiseList.add(new SunSetRiseData(date.getTime(), sunRise.getTime(), sunSet.getTime()));
        }
    }

    @Override
    public void setDownloadedTime(Date downloadedTime, int dataType)
    {
        String dateTimeStr = ClockUtil.DB_DATE_FORMAT.format(downloadedTime);

        switch (dataType)
        {
            case WeatherDataDTO.ULTRA_SRT_NCST:
                binding.ultraSrtNcstDownloadedTime.setText(dateTimeStr);
                break;
            case WeatherDataDTO.ULTRA_SRT_FCST:
                binding.ultraSrtFcstDownloadedTime.setText(dateTimeStr);
                break;
            case WeatherDataDTO.MID_LAND_FCST:
                binding.midLandFcstDownloadedTime.setText(dateTimeStr);
                break;
            case WeatherDataDTO.MID_TA:
                binding.midTaDownloadedTime.setText(dateTimeStr);
                break;
            case WeatherDataDTO.AIR_CONDITION:
                binding.airConditionDownloadedTime.setText(dateTimeStr);
                break;
            case WeatherDataDTO.VILAGE_FCST:
                binding.vilageFcstDownloadedTime.setText(dateTimeStr);
                break;
        }
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
