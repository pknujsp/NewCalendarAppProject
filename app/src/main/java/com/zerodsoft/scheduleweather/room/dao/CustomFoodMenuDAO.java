package com.zerodsoft.scheduleweather.room.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.zerodsoft.scheduleweather.room.dto.CustomFoodMenuDTO;

import java.util.List;

@Dao
public interface CustomFoodMenuDAO {
	@Query("INSERT INTO custom_food_menu_table (menu_name) VALUES(:foodMenuName)")
	void insert(String foodMenuName);

	@Query("SELECT * FROM custom_food_menu_table")
	List<CustomFoodMenuDTO> select();

	@Query("SELECT * FROM custom_food_menu_table WHERE menu_name =:foodMenuName")
	CustomFoodMenuDTO select(String foodMenuName);

	@Query("DELETE FROM custom_food_menu_table WHERE id = :id")
	void delete(Integer id);

	@Query("DELETE FROM custom_food_menu_table")
	void deleteAll();

	@Query("SELECT EXISTS (SELECT * FROM custom_food_menu_table WHERE menu_name =:foodMenuName) AS SUCCESS")
	int containsMenu(String foodMenuName);
}
