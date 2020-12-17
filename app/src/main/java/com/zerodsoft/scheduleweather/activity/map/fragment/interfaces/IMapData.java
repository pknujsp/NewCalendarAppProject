package com.zerodsoft.scheduleweather.activity.map.fragment.interfaces;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;

import java.util.List;

public interface IMapData
{
    void createPlacesPoiItems(List<PlaceDocuments> placeDocuments);

    void createAddressesPoiItems(List<AddressResponseDocuments> addressDocuments);

    void createPlacePoiItem(PlaceDocuments document);

    void createAddressPoiItem(AddressResponseDocuments document);

    void selectPoiItem(int index);

    void removeAllPoiItems();

    void showAllPoiItems();

    void deselectPoiItem();
}
