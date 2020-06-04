package com.zerodsoft.scheduleweather.Room.DAO;

import androidx.room.Dao;
import androidx.room.Query;

import com.zerodsoft.scheduleweather.Room.DTO.Area;
import com.zerodsoft.scheduleweather.WeatherData.Phase1Tuple;


import java.util.List;

@Dao
public interface AreaDao
{
    @Query("SELECT area_id, phase_1 FROM area_table GROUP BY phase_1")
    List<Phase1Tuple> getPhase1();

    @Query("SELECT * FROM area_table WHERE phase_1 LIKE :phase1 GROUP BY phase_2")
    List<Area> getPhase2(String phase1);

    @Query("SELECT * FROM area_table WHERE phase_1 LIKE :phase1 AND phase_2 LIKE :phase2")
    Area getPhase2Xy(String phase1, String phase2);

    @Query("SELECT * FROM area_table WHERE phase_1 LIKE :phase1 AND phase_2 LIKE :phase2")
    List<Area> getPhase3(String phase1, String phase2);

    @Query("SELECT * FROM area_table WHERE phase_1 LIKE :phase1 AND phase_2 LIKE :phase2 AND phase_3 LIKE :phase3")
    Area getPhase3Xy(String phase1, String phase2, String phase3);

    @Query("SELECT * FROM area_table WHERE phase_1 LIKE :phase1 AND phase_2 LIKE :phase2 AND phase_3 LIKE :phase3")
    Area getXy(String phase1, String phase2, String phase3);


}
