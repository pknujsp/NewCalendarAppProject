package com.zerodsoft.scheduleweather.event.common;

import android.app.Activity;
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
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.etc.LocationType;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.main.InstanceMainActivity;
import com.zerodsoft.scheduleweather.kakaomap.activity.KakaoMapActivity;
import com.zerodsoft.scheduleweather.kakaomap.bottomsheet.adapter.PlaceItemInMapViewAdapter;
import com.zerodsoft.scheduleweather.kakaomap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import java.util.Collections;

public class ReselectDetailLocationKakao extends KakaoMapActivity
{
    private LocationDTO savedLocationDto;

    private LocationViewModel viewModel;
    private OnBackPressedCallback onBackPressedCallback;
    private int resultCode = Activity.RESULT_CANCELED;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        onBackPressedCallback = new OnBackPressedCallback(true)
        {
            @Override
            public void handleOnBackPressed()
            {
                kakaoMapFragment.binding.mapView.removeAllViews();

                setResult(resultCode);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(onBackPressedCallback);

        viewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        Bundle arguments = getIntent().getExtras();
        savedLocationDto = (LocationDTO) arguments.getParcelable("savedLocationDto");

        showLocationItem();
    }

    private void showLocationItem()
    {
        // 위치가 이미 선택되어 있는 경우 해당 위치 정보를 표시함 (삭제 버튼 추가)
        if (kakaoMapFragment.networkAvailable())
        {
            if (savedLocationDto.getAddressName() != null)
            {
                kakaoMapFragment.setPlaceBottomSheetSelectBtnVisibility(View.GONE);
                kakaoMapFragment.setPlaceBottomSheetUnSelectBtnVisibility(View.VISIBLE);

                // 주소 검색 순서 : 좌표로 주소 변환
                LocalApiPlaceParameter parameter = LocalParameterUtil.getCoordToAddressParameter(savedLocationDto.getLatitude(), savedLocationDto.getLongitude());
                viewModel.getAddressItem(parameter, new CarrierMessagingService.ResultCallback<DataWrapper<AddressResponseDocuments>>()
                {
                    @Override
                    public void onReceiveResult(@NonNull DataWrapper<AddressResponseDocuments> result) throws RemoteException
                    {
                        if (result.getException() == null)
                        {
                            AddressResponseDocuments address = result.getData();
                            kakaoMapFragment.setPlacesListAdapter(new PlaceItemInMapViewAdapter());
                            kakaoMapFragment.createAddressesPoiItems(Collections.singletonList(address));
                            kakaoMapFragment.onPOIItemSelectedByList(0);
                        } else
                        {
                            // exception(error)
                        }
                    }
                });

            } else
            {
                kakaoMapFragment.setPlaceBottomSheetSelectBtnVisibility(View.GONE);
                kakaoMapFragment.setPlaceBottomSheetUnSelectBtnVisibility(View.VISIBLE);

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
                            kakaoMapFragment.setPlacesListAdapter(new PlaceItemInMapViewAdapter());
                            kakaoMapFragment.createPlacesPoiItems(Collections.singletonList(document));
                            kakaoMapFragment.onPOIItemSelectedByList(0);
                        } else
                        {
                            // exception(error)
                        }
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
    protected void onStop()
    {
        super.onStop();
        onBackPressedCallback.remove();
    }

    @Override
    public void onSelectedLocation()
    {
        // 지정이 완료된 경우 - DB에 등록하고 이벤트 액티비티로 넘어가서 날씨 또는 주변 정보 프래그먼트를 실행한다.
        LocationDTO location = kakaoMapFragment.getSelectedLocationDto(savedLocationDto.getCalendarId(), savedLocationDto.getEventId());

        //선택된 위치를 DB에 등록
        viewModel.addLocation(location, new CarrierMessagingService.ResultCallback<Boolean>()
        {
            @Override
            public void onReceiveResult(@NonNull Boolean isAdded) throws RemoteException
            {
                if (isAdded)
                {
                    kakaoMapFragment.binding.mapView.removeAllViews();

                    resultCode = InstanceMainActivity.RESULT_RESELECTED_LOCATION;

                    getIntent().putExtra("selectedLocationName", (location.getLocationType() == LocationType.PLACE ? location.getPlaceName() : location.getAddressName()) + " 지정완료");
                    setResult(resultCode, getIntent());
                    finish();
                } else
                {

                }
            }
        });


    }

    @Override
    public void onRemovedLocation()
    {
        viewModel.removeLocation(savedLocationDto.getCalendarId(), savedLocationDto.getEventId(), new CarrierMessagingService.ResultCallback<Boolean>()
        {
            @Override
            public void onReceiveResult(@NonNull Boolean isRemoved) throws RemoteException
            {
                if (isRemoved)
                {
                    kakaoMapFragment.deselectPoiItem();
                    kakaoMapFragment.removeAllPoiItems();

                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            resultCode = InstanceMainActivity.RESULT_REMOVED_LOCATION;

                            kakaoMapFragment.setPlaceBottomSheetSelectBtnVisibility(View.VISIBLE);
                            kakaoMapFragment.setPlaceBottomSheetUnSelectBtnVisibility(View.GONE);
                            kakaoMapFragment.getPlaceListBottomSheetBehavior().setState(BottomSheetBehavior.STATE_COLLAPSED);
                            Toast.makeText(ReselectDetailLocationKakao.this, getString(R.string.removed_detail_location), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else
                {

                }
            }
        });
    }
}
