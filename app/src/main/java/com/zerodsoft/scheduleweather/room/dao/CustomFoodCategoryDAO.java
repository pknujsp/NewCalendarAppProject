package com.zerodsoft.scheduleweather.room.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.zerodsoft.scheduleweather.room.dto.CustomFoodCategoryDTO;
import com.zerodsoft.scheduleweather.room.dto.CustomPlaceCategoryDTO;

import java.util.List;

@Dao
public interface CustomFoodCategoryDAO
{
    @Query("INSERT INTO custom_food_category_table (category_name) VALUES(:categoryName)")
    void insert(String categoryName);

    @Query("SELECT * FROM custom_food_category_table")
    List<CustomFoodCategoryDTO> select();

    @Query("SELECT * FROM custom_food_category_table WHERE category_name =:categoryName")
    CustomFoodCategoryDTO select(String categoryName);

    @Query("UPDATE custom_food_category_table SET category_name = :newCategoryName WHERE category_name = :previousCategoryName")
    void update(String previousCategoryName, String newCategoryName);

    @Query("DELETE FROM custom_food_category_table WHERE category_name=:categoryName")
    void delete(String categoryName);

    @Query("DELETE FROM custom_food_category_table")
    void deleteAll();

    @Query("SELECT EXISTS (SELECT * FROM custom_food_category_table WHERE category_name =:categoryName) AS SUCCESS")
    int containsCode(String categoryName);
}
