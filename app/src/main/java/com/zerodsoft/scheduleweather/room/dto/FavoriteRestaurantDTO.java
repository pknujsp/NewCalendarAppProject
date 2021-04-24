package com.zerodsoft.scheduleweather.room.dto;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite_restaurant_table")
public class FavoriteRestaurantDTO
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "restaurant_id")
    private String restaurantId;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getRestaurantId()
    {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId)
    {
        this.restaurantId = restaurantId;
    }
}
