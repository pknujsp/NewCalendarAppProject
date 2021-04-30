package com.zerodsoft.scheduleweather.navermap.building;

import android.service.carrier.CarrierMessagingService;

import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;
import com.zerodsoft.scheduleweather.retrofit.Querys;
import com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.building.BuildingAreaParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.building.BuildingAttributeParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.building.FloorCompanyInfoParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.building.FloorEtcFacilityParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.SgisBuildingRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.buildingarea.BuildingAreaResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.buildingattribute.BuildingAttributeResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.floorcompanyinfo.FloorCompanyInfoResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.flooretcfacility.FloorEtcFacilityResponse;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class SgisBuildingDownloader extends JsonDownloader<SgisBuildingRoot>
{
    public void getBuildingList(BuildingAreaParameter parameter, CarrierMessagingService.ResultCallback<DataWrapper<BuildingAreaResponse>> callback)
    {
        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.SGIS_FIGURE);
        Call<BuildingAreaResponse> call = querys.buildingArea(parameter.toMap());

        call.enqueue(new Callback<BuildingAreaResponse>()
        {
            @SneakyThrows
            @Override
            public void onResponse(Call<BuildingAreaResponse> call, Response<BuildingAreaResponse> response)
            {
                DataWrapper<BuildingAreaResponse> dataWrapper = new DataWrapper<>(response.body());
                callback.onReceiveResult(dataWrapper);
            }

            @SneakyThrows
            @Override
            public void onFailure(Call<BuildingAreaResponse> call, Throwable t)
            {
                DataWrapper<BuildingAreaResponse> dataWrapper = new DataWrapper<>(new Exception(t));
                callback.onReceiveResult(dataWrapper);
            }
        });
    }

    public void getBuildingAttribute(BuildingAttributeParameter parameter, CarrierMessagingService.ResultCallback<DataWrapper<BuildingAttributeResponse>> callback)
    {
        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.SGIS_FIGURE);
        Call<BuildingAttributeResponse> call = querys.buildingAttribute(parameter.toMap());

        call.enqueue(new Callback<BuildingAttributeResponse>()
        {
            @SneakyThrows
            @Override
            public void onResponse(Call<BuildingAttributeResponse> call, Response<BuildingAttributeResponse> response)
            {
                DataWrapper<BuildingAttributeResponse> dataWrapper = new DataWrapper<>(response.body());
                callback.onReceiveResult(dataWrapper);
            }

            @SneakyThrows
            @Override
            public void onFailure(Call<BuildingAttributeResponse> call, Throwable t)
            {
                DataWrapper<BuildingAttributeResponse> dataWrapper = new DataWrapper<>(new Exception(t));
                callback.onReceiveResult(dataWrapper);
            }
        });
    }

    public void getFloorEtcFacility(FloorEtcFacilityParameter parameter, CarrierMessagingService.ResultCallback<DataWrapper<FloorEtcFacilityResponse>> callback)
    {
        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.SGIS_FIGURE);
        Call<FloorEtcFacilityResponse> call = querys.floorEtcFacility(parameter.toMap());

        call.enqueue(new Callback<FloorEtcFacilityResponse>()
        {
            @SneakyThrows
            @Override
            public void onResponse(Call<FloorEtcFacilityResponse> call, Response<FloorEtcFacilityResponse> response)
            {
                DataWrapper<FloorEtcFacilityResponse> dataWrapper = new DataWrapper<>(response.body());
                callback.onReceiveResult(dataWrapper);
            }

            @SneakyThrows
            @Override
            public void onFailure(Call<FloorEtcFacilityResponse> call, Throwable t)
            {
                DataWrapper<FloorEtcFacilityResponse> dataWrapper = new DataWrapper<>(new Exception(t));
                callback.onReceiveResult(dataWrapper);
            }
        });
    }

    public void getFloorCompanyInfo(FloorCompanyInfoParameter parameter, CarrierMessagingService.ResultCallback<DataWrapper<FloorCompanyInfoResponse>> callback)
    {
        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.SGIS_FIGURE);
        Call<FloorCompanyInfoResponse> call = querys.floorCompanyInfo(parameter.toMap());

        call.enqueue(new Callback<FloorCompanyInfoResponse>()
        {
            @SneakyThrows
            @Override
            public void onResponse(Call<FloorCompanyInfoResponse> call, Response<FloorCompanyInfoResponse> response)
            {
                DataWrapper<FloorCompanyInfoResponse> dataWrapper = new DataWrapper<>(response.body());
                callback.onReceiveResult(dataWrapper);
            }

            @SneakyThrows
            @Override
            public void onFailure(Call<FloorCompanyInfoResponse> call, Throwable t)
            {
                DataWrapper<FloorCompanyInfoResponse> dataWrapper = new DataWrapper<>(new Exception(t));
                callback.onReceiveResult(dataWrapper);
            }
        });
    }
}
