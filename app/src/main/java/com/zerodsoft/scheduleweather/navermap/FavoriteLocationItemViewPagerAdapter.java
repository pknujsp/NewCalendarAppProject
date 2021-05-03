package com.zerodsoft.scheduleweather.navermap;

import android.app.Activity;
import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.event.common.interfaces.ILocation;
import com.zerodsoft.scheduleweather.event.common.interfaces.ILocationDao;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnClickedBottomSheetListener;
import com.zerodsoft.scheduleweather.navermap.interfaces.PlacesItemBottomSheetButtonOnClickListener;
import com.zerodsoft.scheduleweather.navermap.model.CoordToAddressUtil;
import com.zerodsoft.scheduleweather.navermap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddressDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceKakaoLocalResponse;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoriteLocationItemViewPagerAdapter extends LocationItemViewPagerAdapter
{
    private List<FavoriteLocationDTO> favoriteLocationList = new ArrayList<>();
    private final ILocationDao iLocationDao;
    private Activity activity;

    private final Map<Integer, KakaoLocalDocument> kakaoLocalDocumentMap = new HashMap<>();

    public FavoriteLocationItemViewPagerAdapter(Activity activity, ILocationDao iLocationDao)
    {
        super();
        this.activity = activity;
        this.iLocationDao = iLocationDao;
    }

    public void setFavoriteLocationList(List<FavoriteLocationDTO> favoriteLocationList)
    {
        this.favoriteLocationList.addAll(favoriteLocationList);
        super.placeDocumentsList.clear();
    }

    @Override
    public void setPlacesItemBottomSheetButtonOnClickListener(PlacesItemBottomSheetButtonOnClickListener placesItemBottomSheetButtonOnClickListener)
    {
        super.setPlacesItemBottomSheetButtonOnClickListener(placesItemBottomSheetButtonOnClickListener);
    }

    @Override
    public void setOnClickedBottomSheetListener(OnClickedBottomSheetListener onClickedBottomSheetListener)
    {
        super.setOnClickedBottomSheetListener(onClickedBottomSheetListener);
    }

    @NonNull
    @Override
    public FavoriteLocationItemInMapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new FavoriteLocationItemInMapViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_places_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceItemInMapViewHolder holder, int position, @NonNull List<Object> payloads)
    {
        ((FavoriteLocationItemInMapViewHolder) holder).bind();
    }

    public List<FavoriteLocationDTO> getFavoriteLocationList()
    {
        return favoriteLocationList;
    }

    @Override
    public int getItemCount()
    {
        return favoriteLocationList.size();
    }

    /*
     private void showLocationItem()
    {
        // 위치가 이미 선택되어 있는 경우 해당 위치 정보를 표시함 (삭제 버튼 추가)
        if (naverMapFragment.networkAvailable())
        {
            if (savedLocationDto.getAddressName() != null)
            {
                naverMapFragment.setPlaceBottomSheetSelectBtnVisibility(View.INVISIBLE);
                naverMapFragment.setPlaceBottomSheetUnSelectBtnVisibility(View.VISIBLE);

                // 주소 검색 순서 : 좌표로 주소 변환
                LocalApiPlaceParameter parameter = LocalParameterUtil.getCoordToAddressParameter(savedLocationDto.getLatitude(), savedLocationDto.getLongitude());
                CoordToAddressUtil.coordToAddress(parameter, new CarrierMessagingService.ResultCallback<DataWrapper<CoordToAddress>>()
                {
                    @Override
                    public void onReceiveResult(@NonNull DataWrapper<CoordToAddress> coordToAddressDataWrapper) throws RemoteException
                    {
                        if (coordToAddressDataWrapper.getException() == null)
                        {
                            CoordToAddress coordToAddress = coordToAddressDataWrapper.getData();
                            CoordToAddressDocuments coordToAddressDocuments = coordToAddress.getCoordToAddressDocuments().get(0);
                            coordToAddressDocuments.getCoordToAddressAddress().setLatitude(String.valueOf(savedLocationDto.getLatitude()));
                            coordToAddressDocuments.getCoordToAddressAddress().setLongitude(String.valueOf(savedLocationDto.getLongitude()));

                            naverMapFragment.setLocationItemViewPagerAdapter(new LocationItemViewPagerAdapter(), PoiItemType.SELECTED_ADDRESS_IN_EVENT);
                            naverMapFragment.createPoiItems(Collections.singletonList(coordToAddressDocuments), PoiItemType.SELECTED_ADDRESS_IN_EVENT);
                            naverMapFragment.onPOIItemSelectedByList(0, PoiItemType.SELECTED_ADDRESS_IN_EVENT);
                        } else
                        {
                            // exception(error)
                        }
                    }
                });

            } else
            {
                naverMapFragment.setPlaceBottomSheetSelectBtnVisibility(View.INVISIBLE);
                naverMapFragment.setPlaceBottomSheetUnSelectBtnVisibility(View.VISIBLE);

                // 장소 검색 순서 : 장소의 위경도 내 10M 반경에서 장소 이름 검색(여러개 나올 경우 장소ID와 일치하는 장소를 선택)
                LocalApiPlaceParameter parameter = LocalParameterUtil.getPlaceParameter(savedLocationDto.getPlaceName(),
                        String.valueOf(savedLocationDto.getLatitude()), String.valueOf(savedLocationDto.getLongitude()), LocalApiPlaceParameter.DEFAULT_SIZE,
                        LocalApiPlaceParameter.DEFAULT_PAGE, LocalApiPlaceParameter.SEARCH_CRITERIA_SORT_TYPE_ACCURACY);
                parameter.setRadius("100");

                viewModel.getPlaceItem(parameter, savedLocationDto.getPlaceId(), new CarrierMessagingService.ResultCallback<DataWrapper<PlaceDocuments>>()
                {
                    @Override
                    public void onReceiveResult(@NonNull DataWrapper<PlaceDocuments> result) throws RemoteException
                    {
                        if (result.getException() == null)
                        {
                            PlaceDocuments document = result.getData();
                            naverMapFragment.setLocationItemViewPagerAdapter(new LocationItemViewPagerAdapter(), PoiItemType.SELECTED_PLACE_IN_EVENT);
                            naverMapFragment.createPoiItems(Collections.singletonList(document), PoiItemType.SELECTED_PLACE_IN_EVENT);
                            naverMapFragment.onPOIItemSelectedByList(0, PoiItemType.SELECTED_PLACE_IN_EVENT);
                        } else
                        {
                            // exception(error)
                        }
                    }
                });
            }
        }

    }
     */

    class FavoriteLocationItemInMapViewHolder extends PlaceItemInMapViewHolder
    {
        public FavoriteLocationItemInMapViewHolder(@NonNull View view)
        {
            super(view);
        }

        public void bind()
        {
            final FavoriteLocationDTO favoriteLocationDTO = favoriteLocationList.get(getBindingAdapterPosition());
            if (kakaoLocalDocumentMap.containsKey(favoriteLocationDTO.getId()))
            {
                if (favoriteLocationDTO.getType() == FavoriteLocationDTO.ADDRESS)
                {
                    FavoriteLocationItemInMapViewHolder.super.bind(kakaoLocalDocumentMap.get(favoriteLocationDTO.getId()));
                } else if (favoriteLocationDTO.getType() == FavoriteLocationDTO.PLACE)
                {
                    FavoriteLocationItemInMapViewHolder.super.bind(kakaoLocalDocumentMap.get(favoriteLocationDTO.getId()));
                }
            } else
            {
                if (favoriteLocationDTO.getType() == FavoriteLocationDTO.ADDRESS)
                {
                    // 주소 검색 순서 : 좌표로 주소 변환
                    LocalApiPlaceParameter parameter = LocalParameterUtil.getCoordToAddressParameter
                            (Double.parseDouble(favoriteLocationDTO.getLatitude()), Double.parseDouble(favoriteLocationDTO.getLongitude()));
                    CoordToAddressUtil.coordToAddress(parameter, new JsonDownloader<CoordToAddress>()
                    {
                        @Override
                        public void onResponseSuccessful(CoordToAddress result)
                        {
                            CoordToAddressDocuments coordToAddressDocuments = result.getCoordToAddressDocuments().get(0);
                            coordToAddressDocuments.getCoordToAddressAddress().setLatitude(favoriteLocationDTO.getLatitude());
                            coordToAddressDocuments.getCoordToAddressAddress().setLongitude(favoriteLocationDTO.getLongitude());

                            kakaoLocalDocumentMap.put(favoriteLocationDTO.getId(), coordToAddressDocuments);
                            activity.runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    FavoriteLocationItemInMapViewHolder.super.bind(coordToAddressDocuments);
                                }
                            });
                        }

                        @Override
                        public void onResponseFailed(Exception e)
                        {

                        }
                    });

                } else if (favoriteLocationDTO.getType() == FavoriteLocationDTO.PLACE)
                {
                    // 장소 검색 순서 : 장소의 위경도 내 10M 반경에서 장소 이름 검색(여러개 나올 경우 장소ID와 일치하는 장소를 선택)
                    LocalApiPlaceParameter parameter = LocalParameterUtil.getPlaceParameter(favoriteLocationDTO.getPlaceName(),
                            String.valueOf(favoriteLocationDTO.getLatitude()), String.valueOf(favoriteLocationDTO.getLongitude()), LocalApiPlaceParameter.DEFAULT_SIZE,
                            LocalApiPlaceParameter.DEFAULT_PAGE, LocalApiPlaceParameter.SEARCH_CRITERIA_SORT_TYPE_ACCURACY);
                    parameter.setRadius("100");

                    iLocationDao.getPlaceItem(parameter, favoriteLocationDTO.getPlaceId(), new JsonDownloader<PlaceKakaoLocalResponse>()
                    {
                        @Override
                        public void onResponseSuccessful(PlaceKakaoLocalResponse result)
                        {
                            PlaceDocuments placeDocuments = result.getPlaceDocuments().get(0);
                            kakaoLocalDocumentMap.put(favoriteLocationDTO.getId(), placeDocuments);
                            activity.runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    FavoriteLocationItemInMapViewHolder.super.bind(placeDocuments);
                                }
                            });
                        }

                        @Override
                        public void onResponseFailed(Exception e)
                        {

                        }
                    });
                }
            }

        }
    }
}
