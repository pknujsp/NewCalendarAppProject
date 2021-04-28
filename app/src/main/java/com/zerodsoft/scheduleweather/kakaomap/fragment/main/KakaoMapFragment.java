package com.zerodsoft.scheduleweather.kakaomap.fragment.main;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.provider.Settings;
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

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.etc.FragmentStateCallback;
import com.zerodsoft.scheduleweather.etc.LocationType;
import com.zerodsoft.scheduleweather.kakaomap.bottomsheet.adapter.PlaceItemInMapViewAdapter;
import com.zerodsoft.scheduleweather.event.places.interfaces.PoiItemOnClickListener;
import com.zerodsoft.scheduleweather.kakaomap.building.fragment.BuildingFragment;
import com.zerodsoft.scheduleweather.kakaomap.building.fragment.BuildingListFragment;
import com.zerodsoft.scheduleweather.kakaomap.fragment.search.LocationSearchFragment;
import com.zerodsoft.scheduleweather.kakaomap.fragment.searchheader.MapHeaderMainFragment;
import com.zerodsoft.scheduleweather.kakaomap.fragment.searchheader.MapHeaderSearchFragment;
import com.zerodsoft.scheduleweather.kakaomap.fragment.searchresult.LocationSearchResultFragment;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.BuildingFragmentController;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.BuildingLocationSelectorController;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.OnClickedBottomSheetListener;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.PlacesListBottomSheetController;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.SearchBarController;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.SearchFragmentController;
import com.zerodsoft.scheduleweather.kakaomap.place.PlaceInfoFragment;
import com.zerodsoft.scheduleweather.kakaomap.util.RequestLocationTimer;
import com.zerodsoft.scheduleweather.databinding.FragmentMapBinding;
import com.zerodsoft.scheduleweather.etc.AppPermission;
import com.zerodsoft.scheduleweather.event.places.interfaces.OnClickedPlacesListListener;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.PlacesItemBottomSheetButtonOnClickListener;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapData;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.INetwork;
import com.zerodsoft.scheduleweather.kakaomap.model.CustomPoiItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddressDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;
import com.zerodsoft.scheduleweather.utility.NetworkStatus;

import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;

import static androidx.core.content.ContextCompat.checkSelfPermission;

