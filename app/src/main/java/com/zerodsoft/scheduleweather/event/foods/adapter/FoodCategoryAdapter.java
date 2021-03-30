package com.zerodsoft.scheduleweather.event.foods.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.zerodsoft.scheduleweather.event.foods.dto.FoodCategoryItem;

import java.util.ArrayList;
import java.util.List;

public class FoodCategoryAdapter extends BaseAdapter
{
    private List<FoodCategoryItem> items = new ArrayList<>();

    @Override
    public int getCount()
    {
        return 0;
    }

    @Override
    public Object getItem(int i)
    {
        return null;
    }

    @Override
    public long getItemId(int i)
    {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        return null;
    }

    public void addItem(FoodCategoryItem item)
    {
        items.add(item);
    }
}
