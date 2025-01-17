package com.zerodsoft.calendarplatform.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.zerodsoft.calendarplatform.room.dao.CustomFoodMenuDAO;
import com.zerodsoft.calendarplatform.room.dao.CustomPlaceCategoryDAO;
import com.zerodsoft.calendarplatform.room.dao.FavoriteLocationDAO;
import com.zerodsoft.calendarplatform.room.dao.FoodCriteriaLocationInfoDAO;
import com.zerodsoft.calendarplatform.room.dao.FoodCriteriaLocationSearchHistoryDAO;
import com.zerodsoft.calendarplatform.room.dao.SearchHistoryDAO;
import com.zerodsoft.calendarplatform.room.dao.SelectedCalendarDAO;
import com.zerodsoft.calendarplatform.room.dao.WeatherDataDAO;
import com.zerodsoft.calendarplatform.room.dto.CustomFoodMenuDTO;
import com.zerodsoft.calendarplatform.room.dto.CustomPlaceCategoryDTO;
import com.zerodsoft.calendarplatform.room.dao.FavoriteLocDAO;
import com.zerodsoft.calendarplatform.room.dao.LocationDAO;
import com.zerodsoft.calendarplatform.room.dao.SelectedPlaceCategoryDAO;
import com.zerodsoft.calendarplatform.room.dao.WeatherAreaCodeDAO;
import com.zerodsoft.calendarplatform.room.dto.FavoriteLocationDTO;
import com.zerodsoft.calendarplatform.room.dto.FoodCriteriaLocationInfoDTO;
import com.zerodsoft.calendarplatform.room.dto.FoodCriteriaLocationSearchHistoryDTO;
import com.zerodsoft.calendarplatform.room.dto.LocationDTO;
import com.zerodsoft.calendarplatform.room.dto.SearchHistoryDTO;
import com.zerodsoft.calendarplatform.room.dto.SelectedCalendarDTO;
import com.zerodsoft.calendarplatform.room.dto.SelectedPlaceCategoryDTO;
import com.zerodsoft.calendarplatform.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.calendarplatform.room.dto.WeatherDataDTO;

@Database(entities = {LocationDTO.class, WeatherAreaCodeDTO.class, SelectedPlaceCategoryDTO.class, CustomPlaceCategoryDTO.class, CustomFoodMenuDTO.class,
		FoodCriteriaLocationInfoDTO.class, FoodCriteriaLocationSearchHistoryDTO.class, SearchHistoryDTO.class, FavoriteLocationDTO.class
		, WeatherDataDTO.class, SelectedCalendarDTO.class}, version = 1, exportSchema = false)
public abstract class AppDb extends RoomDatabase {
	private static volatile AppDb instance = null;

	public abstract FavoriteLocDAO favoriteLocDAO();

	public abstract SelectedPlaceCategoryDAO selectedPlaceCategoryDAO();

	public abstract CustomPlaceCategoryDAO customPlaceCategoryDAO();

	public abstract LocationDAO locationDAO();

	public abstract WeatherAreaCodeDAO weatherAreaCodeDAO();

	public abstract CustomFoodMenuDAO customFoodCategoryDAO();

	public abstract FoodCriteriaLocationInfoDAO foodCriteriaLocationInfoDAO();

	public abstract FoodCriteriaLocationSearchHistoryDAO foodCriteriaLocationSearchHistoryDAO();

	public abstract SearchHistoryDAO searchHistoryDAO();

	public abstract FavoriteLocationDAO favoriteRestaurantDAO();

	public abstract WeatherDataDAO weatherDataDAO();

	public abstract SelectedCalendarDAO selectedCalendarDAO();


	public static synchronized AppDb getInstance(Context context) {
		if (instance == null) {
			instance = Room.databaseBuilder(context, AppDb.class, "appdb")
					.createFromAsset("database/appdb.db").build();
		}
		return instance;
	}

	public static void closeInstance() {
		instance = null;
	}
}
