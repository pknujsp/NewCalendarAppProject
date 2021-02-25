package com.zerodsoft.scheduleweather.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.zerodsoft.scheduleweather.retrofit.PlaceCategory;

import java.util.List;

@Dao
public interface PlaceCategoryDAO
{
    @Insert(entity = PlaceCategory.class)
    void insert(PlaceCategory placeCategory);

    @Query("DELETE FROM place_category_table WHERE id = :id")
    void delete(int id);

    @Query("DELETE FROM place_category_table")
    void deleteAll();

    @Query("UPDATE place_category_table SET description =:description WHERE id =:id")
    void update(int id, String description);

    @Query("SELECT * FROM place_category_table")
    LiveData<List<PlaceCategory>> select();
}
