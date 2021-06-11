package com.zerodsoft.scheduleweather.navermap.building.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.common.interfaces.OnBackPressedCallbackController;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.databinding.FragmentBuildingListBinding;
import com.zerodsoft.scheduleweather.navermap.viewmodel.MapSharedViewModel;
import com.zerodsoft.scheduleweather.weather.repository.SgisTranscoord;
import com.zerodsoft.scheduleweather.navermap.BottomSheetType;
import com.zerodsoft.scheduleweather.navermap.building.SgisBuildingDownloader;
import com.zerodsoft.scheduleweather.navermap.building.adapter.BuildingListAdapter;
import com.zerodsoft.scheduleweather.navermap.interfaces.BottomSheetController;
import com.zerodsoft.scheduleweather.navermap.interfaces.BuildingFragmentController;
import com.zerodsoft.scheduleweather.navermap.model.CoordToAddressUtil;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.TransCoordParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.building.BuildingAreaParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.SgisBuildingRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.buildingarea.BuildingAreaItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.buildingarea.BuildingAreaResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.transcoord.TransCoordResponse;
import com.zerodsoft.scheduleweather.sgis.SgisAuth;


public class BuildingListFragment extends Fragment implements OnClickedListItem<BuildingAreaItem> {
	private final BuildingFragmentController buildingFragmentController;
	private BottomSheetController bottomSheetController;
	private FragmentBuildingListBinding binding;
	private MapSharedViewModel mapSharedViewModel;

	public enum CalcType {
		MIN,
		MAX
	}

	private TransCoordResponse minTransCoordResponse;
	private TransCoordResponse maxTransCoordResponse;

	private String centerLatitude;
	private String centerLongitude;

	private BuildingListAdapter buildingListAdapter;
	private OnSearchRadiusChangeListener onSearchRadiusChangeListener;

	public BuildingListFragment(Fragment fragment) {
		buildingFragmentController = (BuildingFragmentController) fragment;
		onSearchRadiusChangeListener = (OnSearchRadiusChangeListener) fragment;
	}

	private final SgisBuildingDownloader sgisBuildingDownloader = new SgisBuildingDownloader();

	private final SgisTranscoord minSgisTranscoord = new SgisTranscoord();

	private final SgisTranscoord maxSgisTranscoord = new SgisTranscoord();

