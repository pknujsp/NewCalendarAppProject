package com.zerodsoft.scheduleweather.event.foods.main.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.common.interfaces.OnBackPressedCallbackController;
import com.zerodsoft.scheduleweather.databinding.FragmentFoodsMainBinding;
import com.zerodsoft.scheduleweather.event.event.fragments.EventFragment;
import com.zerodsoft.scheduleweather.event.foods.activity.FoodsActivity;
import com.zerodsoft.scheduleweather.event.foods.categorylist.FoodCategoryTabFragment;
import com.zerodsoft.scheduleweather.event.foods.categorylist.FoodsCategoryListFragment;
import com.zerodsoft.scheduleweather.event.places.interfaces.BottomSheet;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.BottomSheetController;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.INetwork;

public class FoodsHomeFragment extends Fragment implements OnBackPressedCallbackController
{
    public static final String TAG = "FoodsHomeFragment";
    private final BottomSheetController bottomSheetController;
    private FragmentFoodsMainBinding binding;
    private final INetwork iNetwork;
    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true)
    {
        @Override
        public void handleOnBackPressed()
        {
            FragmentManager fragmentManager = getChildFragmentManager();

            if (fragmentManager.findFragmentByTag(FoodsCategoryListFragment.TAG) != null)
            {
                if (fragmentManager.findFragmentByTag(FoodsCategoryListFragment.TAG).isVisible())
                {
                    getParentFragment().getParentFragmentManager().popBackStack();
                    bottomSheetController.setStateOfBottomSheet(NewFoodsMainFragment.TAG, BottomSheetBehavior.STATE_COLLAPSED);
                } else if (fragmentManager.findFragmentByTag(FoodCategoryTabFragment.TAG) != null)
                {
                    fragmentManager.popBackStackImmediate();
                }
            }
        }
    };

    public FoodsHomeFragment(INetwork iNetwork, BottomSheetController bottomSheetController)
    {
        this.iNetwork = iNetwork;
        this.bottomSheetController = bottomSheetController;
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        addOnBackPressedCallback();
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        FoodsCategoryListFragment foodsCategoryListFragment = new FoodsCategoryListFragment(iNetwork);
        foodsCategoryListFragment.setArguments(getArguments());

        getChildFragmentManager().beginTransaction().add(binding.foodsMainFragmentContainer.getId(), foodsCategoryListFragment, FoodsCategoryListFragment.TAG)
                .commit();
    }

    @Override
    public void onHiddenChanged(boolean hidden)
    {
        super.onHiddenChanged(hidden);
        Fragment foodCategoryTabFragment = getChildFragmentManager().findFragmentByTag(FoodCategoryTabFragment.TAG);

        if (hidden)
        {
            removeOnBackPressedCallback();
            if (foodCategoryTabFragment != null)
            {
                getChildFragmentManager().beginTransaction().hide(foodCategoryTabFragment)
                        .commitNow();
            }
        } else
        {
            addOnBackPressedCallback();
            if (foodCategoryTabFragment != null)
            {
                getChildFragmentManager().beginTransaction().show(foodCategoryTabFragment)
                        .commitNow();
            }
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