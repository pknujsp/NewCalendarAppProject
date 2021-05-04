package com.zerodsoft.scheduleweather.navermap.interfaces;

import com.zerodsoft.scheduleweather.navermap.LocationItemViewPagerAdapter;
import com.zerodsoft.scheduleweather.navermap.PoiItemType;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;

import java.util.List;

public interface IMapData
{
    void createPoiItems(List<? extends KakaoLocalDocument> kakaoLocalDocuments, PoiItemType poiItemType);

    void addPoiItems(List<? extends KakaoLocalDocument> kakaoLocalDocuments, PoiItemType poiItemType);

    void removePoiItems(PoiItemType... poiItemTypes);

    void removePoiItem(PoiItemType poiItemType, int index);

    void removeAllPoiItems();

    void showPoiItems(PoiItemType... poiItemTypes);

    void showPoiItems(PoiItemType poiItemType, boolean isShow);

    void deselectPoiItem();

    int getPoiItemSize(PoiItemType... poiItemTypes);

    void setLocationItemViewPagerAdapter(LocationItemViewPagerAdapter adapter, PoiItemType poiItemType);
}
