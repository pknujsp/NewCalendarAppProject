package com.zerodsoft.scheduleweather.navermap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.navermap.MarkerType;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddressDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LocationItemViewPagerAdapter extends LocationItemViewPagerAbstractAdapter {
	private List<KakaoLocalDocument> kakaoLocalDocumentList = new ArrayList<>();

	public LocationItemViewPagerAdapter(Context context, MarkerType MARKER_TYPE) {
		super(context, MARKER_TYPE);
	}

	public void setLocalDocumentsList(List<? extends KakaoLocalDocument> localDocumentsList) {
		this.kakaoLocalDocumentList.clear();
		this.kakaoLocalDocumentList.addAll(localDocumentsList);
	}

	public List<KakaoLocalDocument> getLocalDocumentsList() {
		return kakaoLocalDocumentList;
	}

	@NonNull
	@Override
	public LocationItemInMapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new LocationItemInMapViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_places_item, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull @NotNull PlaceItemInMapViewHolder holder, int position) {
		holder.bind();
	}

	@Override
	public int getItemCount() {
		return kakaoLocalDocumentList.size();
	}

	@Override
	public KakaoLocalDocument getLocalItem(int position) {
		return kakaoLocalDocumentList.get(position);
	}

	@Override
	public int getLocalItemPosition(KakaoLocalDocument kakaoLocalDocument) {
		int i = 0;
		for (; i < kakaoLocalDocumentList.size(); i++) {
			if (kakaoLocalDocumentList.get(i).equals(kakaoLocalDocument)) {
				break;
			}
		}
		return i;
	}

	@Override
	public int getItemsCount() {
		return kakaoLocalDocumentList.size();
	}

	public int getItemPosition(KakaoLocalDocument kakaoLocalDocument) {
		int position = 0;

		if (kakaoLocalDocument instanceof PlaceDocuments) {
			String placeId = ((PlaceDocuments) kakaoLocalDocument).getId();

			for (KakaoLocalDocument document : kakaoLocalDocumentList) {
				if (((PlaceDocuments) document).getId().equals(placeId)) {
					break;
				}
				position++;
			}
		} else if (kakaoLocalDocument instanceof AddressResponseDocuments) {
			String x = ((AddressResponseDocuments) kakaoLocalDocument).getX();
			String y = ((AddressResponseDocuments) kakaoLocalDocument).getY();

			AddressResponseDocuments addressResponseDocument = null;
			for (KakaoLocalDocument document : kakaoLocalDocumentList) {
				addressResponseDocument = (AddressResponseDocuments) document;

				if (addressResponseDocument.getX().equals(x) &&
						addressResponseDocument.getY().equals(y)) {
					break;
				}
				position++;
			}
		} else if (kakaoLocalDocument instanceof CoordToAddressDocuments) {
			String longitude = ((CoordToAddressDocuments) kakaoLocalDocument).getCoordToAddressAddress().getLongitude();
			String latitude = ((CoordToAddressDocuments) kakaoLocalDocument).getCoordToAddressAddress().getLatitude();

			CoordToAddressDocuments coordToAddressDocument = null;
			for (KakaoLocalDocument document : kakaoLocalDocumentList) {
				coordToAddressDocument = (CoordToAddressDocuments) document;

				if (coordToAddressDocument.getCoordToAddressAddress().getLatitude().equals(latitude) &&
						coordToAddressDocument.getCoordToAddressAddress().getLongitude().equals(longitude)) {
					break;
				}
				position++;
			}
		}

		return position;
	}

	class LocationItemInMapViewHolder extends PlaceItemInMapViewHolder {
		public LocationItemInMapViewHolder(@NonNull View view) {
			super(view);
		}

		@Override
		KakaoLocalDocument getKakaoLocalDocument(int position) {
			return kakaoLocalDocumentList.get(position);
		}

		@Override
		public void bind() {
			setDataView(getKakaoLocalDocument(getBindingAdapterPosition()));
		}

	}
}
