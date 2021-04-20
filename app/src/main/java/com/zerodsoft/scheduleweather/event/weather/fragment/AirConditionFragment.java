package com.zerodsoft.scheduleweather.event.weather.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentAirConditionBinding;
import com.zerodsoft.scheduleweather.event.weather.repository.AirConditionDownloader;
import com.zerodsoft.scheduleweather.event.weather.repository.FindAirConditionStationDownloader;
import com.zerodsoft.scheduleweather.event.weather.repository.SgisTranscoord;
import com.zerodsoft.scheduleweather.event.weather.view.airconditionbar.BarInitDataCreater;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.paremeters.MsrstnAcctoRltmMesureDnstyParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.NearbyMsrstnListParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.SgisAuthParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.TransCoordParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.AirConditionRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.CtprvnRltmMesureDnsty.CtprvnRltmMesureDnstyBody;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.FindStationRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.MsrstnAcctoRltmMesureDnsty.MsrstnAcctoRltmMesureDnstyBody;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.MsrstnAcctoRltmMesureDnsty.MsrstnAcctoRltmMesureDnstyRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.NearbyMsrstnList.NearbyMsrstnListBody;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.NearbyMsrstnList.NearbyMsrstnListRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.SgisRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.auth.SgisAuthResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.auth.SgisAuthResult;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.transcoord.TransCoordResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.transcoord.TransCoordResult;
import com.zerodsoft.scheduleweather.sgis.SgisAuth;

public class AirConditionFragment extends Fragment
{
    private FragmentAirConditionBinding binding;

    private double latitude;
    private double longitude;
    private MsrstnAcctoRltmMesureDnstyParameter msrstnAcctoRltmMesureDnstyParameter;
    private MsrstnAcctoRltmMesureDnstyBody msrstnAcctoRltmMesureDnstyBody;
    private NearbyMsrstnListBody nearbyMsrstnListBody;

    private final AirConditionDownloader airConditionDownloader = new AirConditionDownloader()
    {
        @Override
        public void onResponseSuccessful(AirConditionRoot result)
        {
            if (result instanceof MsrstnAcctoRltmMesureDnstyRoot)
            {
                msrstnAcctoRltmMesureDnstyBody = ((MsrstnAcctoRltmMesureDnstyRoot) result).getResponse().getBody();

                        setData(msrstnAcctoRltmMesureDnstyBody);

            } else if (result instanceof CtprvnRltmMesureDnstyBody)
            {
            }

        }

        @Override
        public void onResponseFailed(Exception e)
        {

        }
    };

    private final FindAirConditionStationDownloader findAirConditionStationDownloader = new FindAirConditionStationDownloader()
    {
        @Override
        public void onResponseSuccessful(FindStationRoot result)
        {
            if (result instanceof NearbyMsrstnListRoot)
            {
                nearbyMsrstnListBody = ((NearbyMsrstnListRoot) result).getResponse().getBody();

                msrstnAcctoRltmMesureDnstyParameter = new MsrstnAcctoRltmMesureDnstyParameter();
                msrstnAcctoRltmMesureDnstyParameter.setDataTerm(MsrstnAcctoRltmMesureDnstyParameter.DATATERM_DAILY);
                msrstnAcctoRltmMesureDnstyParameter.setStationName(nearbyMsrstnListBody.getItems().get(0).getStationName());

                airConditionDownloader.getMsrstnAcctoRltmMesureDnsty(msrstnAcctoRltmMesureDnstyParameter);
            }
        }

        @Override
        public void onResponseFailed(Exception e)
        {

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
            parameter.setPosX(String.valueOf(longitude));
            parameter.setPosY(String.valueOf(latitude));

            sgisTranscoord.transcoord(parameter);
        }

        @Override
        public void onResponseFailed(Exception e)
        {

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


    @Override
    public void setArguments(@Nullable Bundle args)
    {
        super.setArguments(args);
        latitude = args.getDouble("latitude");
        longitude = args.getDouble("longitude");
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
        if (SgisAuth.getSgisAuthResponse() == null)
        {
            sgisAuth.auth();
        } else
        {
            TransCoordParameter parameter = new TransCoordParameter();
            parameter.setAccessToken(SgisAuth.getSgisAuthResponse().getResult().getAccessToken());
            parameter.setSrc(TransCoordParameter.WGS84);
            parameter.setDst(TransCoordParameter.JUNGBU_ORIGIN);
            parameter.setPosX(String.valueOf(longitude));
            parameter.setPosY(String.valueOf(latitude));

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
