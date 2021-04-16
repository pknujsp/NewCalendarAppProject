package com.zerodsoft.scheduleweather.kakaomap.building.adapter;

import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
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
import com.zerodsoft.scheduleweather.kakaomap.building.model.BuildingFloorData;
import com.zerodsoft.scheduleweather.kakaomap.building.model.FacilityData;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;

public class BuildingFloorListAdapter extends RecyclerView.Adapter<BuildingFloorListAdapter.ViewHolder>
{
    private ArrayMap<String, BuildingFloorData> buildingFloorDataArrayMap = new ArrayMap<>();
    private final OnClickDownloadListener onClickDownloadListener;
    private int aboveGroundCount = 10;
    private int underGroundCount = 1;
    public static final int UNDERGROUND_COUNT_MAX = 8;
    public static final int ABOVEGROUND_COUNT_MAX = 130;

    public enum FloorClassification
    {
        UNDERGROUND, ABOVEGROUND
    }

    public BuildingFloorListAdapter(Fragment fragment)
    {
        this.onClickDownloadListener = (OnClickDownloadListener) fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.building_floor_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.onBind();
    }

    @Override
    public int getItemCount()
    {
        return aboveGroundCount + underGroundCount;
    }

    private int getArrIndex(String floor)
    {
        int index = 0;
        final int FLOOR = Integer.parseInt(floor);

        if (underGroundCount < 0)
        {
            //지하 있음
            index = FLOOR >= 1 ? FLOOR + underGroundCount - 1 : FLOOR + underGroundCount;
        } else
        {
            //지하 없음
            index = FLOOR - 1;
        }

        return index;
    }

    public String getFloor(int position)
    {
        int floor = 0;

        if (underGroundCount > 0)
        {
            //지하 있음
            floor = (position - underGroundCount) >= 0 ? position - underGroundCount + 1 : position - underGroundCount;
        } else
        {
            //지하 없음
            floor = position + 1;
        }

        return String.valueOf(floor);
    }

    public void addFloors(FloorClassification floorClassification)
    {
        if (floorClassification == FloorClassification.ABOVEGROUND)
        {
            if (aboveGroundCount == ABOVEGROUND_COUNT_MAX)
            {
                return;
            } else
            {
                aboveGroundCount += 10;

                if (aboveGroundCount >= ABOVEGROUND_COUNT_MAX)
                {
                    aboveGroundCount = ABOVEGROUND_COUNT_MAX;
                }
            }

        } else if (floorClassification == FloorClassification.UNDERGROUND)
        {
            if (underGroundCount == UNDERGROUND_COUNT_MAX)
            {
                return;
            } else
            {
                underGroundCount += 2;

                if (underGroundCount >= UNDERGROUND_COUNT_MAX)
                {
                    underGroundCount = UNDERGROUND_COUNT_MAX;
                }
            }
        }

        notifyDataSetChanged();
    }

    public int getAboveGroundCount()
    {
        return aboveGroundCount;
    }

    public int getUnderGroundCount()
    {
        return underGroundCount;
    }


    class ViewHolder extends RecyclerView.ViewHolder
    {
        BuildingFloorListItemBinding binding;
        CompanyListAdapter companyListAdapter;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            binding = BuildingFloorListItemBinding.bind(itemView);

            binding.companyRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));

            binding.layoutForSearchFloorInfo.setVisibility(View.VISIBLE);
            binding.floorInfoLayout.setVisibility(View.GONE);
        }

        public void onBind()
        {
            final String FLOOR = getFloor(getAdapterPosition());

            binding.layoutForSearchFloorInfo.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    //해당 층 데이터 가져온다
                    onClickDownloadListener.getFloorInfo(FLOOR, new EventCallback<DataWrapper<BuildingFloorData>>()
                    {
                        @Override
                        public void onResult(DataWrapper<BuildingFloorData> e)
                        {
                            if (e.getException() == null)
                            {
                                buildingFloorDataArrayMap.put(FLOOR, e.getData());
                                setResultData();
                            } else
                            {

                            }
                        }
                    });
                }
            });

            setResultData();
        }

        public void setResultData()
        {
            final String FLOOR = getFloor(getAdapterPosition());
            String noFloor = Integer.parseInt(FLOOR) < 0 ? "지하 " + Math.abs(Integer.parseInt(FLOOR)) : FLOOR;

            if (buildingFloorDataArrayMap.get(FLOOR) == null)
            {
                binding.noFloorForSearch.setText(noFloor);

                binding.layoutForSearchFloorInfo.setVisibility(View.VISIBLE);
                binding.floorInfoLayout.setVisibility(View.GONE);
            } else
            {
                binding.noFloor.setText(noFloor + "층");
                companyListAdapter = new CompanyListAdapter(buildingFloorDataArrayMap.get(FLOOR).getCompanyDataList());
                binding.companyRecyclerView.setAdapter(companyListAdapter);

                FacilityData facilityData = buildingFloorDataArrayMap.get(FLOOR).getFacilityData();
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

    public interface OnClickDownloadListener
    {
        void getFloorInfo(String floor, EventCallback<DataWrapper<BuildingFloorData>> callback);
    }
}
