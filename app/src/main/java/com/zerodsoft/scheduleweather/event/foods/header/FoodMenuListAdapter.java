package com.zerodsoft.scheduleweather.event.foods.header;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.event.foods.dto.FoodCategoryItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FoodMenuListAdapter extends RecyclerView.Adapter<FoodMenuListAdapter.ViewHolder> {
	private List<FoodCategoryItem> items = new ArrayList<>();
	private final OnClickedListItem<FoodCategoryItem> onClickedCategoryItem;

	public FoodMenuListAdapter(OnClickedListItem<FoodCategoryItem> onClickedCategoryItem) {
		this.onClickedCategoryItem = onClickedCategoryItem;
	}

	public void setItems(List<FoodCategoryItem> items) {
		this.items = items;
	}

	@NonNull
	@NotNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		LinearLayout layoutView = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.food_category_item, parent, false);
		final int CHILD_VIEW_SIZE = parent.getHeight();

		layoutView.getLayoutParams().width = CHILD_VIEW_SIZE;
		layoutView.getLayoutParams().height = CHILD_VIEW_SIZE;

		return new ViewHolder(layoutView);
	}

	@Override
	public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
		holder.onBind();
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		ImageView foodImgView;
		TextView foodMenuNameTextView;

		public ViewHolder(@NonNull @NotNull View itemView) {
			super(itemView);

			foodImgView = (ImageView) itemView.findViewById(R.id.food_category_image);
			foodMenuNameTextView = (TextView) itemView.findViewById(R.id.food_category_name);
		}

		public void onBind() {
			final int position = getBindingAdapterPosition();

			FoodCategoryItem foodCategoryItem = items.get(position);
			foodMenuNameTextView.setText(foodCategoryItem.getCategoryName());

			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) foodMenuNameTextView.getLayoutParams();

			if (foodCategoryItem.isDefault()) {
				foodImgView.setImageDrawable(foodCategoryItem.getCategoryMainImage());
				Glide.with(itemView).load(foodCategoryItem.getCategoryMainImage()).circleCrop().into(foodImgView);

				layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
				foodImgView.setVisibility(View.VISIBLE);
			} else {
				layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
				foodImgView.setVisibility(View.GONE);
			}

			foodMenuNameTextView.requestLayout();
			foodMenuNameTextView.invalidate();

			itemView.getRootView().setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					onClickedCategoryItem.onClickedListItem(foodCategoryItem, position);
				}
			});
		}
	}
}
