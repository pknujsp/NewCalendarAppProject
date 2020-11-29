package com.zerodsoft.scheduleweather.scheduleinfo.locationfragments.categoryfragments;

import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.zerodsoft.scheduleweather.activity.mapactivity.MapActivity;
import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;
import com.zerodsoft.scheduleweather.retrofit.Querys;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placecategoryresponse.PlaceCategory;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AroundLocationRepository
{
    private Querys querys;
    private LiveData<PagedList<PlaceDocuments>> pagedListLiveData;

    public AroundLocationRepository()
    {
        querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.KAKAO);
        PagedList.Config config = new PagedList.Config.Builder().setEnablePlaceholders(false).setInitialLoadSizeHint(15).build();
        pagedListLiveData = new LivePagedListBuilder(new PlaceItemDataSourceFactory(), config).build();
    }


    public LiveData<PagedList<PlaceDocuments>> getPagedListLiveData()
    {
        return pagedListLiveData;
    }

    public void searchPlaceCategory()
    {
        Map<String, String> queryMap = MapActivity.parameters.getParameterMap();
        Call<PlaceCategory> call = querys.getPlaceCategory(queryMap);

        call.enqueue(new Callback<PlaceCategory>()
        {
            @Override
            public void onResponse(Call<PlaceCategory> call, Response<PlaceCategory> response)
            {
                PlaceCategory placeCategoryResponse = response.body();

                // Message message = handler.obtainMessage();
                // message.what = TYPE_PLACE_CATEGORY;
                Bundle bundle = new Bundle();

                if (placeCategoryResponse.getPlaceCategoryDocuments().isEmpty())
                {
                    bundle.putBoolean("isEmpty", true);
                } else
                {
                    bundle.putBoolean("isEmpty", false);
                }

                bundle.putParcelable("response", placeCategoryResponse);

                //  message.setData(bundle);
                // handler.sendMessage(message);
            }

            @Override
            public void onFailure(Call<PlaceCategory> call, Throwable t)
            {
            }
        });
    }
}
