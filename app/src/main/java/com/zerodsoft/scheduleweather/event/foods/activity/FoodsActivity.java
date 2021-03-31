package com.zerodsoft.scheduleweather.event.foods.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.ActivityFoodsBinding;
import com.zerodsoft.scheduleweather.event.foods.fragment.FoodsMainFragment;

public class FoodsActivity extends AppCompatActivity
{
    private ActivityFoodsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_foods);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        FoodsMainFragment foodsMainFragment = new FoodsMainFragment();
        fragmentTransaction.add(binding.fragmentContainer.getId(), foodsMainFragment, FoodsMainFragment.TAG).commit();
    }
}