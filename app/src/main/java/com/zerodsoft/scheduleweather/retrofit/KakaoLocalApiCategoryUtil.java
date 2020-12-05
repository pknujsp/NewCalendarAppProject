package com.zerodsoft.scheduleweather.retrofit;

import android.content.Context;

import com.google.gson.Gson;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class KakaoLocalApiCategoryUtil
{
    private static Map<String, KakaoLocalApiCategory> categoryMap = null;
    private static List<KakaoLocalApiCategory> categoryList = null;

    private KakaoLocalApiCategoryUtil()
    {

    }

    public static void loadCategoryMap(Context context)
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

        categoryMap = new HashMap<>();
        categoryList = result.categories;

        for (KakaoLocalApiCategory category : categoryList)
        {
            categoryMap.put(category.getName(), category);
        }
    }

    public static void setSearchValue(LocalApiPlaceParameter parameter, String word)
    {
        String categoryDescription = KakaoLocalApiCategoryUtil.getDescription(word);

        if (categoryDescription == null)
        {
            parameter.setQuery(word);
        } else
        {
            parameter.setCategoryGroupCode(categoryDescription);
        }
    }

    public static List<KakaoLocalApiCategory> toArrayList()
    {
        return categoryList;
    }

    public static String getDescription(int id)
    {
        return categoryList.get(id).getDescription();
    }

    public static String getDescription(String searchWord)
    {
        return categoryMap.get(searchWord).getDescription();
    }

    class CategoryJsonItem
    {
        List<KakaoLocalApiCategory> categories;
    }
}
