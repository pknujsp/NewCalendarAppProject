package com.zerodsoft.scheduleweather.room.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.zerodsoft.scheduleweather.room.dto.CustomFoodMenuDTO;
import com.zerodsoft.scheduleweather.room.dto.FavoriteRestaurantDTO;

import java.util.List;

@Dao
public interface FavoriteRestaurantDAO
{
    @Query("INSERT INTO favorite_restaurant_table (restaurant_id) VALUES(:restaurantId)")
    void insert(String restaurantId);

    @Query("SELECT * FROM favorite_restaurant_table")
    List<FavoriteRestaurantDTO> select();

    @Query("SELECT * FROM favorite_restaurant_table WHERE restaurant_id = :restaurantId")
    FavoriteRestaurantDTO select(String restaurantId);

    @Query("DELETE FROM favorite_restaurant_table WHERE restaurant_id =:restaurantId")
    void delete(String restaurantId);

    @Query("DELETE FROM favorite_restaurant_table")
    void deleteAll();

    @Query("SELECT EXISTS (SELECT * FROM favorite_restaurant_table WHERE restaurant_id =:restaurantId) AS SUCCESS")
    int containsMenu(String restaurantId);
}
