package com.zerodsoft.scheduleweather.event.common;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.etc.LocationType;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.navermap.BottomSheetType;
import com.zerodsoft.scheduleweather.navermap.LocationItemViewPagerAdapter;
import com.zerodsoft.scheduleweather.navermap.fragment.searchheader.MapHeaderSearchFragment;
import com.zerodsoft.scheduleweather.navermap.model.CoordToAddressUtil;
import com.zerodsoft.scheduleweather.navermap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.navermap.NaverMapActivity;
import com.zerodsoft.scheduleweather.navermap.MarkerType;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddressDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceKakaoLocalResponse;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import java.util.Collections;

public class SelectionDetailLocationNaver extends NaverMapActivity implements OnMapReadyCallback
{
    public static final int REQUEST_CODE_SELECT_LOCATION_EMPTY_QUERY = 1000;
    public static final int REQUEST_CODE_SELECT_LOCATION_BY_QUERY = 2000;
    public static final int REQUEST_CODE_CHANGE_LOCATION = 3000;

    public static final int RESULT_CODE_CHANGED_LOCATION = 10000;
    public static final int RESULT_CODE_REMOVED_LOCATION = 20000;
    public static final int RESULT_CODE_SELECTED_LOCATION = 30000;

    private LocationDTO selectedLocationDTOInEvent;
    private LocationViewModel viewModel;
    private OnBackPressedCallback onBackPressedCallback;
    private int resultCode = Activity.RESULT_CANCELED;
    private int requestCode;

    private LocationDTO selectedLocationDTOInMap;
    private String locationNameInEvent;
    private final Marker selectedLocationMarker = new Marker();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        onBackPressedCallback = new OnBackPressedCallback(true)
        {
            @Override
            public void handleOnBackPressed()
            {
                setResult(resultCode, getIntent());
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(onBackPressedCallback);

        viewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        Bundle arguments = getIntent().getExtras();
        selectedLocationDTOInEvent = (LocationDTO) arguments.getParcelable(LocationSelectorKey.SELECTED_LOCATION_DTO_IN_EVENT.name());
        locationNameInEvent = arguments.getString(LocationSelectorKey.LOCATION_NAME_IN_EVENT.name());
        requestCode = arguments.getInt(LocationSelectorKey.REQUEST_CODE.name());

        arguments.remove(LocationSelectorKey.SELECTED_LOCATION_DTO_IN_EVENT.name());

        selectedLocationMarker.setCaptionColor(Color.BLUE);
        selectedLocationMarker.setCaptionTextSize(14f);

        switch (requestCode)
        {
            case REQUEST_CODE_SELECT_LOCATION_EMPTY_QUERY:
            {
                // 아무것도 하지 않음
                naverMapFragment.setPlaceBottomSheetSelectBtnVisibility(View.VISIBLE);
                naverMapFragment.setPlaceBottomSheetUnSelectBtnVisibility(View.GONE);
                break;
            }
            case REQUEST_CODE_SELECT_LOCATION_BY_QUERY:
            {
                naverMapFragment.setPlaceBottomSheetSelectBtnVisibility(View.VISIBLE);
                naverMapFragment.setPlaceBottomSheetUnSelectBtnVisibility(View.GONE);
                binding.naverMapActivityRootLayout.getViewTreeObserver().addOnGlobalLayoutListener(acitivityRootOnGlobalLayoutListener);
                break;
            }
            case REQUEST_CODE_CHANGE_LOCATION:
            {
                naverMapFragment.setPlaceBottomSheetSelectBtnVisibility(View.GONE);
                naverMapFragment.setPlaceBottomSheetUnSelectBtnVisibility(View.VISIBLE);
                naverMapFragment.mapFragment.getMapAsync(this::onMapReady);
                break;
            }
        }
    }


    private ViewTreeObserver.OnGlobalLayoutListener acitivityRootOnGlobalLayoutListener =
            new ViewTreeObserver.OnGlobalLayoutListener()
            {
                @Override
                public void onGlobalLayout()
                {
                    naverMapFragment.binding.locationSearchBottomSheet.searchFragmentContainer.getViewTreeObserver()
                            .addOnGlobalLayoutListener(searchBottomSheetFragmentOnGlobalLayoutListener);

                    naverMapFragment.binding.naverMapHeaderBar.getRoot().performClick();

                    binding.naverMapActivityRootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(acitivityRootOnGlobalLayoutListener);
                }
            };

    private ViewTreeObserver.OnGlobalLayoutListener searchBottomSheetFragmentOnGlobalLayoutListener =
            new ViewTreeObserver.OnGlobalLayoutListener()
            {
                @Override
                public void onGlobalLayout()
                {
                    ((MapHeaderSearchFragment) naverMapFragment.getChildFragmentManager().findFragmentByTag(MapHeaderSearchFragment.TAG)).setQuery(locationNameInEvent, true);
                    naverMapFragment.binding.locationSearchBottomSheet.searchFragmentContainer.getViewTreeObserver().removeOnGlobalLayoutListener(searchBottomSheetFragmentOnGlobalLayoutListener);
                }
            };


    private void showLocationItem()
    {
        // 위치가 이미 선택되어 있는 경우 해당 위치 정보를 표시함 (삭제 버튼 추가)
        if (naverMapFragment.networkAvailable())
        {
            if (selectedLocationDTOInEvent.getLocationType() == LocationType.ADDRESS)
            {
                // 주소 검색 순서 : 좌표로 주소 변환
                LocalApiPlaceParameter parameter = LocalParameterUtil.getCoordToAddressParameter(selectedLocationDTOInEvent.getLatitude(), selectedLocationDTOInEvent.getLongitude());
                CoordToAddressUtil.coordToAddress(parameter, new CarrierMessagingService.ResultCallback<DataWrapper<CoordToAddress>>()
                {
                    @Override
                    public void onReceiveResult(@NonNull DataWrapper<CoordToAddress> coordToAddressDataWrapper) throws RemoteException
                    {
                        if (coordToAddressDataWrapper.getException() == null)
                        {
                            CoordToAddress coordToAddress = coordToAddressDataWrapper.getData();
                            CoordToAddressDocuments coordToAddressDocuments = coordToAddress.getCoordToAddressDocuments().get(0);
                            coordToAddressDocuments.getCoordToAddressAddress().setLatitude(String.valueOf(selectedLocationDTOInEvent.getLatitude()));
                            coordToAddressDocuments.getCoordToAddressAddress().setLongitude(String.valueOf(selectedLocationDTOInEvent.getLongitude()));

                            naverMapFragment.setLocationItemViewPagerAdapter(new LocationItemViewPagerAdapter(getApplicationContext()), MarkerType.SELECTED_ADDRESS_IN_EVENT);
                            naverMapFragment.createPoiItems(Collections.singletonList(coordToAddressDocuments), MarkerType.SELECTED_ADDRESS_IN_EVENT);
                            naverMapFragment.onPOIItemSelectedByList(0, MarkerType.SELECTED_ADDRESS_IN_EVENT);
                        } else
                        {
                            // exception(error)
                        }
                    }
                });

            } else if (selectedLocationDTOInEvent.getLocationType() == LocationType.PLACE)
            {
                // 장소 검색 순서 : 장소의 위경도 내 10M 반경에서 장소 이름 검색(여러개 나올 경우 장소ID와 일치하는 장소를 선택)
                LocalApiPlaceParameter parameter = LocalParameterUtil.getPlaceParameter(selectedLocationDTOInEvent.getPlaceName(),
                        String.valueOf(selectedLocationDTOInEvent.getLatitude()), String.valueOf(selectedLocationDTOInEvent.getLongitude()), LocalApiPlaceParameter.DEFAULT_SIZE,
                        LocalApiPlaceParameter.DEFAULT_PAGE, LocalApiPlaceParameter.SEARCH_CRITERIA_SORT_TYPE_ACCURACY);
                parameter.setRadius("100");

                viewModel.getPlaceItem(parameter, selectedLocationDTOInEvent.getPlaceId(), new JsonDownloader<PlaceKakaoLocalResponse>()
                {
                    @Override
                    public void onResponseSuccessful(PlaceKakaoLocalResponse result)
                    {
                        PlaceDocuments document = result.getPlaceDocuments().get(0);
                        naverMapFragment.setLocationItemViewPagerAdapter(new LocationItemViewPagerAdapter(getApplicationContext()), MarkerType.SELECTED_PLACE_IN_EVENT);
                        naverMapFragment.createPoiItems(Collections.singletonList(document), MarkerType.SELECTED_PLACE_IN_EVENT);
                        naverMapFragment.onPOIItemSelectedByList(0, MarkerType.SELECTED_PLACE_IN_EVENT);
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
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        onBackPressedCallback.remove();
    }

    @Override
    public void onSelectedLocation(KakaoLocalDocument kakaoLocalDocument)
    {
        if (selectedLocationDTOInMap != null)
        {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.request_select_location_title)
                    .setMessage(R.string.message_existing_selected_location)
                    .setCancelable(false)
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            dialogInterface.dismiss();
                        }
                    })
                    .setPositiveButton(R.string.check, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            saveLocation();
                        }
                    }).create().show();
        } else
        {
            saveLocation();
        }
    }

