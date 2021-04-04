package com.zerodsoft.scheduleweather.event.foods.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.event.foods.dto.FoodCategoryItem;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnClickedCategoryItem;

import java.util.ArrayList;
import java.util.List;

public class FoodCategoryAdapter extends RecyclerView.Adapter<FoodCategoryAdapter.CategoryViewHolder>
{
    private List<FoodCategoryItem> items = new ArrayList<>();
    private final OnClickedCategoryItem onClickedCategoryItem;

    public FoodCategoryAdapter(Context context, Fragment fragment)
    {
        this.onClickedCategoryItem = (OnClickedCategoryItem) fragment;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new CategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.food_category_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position)
    {
        holder.onBind();
    }

    @Override
    public int getItemCount()
    {
        return items.size();
    }

    public void setItems(List<FoodCategoryItem> items)
    {
        this.items = items;
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder
    {
        private TextView foodCategoryNameTextView;
        private ImageView foodCategoryImageView;
        private FoodCategoryItem foodCategoryItem;

        public CategoryViewHolder(@NonNull View itemView)
        {
            super(itemView);

            foodCategoryNameTextView = (TextView) itemView.findViewById(R.id.food_category_name);
            foodCategoryImageView = (ImageView) itemView.findViewById(R.id.food_category_image);
        }

        public void onBind()
        {
            foodCategoryItem = items.get(getAdapterPosition());

            foodCategoryNameTextView.setText(foodCategoryItem.getCategoryName());
            if (foodCategoryItem.isDefault())
            {
                foodCategoryImageView.setImageDrawable(foodCategoryItem.getCategoryMainImage());
                Glide.with(itemView).load(foodCategoryItem.getCategoryMainImage()).circleCrop().into(foodCategoryImageView);
                foodCategoryImageView.setVisibility(View.VISIBLE);
            } else
            {
                foodCategoryImageView.setVisibility(View.GONE);
            }

            itemView.getRootView().setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    onClickedCategoryItem.onClickedFoodCategory(foodCategoryItem);
                }
            });
        }
    }

}
