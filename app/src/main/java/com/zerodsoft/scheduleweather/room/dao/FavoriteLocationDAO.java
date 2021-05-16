package com.zerodsoft.scheduleweather.room.dao;

import android.service.carrier.CarrierMessagingService;

import androidx.room.Dao;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;
import com.zerodsoft.scheduleweather.room.interfaces.FavoriteLocationQuery;

import java.util.List;

@Dao
public interface FavoriteLocationDAO
{
    /*
       @Ignore
    public static final int RESTAURANT = 0;
    @Ignore
    public static final int PLACE = 1;
    @Ignore
    public static final int ADDRESS = 2;
    ONLY_FOR_MAP = 3
     */

    @Insert(onConflict = OnConflictStrategy.IGNORE, entity = FavoriteLocationDTO.class)
    long insert(FavoriteLocationDTO favoriteLocationDTO);

    @Query("SELECT * FROM favorite_location_table " +
            "WHERE (CASE " +
            "WHEN :type = 3 THEN type = 1 OR type = 2 OR type = 0 " +
            "END)")
    List<FavoriteLocationDTO> select(Integer type);

    @SuppressWarnings("Unused parameter")
    @Ignore()
    @Query("SELECT * FROM favorite_location_table " +
            "WHERE (CASE " +
            "WHEN :type = null THEN id = :id " +
            "ELSE id = :id " +
            "END)")
    FavoriteLocationDTO select(Integer type, Integer id);

    @Query("DELETE FROM favorite_location_table WHERE id = :id")
    void delete(Integer id);

    @Query("DELETE FROM favorite_location_table WHERE type = :type")
    void deleteAll(Integer type);

    @Query("DELETE FROM favorite_location_table")
    void deleteAll();

    @Query("SELECT * FROM favorite_location_table " +
            "WHERE (CASE " +
            "WHEN :placeId = null THEN address = :address AND latitude = :latitude AND longitude = :longitude " +
            "ELSE place_id = :placeId " +
            "END)")
    FavoriteLocationDTO contains(String placeId, String address, String latitude, String longitude);
}