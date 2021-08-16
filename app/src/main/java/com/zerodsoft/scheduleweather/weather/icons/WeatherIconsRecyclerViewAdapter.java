package com.zerodsoft.scheduleweather.weather.icons;

import android.graphics.drawable.Drawable;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.LayoutInflaterCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WeatherIconsRecyclerViewAdapter extends RecyclerView.Adapter<WeatherIconsRecyclerViewAdapter.ViewHolder> {
	private List<Drawable> iconsList;
	private List<String> descriptionList;

	public WeatherIconsRecyclerViewAdapter(List<Drawable> iconsList, List<String> descriptionList) {
		this.iconsList = iconsList;
		this.descriptionList = descriptionList;
	}

	@NonNull
	@NotNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_icons_list_item, null, false));
	}

	@Override
	public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
		holder.onBind();
	}

	@Override
	public int getItemCount() {
		return iconsList.size();
	}


	class ViewHolder extends RecyclerView.ViewHolder {
		ImageView icon;
		TextView description;

		public ViewHolder(@NonNull @NotNull View itemView) {
			super(itemView);

			icon = (ImageView) itemView.findViewById(R.id.icon);
			description = (TextView) itemView.findViewById(R.id.description);
		}

		public void onBind() {
			int index = getBindingAdapterPosition();
			icon.setImageDrawable(iconsList.get(index));
			description.setText(descriptionList.get(index));
		}
	}

}
