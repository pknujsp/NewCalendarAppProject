package com.zerodsoft.scheduleweather.navermap.interfaces;

import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;

public interface OnClickedFavoriteButtonInExpandableListListener {
	void onAddedNewFavorite(FavoriteLocationDTO favoriteLocationDTO, int groupPosition,
	                        int childPosition);

	void onRemovedFavorite(FavoriteLocationDTO favoriteLocationDTO, int position);
}