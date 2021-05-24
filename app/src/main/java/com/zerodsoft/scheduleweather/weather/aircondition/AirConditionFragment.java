package com.zerodsoft.scheduleweather.weather.aircondition;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentAirConditionBinding;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.MsrstnAcctoRltmMesureDnsty.MsrstnAcctoRltmMesureDnstyItem;
import com.zerodsoft.scheduleweather.room.dto.WeatherDataDTO;
import com.zerodsoft.scheduleweather.weather.aircondition.airconditionbar.AirConditionResult;
import com.zerodsoft.scheduleweather.weather.common.OnUpdateListener;
import com.zerodsoft.scheduleweather.weather.common.ViewProgress;
import com.zerodsoft.scheduleweather.weather.common.WeatherDataCallback;
import com.zerodsoft.scheduleweather.weather.dataprocessing.AirConditionProcessing;
import com.zerodsoft.scheduleweather.weather.interfaces.OnDownloadedTimeListener;
import com.zerodsoft.scheduleweather.weather.aircondition.airconditionbar.BarInitDataCreater;

public class AirConditionFragment extends Fragment implements OnUpdateListener {
	private FragmentAirConditionBinding binding;

	private final String LATITUDE;
	private final String LONGITUDE;
	private final OnDownloadedTimeListener onDownloadedTimeListener;

	private ViewProgress viewProgress;
	private AirConditionProcessing airConditionProcessing;

	public AirConditionFragment(String LATITUDE, String LONGITUDE, OnDownloadedTimeListener onDownloadedTimeListener) {
		this.LATITUDE = LATITUDE;
		this.LONGITUDE = LONGITUDE;
		this.onDownloadedTimeListener = onDownloadedTimeListener;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		airConditionProcessing = new AirConditionProcessing(getContext(), LATITUDE, LONGITUDE);
	}
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = FragmentAirConditionBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		binding.finedustStatus.setText("");
		binding.ultraFinedustStatus.setText("");
		binding.showDetailDialogButton.setOnClickListener(onClickListener);

		viewProgress = new ViewProgress(binding.airConditionLayout, binding.weatherProgressLayout.progressBar,
				binding.weatherProgressLayout.errorTextview, binding.weatherProgressLayout.getRoot());

		init();
	}

	private void init() {
		viewProgress.onStartedProcessingData();

		airConditionProcessing.getWeatherData(new WeatherDataCallback<AirConditionResult>() {
			@Override
			public void isSuccessful(AirConditionResult e) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						onDownloadedTimeListener.setDownloadedTime(e.getDownloadedDate(), WeatherDataDTO.AIR_CONDITION);
						viewProgress.onCompletedProcessingData(true);
						setData(e);
					}
				});
			}

			@Override
			public void isFailure(Exception e) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						onDownloadedTimeListener.setDownloadedTime(null, WeatherDataDTO.AIR_CONDITION);
						viewProgress.onCompletedProcessingData(false, e.getMessage());
					}
				});
			}
		});
	}


	private void setData(AirConditionResult airConditionResult) {
		binding.finedustStatus.setText("");
		binding.ultraFinedustStatus.setText("");

		String pm10 = "";
		String pm25 = "";

		MsrstnAcctoRltmMesureDnstyItem airConditionFinalData = airConditionResult.getAirConditionFinalData();

		//pm10
		if (airConditionFinalData.getPm10Flag() == null) {
			pm10 = BarInitDataCreater.getGrade(airConditionFinalData.getPm10Grade1h(), getContext()) + ", " + airConditionFinalData.getPm10Value()
					+ getString(R.string.finedust_unit);
			binding.finedustStatus.setTextColor(BarInitDataCreater.getGradeColor(airConditionFinalData.getPm10Grade1h(), getContext()));
		} else {
			pm10 = airConditionFinalData.getPm10Flag();
		}

		//pm2.5
		if (airConditionFinalData.getPm25Flag() == null) {
			pm25 = BarInitDataCreater.getGrade(airConditionFinalData.getPm25Grade1h(), getContext()) + ", " + airConditionFinalData.getPm25Value()
					+ getString(R.string.finedust_unit);
			binding.ultraFinedustStatus.setTextColor(BarInitDataCreater.getGradeColor(airConditionFinalData.getPm25Grade1h(), getContext()));
		} else {
			pm25 = airConditionFinalData.getPm25Flag();
		}

		binding.finedustStatus.setText(pm10);
		binding.ultraFinedustStatus.setText(pm25);
	}

	public void refresh() {
		viewProgress.onStartedProcessingData();
		airConditionProcessing.refresh(new WeatherDataCallback<AirConditionResult>() {
			@Override
			public void isSuccessful(AirConditionResult e) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						onDownloadedTimeListener.setDownloadedTime(e.getDownloadedDate(), WeatherDataDTO.AIR_CONDITION);
						viewProgress.onCompletedProcessingData(true);
						setData(e);
					}
				});
			}

			@Override
			public void isFailure(Exception e) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						onDownloadedTimeListener.setDownloadedTime(null, WeatherDataDTO.AIR_CONDITION);
						viewProgress.onCompletedProcessingData(false, e.getMessage());
					}
				});
			}
		});

	}

	private final View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			AirConditionDialogFragment airConditionDialogFragment = new AirConditionDialogFragment(AirConditionFragment.this);

			Bundle bundle = new Bundle();
			bundle.putString("latitude", LATITUDE);
			bundle.putString("longitude", LONGITUDE);
			airConditionDialogFragment.setArguments(bundle);
			airConditionDialogFragment.show(getChildFragmentManager(), AirConditionDialogFragment.TAG);
		}
	};

	@Override
	public void onUpdatedData() {
		init();
	}
}
