package com.zerodsoft.scheduleweather.navermap.favorite;

import android.view.View;

import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;

public interface OnClickedFavoriteItem extends OnClickedListItem<FavoriteLocationDTO>
{
    @Override
    void onClickedListItem(FavoriteLocationDTO e, int position);

    @Override
    void deleteListItem(FavoriteLocationDTO e, int position);

    void onClickedEditButton(FavoriteLocationDTO e, View anchorView, int index);

    void onClickedShareButton(FavoriteLocationDTO e);
}
