package com.zerodsoft.scheduleweather.room.dao;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;

@Dao
public interface LocationDAO
{
    @Insert
    long insertPlace(PlaceDTO placeDTO);

    @Insert
    long insertAddress(AddressDTO addressDTO);

    @Query("SELECT * FROM TB_PLACE WHERE schedule_id = :scheduleId")
    LiveData<PlaceDTO> selectPlace(int scheduleId);

    @Query("SELECT * FROM TB_ADDRESS WHERE schedule_id = :scheduleId")
    LiveData<AddressDTO> selectAddress(int scheduleId);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updatePlace(PlaceDTO placeDTO);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateAddress(AddressDTO addressDTO);

    @Query("DELETE FROM TB_PLACE WHERE schedule_id = :scheduleId")
    void deletePlace(int scheduleId);

    @Query("DELETE FROM TB_ADDRESS WHERE schedule_id = :scheduleId")
    void deleteAddress(int scheduleId);
}
