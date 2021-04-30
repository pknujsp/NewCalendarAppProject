package com.zerodsoft.scheduleweather.navermap.interfaces;

import com.zerodsoft.scheduleweather.navermap.bottomsheet.adapter.PlaceItemInMapViewAdapter;
import com.zerodsoft.scheduleweather.navermap.PoiItemType;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;

import java.util.List;

public interface IMapData
{
    void createPoiItems(List<? extends KakaoLocalDocument> kakaoLocalDocuments, PoiItemType poiItemType);

    void addPoiItems(List<? extends KakaoLocalDocument> kakaoLocalDocuments, PoiItemType poiItemType);

    void removePoiItems(PoiItemType... poiItemTypes);

    void removeAllPoiItems();

    void showPoiItems(PoiItemType... poiItemTypes);

    void deselectPoiItem();

    int getPoiItemSize(PoiItemType... poiItemTypes);

    void setPlacesListAdapter(PlaceItemInMapViewAdapter adapter, PoiItemType poiItemType);
}
