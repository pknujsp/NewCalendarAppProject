package com.zerodsoft.scheduleweather.activity.mapactivity.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryCode;
import com.zerodsoft.scheduleweather.retrofit.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.Querys;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.LocationSearchResult;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placecategoryresponse.PlaceCategory;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placekeywordresponse.PlaceKeyword;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapController
{
    public static final int TYPE_ADDRESS = 0;
    public static final int TYPE_PLACE_KEYWORD = 1;
    public static final int TYPE_PLACE_CATEGORY = 2;
    public static final int TYPE_COORD_TO_ADDRESS = 3;
    public static final int TYPE_NOT = 4;

    private int calledDownloadTotalCount = 0;
    private String fragmentTag;
    private LocalApiPlaceParameter parameter;
    private int dataType = TYPE_NOT;

    private OnDownloadListener onDownloadListener;

    public MapController(Activity activity)
    {
        onDownloadListener = (OnDownloadListener) activity;
    }


    public interface OnDownloadListener
    {
        void onDownloadedData(LocalApiPlaceParameter parameter, int dataType, String fragmentTag, LocationSearchResult locationSearchResult);

        void requestData(LocalApiPlaceParameter parameter, int dataType, String fragmentTag);
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler()
    {
        private int totalCallCount = 0;
        private LocationSearchResult locationSearchResult = new LocationSearchResult();

        @Override
        public void handleMessage(Message msg)
        {
            ++totalCallCount;
            if (locationSearchResult == null)
            {
                locationSearchResult = new LocationSearchResult();
            }

            Bundle bundle = msg.getData();

            if (!bundle.getBoolean("isEmpty"))
            {
                switch (msg.what)
                {
                    case TYPE_ADDRESS:
                        locationSearchResult.setAddressResponse(bundle.getParcelable("response"));
                        break;
                    case TYPE_PLACE_KEYWORD:
                        locationSearchResult.setPlaceKeywordResponse(bundle.getParcelable("response"));
                        break;
                    case TYPE_PLACE_CATEGORY:
                        locationSearchResult.setPlaceCategoryResponse(bundle.getParcelable("response"));
                        break;
                    case TYPE_COORD_TO_ADDRESS:
                        locationSearchResult.setCoordToAddressResponse(bundle.getParcelable("response"));
                        break;
                }
            }

            if (totalCallCount == calledDownloadTotalCount)
            {
                locationSearchResult.setDownloadedDate(new Date(System.currentTimeMillis()));
                try
                {
                    onDownloadListener.onDownloadedData((LocalApiPlaceParameter) parameter.clone(), dataType, fragmentTag, (LocationSearchResult) locationSearchResult.clone());
                } catch (CloneNotSupportedException e)
                {
                    e.printStackTrace();
                }

                dataType = TYPE_NOT;
                calledDownloadTotalCount = 0;
                totalCallCount = 0;
            }
        }
    };

    public void searchAddress(LocalApiPlaceParameter parameter)
    {
        Querys querys = HttpCommunicationClient.getApiService();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("query", parameter.getQuery());
        queryMap.put("page", parameter.getPage());
        queryMap.put("AddressSize", "15");
        Call<AddressResponse> call = querys.getAddress(queryMap);

        call.enqueue(new Callback<AddressResponse>()
        {
            @Override
            public void onResponse(Call<AddressResponse> call, Response<AddressResponse> response)
            {
                AddressResponse addressResponse = response.body();

                Message message = handler.obtainMessage();
                message.what = TYPE_ADDRESS;
                Bundle bundle = new Bundle();

                if (addressResponse.getAddressResponseDocumentsList().isEmpty())
                {
                    bundle.putBoolean("isEmpty", true);
                } else
                {
                    bundle.putBoolean("isEmpty", false);
                }
                bundle.putParcelable("response", addressResponse);

                message.setData(bundle);
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(Call<AddressResponse> call, Throwable t)
            {
            }
        });
    }

    public void searchPlaceKeyWord(LocalApiPlaceParameter parameter)
    {
        Querys querys = HttpCommunicationClient.getApiService();
        Map<String, String> queryMap = parameter.getParameterMap();
        Call<PlaceKeyword> call = querys.getPlaceKeyword(queryMap);

        call.enqueue(new Callback<PlaceKeyword>()
        {
            @Override
            public void onResponse(Call<PlaceKeyword> call, Response<PlaceKeyword> response)
            {
                PlaceKeyword placeKeywordResponse = response.body();

                Message message = handler.obtainMessage();
                message.what = TYPE_PLACE_KEYWORD;
                Bundle bundle = new Bundle();

                if (placeKeywordResponse.getPlaceKeywordDocuments().isEmpty())
                {
                    bundle.putBoolean("isEmpty", true);
                } else
                {
                    bundle.putBoolean("isEmpty", false);
                }
                bundle.putParcelable("response", placeKeywordResponse);

                message.setData(bundle);
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(Call<PlaceKeyword> call, Throwable t)
            {
            }
        });
    }

    public void searchPlaceCategory(LocalApiPlaceParameter parameter)
    {
        Querys querys = HttpCommunicationClient.getApiService();
        Map<String, String> queryMap = parameter.getParameterMap();
        Call<PlaceCategory> call = querys.getPlaceCategory(queryMap);

        call.enqueue(new Callback<PlaceCategory>()
        {
            @Override
            public void onResponse(Call<PlaceCategory> call, Response<PlaceCategory> response)
            {
                PlaceCategory placeCategoryResponse = response.body();

                Message message = handler.obtainMessage();
                message.what = TYPE_PLACE_CATEGORY;
                Bundle bundle = new Bundle();

                if (placeCategoryResponse.getPlaceCategoryDocuments().isEmpty())
                {
                    bundle.putBoolean("isEmpty", true);
                } else
                {
                    bundle.putBoolean("isEmpty", false);
                }

                bundle.putParcelable("response", placeCategoryResponse);

                message.setData(bundle);
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(Call<PlaceCategory> call, Throwable t)
            {
            }
        });
    }

    public void getCoordToAddress(LocalApiPlaceParameter parameter)
    {
        Querys querys = HttpCommunicationClient.getApiService();
        Map<String, String> queryMap = parameter.getParameterMap();
        Call<CoordToAddress> call = querys.getCoordToAddress(queryMap);

        call.enqueue(new Callback<CoordToAddress>()
        {
            @Override
            public void onResponse(Call<CoordToAddress> call, Response<CoordToAddress> response)
            {
                CoordToAddress coordToAddressResponse = response.body();

                Message message = handler.obtainMessage();
                message.what = TYPE_COORD_TO_ADDRESS;
                Bundle bundle = new Bundle();

                if (coordToAddressResponse.getCoordToAddressDocuments().isEmpty())
                {
                    bundle.putBoolean("isEmpty", true);
                } else
                {
                    bundle.putBoolean("isEmpty", false);
                }
                bundle.putParcelable("response", coordToAddressResponse);

                message.setData(bundle);
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(Call<CoordToAddress> call, Throwable t)
            {
            }
        });
    }

    public void selectLocation(LocalApiPlaceParameter parameter, int dataType, String fragmentTag)
    {
        // 스크롤할때 추가 데이터를 받아옴, 선택된 위치의 정보를 가져옴
        this.parameter = parameter;
        this.dataType = dataType;
        this.fragmentTag = fragmentTag;

        if (dataType != TYPE_NOT)
        {
            calledDownloadTotalCount = 1;

            switch (dataType)
            {
                case TYPE_ADDRESS:
                    searchAddress(parameter);
                    break;
                case TYPE_PLACE_CATEGORY:
                    searchPlaceCategory(parameter);
                    break;
                case TYPE_PLACE_KEYWORD:
                    searchPlaceKeyWord(parameter);
                    break;
                case TYPE_COORD_TO_ADDRESS:
                    getCoordToAddress(parameter);
                    break;
            }
            return;
        } else
        {
            String categoryName = getCategoryName(parameter.getQuery());

            if (categoryName != null)
            {
                calledDownloadTotalCount = 1;
                parameter.setCategoryGroupCode(categoryName);
                searchPlaceCategory(parameter);
            } else
            {
                // 카테고리 검색
                calledDownloadTotalCount = 2;
                searchAddress(parameter);
                searchPlaceKeyWord(parameter);
            }
        }
    }


    private String getCategoryName(String searchWord)
    {
        KakaoLocalApiCategoryCode.loadCategoryMap();
        String categoryName = KakaoLocalApiCategoryCode.getName(searchWord);

        if (categoryName != null)
        {
            return categoryName;
        } else
        {
            return null;
        }
    }


    public LocalApiPlaceParameter getParameter()
    {
        return parameter;
    }
}
