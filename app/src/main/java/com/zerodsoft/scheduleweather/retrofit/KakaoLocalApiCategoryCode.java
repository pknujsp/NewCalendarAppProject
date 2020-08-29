package com.zerodsoft.scheduleweather.retrofit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KakaoLocalApiCategoryCode
{
    private static Map<String, String> categoryMap = null;
    private static List<KakaoLocalApiCategory> categoryList = null;

    private KakaoLocalApiCategoryCode()
    {

    }

    public static void loadCategoryMap()
    {
        if (categoryMap == null)
        {
            categoryMap = new HashMap<>();

            categoryMap.put("대형마트", "MT1");
            categoryMap.put("편의점", "CS2");
            categoryMap.put("어린이집", "PS3");
            categoryMap.put("유치원", "PS3");
            categoryMap.put("학교", "SC4");
            categoryMap.put("학원", "AC5");
            categoryMap.put("주차장", "PK6");
            categoryMap.put("주유소", "OL7");
            categoryMap.put("충전소", "OL7");
            categoryMap.put("지하철역", "SW8");
            categoryMap.put("은행", "BK9");
            categoryMap.put("문화시설", "CT1");
            categoryMap.put("중개업소", "AG2");
            categoryMap.put("공공기관", "PO3");
            categoryMap.put("관광명소", "AT4");
            categoryMap.put("숙박", "AD5");
            categoryMap.put("음식점", "FD6");
            categoryMap.put("카페", "CE7");
            categoryMap.put("병원", "HP8");
            categoryMap.put("약국", "PM9");
        }
    }

    public static List<KakaoLocalApiCategory> toArrayList()
    {
        if (categoryList == null)
        {
            categoryList = new ArrayList<>();

            for (Map.Entry entry : categoryMap.entrySet())
            {
                categoryList.add(new KakaoLocalApiCategory(entry.getValue().toString(), entry.getKey().toString()));
            }
        }
        return categoryList;
    }

    public static String getName(String description)
    {
        return categoryMap.get(description);
    }
}
