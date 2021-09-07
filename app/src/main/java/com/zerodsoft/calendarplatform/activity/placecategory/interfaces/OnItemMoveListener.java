package com.zerodsoft.calendarplatform.activity.placecategory.interfaces;

public interface OnItemMoveListener {
	boolean onItemMove(int fromPosition, int toPosition);

	boolean onItemSwiped(int position);
}
