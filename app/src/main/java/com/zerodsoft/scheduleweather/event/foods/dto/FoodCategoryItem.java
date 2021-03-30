package com.zerodsoft.scheduleweather.event.foods.dto;

import android.graphics.drawable.Drawable;

public class FoodCategoryItem
{
    private String categoryName;
    private Drawable categoryMainImage;
    private boolean isDefault;

    public FoodCategoryItem(String categoryName, Drawable categoryMainImage, boolean isDefault)
    {
        this.categoryName = categoryName;
        this.categoryMainImage = categoryMainImage;
        this.isDefault = isDefault;
    }

    public String getCategoryName()
    {
        return categoryName;
    }

    public Drawable getCategoryMainImage()
    {
        return categoryMainImage;
    }

    public boolean isDefault()
    {
        return isDefault;
    }
}
