package com.zerodsoft.scheduleweather.retrofit;

import android.content.Context;

import com.zerodsoft.scheduleweather.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class KakaoLocalApiCategoryCode
{
    private static Map<String, String> categoryMap = null;
    private static List<KakaoLocalApiCategory> categoryList = null;

    private KakaoLocalApiCategoryCode()
    {

    }

    public static void loadCategoryMap(Context context)
    {
        if (categoryMap == null)
        {
            String[] names = context.getResources().getStringArray(R.array.kakao_local_api_category_name);
            String[] descriptions = context.getResources().getStringArray(R.array.kakao_local_api_category_description);

            categoryMap = new HashMap<>();

            for (int i = 0; i < names.length; i++)
            {
                categoryMap.put(names[i], descriptions[i]);
            }
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

    public static String getDescription(String name)
    {
        return categoryMap.get(name);
    }
}
