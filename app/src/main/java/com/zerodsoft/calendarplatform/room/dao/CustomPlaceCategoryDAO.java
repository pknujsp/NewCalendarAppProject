package com.zerodsoft.calendarplatform.room.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.zerodsoft.calendarplatform.room.dto.CustomPlaceCategoryDTO;

import java.util.List;

@Dao
public interface CustomPlaceCategoryDAO
{
    @Query("INSERT INTO custom_category_table (code) VALUES(:code)")
    void insert(String code);

    @Query("SELECT * FROM custom_category_table")
    List<CustomPlaceCategoryDTO> select();

    @Query("SELECT * FROM custom_category_table WHERE code =:code")
    CustomPlaceCategoryDTO select(String code);

    @Query("UPDATE custom_category_table SET code = :code WHERE code = :previousCode")
    void update(String previousCode, String code);

    @Query("DELETE FROM custom_category_table WHERE code=:code")
    void delete(String code);

    @Query("DELETE FROM custom_category_table")
    void deleteAll();

    @Query("SELECT EXISTS (SELECT * FROM custom_category_table WHERE code =:code) AS SUCCESS")
    int containsCode(String code);


}
