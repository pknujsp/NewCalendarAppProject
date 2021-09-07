package com.zerodsoft.calendarplatform.event.foods.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.etc.LocationType;
import com.zerodsoft.calendarplatform.event.foods.interfaces.LocationHistoryController;
import com.zerodsoft.calendarplatform.room.dto.FoodCriteriaLocationSearchHistoryDTO;

import java.util.ArrayList;
import java.util.List;

public class FoodCriteriaLocationHistoryAdapter extends RecyclerView.Adapter<FoodCriteriaLocationHistoryAdapter.HistoryViewHolder> {
	private final LocationHistoryController locationHistoryController;
	private List<FoodCriteriaLocationSearchHistoryDTO> foodCriteriaLocationHistoryList = new ArrayList<>();

	public FoodCriteriaLocationHistoryAdapter(LocationHistoryController locationHistoryController) {
		this.locationHistoryController = locationHistoryController;
	}

	public List<FoodCriteriaLocationSearchHistoryDTO> getFoodCriteriaLocationHistoryList() {
		return foodCriteriaLocationHistoryList;
	}

	public void setFoodCriteriaLocationHistoryList(List<FoodCriteriaLocationSearchHistoryDTO> foodCriteriaLocationHistoryList) {
		this.foodCriteriaLocationHistoryList = foodCriteriaLocationHistoryList;
	}

	@NonNull
	@Override
	public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new HistoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_location_history_item, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
		holder.onBind();
	}

	@Override
	public int getItemCount() {
		return foodCriteriaLocationHistoryList.size();
	}

	class HistoryViewHolder extends RecyclerView.ViewHolder {
		private LinearLayout rootLayout;
		private TextView placeNameTextView;
		private TextView addressNameTextView;
		private ImageButton deleteButton;

		public HistoryViewHolder(@NonNull View itemView) {
			super(itemView);

			rootLayout = (LinearLayout) itemView.findViewById(R.id.root_layout);
			placeNameTextView = (TextView) itemView.findViewById(R.id.place_name);
			addressNameTextView = (TextView) itemView.findViewById(R.id.address_name);
			deleteButton = (ImageButton) itemView.findViewById(R.id.delete_button);
		}

		public void onBind() {
			FoodCriteriaLocationSearchHistoryDTO history = foodCriteriaLocationHistoryList.get(getBindingAdapterPosition());

			if (history.getLocationType() == LocationType.PLACE) {
				placeNameTextView.setText(history.getPlaceName());
				placeNameTextView.setVisibility(View.VISIBLE);
			} else {
				placeNameTextView.setVisibility(View.GONE);
			}
			addressNameTextView.setText(history.getAddressName());
			addressNameTextView.setVisibility(View.VISIBLE);

			rootLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//해당 위치를 선택
					locationHistoryController.onClickedLocationHistoryItem(history);
				}
			});

			deleteButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					locationHistoryController.delete(history.getId());
				}
			});
		}
	}
}
