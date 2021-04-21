package com.zerodsoft.scheduleweather.room.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.zerodsoft.scheduleweather.room.dto.CustomFoodMenuDTO;

import java.util.List;

@Dao
public interface CustomFoodMenuDAO
{
    @Query("INSERT INTO custom_food_menu_table (menu_name) VALUES(:categoryName)")
    void insert(String categoryName);

    @Query("SELECT * FROM custom_food_menu_table")
    List<CustomFoodMenuDTO> select();

    @Query("SELECT * FROM custom_food_menu_table WHERE menu_name =:categoryName")
    CustomFoodMenuDTO select(String categoryName);

    @Query("UPDATE custom_food_menu_table SET menu_name = :newCategoryName WHERE menu_name = :previousCategoryName")
    void update(String previousCategoryName, String newCategoryName);

    @Query("DELETE FROM custom_food_menu_table WHERE menu_name=:categoryName")
    void delete(String categoryName);

    @Query("DELETE FROM custom_food_menu_table")
    void deleteAll();

    @Query("SELECT EXISTS (SELECT * FROM custom_food_menu_table WHERE menu_name =:categoryName) AS SUCCESS")
    int containsMenu(String categoryName);
}
