package com.zerodsoft.scheduleweather.navermap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.navermap.MarkerType;
import com.zerodsoft.scheduleweather.navermap.interfaces.ILoadLocationData;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnCoordToAddressListener;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnKakaoLocalApiCallback;
import com.zerodsoft.scheduleweather.navermap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddressDocuments;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;

import org.jetbrains.annotations.NotNull;

public class OnLongClickMapLocationItemAdapter extends LocationItemViewPagerAbstractAdapter {
	private final ILoadLocationData iLoadLocationData;
	private CoordToAddressDocuments coordToAddressDocument;
	private String latitude;
	private String longitude;

	public OnLongClickMapLocationItemAdapter(Context context, ILoadLocationData iLoadLocationData) {
		super(context, MarkerType.LONG_CLICKED_MAP);
		this.iLoadLocationData = iLoadLocationData;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	@Override
	public int getItemsCount() {
		return 1;
	}

	@NonNull
	@NotNull
	@Override
	public OnLongClickMapLocationItemViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		return new OnLongClickMapLocationItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_places_item, parent, false));
	}


	@Override
	public void onBindViewHolder(@NonNull @NotNull PlaceItemInMapViewHolder holder, int position) {
		holder.bind();
	}

	@Override
	public KakaoLocalDocument getLocalItem(int position) {
		return coordToAddressDocument;
	}

	@Override
	public int getItemCount() {
		return 1;
	}

	@Override
	public int getLocalItemPosition(KakaoLocalDocument kakaoLocalDocument) {
		return 1;
	}

	class OnLongClickMapLocationItemViewHolder extends PlaceItemInMapViewHolder {

		public OnLongClickMapLocationItemViewHolder(@NonNull @NotNull View view) {
			super(view);
			binding.unselectThisPlaceButton.setVisibility(View.GONE);
			binding.itemPosition.setVisibility(View.GONE);
		}

		@Override
		public void bind() {
			LocalApiPlaceParameter parameter = LocalParameterUtil.getCoordToAddressParameter(Double.parseDouble(latitude), Double.parseDouble(longitude));
			iLoadLocationData.loadLocationData(FavoriteLocationDTO.ADDRESS, parameter,
					new OnKakaoLocalApiCallback() {
						@Override
						public void onResultSuccessful(int type, KakaoLocalResponse result) {
							coordToAddressDocument = ((CoordToAddress) result).getCoordToAddressDocuments().get(0);
							coordToAddressDocument.getCoordToAddressAddress().setLatitude(latitude);
							coordToAddressDocument.getCoordToAddressAddress().setLongitude(longitude);

							setDataView(coordToAddressDocument);
						}

						@Override
						public void onResultNoData() {

						}
					});

		}

		@Override
		KakaoLocalDocument getKakaoLocalDocument(int position) {
			return coordToAddressDocument;
		}
	}

}
