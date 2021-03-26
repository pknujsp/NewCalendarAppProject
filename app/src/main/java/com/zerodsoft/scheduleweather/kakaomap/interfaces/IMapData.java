package com.zerodsoft.scheduleweather.kakaomap.interfaces;

import com.zerodsoft.scheduleweather.kakaomap.bottomsheet.adapter.PlaceItemInMapViewAdapter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;

import java.util.List;

public interface IMapData
{
    void createPlacesPoiItems(List<PlaceDocuments> placeDocuments);

    void createAddressesPoiItems(List<AddressResponseDocuments> addressDocuments);

    void selectPoiItem(int index);

    void removeAllPoiItems();

    void showAllPoiItems();

    void deselectPoiItem();

    void backToPreviousView();

    int getPoiItemSize();

    void setPlacesListAdapter(PlaceItemInMapViewAdapter adapter);
}
