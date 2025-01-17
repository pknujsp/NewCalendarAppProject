package com.zerodsoft.calendarplatform.navermap.building.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.Utmk;
import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.activity.App;
import com.zerodsoft.calendarplatform.common.classes.JsonDownloader;
import com.zerodsoft.calendarplatform.common.interfaces.OnClickedListItem;
import com.zerodsoft.calendarplatform.databinding.FragmentBuildingListBinding;
import com.zerodsoft.calendarplatform.kakaoplace.LocalParameterUtil;
import com.zerodsoft.calendarplatform.kakaoplace.retrofit.KakaoLocalDownloader;
import com.zerodsoft.calendarplatform.navermap.interfaces.OnKakaoLocalApiCallback;
import com.zerodsoft.calendarplatform.navermap.viewmodel.MapSharedViewModel;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.KakaoLocalResponse;
import com.zerodsoft.calendarplatform.navermap.building.SgisBuildingDownloader;
import com.zerodsoft.calendarplatform.navermap.building.adapter.BuildingListAdapter;
import com.zerodsoft.calendarplatform.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.calendarplatform.retrofit.paremeters.sgis.building.BuildingAreaParameter;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.sgis.building.buildingarea.BuildingAreaItem;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.sgis.building.buildingarea.BuildingAreaResponse;

import org.jetbrains.annotations.NotNull;


public class BuildingListFragment extends Fragment implements OnClickedListItem<BuildingAreaItem> {
	private final IDrawCircleOnMap iDrawCircleOnMap;
	private FragmentBuildingListBinding binding;
	private MapSharedViewModel mapSharedViewModel;

	private Double centerLatitude;
	private Double centerLongitude;

	private BuildingListAdapter buildingListAdapter;

	public enum CalcType {
		MIN,
		MAX
	}


	public BuildingListFragment(IDrawCircleOnMap iDrawCircleOnMap) {
		this.iDrawCircleOnMap = iDrawCircleOnMap;
	}

	private final SgisBuildingDownloader sgisBuildingDownloader = new SgisBuildingDownloader();

	private void getBuildingList() {
		binding.customProgressView.onStartedProcessingData();
		final int RANGE_RADIUS = Integer.parseInt(App.getPreference_key_range_meter_for_search_buildings());

		Utmk minUtmK = calcCoordinate(centerLatitude, centerLongitude, RANGE_RADIUS, CalcType.MIN);
		Utmk maxUtmK = calcCoordinate(centerLatitude, centerLongitude, RANGE_RADIUS, CalcType.MAX);

		BuildingAreaParameter parameter = new BuildingAreaParameter();

		parameter.setMinX(String.valueOf((int) minUtmK.x));
		parameter.setMinY(String.valueOf((int) minUtmK.y));
		parameter.setMaxX(String.valueOf((int) maxUtmK.x));
		parameter.setMaxY(String.valueOf((int) maxUtmK.y));

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

	@Override
	public void onAttach(@NonNull @NotNull Context context) {
		super.onAttach(context);
		iDrawCircleOnMap.drawSearchRadiusCircle();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mapSharedViewModel = new ViewModelProvider(getParentFragment().getParentFragment()).get(MapSharedViewModel.class);

		Bundle bundle = getArguments();
		centerLatitude = bundle.getDouble("centerLatitude");
		centerLongitude = bundle.getDouble("centerLongitude");
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
		binding.radiusSeekbar.setValue(Float.parseFloat(App.getPreference_key_range_meter_for_search_buildings()));

		binding.searchRadius.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				binding.radiusSeekbarLayout.setVisibility(binding.radiusSeekbarLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
				int drawableId = 0;
				if (binding.radiusSeekbarLayout.getVisibility() == View.VISIBLE) {
					drawableId = R.drawable.expand_less_icon;
				} else {
					drawableId = R.drawable.expand_more_icon;
				}
				binding.searchRadius.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableId, 0);
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
				getBuildingList();
			}
		});


		binding.buildingSearchList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
		binding.buildingSearchList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

		//중심 좌표 기준으로 최소/최대 좌표값 계산
		LocalApiPlaceParameter coordToAddressParameter = LocalParameterUtil.getCoordToAddressParameter(centerLatitude, centerLongitude);
		KakaoLocalDownloader.coordToAddress(coordToAddressParameter, new OnKakaoLocalApiCallback() {
			@Override
			public void onResultSuccessful(int type, KakaoLocalResponse result) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						CoordToAddress coordToAddress = (CoordToAddress) result;
						binding.criteriaAddress.setText(coordToAddress.getCoordToAddressDocuments().get(0).getCoordToAddressAddress().getAddressName());
					}
				});
			}

			@Override
			public void onResultNoData() {

			}
		});

		getBuildingList();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		iDrawCircleOnMap.removeSearchRadiusCircle();
	}

	private void setSearchRadius() {
		iDrawCircleOnMap.drawSearchRadiusCircle();
		binding.searchRadius.setText(getString(R.string.search_radius) + " " + App.getPreference_key_range_meter_for_search_buildings() + "m");
	}


	private Utmk calcCoordinate(double latitude, double longitude, int meter, CalcType type) {
		/*
		final int LAT = (int) latitude;

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
		 */

		LatLng latLng = new LatLng(latitude, longitude);

		if (type == CalcType.MIN) {
			return Utmk.valueOf(latLng.offset(-meter, -meter));
		} else {
			return Utmk.valueOf(latLng.offset(+meter, +meter));
		}
	}

	private void clearText() {
		binding.criteriaAddress.setText("");
	}

	@Override
	public void onClickedListItem(BuildingAreaItem e, int position) {
		//change fragment
		BuildingFragment buildingFragment = new BuildingFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelable("building", e);
		buildingFragment.setArguments(bundle);

		getParentFragmentManager().beginTransaction().hide(this)
				.add(R.id.fragment_container, buildingFragment, getString(R.string.tag_building_info_fragment))
				.addToBackStack(getString(R.string.tag_building_info_fragment)).commit();
	}

	@Override
	public void deleteListItem(BuildingAreaItem e, int position) {
		//사용안함
	}


	public interface IDrawCircleOnMap {
		void drawSearchRadiusCircle();

		void removeSearchRadiusCircle();
	}

}