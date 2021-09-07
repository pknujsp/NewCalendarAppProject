package com.zerodsoft.calendarplatform.navermap.favorite;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.databinding.FavoriteLocationItemBinding;
import com.zerodsoft.calendarplatform.navermap.util.LocationUtil;
import com.zerodsoft.calendarplatform.room.dto.FavoriteLocationDTO;

import java.util.ArrayList;
import java.util.List;

public class FavoriteLocationAdapter extends RecyclerView.Adapter<FavoriteLocationAdapter.ViewHolder> {
	private final OnClickedFavoriteItem onClickedFavoriteItem;
	private final CompoundButton.OnCheckedChangeListener onCheckedChangeListener;
	private int distanceVisibility;

	private int checkBoxVisibility = View.GONE;
	private List<FavoriteLocationDTO> list = new ArrayList<>();

	public FavoriteLocationAdapter(OnClickedFavoriteItem onClickedFavoriteItem, CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
		this.onClickedFavoriteItem = onClickedFavoriteItem;
		this.onCheckedChangeListener = onCheckedChangeListener;
	}

	public void setDistanceVisibility(int distanceVisibility) {
		this.distanceVisibility = distanceVisibility;
	}

	public void setList(List<FavoriteLocationDTO> list) {
		this.list.addAll(list);
	}

	public void setCheckBoxVisibility(int checkBoxVisibility) {
		this.checkBoxVisibility = checkBoxVisibility;
	}

	public List<FavoriteLocationDTO> getList() {
		return list;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_location_item, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		holder.onBind();
	}

	@Override
	public int getItemCount() {
		return list.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		FavoriteLocationItemBinding binding;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			binding = FavoriteLocationItemBinding.bind(itemView);
			binding.addressItemLayout.distance.setVisibility(distanceVisibility);
			binding.placeItemLayout.distance.setVisibility(distanceVisibility);
		}

		public void onBind() {
			final int position = getBindingAdapterPosition();
			FavoriteLocationDTO favoriteLocationDTO = list.get(position);

			binding.checkbox.setVisibility(checkBoxVisibility);
			binding.checkbox.setChecked(false);
			binding.checkbox.setTag(favoriteLocationDTO);
			binding.checkbox.setOnCheckedChangeListener(onCheckedChangeListener);

			if (checkBoxVisibility == View.VISIBLE) {
				binding.getRoot().setClickable(false);
				binding.moreButton.setVisibility(View.GONE);
			} else {
				binding.getRoot().setClickable(true);
				binding.moreButton.setVisibility(View.VISIBLE);
				binding.getRoot().setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						onClickedFavoriteItem.onClickedListItem(favoriteLocationDTO, getBindingAdapterPosition());
					}
				});
			}

			binding.moreButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					onClickedFavoriteItem.onClickedEditButton(favoriteLocationDTO, binding.moreButton, position);
				}
			});

			String distance = LocationUtil.convertMeterToKm(String.valueOf(favoriteLocationDTO.getDistance()));

			if (favoriteLocationDTO.getType() == FavoriteLocationDTO.PLACE || favoriteLocationDTO.getType() == FavoriteLocationDTO.RESTAURANT) {
				binding.placeItemLayout.placeName.setText(favoriteLocationDTO.getPlaceName());
				binding.placeItemLayout.addressName.setText(favoriteLocationDTO.getAddress());
				binding.placeItemLayout.distance.setText(distance);
				binding.placeItemLayout.type.setText(favoriteLocationDTO.getType() == FavoriteLocationDTO.RESTAURANT ? R.string.restaurant : R.string.place);

				if (favoriteLocationDTO.getType() == FavoriteLocationDTO.RESTAURANT) {
					binding.placeItemLayout.placeCategory.setText(favoriteLocationDTO.getPlaceCategoryName());
					binding.placeItemLayout.placeCategory.setVisibility(View.VISIBLE);
				} else {
					binding.placeItemLayout.placeCategory.setVisibility(View.GONE);
				}

				binding.addressItemLayout.getRoot().setVisibility(View.GONE);
				binding.placeItemLayout.getRoot().setVisibility(View.VISIBLE);
			} else if (favoriteLocationDTO.getType() == FavoriteLocationDTO.ADDRESS) {
				binding.addressItemLayout.addressName.setText(favoriteLocationDTO.getAddress());
				binding.addressItemLayout.distance.setText(distance);
				binding.addressItemLayout.type.setText(R.string.address);

				binding.addressItemLayout.getRoot().setVisibility(View.VISIBLE);
				binding.placeItemLayout.getRoot().setVisibility(View.GONE);
			}
		}
	}
}
