package com.zerodsoft.scheduleweather.activity.placecategory.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.placecategory.adapter.CategoryExpandableListAdapter;
import com.zerodsoft.scheduleweather.activity.placecategory.interfaces.PlaceCategoryEditPopup;
import com.zerodsoft.scheduleweather.activity.placecategory.model.PlaceCategoryData;
import com.zerodsoft.scheduleweather.activity.placecategory.viewmodel.PlaceCategoryViewModel;
import com.zerodsoft.scheduleweather.databinding.ActivityCategorySettingsBinding;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;
import com.zerodsoft.scheduleweather.room.dto.CustomPlaceCategoryDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;
import com.zerodsoft.scheduleweather.room.dto.SelectedPlaceCategoryDTO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CategorySettingsActivity extends AppCompatActivity implements PlaceCategoryEditPopup
{
    public static final int DEFAULT_CATEGORY_INDEX = 0;
    public static final int CUSTOM_CATEGORY_INDEX = 1;

    private ActivityCategorySettingsBinding binding;
    private CategoryExpandableListAdapter adapter;
    private PlaceCategoryViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_category_settings);

        binding.addCategoryButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // 커스텀 카테고리 추가 다이얼로그 표시
            }
        });

        viewModel = new ViewModelProvider(this).get(PlaceCategoryViewModel.class);
        viewModel.getPlaceCategoryDataLiveData().observe(this, new Observer<PlaceCategoryData>()
        {
            @Override
            public void onChanged(PlaceCategoryData resultData)
            {
                if (resultData != null)
                {
                    List<PlaceCategoryDTO> customCategories = resultData.getCustomCategories();
                    List<PlaceCategoryDTO> defaultCategories = resultData.getDefaultPlaceCategories();
                    List<SelectedPlaceCategoryDTO> selectedCategories = resultData.getSelectedPlaceCategories();

                    boolean[][] checkedStates = new boolean[2][];
                    checkedStates[DEFAULT_CATEGORY_INDEX] = new boolean[defaultCategories.size()];
                    checkedStates[CUSTOM_CATEGORY_INDEX] = new boolean[customCategories.size()];

                    int index = 0;

                    //기본 카테고리 체크여부 설정
                    for (PlaceCategoryDTO defaultPlaceCategoryDTO : defaultCategories)
                    {
                        for (SelectedPlaceCategoryDTO selectedPlaceCategory : selectedCategories)
                        {
                            if (selectedPlaceCategory.getCode().equals(defaultPlaceCategoryDTO.getCode()))
                            {
                                checkedStates[DEFAULT_CATEGORY_INDEX][index] = true;
                                break;
                            }
                        }
                        index++;
                    }
                    index = 0;

                    //커스텀 카테고리 체크여부 설정
                    for (PlaceCategoryDTO customPlaceCategory : customCategories)
                    {
                        for (SelectedPlaceCategoryDTO selectedPlaceCategory : selectedCategories)
                        {
                            if (selectedPlaceCategory.getCode().equals(customPlaceCategory.getCode()))
                            {
                                checkedStates[CUSTOM_CATEGORY_INDEX][index] = true;
                                break;
                            }
                        }
                        index++;
                    }

                    adapter = new CategoryExpandableListAdapter(CategorySettingsActivity.this, viewModel, defaultCategories, customCategories, checkedStates);
                    binding.categoryExpandableList.setAdapter(adapter);
                }
            }
        });
        viewModel.selectCustom();
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }

    @Override
    public void showPopup(View view)
    {
        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view, Gravity.BOTTOM);
        popupMenu.getMenuInflater().inflate(R.menu.place_category_edit_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.delete_place_category:
                        break;
                    case R.id.edit_place_category:
                        break;
                }
                return true;
            }
        });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener()
        {
            @Override
            public void onDismiss(PopupMenu menu)
            {
                popupMenu.dismiss();
            }
        });

        popupMenu.show();
    }
}