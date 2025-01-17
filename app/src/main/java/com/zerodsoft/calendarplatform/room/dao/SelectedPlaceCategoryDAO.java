package com.zerodsoft.calendarplatform.room.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.zerodsoft.calendarplatform.room.dto.SelectedPlaceCategoryDTO;

import java.util.List;

@Dao
public interface SelectedPlaceCategoryDAO {
	@Query("INSERT INTO selected_place_category_table (code) VALUES(:code)")
	void add(String code);

	@Query("DELETE FROM selected_place_category_table WHERE code = :code")
	void delete(String code);

	@Query("DELETE FROM selected_place_category_table")
	void deleteAll();

	@Query("SELECT * FROM selected_place_category_table")
	List<SelectedPlaceCategoryDTO> getAll();

	@Query("SELECT * FROM selected_place_category_table WHERE code =:code")
	SelectedPlaceCategoryDTO get(String code);
}
