package com.zerodsoft.scheduleweather.navermap.adapter;

import android.content.Context;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.navermap.MarkerType;
import com.zerodsoft.scheduleweather.navermap.interfaces.ILoadLocationData;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnKakaoLocalApiCallback;
import com.zerodsoft.scheduleweather.navermap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddressDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceKakaoLocalResponse;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class FavoriteLocationItemViewPagerAdapter extends LocationItemViewPagerAbstractAdapter {
	private ArrayMap<FavoriteLocationDTO, KakaoLocalDocument> favoriteLocationsMap = new ArrayMap<>();
	private ILoadLocationData iLoadLocationData;

	public FavoriteLocationItemViewPagerAdapter(Context context) {
		super(context, MarkerType.FAVORITE);
	}

	public void setiLoadLocationData(ILoadLocationData iLoadLocationData) {
		this.iLoadLocationData = iLoadLocationData;
	}

	public void setFavoriteLocationList(List<FavoriteLocationDTO> favoriteLocationList) {
		favoriteLocationsMap.clear();

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
			if (removedFavoriteLocationDTO.getId().equals(favoriteLocationDTO.getId())) {
				favoriteLocationsMap.removeAt(index);
				break;
			}
			index++;
		}
	}

	public ArrayMap<FavoriteLocationDTO, KakaoLocalDocument> getFavoriteLocationsMap() {
		return favoriteLocationsMap;
	}

	@NonNull
	@Override
	public FavoriteLocationItemInMapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new FavoriteLocationItemInMapViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_places_item, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull @NotNull PlaceItemInMapViewHolder holder, int position) {
		holder.bind();
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

	@Override
	public KakaoLocalDocument getLocalItem(int position) {
		return favoriteLocationsMap.valueAt(position);
	}

	@Override
	public int getLocalItemPosition(KakaoLocalDocument kakaoLocalDocument) {
		int i = 0;
		for (; i < favoriteLocationsMap.size(); i++) {
			if (favoriteLocationsMap.valueAt(i).equals(kakaoLocalDocument)) {
				break;
			}
		}
		return i;
	}

	@Override
	public int getItemsCount() {
		return favoriteLocationsMap.size();
	}

	class FavoriteLocationItemInMapViewHolder extends PlaceItemInMapViewHolder {

		public FavoriteLocationItemInMapViewHolder(@NonNull View view) {
			super(view);
		}

		@Override
		KakaoLocalDocument getKakaoLocalDocument(int position) {
			return favoriteLocationsMap.valueAt(position);
		}

		@Override
		public void bind() {
			final int position = getBindingAdapterPosition();

			if (favoriteLocationsMap.valueAt(position) == null) {
				FavoriteLocationDTO favoriteLocationDTO = favoriteLocationsMap.keyAt(position);
				LocalApiPlaceParameter parameter = null;

				if (favoriteLocationDTO.getType() == FavoriteLocationDTO.ADDRESS) {
					parameter = LocalParameterUtil.getCoordToAddressParameter
							(Double.parseDouble(favoriteLocationDTO.getLatitude()), Double.parseDouble(favoriteLocationDTO.getLongitude()));
				} else {
					parameter = LocalParameterUtil.getPlaceParameter(favoriteLocationDTO.getPlaceName(),
							String.valueOf(favoriteLocationDTO.getLatitude()), String.valueOf(favoriteLocationDTO.getLongitude()), LocalApiPlaceParameter.DEFAULT_SIZE,
							LocalApiPlaceParameter.DEFAULT_PAGE, LocalApiPlaceParameter.SEARCH_CRITERIA_SORT_TYPE_ACCURACY);
					parameter.setRadius("30");
				}

				iLoadLocationData.loadLocationData(favoriteLocationDTO.getType(), parameter, new OnKakaoLocalApiCallback() {
					@Override
					public void onResultSuccessful(int type, KakaoLocalResponse result) {
						if (type == FavoriteLocationDTO.PLACE || type == FavoriteLocationDTO.RESTAURANT) {
							int index = 0;
							List<PlaceDocuments> placeDocumentsList = ((PlaceKakaoLocalResponse) result).getPlaceDocuments();

							for (PlaceDocuments placeDocument : placeDocumentsList) {
								if (placeDocument.getId().equals(favoriteLocationDTO.getPlaceId())) {
									break;
								}
								index++;
							}
							favoriteLocationsMap.put(favoriteLocationDTO, placeDocumentsList.get(index));
						} else {
							CoordToAddressDocuments coordToAddressDocument = ((CoordToAddress) result).getCoordToAddressDocuments().get(0);
							coordToAddressDocument.getCoordToAddressAddress().setLatitude(favoriteLocationDTO.getLatitude());
							coordToAddressDocument.getCoordToAddressAddress().setLongitude(favoriteLocationDTO.getLongitude());
							favoriteLocationsMap.put(favoriteLocationDTO, coordToAddressDocument);
						}
						setDataView(favoriteLocationsMap.valueAt(position));
					}

					@Override
					public void onResultNoData() {

					}
				});
			} else {
				setDataView(favoriteLocationsMap.valueAt(position));
			}
		}
	}

}
