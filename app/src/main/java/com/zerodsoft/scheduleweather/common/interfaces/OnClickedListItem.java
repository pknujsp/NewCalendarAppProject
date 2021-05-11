package com.zerodsoft.scheduleweather.common.interfaces;

public interface OnClickedListItem<T>
{
    void onClickedListItem(T e, int position);
    void deleteListItem(T e, int position);
}
