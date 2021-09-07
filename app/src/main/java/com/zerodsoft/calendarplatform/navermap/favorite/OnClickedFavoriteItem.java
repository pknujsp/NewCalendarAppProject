package com.zerodsoft.calendarplatform.navermap.favorite;

import android.view.View;

import com.zerodsoft.calendarplatform.common.interfaces.OnClickedListItem;
import com.zerodsoft.calendarplatform.room.dto.FavoriteLocationDTO;

public interface OnClickedFavoriteItem extends OnClickedListItem<FavoriteLocationDTO>
{
    @Override
    void onClickedListItem(FavoriteLocationDTO e, int position);

    @Override
    void deleteListItem(FavoriteLocationDTO e, int position);

    void onClickedEditButton(FavoriteLocationDTO e, View anchorView, int index);

    void onClickedShareButton(FavoriteLocationDTO e);
}
