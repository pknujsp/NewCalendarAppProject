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
        try
        {
            CriteriaLocationRepository.restaurantCriteriaLocation = (LocationDTO) restaurantCriteriaLocation.clone();
        } catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
        }
    }

    public static LocationDTO getRestaurantCriteriaLocation()
    {
        LocationDTO locationDTO = null;
        try
        {
            locationDTO = (LocationDTO) restaurantCriteriaLocation.clone();
        } catch (Exception e)
        {

        }
        return locationDTO;
    }

    public static void removeRestaurantCriteriaLocation()
    {
        restaurantCriteriaLocation = null;
    }
}
