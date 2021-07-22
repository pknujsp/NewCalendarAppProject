package com.zerodsoft.scheduleweather.navermap.adapter;

import android.content.Context;
import android.util.ArrayMap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.navermap.MarkerType;
import com.zerodsoft.scheduleweather.navermap.interfaces.ILoadLocationData;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnKakaoLocalApiCallback;
import com.zerodsoft.scheduleweather.kakaoplace.LocalParameterUtil;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddressDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceKakaoLocalResponse;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FavoriteLocationItemViewPagerAdapter extends LocationItemViewPagerAbstractAdapter {
	private List<FavoriteHolder> favoriteHolderList = new ArrayList<>();
	private ILoadLocationData iLoadLocationData;

	public FavoriteLocationItemViewPagerAdapter(Context context) {
		super(context, MarkerType.FAVORITE);
	}

	public void setiLoadLocationData(ILoadLocationData iLoadLocationData) {
		this.iLoadLocationData = iLoadLocationData;
	}

	public void setFavoriteLocationList(List<FavoriteLocationDTO> favoriteLocationList) {
		favoriteHolderList.clear();

		for (FavoriteLocationDTO favoriteLocationDTO : favoriteLocationList) {
			favoriteHolderList.add(new FavoriteHolder(favoriteLocationDTO, null));
		}
	}

	public void addFavoriteLocation(FavoriteLocationDTO newFavoriteLocationDTO) {
		favoriteHolderList.add(new FavoriteHolder(newFavoriteLocationDTO, null));
	}

	public void removeFavoriteLocation(FavoriteLocationDTO removedFavoriteLocationDTO) {
		int index = 0;

		for (FavoriteHolder favoriteHolder : favoriteHolderList) {
			if (favoriteHolder.favoriteLocationDTO.getId().equals(removedFavoriteLocationDTO.getId())) {
				favoriteHolderList.remove(index);
				break;
			}
			index++;
		}
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
		return favoriteHolderList.get(position).favoriteLocationDTO;
	}

	public int getItemPosition(FavoriteLocationDTO favoriteLocationDTO) {
		final int id = favoriteLocationDTO.getId();
		int index = 0;

		for (FavoriteHolder favoriteHolder : favoriteHolderList) {
			if (favoriteHolder.favoriteLocationDTO.getId().equals(favoriteLocationDTO.getId())) {
				break;
			}
			index++;
		}
		return index;
	}

	@Override
	public int getItemCount() {
		return favoriteHolderList.size();
	}

	@Override
	public KakaoLocalDocument getLocalItem(int position) {
		return favoriteHolderList.get(position).kakaoLocalDocument;
	}

	@Override
	public int getLocalItemPosition(KakaoLocalDocument kakaoLocalDocument) {
		int index = 0;

		for (FavoriteHolder favoriteHolder : favoriteHolderList) {
			if (favoriteHolder.kakaoLocalDocument.equals(kakaoLocalDocument)) {
				break;
			}
			index++;
		}
		return index;
	}

	@Override
	public int getItemsCount() {
		return favoriteHolderList.size();
	}

	class FavoriteLocationItemInMapViewHolder extends PlaceItemInMapViewHolder {

		public FavoriteLocationItemInMapViewHolder(@NonNull View view) {
			super(view);
		}

		@Override
		KakaoLocalDocument getKakaoLocalDocument(int position) {
			return favoriteHolderList.get(position).kakaoLocalDocument;
		}

		@Override
		public void bind() {
			final int position = getBindingAdapterPosition();

			if (favoriteHolderList.get(position).kakaoLocalDocument == null) {
				FavoriteLocationDTO favoriteLocationDTO = favoriteHolderList.get(position).favoriteLocationDTO;
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
							favoriteHolderList.get(position).kakaoLocalDocument = placeDocumentsList.get(index);
						} else {
							CoordToAddressDocuments coordToAddressDocument = ((CoordToAddress) result).getCoordToAddressDocuments().get(0);
							coordToAddressDocument.getCoordToAddressAddress().setLatitude(favoriteLocationDTO.getLatitude());
							coordToAddressDocument.getCoordToAddressAddress().setLongitude(favoriteLocationDTO.getLongitude());

							favoriteHolderList.get(position).kakaoLocalDocument = coordToAddressDocument;
						}
						setDataView(favoriteHolderList.get(position).kakaoLocalDocument);
					}

					@Override
					public void onResultNoData() {

					}
				});
			} else {
				setDataView(favoriteHolderList.get(position).kakaoLocalDocument);
			}
		}
	}

	static class FavoriteHolder {
		final FavoriteLocationDTO favoriteLocationDTO;
		KakaoLocalDocument kakaoLocalDocument;

		public FavoriteHolder(FavoriteLocationDTO favoriteLocationDTO, KakaoLocalDocument kakaoLocalDocument) {
			this.favoriteLocationDTO = favoriteLocationDTO;
			this.kakaoLocalDocument = kakaoLocalDocument;
		}
	}

}
