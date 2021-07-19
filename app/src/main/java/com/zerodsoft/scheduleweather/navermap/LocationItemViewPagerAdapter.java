package com.zerodsoft.scheduleweather.navermap;

import android.content.Context;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddressDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;

import java.util.ArrayList;
import java.util.List;

public class LocationItemViewPagerAdapter extends LocationItemViewPagerAbstractAdapter {
	List<KakaoLocalDocument> kakaoLocalDocumentList = new ArrayList<>();

	public LocationItemViewPagerAdapter(Context context, MarkerType MARKER_TYPE) {
		super(context, MARKER_TYPE);
	}

	public void setLocalDocumentsList(List<? extends KakaoLocalDocument> localDocumentsList) {
		this.kakaoLocalDocumentList.addAll(localDocumentsList);
	}

	public List<KakaoLocalDocument> getLocalDocumentsList() {
		return kakaoLocalDocumentList;
	}

	@Override
	public int getItemCount() {
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
}
