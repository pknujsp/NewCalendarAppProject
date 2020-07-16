package com.zerodsoft.scheduleweather.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.Retrofit.DownloadData;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressResponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceCategoryResponse.PlaceCategoryDocuments;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceKeywordResponse.PlaceKeywordDocuments;
import com.zerodsoft.scheduleweather.Room.DTO.LocationDTO;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.List;

public class AddLocationActivity extends AppCompatActivity
{
    private TextView addressTextView;
    private ImageButton checkButton;
    private ImageButton zoomInButton;
    private ImageButton zoomOutButton;
    private ImageButton gpsButton;

    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout bottomSheet;

    private TextView selectedItemPlaceNameTextView;
    private TextView selectedItemPlaceCategoryTextView;
    private TextView selectedItemPlaceAddressTextView;
    private TextView selectedItemPlaceDescriptionTextView;

    private TextView selectedItemAddressNameTextView;
    private TextView selectedItemAnotherAddressNameTextView;
    private TextView selectedItemAnotherAddressTypeTextView;

    private ImageButton selectedItemFavoriteButton;
    private ImageButton selectedItemShareButton;
    private ImageButton selectedItemCheckButton;
    private ImageButton selectedItemLeftButton;
    private ImageButton selectedItemRightButton;

    private LinearLayout placeItemLayout;
    private LinearLayout addressItemLayout;

    private List<AddressResponseDocuments> addressList = null;
    private List<PlaceKeywordDocuments> placeKeywordList = null;
    private List<PlaceCategoryDocuments> placeCategoryList = null;
    private long downloadedTime;
    private int resultType;
    private int selectedItemPosition;

    private LocationDTO locationDTO;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        bottomSheet = (LinearLayout) findViewById(R.id.map_item_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        setBottomSheetCallback();
        bottomSheet.setVisibility(View.GONE);

        addressTextView = (TextView) findViewById(R.id.address_textview);
        checkButton = (ImageButton) findViewById(R.id.add_location_check_button);
        zoomInButton = (ImageButton) findViewById(R.id.zoom_in_button);
        zoomOutButton = (ImageButton) findViewById(R.id.zoom_out_button);
        gpsButton = (ImageButton) findViewById(R.id.gps_button);

        placeItemLayout = (LinearLayout) findViewById(R.id.item_place_layout);
        addressItemLayout = (LinearLayout) findViewById(R.id.item_address_layout);

        selectedItemPlaceNameTextView = (TextView) findViewById(R.id.selected_place_name_textview);
        selectedItemPlaceCategoryTextView = (TextView) findViewById(R.id.selected_place_category_textview);
        selectedItemPlaceAddressTextView = (TextView) findViewById(R.id.selected_place_address_textview);
        selectedItemPlaceDescriptionTextView = (TextView) findViewById(R.id.selected_place_description_textview);

        selectedItemFavoriteButton = (ImageButton) findViewById(R.id.add_favorite_address_button);
        selectedItemShareButton = (ImageButton) findViewById(R.id.share_address_button);
        selectedItemCheckButton = (ImageButton) findViewById(R.id.check_address_button);
        selectedItemLeftButton = (ImageButton) findViewById(R.id.left_address_button);
        selectedItemRightButton = (ImageButton) findViewById(R.id.right_address_button);

        selectedItemAddressNameTextView = (TextView) findViewById(R.id.selected_address_name_textview);
        selectedItemAnotherAddressNameTextView = (TextView) findViewById(R.id.selected_another_address_textview);
        selectedItemAnotherAddressTypeTextView = (TextView) findViewById(R.id.selected_another_address_type_textview);

        mapView = new MapView(this);
        if (!MapView.isMapTilePersistentCacheEnabled())
        {
            MapView.setMapTilePersistentCacheEnabled(true);
        }
        ConstraintLayout mapViewContainer = (ConstraintLayout) findViewById(R.id.add_location_map_view);
        mapViewContainer.addView(mapView);

        availableIntent();

        mapView.setCurrentLocationEventListener(new MapView.CurrentLocationEventListener()
        {
            @Override
            public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v)
            {
                // 단말의 현위치 좌표값을 통보받을 수 있다.
                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(mapPoint.getMapPointGeoCoord().latitude, mapPoint.getMapPointGeoCoord().longitude), true);

                // 5초후 현위치를 잡으면 트랙킹 모드 종료
                //mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
            }

            @Override
            public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v)
            {

            }

            @Override
            public void onCurrentLocationUpdateFailed(MapView mapView)
            {
                // 현위치 갱신 작업에 실패한 경우 호출된다.
            }

