package com.zerodsoft.scheduleweather.common.interfaces;

public interface OnClickedListItem<T>
{
    void onClickedListItem(T e);
    void deleteListItem(T e, int position);
}
