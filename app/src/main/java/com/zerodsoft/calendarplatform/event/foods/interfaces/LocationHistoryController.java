package com.zerodsoft.calendarplatform.event.foods.interfaces;

import com.zerodsoft.calendarplatform.room.dto.FoodCriteriaLocationSearchHistoryDTO;

public interface LocationHistoryController
{
    void onClickedLocationHistoryItem(FoodCriteriaLocationSearchHistoryDTO foodCriteriaLocationSearchHistoryDTO);

    void delete(int id);
}