	private void getBuildingList() {
		if (minTransCoordResponse != null && maxTransCoordResponse != null) {
			BuildingAreaParameter parameter = new BuildingAreaParameter();

			parameter.setMinX(String.valueOf((int) Double.parseDouble(minTransCoordResponse.getResult().getPosX())));
			parameter.setMinY(String.valueOf((int) Double.parseDouble(minTransCoordResponse.getResult().getPosY())));
			parameter.setMaxX(String.valueOf((int) Double.parseDouble(maxTransCoordResponse.getResult().getPosX())));
			parameter.setMaxY(String.valueOf((int) Double.parseDouble(maxTransCoordResponse.getResult().getPosY())));

			minTransCoordResponse = null;
			maxTransCoordResponse = null;

			sgisBuildingDownloader.getBuildingList(parameter, new JsonDownloader<BuildingAreaResponse>() {
				@Override
				public void onResponseSuccessful(BuildingAreaResponse result) {
					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							//리스트 생성
							if (result.getResult().isEmpty()) {
								binding.customProgressView.onFailedProcessingData(getString(R.string.not_founded_search_result));
							} else {
								buildingListAdapter = new BuildingListAdapter(result.getResult(), BuildingListFragment.this);
								binding.buildingSearchList.setAdapter(buildingListAdapter);
								binding.customProgressView.onSuccessfulProcessingData();
							}
						}
					});
				}

				@Override
				public void onResponseFailed(Exception e) {

				}
			});
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mapSharedViewModel = new ViewModelProvider(getParentFragment()).get(MapSharedViewModel.class);
		bottomSheetController = mapSharedViewModel.getBottomSheetController();

		Bundle bundle = getArguments();
		centerLatitude = bundle.getString("centerLatitude");
		centerLongitude = bundle.getString("centerLongitude");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentBuildingListBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		binding.customProgressView.setContentView(binding.buildingSearchList);

		clearText();
		setSearchRadius();

		binding.radiusSeekbarLayout.setVisibility(View.GONE);
		binding.radiusSeekbar.setValue(Float.valueOf(App.getPreference_key_range_meter_for_search_buildings()));

		binding.searchRadius.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				binding.radiusSeekbarLayout.setVisibility(binding.radiusSeekbarLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
			}
		});

		binding.applyRadius.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//변경한 값 적용
				binding.radiusSeekbarLayout.setVisibility(View.GONE);

				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
				SharedPreferences.Editor editor = preferences.edit();

				final String newValue = String.valueOf((int) binding.radiusSeekbar.getValue());
				editor.putString(getString(R.string.preference_key_range_meter_for_search_buildings), newValue);
				editor.commit();

				App.setPreference_key_range_meter_for_search_buildings(newValue);
				setSearchRadius();

				binding.buildingSearchList.setAdapter(null);
				transcoord();
			}
		});


		binding.buildingSearchList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
		binding.buildingSearchList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

		//중심 좌표 기준으로 최소/최대 좌표값 계산
		LocalApiPlaceParameter coordToAddressParameter = new LocalApiPlaceParameter();
		coordToAddressParameter.setX(centerLongitude);
		coordToAddressParameter.setY(centerLatitude);

		CoordToAddressUtil.coordToAddress(coordToAddressParameter, new CarrierMessagingService.ResultCallback<DataWrapper<CoordToAddress>>() {
			@Override
			public void onReceiveResult(@NonNull DataWrapper<CoordToAddress> coordToAddressDataWrapper) throws RemoteException {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (coordToAddressDataWrapper.getException() == null) {
							CoordToAddress coordToAddress = coordToAddressDataWrapper.getData();
							binding.criteriaAddress.setText(coordToAddress.getCoordToAddressDocuments().get(0).getCoordToAddressAddress().getAddressName());
						}
					}
				});

			}
		});

		transcoord();
		bottomSheetController.setStateOfBottomSheet(BottomSheetType.BUILDING, BottomSheetBehavior.STATE_EXPANDED);
	}

	private void setSearchRadius() {
		onSearchRadiusChangeListener.drawSearchRadiusCircle();
		binding.searchRadius.setText(getString(R.string.search_radius) + " " + App.getPreference_key_range_meter_for_search_buildings() + "m");
	}

	public void transcoord() {
		binding.customProgressView.onStartedProcessingData(null);

		final int RANGE_RADIUS = Integer.parseInt(App.getPreference_key_range_meter_for_search_buildings());

		String[] min = calcCoordinate(centerLatitude, centerLongitude, RANGE_RADIUS, CalcType.MIN);
		String minLongitude = min[1];
		String minLatitude = min[0];

		String[] max = calcCoordinate(centerLatitude, centerLongitude, RANGE_RADIUS, CalcType.MAX);
		String maxLongitude = max[1];
		String maxLatitude = max[0];

		TransCoordParameter minParameter = new TransCoordParameter();
		minParameter.setSrc(TransCoordParameter.WGS84);
		minParameter.setDst(TransCoordParameter.UTM_K);
		minParameter.setPosX(minLongitude);
		minParameter.setPosY(minLatitude);

		TransCoordParameter maxParameter = new TransCoordParameter();
		maxParameter.setSrc(TransCoordParameter.WGS84);
		maxParameter.setDst(TransCoordParameter.UTM_K);
		maxParameter.setPosX(maxLongitude);
		maxParameter.setPosY(maxLatitude);

		minSgisTranscoord.transcoord(minParameter, new JsonDownloader<TransCoordResponse>() {
			@Override
			public void onResponseSuccessful(TransCoordResponse result) {
				minTransCoordResponse = result;
				getBuildingList();
			}

			@Override
			public void onResponseFailed(Exception e) {

			}
		});
		maxSgisTranscoord.transcoord(maxParameter, new JsonDownloader<TransCoordResponse>() {
			@Override
			public void onResponseSuccessful(TransCoordResponse result) {
				maxTransCoordResponse = result;
				getBuildingList();
			}

			@Override
			public void onResponseFailed(Exception e) {

			}
		});
	}

	private String[] calcCoordinate(String latitude, String longitude, int meter, CalcType type) {
		final int LAT = (int) Double.parseDouble(latitude);

		//위도 1도의 미터
		final double METER_PER_DEGREE_LAT = 111000;

		//1미터의 위도 소수점 값
		final double METER_1_DECIMAL_LAT = 1.0 / METER_PER_DEGREE_LAT;

		//경도 1도의 미터
		final double METER_PER_DEGREE_LON = (2.0 * 3.14 * 6380.0 * Math.cos(LAT) / 360.0) * 1000.0;

		//1미터의 경도 소수점 값
		final double METER_1_DECIMAL_LON = 1.0 / METER_PER_DEGREE_LON;

		if (type == CalcType.MIN) {
			return new String[]{String.valueOf(Double.parseDouble(latitude) - METER_1_DECIMAL_LAT * meter),
					String.valueOf(Double.parseDouble(longitude) - METER_1_DECIMAL_LON * meter)};
		} else {
			return new String[]{String.valueOf(Double.parseDouble(latitude) + METER_1_DECIMAL_LAT * meter),
					String.valueOf(Double.parseDouble(longitude) + METER_1_DECIMAL_LON * meter)};
		}
	}

	private void clearText() {
		binding.criteriaAddress.setText("");
	}

	@Override
	public void onClickedListItem(BuildingAreaItem e, int position) {
		//change fragment
		buildingFragmentController.setBuildingBottomSheetHeight(getString(R.string.tag_building_info_fragment));

		BuildingFragment buildingFragment = new BuildingFragment(buildingFragmentController);
		Bundle bundle = new Bundle();
		bundle.putParcelable("building", e);
		buildingFragment.setArguments(bundle);

		getParentFragmentManager().beginTransaction().hide(this)
				.add(R.id.building_fragment_container, buildingFragment, getString(R.string.tag_building_info_fragment))
				.addToBackStack(getString(R.string.tag_building_info_fragment)).commit();
	}

	@Override
	public void deleteListItem(BuildingAreaItem e, int position) {
		//사용안함
	}


	public interface OnSearchRadiusChangeListener {
		void drawSearchRadiusCircle();
	}

}