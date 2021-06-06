package com.zerodsoft.scheduleweather.common.interfaces;

import java.io.Serializable;

public interface OnClickedListItem<T> extends Serializable {
	void onClickedListItem(T e, int position);

	void deleteListItem(T e, int position);
}
