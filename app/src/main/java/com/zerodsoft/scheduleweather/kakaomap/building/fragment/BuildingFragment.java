package com.zerodsoft.scheduleweather.kakaomap.building.fragment;

import android.app.Dialog;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.OnBackPressedCallbackController;
import com.zerodsoft.scheduleweather.databinding.FragmentBuildingBinding;
import com.zerodsoft.scheduleweather.kakaomap.building.SgisBuildingDownloader;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.BuildingFragmentController;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.building.BuildingAttributeParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.SgisBuildingRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.buildingarea.BuildingAreaItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.buildingattribute.BuildingAttributeResponse;
import com.zerodsoft.scheduleweather.sgis.SgisAuth;

public class BuildingFragment extends Fragment implements OnBackPressedCallbackController
{
    public static final String TAG = "BuildingFragment";
    private FragmentBuildingBinding binding;
    private BuildingAreaItem buildingAreaItem;
    private final BuildingFragmentController buildingFragmentController;
    private final SgisBuildingDownloader sgisBuildingDownloader = new SgisBuildingDownloader()
    {
        @Override
        public void onResponse(DataWrapper<? extends SgisBuildingRoot> result)
        {

        }
    };

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

        binding.errorText.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.buildingInfoLayout.getRoot().setVisibility(View.GONE);
        binding.buildingFloorInfoLayout.getRoot().setVisibility(View.GONE);

        clearText();

        BuildingAttributeParameter buildingAttributeParameter = new BuildingAttributeParameter();
        buildingAttributeParameter.setAccessToken(SgisAuth.getSgisAuthResponse().getResult().getAccessToken());
        buildingAttributeParameter.setSufId(buildingAreaItem.getSufId());

        sgisBuildingDownloader.getBuildingAttribute(buildingAttributeParameter, new CarrierMessagingService.ResultCallback<DataWrapper<BuildingAttributeResponse>>()
        {
            @Override
            public void onReceiveResult(@NonNull DataWrapper<BuildingAttributeResponse> buildingAttributeResponseDataWrapper) throws RemoteException
            {
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        binding.progressBar.setVisibility(View.GONE);

                        if (buildingAttributeResponseDataWrapper.getException() == null)
                        {
                            setBuildingInfo(buildingAttributeResponseDataWrapper.getData());
                            binding.buildingInfoLayout.getRoot().setVisibility(View.VISIBLE);
                            binding.buildingFloorInfoLayout.getRoot().setVisibility(View.VISIBLE);
                        } else
                        {
                            binding.errorText.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
    }

    private void setBuildingInfo(BuildingAttributeResponse buildingAttributeResponse)
    {
        String notData = getString(R.string.not_data);

        binding.buildingInfoLayout.buildingName.setText(buildingAttributeResponse.getResult().getBdName() == null ? notData : buildingAttributeResponse.getResult().getBdName());
        binding.buildingInfoLayout.buildingNewAddress.setText(buildingAttributeResponse.getResult().getBdNewAddress() == null ? notData : buildingAttributeResponse.getResult().getBdNewAddress());
        binding.buildingInfoLayout.buildingAdmAddress.setText(buildingAttributeResponse.getResult().getBdAdmAddr() == null ? notData : buildingAttributeResponse.getResult().getBdAdmAddr());
        binding.buildingInfoLayout.buildingHighestFloor.setText(buildingAttributeResponse.getResult().getHighestFloor() == null ? notData : buildingAttributeResponse.getResult().getHighestFloor());
        binding.buildingInfoLayout.buildingLowestFloor.setText(buildingAttributeResponse.getResult().getLowestFloor() == null ? notData : buildingAttributeResponse.getResult().getLowestFloor());
    }

    private void clearText()
    {
        binding.buildingInfoLayout.buildingName.setText("");
        binding.buildingInfoLayout.buildingNewAddress.setText("");
        binding.buildingInfoLayout.buildingAdmAddress.setText("");
        binding.buildingInfoLayout.buildingHighestFloor.setText("");
        binding.buildingInfoLayout.buildingLowestFloor.setText("");

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