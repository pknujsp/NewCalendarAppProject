package com.zerodsoft.scheduleweather.retrofit;

import android.content.Context;

import com.google.gson.Gson;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class KakaoLocalApiCategoryUtil
{
    private static List<KakaoLocalApiCategory> categoryList = null;

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
    }


    public static boolean isCategory(String word)
    {
        boolean isCategory = false;
        try
        {
            int id = Integer.parseInt(word);
            if (id >= 0 && id < categoryList.size())
            {
                isCategory = true;
            }
        } catch (NumberFormatException e)
        {
            isCategory = false;
        }
        return isCategory;
    }

    public static List<KakaoLocalApiCategory> getList()
    {
        return categoryList;
    }

    public static String getName(int id) throws IndexOutOfBoundsException
    {
        return categoryList.get(id).getName();
    }

    public static KakaoLocalApiCategory getCategoryInfo(int position)
    {
        return categoryList.get(position);
    }

    public static String getDescription(int id)
    {
        return categoryList.get(id).getDescription();
    }

    class CategoryJsonItem
    {
        List<KakaoLocalApiCategory> categories;
    }
}