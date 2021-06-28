package com.zerodsoft.scheduleweather.navermap;

import android.content.Context;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnClickedBottomSheetListener;
import com.zerodsoft.scheduleweather.navermap.interfaces.PlacesItemBottomSheetButtonOnClickListener;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FavoriteLocationItemViewPagerAdapter extends LocationItemViewPagerAdapter {
	private ArrayMap<FavoriteLocationDTO, KakaoLocalDocument> favoriteLocationsMap = new ArrayMap<>();

	public FavoriteLocationItemViewPagerAdapter(Context context) {
		super(context, MarkerType.FAVORITE);
	}

	public void setFavoriteLocationList(List<FavoriteLocationDTO> favoriteLocationList) {
		for (FavoriteLocationDTO favoriteLocationDTO : favoriteLocationList) {
			favoriteLocationsMap.put(favoriteLocationDTO, null);
		}
	}

	public void addFavoriteLocation(FavoriteLocationDTO newFavoriteLocationDTO) {
		favoriteLocationsMap.put(newFavoriteLocationDTO, null);
	}

	public void removeFavoriteLocation(FavoriteLocationDTO removedFavoriteLocationDTO) {
		int index = 0;
		Set<FavoriteLocationDTO> keySet = favoriteLocationsMap.keySet();

		for (FavoriteLocationDTO favoriteLocationDTO : keySet) {
			if (removedFavoriteLocationDTO.equals(favoriteLocationDTO)) {
				favoriteLocationsMap.removeAt(index);
				break;
			}
			index++;
		}
	}

	public void setFavoriteLocationsMap(ArrayMap<FavoriteLocationDTO, KakaoLocalDocument> favoriteLocationsMap) {
		this.favoriteLocationsMap.putAll(favoriteLocationsMap);
	}

	public ArrayMap<FavoriteLocationDTO, KakaoLocalDocument> getFavoriteLocationsMap() {
		return favoriteLocationsMap;
	}

	@Override
	public void setPlacesItemBottomSheetButtonOnClickListener(PlacesItemBottomSheetButtonOnClickListener placesItemBottomSheetButtonOnClickListener) {
		super.setPlacesItemBottomSheetButtonOnClickListener(placesItemBottomSheetButtonOnClickListener);
	}

	@Override
	public void setOnClickedBottomSheetListener(OnClickedBottomSheetListener onClickedBottomSheetListener) {
		super.setOnClickedBottomSheetListener(onClickedBottomSheetListener);
	}

	@NonNull
	@Override
	public FavoriteLocationItemInMapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new FavoriteLocationItemInMapViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_places_item, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull @NotNull PlaceItemInMapViewHolder holder, int position) {
		((FavoriteLocationItemInMapViewHolder) holder).bind();
	}

	public FavoriteLocationDTO getKey(int position) {
		return favoriteLocationsMap.keyAt(position);
	}

	public int getItemPosition(FavoriteLocationDTO favoriteLocationDTO) {
		final int id = favoriteLocationDTO.getId();
		Set<FavoriteLocationDTO> keySet = favoriteLocationsMap.keySet();

		int position = 0;
		for (FavoriteLocationDTO favoriteLocation : keySet) {
			if (id == favoriteLocation.getId()) {
				break;
			}
			position++;
		}

		return position;
	}

	@Override
	public int getItemCount() {
		return favoriteLocationsMap.size();
	}

	class FavoriteLocationItemInMapViewHolder extends PlaceItemInMapViewHolder {

		public FavoriteLocationItemInMapViewHolder(@NonNull View view) {
			super(view);
			customProgressView.onStartedProcessingData();
		}

		@Override
		protected void onClickedFavoriteBtn(int position) {
			processOnClickedFavoriteBtn(favoriteLocationsMap.get(favoriteLocationsMap.keyAt(position)));
		}

		public void bind() {
			final int position = getBindingAdapterPosition();

			if (favoriteLocationsMap.get(favoriteLocationsMap.keyAt(position)) != null) {
				setDataView(favoriteLocationsMap.get(favoriteLocationsMap.keyAt(position)));
			}
		}

		@Override
		public void setDataView(KakaoLocalDocument kakaoLocalDocument) {
			super.setDataView(kakaoLocalDocument);
		}
	}
}
