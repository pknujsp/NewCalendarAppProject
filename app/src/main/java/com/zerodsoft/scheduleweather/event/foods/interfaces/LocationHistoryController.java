package com.zerodsoft.scheduleweather.event.foods.interfaces;

import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationSearchHistoryDTO;

public interface LocationHistoryController
{
    void onClickedLocationHistoryItem(FoodCriteriaLocationSearchHistoryDTO foodCriteriaLocationSearchHistoryDTO);

    void delete(int id);
}
