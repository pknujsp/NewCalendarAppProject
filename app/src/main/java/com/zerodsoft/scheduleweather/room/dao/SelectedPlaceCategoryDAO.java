package com.zerodsoft.scheduleweather.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;
import com.zerodsoft.scheduleweather.room.dto.SelectedPlaceCategoryDTO;

import java.util.List;

@Dao
public interface SelectedPlaceCategoryDAO
{
    @Query("INSERT INTO selected_place_category_table (code) VALUES(:code)")
    void insert(String code);

    @Query("DELETE FROM selected_place_category_table WHERE code = :code")
    void delete(String code);

    @Query("DELETE FROM selected_place_category_table")
    void deleteAll();

    @Query("SELECT * FROM selected_place_category_table")
    List<SelectedPlaceCategoryDTO> select();

    @Query("SELECT * FROM selected_place_category_table WHERE code =:code")
    SelectedPlaceCategoryDTO select(String code);

    @Query("UPDATE selected_place_category_table SET code =:newCode WHERE code =:currentCode")
    void update(String currentCode, String newCode);
}
