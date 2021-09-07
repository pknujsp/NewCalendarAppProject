package com.zerodsoft.calendarplatform.weather.ultrasrtncst;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.databinding.UltraSrtNcstFragmentBinding;
import com.zerodsoft.calendarplatform.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.calendarplatform.utility.ClockUtil;
import com.zerodsoft.calendarplatform.weather.common.WeatherDataCallback;
import com.zerodsoft.calendarplatform.weather.dataprocessing.UltraSrtNcstProcessing;
import com.zerodsoft.calendarplatform.weather.dataprocessing.WeatherDataConverter;
import com.zerodsoft.calendarplatform.weather.interfaces.CheckSuccess;
import com.zerodsoft.calendarplatform.weather.interfaces.LoadWeatherDataResultCallback;


public class UltraSrtNcstFragment extends Fragment implements CheckSuccess {
	/*
	- 초단기 실황 -
	기온
	1시간 강수량
	동서바람성분(미 표시)
	남북바람성분(미 표시)
	습도
	강수형태
	풍향
	풍속
	 */
	private UltraSrtNcstFragmentBinding binding;
	private WeatherAreaCodeDTO weatherAreaCode;
	private UltraSrtNcstProcessing ultraSrtNcstProcessing;
	private LoadWeatherDataResultCallback loadWeatherDataResultCallback;


	public UltraSrtNcstFragment(WeatherAreaCodeDTO weatherAreaCodeDTO, LoadWeatherDataResultCallback loadWeatherDataResultCallback) {
		this.weatherAreaCode = weatherAreaCodeDTO;
		this.loadWeatherDataResultCallback = loadWeatherDataResultCallback;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = UltraSrtNcstFragmentBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		binding.customProgressView.setContentView(binding.ultraSrtNcstLayout);
		binding.customProgressView.onStartedProcessingData();
		loadWeatherDataResultCallback.onLoadStarted();
		clearViews();

		ultraSrtNcstProcessing = new UltraSrtNcstProcessing(getContext(), weatherAreaCode.getY(), weatherAreaCode.getX());
		ultraSrtNcstProcessing.getWeatherData(new WeatherDataCallback<UltraSrtNcstResult>() {
			@Override
			public void isSuccessful(UltraSrtNcstResult e) {
				if (getActivity() != null) {
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							setValue(e);
							binding.customProgressView.onSuccessfulProcessingData();
							loadWeatherDataResultCallback.onResult(true);
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
							loadWeatherDataResultCallback.onResult(false);
							clearViews();
						}
					});
				}
			}
		});
	}

	private void setValue(UltraSrtNcstResult ultraSrtNcstResult) {
		UltraSrtNcstFinalData ultraSrtNcstFinalData = ultraSrtNcstResult.getUltraSrtNcstFinalData();

		binding.fcstDateTime.setText(ClockUtil.weatherLastUpdatedTimeFormat.format(ultraSrtNcstResult.getDownloadedDate()));

		//기온
		binding.ultraSrtNcstTemp.setText(ultraSrtNcstFinalData.getTemperature() + getString(R.string.celcius));
		//강수형태
		int ptyDrawableId = WeatherDataConverter.getSkyDrawableId(null, ultraSrtNcstFinalData.getPrecipitationForm(), true);
		if (ptyDrawableId != 0) {
			binding.ptyImage.setImageDrawable(ContextCompat.getDrawable(getContext(), ptyDrawableId));
			binding.ptyImage.setVisibility(View.VISIBLE);
		} else {
			binding.ptyImage.setVisibility(View.GONE);
		}
		//습도
		binding.ultraSrtNcstHumidity.setText(ultraSrtNcstFinalData.getHumidity());
		//바람
		binding.ultraSrtNcstWind.setText(ultraSrtNcstFinalData.getWindSpeed() + "m/s, " + ultraSrtNcstFinalData.getWindDirection() + "\n" +
				WeatherDataConverter.getWindSpeedDescription(ultraSrtNcstFinalData.getWindSpeed()));
		//시간 강수량
		binding.ultraSrtNcstRn1.setText(ultraSrtNcstFinalData.getPrecipitation1Hour());
	}

	public void refresh() {
		binding.customProgressView.onStartedProcessingData();
		loadWeatherDataResultCallback.onLoadStarted();

		ultraSrtNcstProcessing.refresh(new WeatherDataCallback<UltraSrtNcstResult>() {
			@Override
			public void isSuccessful(UltraSrtNcstResult e) {
				if (getActivity() != null) {
					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							setValue(e);
							binding.customProgressView.onSuccessfulProcessingData();
							loadWeatherDataResultCallback.onResult(true);
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
							loadWeatherDataResultCallback.onResult(false);
							clearViews();
						}
					});
				}
			}
		});
	}

	public void clearViews() {
		binding.fcstDateTime.setText("");
		binding.ultraSrtNcstTemp.setText("");
		binding.ptyImage.setVisibility(View.GONE);
		binding.ultraSrtNcstHumidity.setText("");
		binding.ultraSrtNcstWind.setText("");
		binding.ultraSrtNcstRn1.setText("");
	}

	@Override
	public boolean isSuccess() {
		return binding.customProgressView.isSuccess();
	}
}