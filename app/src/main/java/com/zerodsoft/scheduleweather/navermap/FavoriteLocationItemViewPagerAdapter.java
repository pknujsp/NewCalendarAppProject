package com.zerodsoft.scheduleweather.navermap;

import android.content.Context;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.common.view.CustomProgressView;
import com.zerodsoft.scheduleweather.event.common.interfaces.ILocationDao;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnClickedBottomSheetListener;
import com.zerodsoft.scheduleweather.navermap.interfaces.PlacesItemBottomSheetButtonOnClickListener;
import com.zerodsoft.scheduleweather.navermap.model.CoordToAddressUtilByKakao;
import com.zerodsoft.scheduleweather.navermap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddressDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceKakaoLocalResponse;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FavoriteLocationItemViewPagerAdapter extends LocationItemViewPagerAdapter {
	private ArrayMap<FavoriteLocationDTO, KakaoLocalDocument> favoriteLocationsMap = new ArrayMap<>();
	private List<FavoriteLocationDTO> favoriteLocationList = new ArrayList<>();

	public FavoriteLocationItemViewPagerAdapter(Context context) {
		super(context, MarkerType.FAVORITE);
	}

	public void setFavoriteLocationList(List<FavoriteLocationDTO> favoriteLocationList) {
		this.favoriteLocationList = favoriteLocationList;
		for (FavoriteLocationDTO favoriteLocationDTO : favoriteLocationList) {
			favoriteLocationsMap.put(favoriteLocationDTO, null);
		}
	}

	public void addFavoriteLocation(FavoriteLocationDTO newFavoriteLocationDTO) {
		favoriteLocationList.add(newFavoriteLocationDTO);
		favoriteLocationsMap.put(newFavoriteLocationDTO, null);
	}

	public int removeFavoriteLocation(FavoriteLocationDTO removedFavoriteLocationDTO) {
		int index = 0;
		for (FavoriteLocationDTO favoriteLocationDTO : favoriteLocationList) {
			if (removedFavoriteLocationDTO.equals(favoriteLocationDTO)) {
				favoriteLocationList.remove(index);
				favoriteLocationsMap.removeAt(index);
				break;
			}
			index++;
		}
		return index;
	}


	public void setFavoriteLocationsMap(ArrayMap<FavoriteLocationDTO, KakaoLocalDocument> favoriteLocationsMap) {
		this.favoriteLocationsMap.putAll(favoriteLocationsMap);
		int size = favoriteLocationsMap.size();
		for (int i = 0; i < size; i++) {
			placeDocumentsList.add(favoriteLocationsMap.get(favoriteLocationsMap.keyAt(i)));
		}
	}

	public List<FavoriteLocationDTO> getFavoriteLocationList() {
		return favoriteLocationList;
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
		protected void onClickedFavoriteBtn() {
			super.onClickedFavoriteBtn();
			int position = getBindingAdapterPosition();
			placeDocumentsList.remove(position);
			favoriteLocationList.remove(position);
			favoriteLocationsMap.removeAt(position);
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
