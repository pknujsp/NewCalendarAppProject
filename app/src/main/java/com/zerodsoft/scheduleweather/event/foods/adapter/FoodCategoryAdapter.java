package com.zerodsoft.scheduleweather.event.foods.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.event.foods.dto.FoodCategoryItem;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnClickedCategoryItem;

import java.util.ArrayList;
import java.util.List;

public class FoodCategoryAdapter extends BaseAdapter
{
    private List<FoodCategoryItem> items = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private FoodCategoryViewHolder viewHolder;
    private final OnClickedCategoryItem onClickedCategoryItem;

    public FoodCategoryAdapter(Context context, Fragment fragment)
    {
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.onClickedCategoryItem = (OnClickedCategoryItem) fragment;
    }

    @Override
    public int getCount()
    {
        return items.size();
    }

    @Override
    public Object getItem(int i)
    {
        return items.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        if (view == null)
        {
            view = layoutInflater.inflate(R.layout.food_category_item, null);

            viewHolder = new FoodCategoryViewHolder(items.get(i));
            viewHolder.foodCategoryNameTextView = (TextView) view.findViewById(R.id.food_category_name);
            viewHolder.foodCategoryImageView = (ImageView) view.findViewById(R.id.food_category_image);

            view.setTag(viewHolder);
        }

        viewHolder.foodCategoryNameTextView.setText(viewHolder.foodCategoryItem.getCategoryName());
        if (viewHolder.foodCategoryItem.isDefault())
        {
            viewHolder.foodCategoryImageView.setImageDrawable(viewHolder.foodCategoryItem.getCategoryMainImage());
            viewHolder.foodCategoryImageView.setVisibility(View.VISIBLE);
        } else
        {
            viewHolder.foodCategoryImageView.setVisibility(View.GONE);
        }

        view.setOnClickListener(onClickListener);

        return view;
    }

    public void addItem(FoodCategoryItem item)
    {
        items.add(item);
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            onClickedCategoryItem.onClickedFoodCategory(((FoodCategoryViewHolder) view.getTag()).foodCategoryItem);
        }
    };

    static class FoodCategoryViewHolder
    {
        TextView foodCategoryNameTextView;
        ImageView foodCategoryImageView;
        FoodCategoryItem foodCategoryItem;

        public FoodCategoryViewHolder(FoodCategoryItem foodCategoryItem)
        {
            this.foodCategoryItem = foodCategoryItem;
        }
    }
}