    private void saveLocation()
    {
        LocationDTO location = naverMapFragment.getSelectedLocationDto();
        selectedLocationDTOInMap = location;
        Bundle bundle = new Bundle();
        bundle.putParcelable(LocationSelectorKey.SELECTED_LOCATION_DTO_IN_MAP.name(), location);
        getIntent().putExtras(bundle);

        removeMarker();
        createMarker();
        naverMapFragment.getBottomSheetBehavior(BottomSheetType.LOCATION_ITEM).setState(BottomSheetBehavior.STATE_COLLAPSED);

        resultCode = (requestCode == REQUEST_CODE_CHANGE_LOCATION)
                ? RESULT_CODE_CHANGED_LOCATION : RESULT_CODE_SELECTED_LOCATION;

        String locationName = location.getLocationType() == LocationType.PLACE ? location.getPlaceName() : location.getAddressName();
        Toast.makeText(this, locationName + " - " + getString(R.string.selected_location), Toast.LENGTH_SHORT).show();
    }

    private void createMarker()
    {
        selectedLocationMarker.setPosition(new LatLng(selectedLocationDTOInMap.getLatitude(), selectedLocationDTOInMap.getLongitude()));
        selectedLocationMarker.setCaptionText(selectedLocationDTOInMap.getLocationType() == LocationType.PLACE ? selectedLocationDTOInMap.getPlaceName() : selectedLocationDTOInMap.getAddressName());
        selectedLocationMarker.setMap(naverMapFragment.naverMap);
    }

    private void removeMarker()
    {
        if (selectedLocationMarker.getMap() != null)
        {
            selectedLocationMarker.setMap(null);
        }
    }

    @Override
    public void onRemovedLocation()
    {
        naverMapFragment.deselectPoiItem();
        naverMapFragment.removeAllPoiItems();
        removeMarker();

        naverMapFragment.getBottomSheetBehavior(BottomSheetType.LOCATION_ITEM).setState(BottomSheetBehavior.STATE_COLLAPSED);

        resultCode = RESULT_CODE_REMOVED_LOCATION;

        Toast.makeText(getApplicationContext(), R.string.canceled_location, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap)
    {
        if (selectedLocationDTOInEvent != null)
        {
            showLocationItem();
        }
    }
}
