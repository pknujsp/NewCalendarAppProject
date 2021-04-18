package com.zerodsoft.scheduleweather.event.foods.share;

import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

public class CriteriaLocationRepository
{
    private static LocationDTO restaurantCriteriaLocation;

    private CriteriaLocationRepository()
    {

    }

    public static void setRestaurantCriteriaLocation(LocationDTO restaurantCriteriaLocation)
    {
        CriteriaLocationRepository.restaurantCriteriaLocation = restaurantCriteriaLocation;
    }

    public static LocationDTO getRestaurantCriteriaLocation()
    {
        return restaurantCriteriaLocation.copy();
    }

    public static void removeRestaurantCriteriaLocation()
    {
        restaurantCriteriaLocation = null;
    }
}
