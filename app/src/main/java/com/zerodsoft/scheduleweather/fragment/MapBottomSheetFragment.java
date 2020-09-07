package com.zerodsoft.scheduleweather.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.MapController;
import com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.MapFragment;
import com.zerodsoft.scheduleweather.activity.mapactivity.MapActivity;
import com.zerodsoft.scheduleweather.databinding.MapItemBottomSheetBinding;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.LocationSearchResult;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;
import com.zerodsoft.scheduleweather.utility.LonLat;
import com.zerodsoft.scheduleweather.utility.LonLatConverter;

public class MapBottomSheetFragment extends Fragment implements MapFragment.OnControlItemFragment
{
    public static final String TAG = "MAP_BOTTOM_SHEET_FRAGMENT";

    private MapItemBottomSheetBinding binding;
    private BottomSheetBehavior bottomSheetBehavior;

    private LocationSearchResult locationSearchResult;
    private int dataType = MapController.TYPE_NOT;
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
                bundle.putInt("dataType", dataType);
                LonLat lonLat = null;
                double lon, lat;

                switch (dataType)
                {
                    case MapController.TYPE_ADDRESS:
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

                    case MapController.TYPE_PLACE_KEYWORD:
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

                    case MapController.TYPE_PLACE_CATEGORY:
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
        dataType = MapController.TYPE_NOT;
        super.onDetach();
    }

    private void setLayoutVisibility()
    {
        switch (dataType)
        {
            case MapController.TYPE_ADDRESS:
                binding.itemAddressLayout.setVisibility(View.VISIBLE);
                binding.itemPlaceLayout.setVisibility(View.GONE);
                break;
            case MapController.TYPE_PLACE_CATEGORY:
            case MapController.TYPE_PLACE_KEYWORD:
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

    private void setViewData()
    {
        switch (dataType)
        {
            case MapController.TYPE_ADDRESS:
                binding.selectedAddressNameTextview.setText(locationSearchResult.getAddressResponse().getAddressResponseDocumentsList().get(selectedItemPosition).getAddressName());
                if (locationSearchResult.getAddressResponse().getAddressResponseDocumentsList().get(selectedItemPosition).getAddressTypeStr().equals("도로명"))
                {
                    binding.selectedAnotherAddressTextview.setText(locationSearchResult.getAddressResponse().getAddressResponseDocumentsList().get(selectedItemPosition).getAddressResponseRoadAddress().getAddressName());
                } else
                {
                    // 지번
                    binding.selectedAnotherAddressTextview.setText(locationSearchResult.getAddressResponse().getAddressResponseDocumentsList().get(selectedItemPosition).getAddressResponseAddress().getAddressName());
                }
                binding.selectedAnotherAddressTypeTextview.setText(locationSearchResult.getAddressResponse().getAddressResponseDocumentsList().get(selectedItemPosition).getAddressTypeStr());
                break;

            case MapController.TYPE_PLACE_KEYWORD:
                binding.selectedPlaceAddressTextview.setText(locationSearchResult.getPlaceKeywordResponse().getPlaceKeywordDocuments().get(selectedItemPosition).getAddressName());
                binding.selectedPlaceCategoryTextview.setText(locationSearchResult.getPlaceKeywordResponse().getPlaceKeywordDocuments().get(selectedItemPosition).getCategoryName());
                binding.selectedPlaceDescriptionTextview.setText(locationSearchResult.getPlaceKeywordResponse().getPlaceKeywordDocuments().get(selectedItemPosition).getCategoryGroupName());
                binding.selectedPlaceNameTextview.setText(locationSearchResult.getPlaceKeywordResponse().getPlaceKeywordDocuments().get(selectedItemPosition).getPlaceName());
                break;

            case MapController.TYPE_PLACE_CATEGORY:
                binding.selectedPlaceAddressTextview.setText(locationSearchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments().get(selectedItemPosition).getAddressName());
                binding.selectedPlaceCategoryTextview.setText(locationSearchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments().get(selectedItemPosition).getCategoryName());
                binding.selectedPlaceDescriptionTextview.setText(locationSearchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments().get(selectedItemPosition).getCategoryGroupName());
                binding.selectedPlaceNameTextview.setText(locationSearchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments().get(selectedItemPosition).getPlaceName());
                break;

            case MapController.TYPE_COORD_TO_ADDRESS:
                if (locationSearchResult.getCoordToAddressResponse().getCoordToAddressDocuments().get(selectedItemPosition).getCoordToAddressAddress() != null)
                {
                    // 지번
                    binding.selectedAddressNameTextview.setText(locationSearchResult.getCoordToAddressResponse().getCoordToAddressDocuments().get(selectedItemPosition).getCoordToAddressAddress().getAddressName());
                    binding.selectedAnotherAddressTextview.setText(locationSearchResult.getCoordToAddressResponse().getCoordToAddressDocuments().get(selectedItemPosition).getCoordToAddressAddress().getMainAddressNo());
                    binding.selectedAnotherAddressTypeTextview.setText(locationSearchResult.getCoordToAddressResponse().getCoordToAddressDocuments().get(selectedItemPosition).getCoordToAddressAddress().getRegion1DepthName());
                } else
                {
                    // 도로명
                    binding.selectedAddressNameTextview.setText(locationSearchResult.getCoordToAddressResponse().getCoordToAddressDocuments().get(selectedItemPosition).getCoordToAddressRoadAddress().getAddressName());
                    binding.selectedAnotherAddressTextview.setText(locationSearchResult.getCoordToAddressResponse().getCoordToAddressDocuments().get(selectedItemPosition).getCoordToAddressRoadAddress().getRoadName());
                    binding.selectedAnotherAddressTypeTextview.setText(locationSearchResult.getCoordToAddressResponse().getCoordToAddressDocuments().get(selectedItemPosition).getCoordToAddressRoadAddress().getRegion1DepthName());
                }
                break;
        }
    }

    public void onChangeItems(Bundle bundle)
    {
        dataType = bundle.getInt("dataType");
        selectedItemPosition = bundle.getInt("position");
        locationSearchResult = bundle.getParcelable("locationSearchResult");

        switch (dataType)
        {
            case MapController.TYPE_ADDRESS:
                itemPositionMax = locationSearchResult.getAddressResponse().getAddressResponseDocumentsList().size() - 1;
                break;
            case MapController.TYPE_PLACE_KEYWORD:
                itemPositionMax = locationSearchResult.getPlaceKeywordResponse().getPlaceKeywordDocuments().size() - 1;
                break;
            case MapController.TYPE_PLACE_CATEGORY:
                itemPositionMax = locationSearchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments().size() - 1;
                break;
            case MapController.TYPE_COORD_TO_ADDRESS:
                itemPositionMax = locationSearchResult.getCoordToAddressResponse().getCoordToAddressDocuments().size() - 1;
                break;
        }
        setLayoutVisibility();
        setViewData();

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
        setViewData();

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
}
