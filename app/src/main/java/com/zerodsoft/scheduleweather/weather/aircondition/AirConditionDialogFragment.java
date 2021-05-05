package com.zerodsoft.scheduleweather.weather.aircondition;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.DialogFragmentAirConditionBinding;
import com.zerodsoft.scheduleweather.weather.view.airconditionbar.BarInitDataCreater;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.MsrstnAcctoRltmMesureDnsty.MsrstnAcctoRltmMesureDnstyBody;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.NearbyMsrstnList.NearbyMsrstnListBody;

public class AirConditionDialogFragment extends DialogFragment
{
    private DialogFragmentAirConditionBinding binding;
    private MsrstnAcctoRltmMesureDnstyBody msrstnAcctoRltmMesureDnstyBody;
    private NearbyMsrstnListBody nearbyMsrstnListBody;

    public AirConditionDialogFragment()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        msrstnAcctoRltmMesureDnstyBody = bundle.getParcelable("msrstnAcctoRltmMesureDnstyBody");
        nearbyMsrstnListBody = bundle.getParcelable("nearbyMsrstnListBody");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = DialogFragmentAirConditionBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        binding.stationName.setText("");
        binding.pm25Status.setText("");
        binding.pm10Status.setText("");
        binding.no2Status.setText("");
        binding.coStatus.setText("");
        binding.o3Status.setText("");
        binding.so2Status.setText("");

        setData();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.width = (int) (layoutParams.width * 0.8);
        layoutParams.height = (int) (layoutParams.height * 0.7);

        getDialog().getWindow().setAttributes(layoutParams);
    }

    private void setData()
    {
        binding.stationName.setText("");
        binding.pm25Status.setText("");
        binding.pm10Status.setText("");
        binding.no2Status.setText("");
        binding.coStatus.setText("");
        binding.o3Status.setText("");
        binding.so2Status.setText("");

        String pm10 = "";
        String pm25 = "";
        String o3 = "";
        String so2 = "";
        String co = "";
        String no2 = "";

        //측정소
        binding.stationName.setText(nearbyMsrstnListBody.getItems().get(0).getAddr() + " " + getString(R.string.station_name));

        //pm10
        if (msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getPm10Flag() == null)
        {
            binding.finedustBar.setDataValue(Double.parseDouble(msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getPm10Value()));
            binding.finedustBar.invalidate();
            pm10 = BarInitDataCreater.getGrade(msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getPm10Grade1h(), getContext()) + ", " + msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getPm10Value()
                    + getString(R.string.finedust_unit);
        } else
        {
            pm10 = msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getPm10Flag();
        }

        //pm2.5
        if (msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getPm25Flag() == null)
        {
            binding.ultraFinedustBar.setDataValue(Double.parseDouble(msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getPm25Value()));
            binding.ultraFinedustBar.invalidate();
            pm25 = BarInitDataCreater.getGrade(msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getPm25Grade1h(), getContext()) + ", " + msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getPm25Value()
                    + getString(R.string.finedust_unit);
        } else
        {
            pm25 = msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getPm25Flag();
        }

        //no2 이산화질소
        if (msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getNo2Flag() == null)
        {
            binding.no2Bar.setDataValue(Double.parseDouble(msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getNo2Value()));
            binding.no2Bar.invalidate();
            no2 = BarInitDataCreater.getGrade(msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getNo2Grade(), getContext()) + ", " + msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getNo2Value()
                    + getString(R.string.ppm);
        } else
        {
            no2 = msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getNo2Flag();
        }

        //co 일산화탄소
        if (msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getCoFlag() == null)
        {
            binding.coBar.setDataValue(Double.parseDouble(msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getCoValue()));
            binding.coBar.invalidate();
            co = BarInitDataCreater.getGrade(msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getCoGrade(), getContext()) + ", " + msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getCoValue()
                    + getString(R.string.ppm);
        } else
        {
            co = msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getCoFlag();
        }

        //so2 아황산가스
        if (msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getSo2Flag() == null)
        {
            binding.so2Bar.setDataValue(Double.parseDouble(msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getSo2Value()));
            binding.so2Bar.invalidate();
            so2 = BarInitDataCreater.getGrade(msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getSo2Grade(), getContext()) + ", " + msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getSo2Value()
                    + getString(R.string.ppm);
        } else
        {
            so2 = msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getSo2Flag();
        }

        //o3 오존
        if (msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getO3Flag() == null)
        {
            binding.o3Bar.setDataValue(Double.parseDouble(msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getO3Value()));
            binding.o3Bar.invalidate();
            o3 = BarInitDataCreater.getGrade(msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getO3Grade(), getContext()) + ", " + msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getO3Value()
                    + getString(R.string.ppm);
        } else
        {
            o3 = msrstnAcctoRltmMesureDnstyBody.getItem().get(0).getO3Flag();
        }


        binding.pm10Status.setText(pm10);
        binding.pm25Status.setText(pm25);
        binding.no2Status.setText(no2);
        binding.so2Status.setText(so2);
        binding.coStatus.setText(co);
        binding.o3Status.setText(o3);
    }
}