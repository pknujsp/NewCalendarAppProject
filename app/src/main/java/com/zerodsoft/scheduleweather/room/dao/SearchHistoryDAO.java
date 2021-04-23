package com.zerodsoft.scheduleweather.room.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.zerodsoft.scheduleweather.room.dto.SearchHistoryDTO;

import java.util.List;

@Dao
public interface SearchHistoryDAO
{
    @Query("INSERT INTO search_history_table (type, value) VALUES (:type, :value)")
    void insert(Integer type, String value);

    @Query("SELECT * FROM search_history_table WHERE type = :type")
    List<SearchHistoryDTO> select(Integer type);

    @Query("SELECT * FROM search_history_table WHERE type = :type AND value = :value")
    SearchHistoryDTO select(Integer type, String value);

    @Query("DELETE FROM search_history_table WHERE id = :id")
    void delete(int id);

    @Query("DELETE FROM search_history_table WHERE type = :type AND value = :value")
    void delete(Integer type, String value);

    @Query("DELETE FROM search_history_table WHERE type = :type")
    void deleteAll(Integer type);

    @Query("SELECT EXISTS (SELECT * FROM search_history_table WHERE type = :type AND value = :value) AS SUCCESS")
    int contains(Integer type, String value);
}
