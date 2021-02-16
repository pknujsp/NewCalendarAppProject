package com.zerodsoft.scheduleweather.event.common.repository;

import android.app.Application;
import android.service.carrier.CarrierMessagingService;

import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.event.common.interfaces.ILocationDao;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;
import com.zerodsoft.scheduleweather.retrofit.Querys;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressKakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceKakaoLocalResponse;
import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dao.LocationDAO;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import java.util.Map;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationRepository implements ILocationDao
{
    private MutableLiveData<LocationDTO> locationLiveData;
    private LocationDAO locationDAO;
    private Querys querys;

    public LocationRepository(Application application)
    {
        locationDAO = AppDb.getInstance(application.getApplicationContext()).locationDAO();
        locationLiveData = new MutableLiveData<>();
    }

    @Override
    public void getAddressItem(LocalApiPlaceParameter parameter, CarrierMessagingService.ResultCallback<DataWrapper<AddressResponseDocuments>> callback)
    {
        querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.KAKAO);
        Map<String, String> queryMap = parameter.getParameterMap();
        Call<AddressKakaoLocalResponse> call = querys.getAddress(queryMap);

        call.enqueue(new Callback<AddressKakaoLocalResponse>()
        {
            @SneakyThrows
            @Override
            public void onResponse(Call<AddressKakaoLocalResponse> call, Response<AddressKakaoLocalResponse> response)
            {
                DataWrapper<AddressResponseDocuments> dataWrapper = null;

                if (response.isSuccessful())
                {
                    AddressResponseDocuments document = response.body().getAddressResponseDocumentsList().get(0);
                    dataWrapper = new DataWrapper<>(document);
                } else
                {
                    dataWrapper = new DataWrapper<>(new NullPointerException());
                }

                callback.onReceiveResult(dataWrapper);
            }

            @SneakyThrows
            @Override
            public void onFailure(Call<AddressKakaoLocalResponse> call, Throwable t)
            {
                DataWrapper<AddressResponseDocuments> dataWrapper = new DataWrapper<>(new Exception(t));
                callback.onReceiveResult(dataWrapper);
            }
        });
    }

    @Override
    public void getPlaceItem(LocalApiPlaceParameter parameter, String placeId, CarrierMessagingService.ResultCallback<DataWrapper<PlaceDocuments>> callback)
    {
        querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.KAKAO);
        Map<String, String> queryMap = parameter.getParameterMap();
        Call<PlaceKakaoLocalResponse> call = null;

        if (parameter.getQuery() == null)
        {
            call = querys.getPlaceCategory(queryMap);
        } else
        {
            call = querys.getPlaceKeyword(queryMap);
        }

        call.enqueue(new Callback<PlaceKakaoLocalResponse>()
        {
            @SneakyThrows
            @Override
            public void onResponse(Call<PlaceKakaoLocalResponse> call, Response<PlaceKakaoLocalResponse> response)
            {
                DataWrapper<PlaceDocuments> dataWrapper = null;

                if (response.isSuccessful())
                {
                    PlaceDocuments document = response.body().getPlaceDocuments().get(0);
                    dataWrapper = new DataWrapper<>(document);
                } else
                {
                    dataWrapper = new DataWrapper<>(new NullPointerException());
                }

                callback.onReceiveResult(dataWrapper);
            }

            @SneakyThrows
            @Override
            public void onFailure(Call<PlaceKakaoLocalResponse> call, Throwable t)
            {
                DataWrapper<PlaceDocuments> dataWrapper = new DataWrapper<>(new Exception(t));
                callback.onReceiveResult(dataWrapper);
            }
        });
    }


    @Override
    public void getLocation(int calendarId, long eventId, CarrierMessagingService.ResultCallback<LocationDTO> resultCallback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                LocationDTO locationDTO = locationDAO.select(calendarId, eventId);
                locationLiveData.postValue(locationDTO == null ? new LocationDTO() : locationDTO);
                resultCallback.onReceiveResult(locationDTO == null ? new LocationDTO() : locationDTO);
            }
        });
    }

    @Override
    public void hasDetailLocation(int calendarId, long eventId, CarrierMessagingService.ResultCallback<Boolean> resultCallback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                int result = locationDAO.hasLocation(calendarId, eventId);
                resultCallback.onReceiveResult(result == 1);
            }
        });
    }

    @Override
    public void addLocation(LocationDTO location, CarrierMessagingService.ResultCallback<Boolean> resultCallback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                long result = locationDAO.insert(location);
                resultCallback.onReceiveResult(result > -1);
            }
        });
    }

    @Override
    public void removeLocation(int calendarId, long eventId, CarrierMessagingService.ResultCallback<Boolean> resultCallback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                locationDAO.delete(calendarId, eventId);
                resultCallback.onReceiveResult(true);
            }
        });
    }

    @Override
    public void modifyLocation(LocationDTO location, CarrierMessagingService.ResultCallback<Boolean> resultCallback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                locationDAO.update(location);
                resultCallback.onReceiveResult(true);
            }
        });
    }

    public MutableLiveData<LocationDTO> getLocationLiveData()
    {
        return locationLiveData;
    }
}