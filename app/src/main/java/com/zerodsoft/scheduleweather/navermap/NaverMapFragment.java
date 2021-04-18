package com.zerodsoft.scheduleweather.navermap;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.os.RemoteException;
import android.provider.Settings;
import android.service.carrier.CarrierMessagingService;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.geometry.Utmk;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapOptions;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.Projection;
import com.naver.maps.map.overlay.CircleOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.util.FusedLocationSource;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.databinding.FragmentNaverMapBinding;
import com.zerodsoft.scheduleweather.etc.AppPermission;
import com.zerodsoft.scheduleweather.etc.FragmentStateCallback;
import com.zerodsoft.scheduleweather.event.places.interfaces.OnClickedPlacesListListener;
import com.zerodsoft.scheduleweather.event.places.interfaces.PoiItemOnClickListener;
import com.zerodsoft.scheduleweather.kakaomap.bottomsheet.adapter.PlaceItemInMapViewAdapter;
import com.zerodsoft.scheduleweather.kakaomap.building.fragment.BuildingFragment;
import com.zerodsoft.scheduleweather.kakaomap.building.fragment.BuildingListFragment;
import com.zerodsoft.scheduleweather.kakaomap.fragment.search.LocationSearchFragment;
import com.zerodsoft.scheduleweather.kakaomap.fragment.searchheader.MapHeaderMainFragment;
import com.zerodsoft.scheduleweather.kakaomap.fragment.searchheader.MapHeaderSearchFragment;
import com.zerodsoft.scheduleweather.kakaomap.fragment.searchresult.LocationSearchResultFragment;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.BuildingFragmentController;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.BuildingLocationSelectorController;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapData;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.INetwork;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.OnClickedBottomSheetListener;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.PlacesItemBottomSheetButtonOnClickListener;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.PlacesListBottomSheetController;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.SearchBarController;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.SearchFragmentController;
import com.zerodsoft.scheduleweather.kakaomap.place.PlaceInfoFragment;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.address.ReverseGeoCodingParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.address.reversegeocoding.ReverseGeoCodingResponse;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;
import com.zerodsoft.scheduleweather.sgis.SgisAddress;
import com.zerodsoft.scheduleweather.sgis.SgisAuth;
import com.zerodsoft.scheduleweather.utility.NetworkStatus;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NaverMapFragment extends Fragment implements OnMapReadyCallback, IMapPoint, IMapData, INetwork, OnClickedPlacesListListener, PlacesItemBottomSheetButtonOnClickListener,
        PlacesListBottomSheetController, PoiItemOnClickListener<Marker>, OnClickedBottomSheetListener,
        MapHeaderSearchFragment.LocationSearchListener, SearchFragmentController, BuildingLocationSelectorController,
        BuildingFragmentController, BuildingListFragment.OnSearchRadiusChangeListener, NaverMap.OnMapClickListener,
        NaverMap.OnCameraIdleListener, CameraUpdate.FinishCallback, NaverMap.OnLocationChangeListener
{
    public static final int REQUEST_CODE_LOCATION = 10000;
    public static final int BUILDING_RANGE_OVERLAY_TAG = 1500;

    private static final String TAG = "NaverMapFragment";

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private FusedLocationSource fusedLocationSource;
    private LocationManager locationManager;
    private CircleOverlay buildingRangeCircleOverlay;

    public FragmentNaverMapBinding binding;
    public MapFragment mapFragment;
    public NaverMap naverMap;

    public PlacesItemBottomSheetButtonOnClickListener placesItemBottomSheetButtonOnClickListener;

    public ImageButton zoomInButton;
    public ImageButton zoomOutButton;
    public ImageButton gpsButton;
    public ImageButton buildingButton;

    public int selectedPoiItemIndex;
    public boolean isSelectedPoiItem;

    public NetworkStatus networkStatus;

    public LinearLayout placesListBottomSheet;
    public BottomSheetBehavior placeListBottomSheetBehavior;

    public LinearLayout locationSearchBottomSheet;
    public BottomSheetBehavior locationSearchBottomSheetBehavior;

    public LinearLayout buildingBottomSheet;
    public BottomSheetBehavior buildingBottomSheetBehavior;

    public ViewPager2 placesBottomSheetViewPager;
    public PlaceItemInMapViewAdapter adapter;

    public int placeBottomSheetSelectBtnVisibility;
    public int placeBottomSheetUnSelectBtnVisibility;

    public Double mapTranslationYByBuildingBottomSheet;

    public List<Marker> markerList = new ArrayList<>();


    public void setPlacesItemBottomSheetButtonOnClickListener(PlacesItemBottomSheetButtonOnClickListener placesItemBottomSheetButtonOnClickListener)
    {
        this.placesItemBottomSheetButtonOnClickListener = placesItemBottomSheetButtonOnClickListener;
    }

    public final LocationListener locationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            if (getActivity() != null)
            {
                CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(location.getLatitude(), location.getLongitude()));
                naverMap.moveCamera(cameraUpdate);
                setCurrentAddress();
                locationManager.removeUpdates(locationListener);
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle)
        {

        }

        @Override
        public void onProviderEnabled(String s)
        {

        }

        @Override
        public void onProviderDisabled(String s)
        {

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        networkStatus = new NetworkStatus(getActivity());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = FragmentNaverMapBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        setPlacesListBottomSheet();
        setLocationSearchBottomSheet();
        setBuildingBottomSheet();

        zoomInButton = binding.naverMapButtonsLayout.zoomInButton;
        zoomOutButton = binding.naverMapButtonsLayout.zoomOutButton;
        gpsButton = binding.naverMapButtonsLayout.gpsButton;
        buildingButton = binding.naverMapButtonsLayout.buildingButton;

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        binding.naverMapFragmentRootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                //search bottom sheet 크기 조정
                int headerBarHeight = (int) getResources().getDimension(R.dimen.map_header_bar_height);
                int headerBarTopMargin = (int) getResources().getDimension(R.dimen.map_header_bar_top_margin);
                int headerBarMargin = (int) (headerBarTopMargin * 1.5f);

                int searchBottomSheetHeight = binding.naverMapFragmentRootLayout.getHeight() - headerBarHeight - headerBarMargin;

                locationSearchBottomSheet.getLayoutParams().height = searchBottomSheetHeight;
                locationSearchBottomSheet.requestLayout();
                locationSearchBottomSheetBehavior.onLayoutChild(binding.naverMapFragmentRootLayout, locationSearchBottomSheet, ViewCompat.LAYOUT_DIRECTION_LTR);

                //building list bottom sheet 크기 조정
                int buildingBottomSheetExtraHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60f, getContext().getResources().getDisplayMetrics());

                //list 프래그먼트와 빌딩 정보 프래그먼트 두 개의 높이를 다르게 설정
                int buildingListHeight = binding.naverMapFragmentRootLayout.getHeight() / 2 + buildingBottomSheetExtraHeight;
                int buildingInfoHeight = searchBottomSheetHeight;
                BuildingBottomSheetHeightViewHolder buildingBottomSheetHeightViewHolder = new BuildingBottomSheetHeightViewHolder(buildingListHeight, buildingInfoHeight);

                buildingBottomSheet.setTag(buildingBottomSheetHeightViewHolder);

                buildingBottomSheet.getLayoutParams().height = buildingListHeight;
                buildingBottomSheet.requestLayout();
                buildingBottomSheetBehavior.onLayoutChild(binding.naverMapFragmentRootLayout, buildingBottomSheet, ViewCompat.LAYOUT_DIRECTION_LTR);

                binding.naverMapFragmentRootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        binding.naverMapHeaderBar.getRoot().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                removeBuildingLocationSelector();
                closeBuildingFragments();
                FragmentManager fragmentManager = getChildFragmentManager();
                LocationSearchFragment locationSearchFragment = (LocationSearchFragment) fragmentManager.findFragmentByTag(LocationSearchFragment.TAG);

                fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(MapHeaderMainFragment.TAG))
                        .show(fragmentManager.findFragmentByTag(MapHeaderSearchFragment.TAG))
                        .show(locationSearchFragment)
                        .commitNow();

                locationSearchFragment.addOnBackPressedCallback();
                locationSearchBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        buildingButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showBuildingLocationSelector();
            }
        });

        zoomInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                naverMap.moveCamera(CameraUpdate.zoomIn());
            }
        });

        zoomOutButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                naverMap.moveCamera(CameraUpdate.zoomOut());
            }
        });

        gpsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                /*
                if (!AppPermission.grantedPermissions(
                        getContext(), Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION))

                {
                    requestLocationUpdates.launch(PERMISSIONS);
                } else
                {
                    naverMap.setLocationSource(fusedLocationSource);
                    naverMap.setLocationTrackingMode(LocationTrackingMode.NoFollow);
                }

                 */


                if (naverMap.getLocationTrackingMode() == LocationTrackingMode.None)
                {
                    //권한 확인
                    boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                    ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
                    ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

                    if (networkAvailable())
                    {
                        if (isGpsEnabled || isNetworkEnabled)
                        {
                            naverMap.setLocationSource(fusedLocationSource);
                            naverMap.setLocationTrackingMode(LocationTrackingMode.NoFollow);
                        } else if (!isGpsEnabled)
                        {
                            showRequestGpsDialog();
                        }
                    }
                } else
                {
                    naverMap.setLocationSource(null);
                }
            }
        });


        FragmentManager fragmentManager = getChildFragmentManager();

        mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.naver_map_fragment);
        if (mapFragment == null)
        {
            NaverMapOptions naverMapOptions = new NaverMapOptions();
            naverMapOptions.scaleBarEnabled(true).locationButtonEnabled(false).compassEnabled(false).zoomControlEnabled(false);

            mapFragment = MapFragment.newInstance(naverMapOptions);

            fragmentManager.beginTransaction().add(R.id.naver_map_fragment, mapFragment).commitNow();
        }

        fusedLocationSource = new FusedLocationSource(this, PERMISSION_REQUEST_CODE);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    private void setLocationSearchBottomSheet()
    {
        locationSearchBottomSheet = binding.locationSearchBottomSheet.locationSearchBottomsheet;

        locationSearchBottomSheetBehavior = BottomSheetBehavior.from(locationSearchBottomSheet);
        locationSearchBottomSheetBehavior.setDraggable(false);
        locationSearchBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        locationSearchBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback()
        {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState)
            {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset)
            {

            }
        });

        MapHeaderSearchFragment mapHeaderSearchFragment = new MapHeaderSearchFragment(NaverMapFragment.this);
        LocationSearchFragment locationSearchFragment = new LocationSearchFragment(NaverMapFragment.this,
                new FragmentStateCallback()
                {
                    @Override
                    public void onChangedState(int state)
                    {
                    }
                });

        mapHeaderSearchFragment.setSearchHistoryDataController(locationSearchFragment);
        locationSearchFragment.setSearchBarController(mapHeaderSearchFragment);

        getChildFragmentManager().beginTransaction()
                .add(binding.naverMapHeaderBar.headerFragmentContainer.getId(), new MapHeaderMainFragment(), MapHeaderMainFragment.TAG)
                .add(binding.naverMapHeaderBar.headerFragmentContainer.getId(), mapHeaderSearchFragment, MapHeaderSearchFragment.TAG)
                .add(binding.locationSearchBottomSheet.searchFragmentContainer.getId(), locationSearchFragment, LocationSearchFragment.TAG)
                .hide(mapHeaderSearchFragment)
                .commitNow();
    }

    private void setBuildingBottomSheet()
    {
        buildingBottomSheet = (LinearLayout) binding.buildingBottomSheet.buildingBottomsheet;

        buildingBottomSheetBehavior = BottomSheetBehavior.from(buildingBottomSheet);
        buildingBottomSheetBehavior.setDraggable(false);
        buildingBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        buildingBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback()
        {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState)
            {
                switch (newState)
                {
                    case BottomSheetBehavior.STATE_EXPANDED:
                    {
                       /*
                       <지도 카메라 위치 이동 방법>
                       MapView.getMapCenterPoint() 메소드로 지도 중심 좌표(MapPoint center)를 얻습니다.
                        중심 좌표 객체의 center.getMapPointScreenLocation() 메소드를 통해 pixel 좌표값(MapPoint.PlainCoordinate pixel)을 얻어냅니다.
                        그 pixel 좌표값으로부터 얼마나 이동시키면 될 지 계산합니다. 앞서 구한 pixel에 이동하고자 하는 offset을 더하여 tx, ty 값을 확보합니다.
                        (double tx = pixel.x + offsetX, double ty = pixel.y + offsetY)
                        MapPoint newCenter = MapPoint.mapPointWithScreenLocation(tx, ty) 정적 메소드로 입력한 스크린 좌표를 역변환 하여 지도상 좌표(newCenter)를 구합니다.
                        MapView.setMapCenterPoint(newCenter, true) 메소드로 지도 중심을 이동시킵니다.
                        */
                        PointF movePoint = new PointF(0f, -mapTranslationYByBuildingBottomSheet.floatValue());
                        CameraUpdate cameraUpdate = CameraUpdate.scrollBy(movePoint);
                        naverMap.moveCamera(cameraUpdate);

                        break;
                    }
                    case BottomSheetBehavior.STATE_COLLAPSED:
                    {
                        PointF movePoint = new PointF(0f, mapTranslationYByBuildingBottomSheet.floatValue());
                        CameraUpdate cameraUpdate = CameraUpdate.scrollBy(movePoint);
                        naverMap.moveCamera(cameraUpdate);

                        break;
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset)
            {
                //expanded일때 offset == 1.0, collapsed일때 offset == 0.0
                //offset에 따라서 버튼들이 이동하고, 지도의 좌표가 변경되어야 한다.
                float translationValue = -buildingBottomSheet.getHeight() * slideOffset;

                binding.naverMapButtonsLayout.getRoot().animate().translationY(translationValue);
            }
        });
    }

    private void setPlacesListBottomSheet()
    {
        placesListBottomSheet = binding.placeslistBottomSheet.placesBottomsheet;
        placesBottomSheetViewPager = (ViewPager2) placesListBottomSheet.findViewById(R.id.place_items_viewpager);

        placesBottomSheetViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback()
        {
            private int mCurrentPosition;
            private int mScrollState;

            @Override
            public void onPageScrollStateChanged(final int state)
            {
                handleScrollState(state);
                mScrollState = state;
            }

            private void handleScrollState(final int state)
            {
                if (state == ViewPager.SCROLL_STATE_IDLE && mScrollState == ViewPager.SCROLL_STATE_DRAGGING)
                {
                    setNextItemIfNeeded();
                }
            }

            private void setNextItemIfNeeded()
            {
                if (!isScrollStateSettling())
                {
                    handleSetNextItem();
                }
            }

            private boolean isScrollStateSettling()
            {
                return mScrollState == ViewPager.SCROLL_STATE_SETTLING;
            }

            private void handleSetNextItem()
            {

            }

            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels)
            {
            }

            @Override
            public void onPageSelected(int position)
            {
                super.onPageSelected(position);

                mCurrentPosition = position;
                if (isSelectedPoiItem)
                {
                    onPOIItemSelectedByBottomSheet(mCurrentPosition);
                }
            }
        });

        final int rlPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, getResources().getDisplayMetrics());
        final int bPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, getResources().getDisplayMetrics());
        final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, getResources().getDisplayMetrics());

        placesBottomSheetViewPager.setPadding(rlPadding, 0, rlPadding, bPadding);
        placesBottomSheetViewPager.setOffscreenPageLimit(3);
        placesBottomSheetViewPager.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(margin));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer()
        {
            @Override
            public void transformPage(@NonNull View page, float position)
            {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.8f + r * 0.2f);
            }
        });
        placesBottomSheetViewPager.setPageTransformer(compositePageTransformer);

        placeListBottomSheetBehavior = BottomSheetBehavior.from(placesListBottomSheet);
        placeListBottomSheetBehavior.setDraggable(true);
        placeListBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback()
        {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState)
            {
                switch (newState)
                {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset)
            {
                //expanded일때 offset == 1.0, collapsed일때 offset == 0.0
                //offset에 따라서 버튼들이 이동하고, 지도의 좌표가 변경되어야 한다.
                float translationValue = -placesListBottomSheet.getHeight() * slideOffset;

                binding.naverMapButtonsLayout.getRoot().animate().translationY(translationValue);
            }
        });

    }

    public void setPlaceBottomSheetSelectBtnVisibility(int placeBottomSheetSelectBtnVisibility)
    {
        this.placeBottomSheetSelectBtnVisibility = placeBottomSheetSelectBtnVisibility;
    }

    public void setPlaceBottomSheetUnSelectBtnVisibility(
            int placeBottomSheetUnSelectBtnVisibility)
    {
        this.placeBottomSheetUnSelectBtnVisibility = placeBottomSheetUnSelectBtnVisibility;
    }


    public void showRequestGpsDialog()
    {
        new AlertDialog.Builder(getActivity())
                .setMessage(getString(R.string.request_to_make_gps_on))
                .setPositiveButton(getString(R.string.check), new
                        DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt)
                            {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap)
    {
        this.naverMap = naverMap;
        naverMap.addOnLocationChangeListener(this::onLocationChange);
        naverMap.addOnCameraIdleListener(this);
        naverMap.setOnMapClickListener(this);
        naverMap.getUiSettings().setZoomControlEnabled(false);

        //바텀 시트의 상태에 따라서 카메라를 이동시킬 Y값
        final int bottomSheetTopY = binding.naverMapViewLayout.getHeight() - buildingBottomSheet.getHeight();
        final int mapHeaderBarBottomY = binding.naverMapHeaderBar.getRoot().getBottom();
        final int SIZE_BETWEEN_HEADER_BAR_BOTTOM_AND_BOTTOM_SHEET_TOP = bottomSheetTopY - mapHeaderBarBottomY;

        Projection projection = naverMap.getProjection();
        PointF point = projection.toScreenLocation(naverMap.getCameraPosition().target);

        mapTranslationYByBuildingBottomSheet = (point.y - (mapHeaderBarBottomY +
                SIZE_BETWEEN_HEADER_BAR_BOTTOM_AND_BOTTOM_SHEET_TOP / 2.0));

    }

    private final ActivityResultLauncher<String[]> requestLocationUpdates = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>()
            {
                @Override
                public void onActivityResult(Map<String, Boolean> result)
                {

                    if (result.size() > 0 && result.get(PERMISSIONS[0]))
                    {
                        fusedLocationSource.onRequestPermissionsResult(REQUEST_CODE_LOCATION, PERMISSIONS, new int[]{PackageManager.PERMISSION_GRANTED, PackageManager.PERMISSION_GRANTED});
                        naverMap.setLocationSource(fusedLocationSource);
                        naverMap.setLocationTrackingMode(LocationTrackingMode.NoFollow);
                    } else
                    {
                        Toast.makeText(getActivity(), getString(R.string.message_needs_location_permission), Toast.LENGTH_SHORT).show();
                        naverMap.setLocationSource(null);
                    }
                }
            });

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        networkStatus.unregisterNetworkCallback();
    }

    public void setCurrentAddress()
    {
        //sgis reverse geocoding 이용
        LatLng latLng = naverMap.getContentBounds().getCenter();
        Utmk utmk = Utmk.valueOf(latLng);

        ReverseGeoCodingParameter parameter = new ReverseGeoCodingParameter();
        parameter.setAccessToken(SgisAuth.getSgisAuthResponse().getResult().getAccessToken());
        parameter.setAddrType("20");
        parameter.setxCoor(String.valueOf(utmk.x));
        parameter.setyCoor(String.valueOf(utmk.y));

        SgisAddress.reverseGeoCoding(parameter, new CarrierMessagingService.ResultCallback<DataWrapper<ReverseGeoCodingResponse>>()
        {
            @Override
            public void onReceiveResult(@NonNull DataWrapper<ReverseGeoCodingResponse> reverseGeoCodingResponseDataWrapper) throws RemoteException
            {
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (reverseGeoCodingResponseDataWrapper.getData() != null)
                        {
                            if (reverseGeoCodingResponseDataWrapper.getData().getResult() != null)
                            {
                                if (!reverseGeoCodingResponseDataWrapper.getData().getResult().isEmpty())
                                {
                                    binding.naverMapButtonsLayout.currentAddress.setText(reverseGeoCodingResponseDataWrapper.getData().getResult().get(0).getFullAddress());
                                    return;
                                }
                            }
                        }
                        binding.naverMapButtonsLayout.currentAddress.setText("");

                    }
                });
            }
        });
    }


    @Override
    public boolean networkAvailable()
    {
        return networkStatus.networkAvailable(getActivity());
    }

    @Override
    public double getLatitude()
    {
        return naverMap.getContentBounds().getCenter().latitude;
    }

    @Override
    public double getLongitude()
    {
        return naverMap.getContentBounds().getCenter().longitude;
    }

    @Override
    public void setMapVisibility(int visibility)
    {
        binding.naverMapViewLayout.setVisibility(visibility);
    }

    private void onClickedMarkerByTouch(Marker marker)
    {
        //poiitem을 직접 선택한 경우 호출
        selectedPoiItemIndex = markerList.indexOf(marker);
        isSelectedPoiItem = true;

        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(marker.getPosition());
        cameraUpdate.animate(CameraAnimation.Easing, 150);
        naverMap.moveCamera(cameraUpdate);
        //open bottomsheet and show selected item data
        placeListBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        placesBottomSheetViewPager.setCurrentItem(selectedPoiItemIndex, false);
    }

    @Override
    public void createPlacesPoiItems(List<PlaceDocuments> placeDocuments)
    {
        if (!placeDocuments.isEmpty())
        {
            adapter.setPlaceDocumentsList(placeDocuments);
            adapter.notifyDataSetChanged();

            removeAllPoiItems();
            KakaoLocalDocument kakaoLocalDocument = null;

            for (PlaceDocuments document : placeDocuments)
            {
                Marker marker = new Marker();
                marker.setPosition(new LatLng(document.getY(), document.getX()));
                marker.setMap(naverMap);
                marker.setCaptionText(document.getPlaceName());
                marker.setOnClickListener(new Overlay.OnClickListener()
                {
                    @Override
                    public boolean onClick(@NonNull Overlay overlay)
                    {
                        onClickedMarkerByTouch(marker);
                        return true;
                    }
                });

                kakaoLocalDocument = document;
                marker.setTag(kakaoLocalDocument);
                markerList.add(marker);
            }
        }
    }

    @Override
    public void createAddressesPoiItems(List<AddressResponseDocuments> addressDocuments)
    {
        if (!addressDocuments.isEmpty())
        {
            adapter.setPlaceDocumentsList(addressDocuments);
            adapter.notifyDataSetChanged();

            removeAllPoiItems();
            KakaoLocalDocument kakaoLocalDocument = null;

            for (AddressResponseDocuments document : addressDocuments)
            {
                Marker marker = new Marker();
                marker.setPosition(new LatLng(document.getY(), document.getX()));
                marker.setMap(naverMap);
                marker.setCaptionText(document.getAddressName());
                marker.setOnClickListener(new Overlay.OnClickListener()
                {
                    @Override
                    public boolean onClick(@NonNull Overlay overlay)
                    {
                        onClickedMarkerByTouch(marker);
                        return true;
                    }
                });

                kakaoLocalDocument = document;
                marker.setTag(kakaoLocalDocument);
                markerList.add(marker);
            }
        }
    }

    @Override
    public void selectPoiItem(int index)
    {
        onPOIItemSelectedByList(index);
    }


    @Override
    public void removeAllPoiItems()
    {
        for (Marker marker : markerList)
        {
            marker.setMap(null);
        }
        markerList.clear();
    }

    @Override
    public void showAllPoiItems()
    {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        List<LatLng> latLngList = new ArrayList<>();

        for (Marker marker : markerList)
        {
            latLngList.add(marker.getPosition());
        }

        builder.include(latLngList);
        CameraUpdate cameraUpdate = CameraUpdate.fitBounds(builder.build(), 10);
        naverMap.moveCamera(cameraUpdate);
    }

    @Override
    public void deselectPoiItem()
    {
        // mapView.deselectPOIItem(mapView.getPOIItems()[selectedPoiItemIndex]);
        isSelectedPoiItem = false;
    }

    @Override
    public void backToPreviousView()
    {
        if (isSelectedPoiItem)
        {
            deselectPoiItem();
        }
    }

    @Override
    public int getPoiItemSize()
    {
        return markerList.size();
    }

    @Override
    public void setPlacesListAdapter(PlaceItemInMapViewAdapter adapter)
    {
        this.adapter = adapter;
        placesBottomSheetViewPager.setAdapter(adapter);
        adapter.setPlacesItemBottomSheetButtonOnClickListener(placesItemBottomSheetButtonOnClickListener);
        adapter.setOnClickedBottomSheetListener(this);
        adapter.setVisibleSelectBtn(placeBottomSheetSelectBtnVisibility);
        adapter.setVisibleUnSelectBtn(placeBottomSheetUnSelectBtnVisibility);
    }

    public LocationDTO getSelectedLocationDto(int calendarId, long eventId)
    {
        // 선택된 poiitem의 리스트내 인덱스를 가져온다.
        // 인덱스로 아이템을 가져온다.

        LocationDTO location = new LocationDTO();
        location.setCalendarId(calendarId);
        location.setEventId(eventId);

        KakaoLocalDocument kakaoLocalDocument = (KakaoLocalDocument) markerList.get(selectedPoiItemIndex).getTag();

        // 주소인지 장소인지를 구분한다.
        if (kakaoLocalDocument instanceof PlaceDocuments)
        {
            PlaceDocuments placeDocuments = (PlaceDocuments) kakaoLocalDocument;
            location.setPlaceId(placeDocuments.getId());
            location.setPlaceName(placeDocuments.getPlaceName());
            location.setLatitude(placeDocuments.getY());
            location.setLongitude(placeDocuments.getX());
        } else
        {
            AddressResponseDocuments addressDocuments = (AddressResponseDocuments) kakaoLocalDocument;

            location.setAddressName(addressDocuments.getAddressName());
            location.setLatitude(addressDocuments.getY());
            location.setLongitude(addressDocuments.getX());
        }
        return location;
    }

    @Override
    public void onPOIItemSelectedByTouch(Marker e)
    {

    }

    @Override
    public void onPOIItemSelectedByList(int index)
    {
        //bottomsheet가 아닌 list에서 아이템을 선택한 경우 호출
        //adapter -> poiitem생성 -> select poiitem -> bottomsheet열고 정보 표시
        Marker marker = markerList.get(index);
        marker.performClick();
    }

    @Override
    public void onPOIItemSelectedByBottomSheet(int index)
    {
        //bottomsheet에서 스크롤 하는 경우 호출
        Marker marker = markerList.get(index);
        marker.performClick();
    }


    public int getSelectedPoiItemIndex()
    {
        return selectedPoiItemIndex;
    }

    @Override
    public void onClickedItemInList(PlaceCategoryDTO placeCategory, int index)
    {

    }

    @Override
    public void onClickedMoreInList(PlaceCategoryDTO placeCategory)
    {

    }


    @Override
    public void onSelectedLocation()
    {

    }

    @Override
    public void onRemovedLocation()
    {

    }


    public BottomSheetBehavior getPlaceListBottomSheetBehavior()
    {
        return placeListBottomSheetBehavior;
    }

    @Override
    public void setPlacesListBottomSheetState(int state)
    {
        placeListBottomSheetBehavior.setState(state);
    }

    @Override
    public int getPlacesListBottomSheetState()
    {
        return placeListBottomSheetBehavior.getState();
    }

    @Override
    public void onClickedPlaceBottomSheet(KakaoLocalDocument kakaoLocalDocument)
    {
        //place or address
        if (kakaoLocalDocument instanceof PlaceDocuments)
        {
            PlaceInfoFragment placeInfoFragment = new PlaceInfoFragment();
            Bundle bundle = new Bundle();
            bundle.putString("placeId", ((PlaceDocuments) kakaoLocalDocument).getId());
            placeInfoFragment.setArguments(bundle);

            placeInfoFragment.show(getChildFragmentManager(), PlaceInfoFragment.TAG);
        } else
        {

        }
    }

    @Override
    public void search(String query)
    {
        FragmentManager fragmentManager = getChildFragmentManager();
        boolean locationSearchResultIsVisible = fragmentManager.findFragmentByTag(LocationSearchResultFragment.TAG)
                != null ? true : false;

        if (locationSearchResultIsVisible)
        {
            placeListBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            removeAllPoiItems();

            LocationSearchResultFragment locationSearchResultFragment =
                    (LocationSearchResultFragment) fragmentManager.findFragmentByTag(LocationSearchResultFragment.TAG);
            locationSearchResultFragment.search(query);
        } else
        {
            MapHeaderSearchFragment mapHeaderSearchFragment = (MapHeaderSearchFragment) fragmentManager.findFragmentByTag(MapHeaderSearchFragment.TAG);
            mapHeaderSearchFragment.setViewTypeVisibility(View.VISIBLE);
            LocationSearchResultFragment locationSearchResultFragment = new LocationSearchResultFragment(query, this, mapHeaderSearchFragment);

            fragmentManager.beginTransaction().add(binding.locationSearchBottomSheet.searchFragmentContainer.getId()
                    , locationSearchResultFragment, LocationSearchResultFragment.TAG).hide(fragmentManager.findFragmentByTag(LocationSearchFragment.TAG))
                    .commitNow();

            locationSearchResultFragment.addOnBackPressedCallback();
        }
    }

    /**
     * current fragment tag마다 별도로 동작
     */
    @Override
    public void closeSearchFragments(String currentFragmentTag)
    {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        LocationSearchFragment locationSearchFragment = (LocationSearchFragment) fragmentManager.findFragmentByTag(LocationSearchFragment.TAG);
        MapHeaderMainFragment mapHeaderMainFragment = (MapHeaderMainFragment) fragmentManager.findFragmentByTag(MapHeaderMainFragment.TAG);
        MapHeaderSearchFragment mapHeaderSearchFragment = (MapHeaderSearchFragment) fragmentManager.findFragmentByTag(MapHeaderSearchFragment.TAG);

        if (currentFragmentTag.equals(LocationSearchFragment.TAG))
        {
            locationSearchBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            locationSearchFragment.removeOnBackPressedCallback();

            fragmentTransaction.hide(mapHeaderSearchFragment)
                    .show(mapHeaderMainFragment)
                    .commitNow();
        } else if (currentFragmentTag.equals(LocationSearchResultFragment.TAG))
        {
            removeBuildingLocationSelector();
            closeBuildingFragments();
            backToPreviousView();
            placeListBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            LocationSearchResultFragment locationSearchResultFragment =
                    (LocationSearchResultFragment) fragmentManager.findFragmentByTag(LocationSearchResultFragment.TAG);

            removeAllPoiItems();
            mapHeaderSearchFragment.setViewTypeVisibility(View.GONE);
            mapHeaderSearchFragment.changeViewTypeImg(SearchBarController.MAP);
            mapHeaderSearchFragment.setQuery("", false);

            locationSearchResultFragment.removeOnBackPressedCallback();
            fragmentTransaction.remove(locationSearchResultFragment).show(locationSearchFragment).commitNow();
        }
    }

    @Override
    public void setStateOfSearchBottomSheet(int state)
    {
        locationSearchBottomSheetBehavior.setState(state);
    }

    @Override
    public int getStateOfSearchBottomSheet()
    {
        return locationSearchBottomSheetBehavior.getState();
    }

    @Override
    public void removeBuildingLocationSelector()
    {
        if (binding.naverMapViewLayout.findViewWithTag("BUILDING_SELECTOR") != null)
        {
            buildingButton.setImageDrawable(getContext().getDrawable(R.drawable.building_black));
            binding.naverMapViewLayout.removeView(binding.naverMapViewLayout.findViewWithTag("BUILDING_SELECTOR"));
        }
    }

    @Override
    public void showBuildingLocationSelector()
    {
        if (binding.naverMapViewLayout.findViewWithTag("BUILDING_SELECTOR") == null)
        {
            closeBuildingFragments();

            buildingButton.setImageDrawable(getContext().getDrawable(R.drawable.building_blue));
            //드래그로 이동가능한 마커 생성
            View selectorView = getLayoutInflater().inflate(R.layout.building_location_selector_view, null);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            selectorView.setLayoutParams(layoutParams);
            selectorView.setTag("BUILDING_SELECTOR");

            binding.naverMapViewLayout.addView(selectorView);

            ((Button) selectorView.findViewById(R.id.search_buildings_button)).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    //빌딩 목록 바텀 시트 열기
                    //map center point를 좌표로 지정
                    setBuildingBottomSheetHeight(BuildingListFragment.TAG);
                    removeBuildingLocationSelector();

                    drawSearchRadiusCircle();
                    LatLng latLng = naverMap.getCameraPosition().target;

                    String centerLatitude = String.valueOf(latLng.latitude);
                    String centerLongitude = String.valueOf(latLng.longitude);

                    BuildingListFragment buildingListFragment = new BuildingListFragment(NaverMapFragment.this);
                    Bundle bundle = new Bundle();
                    bundle.putString("centerLatitude", centerLatitude);
                    bundle.putString("centerLongitude", centerLongitude);
                    buildingListFragment.setArguments(bundle);

                    getChildFragmentManager().beginTransaction().add(binding.buildingBottomSheet.buildingFragmentContainer.getId(), buildingListFragment,
                            BuildingListFragment.TAG)
                            .commitNow();

                    buildingListFragment.addOnBackPressedCallback();

                    buildingBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }

            });
        } else
        {
            closeBuildingFragments();
            removeBuildingLocationSelector();
        }

    }

    @Override
    public void setStateBuildingBottomSheet(int state)
    {
        buildingBottomSheetBehavior.setState(state);
    }

    @Override
    public void closeBuildingFragments(String currentFragmentTag)
    {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        BuildingListFragment buildingListFragment = (BuildingListFragment) fragmentManager.findFragmentByTag(BuildingListFragment.TAG);

        if (currentFragmentTag.equals(BuildingListFragment.TAG))
        {
            buildingBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            buildingListFragment.removeOnBackPressedCallback();

            fragmentTransaction.remove(buildingListFragment)
                    .commitNow();
            if (buildingRangeCircleOverlay != null)
            {
                buildingRangeCircleOverlay.setMap(null);
                buildingRangeCircleOverlay = null;
            }
        } else if (currentFragmentTag.equals(BuildingFragment.TAG))
        {
            setBuildingBottomSheetHeight(BuildingListFragment.TAG);

            BuildingFragment buildingFragment = (BuildingFragment) fragmentManager.findFragmentByTag(BuildingFragment.TAG);
            buildingFragment.removeOnBackPressedCallback();
            fragmentTransaction.remove(buildingFragment).show(buildingListFragment).commitNow();

            buildingBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    public void closeBuildingFragments()
    {
        if (buildingBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED)
        {
            setBuildingBottomSheetHeight(BuildingListFragment.TAG);

            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            BuildingListFragment buildingListFragment = (BuildingListFragment) fragmentManager.findFragmentByTag(BuildingListFragment.TAG);
            BuildingFragment buildingFragment = (BuildingFragment) fragmentManager.findFragmentByTag(BuildingFragment.TAG);

            buildingBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            if (buildingRangeCircleOverlay != null)
            {
                buildingRangeCircleOverlay.setMap(null);
                buildingRangeCircleOverlay = null;
            }

            if (buildingFragment != null)
            {
                buildingFragment.removeOnBackPressedCallback();
                buildingListFragment.removeOnBackPressedCallback();

                fragmentTransaction.remove(buildingListFragment)
                        .remove(buildingFragment)
                        .commitNow();
            } else
            {
                buildingListFragment.removeOnBackPressedCallback();

                fragmentTransaction.remove(buildingListFragment)
                        .commitNow();
            }
        }
    }

    @Override
    public void drawSearchRadiusCircle()
    {
        LatLng latLng = naverMap.getCameraPosition().target;
        if (buildingRangeCircleOverlay != null)
        {
            latLng = buildingRangeCircleOverlay.getCenter();
            buildingRangeCircleOverlay.setMap(null);
            buildingRangeCircleOverlay = null;
        }

        buildingRangeCircleOverlay = new CircleOverlay(latLng, Integer.parseInt(App.getPreference_key_range_meter_for_search_buildings()));
        buildingRangeCircleOverlay.setColor(Color.argb(128, 0, 255, 0));
        buildingRangeCircleOverlay.setOutlineColor(Color.argb(128, 255, 0, 0));
        buildingRangeCircleOverlay.setTag(BUILDING_RANGE_OVERLAY_TAG);
        buildingRangeCircleOverlay.setMap(naverMap);
    }

    @Override
    public void setBuildingBottomSheetHeight(String fragmentTag)
    {
        BuildingBottomSheetHeightViewHolder buildingBottomSheetHeightViewHolder = (BuildingBottomSheetHeightViewHolder) buildingBottomSheet.getTag();

        if (fragmentTag.equals(BuildingListFragment.TAG))
        {
            buildingBottomSheet.getLayoutParams().height = buildingBottomSheetHeightViewHolder.listHeight;
        } else if (fragmentTag.equals(BuildingFragment.TAG))
        {
            buildingBottomSheet.getLayoutParams().height = buildingBottomSheetHeightViewHolder.infoHeight;
        }

        buildingBottomSheet.requestLayout();
        buildingBottomSheetBehavior.onLayoutChild(binding.naverMapFragmentRootLayout, buildingBottomSheet, ViewCompat.LAYOUT_DIRECTION_LTR);
    }

    @Override
    public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng)
    {
        if (isSelectedPoiItem)
        {
            deselectPoiItem();
            placeListBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    @Override
    public void onCameraIdle()
    {
        setCurrentAddress();
    }

    @Override
    public void onCameraUpdateFinish()
    {
        setCurrentAddress();
    }

    @Override
    public void onLocationChange(@NonNull Location location)
    {
        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(location.getLatitude(), location.getLongitude()));
        naverMap.moveCamera(cameraUpdate);
        naverMap.setLocationSource(null);
    }

    static class BuildingBottomSheetHeightViewHolder
    {
        final int listHeight;
        final int infoHeight;

        public BuildingBottomSheetHeightViewHolder(int listHeight, int infoHeight)
        {
            this.listHeight = listHeight;
            this.infoHeight = infoHeight;
        }
    }

}