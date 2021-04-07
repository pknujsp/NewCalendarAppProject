package com.zerodsoft.scheduleweather.retrofit;

import android.content.Context;

import com.google.gson.Gson;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KakaoLocalApiCategoryUtil
{
    private static List<PlaceCategoryDTO> categoryList = null;
    private static Map<String, String> defaultPlaceCategoryMap = null;

    private KakaoLocalApiCategoryUtil()
    {

    }

    public static void loadCategories(Context context)
    {
        // jsom파일에서 데이터 읽기
        StringBuilder jsonString = new StringBuilder();
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(context.getAssets().open("database/kakaolocalapicategory.json"));
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(bufferedInputStream)))
        {
            String str = null;
            while ((str = bufferedReader.readLine()) != null)
            {
                jsonString.append(str);
            }
        } catch (Exception e)
        {

        }

        Gson gson = new Gson();
        CategoryJsonItem result = gson.fromJson(jsonString.toString(), CategoryJsonItem.class);

        categoryList = result.categories;
        defaultPlaceCategoryMap = new HashMap<>();
        for (PlaceCategoryDTO defaultCategory : categoryList)
        {
            defaultPlaceCategoryMap.put(defaultCategory.getCode(), defaultCategory.getDescription());
        }
    }


    public static boolean isCategory(String word)
    {
        boolean isCategory = false;
        try
        {
            if (defaultPlaceCategoryMap.containsKey(word) || defaultPlaceCategoryMap.containsValue(word))
            {
                isCategory = true;
            }
        } catch (NumberFormatException e)
        {
            isCategory = false;
        }
        return isCategory;
    }

    public static List<PlaceCategoryDTO> getDefaultPlaceCategoryList()
    {
        return categoryList;
    }

    public static Map<String, String> getDefaultPlaceCategoryMap()
    {
        return defaultPlaceCategoryMap;
    }

    public static String getCode(String word) throws IndexOutOfBoundsException
    {
        String code = null;

        if (defaultPlaceCategoryMap.containsKey(word))
        {
            code = word;
        } else
        {
            for (PlaceCategoryDTO placeCategory : categoryList)
            {
                if (placeCategory.getDescription().equals(word))
                {
                    code = placeCategory.getCode();
                }
            }
        }
        return code;
    }


    public static String getDescription(int id)
    {
        return categoryList.get(id).getDescription();
    }

    public static String getDefaultDescription(String code)
    {
        return defaultPlaceCategoryMap.get(code);
    }

    class CategoryJsonItem
    {
        List<PlaceCategoryDTO> categories;
    }
}
