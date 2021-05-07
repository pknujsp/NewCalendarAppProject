package com.zerodsoft.scheduleweather.weather.aircondition;

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
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentAirConditionBinding;
import com.zerodsoft.scheduleweather.room.dto.WeatherDataDTO;
import com.zerodsoft.scheduleweather.weather.common.ViewProgress;
import com.zerodsoft.scheduleweather.weather.interfaces.OnDownloadedTimeListener;
import com.zerodsoft.scheduleweather.weather.repository.AirConditionDownloader;
import com.zerodsoft.scheduleweather.weather.repository.FindAirConditionStationDownloader;
import com.zerodsoft.scheduleweather.weather.repository.SgisTranscoord;
import com.zerodsoft.scheduleweather.weather.view.airconditionbar.BarInitDataCreater;
import com.zerodsoft.scheduleweather.retrofit.paremeters.MsrstnAcctoRltmMesureDnstyParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.NearbyMsrstnListParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.TransCoordParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.MsrstnAcctoRltmMesureDnsty.MsrstnAcctoRltmMesureDnstyBody;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.MsrstnAcctoRltmMesureDnsty.MsrstnAcctoRltmMesureDnstyRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.NearbyMsrstnList.NearbyMsrstnListBody;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.NearbyMsrstnList.NearbyMsrstnListRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.auth.SgisAuthResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.transcoord.TransCoordResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.transcoord.TransCoordResult;
import com.zerodsoft.scheduleweather.sgis.SgisAuth;
import com.zerodsoft.scheduleweather.weather.viewmodel.WeatherDbViewModel;

import java.util.Date;

public class AirConditionFragment extends Fragment
{
    private FragmentAirConditionBinding binding;

    private final double LATITUDE;
    private final double LONGITUDE;
    private final OnDownloadedTimeListener onDownloadedTimeListener;

    private MsrstnAcctoRltmMesureDnstyParameter msrstnAcctoRltmMesureDnstyParameter;
    private MsrstnAcctoRltmMesureDnstyBody msrstnAcctoRltmMesureDnstyBody;
    private NearbyMsrstnListBody nearbyMsrstnListBody;

    private WeatherDbViewModel weatherDbViewModel;
    private ViewProgress viewProgress;

    public AirConditionFragment(double LATITUDE, double LONGITUDE, OnDownloadedTimeListener onDownloadedTimeListener)
    {
        this.LATITUDE = LATITUDE;
        this.LONGITUDE = LONGITUDE;
        this.onDownloadedTimeListener = onDownloadedTimeListener;
    }

