package com.zerodsoft.calendarplatform.activity.preferences.customfoodmenu.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.common.interfaces.OnClickedListItem;
import com.zerodsoft.calendarplatform.room.dto.CustomFoodMenuDTO;

import java.util.ArrayList;
import java.util.List;

public class CustomFoodMenuAdapter extends RecyclerView.Adapter<CustomFoodMenuAdapter.ViewHolder> {
	private List<CustomFoodMenuDTO> list = new ArrayList<>();
	private OnClickedListItem<CustomFoodMenuDTO> onClickedListItem;

	public CustomFoodMenuAdapter(OnClickedListItem<CustomFoodMenuDTO> onClickedListItem) {
		this.onClickedListItem = onClickedListItem;
	}

	public void setList(List<CustomFoodMenuDTO> list) {
		this.list.addAll(list);
	}

	public List<CustomFoodMenuDTO> getList() {
		return list;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_history_item, parent, false));
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
		TextView valueTextView;
		ImageButton deleteButton;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);

			valueTextView = (TextView) itemView.findViewById(R.id.value);
			deleteButton = (ImageButton) itemView.findViewById(R.id.delete);
		}

		public void onBind() {
			CustomFoodMenuDTO customFoodMenuDTO = list.get(getBindingAdapterPosition());

			valueTextView.setText(customFoodMenuDTO.getMenuName());
			deleteButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					onClickedListItem.deleteListItem(customFoodMenuDTO, getBindingAdapterPosition());
				}
			});
		}
	}
}
