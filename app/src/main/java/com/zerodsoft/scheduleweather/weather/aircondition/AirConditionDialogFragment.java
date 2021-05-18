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
import com.zerodsoft.scheduleweather.room.dto.WeatherDataDTO;
import com.zerodsoft.scheduleweather.weather.aircondition.airconditionbar.AirConditionFinalData;
import com.zerodsoft.scheduleweather.weather.aircondition.airconditionbar.AirConditionResult;
import com.zerodsoft.scheduleweather.weather.aircondition.airconditionbar.BarInitDataCreater;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.MsrstnAcctoRltmMesureDnsty.MsrstnAcctoRltmMesureDnstyBody;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.NearbyMsrstnList.NearbyMsrstnListBody;
import com.zerodsoft.scheduleweather.weather.common.ViewProgress;
import com.zerodsoft.scheduleweather.weather.common.WeatherDataCallback;
import com.zerodsoft.scheduleweather.weather.dataprocessing.AirConditionProcessing;

public class AirConditionDialogFragment extends DialogFragment {
	public static final String TAG = "AirConditionDialogFragment";
	private DialogFragmentAirConditionBinding binding;
	private AirConditionProcessing airConditionProcessing;
	private String latitude;
	private String longitude;

	public AirConditionDialogFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getArguments();
		latitude = bundle.getString("latitude");
		longitude = bundle.getString("longitude");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = DialogFragmentAirConditionBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		binding.stationName.setText("");
		binding.pm25Status.setText("");
		binding.pm10Status.setText("");
		binding.no2Status.setText("");
		binding.coStatus.setText("");
		binding.o3Status.setText("");
		binding.so2Status.setText("");

		airConditionProcessing = new AirConditionProcessing(getContext(), latitude, longitude);


		airConditionProcessing.getWeatherData(new WeatherDataCallback<AirConditionResult>() {
			@Override
			public void isSuccessful(AirConditionResult e) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setData(e);
					}
				});
			}

			@Override
			public void isFailure(Exception e) {

			}
		});

	}

	@Override
	public void onResume() {
		super.onResume();

		WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
		layoutParams.width = (int) (layoutParams.width * 0.8);
		layoutParams.height = (int) (layoutParams.height * 0.7);

		getDialog().getWindow().setAttributes(layoutParams);
	}

	private void setData(AirConditionResult airConditionResult) {
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

		AirConditionFinalData airConditionFinalData = airConditionResult.getAirConditionFinalData();

		//측정소
		//	binding.stationName.setText(airConditionFinalData.getA() + " " + getString(R.string.station_name));

		//pm10
		if (airConditionFinalData.getPm10Flag() == null) {
			binding.finedustBar.setDataValue(Double.parseDouble(airConditionFinalData.getPm10Value()));
			binding.finedustBar.invalidate();
			pm10 = BarInitDataCreater.getGrade(airConditionFinalData.getPm10Grade1h(), getContext()) + ", " + airConditionFinalData.getPm10Value()
					+ getString(R.string.finedust_unit);
		} else {
			pm10 = airConditionFinalData.getPm10Flag();
		}

		//pm2.5
		if (airConditionFinalData.getPm25Flag() == null) {
			binding.ultraFinedustBar.setDataValue(Double.parseDouble(airConditionFinalData.getPm25Value()));
			binding.ultraFinedustBar.invalidate();
			pm25 = BarInitDataCreater.getGrade(airConditionFinalData.getPm25Grade1h(), getContext()) + ", " + airConditionFinalData.getPm25Value()
					+ getString(R.string.finedust_unit);
		} else {
			pm25 = airConditionFinalData.getPm25Flag();
		}

		//no2 이산화질소
		if (airConditionFinalData.getNo2Flag() == null) {
			binding.no2Bar.setDataValue(Double.parseDouble(airConditionFinalData.getNo2Value()));
			binding.no2Bar.invalidate();
			no2 = BarInitDataCreater.getGrade(airConditionFinalData.getNo2Grade(), getContext()) + ", " + airConditionFinalData.getNo2Value()
					+ getString(R.string.ppm);
		} else {
			no2 = airConditionFinalData.getNo2Flag();
		}

		//co 일산화탄소
		if (airConditionFinalData.getCoFlag() == null) {
			binding.coBar.setDataValue(Double.parseDouble(airConditionFinalData.getCoValue()));
			binding.coBar.invalidate();
			co = BarInitDataCreater.getGrade(airConditionFinalData.getCoGrade(), getContext()) + ", " + airConditionFinalData.getCoValue()
					+ getString(R.string.ppm);
		} else {
			co = airConditionFinalData.getCoFlag();
		}

		//so2 아황산가스
		if (airConditionFinalData.getSo2Flag() == null) {
			binding.so2Bar.setDataValue(Double.parseDouble(airConditionFinalData.getSo2Value()));
			binding.so2Bar.invalidate();
			so2 = BarInitDataCreater.getGrade(airConditionFinalData.getSo2Grade(), getContext()) + ", " + airConditionFinalData.getSo2Value()
					+ getString(R.string.ppm);
		} else {
			so2 = airConditionFinalData.getSo2Flag();
		}

		//o3 오존
		if (airConditionFinalData.getO3Flag() == null) {
			binding.o3Bar.setDataValue(Double.parseDouble(airConditionFinalData.getO3Value()));
			binding.o3Bar.invalidate();
			o3 = BarInitDataCreater.getGrade(airConditionFinalData.getO3Grade(), getContext()) + ", " + airConditionFinalData.getO3Value()
					+ getString(R.string.ppm);
		} else {
			o3 = airConditionFinalData.getO3Flag();
		}

		binding.pm10Status.setText(pm10);
		binding.pm25Status.setText(pm25);
		binding.no2Status.setText(no2);
		binding.so2Status.setText(so2);
		binding.coStatus.setText(co);
		binding.o3Status.setText(o3);
	}
}