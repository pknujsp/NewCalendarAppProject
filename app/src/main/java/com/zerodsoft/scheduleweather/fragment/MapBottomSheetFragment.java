package com.zerodsoft.scheduleweather.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.activity.mapactivity.MapActivity;
import com.zerodsoft.scheduleweather.databinding.MapItemBottomSheetBinding;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placecategoryresponse.PlaceCategoryDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placekeywordresponse.PlaceKeywordDocuments;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;
import com.zerodsoft.scheduleweather.utility.LonLat;
import com.zerodsoft.scheduleweather.utility.LonLatConverter;

import java.util.List;

public class MapBottomSheetFragment extends Fragment implements MapActivity.OnControlItemFragment
{
    public static final String TAG = "MAP_BOTTOM_SHEET_FRAGMENT";

    private MapItemBottomSheetBinding binding;
    private BottomSheetBehavior bottomSheetBehavior;

    private List<AddressResponseDocuments> addressList = null;
    private List<PlaceKeywordDocuments> placeKeywordList = null;
    private List<PlaceCategoryDocuments> placeCategoryList = null;

    private int resultType = KakaoLocalApi.NOT_DOWNLOADED;
    private int selectedItemPosition;
    private int itemPositionMax;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = MapItemBottomSheetBinding.inflate(inflater, container, false);
        bottomSheetBehavior = BottomSheetBehavior.from(binding.getRoot());
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback()
        {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState)
            {
             /*
                STATE_COLLAPSED: 기본적인 상태이며, 일부분의 레이아웃만 보여지고 있는 상태. 이 높이는 behavior_peekHeight속성을 통해 변경 가능
                STATE_DRAGGING: 드래그중인 상태
                STATE_SETTLING: 드래그후 완전히 고정된 상태
                STATE_EXPANDED: 확장된 상태
                STATE_HIDDEN: 기본적으로 비활성화 상태이며, app:behavior_hideable을 사용하는 경우 완전히 숨겨져 있는 상태
             */
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset)
            {

            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        binding.addFavoriteLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });

        binding.shareLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });


        binding.choiceLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Bundle bundle = new Bundle();
                bundle.putInt("type", resultType);
                LonLat lonLat = null;
                double lon, lat;

                switch (resultType)
                {
                    case KakaoLocalApi.TYPE_ADDRESS:
                        lon = addressList.get(selectedItemPosition).getX();
                        lat = addressList.get(selectedItemPosition).getY();
                        lonLat = LonLatConverter.convertLonLat(lon, lat);

                        AddressDTO addressDTO = new AddressDTO();
                        addressDTO.setAddressName(addressList.get(selectedItemPosition).getAddressName());
                        addressDTO.setLongitude(Double.toString(lon));
                        addressDTO.setLatitude(Double.toString(lat));
                        addressDTO.setWeatherX(Integer.toString(lonLat.getX()));
                        addressDTO.setWeatherY(Integer.toString(lonLat.getY()));

                        bundle.putParcelable("addressDTO", addressDTO);
                        break;

                    case KakaoLocalApi.TYPE_PLACE_KEYWORD:
                        lon = placeKeywordList.get(selectedItemPosition).getX();
                        lat = placeKeywordList.get(selectedItemPosition).getY();
                        lonLat = LonLatConverter.convertLonLat(lon, lat);

                        PlaceDTO placeDTOKeyword = new PlaceDTO();
                        placeDTOKeyword.setPlaceId(placeKeywordList.get(selectedItemPosition).getId());
                        placeDTOKeyword.setPlaceName(placeKeywordList.get(selectedItemPosition).getPlaceName());
                        placeDTOKeyword.setLongitude(Double.toString(lon));
                        placeDTOKeyword.setLatitude(Double.toString(lat));
                        placeDTOKeyword.setWeatherX(Integer.toString(lonLat.getX()));
                        placeDTOKeyword.setWeatherY(Integer.toString(lonLat.getY()));

                        bundle.putParcelable("placeDTO", placeDTOKeyword);
                        break;

                    case KakaoLocalApi.TYPE_PLACE_CATEGORY:
                        lon = Double.valueOf(placeCategoryList.get(selectedItemPosition).getX());
                        lat = Double.valueOf(placeCategoryList.get(selectedItemPosition).getY());
                        lonLat = LonLatConverter.convertLonLat(lon, lat);

                        PlaceDTO placeDTOCategory = new PlaceDTO();
                        placeDTOCategory.setPlaceId(placeCategoryList.get(selectedItemPosition).getId());
                        placeDTOCategory.setPlaceName(placeCategoryList.get(selectedItemPosition).getPlaceName());
                        placeDTOCategory.setLongitude(placeCategoryList.get(selectedItemPosition).getX());
                        placeDTOCategory.setLatitude(placeCategoryList.get(selectedItemPosition).getY());
                        placeDTOCategory.setWeatherX(Integer.toString(lonLat.getX()));
                        placeDTOCategory.setWeatherY(Integer.toString(lonLat.getY()));

                        bundle.putParcelable("placeDTO", placeDTOCategory);
                        break;
                }
                ((MapActivity) getActivity()).onChoicedLocation(bundle);
            }
        });


        binding.leftLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (selectedItemPosition == 0)
                {
                    selectedItemPosition = itemPositionMax;
                } else
                {
                    --selectedItemPosition;
                }
                ((MapActivity) getActivity()).onItemSelected(selectedItemPosition);
            }
        });


        binding.rightLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (selectedItemPosition < itemPositionMax)
                {
                    ++selectedItemPosition;
                } else
                {
                    selectedItemPosition = 0;
                }
                ((MapActivity) getActivity()).onItemSelected(selectedItemPosition);
            }
        });

        binding.cancelLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onDetach()
    {
        resultType = Integer.MIN_VALUE;
        super.onDetach();
    }

    private void setLayoutVisibility()
    {
        switch (resultType)
        {
            case KakaoLocalApi.TYPE_ADDRESS:
                binding.itemAddressLayout.setVisibility(View.VISIBLE);
                binding.itemPlaceLayout.setVisibility(View.GONE);
                break;
            case KakaoLocalApi.TYPE_PLACE_CATEGORY:
            case KakaoLocalApi.TYPE_PLACE_KEYWORD:
                // keyword, category
                binding.itemPlaceLayout.setVisibility(View.VISIBLE);
                binding.itemAddressLayout.setVisibility(View.GONE);
                break;
        }

        if (MapActivity.isSelectedLocation)
        {
            binding.choiceLocationButton.setVisibility(View.VISIBLE);
            binding.cancelLocationButton.setVisibility(View.VISIBLE);
        } else
        {
            binding.choiceLocationButton.setVisibility(View.VISIBLE);
            binding.cancelLocationButton.setVisibility(View.GONE);
        }
    }

    private void setViewText()
    {
        switch (resultType)
        {
            case KakaoLocalApi.TYPE_ADDRESS:
                binding.setAddress(addressList.get(selectedItemPosition));
                break;
            case KakaoLocalApi.TYPE_PLACE_KEYWORD:
                binding.setPlaceKeyword(placeKeywordList.get(selectedItemPosition));
                break;
            case KakaoLocalApi.TYPE_PLACE_CATEGORY:
                binding.setPlaceCategory(placeCategoryList.get(selectedItemPosition));
                break;
        }
    }

    @Override
    public void onChangeItem(Bundle bundle)
    {
        resultType = bundle.getInt("type");
        selectedItemPosition = bundle.getInt("position");

        switch (resultType)
        {
            case KakaoLocalApi.TYPE_ADDRESS:
                addressList = bundle.getParcelableArrayList("itemList");
                itemPositionMax = addressList.size() - 1;
                break;
            case KakaoLocalApi.TYPE_PLACE_KEYWORD:
                placeKeywordList = bundle.getParcelableArrayList("itemList");
                itemPositionMax = placeKeywordList.size() - 1;
                break;
            case KakaoLocalApi.TYPE_PLACE_CATEGORY:
                placeCategoryList = bundle.getParcelableArrayList("itemList");
                itemPositionMax = placeCategoryList.size() - 1;
                break;
        }

        setLayoutVisibility();
        setViewText();

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void setBehaviorState(int state)
    {
        bottomSheetBehavior.setState(state);
    }

    @Override
    public boolean isFragmentExpanded()
    {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
        {
            return true;
        } else
        {
            return false;
        }
    }

    @Override
    public void onShowItem(int position)
    {
        selectedItemPosition = position;

        setLayoutVisibility();
        setViewText();

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
}

/*
시트의 하단 툴바가 보이지 않는 버그
- 발생 하는 경우
시트가 접혀있다가 아이템 클릭 후 펼쳐질 때
- 발생 하지 않는 경우
그 외
 */
