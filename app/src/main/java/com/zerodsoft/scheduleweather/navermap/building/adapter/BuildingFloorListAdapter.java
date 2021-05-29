package com.zerodsoft.scheduleweather.navermap.building.adapter;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarview.callback.EventCallback;
import com.zerodsoft.scheduleweather.databinding.BuildingFloorListItemBinding;
import com.zerodsoft.scheduleweather.navermap.building.model.BuildingFloorData;
import com.zerodsoft.scheduleweather.navermap.building.model.FacilityData;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;

public class BuildingFloorListAdapter extends RecyclerView.Adapter<BuildingFloorListAdapter.ViewHolder> {
	private SparseArray<BuildingFloorData> buildingFloorDataSparseArray = new SparseArray<>();
	private final OnClickDownloadListener onClickDownloadListener;
	private int aboveGroundCount = 2;
	private int underGroundCount = 1;
	public static final int UNDERGROUND_COUNT_MAX = 3;
	public static final int ABOVEGROUND_COUNT_MAX = 20;

	public enum FloorClassification {
		UNDERGROUND,
		ABOVEGROUND
	}

	public BuildingFloorListAdapter(Fragment fragment) {
		this.onClickDownloadListener = (OnClickDownloadListener) fragment;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.building_floor_list_item, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		holder.onBind();
	}

	@Override
	public int getItemCount() {
		return aboveGroundCount + underGroundCount;
	}


	public int getFloor(int position) {
		int floor = position >= aboveGroundCount ? aboveGroundCount - position - 1 : aboveGroundCount - position;
		return floor;
	}

	public void addFloors(FloorClassification floorClassification) {
		if (floorClassification == FloorClassification.ABOVEGROUND) {
			if (aboveGroundCount == ABOVEGROUND_COUNT_MAX) {
				return;
			} else {
				aboveGroundCount += 3;

				if (aboveGroundCount >= ABOVEGROUND_COUNT_MAX) {
					aboveGroundCount = ABOVEGROUND_COUNT_MAX;
				}
			}

		} else if (floorClassification == FloorClassification.UNDERGROUND) {
			if (underGroundCount == UNDERGROUND_COUNT_MAX) {
				return;
			} else {
				underGroundCount += 2;

				if (underGroundCount >= UNDERGROUND_COUNT_MAX) {
					underGroundCount = UNDERGROUND_COUNT_MAX;
				}
			}
		}
		notifyDataSetChanged();
	}

	public int getAboveGroundCount() {
		return aboveGroundCount;
	}

	public int getUnderGroundCount() {
		return underGroundCount;
	}


	class ViewHolder extends RecyclerView.ViewHolder {
		BuildingFloorListItemBinding binding;
		CompanyListAdapter companyListAdapter;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);

			binding = BuildingFloorListItemBinding.bind(itemView);
			binding.companyRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));

			binding.layoutForSearchFloorInfo.setVisibility(View.VISIBLE);
			binding.floorInfoLayout.setVisibility(View.GONE);
		}

		public void onBind() {
			final int floor = getFloor(getBindingAdapterPosition());

			binding.layoutForSearchFloorInfo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					//해당 층 데이터 가져온다
					onClickDownloadListener.getFloorInfo(String.valueOf(floor), new EventCallback<DataWrapper<BuildingFloorData>>() {
						@Override
						public void onResult(DataWrapper<BuildingFloorData> e) {
							if (e.getException() == null) {
								buildingFloorDataSparseArray.put(floor, e.getData());
								setResultData();
							} else {

							}
						}
					});
				}
			});

			setResultData();
		}

		public void setResultData() {
			final int floor = getFloor(getBindingAdapterPosition());
			String noFloor = floor < 0 ? "지하 " + Math.abs(floor) : String.valueOf(floor);

			if (buildingFloorDataSparseArray.get(floor) == null) {
				binding.noFloorForSearch.setText(noFloor);

				binding.layoutForSearchFloorInfo.setVisibility(View.VISIBLE);
				binding.floorInfoLayout.setVisibility(View.GONE);
			} else {
				binding.noFloor.setText(noFloor + "층");
				companyListAdapter = new CompanyListAdapter(buildingFloorDataSparseArray.get(floor).getCompanyDataList());
				binding.companyRecyclerView.setAdapter(companyListAdapter);

				FacilityData facilityData = buildingFloorDataSparseArray.get(floor).getFacilityData();
				binding.buildingFloorFacilityLayout.elevatorCount.setText(facilityData.getElevatorCount());
				binding.buildingFloorFacilityLayout.entranceCount.setText(facilityData.getEntranceCount());
				binding.buildingFloorFacilityLayout.movingWorkCount.setText(facilityData.getMovingWorkCount());
				binding.buildingFloorFacilityLayout.stairsCount.setText(facilityData.getStairsCount());
				binding.buildingFloorFacilityLayout.vacantRoomCount.setText(facilityData.getVacantRoomCount());
				binding.buildingFloorFacilityLayout.toiletCount.setText(facilityData.getToiletCount());

				binding.layoutForSearchFloorInfo.setVisibility(View.GONE);
				binding.floorInfoLayout.setVisibility(View.VISIBLE);
			}
		}
	}

	public interface OnClickDownloadListener {
		void getFloorInfo(String floor, EventCallback<DataWrapper<BuildingFloorData>> callback);
	}
}
