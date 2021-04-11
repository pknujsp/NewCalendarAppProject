package com.zerodsoft.scheduleweather.kakaomap.building;

import android.app.Dialog;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.OnBackPressedCallbackController;
import com.zerodsoft.scheduleweather.databinding.FragmentBuildingBinding;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.BuildingFragmentController;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.buildingarea.BuildingAreaItem;

public class BuildingFragment extends Fragment implements OnBackPressedCallbackController
{
    public static final String TAG = "BuildingFragment";
    private FragmentBuildingBinding binding;
    private BuildingAreaItem buildingAreaItem;
    private final BuildingFragmentController buildingFragmentController;

    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true)
    {
        @Override
        public void handleOnBackPressed()
        {
            buildingFragmentController.closeBuildingFragments(TAG);
        }
    };

    public BuildingFragment(BuildingFragmentController buildingFragmentController)
    {
        this.buildingFragmentController = buildingFragmentController;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        buildingAreaItem = getArguments().getParcelable("building");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = FragmentBuildingBinding.inflate(inflater);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void addOnBackPressedCallback()
    {
        getActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    @Override
    public void removeOnBackPressedCallback()
    {
        onBackPressedCallback.remove();
    }
}