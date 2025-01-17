package com.zerodsoft.calendarplatform.weather.aircondition;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.databinding.FragmentAirConditionBinding;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.aircondition.MsrstnAcctoRltmMesureDnsty.MsrstnAcctoRltmMesureDnstyItem;
import com.zerodsoft.calendarplatform.weather.aircondition.airconditionbar.AirConditionResult;
import com.zerodsoft.calendarplatform.weather.common.OnUpdateListener;
import com.zerodsoft.calendarplatform.weather.common.WeatherDataCallback;
import com.zerodsoft.calendarplatform.weather.dataprocessing.AirConditionProcessing;
import com.zerodsoft.calendarplatform.weather.aircondition.airconditionbar.BarInitDataCreater;
import com.zerodsoft.calendarplatform.weather.interfaces.CheckSuccess;
import com.zerodsoft.calendarplatform.weather.interfaces.LoadWeatherDataResultCallback;

public class AirConditionFragment extends Fragment implements OnUpdateListener, CheckSuccess {
	private FragmentAirConditionBinding binding;

	private final String LATITUDE;
	private final String LONGITUDE;
	private AirConditionProcessing airConditionProcessing;
	private LoadWeatherDataResultCallback loadWeatherDataResultCallback;

	public AirConditionFragment(String LATITUDE, String LONGITUDE, LoadWeatherDataResultCallback loadWeatherDataResultCallback) {
		this.LATITUDE = LATITUDE;
		this.LONGITUDE = LONGITUDE;
		this.loadWeatherDataResultCallback = loadWeatherDataResultCallback;
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
		binding.customProgressView.setContentView(binding.airConditionLayout);

		binding.finedustStatus.setText("");
		binding.ultraFinedustStatus.setText("");
		binding.showDetailDialogButton.setOnClickListener(onClickListener);

		loadWeatherDataResultCallback.onLoadStarted();
		binding.customProgressView.onStartedProcessingData();

		airConditionProcessing.getWeatherData(new WeatherDataCallback<AirConditionResult>() {
			@Override
			public void isSuccessful(AirConditionResult e) {
				if (getActivity() != null) {
					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							setData(e);
							binding.customProgressView.onSuccessfulProcessingData();
							loadWeatherDataResultCallback.onResult(true, null);
						}
					});
				}
			}

			@Override
			public void isFailure(Exception e) {
				if (getActivity() != null) {
					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							binding.customProgressView.onFailedProcessingData(getString(R.string.error));
							loadWeatherDataResultCallback.onResult(false, e);
						}
					});
				}
			}
		});
	}

	private void setData(AirConditionResult airConditionResult) {
		binding.stationAddress.setText(airConditionResult.getNearbyMsrstnListRoot().getResponse().getBody().getItems().get(0).getStationName());
		binding.finedustStatus.setText("");
		binding.ultraFinedustStatus.setText("");
		binding.fcstDateTime.setText("");

		MsrstnAcctoRltmMesureDnstyItem airConditionFinalData = airConditionResult.getAirConditionFinalData();
		if (airConditionFinalData == null) {
			binding.finedustStatus.setText(getString(R.string.not_data));
			binding.ultraFinedustStatus.setText(getString(R.string.not_data));
			binding.finedustStatus.setTextColor(Color.GRAY);
			binding.ultraFinedustStatus.setTextColor(Color.GRAY);

			binding.showDetailDialogButton.setClickable(false);
		} else {
			String pm10 = "";
			String pm25 = "";

			//pm10
			if (airConditionFinalData.getPm10Flag() == null) {
				pm10 = BarInitDataCreater.getGrade(airConditionFinalData.getPm10Grade1h(), getContext()) + ", " + airConditionFinalData.getPm10Value()
						+ getString(R.string.finedust_unit);
				binding.finedustStatus.setTextColor(BarInitDataCreater.getGradeColor(airConditionFinalData.getPm10Grade1h(), getContext()));
			} else {
				binding.finedustStatus.setTextColor(Color.GRAY);
				pm10 = airConditionFinalData.getPm10Flag();
			}

			//pm2.5
			if (airConditionFinalData.getPm25Flag() == null) {
				pm25 = BarInitDataCreater.getGrade(airConditionFinalData.getPm25Grade1h(), getContext()) + ", " + airConditionFinalData.getPm25Value()
						+ getString(R.string.finedust_unit);
				binding.ultraFinedustStatus.setTextColor(BarInitDataCreater.getGradeColor(airConditionFinalData.getPm25Grade1h(), getContext()));
			} else {
				binding.ultraFinedustStatus.setTextColor(Color.GRAY);
				pm25 = airConditionFinalData.getPm25Flag();
			}

			binding.finedustStatus.setText(pm10);
			binding.ultraFinedustStatus.setText(pm25);
			binding.fcstDateTime.setText(airConditionFinalData.getDataTime());
			binding.showDetailDialogButton.setClickable(true);
		}
	}

	public void refresh() {
		loadWeatherDataResultCallback.onLoadStarted();
		binding.customProgressView.onStartedProcessingData();

		airConditionProcessing.refresh(new WeatherDataCallback<AirConditionResult>() {
			@Override
			public void isSuccessful(AirConditionResult e) {
				if (getActivity() != null) {
					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							setData(e);
							binding.customProgressView.onSuccessfulProcessingData();
							loadWeatherDataResultCallback.onResult(true, null);
						}
					});
				}
			}

			@Override
			public void isFailure(Exception e) {
				if (getActivity() != null) {
					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							binding.customProgressView.onFailedProcessingData(getString(R.string.error));
							loadWeatherDataResultCallback.onResult(false, e);
						}
					});
				}
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
		airConditionProcessing.getWeatherData(new WeatherDataCallback<AirConditionResult>() {
			@Override
			public void isSuccessful(AirConditionResult e) {
				if (getActivity() != null) {
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							setData(e);
						}
					});
				}
			}

			@Override
			public void isFailure(Exception e) {
				if (getActivity() != null) {
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							binding.customProgressView.onFailedProcessingData(getString(R.string.error));
						}
					});
				}
			}
		});
	}

	@Override
	public boolean isSuccess() {
		return binding.customProgressView.isSuccess();
	}
}
