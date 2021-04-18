package com.zerodsoft.scheduleweather.event.foods.settings;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.OnBackPressedCallbackController;
import com.zerodsoft.scheduleweather.databinding.FragmentFoodsSettingsBinding;

public class FoodsSettingsFragment extends Fragment implements OnBackPressedCallbackController
{
    public static final String TAG = "FoodsSettingsFragment";
    private FragmentFoodsSettingsBinding binding;
    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true)
    {
        @Override
        public void handleOnBackPressed()
        {
            getActivity().finish();
        }
    };

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
       // addOnBackPressedCallback();
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