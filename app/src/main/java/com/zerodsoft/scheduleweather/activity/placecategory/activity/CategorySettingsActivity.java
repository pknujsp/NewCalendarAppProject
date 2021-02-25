package com.zerodsoft.scheduleweather.activity.placecategory.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.placecategory.adapter.CategoryExpandableListAdapter;
import com.zerodsoft.scheduleweather.databinding.ActivityCategorySettingsBinding;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;
import com.zerodsoft.scheduleweather.retrofit.PlaceCategory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CategorySettingsActivity extends AppCompatActivity
{
    private ActivityCategorySettingsBinding binding;
    private CategoryExpandableListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_category_settings);

        SharedPreferences defaultCategory = getSharedPreferences("default_category", MODE_PRIVATE);
        SharedPreferences customCategory = getSharedPreferences("custom_category", MODE_PRIVATE);

        final Set<String> selectedDefaultSet = new HashSet<>();
        final Set<String> selectedCustomSet = new HashSet<>();
        final Set<String> customCategorySet = new HashSet<>();

        defaultCategory.getStringSet("selected_categories", selectedDefaultSet);
        customCategory.getStringSet("selected_categories", selectedCustomSet);
        customCategory.getStringSet("categories", customCategorySet);

        final List<PlaceCategory> placeCategories = KakaoLocalApiCategoryUtil.getList();

        boolean[][] checkedStates = new boolean[2][];
        checkedStates[0] = new boolean[placeCategories.size()];
        checkedStates[1] = new boolean[customCategorySet.size()];

        int index = 0;

        //기본 카테고리 체크여부 설정
        for (PlaceCategory placeCategory : placeCategories)
        {
            for (String code : selectedDefaultSet)
            {
                if (code.equals(placeCategory.getCode()))
                {
                    checkedStates[0][index] = true;
                    break;
                }
            }
            index++;
        }
        index = 0;
        List<PlaceCategory> customCategories = new ArrayList<>();

        //커스텀 카테고리 체크여부 설정
        for (String description : customCategorySet)
        {
            PlaceCategory placeCategory = new PlaceCategory();
            placeCategory.setDescription(description);
            customCategories.add(placeCategory);

            for (String selectedDescription : selectedCustomSet)
            {
                if (selectedDescription.equals(description))
                {
                    checkedStates[1][index] = true;
                    break;
                }
            }
            index++;
        }

        adapter = new CategoryExpandableListAdapter(getApplicationContext(), placeCategories, customCategories, checkedStates);
        binding.categoryExpandableList.setAdapter(adapter);
    }

    @Override
    public void onBackPressed()
    {
    }
}