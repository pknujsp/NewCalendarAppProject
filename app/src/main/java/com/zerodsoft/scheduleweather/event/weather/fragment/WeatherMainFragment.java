package com.zerodsoft.scheduleweather.event.weather.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.databinding.FragmentWeatherItemBinding;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.room.dto.WeatherDataDTO;
import com.zerodsoft.scheduleweather.weather.aircondition.AirConditionFragment;
import com.zerodsoft.scheduleweather.weather.interfaces.OnDownloadedTimeListener;
import com.zerodsoft.scheduleweather.weather.mid.MidFcstFragment;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.weather.repository.AirConditionDownloader;
import com.zerodsoft.scheduleweather.weather.repository.WeatherDataDownloader;
import com.zerodsoft.scheduleweather.weather.ultrasrtfcst.UltraSrtFcstFragment;
import com.zerodsoft.scheduleweather.weather.ultrasrtncst.UltraSrtNcstFragment;
import com.zerodsoft.scheduleweather.weather.viewmodel.AreaCodeViewModel;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.weather.vilagefcst.VilageFcstFragment;

import java.util.Date;

public class WeatherMainFragment extends BottomSheetDialogFragment implements OnDownloadedTimeListener {
	public static final String TAG = "WeatherMainFragment";

	private final int CALENDAR_ID;
	private final long EVENT_ID;
	private final int VIEW_HEIGHT;

	private FragmentWeatherItemBinding binding;
	private LocationDTO selectedLocationDto;

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

	private AreaCodeViewModel areaCodeViewModel;
	private LocationViewModel locationViewModel;
	private WeatherAreaCodeDTO weatherAreaCode;

	private String latitude;
	private String longitude;
	private boolean hasSimpleLocation;

	private BottomSheetBehavior bottomSheetBehavior;

	public WeatherMainFragment(int VIEW_HEIGHT, int CALENDAR_ID, long EVENT_ID) {
		this.VIEW_HEIGHT = VIEW_HEIGHT;
		this.CALENDAR_ID = CALENDAR_ID;
		this.EVENT_ID = EVENT_ID;
	}

	@Override
	public void dismiss() {
		bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
	}

	@Override
	public void onDestroy() {
		WeatherDataDownloader.close();
		AirConditionDownloader.close();
		super.onDestroy();
	}

	@Override
	public void onDismiss(@NonNull DialogInterface dialog) {
		super.onDismiss(dialog);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle arguments = getArguments();
		latitude = arguments.getString("latitude");
		longitude = arguments.getString("longitude");
		hasSimpleLocation = arguments.getBoolean("hasSimpleLocation");
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);

		bottomSheetBehavior = ((BottomSheetDialog) dialog).getBehavior();
		bottomSheetBehavior.setDraggable(false);
		bottomSheetBehavior.setPeekHeight(0);
		bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

		return dialog;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = FragmentWeatherItemBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		areaCodeViewModel = new ViewModelProvider(this).get(AreaCodeViewModel.class);
		locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

		binding.ultraSrtNcstDownloadedTime.setText("");
		binding.ultraSrtFcstDownloadedTime.setText("");
		binding.midLandFcstDownloadedTime.setText("");
		binding.midTaDownloadedTime.setText("");
		binding.airConditionDownloadedTime.setText("");
		binding.vilageFcstDownloadedTime.setText("");

		View bottomSheet = getDialog().findViewById(R.id.design_bottom_sheet);
		bottomSheet.getLayoutParams().height = VIEW_HEIGHT;

		binding.refreshWeatherFab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ultraSrtNcstFragment.refresh();
				ultraSrtFcstFragment.refresh();
				vilageFcstFragment.refresh();
				midFcstFragment.refresh();
				airConditionFragment.refresh();
			}
		});

		binding.scrollview.setOnScrollChangeListener(new View.OnScrollChangeListener() {
			@Override
			public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
				if (scrollY - oldScrollY > 0) {
					// 아래로 스크롤
					binding.refreshWeatherFab.setVisibility(View.GONE);
				} else if (scrollY - oldScrollY < 0) {
					// 위로 스크롤
					binding.refreshWeatherFab.setVisibility(View.VISIBLE);
				}
			}
		});

		locationViewModel.getLocation(CALENDAR_ID, EVENT_ID, new DbQueryCallback<LocationDTO>() {
			@Override
			public void onResultSuccessful(LocationDTO selectedLocationResultDto) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						selectedLocationDto = selectedLocationResultDto;

						final double lat = Double.parseDouble(selectedLocationResultDto.getLatitude());
						final double lon = Double.parseDouble(selectedLocationResultDto.getLongitude());

						loadInitialData(lat, lon);
					}
				});

			}

			@Override
			public void onResultNoData() {
				final double lat = Double.parseDouble(latitude);
				final double lon = Double.parseDouble(longitude);

				loadInitialData(lat, lon);
			}
		});


	}

	private void loadInitialData(double latitude, double longitude) {
		areaCodeViewModel.getCodeOfProximateArea(latitude, longitude, new DbQueryCallback<WeatherAreaCodeDTO>() {
			@Override
			public void onResultSuccessful(WeatherAreaCodeDTO weatherAreaCodeResultDto) {
				weatherAreaCode = weatherAreaCodeResultDto;

				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (selectedLocationDto == null) {
							Toast.makeText(getContext(), hasSimpleLocation ?
											R.string.msg_getting_weather_data_of_map_center_point_because_havnt_detail_location :
											R.string.msg_getting_weather_data_of_map_center_point_because_havnt_simple_location,
									Toast.LENGTH_SHORT).show();
						}
						setAddressName();
						createFragments();
					}
				});
			}

			@Override
			public void onResultNoData() {

			}
		});
	}

	private void createFragments() {
		ultraSrtNcstFragment = new UltraSrtNcstFragment(weatherAreaCode, this);
		ultraSrtFcstFragment = new UltraSrtFcstFragment(weatherAreaCode, this);
		vilageFcstFragment = new VilageFcstFragment(weatherAreaCode, this);
		midFcstFragment = new MidFcstFragment(weatherAreaCode, this);

		String lat, lon = null;
		if (selectedLocationDto != null) {
			lat = selectedLocationDto.getLatitude();
			lon = selectedLocationDto.getLongitude();
		} else {
			lat = latitude;
			lon = longitude;
		}
		airConditionFragment = new AirConditionFragment(lat, lon, this);

		getChildFragmentManager().beginTransaction()
				.add(binding.ultraSrtNcstFragmentContainer.getId(), ultraSrtNcstFragment, "0")
				.add(binding.ultraSrtFcstFragmentContainer.getId(), ultraSrtFcstFragment, "1")
				.add(binding.vilageFcstFragmentContainer.getId(), vilageFcstFragment, "2")
				.add(binding.midFcstFragmentContainer.getId(), midFcstFragment, "3")
				.add(binding.airConditionFragmentContainer.getId(), airConditionFragment, "4")
				.commit();
	}

	private void setAddressName() {
		String addressName = weatherAreaCode.getPhase1() + " " + weatherAreaCode.getPhase2() + " " + weatherAreaCode.getPhase3();
		binding.addressName.setText(addressName);
	}

	@Override
	public void setDownloadedTime(Date downloadedTime, int dataType) {
		String dateTimeStr = downloadedTime == null ? getString(R.string.error) : ClockUtil.DB_DATE_FORMAT.format(downloadedTime);
		switch (dataType) {
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


}