package com.zerodsoft.scheduleweather.event.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.etc.LocationType;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.main.InstanceMainActivity;
import com.zerodsoft.scheduleweather.navermap.BottomSheetType;
import com.zerodsoft.scheduleweather.navermap.LocationItemViewPagerAdapter;
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
    private LocationDTO selectedLocationDTOInEvent;
    private LocationViewModel viewModel;
    private OnBackPressedCallback onBackPressedCallback;
    private int resultCode = Activity.RESULT_CANCELED;

    private LocationDTO selectedLocationDTOInMap;
    private Marker selectedLocationMarker = new Marker();

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
        selectedLocationDTOInEvent = (LocationDTO) arguments.getParcelable("selectedLocationDTO");
        arguments.remove("selectedLocationDTO");

        if (selectedLocationDTOInEvent == null)
        {
            naverMapFragment.setPlaceBottomSheetSelectBtnVisibility(View.VISIBLE);
            naverMapFragment.setPlaceBottomSheetUnSelectBtnVisibility(View.GONE);
        }

        naverMapFragment.mapFragment.getMapAsync(this::onMapReady);
    }


    private void showLocationItem()
    {
        // 위치가 이미 선택되어 있는 경우 해당 위치 정보를 표시함 (삭제 버튼 추가)
        if (naverMapFragment.networkAvailable())
        {
            if (selectedLocationDTOInEvent.getLocationType() == LocationType.ADDRESS)
            {
                naverMapFragment.setPlaceBottomSheetSelectBtnVisibility(View.GONE);
                naverMapFragment.setPlaceBottomSheetUnSelectBtnVisibility(View.VISIBLE);

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
                naverMapFragment.setPlaceBottomSheetSelectBtnVisibility(View.GONE);
                naverMapFragment.setPlaceBottomSheetUnSelectBtnVisibility(View.VISIBLE);

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
                            saveLocation(kakaoLocalDocument);
                        }
                    }).create().show();
        } else
        {
            saveLocation(kakaoLocalDocument);
        }


    }

    private void saveLocation(KakaoLocalDocument kakaoLocalDocument)
    {
        LocationDTO location = naverMapFragment.getSelectedLocationDto();
        selectedLocationDTOInMap = location;
        Bundle bundle = new Bundle();
        bundle.putParcelable("selectedLocationDTO", location);
        getIntent().putExtras(bundle);

        naverMapFragment.getBottomSheetBehavior(BottomSheetType.LOCATION_ITEM).setState(BottomSheetBehavior.STATE_COLLAPSED);

        resultCode = selectedLocationDTOInEvent == null ? InstanceMainActivity.RESULT_SELECTED_LOCATION : InstanceMainActivity.RESULT_RESELECTED_LOCATION;

        String locationName = location.getLocationType() == LocationType.PLACE ? location.getPlaceName() : location.getAddressName();
        Toast.makeText(this, locationName + " - " + getString(R.string.selected_location), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRemovedLocation()
    {
        naverMapFragment.deselectPoiItem();
        naverMapFragment.removeAllPoiItems();

        naverMapFragment.getBottomSheetBehavior(BottomSheetType.LOCATION_ITEM).setState(BottomSheetBehavior.STATE_COLLAPSED);

        resultCode = InstanceMainActivity.RESULT_REMOVED_LOCATION;

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
