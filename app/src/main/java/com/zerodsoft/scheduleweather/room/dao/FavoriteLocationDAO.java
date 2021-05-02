package com.zerodsoft.scheduleweather.room.dao;

import android.service.carrier.CarrierMessagingService;

import androidx.room.Dao;
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
     */

    @Query("INSERT INTO favorite_location_table (location_id, location_name, latitude, longitude, type) VALUES(:locationId, :locationName, :latitude, :longitude, :type)")
    void insert(String locationId, String locationName, String latitude, String longitude, Integer type);

    @Query("SELECT * FROM favorite_location_table WHERE type = :type")
    List<FavoriteLocationDTO> select(Integer type);

    @Query("SELECT * FROM favorite_location_table WHERE location_name = :locationName AND type = :type")
    FavoriteLocationDTO select(String locationName, Integer type);

    @Query("DELETE FROM favorite_location_table WHERE location_name = :locationName AND type = :type")
    void delete(String locationName, Integer type);

    @Query("DELETE FROM favorite_location_table WHERE type = :type")
    void deleteAll(Integer type);

    // @Query("SELECT EXISTS (SELECT * FROM favorite_location_table WHERE location_name = :locationName AND type = :type) AS SUCCESS")
    @Query("SELECT EXISTS (SELECT * FROM favorite_location_table " +
            "WHERE (CASE " +
            "WHEN type = 0 THEN location_id = :locationId " +
            "WHEN type = 1 THEN location_id = :locationId " +
            "WHEN type = 2 THEN location_name = :locationName " +
            "END)) AS SUCCESS")
    int contains(Integer type, String locationName, String locationId);
}
