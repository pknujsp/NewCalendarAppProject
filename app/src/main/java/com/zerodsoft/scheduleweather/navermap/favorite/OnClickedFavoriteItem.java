package com.zerodsoft.scheduleweather.navermap.favorite;

import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;

public interface OnClickedFavoriteItem extends OnClickedListItem<FavoriteLocationDTO>
{
    @Override
    void onClickedListItem(FavoriteLocationDTO e);

    @Override
    void deleteListItem(FavoriteLocationDTO e, int position);

    void onClickedEditButton(FavoriteLocationDTO e);

    void onClickedShareButton(FavoriteLocationDTO e);
}
