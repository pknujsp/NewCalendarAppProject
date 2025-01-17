package com.zerodsoft.calendarplatform.navermap.places.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.navermap.callback.PlaceItemCallback;
import com.zerodsoft.calendarplatform.navermap.util.LocationUtil;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.calendarplatform.navermap.places.interfaces.OnClickedPlacesListListener;
import com.zerodsoft.calendarplatform.room.dto.PlaceCategoryDTO;

public class PlaceItemsAdapters extends PagedListAdapter<PlaceDocuments, PlaceItemsAdapters.ItemViewHolder> {
	private final OnClickedPlacesListListener onClickedPlacesListListener;
	private final PlaceCategoryDTO placeCategoryDTO;


	public PlaceItemsAdapters(OnClickedPlacesListListener onClickedPlacesListListener, PlaceCategoryDTO placeCategoryDTO) {
		super(new PlaceItemCallback());
		this.onClickedPlacesListListener = onClickedPlacesListListener;
		this.placeCategoryDTO = placeCategoryDTO;
	}

	class ItemViewHolder extends RecyclerView.ViewHolder {
		private TextView placeNameTextView;
		private TextView placeAddressTextView;
		private TextView placeCategoryTextView;
		private TextView placeDistanceTextView;

		public ItemViewHolder(View view) {
			super(view);
			placeNameTextView = (TextView) view.findViewById(R.id.place_item_name);
			placeAddressTextView = (TextView) view.findViewById(R.id.place_item_address);
			placeCategoryTextView = (TextView) view.findViewById(R.id.place_item_category);
			placeDistanceTextView = (TextView) view.findViewById(R.id.place_item_distance);
		}

		public void bind(PlaceDocuments item) {
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					onClickedPlacesListListener.onClickedItemInList(placeCategoryDTO, item, getBindingAdapterPosition());
				}
			});

			placeNameTextView.setText(item.getPlaceName());
			placeAddressTextView.setText(item.getAddressName());
			placeCategoryTextView.setText(item.getCategoryName());
			placeDistanceTextView.setText(LocationUtil.convertMeterToKm(item.getDistance()));
		}
	}


	@NonNull
	@Override
	public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item_around_location, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
		holder.bind(getItem(position));
	}

	@Override
	public void submitList(@Nullable PagedList<PlaceDocuments> pagedList) {
		super.submitList(pagedList);
	}


}