    private final AirConditionDownloader airConditionDownloader = new AirConditionDownloader()
    {
        @Override
        public void onResponseSuccessful(JsonObject result)
        {
            Gson gson = new Gson();
            MsrstnAcctoRltmMesureDnstyRoot root = gson.fromJson(result.toString(), MsrstnAcctoRltmMesureDnstyRoot.class);
            msrstnAcctoRltmMesureDnstyBody = root.getResponse().getBody();
            setData(msrstnAcctoRltmMesureDnstyBody);

            WeatherDataDTO weatherDataDTO = new WeatherDataDTO();
            weatherDataDTO.setLatitude(String.valueOf(LATITUDE));
            weatherDataDTO.setLongitude(String.valueOf(LONGITUDE));
            weatherDataDTO.setDataType(WeatherDataDTO.AIR_CONDITION);
            weatherDataDTO.setJson(result.toString());
            weatherDataDTO.setDownloadedDate(String.valueOf(System.currentTimeMillis()));

            weatherDbViewModel.contains(weatherDataDTO.getLatitude(), weatherDataDTO.getLongitude(), WeatherDataDTO.AIR_CONDITION, new CarrierMessagingService.ResultCallback<Boolean>()
            {
                @Override
                public void onReceiveResult(@NonNull Boolean isContains) throws RemoteException
                {
                    if (isContains)
                    {
                        weatherDbViewModel.update(weatherDataDTO.getLatitude(), weatherDataDTO.getLongitude(), WeatherDataDTO.AIR_CONDITION
                                , weatherDataDTO.getJson(), weatherDataDTO.getDownloadedDate(), new CarrierMessagingService.ResultCallback<Boolean>()
                                {
                                    @Override
                                    public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException
                                    {

                                    }
                                });
                    } else
                    {
                        weatherDbViewModel.insert(weatherDataDTO, new CarrierMessagingService.ResultCallback<WeatherDataDTO>()
                        {
                            @Override
                            public void onReceiveResult(@NonNull WeatherDataDTO weatherDataDTO) throws RemoteException
                            {

                            }
                        });

                    }
                }
            });

            onDownloadedTimeListener.setDownloadedTime(new Date(Long.parseLong(weatherDataDTO.getDownloadedDate())), WeatherDataDTO.AIR_CONDITION);
            viewProgress.onCompletedProcessingData(true);
        }

        @Override
        public void onResponseFailed(Exception e)
        {
            requireActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    viewProgress.onCompletedProcessingData(false);
                    Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private final FindAirConditionStationDownloader findAirConditionStationDownloader = new FindAirConditionStationDownloader()
    {
        @Override
        public void onResponseSuccessful(JsonObject result)
        {
            Gson gson = new Gson();
            NearbyMsrstnListRoot root = gson.fromJson(result.toString(), NearbyMsrstnListRoot.class);
            nearbyMsrstnListBody = root.getResponse().getBody();

            WeatherDataDTO weatherDataDTO = new WeatherDataDTO();
            weatherDataDTO.setLatitude(String.valueOf(LATITUDE));
            weatherDataDTO.setLongitude(String.valueOf(LONGITUDE));
            weatherDataDTO.setDataType(WeatherDataDTO.NEAR_BY_MSRSTN_LIST);
            weatherDataDTO.setJson(result.toString());
            weatherDataDTO.setDownloadedDate(String.valueOf(System.currentTimeMillis()));

            weatherDbViewModel.contains(String.valueOf(LATITUDE), String.valueOf(LONGITUDE), WeatherDataDTO.NEAR_BY_MSRSTN_LIST, new CarrierMessagingService.ResultCallback<Boolean>()
            {
                @Override
                public void onReceiveResult(@NonNull Boolean isContains) throws RemoteException
                {
                    if (isContains)
                    {
                        weatherDbViewModel.update(String.valueOf(LATITUDE), String.valueOf(LONGITUDE), WeatherDataDTO.NEAR_BY_MSRSTN_LIST
                                , result.toString(), weatherDataDTO.getDownloadedDate(), new CarrierMessagingService.ResultCallback<Boolean>()
                                {
                                    @Override
                                    public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException
                                    {

                                    }
                                });
                    } else
                    {
                        weatherDbViewModel.insert(weatherDataDTO, new CarrierMessagingService.ResultCallback<WeatherDataDTO>()
                        {
                            @Override
                            public void onReceiveResult(@NonNull WeatherDataDTO weatherDataDTO) throws RemoteException
                            {

                            }
                        });
                    }
                }
            });

            msrstnAcctoRltmMesureDnstyParameter = new MsrstnAcctoRltmMesureDnstyParameter();
            msrstnAcctoRltmMesureDnstyParameter.setDataTerm(MsrstnAcctoRltmMesureDnstyParameter.DATATERM_DAILY);
            msrstnAcctoRltmMesureDnstyParameter.setStationName(nearbyMsrstnListBody.getItems().get(0).getStationName());

            airConditionDownloader.getMsrstnAcctoRltmMesureDnsty(msrstnAcctoRltmMesureDnstyParameter);
        }

        @Override
        public void onResponseFailed(Exception e)
        {
            requireActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    viewProgress.onCompletedProcessingData(false);
                    Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private final SgisAuth sgisAuth = new SgisAuth()
    {
        @Override
        public void onResponseSuccessful(SgisAuthResponse result)
        {
            SgisAuth.setSgisAuthResponse(result);

            TransCoordParameter parameter = new TransCoordParameter();
            parameter.setAccessToken(result.getResult().getAccessToken());
            parameter.setSrc(TransCoordParameter.WGS84);
            parameter.setDst(TransCoordParameter.JUNGBU_ORIGIN);
            parameter.setPosX(String.valueOf(LONGITUDE));
            parameter.setPosY(String.valueOf(LATITUDE));

            sgisTranscoord.transcoord(parameter);
        }

        @Override
        public void onResponseFailed(Exception e)
        {
            requireActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private final SgisTranscoord sgisTranscoord = new SgisTranscoord()
    {
        @Override
        public void onResponseSuccessful(TransCoordResponse result)
        {
            TransCoordResult transCoordResult = result.getResult();
            NearbyMsrstnListParameter parameter = new NearbyMsrstnListParameter();
            parameter.setTmX(transCoordResult.getPosX());
            parameter.setTmY(transCoordResult.getPosY());

            findAirConditionStationDownloader.getNearbyMsrstnList(parameter);
        }

        @Override
        public void onResponseFailed(Exception e)
        {

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = FragmentAirConditionBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        binding.finedustStatus.setText("");
        binding.ultraFinedustStatus.setText("");
        binding.showDetailDialogButton.setOnClickListener(onClickListener);
        viewProgress = new ViewProgress(binding.airConditionLayout, binding.weatherProgressLayout.progressBar, binding.weatherProgressLayout.errorTextview);
        viewProgress.onStartedProcessingData();

        weatherDbViewModel = new ViewModelProvider(this).get(WeatherDbViewModel.class);

        weatherDbViewModel.getWeatherData(String.valueOf(LATITUDE), String.valueOf(LONGITUDE), WeatherDataDTO.NEAR_BY_MSRSTN_LIST, new CarrierMessagingService.ResultCallback<WeatherDataDTO>()
        {
            @Override
            public void onReceiveResult(@NonNull WeatherDataDTO weatherDataDTO) throws RemoteException
            {
                if (weatherDataDTO == null)
                {

                } else
                {
                    Gson gson = new Gson();
                    NearbyMsrstnListRoot root = gson.fromJson(weatherDataDTO.getJson(), NearbyMsrstnListRoot.class);
                    nearbyMsrstnListBody = root.getResponse().getBody();
                }
            }
        });

        weatherDbViewModel.getWeatherData(String.valueOf(LATITUDE), String.valueOf(LONGITUDE), WeatherDataDTO.AIR_CONDITION, new CarrierMessagingService.ResultCallback<WeatherDataDTO>()
        {
            @Override
            public void onReceiveResult(@NonNull WeatherDataDTO weatherDataDTO) throws RemoteException
            {
                requireActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (weatherDataDTO == null)
                        {
                            refresh();
                        } else
                        {
                            Gson gson = new Gson();
                            MsrstnAcctoRltmMesureDnstyRoot root = gson.fromJson(weatherDataDTO.getJson(), MsrstnAcctoRltmMesureDnstyRoot.class);
                            msrstnAcctoRltmMesureDnstyBody = root.getResponse().getBody();
                            setData(msrstnAcctoRltmMesureDnstyBody);
                            onDownloadedTimeListener.setDownloadedTime(new Date(Long.parseLong(weatherDataDTO.getDownloadedDate())), WeatherDataDTO.AIR_CONDITION);
                            viewProgress.onCompletedProcessingData(true);
                        }
                    }
                });
            }
        });
    }


    private void setData(MsrstnAcctoRltmMesureDnstyBody msrstnAcctoRltmMesureDnstyBody)
    {
        binding.finedustStatus.setText("");
        binding.ultraFinedustStatus.setText("");

        String pm10 = "";
        String pm25 = "";

        //pm10
        if (msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getPm10Flag() == null)
        {
            pm10 = BarInitDataCreater.getGrade(msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getPm10Grade1h(), getContext()) + ", " + msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getPm10Value()
                    + getString(R.string.finedust_unit);
            binding.finedustStatus.setTextColor(BarInitDataCreater.getGradeColor(msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getPm10Grade1h(), getContext()));
        } else
        {
            pm10 = msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getPm10Flag();
        }

        //pm2.5
        if (msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getPm25Flag() == null)
        {
            pm25 = BarInitDataCreater.getGrade(msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getPm25Grade1h(), getContext()) + ", " + msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getPm25Value()
                    + getString(R.string.finedust_unit);
            binding.ultraFinedustStatus.setTextColor(BarInitDataCreater.getGradeColor(msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getPm25Grade1h(), getContext()));
        } else
        {
            pm25 = msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getPm25Flag();
        }

        binding.finedustStatus.setText(pm10);
        binding.ultraFinedustStatus.setText(pm25);
    }

    public void refresh()
    {
        viewProgress.onStartedProcessingData();
        if (SgisAuth.getSgisAuthResponse() == null)
        {
            sgisAuth.auth();
        } else
        {
            TransCoordParameter parameter = new TransCoordParameter();
            parameter.setAccessToken(SgisAuth.getSgisAuthResponse().getResult().getAccessToken());
            parameter.setSrc(TransCoordParameter.WGS84);
            parameter.setDst(TransCoordParameter.JUNGBU_ORIGIN);
            parameter.setPosX(String.valueOf(LONGITUDE));
            parameter.setPosY(String.valueOf(LATITUDE));

            sgisTranscoord.transcoord(parameter);
        }
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            AirConditionDialogFragment airConditionDialogFragment = new AirConditionDialogFragment();

            Bundle bundle = new Bundle();
            bundle.putParcelable("msrstnAcctoRltmMesureDnstyBody", msrstnAcctoRltmMesureDnstyBody);
            bundle.putParcelable("nearbyMsrstnListBody", nearbyMsrstnListBody);
            airConditionDialogFragment.setArguments(bundle);

            airConditionDialogFragment.show(getChildFragmentManager(), "TAG");
        }
    };
}
