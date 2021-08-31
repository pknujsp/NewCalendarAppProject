package com.zerodsoft.scheduleweather.weather.aircondition;

import android.graphics.Color;
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
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.weather.aircondition.airconditionbar.AirConditionResult;
import com.zerodsoft.scheduleweather.weather.common.OnUpdateListener;
import com.zerodsoft.scheduleweather.weather.common.WeatherDataCallback;
import com.zerodsoft.scheduleweather.weather.dataprocessing.AirConditionProcessing;
import com.zerodsoft.scheduleweather.weather.aircondition.airconditionbar.BarInitDataCreater;

import java.util.Date;

public class AirConditionFragment extends Fragment implements OnUpdateListener {
	private FragmentAirConditionBinding binding;

	private final String LATITUDE;
	private final String LONGITUDE;
	private AirConditionProcessing airConditionProcessing;

	public AirConditionFragment(String LATITUDE, String LONGITUDE) {
		this.LATITUDE = LATITUDE;
		this.LONGITUDE = LONGITUDE;
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

		init();
	}

	private void init() {
		binding.customProgressView.onStartedProcessingData();

		airConditionProcessing.getWeatherData(new WeatherDataCallback<AirConditionResult>() {
			@Override
			public void isSuccessful(AirConditionResult e) {
				if (getActivity() != null) {
					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Date lastUpdatedTime = e.getDownloadedDate();
							binding.lastUpdatedTime.setText(ClockUtil.weatherLastUpdatedTimeFormat.format(lastUpdatedTime));
							setData(e);
							binding.customProgressView.onSuccessfulProcessingData();
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
						}
					});
				}
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
	}

	public void refresh() {
		binding.customProgressView.onStartedProcessingData();
		airConditionProcessing.refresh(new WeatherDataCallback<AirConditionResult>() {
			@Override
			public void isSuccessful(AirConditionResult e) {
				if (getActivity() != null) {
					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Date lastUpdatedTime = e.getDownloadedDate();
							binding.lastUpdatedTime.setText(ClockUtil.weatherLastUpdatedTimeFormat.format(lastUpdatedTime));
							setData(e);
							binding.customProgressView.onSuccessfulProcessingData();
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
		init();
	}
}
