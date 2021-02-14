package com.zerodsoft.scheduleweather.kakaomap.util;

import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;

public class LocalParameterUtil
{
    public static LocalApiPlaceParameter getPlaceParameter(String searchWord, double latitude, double longitude, String size, String page, int sortCriteria)
    {
        LocalApiPlaceParameter parameter = new LocalApiPlaceParameter();

        parameter.setY(Double.toString(latitude)).setX(Double.toString(longitude))
                .setSize(size).setPage(page);

        switch (sortCriteria)
        {
            case LocalApiPlaceParameter.SEARCH_CRITERIA_SORT_TYPE_ACCURACY:
                parameter.setSort(LocalApiPlaceParameter.SORT_ACCURACY);
                break;
            case LocalApiPlaceParameter.SEARCH_CRITERIA_SORT_TYPE_DISTANCE:
                parameter.setSort(LocalApiPlaceParameter.SORT_DISTANCE);
                break;
        }

        if (KakaoLocalApiCategoryUtil.isCategory(searchWord))
        {
            parameter.setCategoryGroupCode(KakaoLocalApiCategoryUtil.getName(Integer.parseInt(searchWord)));
        } else
        {
            parameter.setQuery(searchWord);
        }

        return parameter;
    }

    public static LocalApiPlaceParameter getAddressParameter(String searchWord, String size, String page)
    {
        LocalApiPlaceParameter parameter = new LocalApiPlaceParameter();
        parameter.setQuery(searchWord).setSize(size).setPage(page);
        return parameter;
    }

    public static LocalApiPlaceParameter getCoordToAddressParameter(double latitude, double longitude)
    {
        LocalApiPlaceParameter parameter = new LocalApiPlaceParameter();
        parameter.setX(String.valueOf(longitude)).setY(String.valueOf(latitude));
        return parameter;
    }
}
