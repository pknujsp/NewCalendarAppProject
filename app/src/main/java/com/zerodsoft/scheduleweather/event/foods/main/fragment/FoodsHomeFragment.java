package com.zerodsoft.scheduleweather.event.foods.main.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.common.interfaces.OnBackPressedCallbackController;
import com.zerodsoft.scheduleweather.databinding.FragmentFoodsMainBinding;
import com.zerodsoft.scheduleweather.event.foods.categorylist.RestaurantListTabFragment;
import com.zerodsoft.scheduleweather.event.foods.categorylist.FoodsMenuListFragment;
import com.zerodsoft.scheduleweather.navermap.BottomSheetType;
import com.zerodsoft.scheduleweather.navermap.interfaces.BottomSheetController;
import com.zerodsoft.scheduleweather.navermap.interfaces.INetwork;

public class FoodsHomeFragment extends Fragment implements OnBackPressedCallbackController
{
    public static final String TAG = "FoodsHomeFragment";
    private final BottomSheetController bottomSheetController;
    private final NewFoodsMainFragment.FoodMenuChipsViewController foodMenuChipsViewController;
    private FragmentFoodsMainBinding binding;
    private final INetwork iNetwork;
    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true)
    {
        @Override
        public void handleOnBackPressed()
        {
            FragmentManager fragmentManager = getChildFragmentManager();

            if (fragmentManager.findFragmentByTag(FoodsMenuListFragment.TAG) != null)
            {
                if (fragmentManager.findFragmentByTag(FoodsMenuListFragment.TAG).isVisible())
                {
                    getParentFragment().getParentFragmentManager().popBackStack();
                    bottomSheetController.setStateOfBottomSheet(BottomSheetType.RESTAURANT, BottomSheetBehavior.STATE_COLLAPSED);
                } else if (fragmentManager.findFragmentByTag(RestaurantListTabFragment.TAG) != null)
                {
                    fragmentManager.popBackStackImmediate();
                }
            }
        }
    };

    public FoodsHomeFragment(INetwork iNetwork, BottomSheetController bottomSheetController, NewFoodsMainFragment.FoodMenuChipsViewController foodMenuChipsViewController)
    {
        this.iNetwork = iNetwork;
        this.bottomSheetController = bottomSheetController;
        this.foodMenuChipsViewController = foodMenuChipsViewController;
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

        FoodsMenuListFragment foodsMenuListFragment = new FoodsMenuListFragment(iNetwork, foodMenuChipsViewController, bottomSheetController);
        foodsMenuListFragment.setArguments(getArguments());

        getChildFragmentManager().beginTransaction().add(binding.foodsMainFragmentContainer.getId(), foodsMenuListFragment, FoodsMenuListFragment.TAG)
                .commit();
    }

    @Override
    public void onHiddenChanged(boolean hidden)
    {
        super.onHiddenChanged(hidden);
        Fragment foodCategoryTabFragment = getChildFragmentManager().findFragmentByTag(RestaurantListTabFragment.TAG);

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