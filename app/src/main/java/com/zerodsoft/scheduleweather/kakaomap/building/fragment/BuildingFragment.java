package com.zerodsoft.scheduleweather.kakaomap.building.fragment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.widget.Toast;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarview.callback.EventCallback;
import com.zerodsoft.scheduleweather.common.interfaces.OnBackPressedCallbackController;
import com.zerodsoft.scheduleweather.databinding.FragmentBuildingBinding;
import com.zerodsoft.scheduleweather.kakaomap.building.SgisBuildingDownloader;
import com.zerodsoft.scheduleweather.kakaomap.building.adapter.BuildingFloorListAdapter;
import com.zerodsoft.scheduleweather.kakaomap.building.model.BuildingFloorData;
import com.zerodsoft.scheduleweather.kakaomap.building.model.CompanyData;
import com.zerodsoft.scheduleweather.kakaomap.building.model.FacilityData;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.BuildingFragmentController;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.building.BuildingAttributeParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.building.FloorCompanyInfoParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.SgisBuildingRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.buildingarea.BuildingAreaItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.buildingattribute.BuildingAttributeResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.floorcompanyinfo.FloorCompanyInfoCompanyListItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.floorcompanyinfo.FloorCompanyInfoFacilityListItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.floorcompanyinfo.FloorCompanyInfoResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.floorcompanyinfo.FloorCompanyInfoResult;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.floorcompanyinfo.FloorCompanyInfoThemeCdListItem;
import com.zerodsoft.scheduleweather.sgis.SgisAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildingFragment extends Fragment implements OnBackPressedCallbackController, BuildingFloorListAdapter.OnClickDownloadListener
{
    public static final String TAG = "BuildingFragment";
    private FragmentBuildingBinding binding;
    private BuildingAreaItem buildingAreaItem;
    private BuildingFloorListAdapter buildingFloorListAdapter;
    private static final int SCROLLING_TOP = -1;
    private static final int SCROLLING_BOTTOM = 1;
    private final BuildingFragmentController buildingFragmentController;
    private final SgisBuildingDownloader sgisBuildingDownloader = new SgisBuildingDownloader()
    {
        @Override
        public void onResponseSuccessful(SgisBuildingRoot result)
        {

        }

        @Override
        public void onResponseFailed(Exception e)
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

        binding.buildingFloorInfoLayout.buildingFloorRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);

                if (!recyclerView.canScrollVertically(SCROLLING_TOP))
                {
                    if (buildingFloorListAdapter.getUnderGroundCount() < BuildingFloorListAdapter.UNDERGROUND_COUNT_MAX)
                    {
                        buildingFloorListAdapter.addFloors(BuildingFloorListAdapter.FloorClassification.UNDERGROUND);
                    }
                    return;
                }

                if (!recyclerView.canScrollVertically(SCROLLING_BOTTOM))
                {
                    if (buildingFloorListAdapter.getAboveGroundCount() < BuildingFloorListAdapter.ABOVEGROUND_COUNT_MAX)
                    {
                        buildingFloorListAdapter.addFloors(BuildingFloorListAdapter.FloorClassification.ABOVEGROUND);
                    }
                    return;
                }
            }
        });

        binding.buildingFloorInfoLayout.buildingFloorRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        buildingFloorListAdapter = new BuildingFloorListAdapter(this);
        binding.buildingFloorInfoLayout.buildingFloorRecyclerview.setAdapter(buildingFloorListAdapter);
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

    @Override
    public void getFloorInfo(String floor, EventCallback<DataWrapper<BuildingFloorData>> callback)
    {
        FloorCompanyInfoParameter parameter = new FloorCompanyInfoParameter();
        parameter.setAccessToken(SgisAuth.getSgisAuthResponse().getResult().getAccessToken());
        parameter.setFloorNo(floor);
        parameter.setSufId(buildingAreaItem.getSufId());

        sgisBuildingDownloader.getFloorCompanyInfo(parameter, new CarrierMessagingService.ResultCallback<DataWrapper<FloorCompanyInfoResponse>>()
        {
            @Override
            public void onReceiveResult(@NonNull DataWrapper<FloorCompanyInfoResponse> floorCompanyInfoResponseDataWrapper) throws RemoteException
            {
                //처리 후 리스트 갱신
                if (floorCompanyInfoResponseDataWrapper.getException() == null)
                {
                    FloorCompanyInfoResult floorCompanyInfoResult = floorCompanyInfoResponseDataWrapper.getData().getResult();

                    Map<String, String> themesMap = new HashMap<>();
                    List<FloorCompanyInfoThemeCdListItem> themeCdListItemList = floorCompanyInfoResult.getThemeCdList();

                    for (FloorCompanyInfoThemeCdListItem theme : themeCdListItemList)
                    {
                        themesMap.put(theme.getThemeCd(), theme.getThemeCdNm());
                    }
                    //company theme값 지정
                    List<FloorCompanyInfoCompanyListItem> companyInfoResultList = floorCompanyInfoResult.getCompanyList();
                    List<CompanyData> companyDataList = new ArrayList<>();

                    for (FloorCompanyInfoCompanyListItem company : companyInfoResultList)
                    {
                        CompanyData companyData = new CompanyData(company.getCorpName(), themesMap.get(company.getThemeCd()));
                        companyDataList.add(companyData);
                    }

                    //시설물 개수 정리
                    List<FloorCompanyInfoFacilityListItem> facilityListItemList = floorCompanyInfoResult.getFacilityList();

                    Map<String, Integer> facilityMap = new HashMap<>();
                    for (FloorCompanyInfoFacilityListItem facility : facilityListItemList)
                    {
                        if (facilityMap.containsKey(facility.getFacType()))
                        {
                            int count = facilityMap.get(facility.getFacType());
                            facilityMap.put(facility.getFacType(), ++count);
                        } else
                        {
                            facilityMap.put(facility.getFacType(), 1);
                        }
                    }

                    FacilityData facilityData = new FacilityData();
                    facilityData.setElevatorCount(facilityMap.containsKey("0002") ? facilityMap.get("0002").toString() : "0");
                    facilityData.setEntranceCount(facilityMap.containsKey("0004") ? facilityMap.get("0004").toString() : "0");
                    facilityData.setMovingWorkCount(facilityMap.containsKey("0005") ? facilityMap.get("0005").toString() : "0");
                    facilityData.setStairsCount(facilityMap.containsKey("0001") ? facilityMap.get("0001").toString() : "0");
                    facilityData.setToiletCount(facilityMap.containsKey("0003") ? facilityMap.get("0003").toString() : "0");
                    facilityData.setVacantRoomCount(facilityMap.containsKey("0000") ? facilityMap.get("0000").toString() : "0");

                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            callback.onResult(new DataWrapper<>(new BuildingFloorData(companyDataList, facilityData)));

                        }
                    });
                } else
                {
                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            callback.onResult(new DataWrapper<>(floorCompanyInfoResponseDataWrapper.getException()));

                        }
                    });
                }

            }
        });
    }
}