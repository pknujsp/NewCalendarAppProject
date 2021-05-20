package com.zerodsoft.scheduleweather.weather.ultrasrtncst;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.UltraSrtNcstFragmentBinding;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.room.dto.WeatherDataDTO;
import com.zerodsoft.scheduleweather.weather.common.ViewProgress;
import com.zerodsoft.scheduleweather.weather.common.WeatherDataCallback;
import com.zerodsoft.scheduleweather.weather.dataprocessing.UltraSrtNcstProcessing;
import com.zerodsoft.scheduleweather.weather.interfaces.OnDownloadedTimeListener;
import com.zerodsoft.scheduleweather.utility.WeatherDataConverter;


public class UltraSrtNcstFragment extends Fragment {
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
	private final OnDownloadedTimeListener onDownloadedTimeListener;

	private UltraSrtNcstFragmentBinding binding;
	private WeatherAreaCodeDTO weatherAreaCode;
	private ViewProgress viewProgress;
	private UltraSrtNcstProcessing ultraSrtNcstProcessing;


	public UltraSrtNcstFragment(WeatherAreaCodeDTO weatherAreaCodeDTO, OnDownloadedTimeListener onDownloadedTimeListener) {
		this.weatherAreaCode = weatherAreaCodeDTO;
		this.onDownloadedTimeListener = onDownloadedTimeListener;
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
		clearViews();

		ultraSrtNcstProcessing = new UltraSrtNcstProcessing(getContext(), weatherAreaCode.getY(), weatherAreaCode.getX());
		viewProgress = new ViewProgress(binding.ultraSrtNcstLayout, binding.weatherProgressLayout.progressBar,
				binding.weatherProgressLayout.errorTextview, binding.weatherProgressLayout.getRoot());
		viewProgress.onStartedProcessingData();

		ultraSrtNcstProcessing.getWeatherData(new WeatherDataCallback<UltraSrtNcstResult>() {
			@Override
			public void isSuccessful(UltraSrtNcstResult e) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						onDownloadedTimeListener.setDownloadedTime(e.getDownloadedDate(), WeatherDataDTO.ULTRA_SRT_NCST);
						viewProgress.onCompletedProcessingData(true);
						setValue(e);
					}
				});
			}

			@Override
			public void isFailure(Exception e) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						clearViews();
						onDownloadedTimeListener.setDownloadedTime(null, WeatherDataDTO.ULTRA_SRT_NCST);
						viewProgress.onCompletedProcessingData(false, e.getMessage());
					}
				});

			}
		});
	}

	private void setValue(UltraSrtNcstResult ultraSrtNcstResult) {
		UltraSrtNcstFinalData ultraSrtNcstFinalData = ultraSrtNcstResult.getUltraSrtNcstFinalData();
		//기온
		binding.ultraSrtNcstTemp.setText(ultraSrtNcstFinalData.getTemperature() + getString(R.string.celcius));
		//강수형태
		binding.ultraSrtNcstPty.setText(ultraSrtNcstFinalData.getPrecipitationForm());
		//습도
		binding.ultraSrtNcstHumidity.setText(ultraSrtNcstFinalData.getHumidity());
		//바람
		binding.ultraSrtNcstWind.setText(ultraSrtNcstFinalData.getWindSpeed() + "m/s, " + ultraSrtNcstFinalData.getWindDirection() + "\n" +
				WeatherDataConverter.getWindSpeedDescription(ultraSrtNcstFinalData.getWindSpeed()));
		//시간 강수량
		binding.ultraSrtNcstRn1.setText(ultraSrtNcstFinalData.getPrecipitation1Hour());
	}

	public void refresh() {
		viewProgress.onStartedProcessingData();

		ultraSrtNcstProcessing.refresh(new WeatherDataCallback<UltraSrtNcstResult>() {
			@Override
			public void isSuccessful(UltraSrtNcstResult e) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						onDownloadedTimeListener.setDownloadedTime(e.getDownloadedDate(), WeatherDataDTO.ULTRA_SRT_NCST);
						viewProgress.onCompletedProcessingData(true);
						setValue(e);
					}
				});

			}

			@Override
			public void isFailure(Exception e) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						clearViews();
						onDownloadedTimeListener.setDownloadedTime(null, WeatherDataDTO.ULTRA_SRT_NCST);
						viewProgress.onCompletedProcessingData(false, e.getMessage());
					}
				});
			}
		});
	}

	public void clearViews() {
		binding.ultraSrtNcstTemp.setText("");
		binding.ultraSrtNcstPty.setText("");
		binding.ultraSrtNcstHumidity.setText("");
		binding.ultraSrtNcstWind.setText("");
		binding.ultraSrtNcstRn1.setText("");
	}
}