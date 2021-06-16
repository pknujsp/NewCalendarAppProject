package com.zerodsoft.scheduleweather.activity.placecategory.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.placecategory.interfaces.OnItemMoveListener;
import com.zerodsoft.scheduleweather.event.places.interfaces.PlaceCategory;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlaceCategoryAdapter extends RecyclerView.Adapter<PlaceCategoryAdapter.CategoryViewHolder> implements OnItemMoveListener {
	private List<PlaceCategoryDTO> placeCategoryList = new ArrayList<>();
	private final OnStartDragListener onStartDragListener;

	public PlaceCategoryAdapter(OnStartDragListener onStartDragListener) {
		this.onStartDragListener = onStartDragListener;
	}

	public void setPlaceCategoryList(List<PlaceCategoryDTO> placeCategoryList) {
		this.placeCategoryList.clear();
		this.placeCategoryList.addAll(placeCategoryList);
	}

	public List<PlaceCategoryDTO> getPlaceCategoryList() {
		return placeCategoryList;
	}

	public int getItemPosition(String code) {
		int position = 0;
		for (PlaceCategoryDTO placeCategory : placeCategoryList) {
			if (placeCategory.getCode().equals(code)) {
				break;
			}
			position++;
		}

		return position;
	}

	@Override
	public boolean onItemMove(int fromPosition, int toPosition) {
		Collections.swap(placeCategoryList, fromPosition, toPosition);
		notifyItemMoved(fromPosition, toPosition);
		return true;
	}

	@Override
	public boolean onItemSwiped(int position) {
		placeCategoryList.remove(position);
		notifyItemRemoved(position);
		return true;
	}


	@NonNull
	@Override
	public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new CategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.place_category_row_item, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
		holder.onBind();
	}

	@Override
	public int getItemCount() {
		return placeCategoryList.size();
	}

	public class CategoryViewHolder extends RecyclerView.ViewHolder {
		private TextView categoryDescription;
		private TextView categoryType;
		private ImageButton dragHandle;

		public CategoryViewHolder(@NonNull View itemView) {
			super(itemView);
			categoryDescription = (TextView) itemView.findViewById(R.id.category_description);
			categoryType = (TextView) itemView.findViewById(R.id.category_type);
			dragHandle = (ImageButton) itemView.findViewById(R.id.category_drag_handle);
		}

		@SuppressLint("ClickableViewAccessibility")
		public void onBind() {
			categoryDescription.setText(placeCategoryList.get(getBindingAdapterPosition()).getDescription());
			categoryType.setText(!placeCategoryList.get(getBindingAdapterPosition()).isCustom() ?
					itemView.getContext().getString(R.string.default_category) : itemView.getContext().getString(R.string.custom_category));

			dragHandle.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent motionEvent) {
					if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
						onStartDragListener.onStartDrag(CategoryViewHolder.this);
					}
					return false;
				}
			});

		}
	}

	public interface OnStartDragListener {
		void onStartDrag(CategoryViewHolder viewHolder);
	}


}
