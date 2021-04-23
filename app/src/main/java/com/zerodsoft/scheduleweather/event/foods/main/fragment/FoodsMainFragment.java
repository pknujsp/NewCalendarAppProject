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

import com.zerodsoft.scheduleweather.common.interfaces.OnBackPressedCallbackController;
import com.zerodsoft.scheduleweather.databinding.FragmentFoodsMainBinding;
import com.zerodsoft.scheduleweather.event.foods.activity.FoodsActivity;
import com.zerodsoft.scheduleweather.event.foods.categorylist.FoodCategoryTabFragment;
import com.zerodsoft.scheduleweather.event.foods.categorylist.FoodsCategoryListFragment;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.INetwork;

public class FoodsMainFragment extends Fragment implements OnBackPressedCallbackController
{
    public static final String TAG = "FoodsMainFragment";
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
                    getActivity().finish();
                } else if (fragmentManager.findFragmentByTag(FoodCategoryTabFragment.TAG) != null)
                {
                    fragmentManager.popBackStackImmediate();
                }
            }
        }
    };

    public FoodsMainFragment(INetwork iNetwork)
    {
        this.iNetwork = iNetwork;
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        addOnBackPressedCallback();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        // removeOnBackPressedCallback();
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