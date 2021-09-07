package com.zerodsoft.calendarplatform.activity.placecategory.model;

import com.zerodsoft.calendarplatform.retrofit.KakaoLocalApiCategoryUtil;
import com.zerodsoft.calendarplatform.room.dto.PlaceCategoryDTO;
import com.zerodsoft.calendarplatform.room.dto.SelectedPlaceCategoryDTO;

import java.util.ArrayList;
import java.util.List;

public class PlaceCategoryData
{
    private List<PlaceCategoryDTO> defaultPlaceCategories = KakaoLocalApiCategoryUtil.getDefaultPlaceCategoryList();
    private List<PlaceCategoryDTO> customCategories = new ArrayList<>();
    private List<SelectedPlaceCategoryDTO> selectedPlaceCategories =new ArrayList<>();

    public PlaceCategoryData()
    {
    }

    public List<PlaceCategoryDTO> getDefaultPlaceCategories()
    {
        return defaultPlaceCategories;
    }

    public void setDefaultPlaceCategories(List<PlaceCategoryDTO> defaultPlaceCategories)
    {
        this.defaultPlaceCategories = defaultPlaceCategories;
    }

    public List<PlaceCategoryDTO> getCustomCategories()
    {
        return customCategories;
    }

    public void setCustomCategories(List<PlaceCategoryDTO> customCategories)
    {
        this.customCategories = customCategories;
    }

    public List<SelectedPlaceCategoryDTO> getSelectedPlaceCategories()
    {
        return selectedPlaceCategories;
    }

    public void setSelectedPlaceCategories(List<SelectedPlaceCategoryDTO> selectedPlaceCategories)
    {
        this.selectedPlaceCategories = selectedPlaceCategories;
    }
}
