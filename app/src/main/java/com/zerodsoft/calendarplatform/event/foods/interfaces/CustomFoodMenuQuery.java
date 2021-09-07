package com.zerodsoft.calendarplatform.event.foods.interfaces;

import com.zerodsoft.calendarplatform.common.interfaces.DbQueryCallback;
import com.zerodsoft.calendarplatform.room.dto.CustomFoodMenuDTO;

import java.util.List;

public interface CustomFoodMenuQuery
{
    void insert(String menuName, DbQueryCallback<CustomFoodMenuDTO> callback);

    void select(DbQueryCallback<List<CustomFoodMenuDTO>> callback);

    void delete(Integer id, DbQueryCallback<Boolean> callback);

    void deleteAll(DbQueryCallback<Boolean> callback);

    void containsMenu(String menuName, DbQueryCallback<Boolean> callback);
}
