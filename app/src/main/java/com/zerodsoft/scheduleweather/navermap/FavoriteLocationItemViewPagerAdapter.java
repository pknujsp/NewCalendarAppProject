package com.zerodsoft.scheduleweather.navermap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.event.common.interfaces.ILocationDao;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnClickedBottomSheetListener;
import com.zerodsoft.scheduleweather.navermap.interfaces.PlacesItemBottomSheetButtonOnClickListener;
import com.zerodsoft.scheduleweather.navermap.model.CoordToAddressUtil;
import com.zerodsoft.scheduleweather.navermap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddressDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceKakaoLocalResponse;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;
import com.zerodsoft.scheduleweather.weather.common.ViewProgress;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoriteLocationItemViewPagerAdapter extends LocationItemViewPagerAdapter
{
    private List<FavoriteLocationDTO> favoriteLocationList = new ArrayList<>();
    private final ILocationDao iLocationDao;

    private final Map<Integer, KakaoLocalDocument> kakaoLocalDocumentMap = new HashMap<>();

    public FavoriteLocationItemViewPagerAdapter(Context context, ILocationDao iLocationDao)
    {
        super(context);
        this.iLocationDao = iLocationDao;
    }

    public void setFavoriteLocationList(List<FavoriteLocationDTO> favoriteLocationList)
    {
        this.favoriteLocationList.addAll(favoriteLocationList);
        placeDocumentsList.clear();
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
    public void onBindViewHolder(@NonNull @NotNull PlaceItemInMapViewHolder holder, int position)
    {
        ((FavoriteLocationItemInMapViewHolder) holder).bind(favoriteLocationList.get(position));
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

    class FavoriteLocationItemInMapViewHolder extends PlaceItemInMapViewHolder
    {
        private ViewProgress viewProgress;

        public FavoriteLocationItemInMapViewHolder(@NonNull View view)
        {
            super(view);
            viewProgress = new ViewProgress(binding.placeItemCardviewInBottomsheet, binding.locationItemProgressLayout.progressBar
                    , binding.locationItemProgressLayout.errorTextview);

            viewProgress.onStartedProcessingData();
        }

        public void bind(FavoriteLocationDTO favoriteLocationDTO)
        {
            final int position = getBindingAdapterPosition();

            if (kakaoLocalDocumentMap.containsKey(favoriteLocationDTO.getId()))
            {
                if (favoriteLocationDTO.getType() == FavoriteLocationDTO.ADDRESS)
                {
                    setDataView(placeDocumentsList.get(position));
                } else if (favoriteLocationDTO.getType() == FavoriteLocationDTO.PLACE)
                {
                    setDataView(placeDocumentsList.get(position));
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
                            placeDocumentsList.add(coordToAddressDocuments);
                            setDataView(coordToAddressDocuments);

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
                            placeDocumentsList.add(placeDocuments);

                            setDataView(placeDocuments);
                        }

                        @Override
                        public void onResponseFailed(Exception e)
                        {

                        }
                    });
                }
            }

        }

        @Override
        public void setDataView(KakaoLocalDocument kakaoLocalDocument)
        {
            super.setDataView(kakaoLocalDocument);
            viewProgress.onCompletedProcessingData(true);
        }
    }
}
