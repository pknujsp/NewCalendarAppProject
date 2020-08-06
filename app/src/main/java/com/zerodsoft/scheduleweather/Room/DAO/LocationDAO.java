package com.zerodsoft.scheduleweather.Room.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.zerodsoft.scheduleweather.Room.DTO.AddressDTO;
import com.zerodsoft.scheduleweather.Room.DTO.PlaceDTO;

@Dao
public interface LocationDAO
{
    @Insert
    long insertPlace(PlaceDTO placeDTO);

    @Insert
    long insertAddress(AddressDTO addressDTO);

    @Query("SELECT * FROM TB_PLACE WHERE id = :id")
    PlaceDTO selectPlace(int id);

    @Query("SELECT * FROM TB_ADDRESS WHERE id = :id")
    AddressDTO selectAddress(int id);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updatePlace(PlaceDTO placeDTO);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateAddress(AddressDTO addressDTO);

    @Query("DELETE FROM TB_PLACE WHERE id = :id")
    void deletePlace(int id);

    @Query("DELETE FROM TB_ADDRESS WHERE id = :id")
    void deleteAddress(int id);
}
