package com.zerodsoft.calendarplatform.navermap.interfaces;

import com.zerodsoft.calendarplatform.room.dto.FavoriteLocationDTO;

public interface OnClickedFavoriteButtonInExpandableListListener {
	void onAddedNewFavorite(FavoriteLocationDTO favoriteLocationDTO, int groupPosition,
	                        int childPosition);

	void onRemovedFavorite(FavoriteLocationDTO favoriteLocationDTO, int position);
}