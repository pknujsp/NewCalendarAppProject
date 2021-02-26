package com.zerodsoft.scheduleweather.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.zerodsoft.scheduleweather.room.dto.CustomPlaceCategoryDTO;

import java.util.List;

@Dao
public interface CustomPlaceCategoryDAO
{
    @Query("INSERT INTO custom_category_table (code) VALUES(:code)")
    void insert(String code);

    @Query("SELECT * FROM custom_category_table")
    List<CustomPlaceCategoryDTO> select();

    @Query("UPDATE custom_category_table SET code = :code WHERE id = :id")
    void update(int id, String code);

    @Query("DELETE FROM custom_category_table WHERE id=:id")
    void delete(int id);

    @Query("DELETE FROM custom_category_table")
    void deleteAll();
}
