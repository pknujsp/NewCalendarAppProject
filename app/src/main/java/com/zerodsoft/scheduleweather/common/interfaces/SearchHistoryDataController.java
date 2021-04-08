package com.zerodsoft.scheduleweather.common.interfaces;

import com.zerodsoft.scheduleweather.room.dto.SearchHistoryDTO;

public interface SearchHistoryDataController<T>
{
    void updateSearchHistoryList();

    void deleteListItem(T e, int position);

    void insertValueToHistory(String value);
}
