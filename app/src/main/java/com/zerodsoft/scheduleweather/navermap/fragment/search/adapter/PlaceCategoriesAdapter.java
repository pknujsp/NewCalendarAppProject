package com.zerodsoft.scheduleweather.navermap.fragment.search.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnSelectedMapCategory;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;
import com.zerodsoft.scheduleweather.room.dto.SearchHistoryDTO;

import java.util.List;

public class PlaceCategoriesAdapter extends RecyclerView.Adapter<PlaceCategoriesAdapter.PlaceCategoryViewHolder> {
	private final List<PlaceCategoryDTO> categoryList;
	private final OnClickedListItem<PlaceCategoryDTO> onClickedListItemOnPlaceCategory;

	public PlaceCategoriesAdapter(OnClickedListItem<PlaceCategoryDTO> onClickedListItemOnPlaceCategory) {
		categoryList = KakaoLocalApiCategoryUtil.getDefaultPlaceCategoryList();
		this.onClickedListItemOnPlaceCategory = onClickedListItemOnPlaceCategory;
	}

	@NonNull
	@Override
	public PlaceCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new PlaceCategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_category_recyclerview_item, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull PlaceCategoryViewHolder holder, int position) {
		holder.onBind();
	}

	@Override
	public int getItemCount() {
		return categoryList.size();
	}

	class PlaceCategoryViewHolder extends RecyclerView.ViewHolder {
		private TextView categoryDescriptionTextView;

		PlaceCategoryViewHolder(View view) {
			super(view);
			categoryDescriptionTextView = (TextView) view.findViewById(R.id.category_description_textview);
		}

		public void onBind() {
			categoryDescriptionTextView.setText(categoryList.get(getBindingAdapterPosition()).getDescription());
			categoryDescriptionTextView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					onClickedListItemOnPlaceCategory.onClickedListItem(categoryList.get(getBindingAdapterPosition()), getBindingAdapterPosition());
				}
			});
		}
	}
}
