package com.zerodsoft.calendarplatform.navermap.building.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.common.interfaces.OnClickedListItem;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.sgis.building.buildingarea.BuildingAreaItem;

import java.util.List;

public class BuildingListAdapter extends RecyclerView.Adapter<BuildingListAdapter.ViewHolder>
{
    private List<BuildingAreaItem> buildingList;
    private final OnClickedListItem<BuildingAreaItem> onClickedListItem;

    public BuildingListAdapter(List<BuildingAreaItem> buildingList, OnClickedListItem<BuildingAreaItem> onClickedListItem)
    {
        this.buildingList = buildingList;
        this.onClickedListItem = onClickedListItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.building_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.onBind();
    }

    @Override
    public int getItemCount()
    {
        return buildingList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView buildingName;
        TextView buildingAddress;
        TextView minFloor;
        TextView maxFloor;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            buildingName = (TextView) itemView.findViewById(R.id.building_name);
            buildingAddress = (TextView) itemView.findViewById(R.id.building_address);
            minFloor = (TextView) itemView.findViewById(R.id.building_min_floor);
            maxFloor = (TextView) itemView.findViewById(R.id.building_max_floor);
        }

        public void onBind()
        {
            BuildingAreaItem building = buildingList.get(getBindingAdapterPosition());

            buildingName.setText(building.getBdName() == null ? itemView.getContext().getString(R.string.not_data) : building.getBdName());
            buildingAddress.setText(building.getBdNewAddress() == null ? itemView.getContext().getString(R.string.not_data) : building.getBdNewAddress());
            minFloor.setText(building.getLowestFloor() == null ? itemView.getContext().getString(R.string.not_data) : building.getLowestFloor());
            maxFloor.setText(building.getHighestFloor() == null ? itemView.getContext().getString(R.string.not_data) : building.getHighestFloor());

            itemView.getRootView().setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    onClickedListItem.onClickedListItem(building, getBindingAdapterPosition());
                }
            });

        }
    }

}
