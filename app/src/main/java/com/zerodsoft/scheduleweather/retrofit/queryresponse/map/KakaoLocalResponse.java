package com.zerodsoft.scheduleweather.retrofit.queryresponse.map;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressKakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoregioncoderesponse.CoordToRegionCode;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceKakaoLocalResponse;

public class KakaoLocalResponse {
	public boolean isEmpty() {
		boolean isEmpty = false;

		if (this instanceof PlaceKakaoLocalResponse) {
			isEmpty = ((PlaceKakaoLocalResponse) this).getPlaceDocuments().isEmpty();
		} else if (this instanceof AddressKakaoLocalResponse) {
			isEmpty = ((AddressKakaoLocalResponse) this).getAddressResponseDocumentsList().isEmpty();
		} else if (this instanceof CoordToAddress) {
			isEmpty = ((CoordToAddress) this).getCoordToAddressDocuments().isEmpty();
		} else if (this instanceof CoordToRegionCode) {
			isEmpty = ((CoordToRegionCode) this).getCoordToRegionCodeDocuments().isEmpty();
		}

		return isEmpty;
	}

	public int size() {
		int size = 0;

		if (this instanceof PlaceKakaoLocalResponse) {
			size = ((PlaceKakaoLocalResponse) this).getPlaceDocuments().size();
		} else if (this instanceof AddressKakaoLocalResponse) {
			size = ((AddressKakaoLocalResponse) this).getAddressResponseDocumentsList().size();
		} else if (this instanceof CoordToAddress) {
			size = ((CoordToAddress) this).getCoordToAddressDocuments().size();
		} else if (this instanceof CoordToRegionCode) {
			size = ((CoordToRegionCode) this).getCoordToRegionCodeDocuments().size();
		}

		return size;
	}
}
