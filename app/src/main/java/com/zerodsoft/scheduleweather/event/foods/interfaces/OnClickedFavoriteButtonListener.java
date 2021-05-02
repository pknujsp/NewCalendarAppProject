package com.zerodsoft.scheduleweather.event.foods.interfaces;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;

public interface OnClickedFavoriteButtonListener
{
    void onClickedFavoriteButton(KakaoLocalDocument kakaoLocalDocument, FavoriteLocationDTO favoriteLocationDTO, int groupPosition, int childPosition);

    void onClickedFavoriteButton(KakaoLocalDocument kakaoLocalDocument, FavoriteLocationDTO favoriteLocationDTO, int position);
}
