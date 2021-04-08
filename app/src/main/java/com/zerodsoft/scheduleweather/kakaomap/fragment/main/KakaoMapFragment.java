package com.zerodsoft.scheduleweather.kakaomap.fragment.main;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.etc.FragmentStateCallback;
import com.zerodsoft.scheduleweather.kakaomap.bottomsheet.adapter.PlaceItemInMapViewAdapter;
import com.zerodsoft.scheduleweather.event.places.interfaces.PoiItemOnClickListener;
import com.zerodsoft.scheduleweather.kakaomap.fragment.search.LocationSearchFragment;
import com.zerodsoft.scheduleweather.kakaomap.fragment.searchheader.MapHeaderMainFragment;
import com.zerodsoft.scheduleweather.kakaomap.fragment.searchheader.MapHeaderSearchFragment;
import com.zerodsoft.scheduleweather.kakaomap.fragment.searchresult.LocationSearchResultFragment;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.OnClickedBottomSheetListener;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.PlacesListBottomSheetController;
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
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;
import com.zerodsoft.scheduleweather.utility.NetworkStatus;

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
        PlacesListBottomSheetController, PoiItemOnClickListener, OnClickedBottomSheetListener,
        MapHeaderSearchFragment.LocationSearchListener, SearchFragmentController
{
    public static final int REQUEST_CODE_LOCATION = 10000;

    public PlacesItemBottomSheetButtonOnClickListener placesItemBottomSheetButtonOnClickListener;
    public FragmentMapBinding binding;
    public MapView mapView;

    private String appKey;
    public MapReverseGeoCoder mapReverseGeoCoder;
    public LocationManager locationManager;

    public ImageButton zoomInButton;
    public ImageButton zoomOutButton;
    public ImageButton gpsButton;

    public int selectedPoiItemIndex;
    public boolean isSelectedPoiItem;

    public NetworkStatus networkStatus;

    public LinearLayout placesListBottomSheet;
    public BottomSheetBehavior placeListBottomSheetBehavior;

    public LinearLayout locationSearchBottomSheet;
    public BottomSheetBehavior locationSearchBottomSheetBehavior;

    public ViewPager2 placesBottomSheetViewPager;
    public PlaceItemInMapViewAdapter adapter;

    public int placeBottomSheetSelectBtnVisibility;
    public int placeBottomSheetUnSelectBtnVisibility;

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

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        zoomInButton = binding.mapButtonsLayout.zoomInButton;
        zoomOutButton = binding.mapButtonsLayout.zoomOutButton;
        gpsButton = binding.mapButtonsLayout.gpsButton;

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(binding.mapHeaderBar.headerFragmentContainer.getId(), new MapHeaderMainFragment(), MapHeaderMainFragment.TAG).commitNow();

        binding.mapHeaderBar.getRoot().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int headerBarHeight = binding.mapHeaderBar.getRoot().getHeight();
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) binding.mapHeaderBar.getRoot().getLayoutParams();
                int headerBarMargin = layoutParams.topMargin + layoutParams.topMargin / 2;
                int newHeight = binding.mapRootLayout.getHeight() - headerBarHeight - headerBarMargin;

                locationSearchBottomSheet.getLayoutParams().height = newHeight;
                locationSearchBottomSheet.requestLayout();
                locationSearchBottomSheetBehavior.onLayoutChild(binding.mapRootLayout, locationSearchBottomSheet, ViewCompat.LAYOUT_DIRECTION_LTR);

                FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

                MapHeaderSearchFragment mapHeaderSearchFragment = new MapHeaderSearchFragment(KakaoMapFragment.this);
                LocationSearchFragment locationSearchFragment = new LocationSearchFragment(KakaoMapFragment.this, mapHeaderSearchFragment,
                        new FragmentStateCallback()
                        {
                            @Override
                            public void onChangedState(int state)
                            {
                                if (state == FragmentStateCallback.ON_REMOVED)
                                {
                                    closeSearchFragments();
                                }
                            }
                        });
                mapHeaderSearchFragment.setSearchHistoryDataController(locationSearchFragment);


                fragmentTransaction.remove(getChildFragmentManager().findFragmentByTag(MapHeaderMainFragment.TAG))
                        .add(binding.mapHeaderBar.headerFragmentContainer.getId(), mapHeaderSearchFragment, MapHeaderSearchFragment.TAG)
                        .add(binding.locationSearchBottomSheet.searchFragmentContainer.getId(), locationSearchFragment, LocationSearchFragment.TAG)
                        .commit();

                locationSearchBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
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

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset)
            {
                //expanded일때 offset == 1.0, collapsed일때 offset == 0.0
                //offset에 따라서 버튼들이 이동하고, 지도의 좌표가 변경되어야 한다.
                float height = binding.mapButtonsLayout.getRoot().getHeight();
                float translationValue = -height * slideOffset;

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
        return networkStatus.networkAvailable(getActivity());
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
        } else
        {
            AddressResponseDocuments addressDocuments = (AddressResponseDocuments) item.getKakaoLocalDocument();

            location.setAddressName(addressDocuments.getAddressName());
            location.setLatitude(addressDocuments.getY());
            location.setLongitude(addressDocuments.getX());
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
        onPOIItemSelectedByTouch(mapView, mapPOIItem);
    }

    @Override
    public void onPOIItemSelectedByTouch(MapView mapView, MapPOIItem mapPOIItem)
    {
        //poiitem을 직접 선택한 경우 호출
        selectedPoiItemIndex = mapPOIItem.getTag();
        isSelectedPoiItem = true;

        mapView.setMapCenterPoint(mapPOIItem.getMapPoint(), true);
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
        if (LocationSearchResultFragment.getInstance() == null)
        {
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            MapHeaderSearchFragment mapHeaderSearchFragment = (MapHeaderSearchFragment) getChildFragmentManager().findFragmentByTag(MapHeaderSearchFragment.TAG);

            fragmentTransaction.add(binding.locationSearchBottomSheet.searchFragmentContainer.getId()
                    , LocationSearchResultFragment.newInstance(query, KakaoMapFragment.this, mapHeaderSearchFragment)
                    , LocationSearchResultFragment.TAG).hide(getChildFragmentManager().findFragmentByTag(LocationSearchFragment.TAG))
                    .addToBackStack(LocationSearchResultFragment.TAG).commit();
        } else
        {
            LocationSearchResultFragment.getInstance().search(query);
        }
    }

    public void closeSearchFragments()
    {
        locationSearchBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        MapHeaderMainFragment mapHeaderMainFragment = new MapHeaderMainFragment();
        fragmentTransaction.remove(getChildFragmentManager().findFragmentByTag(MapHeaderSearchFragment.TAG))
                .add(binding.mapHeaderBar.headerFragmentContainer.getId(), mapHeaderMainFragment, MapHeaderMainFragment.TAG);

        if (getChildFragmentManager().findFragmentByTag(LocationSearchFragment.TAG) != null)
        {
            fragmentTransaction.remove(getChildFragmentManager().findFragmentByTag(LocationSearchFragment.TAG));
        }

        if (getChildFragmentManager().findFragmentByTag(LocationSearchResultFragment.TAG) != null)
        {
            fragmentTransaction.remove(getChildFragmentManager().findFragmentByTag(LocationSearchResultFragment.TAG));
        }

        fragmentTransaction.commit();
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
}
