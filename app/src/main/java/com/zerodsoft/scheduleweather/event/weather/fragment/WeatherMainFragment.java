package com.zerodsoft.scheduleweather.event.weather.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.databinding.FragmentWeatherItemBinding;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.weather.aircondition.AirConditionFragment;
import com.zerodsoft.scheduleweather.weather.hourlyfcst.HourlyFcstFragment;
import com.zerodsoft.scheduleweather.weather.icons.WeatherIconsRecyclerViewAdapter;
import com.zerodsoft.scheduleweather.weather.mid.MidFcstFragment;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.weather.repository.AirConditionDownloader;
import com.zerodsoft.scheduleweather.weather.repository.WeatherDataDownloader;
import com.zerodsoft.scheduleweather.weather.sunsetrise.SunSetRiseFragment;
import com.zerodsoft.scheduleweather.weather.ultrasrtncst.UltraSrtNcstFragment;
import com.zerodsoft.scheduleweather.weather.viewmodel.AreaCodeViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WeatherMainFragment extends BottomSheetDialogFragment {
	public static final String TAG = "WeatherMainFragment";

	private final int CALENDAR_ID;
	private final long EVENT_ID;
	private final int VIEW_HEIGHT;
	private final DialogInterface dialogInterface;

	private FragmentWeatherItemBinding binding;
	private LocationDTO selectedLocationDto;

	//초단기 실황
	private UltraSrtNcstFragment ultraSrtNcstFragment;
	//시간별 예보
	private HourlyFcstFragment hourlyFcstFragment;
	//중기 예보
	private MidFcstFragment midFcstFragment;
	//대기 상태
	private AirConditionFragment airConditionFragment;
	//일출 일몰
	private SunSetRiseFragment sunSetRiseFragment;

	private AreaCodeViewModel areaCodeViewModel;
	private LocationViewModel locationViewModel;
	private WeatherAreaCodeDTO weatherAreaCode;

	private BottomSheetBehavior bottomSheetBehavior;

	private static final List<Drawable> weatherIconDrawablesList = new ArrayList<>();
	private static final List<String> weatherIconDescriptionsList = new ArrayList<>();

	public WeatherMainFragment(DialogInterface dialogInterface, int VIEW_HEIGHT, int CALENDAR_ID, long EVENT_ID) {
		this.dialogInterface = dialogInterface;
		this.VIEW_HEIGHT = VIEW_HEIGHT;
		this.CALENDAR_ID = CALENDAR_ID;
		this.EVENT_ID = EVENT_ID;
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
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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

		View bottomSheet = getDialog().findViewById(R.id.design_bottom_sheet);
		bottomSheet.getLayoutParams().height = VIEW_HEIGHT;

		binding.weatherIconsInfoLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				View weatherIconsInfoView = getLayoutInflater().inflate(R.layout.weather_icons_info, null);

				if (weatherIconDrawablesList.isEmpty() || weatherIconDescriptionsList.isEmpty()) {
					TypedArray imgs = getResources().obtainTypedArray(R.array.weather_icons_arr);

					for (int index = 0; index < imgs.length(); index++) {
						weatherIconDrawablesList.add(imgs.getDrawable(index));
					}

					weatherIconDescriptionsList.addAll(Arrays.asList(getResources().getStringArray(R.array.weather_icon_descriptions_arr)));
				}

				RecyclerView weatherIconsRecyclerView = (RecyclerView) weatherIconsInfoView.findViewById(R.id.weather_icons_list);
				weatherIconsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
				weatherIconsRecyclerView.setAdapter(new WeatherIconsRecyclerViewAdapter(weatherIconDrawablesList, weatherIconDescriptionsList));

				AlertDialog dialog = new MaterialAlertDialogBuilder(requireActivity())
						.setTitle(R.string.weather_icons_info)
						.setView(weatherIconsInfoView).create();
				dialog.show();
			}
		});

		binding.refreshWeatherFab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ultraSrtNcstFragment.refresh();
				hourlyFcstFragment.refresh();
				midFcstFragment.refresh();
				airConditionFragment.refresh();
				sunSetRiseFragment.refresh();
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

		locationViewModel.getLocation(EVENT_ID, new DbQueryCallback<LocationDTO>() {
			@Override
			public void onResultSuccessful(LocationDTO selectedLocationResultDto) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						selectedLocationDto = selectedLocationResultDto;
						loadInitialData(Double.parseDouble(selectedLocationDto.getLatitude()), Double.parseDouble(selectedLocationDto.getLongitude()));
					}
				});

			}

			@Override
			public void onResultNoData() {
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
		ultraSrtNcstFragment = new UltraSrtNcstFragment(weatherAreaCode);
		hourlyFcstFragment = new HourlyFcstFragment(weatherAreaCode);
		midFcstFragment = new MidFcstFragment(weatherAreaCode);
		sunSetRiseFragment = new SunSetRiseFragment(weatherAreaCode);

		String lat, lon = null;
		lat = selectedLocationDto.getLatitude();
		lon = selectedLocationDto.getLongitude();
		airConditionFragment = new AirConditionFragment(lat, lon);

		getChildFragmentManager().beginTransaction()
				.add(binding.ultraSrtNcstFragmentContainer.getId(), ultraSrtNcstFragment, "0")
				.add(binding.hourlyFcstFragmentContainer.getId(), hourlyFcstFragment, "1")
				.add(binding.midFcstFragmentContainer.getId(), midFcstFragment, "2")
				.add(binding.airConditionFragmentContainer.getId(), airConditionFragment, "3")
				.add(binding.sunSetRiseFragmentContainer.getId(), sunSetRiseFragment, "4")
				.commit();
	}

	private void setAddressName() {
		String addressName = weatherAreaCode.getPhase1() + " " + weatherAreaCode.getPhase2() + " " + weatherAreaCode.getPhase3();
		binding.addressName.setText(addressName);
	}


}
