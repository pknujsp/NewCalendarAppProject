package com.zerodsoft.scheduleweather.event.foods.share;

import android.os.Bundle;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.FavoriteRestaurantDTO;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FavoriteRestaurantCloud
{
    private static FavoriteRestaurantCloud instance;
    private Set<String> favoriteRestaurantSet = new HashSet<>();

    public static FavoriteRestaurantCloud newInstance()
    {
        instance = new FavoriteRestaurantCloud();
        return instance;
    }

    public static FavoriteRestaurantCloud getInstance()
    {
        return instance;
    }

    public static void close()
    {
        instance = null;
    }

    public void add(String restaurantId)
    {
        favoriteRestaurantSet.add(restaurantId);
    }

    public boolean contains(String restaurantId)
    {
        return favoriteRestaurantSet.contains(restaurantId);
    }

    public Set<String> getSet()
    {
        return new HashSet<>(favoriteRestaurantSet);
    }

    public void delete(String restaurantId)
    {
        favoriteRestaurantSet.remove(restaurantId);
    }

    public void deleteAll()
    {
        favoriteRestaurantSet.clear();
    }

}
