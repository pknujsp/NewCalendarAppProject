package com.zerodsoft.scheduleweather.kakaomap.building;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.OnBackPressedCallbackController;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.databinding.FragmentBuildingListBinding;
import com.zerodsoft.scheduleweather.event.weather.repository.SgisTranscoord;
import com.zerodsoft.scheduleweather.kakaomap.building.adapter.BuildingListAdapter;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.paremeters.NearbyMsrstnListParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.SgisAuthParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.TransCoordParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.building.BuildingAreaParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.auth.SgisAuthResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.SgisBuildingRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.buildingarea.BuildingAreaItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.buildingarea.BuildingAreaResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.buildingattribute.BuildingAttributeResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.floorcompanyinfo.FloorCompanyInfoResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.flooretcfacility.FloorEtcFacilityResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.transcoord.TransCoordResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.transcoord.TransCoordResult;
import com.zerodsoft.scheduleweather.sgis.SgisAuth;


public class BuildingListFragment extends Fragment implements OnClickedListItem<BuildingAreaItem>, OnBackPressedCallbackController
{
    private FragmentBuildingListBinding binding;

    private TransCoordResponse minTransCoordResponse;
    private TransCoordResponse maxTransCoordResponse;

    private String minLongitude;
    private String minLatitude;
    private String maxLongitude;
    private String maxLatitude;

    private BuildingListAdapter buildingListAdapter;

    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true)
    {
        @Override
        public void handleOnBackPressed()
        {

        }
    };

    private final SgisBuildingDownloader sgisBuildingDownloader = new SgisBuildingDownloader()
    {
        @Override
        public void onResponse(DataWrapper<? extends SgisBuildingRoot> result)
        {
            if (result.getData() instanceof BuildingAreaResponse)
            {

            } else if (result.getData() instanceof BuildingAttributeResponse)
            {

            } else if (result.getData() instanceof FloorEtcFacilityResponse)
            {

            } else if (result.getData() instanceof FloorCompanyInfoResponse)
            {

            }

        }
    };

    private final SgisAuth sgisAuth = new SgisAuth()
    {
        @Override
        public void onResponse(DataWrapper<? extends SgisAuthResponse> result)
        {
            if (result.getException() == null)
            {
                if (result.getData() instanceof SgisAuthResponse)
                {
                    SgisAuth.setSgisAuthResponse(result.getData());
                    transcoord();
                }
            } else
            {

            }
        }
    };

    private final SgisTranscoord minSgisTranscoord = new SgisTranscoord()
    {
        @Override
        public void onResponse(DataWrapper<? extends TransCoordResponse> result)
        {
            if (result.getException() == null)
            {
                if (result.getData() instanceof TransCoordResponse)
                {
                    minTransCoordResponse = result.getData();
                    getBuildingList();
                }
            } else
            {

            }
        }
    };

    private final SgisTranscoord maxSgisTranscoord = new SgisTranscoord()
    {
        @Override
        public void onResponse(DataWrapper<? extends TransCoordResponse> result)
        {
            if (result.getException() == null)
            {
                if (result.getData() instanceof TransCoordResponse)
                {
                    maxTransCoordResponse = result.getData();
                    getBuildingList();
                }
            } else
            {

            }
        }
    };

    private void getBuildingList()
    {
        if (minTransCoordResponse != null && maxTransCoordResponse != null)
        {
            BuildingAreaParameter parameter = new BuildingAreaParameter();

            parameter.setAccessToken(SgisAuth.getSgisAuthResponse().getResult().getAccessToken());
            parameter.setMinX(minTransCoordResponse.getResult().getPosX());
            parameter.setMinY(minTransCoordResponse.getResult().getPosY());
            parameter.setMaxX(maxTransCoordResponse.getResult().getPosX());
            parameter.setMaxY(maxTransCoordResponse.getResult().getPosY());

            sgisBuildingDownloader.getBuildingList(parameter, new CarrierMessagingService.ResultCallback<DataWrapper<BuildingAreaResponse>>()
            {
                @Override
                public void onReceiveResult(@NonNull DataWrapper<BuildingAreaResponse> buildingAreaResponseDataWrapper) throws RemoteException
                {
                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            //리스트 생성
                            buildingListAdapter = new BuildingListAdapter(buildingAreaResponseDataWrapper.getData().getResult(), BuildingListFragment.this);
                            binding.buildingSearchList.setAdapter(buildingListAdapter);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        minLatitude = bundle.getString("minLatitude");
        minLongitude = bundle.getString("minLongitude");
        maxLatitude = bundle.getString("maxLatitude");
        maxLongitude = bundle.getString("maxLongitude");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = FragmentBuildingListBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        binding.buildingSearchList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.buildingSearchList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        transcoord();
    }

    public void transcoord()
    {
        if (SgisAuth.getSgisAuthResponse() != null)
        {
            TransCoordParameter minParameter = new TransCoordParameter();
            minParameter.setAccessToken(SgisAuth.getSgisAuthResponse().getResult().getAccessToken());
            minParameter.setSrc(TransCoordParameter.WGS84);
            minParameter.setDst(TransCoordParameter.JUNGBU_ORIGIN);
            minParameter.setPosX(minLongitude);
            minParameter.setPosY(minLatitude);

            TransCoordParameter maxParameter = new TransCoordParameter();
            maxParameter.setAccessToken(SgisAuth.getSgisAuthResponse().getResult().getAccessToken());
            maxParameter.setSrc(TransCoordParameter.WGS84);
            maxParameter.setDst(TransCoordParameter.JUNGBU_ORIGIN);
            maxParameter.setPosX(maxLongitude);
            maxParameter.setPosY(maxLatitude);

            minSgisTranscoord.transcoord(minParameter);
            maxSgisTranscoord.transcoord(maxParameter);
        } else
        {
            sgisAuth.auth(new SgisAuthParameter());
        }
    }

    @Override
    public void onClickedListItem(BuildingAreaItem e)
    {
        //change fragment

    }

    @Override
    public void deleteListItem(BuildingAreaItem e, int position)
    {
        //사용안함
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