            @Override
            public void onCurrentLocationUpdateCancelled(MapView mapView)
            {
                // 현위치 트랙킹 기능이 사용자에 의해 취소된 경우 호출된다.
            }
        });

        checkButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                setResult(RESULT_OK, getIntent());
                finish();
            }
        });

        addressTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(AddLocationActivity.this, SearchAddressActivity.class);
                startActivity(intent);
            }
        });

        zoomInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // 줌 인
                mapView.zoomIn(true);

            }
        });

        zoomOutButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // 줌 인
                mapView.zoomOut(true);
            }
        });

        gpsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
                gpsButton.setClickable(false);
                TimeOutThread timeOutThread = new TimeOutThread();
                timeOutThread.start();
            }
        });
    }

    private boolean availableIntent()
    {
        if (getIntent().getExtras() != null)
        {
            Bundle bundle = getIntent().getExtras();
            resultType = bundle.getInt("type");
            selectedItemPosition = bundle.getInt("position");
            downloadedTime = bundle.getLong("downloadedTime");

            if (resultType == DownloadData.ADDRESS)
            {
                addressList = bundle.getParcelableArrayList("itemsInfo");
            } else if (resultType == DownloadData.PLACE_KEYWORD || resultType == DownloadData.PLACE_CATEGORY)
            {
                placeKeywordList = bundle.getParcelableArrayList("itemsInfo");
            }
            displayItemInfo(selectedItemPosition);
            return true;
        } else
        {
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633), true);
            return false;
        }
    }

    private void displayItemInfo(int position)
    {
        inflateSelectItemLayout(View.VISIBLE);
        double latitude = 0, longitude = 0;

        if (resultType == DownloadData.ADDRESS)
        {
            longitude = addressList.get(position).getX();
            latitude = addressList.get(position).getY();

            displayAddressInfo(position);
        } else if (resultType == DownloadData.PLACE_KEYWORD)
        {
            longitude = placeKeywordList.get(position).getX();
            latitude = placeKeywordList.get(position).getY();

            displayPlaceInfo(position);
        } else if (resultType == DownloadData.PLACE_CATEGORY)
        {
            longitude = Double.valueOf(placeCategoryList.get(position).getX());
            latitude = Double.valueOf(placeCategoryList.get(position).getY());

            displayPlaceInfo(position);
        }

        setCenterPoint(latitude, longitude, position);
    }

    private void displayPlaceInfo(int position)
    {
        switch (resultType)
        {
            case DownloadData.PLACE_KEYWORD:
                selectedItemPlaceNameTextView.setText(placeKeywordList.get(position).getPlaceName());
                selectedItemPlaceCategoryTextView.setText(placeKeywordList.get(position).getCategoryName());

                if (placeKeywordList.get(position).getRoadAddressName() != null)
                {
                    selectedItemPlaceAddressTextView.setText(placeKeywordList.get(position).getRoadAddressName());
                } else
                {
                    // 지번주소만 있는 경우
                    selectedItemPlaceAddressTextView.setText(placeKeywordList.get(position).getAddressName());
                }
                selectedItemPlaceDescriptionTextView.setText("TEST");
                break;

            case DownloadData.PLACE_CATEGORY:
                selectedItemPlaceNameTextView.setText(placeCategoryList.get(position).getPlaceName());
                selectedItemPlaceCategoryTextView.setText(placeCategoryList.get(position).getCategoryName());

                if (placeCategoryList.get(position).getRoadAddressName() != null)
                {
                    selectedItemPlaceAddressTextView.setText(placeCategoryList.get(position).getRoadAddressName());
                } else
                {
                    // 지번주소만 있는 경우
                    selectedItemPlaceAddressTextView.setText(placeCategoryList.get(position).getAddressName());
                }
                selectedItemPlaceDescriptionTextView.setText("TEST");
                break;
        }
    }

    private void displayAddressInfo(int position)
    {
        selectedItemAddressNameTextView.setText(addressList.get(position).getAddressName());

        switch (addressList.get(position).getAddressType())
        {
            case AddressResponseDocuments.REGION:
                //지명
                selectedItemAnotherAddressTypeTextView.setText(getString(R.string.region));
                selectedItemAnotherAddressNameTextView.setText(addressList.get(position).getAddressResponseAddress().getAddressName());
                break;
            case AddressResponseDocuments.REGION_ADDR:
                //지명 주소
                selectedItemAnotherAddressTypeTextView.setText(getString(R.string.road_addr));
                selectedItemAnotherAddressNameTextView.setText(addressList.get(position).getAddressResponseRoadAddress().getAddressName());
                break;
            case AddressResponseDocuments.ROAD:
                //도로명
                selectedItemAnotherAddressTypeTextView.setText(getString(R.string.road));
                selectedItemAnotherAddressNameTextView.setText(addressList.get(position).getAddressResponseRoadAddress().getAddressName());
                break;
            case AddressResponseDocuments.ROAD_ADDR:
                //도로명 주소
                selectedItemAnotherAddressTypeTextView.setText(getString(R.string.region_addr));
                selectedItemAnotherAddressNameTextView.setText(addressList.get(position).getAddressResponseAddress().getAddressName());
                break;
        }
    }

    private void setCenterPoint(double latitude, double longitude, int position)
    {
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);

        MapPOIItem marker = new MapPOIItem();

        if (resultType == DownloadData.ADDRESS)
        {
            marker.setItemName(addressList.get(position).getAddressName());
        } else if (resultType == DownloadData.PLACE_KEYWORD)
        {
            marker.setItemName(placeKeywordList.get(position).getPlaceName());
        } else if (resultType == DownloadData.PLACE_CATEGORY)
        {
            marker.setItemName(placeCategoryList.get(position).getPlaceName());
        }
        marker.setTag(0);
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker);
    }

    private void setBottomSheetCallback()
    {
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback()
        {
            @Override
            public void onStateChanged(@NonNull View view, int newState)
            {

            }

            @Override
            public void onSlide(@NonNull View view, float v)
            {

            }
        });
    }

    private void inflateSelectItemLayout(int isVisibility)
    {
        bottomSheet.setVisibility(View.VISIBLE);
        bottomSheetBehavior.setHideable(false);

        /*
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
        {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else
        {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

         */

        switch (resultType)
        {
            case DownloadData.ADDRESS:
                addressItemLayout.setVisibility(isVisibility);
                placeItemLayout.setVisibility(View.GONE);
                break;

            default:
                placeItemLayout.setVisibility(isVisibility);
                addressItemLayout.setVisibility(View.GONE);
                break;
        }
    }

    class TimeOutThread extends Thread
    {

        @Override
        public void run()
        {
            try
            {
                Thread.sleep(5000);

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                        gpsButton.setClickable(true);
                    }
                });
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

}