public class KakaoMapFragment extends Fragment implements IMapPoint, IMapData, MapView.POIItemEventListener, MapView.MapViewEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener,
        INetwork, OnClickedPlacesListListener, PlacesItemBottomSheetButtonOnClickListener,
        PlacesListBottomSheetController, PoiItemOnClickListener<MapPOIItem>, OnClickedBottomSheetListener,
        MapHeaderSearchFragment.LocationSearchListener, SearchFragmentController, BuildingLocationSelectorController,
        BuildingFragmentController, BuildingListFragment.OnSearchRadiusChangeListener
{
    public static final int REQUEST_CODE_LOCATION = 10000;

    public PlacesItemBottomSheetButtonOnClickListener placesItemBottomSheetButtonOnClickListener;
    public FragmentMapBinding binding;
    public MapView mapView;

    public static final int BUILDING_RANGE_OVERLAY_TAG = 1500;

    private String appKey;
    public MapReverseGeoCoder mapReverseGeoCoder;
    public LocationManager locationManager;

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

    public KakaoMapFragment()
    {

    }


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
                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(location.getLatitude(), location.getLongitude()), true);
                mapReverseGeoCoder = new MapReverseGeoCoder(appKey, mapView.getMapCenterPoint(), KakaoMapFragment.this, getActivity());
                mapReverseGeoCoder.startFindingAddress(MapReverseGeoCoder.AddressType.ShortAddress);
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
        networkStatus = new NetworkStatus(getContext(), new ConnectivityManager.NetworkCallback()
        {
            @Override
            public void onAvailable(@NonNull Network network)
            {
                super.onAvailable(network);
            }

            @Override
            public void onLost(@NonNull Network network)
            {
                super.onLost(network);
            }

            @Override
            public void onUnavailable()
            {
                super.onUnavailable();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = FragmentMapBinding.inflate(inflater);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        setPlacesListBottomSheet();
        setLocationSearchBottomSheet();
        setBuildingBottomSheet();

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        zoomInButton = binding.mapButtonsLayout.zoomInButton;
        zoomOutButton = binding.mapButtonsLayout.zoomOutButton;
        gpsButton = binding.mapButtonsLayout.gpsButton;
        buildingButton = binding.mapButtonsLayout.buildingButton;


        binding.mapRootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                //search bottom sheet 크기 조정
                int headerBarHeight = (int) getResources().getDimension(R.dimen.map_header_bar_height);
                int headerBarTopMargin = (int) getResources().getDimension(R.dimen.map_header_bar_top_margin);
                int headerBarMargin = (int) (headerBarTopMargin * 1.5f);

                int searchBottomSheetHeight = binding.mapRootLayout.getHeight() - headerBarHeight - headerBarMargin;

                locationSearchBottomSheet.getLayoutParams().height = searchBottomSheetHeight;
                locationSearchBottomSheet.requestLayout();
                locationSearchBottomSheetBehavior.onLayoutChild(binding.mapRootLayout, locationSearchBottomSheet, ViewCompat.LAYOUT_DIRECTION_LTR);

                //building list bottom sheet 크기 조정
                int buildingBottomSheetExtraHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60f, getContext().getResources().getDisplayMetrics());

                //list 프래그먼트와 빌딩 정보 프래그먼트 두 개의 높이를 다르게 설정
                int buildingListHeight = binding.mapRootLayout.getHeight() / 2 + buildingBottomSheetExtraHeight;
                int buildingInfoHeight = searchBottomSheetHeight;
                BuildingBottomSheetHeightViewHolder buildingBottomSheetHeightViewHolder = new BuildingBottomSheetHeightViewHolder(buildingListHeight, buildingInfoHeight);

                buildingBottomSheet.setTag(buildingBottomSheetHeightViewHolder);

                buildingBottomSheet.getLayoutParams().height = buildingListHeight;
                buildingBottomSheet.requestLayout();
                buildingBottomSheetBehavior.onLayoutChild(binding.mapRootLayout, buildingBottomSheet, ViewCompat.LAYOUT_DIRECTION_LTR);

                binding.mapRootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        binding.mapHeaderBar.getRoot().setOnClickListener(new View.OnClickListener()
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
                mapView.zoomIn(true);
            }
        });

        zoomOutButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mapView.zoomOut(true);
            }
        });

        gpsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //권한 확인
                boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
                checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

                if (networkAvailable())
                {
                    if (isGpsEnabled && isNetworkEnabled)
                    {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                        Timer timer = new Timer();
                        timer.schedule(new RequestLocationTimer()
                        {
                            @Override
                            public void run()
                            {
                                timer.cancel();
                                getActivity().runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        locationManager.removeUpdates(locationListener);
                                        checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
                                        checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
                                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                                    }
                                });

                            }
                        }, 2000);
                    } else if (!isGpsEnabled)
                    {
                        showRequestGpsDialog();
                    }
                }

            }
        });

        MapView.setMapTilePersistentCacheEnabled(true);
        mapView = new MapView(getActivity());
        mapView.setPOIItemEventListener(this);
        mapView.setMapViewEventListener(this);

        binding.mapView.addView(mapView);

        if (!AppPermission.grantedPermissions(
                getContext(), Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION))

        {
            permissionResultLauncher.launch(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION});
        }


    }

    final ActivityResultLauncher<String[]> permissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>()
            {
                @Override
                public void onActivityResult(Map<String, Boolean> result)
                {
                    Set<String> keySet = result.keySet();
                    for (String key : keySet)
                    {
                        if (!result.get(key))
                        {
                            Toast.makeText(getActivity(), getString(R.string.message_needs_location_permission), Toast.LENGTH_SHORT).show();
                            binding.mapButtonsLayout.gpsButton.setVisibility(View.GONE);
                            break;
                        }
                    }
                }
            });

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


        MapHeaderSearchFragment mapHeaderSearchFragment = new MapHeaderSearchFragment(KakaoMapFragment.this);
        LocationSearchFragment locationSearchFragment = new LocationSearchFragment(KakaoMapFragment.this,
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
                .add(binding.mapHeaderBar.headerFragmentContainer.getId(), new MapHeaderMainFragment(), MapHeaderMainFragment.TAG)
                .add(binding.mapHeaderBar.headerFragmentContainer.getId(), mapHeaderSearchFragment, MapHeaderSearchFragment.TAG)
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
                        MapPoint.PlainCoordinate currentCenterCoordinate = mapView.getMapCenterPoint().getMapPointScreenLocation();

                        MapPoint newCenter = MapPoint.mapPointWithScreenLocation(currentCenterCoordinate.x, currentCenterCoordinate.y + mapTranslationYByBuildingBottomSheet);
                        mapView.setMapCenterPoint(newCenter, true);

                        break;
                    }
                    case BottomSheetBehavior.STATE_COLLAPSED:
                    {
                        MapPoint.PlainCoordinate currentMapCenterCoordinate = mapView.getMapCenterPoint().getMapPointScreenLocation();
                        MapPoint newCenter = MapPoint.mapPointWithScreenLocation(currentMapCenterCoordinate.x, currentMapCenterCoordinate.y - mapTranslationYByBuildingBottomSheet);
                        mapView.setMapCenterPoint(newCenter, true);

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

                binding.mapButtonsLayout.getRoot().animate().translationY(translationValue);
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
                /*
                final int lastPosition = bottomSheetViewPager.getAdapter().getItemCount() - 1;
                if (mCurrentPosition == 0)
                {
                    bottomSheetViewPager.setCurrentItem(lastPosition, false);
                } else if (mCurrentPosition == lastPosition)
                {
                    bottomSheetViewPager.setCurrentItem(0, false);
                }

                 */
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

                binding.mapButtonsLayout.getRoot().animate().translationY(translationValue);
            }
        });

       /*
        placeListBottomSheetBehavior.setPeekHeight(0);
        placeListBottomSheetBehavior.setDraggable(true);
        placeListBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        placeListBottomSheetBehavior.setAnchorOffset(0.5f);
        placeListBottomSheetBehavior.setAnchorSheetCallback(new PlacesListBottomSheetBehavior.AnchorSheetCallback()
        {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState)
            {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                {

                } else if (newState == BottomSheetBehavior.STATE_EXPANDED)
                {

                } else if (newState == BottomSheetBehavior.STATE_DRAGGING)
                {

                } else if (newState == BottomSheetBehavior.STATE_HALF_EXPANDED)
                {

                } else if (newState == BottomSheetBehavior.STATE_HIDDEN)
                {

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset)
            {
                float h = bottomSheet.getHeight();
                float off = h * slideOffset;


                switch (placeListBottomSheetBehavior.getState())
                {
                    case BottomSheetBehavior.STATE_DRAGGING:
                    case BottomSheetBehavior.STATE_SETTLING:
                        setMapPaddingBottom(off);
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                    case BottomSheetBehavior.STATE_COLLAPSED:
                    case BottomSheetBehavior.STATE_EXPANDED:
                        break;
                }
            }
        });

        */
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
    public void onStart()
    {
        super.onStart();

        int headerBarHeight = binding.mapHeaderBar.getRoot().getHeight();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) binding.mapHeaderBar.getRoot().getLayoutParams();
        int headerBarMargin = layoutParams.topMargin + layoutParams.topMargin / 2;
        int newHeight = binding.mapRootLayout.getHeight() - headerBarHeight - headerBarMargin;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        networkStatus.unregisterNetworkCallback();
    }


    @Override
    public boolean networkAvailable()
    {
        return networkStatus.networkAvailable();
    }

    @Override
    public double getLatitude()
    {
        return mapView.getMapCenterPoint().getMapPointGeoCoord().latitude;
    }

    @Override
    public double getLongitude()
    {
        return mapView.getMapCenterPoint().getMapPointGeoCoord().longitude;
    }

    @Override
    public void setMapVisibility(int visibility)
    {
        binding.mapViewLayout.setVisibility(visibility);
    }

    /*
    @Override
    public void createPlacesPoiItems(List<PlaceDocuments> placeDocuments)
    {
        if (mapView.getPOIItems().length > 0)
        {
            mapView.removeAllPOIItems();
        }
        if (!placeDocuments.isEmpty())
        {
            adapter.setPlaceDocumentsList(placeDocuments);
            adapter.notifyDataSetChanged();

            CustomPoiItem[] poiItems = new CustomPoiItem[placeDocuments.size()];

            int index = 0;
            for (PlaceDocuments document : placeDocuments)
            {
                poiItems[index] = new CustomPoiItem();
                poiItems[index].setItemName(document.getPlaceName());
                poiItems[index].setMapPoint(MapPoint.mapPointWithGeoCoord(document.getY(), document.getX()));
                poiItems[index].setKakaoLocalDocument(document);
                poiItems[index].setTag(index);
                poiItems[index].setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                poiItems[index].setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                index++;
            }
            mapView.addPOIItems(poiItems);
        }

    }

    @Override
    public void createAddressesPoiItems(List<AddressResponseDocuments> addressDocuments)
    {
        if (mapView.getPOIItems().length > 0)
        {
            mapView.removeAllPOIItems();
        }

        if (!addressDocuments.isEmpty())
        {
            adapter.setPlaceDocumentsList(addressDocuments);
            adapter.notifyDataSetChanged();

            CustomPoiItem[] poiItems = new CustomPoiItem[addressDocuments.size()];

            int index = 0;
            for (AddressResponseDocuments document : addressDocuments)
            {
                poiItems[index] = new CustomPoiItem();
                poiItems[index].setItemName(document.getAddressName());
                poiItems[index].setMapPoint(MapPoint.mapPointWithGeoCoord(document.getY(), document.getX()));
                poiItems[index].setKakaoLocalDocument(document);
                poiItems[index].setTag(index);
                poiItems[index].setMarkerType(MapPOIItem.MarkerType.BluePin);
                poiItems[index].setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
                index++;
            }
            mapView.addPOIItems(poiItems);
        }
    }


     */


    @Override
    public void createPoiItems(List<? extends KakaoLocalDocument> kakaoLocalDocuments)
    {

    }

    @Override
    public void addPoiItems(List<? extends KakaoLocalDocument> kakaoLocalDocuments)
    {

    }

    @Override
    public void selectPoiItem(int index)
    {
        onPOIItemSelectedByList(index);
    }


    @Override
    public void removeAllPoiItems()
    {
        mapView.removeAllPOIItems();
    }

    @Override
    public void showAllPoiItems()
    {
        mapView.fitMapViewAreaToShowAllPOIItems();
    }

    @Override
    public void deselectPoiItem()
    {
        mapView.deselectPOIItem(mapView.getPOIItems()[selectedPoiItemIndex]);
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
        return mapView.getPOIItems().length;
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

    @Override
    public void onMapViewInitialized(MapView mapView)
    {
        //바텀 시트의 상태에 따라서 맵의 카메라 되기 위해 이동해야할 Y값
        final int bottomSheetTopY = binding.mapViewLayout.getHeight() - buildingBottomSheet.getHeight();
        final int mapHeaderBarBottomY = binding.mapHeaderBar.getRoot().getBottom();
        final int SIZE_BETWEEN_HEADER_BAR_BOTTOM_AND_BOTTOM_SHEET_TOP = bottomSheetTopY - mapHeaderBarBottomY;
        mapTranslationYByBuildingBottomSheet = (mapView.getMapCenterPoint().getMapPointScreenLocation().y - (mapHeaderBarBottomY +
                SIZE_BETWEEN_HEADER_BAR_BOTTOM_AND_BOTTOM_SHEET_TOP / 2.0));

        ApplicationInfo ai = null;
        try
        {
            ai = getActivity().getPackageManager().getApplicationInfo(getActivity().getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        appKey = ai.metaData.getString("com.kakao.sdk.AppKey");

        mapReverseGeoCoder = new MapReverseGeoCoder(appKey, mapView.getMapCenterPoint(), this, requireActivity());
        mapReverseGeoCoder.startFindingAddress(MapReverseGeoCoder.AddressType.ShortAddress);
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint)
    {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i)
    {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint)
    {
        if (isSelectedPoiItem)
        {
            deselectPoiItem();
            placeListBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint)
    {

    }

    /*
    롱 클릭한 부분의 위치 정보를 표시
     */
    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint)
    {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint)
    {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint)
    {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint)
    {
        if (networkAvailable())
        {
            if (getActivity() != null)
            {
                mapReverseGeoCoder = new MapReverseGeoCoder(appKey, mapPoint, this, getActivity());
                mapReverseGeoCoder.startFindingAddress(MapReverseGeoCoder.AddressType.ShortAddress);
            }
        }
    }

    public LocationDTO getSelectedLocationDto(int calendarId, long eventId)
    {
        // 선택된 poiitem의 리스트내 인덱스를 가져온다.
        MapPOIItem[] poiItems = mapView.getPOIItems();
        // 인덱스로 아이템을 가져온다.
        CustomPoiItem item = (CustomPoiItem) poiItems[selectedPoiItemIndex];

        LocationDTO location = new LocationDTO();
        location.setCalendarId(calendarId);
        location.setEventId(eventId);

        // 주소인지 장소인지를 구분한다.
        if (item.getKakaoLocalDocument() instanceof PlaceDocuments)
        {
            PlaceDocuments placeDocuments = (PlaceDocuments) item.getKakaoLocalDocument();
            location.setPlaceId(placeDocuments.getId());
            location.setPlaceName(placeDocuments.getPlaceName());
            location.setLatitude(placeDocuments.getY());
            location.setLongitude(placeDocuments.getX());
            location.setLocationType(LocationType.PLACE);
        } else
        {
            AddressResponseDocuments addressDocuments = (AddressResponseDocuments) item.getKakaoLocalDocument();

            location.setAddressName(addressDocuments.getAddressName());
            location.setLatitude(addressDocuments.getY());
            location.setLongitude(addressDocuments.getX());
            location.setLocationType(LocationType.ADDRESS);
        }
        return location;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_LOCATION)
        {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                // 권한 허용됨
                gpsButton.callOnClick();
            } else
            {
                // 권한 거부됨
            }
        }
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem)
    {
        onPOIItemSelectedByTouch(mapPOIItem);
    }

    @Override
    public void onPOIItemSelectedByTouch(MapPOIItem e)
    {
        //poiitem을 직접 선택한 경우 호출
        selectedPoiItemIndex = e.getTag();
        isSelectedPoiItem = true;

        mapView.setMapCenterPoint(e.getMapPoint(), true);
        //open bottomsheet and show selected item data
        placeListBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        placesBottomSheetViewPager.setCurrentItem(selectedPoiItemIndex, false);
    }

    @Override
    public void onPOIItemSelectedByList(int index)
    {
        //bottomsheet가 아닌 list에서 아이템을 선택한 경우 호출
        //adapter -> poiitem생성 -> select poiitem -> bottomsheet열고 정보 표시
        MapPOIItem mapPOIItem = mapView.getPOIItems()[index];
        mapView.selectPOIItem(mapPOIItem, true);

        selectedPoiItemIndex = mapPOIItem.getTag();
        isSelectedPoiItem = true;

        mapView.setMapCenterPoint(mapPOIItem.getMapPoint(), true);
        //open bottomsheet and show selected item data
        placeListBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        placesBottomSheetViewPager.setCurrentItem(selectedPoiItemIndex, false);
    }

    @Override
    public void onPOIItemSelectedByBottomSheet(int index)
    {
        //bottomsheet에서 스크롤 하는 경우 호출
        MapPOIItem mapPOIItem = mapView.getPOIItems()[index];
        mapView.selectPOIItem(mapPOIItem, true);

        selectedPoiItemIndex = mapPOIItem.getTag();
        isSelectedPoiItem = true;

        mapView.setMapCenterPoint(mapPOIItem.getMapPoint(), true);
        //open bottomsheet and show selected item data
        placeListBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem)
    {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem
            mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType)
    {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint
            mapPoint)
    {

    }

    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String
            address)
    {
        binding.mapButtonsLayout.currentAddress.setText(address);
    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder)
    {
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
            LocationSearchResultFragment locationSearchResultFragment = new LocationSearchResultFragment(query, KakaoMapFragment.this, mapHeaderSearchFragment);

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
    public void closeSearchFragments()
    {

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
        if (binding.mapViewLayout.findViewWithTag("BUILDING_SELECTOR") != null)
        {
            buildingButton.setImageDrawable(getContext().getDrawable(R.drawable.building_black));
            binding.mapViewLayout.removeView(binding.mapViewLayout.findViewWithTag("BUILDING_SELECTOR"));
        }
    }

    @Override
    public void showBuildingLocationSelector()
    {
        if (binding.mapViewLayout.findViewWithTag("BUILDING_SELECTOR") == null)
        {
            buildingButton.setImageDrawable(getContext().getDrawable(R.drawable.building_blue));
            //드래그로 이동가능한 마커 생성
            View selectorView = getLayoutInflater().inflate(R.layout.building_location_selector_view, null);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            selectorView.setLayoutParams(layoutParams);
            selectorView.setTag("BUILDING_SELECTOR");

            binding.mapViewLayout.addView(selectorView);

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
                    MapPoint mapPoint = mapView.getMapCenterPoint();

                    String centerLatitude = String.valueOf(mapPoint.getMapPointGeoCoord().latitude);
                    String centerLongitude = String.valueOf(mapPoint.getMapPointGeoCoord().longitude);

                    BuildingListFragment buildingListFragment = new BuildingListFragment(KakaoMapFragment.this);
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
            mapView.removeCircle(mapView.getCircles()[0]);
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

            mapView.removeCircle(mapView.getCircles()[0]);
            buildingBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

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
        MapPoint mapPoint = mapView.getMapCenterPoint();
        if (mapView.findCircleByTag(BUILDING_RANGE_OVERLAY_TAG) != null)
        {
            mapPoint = mapView.findCircleByTag(BUILDING_RANGE_OVERLAY_TAG).getCenter();
            mapView.removeCircle(mapView.findCircleByTag(BUILDING_RANGE_OVERLAY_TAG));
        }

        MapCircle circle = new MapCircle(
                mapPoint, Integer.parseInt(App.getPreference_key_range_meter_for_search_buildings()),
                Color.argb(128, 255, 0, 0), // strokeColor
                Color.argb(128, 0, 255, 0) // fillColor
        );
        circle.setTag(BUILDING_RANGE_OVERLAY_TAG);
        mapView.addCircle(circle);
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
        buildingBottomSheetBehavior.onLayoutChild(binding.mapRootLayout, buildingBottomSheet, ViewCompat.LAYOUT_DIRECTION_LTR);
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
