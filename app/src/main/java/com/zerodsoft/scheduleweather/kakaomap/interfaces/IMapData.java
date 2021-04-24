package com.zerodsoft.scheduleweather.kakaomap.interfaces;

import com.zerodsoft.scheduleweather.kakaomap.bottomsheet.adapter.PlaceItemInMapViewAdapter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddressDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;

import java.util.List;

public interface IMapData
{
    void createPoiItems(List<? extends KakaoLocalDocument> kakaoLocalDocuments);

    void addPoiItems(List<? extends KakaoLocalDocument> kakaoLocalDocuments);

    void selectPoiItem(int index);

    void removeAllPoiItems();

    void showAllPoiItems();

    void deselectPoiItem();

    void backToPreviousView();

    int getPoiItemSize();

    void setPlacesListAdapter(PlaceItemInMapViewAdapter adapter);
}
