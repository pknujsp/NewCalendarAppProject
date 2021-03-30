package com.zerodsoft.scheduleweather.event.foods.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentFoodsMainBinding;
import com.zerodsoft.scheduleweather.event.foods.adapter.FoodCategoryAdapter;
import com.zerodsoft.scheduleweather.event.foods.dto.FoodCategoryItem;

import java.util.ArrayList;
import java.util.List;

public class FoodsMainFragment extends Fragment
{
    private FragmentFoodsMainBinding binding;

    public FoodsMainFragment()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = FragmentFoodsMainBinding.inflate(inflater);
        return binding.getRoot();
    }

    private void setCategories()
    {
        FoodCategoryAdapter foodCategoryAdapter = new FoodCategoryAdapter();

        Context context = getContext();
        foodCategoryAdapter.addItem(new FoodCategoryItem(getString(R.string.hansik), context.getDrawable(R.drawable.cloud_day_icon), true));
        foodCategoryAdapter.addItem(new FoodCategoryItem(getString(R.string.jungsik), context.getDrawable(R.drawable.cloud_day_icon), true));
        foodCategoryAdapter.addItem(new FoodCategoryItem(getString(R.string.illsik), context.getDrawable(R.drawable.cloud_day_icon), true));
        foodCategoryAdapter.addItem(new FoodCategoryItem(getString(R.string.sashimi), context.getDrawable(R.drawable.cloud_day_icon), true));
        foodCategoryAdapter.addItem(new FoodCategoryItem(getString(R.string.yangsik), context.getDrawable(R.drawable.cloud_day_icon), true));
        foodCategoryAdapter.addItem(new FoodCategoryItem(getString(R.string.asian), context.getDrawable(R.drawable.cloud_day_icon), true));
        foodCategoryAdapter.addItem(new FoodCategoryItem(getString(R.string.chicken), context.getDrawable(R.drawable.cloud_day_icon), true));
        foodCategoryAdapter.addItem(new FoodCategoryItem(getString(R.string.fastfood), context.getDrawable(R.drawable.cloud_day_icon), true));
        foodCategoryAdapter.addItem(new FoodCategoryItem(getString(R.string.donkartz), context.getDrawable(R.drawable.cloud_day_icon), true));
        foodCategoryAdapter.addItem(new FoodCategoryItem(getString(R.string.jjim), context.getDrawable(R.drawable.cloud_day_icon), true));
        foodCategoryAdapter.addItem(new FoodCategoryItem(getString(R.string.tang), context.getDrawable(R.drawable.cloud_day_icon), true));
        foodCategoryAdapter.addItem(new FoodCategoryItem(getString(R.string.bunsik), context.getDrawable(R.drawable.cloud_day_icon), true));
        foodCategoryAdapter.addItem(new FoodCategoryItem(getString(R.string.juk), context.getDrawable(R.drawable.cloud_day_icon), true));

        binding.foodCategoryGridview.setAdapter(foodCategoryAdapter);

        /*
          <string name="hansik">한식</string>
    <string name="jungsik">중식</string>
    <string name="illsik">일식</string>
    <string name="sashimi">회</string>
    <string name="yangsik">양식</string>
    <string name="asian">아시안</string>
    <string name="chicken">치킨</string>
    <string name="fastfood">패스트푸드</string>
    <string name="donkartz">돈까스</string>
    <string name="jjim">찜</string>
    <string name="tang">탕</string>
    <string name="bunsik">분식</string>
    <string name="juk">죽</string>
         */
    }
}