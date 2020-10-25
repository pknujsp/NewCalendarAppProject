package com.zerodsoft.scheduleweather.activity.mapactivity.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.zerodsoft.scheduleweather.activity.mapactivity.MapActivity;
import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryCode;
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
    private int dataType = TYPE_NOT;
    private boolean refresh;

    private OnDownloadListener onDownloadListener;

    public MapController(Activity activity)
    {
        onDownloadListener = (OnDownloadListener) activity;
    }


    public interface OnDownloadListener
    {
        void onDownloadedData(int dataType, LocationSearchResult downloadedResult, String fragmentTag, boolean refresh);

        void requestData(int dataType, String fragmentTag, boolean refresh);
    }

    public interface OnChoicedListener
    {
        void onChoicedLocation(Bundle bundle);
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
                locationSearchResult.setResultNum();
                try
                {
                    onDownloadListener.onDownloadedData(dataType, (LocationSearchResult) locationSearchResult.clone(), fragmentTag, refresh);
                } catch (CloneNotSupportedException e)
                {
                    e.printStackTrace();
                }

                refresh = false;
                locationSearchResult = null;
                dataType = TYPE_NOT;
                calledDownloadTotalCount = 0;
                totalCallCount = 0;
            }
        }
    };

    public void searchAddress()
    {
        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.KAKAO);
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("query", MapActivity.parameters.getQuery());
        queryMap.put("page", MapActivity.parameters.getPage());
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

    public void searchPlaceKeyWord()
    {
        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.KAKAO);
        Map<String, String> queryMap = MapActivity.parameters.getParameterMap();
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

    public void searchPlaceCategory()
    {
        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.KAKAO);
        Map<String, String> queryMap = MapActivity.parameters.getParameterMap();
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

    public void getCoordToAddress()
    {
        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.KAKAO);
        Map<String, String> queryMap = MapActivity.parameters.getParameterMap();
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

    public void selectLocation(int dataType, String fragmentTag, boolean refresh)
    {
        // 스크롤할때 추가 데이터를 받아옴, 선택된 위치의 정보를 가져옴
        this.dataType = dataType;
        this.fragmentTag = fragmentTag;
        this.refresh = refresh;

        if (dataType == TYPE_NOT)
        {
            String categoryName = getCategoryName(MapActivity.parameters.getQuery());

            if (categoryName != null)
            {
                calledDownloadTotalCount = 1;
                MapActivity.parameters.setCategoryGroupCode(categoryName);
                searchPlaceCategory();
            } else
            {
                // 카테고리 검색
                calledDownloadTotalCount = 2;
                searchAddress();
                searchPlaceKeyWord();
            }
        } else
        {
            // 선택된 위치의 정보를 가져올 경우
            calledDownloadTotalCount = 1;

            switch (dataType)
            {
                case TYPE_ADDRESS:
                    searchAddress();
                    break;
                case TYPE_PLACE_CATEGORY:
                    searchPlaceCategory();
                    break;
                case TYPE_PLACE_KEYWORD:
                    searchPlaceKeyWord();
                    break;
                case TYPE_COORD_TO_ADDRESS:
                    getCoordToAddress();
                    break;
            }
            return;
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
}
