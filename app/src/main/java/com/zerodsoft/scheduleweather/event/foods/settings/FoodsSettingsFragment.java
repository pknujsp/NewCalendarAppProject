package com.zerodsoft.scheduleweather.event.foods.settings;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.OnBackPressedCallbackController;
import com.zerodsoft.scheduleweather.databinding.FragmentFoodsSettingsBinding;
import com.zerodsoft.scheduleweather.event.foods.main.fragment.NewFoodsMainFragment;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.BottomSheetController;

public class FoodsSettingsFragment extends Fragment implements OnBackPressedCallbackController
{
    public static final String TAG = "FoodsSettingsFragment";
    private FragmentFoodsSettingsBinding binding;
    private final BottomSheetController bottomSheetController;
    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true)
    {
        @Override
        public void handleOnBackPressed()
        {
            bottomSheetController.setStateOfBottomSheet(NewFoodsMainFragment.TAG, BottomSheetBehavior.STATE_COLLAPSED);
        }
    };

    public FoodsSettingsFragment(BottomSheetController bottomSheetController)
    {
        this.bottomSheetController = bottomSheetController;
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
        binding = FragmentFoodsSettingsBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onHiddenChanged(boolean hidden)
    {
        super.onHiddenChanged(hidden);
        if (hidden)
        {
            removeOnBackPressedCallback();
        } else
        {
            addOnBackPressedCallback();
        }
    }

    @Override
    public void addOnBackPressedCallback()
    {
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    @Override
    public void removeOnBackPressedCallback()
    {
        onBackPressedCallback.remove();
    }
}