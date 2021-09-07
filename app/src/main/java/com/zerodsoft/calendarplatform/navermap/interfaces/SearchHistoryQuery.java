package com.zerodsoft.calendarplatform.navermap.interfaces;

import com.zerodsoft.calendarplatform.common.interfaces.DbQueryCallback;
import com.zerodsoft.calendarplatform.room.dto.SearchHistoryDTO;

import java.util.List;

public interface SearchHistoryQuery
{
    void insert(Integer type, String value);

    void select(Integer type, DbQueryCallback<List<SearchHistoryDTO>> callback);

    void select(Integer type, String value, DbQueryCallback<SearchHistoryDTO> callback);

    void delete(int id);

    void delete(Integer type, String value, DbQueryCallback<Boolean> callback);

    void deleteAll(Integer type, DbQueryCallback<Boolean> callback);

    void contains(Integer type, String value, DbQueryCallback<Boolean> callback);
}
