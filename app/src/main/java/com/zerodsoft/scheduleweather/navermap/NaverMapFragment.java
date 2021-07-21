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
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.provider.Settings;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.util.Xml;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapOptions;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.Projection;
import com.naver.maps.map.overlay.CircleOverlay;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.CameraUtils;
import com.naver.maps.map.util.FusedLocationSource;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.common.classes.AppPermission;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.databinding.FragmentNaverMapBinding;
import com.zerodsoft.scheduleweather.etc.LocationType;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.foods.favorite.restaurant.FavoriteLocationViewModel;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnClickedFavoriteButtonListener;
import com.zerodsoft.scheduleweather.event.places.interfaces.MarkerOnClickListener;
import com.zerodsoft.scheduleweather.kakaoplace.retrofit.KakaoLocalDownloader;
import com.zerodsoft.scheduleweather.navermap.adapter.FavoriteLocationItemViewPagerAdapter;
import com.zerodsoft.scheduleweather.navermap.adapter.LocationItemViewPagerAbstractAdapter;
import com.zerodsoft.scheduleweather.navermap.adapter.LocationItemViewPagerAdapter;
import com.zerodsoft.scheduleweather.navermap.adapter.OnLongClickMapLocationItemAdapter;
import com.zerodsoft.scheduleweather.navermap.building.fragment.BuildingFragment;
import com.zerodsoft.scheduleweather.navermap.building.fragment.BuildingListFragment;
import com.zerodsoft.scheduleweather.navermap.favorite.FavoriteLocationFragment;
import com.zerodsoft.scheduleweather.navermap.interfaces.ILoadLocationData;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnKakaoLocalApiCallback;
import com.zerodsoft.scheduleweather.navermap.search.LocationSearchFragment;
import com.zerodsoft.scheduleweather.navermap.searchheader.MapHeaderMainFragment;
import com.zerodsoft.scheduleweather.navermap.searchheader.MapHeaderSearchFragment;
import com.zerodsoft.scheduleweather.navermap.searchresult.LocationSearchResultFragment;
import com.zerodsoft.scheduleweather.navermap.interfaces.BottomSheetController;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapData;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.navermap.interfaces.INetwork;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnClickedBottomSheetListener;
import com.zerodsoft.scheduleweather.navermap.interfaces.PlacesItemBottomSheetButtonOnClickListener;
import com.zerodsoft.scheduleweather.navermap.place.PlaceInfoWebDialogFragment;
import com.zerodsoft.scheduleweather.kakaoplace.LocalParameterUtil;
import com.zerodsoft.scheduleweather.navermap.viewmodel.MapSharedViewModel;
import com.zerodsoft.scheduleweather.navermap.viewmodel.SearchHistoryViewModel;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddressDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoregioncoderesponse.CoordToRegionCode;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.interfaces.FavoriteLocationQuery;
import com.zerodsoft.scheduleweather.utility.NetworkStatus;

import org.jetbrains.annotations.NotNull;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NaverMapFragment extends Fragment implements OnMapReadyCallback, IMapPoint, IMapData, INetwork, PlacesItemBottomSheetButtonOnClickListener,
		MarkerOnClickListener, OnClickedBottomSheetListener,
		NaverMap.OnMapClickListener,
		NaverMap.OnCameraIdleListener, CameraUpdate.FinishCallback, NaverMap.OnLocationChangeListener,
		FragmentManager.OnBackStackChangedListener, BottomSheetController, NaverMap.OnMapLongClickListener,
		OnClickedFavoriteButtonListener {
	public static final int PERMISSION_REQUEST_CODE = 100;
	public static final int REQUEST_CODE_LOCATION = 10000;
	public static final int BUILDING_RANGE_OVERLAY_TAG = 1500;

	protected Integer DEFAULT_HEIGHT_OF_BOTTOMSHEET;

	private FusedLocationSource fusedLocationSource;
	private LocationManager locationManager;

	public FragmentNaverMapBinding binding;
	public MapFragment mapFragment;
	public NaverMap naverMap;

	public LocationViewModel locationViewModel;
	public SearchHistoryViewModel searchHistoryViewModel;
	public MapSharedViewModel mapSharedViewModel;
	public FavoriteLocationViewModel favoriteLocationViewModel;

	public ImageButton zoomInButton;
	public ImageButton zoomOutButton;
	public ImageButton gpsButton;
	public ImageButton buildingButton;
	public ImageButton favoriteLocationsButton;

	public NetworkStatus networkStatus;

	public int placeBottomSheetSelectBtnVisibility = View.GONE;
	public int placeBottomSheetUnSelectBtnVisibility = View.GONE;

	private Integer markerWidth;
	private Integer markerHeight;
	private Integer favoriteMarkerSize;

	public ViewPager2 locationItemBottomSheetViewPager;

	protected AlertDialog loadingDialog;

	final public Map<BottomSheetType, BottomSheetBehavior> bottomSheetBehaviorMap = new HashMap<>();
	final public Map<BottomSheetType, Fragment> bottomSheetFragmentMap = new HashMap<>();
	final public Map<BottomSheetType, LinearLayout> bottomSheetViewMap = new HashMap<>();

	final public Map<MarkerType, List<Marker>> markersMap = new HashMap<>();
	final public Map<MarkerType, LocationItemViewPagerAbstractAdapter> viewPagerAdapterMap = new HashMap<>();

	protected final FragmentManager.FragmentLifecycleCallbacks fragmentLifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() {
		@Override
		public void onFragmentAttached(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f, @NonNull @NotNull Context context) {
			super.onFragmentAttached(fm, f, context);
			if (f instanceof BuildingFragment) {
				BuildingBottomSheetHeightViewHolder buildingBottomSheetHeightViewHolder
						= (BuildingBottomSheetHeightViewHolder) bottomSheetViewMap.get(BottomSheetType.BUILDING).getTag();
				setHeightOfBottomSheetForSpecificFragment(getString(R.string.tag_building_info_fragment), BottomSheetType.BUILDING,
						buildingBottomSheetHeightViewHolder.infoHeight);
			}
		}

		@Override
		public void onFragmentCreated(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
			super.onFragmentCreated(fm, f, savedInstanceState);
			if (f instanceof MapHeaderSearchFragment) {
				binding.headerFragmentContainer.getLayoutParams().height =
						(int) getResources().getDimension(R.dimen.map_header_search_bar_height);
				binding.headerFragmentContainer.requestLayout();
				binding.headerFragmentContainer.invalidate();

				binding.naverMapButtonsLayout.favoriteLocationsButton.setVisibility(View.GONE);
				binding.naverMapButtonsLayout.buildingButton.setVisibility(View.GONE);
			} else if (f instanceof BuildingListFragment) {
				setStateOfBottomSheet(BottomSheetType.BUILDING, BottomSheetBehavior.STATE_EXPANDED);
			}
		}

		@Override
		public void onFragmentDestroyed(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f) {
			super.onFragmentDestroyed(fm, f);
			if (f instanceof MapHeaderSearchFragment) {
				binding.headerFragmentContainer.getLayoutParams().height =
						(int) getResources().getDimension(R.dimen.map_header_bar_height);
				binding.headerFragmentContainer.requestLayout();
				binding.headerFragmentContainer.invalidate();

				binding.naverMapButtonsLayout.favoriteLocationsButton.setVisibility(View.VISIBLE);
				binding.naverMapButtonsLayout.buildingButton.setVisibility(View.VISIBLE);
			} else if (f instanceof BuildingListFragment) {
				buildingButton.setImageDrawable(ContextCompat.getDrawable(getContext(),
						R.drawable.building_black));
				setStateOfBottomSheet(BottomSheetType.BUILDING, BottomSheetBehavior.STATE_COLLAPSED);
				bottomSheetFragmentMap.remove(BottomSheetType.BUILDING);
				binding.naverMapButtonsLayout.favoriteLocationsButton.setVisibility(View.VISIBLE);
			} else if (f instanceof BuildingFragment) {
				BuildingBottomSheetHeightViewHolder buildingBottomSheetHeightViewHolder
						= (BuildingBottomSheetHeightViewHolder) bottomSheetViewMap.get(BottomSheetType.BUILDING).getTag();
				setHeightOfBottomSheetForSpecificFragment(getString(R.string.tag_building_list_fragment), BottomSheetType.BUILDING,
						buildingBottomSheetHeightViewHolder.listHeight);
			}
		}
	};


	public final LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			if (getActivity() != null) {
				CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(location.getLatitude(), location.getLongitude()));
				naverMap.moveCamera(cameraUpdate);
				setCurrentAddress();
				locationManager.removeUpdates(locationListener);
			}
		}

		@Override
		public void onStatusChanged(String s, int i, Bundle bundle) {

		}

		@Override
		public void onProviderEnabled(String s) {

		}

		@Override
		public void onProviderDisabled(String s) {

		}
	};

	@Override
	public void onAttach(@NonNull @NotNull Context context) {
		super.onAttach(context);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		favoriteLocationViewModel = new ViewModelProvider(this).get(FavoriteLocationViewModel.class);
		getChildFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, false);

		mapSharedViewModel = new ViewModelProvider(this).get(MapSharedViewModel.class);
		mapSharedViewModel.setBottomSheetController(this);
		mapSharedViewModel.setiMapData(this);
		mapSharedViewModel.setiMapPoint(this);
		mapSharedViewModel.setPoiItemOnClickListener(this);

		locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
		locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
		searchHistoryViewModel = new ViewModelProvider(this).get(SearchHistoryViewModel.class);

		networkStatus = new NetworkStatus(getContext(), new ConnectivityManager.NetworkCallback() {
			@Override
			public void onAvailable(@NonNull Network network) {
				super.onAvailable(network);
			}

			@Override
			public void onLost(@NonNull Network network) {
				super.onLost(network);
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						networkStatus.showToastDisconnected();
						getParentFragmentManager().popBackStackImmediate();
					}
				});
			}
		});

		CircularProgressIndicator circularProgressIndicator = new CircularProgressIndicator(getActivity());
		circularProgressIndicator.setIndeterminate(true);

		FrameLayout.LayoutParams progressBarLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		progressBarLayoutParams.gravity = Gravity.CENTER;

		FrameLayout loadingRootLayout = new FrameLayout(getContext());
		loadingRootLayout.addView(circularProgressIndicator, progressBarLayoutParams);

		loadingDialog = new MaterialAlertDialogBuilder(getActivity())
				.setView(loadingRootLayout).setCancelable(false).create();
		loadingDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

		markerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, getResources().getDisplayMetrics());
		markerHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32f, getResources().getDisplayMetrics());
		favoriteMarkerSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, getResources().getDisplayMetrics());


	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentNaverMapBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		setLocationItemsBottomSheet();
		setLocationSearchBottomSheet();
		setBuildingBottomSheet();
		setFavoriteLocationsBottomSheet();

		zoomInButton = binding.naverMapButtonsLayout.zoomInButton;
		zoomOutButton = binding.naverMapButtonsLayout.zoomOutButton;
		gpsButton = binding.naverMapButtonsLayout.gpsButton;
		buildingButton = binding.naverMapButtonsLayout.buildingButton;
		favoriteLocationsButton = binding.naverMapButtonsLayout.favoriteLocationsButton;

		binding.naverMapFragmentRootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				//search bottom sheet 크기 조정
				final int headerBarHeight = (int) getResources().getDimension(R.dimen.map_header_search_bar_height);
				final int headerBarTopMargin = (int) getResources().getDimension(R.dimen.map_header_bar_top_margin);
				final int headerBarMargin = (int) (headerBarTopMargin * 1.5f);

				DEFAULT_HEIGHT_OF_BOTTOMSHEET = binding.naverMapFragmentRootLayout.getHeight() - (int) getResources().getDimension(R.dimen.map_header_bar_height) - headerBarMargin;

				final int searchBottomSheetHeight = binding.naverMapFragmentRootLayout.getHeight() - headerBarHeight - headerBarMargin;

				LinearLayout locationSearchBottomSheet = bottomSheetViewMap.get(BottomSheetType.SEARCH_LOCATION);

				locationSearchBottomSheet.getLayoutParams().height = searchBottomSheetHeight;
				locationSearchBottomSheet.requestLayout();

				BottomSheetBehavior locationSearchBottomSheetBehavior = bottomSheetBehaviorMap.get(BottomSheetType.SEARCH_LOCATION);
				locationSearchBottomSheetBehavior.onLayoutChild(binding.naverMapFragmentRootLayout, locationSearchBottomSheet, ViewCompat.LAYOUT_DIRECTION_LTR);

				//building list bottom sheet 크기 조정 --------------------------------------------------------------
				int buildingBottomSheetExtraHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60f, getContext().getResources().getDisplayMetrics());

				//list 프래그먼트와 빌딩 정보 프래그먼트 두 개의 높이를 다르게 설정
				final int buildingListHeight = binding.naverMapFragmentRootLayout.getHeight() / 2 + buildingBottomSheetExtraHeight;
				final int buildingInfoHeight = searchBottomSheetHeight;
				BuildingBottomSheetHeightViewHolder buildingBottomSheetHeightViewHolder = new BuildingBottomSheetHeightViewHolder(buildingListHeight, buildingInfoHeight);

				LinearLayout buildingBottomSheet = bottomSheetViewMap.get(BottomSheetType.BUILDING);

				buildingBottomSheet.setTag(buildingBottomSheetHeightViewHolder);

				buildingBottomSheet.getLayoutParams().height = buildingListHeight;
				buildingBottomSheet.requestLayout();

				BottomSheetBehavior buildingBottomSheetBehavior = bottomSheetBehaviorMap.get(BottomSheetType.BUILDING);
				buildingBottomSheetBehavior.onLayoutChild(binding.naverMapFragmentRootLayout, buildingBottomSheet, ViewCompat.LAYOUT_DIRECTION_LTR);

				//favorite locations bottom sheet 크기 조정 ---------------------------------------------------------------
				final int favoriteLocationsHeight = buildingListHeight;
				LinearLayout favoriteLocationsBottomSheet = bottomSheetViewMap.get(BottomSheetType.FAVORITE_LOCATIONS);

				favoriteLocationsBottomSheet.getLayoutParams().height = favoriteLocationsHeight;
				favoriteLocationsBottomSheet.requestLayout();

				BottomSheetBehavior favoriteLocationsBottomSheetBehavior = bottomSheetBehaviorMap.get(BottomSheetType.FAVORITE_LOCATIONS);
				favoriteLocationsBottomSheetBehavior.onLayoutChild(binding.naverMapFragmentRootLayout, favoriteLocationsBottomSheet, ViewCompat.LAYOUT_DIRECTION_LTR);

				binding.naverMapFragmentRootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
			}
		});

		binding.headerFragmentContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (getChildFragmentManager().findFragmentByTag(getString(R.string.tag_map_search_main_fragment)) != null) {
					//expand search location bottomsheet
					collapseAllExpandedBottomSheets();

					FragmentManager childFragmentManager = getChildFragmentManager();
					int backStackCount = childFragmentManager.getBackStackEntryCount();
					for (int count = 0; count < backStackCount; count++) {
						childFragmentManager.popBackStackImmediate();
					}

					LocationSearchFragment locationSearchFragment =
							new LocationSearchFragment();
					MapHeaderSearchFragment mapHeaderSearchFragment = new MapHeaderSearchFragment();

					final String tag = getString(R.string.tag_location_search_fragment);

					childFragmentManager.beginTransaction().replace(binding.headerFragmentContainer.getId(), mapHeaderSearchFragment,
							getString(R.string.tag_map_header_search_fragment))
							.add(binding.locationSearchBottomSheet.searchFragmentContainer.getId(), locationSearchFragment,
									tag)
							.addToBackStack(tag)
							.commit();

					setStateOfBottomSheet(BottomSheetType.SEARCH_LOCATION, BottomSheetBehavior.STATE_EXPANDED);
				}
			}
		});

		favoriteLocationsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fragmentManager = getChildFragmentManager();

				if (bottomSheetFragmentMap.containsKey(BottomSheetType.FAVORITE_LOCATIONS)) {
					Fragment favoriteLocationFragment = bottomSheetFragmentMap.get(BottomSheetType.FAVORITE_LOCATIONS);

					if (favoriteLocationFragment.isVisible()) {
						fragmentManager.popBackStackImmediate();
					} else {
						fragmentManager.beginTransaction().show(favoriteLocationFragment).addToBackStack(getString(R.string.tag_favorite_locations_fragment)).commit();
						setStateOfBottomSheet(BottomSheetType.FAVORITE_LOCATIONS, BottomSheetBehavior.STATE_EXPANDED);
					}
				} else {
					FavoriteLocationFragment favoriteLocationFragment
							= new FavoriteLocationFragment();
					bottomSheetFragmentMap.put(BottomSheetType.FAVORITE_LOCATIONS, favoriteLocationFragment);

					fragmentManager.beginTransaction()
							.add(binding.favoriteLocationsBottomSheet.fragmentContainerView.getId()
									, favoriteLocationFragment, getString(R.string.tag_favorite_locations_fragment))
							.commitNow();
					fragmentManager.beginTransaction().show(favoriteLocationFragment).addToBackStack(getString(R.string.tag_favorite_locations_fragment)).commit();
					setStateOfBottomSheet(BottomSheetType.FAVORITE_LOCATIONS, BottomSheetBehavior.STATE_EXPANDED);
				}
			}
		});

		buildingButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (binding.naverMapViewLayout.findViewWithTag(getString(R.string.tag_building_selector)) == null) {
					if (getChildFragmentManager().findFragmentByTag(getString(R.string.tag_building_list_fragment)) == null) {
						showBuildingLocationSelector();
					} else {
						//remove all building fragments
						FragmentManager fragmentManager = getChildFragmentManager();
						if (fragmentManager.findFragmentByTag(getString(R.string.tag_building_info_fragment)) != null) {
							fragmentManager.popBackStackImmediate();
						}
						fragmentManager.popBackStackImmediate();
					}
				} else {
					removeBuildingLocationSelector();
					buildingButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.building_black));
				}
			}
		});

		zoomInButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				naverMap.moveCamera(CameraUpdate.zoomIn());
			}
		});

		zoomOutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				naverMap.moveCamera(CameraUpdate.zoomOut());
			}
		});

		gpsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (naverMap.getLocationTrackingMode() == LocationTrackingMode.None) {
					//권한 확인
					boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
					boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

					if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
						if (isGpsEnabled) {
							naverMap.setLocationSource(fusedLocationSource);
							naverMap.setLocationTrackingMode(LocationTrackingMode.NoFollow);
						} else {
							showRequestGpsDialog();
						}
					} else {
						naverMap.setLocationSource(null);
						requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION);
					}
				}
			}
		});
		binding.naverMapButtonsLayout.currentAddress.setText("");
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		networkStatus.unregisterNetworkCallback();
		getChildFragmentManager().unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks);
	}

	protected void loadMap() {
		if (mapFragment == null) {
			loadingDialog.show();

			NaverMapOptions naverMapOptions = new NaverMapOptions();
			naverMapOptions.scaleBarEnabled(true).locationButtonEnabled(false).compassEnabled(false).zoomControlEnabled(false).rotateGesturesEnabled(false)
					.mapType(NaverMap.MapType.Basic).camera(new CameraPosition(new LatLng(37.6076585, 127.0965492), 11));

			mapFragment = MapFragment.newInstance(naverMapOptions);
			getChildFragmentManager().beginTransaction().add(R.id.naver_map_fragment, mapFragment, getString(R.string.tag_map_fragment)).commitNow();

			fusedLocationSource = new FusedLocationSource(this, PERMISSION_REQUEST_CODE);
			mapFragment.getMapAsync(this);
		}

	}

	private void setLocationSearchBottomSheet() {
		LinearLayout locationSearchBottomSheet = binding.locationSearchBottomSheet.locationSearchBottomsheet;

		BottomSheetBehavior locationSearchBottomSheetBehavior = BottomSheetBehavior.from(locationSearchBottomSheet);
		locationSearchBottomSheetBehavior.setDraggable(false);
		locationSearchBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

		bottomSheetViewMap.put(BottomSheetType.SEARCH_LOCATION, locationSearchBottomSheet);
		bottomSheetBehaviorMap.put(BottomSheetType.SEARCH_LOCATION, locationSearchBottomSheetBehavior);
		getChildFragmentManager().beginTransaction()
				.add(binding.headerFragmentContainer.getId(), new MapHeaderMainFragment(), getString(R.string.tag_map_search_main_fragment))
				.commit();
	}

	private void setBuildingBottomSheet() {
		LinearLayout buildingBottomSheet = (LinearLayout) binding.buildingBottomSheet.buildingBottomsheet;

		BottomSheetBehavior buildingBottomSheetBehavior = BottomSheetBehavior.from(buildingBottomSheet);
		buildingBottomSheetBehavior.setDraggable(false);
		buildingBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
		buildingBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
			boolean initializing = true;
			boolean firstInitializing = true;
			float mapTranslationYByBuildingBottomSheet;

			@Override
			public void onStateChanged(@NonNull View bottomSheet, int newState) {
				//바텀 시트의 상태에 따라서 카메라를 이동시킬 Y값
				if (firstInitializing) {
					firstInitializing = false;

					final int bottomSheetTopY = binding.naverMapViewLayout.getHeight() - bottomSheetViewMap.get(BottomSheetType.BUILDING).getHeight();
					final int mapHeaderBarBottomY = binding.headerFragmentContainer.getBottom();
					final int SIZE_BETWEEN_HEADER_BAR_BOTTOM_AND_BOTTOM_SHEET_TOP = bottomSheetTopY - mapHeaderBarBottomY;

					Projection projection = naverMap.getProjection();
					PointF point = projection.toScreenLocation(naverMap.getContentBounds().getCenter());

					mapTranslationYByBuildingBottomSheet = (float) (point.y - (mapHeaderBarBottomY +
							SIZE_BETWEEN_HEADER_BAR_BOTTOM_AND_BOTTOM_SHEET_TOP / 2.0));
				}

				switch (newState) {
					case BottomSheetBehavior.STATE_EXPANDED: {
                       /*
                       <지도 카메라 위치 이동 방법>
                       MapView.getMapCenterPoint() 메소드로 지도 중심 좌표(MapPoint center)를 얻습니다.
                        중심 좌표 객체의 center.getMapPointScreenLocation() 메소드를 통해 pixel 좌표값(MapPoint.PlainCoordinate pixel)을 얻어냅니다.
                        그 pixel 좌표값으로부터 얼마나 이동시키면 될 지 계산합니다. 앞서 구한 pixel에 이동하고자 하는 offset을 더하여 tx, ty 값을 확보합니다.
                        (double tx = pixel.x + offsetX, double ty = pixel.y + offsetY)
                        MapPoint newCenter = MapPoint.mapPointWithScreenLocation(tx, ty) 정적 메소드로 입력한 스크린 좌표를 역변환 하여 지도상 좌표(newCenter)를 구합니다.
                        MapView.setMapCenterPoint(newCenter, true) 메소드로 지도 중심을 이동시킵니다.
                        */
						if (initializing) {
							PointF movePoint = new PointF(0f, -mapTranslationYByBuildingBottomSheet);
							CameraUpdate cameraUpdate = CameraUpdate.scrollBy(movePoint);
							naverMap.moveCamera(cameraUpdate);
							initializing = false;
						}
						break;
					}
					case BottomSheetBehavior.STATE_COLLAPSED: {
						PointF movePoint = new PointF(0f, mapTranslationYByBuildingBottomSheet);
						CameraUpdate cameraUpdate = CameraUpdate.scrollBy(movePoint);
						naverMap.moveCamera(cameraUpdate);
						initializing = true;
						break;
					}
				}
			}

			@Override
			public void onSlide(@NonNull View bottomSheet, float slideOffset) {
				//expanded일때 offset == 1.0, collapsed일때 offset == 0.0
				//offset에 따라서 버튼들이 이동하고, 지도의 좌표가 변경되어야 한다.
				float translationValue = -buildingBottomSheet.getHeight() * slideOffset;
				binding.naverMapButtonsLayout.getRoot().animate().translationY(translationValue);
			}
		});

		bottomSheetViewMap.put(BottomSheetType.BUILDING, buildingBottomSheet);
		bottomSheetBehaviorMap.put(BottomSheetType.BUILDING, buildingBottomSheetBehavior);
	}

	private void setFavoriteLocationsBottomSheet() {
		LinearLayout favoriteLocationsBottomSheet = (LinearLayout) binding.favoriteLocationsBottomSheet.persistentBottomSheetRootLayout;

		BottomSheetBehavior favoriteLocationsBottomSheetBehavior = BottomSheetBehavior.from(favoriteLocationsBottomSheet);
		favoriteLocationsBottomSheetBehavior.setDraggable(false);
		favoriteLocationsBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
		favoriteLocationsBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
			@Override
			public void onStateChanged(@NonNull View bottomSheet, int newState) {
			}

			@Override
			public void onSlide(@NonNull View bottomSheet, float slideOffset) {
				float translationValue = -favoriteLocationsBottomSheet.getHeight() * slideOffset;
				binding.naverMapButtonsLayout.getRoot().animate().translationY(translationValue);
			}
		});

		bottomSheetViewMap.put(BottomSheetType.FAVORITE_LOCATIONS, favoriteLocationsBottomSheet);
		bottomSheetBehaviorMap.put(BottomSheetType.FAVORITE_LOCATIONS, favoriteLocationsBottomSheetBehavior);
	}


	public void onPageSelectedLocationItemBottomSheetViewPager(int position, MarkerType markerType) {
		switch (markerType) {
			case SEARCH_RESULT_ADDRESS:
				LocationSearchResultFragment locationSearchResultFragmentForAddress =
						(LocationSearchResultFragment) getChildFragmentManager().findFragmentByTag(getString(R.string.tag_location_search_result_fragment));
				locationSearchResultFragmentForAddress.loadExtraListData(new RecyclerView.AdapterDataObserver() {
					@Override
					public void onItemRangeInserted(int positionStart, int itemCount) {
						super.onItemRangeInserted(positionStart, itemCount);
					}
				});
				return;

			case SEARCH_RESULT_PLACE:
				LocationSearchResultFragment locationSearchResultFragmentForPlace =
						(LocationSearchResultFragment) getChildFragmentManager().findFragmentByTag(getString(R.string.tag_location_search_result_fragment));
				locationSearchResultFragmentForPlace.loadExtraListData(new RecyclerView.AdapterDataObserver() {
					@Override
					public void onItemRangeInserted(int positionStart, int itemCount) {
						super.onItemRangeInserted(positionStart, itemCount);
					}
				});
				return;
		}


	}

	private void setLocationItemsBottomSheet() {
		LinearLayout locationItemBottomSheet = binding.placeslistBottomSheet.placesBottomsheet;
		locationItemBottomSheetViewPager = (ViewPager2) locationItemBottomSheet.findViewById(R.id.place_items_viewpager);

		locationItemBottomSheetViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
			MarkerType markerType;

			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				markerType = (MarkerType) locationItemBottomSheetViewPager.getTag();

				if (bottomSheetBehaviorMap.get(BottomSheetType.LOCATION_ITEM).getState() == BottomSheetBehavior.STATE_EXPANDED) {
					onPOIItemSelectedByBottomSheet(position, markerType);
				}

				if (markerType == MarkerType.FAVORITE) {
					if (position == ((FavoriteLocationItemViewPagerAdapter) viewPagerAdapterMap.get(markerType)).getItemCount() - 1) {
						onPageSelectedLocationItemBottomSheetViewPager(position, markerType);
					}
				} else {
					if (position == viewPagerAdapterMap.get(markerType).getItemCount() - 1) {
						onPageSelectedLocationItemBottomSheetViewPager(position, markerType);
					}
				}

			}
		});


		locationItemBottomSheetViewPager.setOffscreenPageLimit(3);

		BottomSheetBehavior locationItemBottomSheetBehavior = BottomSheetBehavior.from(locationItemBottomSheet);
		locationItemBottomSheetBehavior.setDraggable(true);
		locationItemBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
			@Override
			public void onStateChanged(@NonNull View bottomSheet, int newState) {

			}

			@Override
			public void onSlide(@NonNull View bottomSheet, float slideOffset) {
				//expanded일때 offset == 1.0, collapsed일때 offset == 0.0
				//offset에 따라서 버튼들이 이동하고, 지도의 좌표가 변경되어야 한다.
				float translationValue = -locationItemBottomSheet.getHeight() * slideOffset;
				binding.naverMapButtonsLayout.getRoot().animate().translationY(translationValue);
			}
		});

		bottomSheetViewMap.put(BottomSheetType.LOCATION_ITEM, locationItemBottomSheet);
		bottomSheetBehaviorMap.put(BottomSheetType.LOCATION_ITEM, locationItemBottomSheetBehavior);
	}

	public void setPlaceBottomSheetSelectBtnVisibility(int placeBottomSheetSelectBtnVisibility) {
		this.placeBottomSheetSelectBtnVisibility = placeBottomSheetSelectBtnVisibility;
	}

	public void setPlaceBottomSheetUnSelectBtnVisibility(
			int placeBottomSheetUnSelectBtnVisibility) {
		this.placeBottomSheetUnSelectBtnVisibility = placeBottomSheetUnSelectBtnVisibility;
	}


	public void showRequestGpsDialog() {
		new AlertDialog.Builder(getActivity())
				.setMessage(getString(R.string.request_to_make_gps_on))
				.setPositiveButton(getString(R.string.check), new
						DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface paramDialogInterface, int paramInt) {
								requestOnGpsLauncher.launch(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
							}
						})
				.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
					}
				})
				.setCancelable(false)
				.show();
	}

	@Override
	public void onMapReady(@NonNull NaverMap naverMap) {
		this.naverMap = naverMap;

		naverMap.addOnLocationChangeListener(this);
		naverMap.addOnCameraIdleListener(this);
		naverMap.setOnMapClickListener(this);
		naverMap.setOnMapLongClickListener(this);
		naverMap.getUiSettings().setZoomControlEnabled(false);

		LocationOverlay locationOverlay = naverMap.getLocationOverlay();
		locationOverlay.setVisible(false);

		loadFavoriteLocations();
		loadingDialog.dismiss();
	}

	private final ActivityResultLauncher<Intent> requestOnGpsLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
			new ActivityResultCallback<ActivityResult>() {
				@Override
				public void onActivityResult(ActivityResult result) {
					if (AppPermission.grantedPermissions(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
						gpsButton.callOnClick();
					} else {

					}
				}
			});


	private final ActivityResultLauncher<String> requestLocationPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
			new ActivityResultCallback<Boolean>() {
				@Override
				public void onActivityResult(Boolean isGranted) {
					if (isGranted) {
						fusedLocationSource.onRequestPermissionsResult(REQUEST_CODE_LOCATION, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
								new int[]{PackageManager.PERMISSION_GRANTED});
						naverMap.setLocationSource(fusedLocationSource);
						naverMap.setLocationTrackingMode(LocationTrackingMode.NoFollow);
					} else {
						Toast.makeText(getActivity(), getString(R.string.message_needs_location_permission), Toast.LENGTH_SHORT).show();
						naverMap.setLocationSource(null);
					}
				}
			});

	private final OnKakaoLocalApiCallback reverseGeoCodingResponseCallback = new OnKakaoLocalApiCallback() {
		@Override
		public void onResultSuccessful(int type, KakaoLocalResponse result) {
			if (getActivity() != null) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						binding.naverMapButtonsLayout.currentAddress.setText(((CoordToRegionCode) result).getCoordToRegionCodeDocuments()
								.get(0).getAddressName());
					}
				});
			}
		}

		@Override
		public void onResultNoData() {
			if (getActivity() != null) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						binding.naverMapButtonsLayout.currentAddress.setText("");
					}
				});
			}
		}
	};

	public void setCurrentAddress() {
		//sgis reverse geocoding 이용
		LatLng latLng = naverMap.getCameraPosition().target;
		/*
		Utmk utmk = Utmk.valueOf(latLng);

		ReverseGeoCodingParameter parameter = new ReverseGeoCodingParameter();
		parameter.setAddrType("20");
		parameter.setxCoor(String.valueOf(utmk.x));
		parameter.setyCoor(String.valueOf(utmk.y));

		SgisAddress.reverseGeoCoding(parameter, reverseGeoCodingResponseJsonDownloader);

		 */

		LocalApiPlaceParameter reverseGeoCodingParameter = LocalParameterUtil.getCoordToRegionCodeParameter(latLng.latitude, latLng.longitude);
		KakaoLocalDownloader.coordToRegionCode(reverseGeoCodingParameter, reverseGeoCodingResponseCallback);
	}


	@Override
	public boolean networkAvailable() {
		return networkStatus.networkAvailable();
	}

	@Override
	public LatLng getMapCenterPoint() {
		return naverMap.getContentBounds().getCenter();
	}


	private void onClickedMarkerByTouch(Marker marker) {
		//poiitem을 직접 선택한 경우 호출
		MarkerHolder markerHolder = (MarkerHolder) marker.getTag();

		CameraUpdate cameraUpdate = CameraUpdate.scrollTo(marker.getPosition());
		cameraUpdate.animate(CameraAnimation.Easing, 150);
		naverMap.moveCamera(cameraUpdate);

		LocationItemViewPagerAbstractAdapter adapter = viewPagerAdapterMap.get(markerHolder.markerType);
		int itemPosition = 0;

		if (markerHolder.markerType == MarkerType.FAVORITE) {
			itemPosition =
					((FavoriteLocationItemViewPagerAdapter) adapter).getItemPosition(((FavoriteMarkerHolder) markerHolder).favoriteLocationDTO);
		} else if (markerHolder.markerType == MarkerType.LONG_CLICKED_MAP) {
			itemPosition = 0;
		} else {
			itemPosition = ((LocationItemViewPagerAdapter) adapter).getItemPosition(markerHolder.kakaoLocalDocument);
		}
		//선택된 마커의 아이템 리스트내 위치 파악 후 뷰 페이저 이동
		locationItemBottomSheetViewPager.setTag(markerHolder.markerType);
		locationItemBottomSheetViewPager.setAdapter(adapter);
		locationItemBottomSheetViewPager.setCurrentItem(itemPosition, false);

		setStateOfBottomSheet(BottomSheetType.LOCATION_ITEM, BottomSheetBehavior.STATE_EXPANDED);
	}

	@Override
	public void createMarkers(@NotNull List<? extends KakaoLocalDocument> kakaoLocalDocuments, @NotNull MarkerType markerType) {
		if (!markersMap.containsKey(markerType)) {
			markersMap.put(markerType, new ArrayList<>());
		} else {
			removeMarkers(markerType);
		}

		LocationItemViewPagerAbstractAdapter adapter = null;

		if (markerType == MarkerType.FAVORITE) {
			FavoriteLocationItemViewPagerAdapter favoriteLocationItemViewPagerAdapter =
					new FavoriteLocationItemViewPagerAdapter(getContext());
			favoriteLocationItemViewPagerAdapter.setiLoadLocationData(new ILoadLocationData() {
				@Override
				public void loadLocationData(int requestType, LocalApiPlaceParameter parameter, OnKakaoLocalApiCallback onKakaoLocalApiCallback) {
					if (requestType == FavoriteLocationDTO.ADDRESS) {
						// 주소 검색 순서 : 좌표로 주소 변환
						KakaoLocalDownloader.coordToAddress(parameter, onKakaoLocalApiCallback);
					} else if (requestType == FavoriteLocationDTO.PLACE || requestType == FavoriteLocationDTO.RESTAURANT) {
						// 장소 검색 순서 : 장소의 위경도 내 10M 반경에서 장소 이름 검색(여러개 나올 경우 장소ID와 일치하는 장소를 선택)
						KakaoLocalDownloader.getPlaces(parameter, onKakaoLocalApiCallback);
					}
				}
			});
			adapter = favoriteLocationItemViewPagerAdapter;
			adapter.setVisibleSelectBtn(placeBottomSheetSelectBtnVisibility);
		} else if (markerType == MarkerType.LONG_CLICKED_MAP) {
			adapter = new OnLongClickMapLocationItemAdapter(getContext(), new ILoadLocationData() {
				@Override
				public void loadLocationData(int requestType, LocalApiPlaceParameter parameter, OnKakaoLocalApiCallback onKakaoLocalApiCallback) {
					KakaoLocalDownloader.coordToAddress(parameter, onKakaoLocalApiCallback);
				}
			});
		} else {
			adapter = new LocationItemViewPagerAdapter(getContext(), markerType);
			adapter.setVisibleSelectBtn(placeBottomSheetSelectBtnVisibility);
			adapter.setVisibleUnSelectBtn(placeBottomSheetUnSelectBtnVisibility);
			((LocationItemViewPagerAdapter) adapter).setLocalDocumentsList(kakaoLocalDocuments);

			adapter.notifyDataSetChanged();
		}

		adapter.setPlacesItemBottomSheetButtonOnClickListener(this);
		adapter.setOnClickedBottomSheetListener(this);
		adapter.setFavoriteLocationQuery(favoriteLocationQuery);
		viewPagerAdapterMap.put(markerType, adapter);

		if (!kakaoLocalDocuments.isEmpty()) {
			if (kakaoLocalDocuments.get(0) instanceof PlaceDocuments) {
				List<PlaceDocuments> placeDocuments = (List<PlaceDocuments>) kakaoLocalDocuments;

				for (PlaceDocuments document : placeDocuments) {
					createPlaceMarker(markerType, document);
				}
			} else if (kakaoLocalDocuments.get(0) instanceof AddressResponseDocuments) {
				List<AddressResponseDocuments> addressDocuments = (List<AddressResponseDocuments>) kakaoLocalDocuments;

				for (AddressResponseDocuments document : addressDocuments) {
					createAddressMarker(markerType, document);
				}
			} else if (kakaoLocalDocuments.get(0) instanceof CoordToAddressDocuments) {
				List<CoordToAddressDocuments> coordToAddressDocuments = (List<CoordToAddressDocuments>) kakaoLocalDocuments;

				for (CoordToAddressDocuments document : coordToAddressDocuments) {
					createCoordToAddressMarker(markerType, document);
				}
			}
		}
	}

	public void createPlaceMarker(MarkerType markerType, PlaceDocuments placeDocument) {
		Marker marker = new Marker();
		marker.setWidth(markerWidth);
		marker.setHeight(markerHeight);
		marker.setPosition(new LatLng(Double.parseDouble(placeDocument.getY()), Double.parseDouble(placeDocument.getX())));
		marker.setMap(naverMap);
		marker.setCaptionText(placeDocument.getPlaceName());
		marker.setOnClickListener(markerOnClickListener);

		MarkerHolder markerHolder = new MarkerHolder(placeDocument, markerType);
		marker.setTag(markerHolder);
		markersMap.get(markerType).add(marker);
	}

	public void createAddressMarker(MarkerType markerType, AddressResponseDocuments addressResponseDocument) {
		Marker marker = new Marker();
		marker.setWidth(markerWidth);
		marker.setHeight(markerHeight);
		marker.setPosition(new LatLng(Double.parseDouble(addressResponseDocument.getY()), Double.parseDouble(addressResponseDocument.getX())));
		marker.setMap(naverMap);
		marker.setCaptionText(addressResponseDocument.getAddressName());
		marker.setOnClickListener(markerOnClickListener);

		MarkerHolder markerHolder = new MarkerHolder(addressResponseDocument, markerType);
		marker.setTag(markerHolder);
		markersMap.get(markerType).add(marker);
	}

	public void createCoordToAddressMarker(MarkerType markerType, CoordToAddressDocuments coordToAddressDocument) {
		Marker marker = new Marker();
		marker.setWidth(markerWidth);
		marker.setHeight(markerHeight);
		marker.setPosition(new LatLng(Double.parseDouble(coordToAddressDocument.getCoordToAddressAddress().getLatitude()),
				Double.parseDouble(coordToAddressDocument.getCoordToAddressAddress().getLongitude())));
		marker.setMap(naverMap);
		marker.setCaptionText(coordToAddressDocument.getCoordToAddressAddress().getAddressName());
		marker.setOnClickListener(markerOnClickListener);

		MarkerHolder markerHolder = new MarkerHolder(coordToAddressDocument, markerType);
		marker.setTag(markerHolder);
		markersMap.get(markerType).add(marker);
	}


	private final Overlay.OnClickListener markerOnClickListener = new Overlay.OnClickListener() {
		@Override
		public boolean onClick(@NonNull Overlay overlay) {
			onClickedMarkerByTouch((Marker) overlay);
			return true;
		}
	};

	@Override
	public void addExtraMarkers(@NotNull List<? extends KakaoLocalDocument> kakaoLocalDocuments, @NotNull MarkerType markerType) {
		if (!kakaoLocalDocuments.isEmpty()) {
			LocationItemViewPagerAdapter adapter = (LocationItemViewPagerAdapter) viewPagerAdapterMap.get(markerType);

			final int LAST_INDEX = adapter.getItemsCount() - 1;
			List<KakaoLocalDocument> currentList = adapter.getLocalDocumentsList();
			List<? extends KakaoLocalDocument> subList = (List<? extends KakaoLocalDocument>) kakaoLocalDocuments.subList(LAST_INDEX + 1, kakaoLocalDocuments.size());

			int currentIndex = currentList.size();
			for (KakaoLocalDocument ob : subList) {
				currentList.add(currentIndex++, ob);
			}
			viewPagerAdapterMap.get(markerType).notifyDataSetChanged();

			if (kakaoLocalDocuments.get(0) instanceof PlaceDocuments) {
				List<PlaceDocuments> placeDocuments = (List<PlaceDocuments>) subList;

				for (PlaceDocuments document : placeDocuments) {
					createPlaceMarker(markerType, document);
				}
			} else if (kakaoLocalDocuments.get(0) instanceof AddressResponseDocuments) {
				List<AddressResponseDocuments> addressDocuments = (List<AddressResponseDocuments>) kakaoLocalDocuments;

				for (AddressResponseDocuments document : addressDocuments) {
					createAddressMarker(markerType, document);
				}
			} else if (kakaoLocalDocuments.get(0) instanceof CoordToAddressDocuments) {
				List<CoordToAddressDocuments> coordToAddressDocuments = (List<CoordToAddressDocuments>) kakaoLocalDocuments;

				for (CoordToAddressDocuments document : coordToAddressDocuments) {
					createCoordToAddressMarker(markerType, document);
				}
			}

		}
	}

	@Override
	public void removeMarker(MarkerType markerType, int index) {
		if (markersMap.containsKey(markerType)) {
			markersMap.get(markerType).get(index).setMap(null);
			markersMap.get(markerType).remove(index);
		}
	}

	@Override
	public void removeMarkers(MarkerType... markerTypes) {
		for (MarkerType markerType : markerTypes) {
			if (markersMap.containsKey(markerType)) {
				List<Marker> markerList = markersMap.get(markerType);
				for (Marker marker : markerList) {
					marker.setMap(null);
				}

				markerList.clear();
			}
		}
	}


	@Override
	public void removeAllMarkers() {
		Set<MarkerType> keySet = markersMap.keySet();
		for (MarkerType markerType : keySet) {
			List<Marker> markerList = markersMap.get(markerType);
			for (Marker marker : markerList) {
				marker.setMap(null);
			}

			markerList.clear();
		}
	}


	@Override
	public void showMarkers(MarkerType... markerTypes) {
		List<LatLng> latLngList = new ArrayList<>();

		for (MarkerType markerType : markerTypes) {
			List<Marker> markerList = markersMap.get(markerType);

			for (Marker marker : markerList) {
				latLngList.add(marker.getPosition());
			}
		}

		if (!latLngList.isEmpty()) {
			LatLngBounds latLngBounds = LatLngBounds.from(latLngList);

			int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, getResources().getDisplayMetrics());
			double fittableZoom = CameraUtils.getFittableZoom(naverMap, latLngBounds, padding);
			if (fittableZoom >= 16) {
				fittableZoom = 16;
			}

			CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(latLngBounds.getCenter(), fittableZoom);
			naverMap.moveCamera(cameraUpdate);
		}
	}

	@Override
	public void deselectMarker() {
		setStateOfBottomSheet(BottomSheetType.LOCATION_ITEM, BottomSheetBehavior.STATE_COLLAPSED);
	}

	private final FavoriteLocationQuery favoriteLocationQuery = new FavoriteLocationQuery() {
		@Override
		public void addNewFavoriteLocation(FavoriteLocationDTO favoriteLocationDTO, DbQueryCallback<FavoriteLocationDTO> callback) {
			favoriteLocationViewModel.addNewFavoriteLocation(favoriteLocationDTO, callback);
		}

		@Override
		public void getFavoriteLocations(Integer type, DbQueryCallback<List<FavoriteLocationDTO>> callback) {

		}

		@Override
		public void getFavoriteLocation(Integer id, DbQueryCallback<FavoriteLocationDTO> callback) {

		}

		@Override
		public void delete(FavoriteLocationDTO favoriteLocationDTO, DbQueryCallback<Boolean> callback) {
			favoriteLocationViewModel.delete(favoriteLocationDTO, callback);
		}

		@Override
		public void deleteAll(Integer type, DbQueryCallback<Boolean> callback) {

		}

		@Override
		public void deleteAll(DbQueryCallback<Boolean> callback) {

		}

		@Override
		public void contains(String placeId, String latitude, String longitude, DbQueryCallback<FavoriteLocationDTO> callback) {
			favoriteLocationViewModel.contains(placeId, latitude, longitude, callback);
		}
	};


	public LocationDTO getSelectedLocationDto() {
		// 선택된 poiitem의 리스트내 인덱스를 가져온다.
		// 인덱스로 아이템을 가져온다.
		MarkerType selectedMarkerType = (MarkerType) locationItemBottomSheetViewPager.getTag();
		int currentViewPagerPosition = locationItemBottomSheetViewPager.getCurrentItem();
		KakaoLocalDocument kakaoLocalDocument = viewPagerAdapterMap.get(selectedMarkerType).getLocalItem(currentViewPagerPosition);

		LocationDTO location = new LocationDTO();

		// 주소인지 장소인지를 구분한다.
		if (kakaoLocalDocument instanceof PlaceDocuments) {
			PlaceDocuments placeDocuments = (PlaceDocuments) kakaoLocalDocument;
			location.setPlaceId(placeDocuments.getId());
			location.setPlaceName(placeDocuments.getPlaceName());
			location.setAddressName(placeDocuments.getAddressName());
			location.setRoadAddressName(placeDocuments.getRoadAddressName());
			location.setLatitude(placeDocuments.getY());
			location.setLongitude(placeDocuments.getX());
			location.setLocationType(LocationType.PLACE);
		} else if (kakaoLocalDocument instanceof AddressResponseDocuments) {
			AddressResponseDocuments addressDocuments = (AddressResponseDocuments) kakaoLocalDocument;

			location.setAddressName(addressDocuments.getAddressName());
			location.setLatitude(addressDocuments.getY());
			location.setLongitude(addressDocuments.getX());
			location.setLocationType(LocationType.ADDRESS);

			if (addressDocuments.getAddressResponseRoadAddress() != null) {
				location.setRoadAddressName(addressDocuments.getAddressResponseRoadAddress().getAddressName());
			}
		} else if (kakaoLocalDocument instanceof CoordToAddressDocuments) {
			CoordToAddressDocuments coordToAddressDocuments = (CoordToAddressDocuments) kakaoLocalDocument;

			location.setAddressName(coordToAddressDocuments.getCoordToAddressAddress().getAddressName());
			location.setLatitude(coordToAddressDocuments.getCoordToAddressAddress().getLatitude());
			location.setLongitude(coordToAddressDocuments.getCoordToAddressAddress().getLongitude());
			location.setLocationType(LocationType.ADDRESS);

			if (coordToAddressDocuments.getCoordToAddressRoadAddress() != null) {
				location.setRoadAddressName(coordToAddressDocuments.getCoordToAddressRoadAddress().getAddressName());
			}
		}
		return location;
	}

	@Override
	public void onPOIItemSelectedByList(KakaoLocalDocument kakaoLocalDocument, MarkerType markerType) {
		//bottomsheet가 아닌 list에서 아이템을 선택한 경우 호출
		//adapter -> poiitem생성 -> select poiitem -> bottomsheet열고 정보 표시
		List<Marker> markerList = markersMap.get(markerType);
		MarkerHolder markerHolder = null;
		Marker selectedMarker = null;

		LocationItemViewPagerAbstractAdapter adapter = viewPagerAdapterMap.get(markerType);
		final int position = adapter.getLocalItemPosition(kakaoLocalDocument);

		if (kakaoLocalDocument instanceof PlaceDocuments) {
			String placeId = ((PlaceDocuments) kakaoLocalDocument).getId();
			for (Marker marker : markerList) {
				markerHolder = (MarkerHolder) marker.getTag();
				if (((PlaceDocuments) markerHolder.kakaoLocalDocument).getId().equals(placeId)) {
					selectedMarker = marker;
					break;
				}
			}
		} else if (kakaoLocalDocument instanceof AddressResponseDocuments) {
			String addressName = ((AddressResponseDocuments) kakaoLocalDocument).getAddressName();
			for (Marker marker : markerList) {
				markerHolder = (MarkerHolder) marker.getTag();
				if (((AddressResponseDocuments) markerHolder.kakaoLocalDocument).getAddressName().equals(addressName)) {
					selectedMarker = marker;
					break;
				}

			}
		} else if (kakaoLocalDocument instanceof CoordToAddressDocuments) {
			String addressName = ((CoordToAddressDocuments) kakaoLocalDocument).getCoordToAddressAddress().getAddressName();
			for (Marker marker : markerList) {
				markerHolder = (MarkerHolder) marker.getTag();
				if (((CoordToAddressDocuments) markerHolder.kakaoLocalDocument).getCoordToAddressAddress().getAddressName().equals(addressName)) {
					selectedMarker = marker;
					break;
				}

			}
		}

		CameraUpdate cameraUpdate = CameraUpdate.scrollTo(selectedMarker.getPosition());
		cameraUpdate.animate(CameraAnimation.Easing, 150);
		naverMap.moveCamera(cameraUpdate);

		//선택된 마커의 아이템 리스트내 위치 파악 후 뷰 페이저 이동
		locationItemBottomSheetViewPager.setTag(markerType);
		locationItemBottomSheetViewPager.setAdapter(adapter);
		locationItemBottomSheetViewPager.setCurrentItem(position, false);

		setStateOfBottomSheet(BottomSheetType.LOCATION_ITEM, BottomSheetBehavior.STATE_EXPANDED);
	}

	@Override
	public void onFavoritePOIItemSelectedByList(FavoriteLocationDTO favoriteLocationDTO) {
		List<Marker> markerList = markersMap.get(MarkerType.FAVORITE);
		MarkerHolder markerHolder = null;
		Marker selectedMarker = null;

		FavoriteLocationItemViewPagerAdapter adapter = (FavoriteLocationItemViewPagerAdapter) viewPagerAdapterMap.get(MarkerType.FAVORITE);
		int selectedPosition = adapter.getItemPosition(favoriteLocationDTO);

		for (Marker marker : markerList) {
			markerHolder = (MarkerHolder) marker.getTag();
			if (((FavoriteMarkerHolder) markerHolder).favoriteLocationDTO.getId().equals(favoriteLocationDTO.getId())) {
				selectedMarker = marker;
				break;
			}
		}

		CameraUpdate cameraUpdate = CameraUpdate.scrollTo(selectedMarker.getPosition());
		cameraUpdate.animate(CameraAnimation.Easing, 150);
		naverMap.moveCamera(cameraUpdate);

		//선택된 마커의 아이템 리스트내 위치 파악 후 뷰 페이저 이동
		locationItemBottomSheetViewPager.setTag(MarkerType.FAVORITE);
		locationItemBottomSheetViewPager.setAdapter(adapter);
		locationItemBottomSheetViewPager.setCurrentItem(selectedPosition, false);

		setStateOfBottomSheet(BottomSheetType.LOCATION_ITEM, BottomSheetBehavior.STATE_EXPANDED);
	}

	@Override
	public void onPOIItemSelectedByBottomSheet(int position, MarkerType markerType) {
		//bottomsheet에서 스크롤 하는 경우 호출
		LatLng target = null;
		List<Marker> markerList = markersMap.get(markerType);

		if (markerType == MarkerType.FAVORITE) {
			FavoriteLocationItemViewPagerAdapter adapter = (FavoriteLocationItemViewPagerAdapter) locationItemBottomSheetViewPager.getAdapter();
			FavoriteLocationDTO favoriteLocationDTO = adapter.getKey(position);
			MarkerHolder markerHolder = null;

			for (Marker marker : markerList) {
				markerHolder = (MarkerHolder) marker.getTag();
				if (((FavoriteMarkerHolder) markerHolder).favoriteLocationDTO.getId().equals(favoriteLocationDTO.getId())) {
					target = marker.getPosition();
					break;
				}
			}
		} else {
			LocationItemViewPagerAbstractAdapter adapter = (LocationItemViewPagerAbstractAdapter) locationItemBottomSheetViewPager.getAdapter();
			KakaoLocalDocument kakaoLocalDocument = adapter.getLocalItem(position);

			if (kakaoLocalDocument instanceof PlaceDocuments) {
				target = new LatLng(Double.parseDouble(((PlaceDocuments) kakaoLocalDocument).getY()), Double.parseDouble(((PlaceDocuments) kakaoLocalDocument).getX()));
			} else if (kakaoLocalDocument instanceof AddressResponseDocuments) {
				target = new LatLng(Double.parseDouble(((AddressResponseDocuments) kakaoLocalDocument).getY()), Double.parseDouble(((AddressResponseDocuments) kakaoLocalDocument).getX()));
			} else if (kakaoLocalDocument instanceof CoordToAddressDocuments) {
				target = new LatLng(Double.parseDouble(((CoordToAddressDocuments) kakaoLocalDocument).getCoordToAddressAddress().getLatitude()), Double.parseDouble(((CoordToAddressDocuments) kakaoLocalDocument).getCoordToAddressAddress().getLongitude()));
			}
		}
		CameraUpdate cameraUpdate = CameraUpdate.scrollTo(target);
		cameraUpdate.animate(CameraAnimation.Easing, 150);
		naverMap.moveCamera(cameraUpdate);
	}

	@Override
	public void onSelectedLocation(KakaoLocalDocument kakaoLocalDocument) {

	}

	@Override
	public void onRemovedLocation() {

	}

	@Override
	public BottomSheetBehavior getBottomSheetBehavior(BottomSheetType bottomSheetType) {
		return bottomSheetBehaviorMap.get(bottomSheetType);
	}

	@Override
	public void onClickedPlaceBottomSheet(KakaoLocalDocument kakaoLocalDocument) {
		//place or address
		if (kakaoLocalDocument instanceof PlaceDocuments) {
			PlaceInfoWebDialogFragment placeInfoWebDialogFragment = new PlaceInfoWebDialogFragment();
			Bundle bundle = new Bundle();
			bundle.putString("placeId", ((PlaceDocuments) kakaoLocalDocument).getId());
			bundle.putInt("bottomSheetHeight", DEFAULT_HEIGHT_OF_BOTTOMSHEET);
			placeInfoWebDialogFragment.setArguments(bundle);

			placeInfoWebDialogFragment.show(getChildFragmentManager(), getString(R.string.tag_place_info_web_dialog_fragment));
		} else {

		}
	}

	public void removeBuildingLocationSelector() {
		if (binding.naverMapViewLayout.findViewWithTag(getString(R.string.tag_building_selector)) != null) {
			binding.naverMapViewLayout.removeView(binding.naverMapViewLayout.findViewWithTag(getString(R.string.tag_building_selector)));
		}
	}

	public void showBuildingLocationSelector() {
		buildingButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.building_blue));
		//드래그로 이동가능한 마커 생성
		View selectorView = getLayoutInflater().inflate(R.layout.building_location_selector_view, null);
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.gravity = Gravity.CENTER;
		selectorView.setLayoutParams(layoutParams);
		selectorView.setTag(getString(R.string.tag_building_selector));

		binding.naverMapViewLayout.addView(selectorView);

		((Button) selectorView.findViewById(R.id.search_buildings_button)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//빌딩 목록 바텀 시트 열기
				//map center point를 좌표로 지정
				removeBuildingLocationSelector();
				binding.naverMapButtonsLayout.favoriteLocationsButton.setVisibility(View.GONE);

				BuildingListFragment buildingListFragment = new BuildingListFragment(new BuildingListFragment.IDrawCircleOnMap() {
					CircleOverlay buildingRangeCircleOverlay;

					@Override
					public void drawSearchRadiusCircle() {
						LatLng latLng = naverMap.getCameraPosition().target;
						if (buildingRangeCircleOverlay != null) {
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
					public void removeSearchRadiusCircle() {
						buildingRangeCircleOverlay.setMap(null);
					}
				});

				Bundle bundle = new Bundle();
				LatLng latLng = naverMap.getContentBounds().getCenter();

				bundle.putDouble("centerLatitude", latLng.latitude);
				bundle.putDouble("centerLongitude", latLng.longitude);
				buildingListFragment.setArguments(bundle);
				bottomSheetFragmentMap.put(BottomSheetType.BUILDING, buildingListFragment);

				getChildFragmentManager().beginTransaction().add(binding.buildingBottomSheet.buildingFragmentContainer.getId(), buildingListFragment,
						getString(R.string.tag_building_list_fragment))
						.addToBackStack(getString(R.string.tag_building_list_fragment))
						.commit();
			}
		});
	}


	public void setHeightOfBottomSheetForSpecificFragment(String fragmentTag, BottomSheetType bottomSheetType, int height) {
		bottomSheetViewMap.get(bottomSheetType).getLayoutParams().height = height;
		bottomSheetViewMap.get(bottomSheetType).requestLayout();

		bottomSheetBehaviorMap.get(bottomSheetType)
				.onLayoutChild(binding.naverMapFragmentRootLayout, bottomSheetViewMap.get(bottomSheetType), ViewCompat.LAYOUT_DIRECTION_LTR);
	}

	@Override
	public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
		deselectMarker();
	}

	@Override
	public void onCameraIdle() {
		setCurrentAddress();
	}

	@Override
	public void onCameraUpdateFinish() {
		//setCurrentAddress();
	}

	@Override
	public void onLocationChange(@NonNull Location location) {
		LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

		CameraUpdate cameraUpdate = CameraUpdate.scrollTo(latLng);
		naverMap.moveCamera(cameraUpdate);
		naverMap.setLocationSource(null);

		LocationOverlay locationOverlay = naverMap.getLocationOverlay();
		locationOverlay.setVisible(true);
		locationOverlay.setPosition(latLng);
	}

	@Override
	public List<BottomSheetBehavior> getBottomSheetBehaviorOfExpanded(BottomSheetBehavior currentBottomSheetBehavior) {
		Set<BottomSheetType> keySet = bottomSheetBehaviorMap.keySet();
		List<BottomSheetBehavior> bottomSheetBehaviors = new ArrayList<>();

		for (BottomSheetType bottomSheetType : keySet) {
			if (bottomSheetBehaviorMap.get(bottomSheetType).getState() == BottomSheetBehavior.STATE_EXPANDED) {

				if (currentBottomSheetBehavior != null) {
					if (!bottomSheetBehaviorMap.get(bottomSheetType).equals(currentBottomSheetBehavior)) {
						bottomSheetBehaviors.add(bottomSheetBehaviorMap.get(bottomSheetType));
					}
				}
			}
		}

		return bottomSheetBehaviors;
	}

	@Override
	public void collapseAllExpandedBottomSheets() {
		Set<BottomSheetType> keySet = bottomSheetBehaviorMap.keySet();

		for (BottomSheetType bottomSheetType : keySet) {
			if (getStateOfBottomSheet(bottomSheetType) == BottomSheetBehavior.STATE_EXPANDED) {
				setStateOfBottomSheet(bottomSheetType, BottomSheetBehavior.STATE_COLLAPSED);
			}
		}
	}

	@Override
	public void onBackStackChanged() {

	}

	@Override
	public void setStateOfBottomSheet(BottomSheetType bottomSheetType, int state) {
		bottomSheetBehaviorMap.get(bottomSheetType).setState(state);
	}

	@Override
	public int getStateOfBottomSheet(BottomSheetType bottomSheetType) {
		return bottomSheetBehaviorMap.get(bottomSheetType).getState();
	}

	@Override
	public void onMapLongClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
		showAddressOfSelectedLocation(latLng);
	}


	private void showAddressOfSelectedLocation(LatLng latLng) {
		//주소 표시
		if (markersMap.containsKey(MarkerType.LONG_CLICKED_MAP)) {
			if (markersMap.get(MarkerType.LONG_CLICKED_MAP).size() > 0) {
				markersMap.get(MarkerType.LONG_CLICKED_MAP).get(0).performClick();
			}
		}
		Marker markerOfSelectedLocation = new Marker(latLng);

		markerOfSelectedLocation.setCaptionColor(Color.BLUE);
		markerOfSelectedLocation.setCaptionHaloColor(Color.rgb(200, 255, 200));
		markerOfSelectedLocation.setCaptionTextSize(14f);
		markerOfSelectedLocation.setCaptionText(getString(R.string.message_click_marker_to_delete));

		MarkerHolder markerHolder = new MarkerHolder(null, MarkerType.LONG_CLICKED_MAP);
		markerOfSelectedLocation.setTag(markerHolder);
		markerOfSelectedLocation.setMap(naverMap);

		markerOfSelectedLocation.setOnClickListener(new Overlay.OnClickListener() {
			@Override
			public boolean onClick(@NonNull Overlay overlay) {
				removeMarkers(MarkerType.LONG_CLICKED_MAP);
				setStateOfBottomSheet(BottomSheetType.LOCATION_ITEM, BottomSheetBehavior.STATE_COLLAPSED);
				return true;
			}
		});

		createMarkers(new ArrayList<>(), MarkerType.LONG_CLICKED_MAP);
		markersMap.get(MarkerType.LONG_CLICKED_MAP).add(markerOfSelectedLocation);

		OnLongClickMapLocationItemAdapter adapter = (OnLongClickMapLocationItemAdapter) viewPagerAdapterMap.get(MarkerType.LONG_CLICKED_MAP);
		adapter.setLatitude(String.valueOf(latLng.latitude));
		adapter.setLongitude(String.valueOf(latLng.longitude));
		adapter.setVisibleSelectBtn(placeBottomSheetSelectBtnVisibility);
		adapter.notifyDataSetChanged();

		onClickedMarkerByTouch(markersMap.get(MarkerType.LONG_CLICKED_MAP).get(0));
	}

	@Override
	public void onClickedFavoriteButton(KakaoLocalDocument kakaoLocalDocument, FavoriteLocationDTO favoriteLocationDTO, int groupPosition, int childPosition) {

	}

	@Override
	public void onClickedFavoriteButton(KakaoLocalDocument kakaoLocalDocument, FavoriteLocationDTO favoriteLocationDTO, int position) {
	}


	@Override
	public void showMarkers(MarkerType markerType, boolean isShow) {
		if (markersMap.containsKey(markerType)) {
			List<Marker> markers = markersMap.get(markerType);
			for (Marker marker : markers) {
				marker.setMap(isShow ? naverMap : null);
			}
		}
	}

	public void loadFavoriteLocations() {
		favoriteLocationViewModel.getFavoriteLocations(FavoriteLocationDTO.ONLY_FOR_MAP, new DbQueryCallback<List<FavoriteLocationDTO>>() {
			@Override
			public void onResultSuccessful(List<FavoriteLocationDTO> list) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						createFavoriteLocationsPoiItems(list);

						if (!list.isEmpty()) {
							showMarkers(MarkerType.FAVORITE, App.isPreference_key_show_favorite_locations_markers_on_map());
						}
					}
				});
			}

			@Override
			public void onResultNoData() {

			}
		});

		favoriteLocationViewModel.getRemovedFavoriteLocationMutableLiveData().observe(this, new Observer<FavoriteLocationDTO>() {
			@Override
			public void onChanged(FavoriteLocationDTO favoriteLocationDTO) {
				if (naverMap != null) {
					removeFavoriteLocationMarker(favoriteLocationDTO);
				}
			}
		});

		favoriteLocationViewModel.getAddedFavoriteLocationMutableLiveData().observe(this, new Observer<FavoriteLocationDTO>() {
			@Override
			public void onChanged(FavoriteLocationDTO favoriteLocationDTO) {
				if (naverMap != null) {
					addFavoriteLocationsPoiItem(favoriteLocationDTO);
				}
			}
		});
	}

	public void createFavoriteLocationsPoiItems(List<FavoriteLocationDTO> favoriteLocationList) {
		createMarkers(new ArrayList<>(), MarkerType.FAVORITE);
		((FavoriteLocationItemViewPagerAdapter) viewPagerAdapterMap.get(MarkerType.FAVORITE)).setFavoriteLocationList(favoriteLocationList);

		for (FavoriteLocationDTO favoriteLocationDTO : favoriteLocationList) {
			createFavoriteLocationMarker(favoriteLocationDTO);
		}
	}


	protected void addFavoriteLocationsPoiItem(FavoriteLocationDTO favoriteLocationDTO) {
		if (App.isPreference_key_show_favorite_locations_markers_on_map()) {
			FavoriteLocationItemViewPagerAdapter adapter = (FavoriteLocationItemViewPagerAdapter) viewPagerAdapterMap.get(MarkerType.FAVORITE);
			adapter.addFavoriteLocation(favoriteLocationDTO);
			adapter.notifyDataSetChanged();
			createFavoriteLocationMarker(favoriteLocationDTO);
		}
	}


	protected void removeFavoriteLocationMarker(FavoriteLocationDTO removeFavoriteLocationDTO) {
		MarkerType selectedMarkerType = (MarkerType) locationItemBottomSheetViewPager.getTag();

		if (selectedMarkerType == MarkerType.FAVORITE) {
			setStateOfBottomSheet(BottomSheetType.LOCATION_ITEM, BottomSheetBehavior.STATE_COLLAPSED);
		}

		FavoriteLocationItemViewPagerAdapter favoriteLocationItemViewPagerAdapter =
				(FavoriteLocationItemViewPagerAdapter) viewPagerAdapterMap.get(MarkerType.FAVORITE);
		favoriteLocationItemViewPagerAdapter.removeFavoriteLocation(removeFavoriteLocationDTO);
		favoriteLocationItemViewPagerAdapter.notifyDataSetChanged();

		int markerIndex = 0;
		List<Marker> markers = markersMap.get(MarkerType.FAVORITE);
		FavoriteMarkerHolder markerHolder = null;

		for (Marker marker : markers) {
			markerHolder = (FavoriteMarkerHolder) marker.getTag();
			if (markerHolder.favoriteLocationDTO.getId().equals(removeFavoriteLocationDTO.getId())) {
				removeMarker(MarkerType.FAVORITE, markerIndex);
				break;
			}
			markerIndex++;
		}
	}


	protected void createFavoriteLocationMarker(FavoriteLocationDTO favoriteLocationDTO) {
		Marker marker = new Marker();
		marker.setWidth(favoriteMarkerSize);
		marker.setHeight(favoriteMarkerSize);
		marker.setPosition(new LatLng(Double.parseDouble(favoriteLocationDTO.getLatitude())
				, Double.parseDouble(favoriteLocationDTO.getLongitude())));
		marker.setMap(naverMap);
		marker.setIcon(OverlayImage.fromResource(R.drawable.favorite_icon));
		marker.setOnClickListener(markerOnClickListener);
		marker.setForceShowIcon(true);
		marker.setCaptionColor(Color.BLUE);
		marker.setCaptionText(favoriteLocationDTO.getType() == FavoriteLocationDTO.ADDRESS ? favoriteLocationDTO.getAddress() :
				favoriteLocationDTO.getPlaceName());
		marker.setSubCaptionTextSize(12f);

		MarkerHolder markerHolder = new FavoriteMarkerHolder(favoriteLocationDTO, MarkerType.FAVORITE);
		marker.setTag(markerHolder);
		markersMap.get(MarkerType.FAVORITE).add(marker);
	}


	protected final Object[] createBottomSheet(int fragmentContainerViewId) {
		XmlPullParser parser = getResources().getXml(R.xml.persistent_bottom_sheet_default_attrs);
		try {
			parser.next();
			parser.nextTag();
		} catch (Exception e) {
			e.printStackTrace();
		}

		AttributeSet attr = Xml.asAttributeSet(parser);
		LinearLayout bottomSheetView = new LinearLayout(getContext(), attr);

		CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		layoutParams.setBehavior(new BottomSheetBehavior());
		bottomSheetView.setLayoutParams(layoutParams);
		bottomSheetView.setClickable(true);
		bottomSheetView.setOrientation(LinearLayout.VERTICAL);

		binding.naverMapFragmentRootLayout.addView(bottomSheetView);

		//fragmentcontainerview 추가
		FragmentContainerView fragmentContainerView = new FragmentContainerView(getContext());
		fragmentContainerView.setId(fragmentContainerViewId);
		fragmentContainerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		bottomSheetView.addView(fragmentContainerView);

		BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);
		bottomSheetBehavior.setDraggable(false);
		bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
		bottomSheetBehavior.setHideable(false);
		bottomSheetBehavior.setPeekHeight(0);

		return new Object[]{bottomSheetView, bottomSheetBehavior};
	}

	static final class BuildingBottomSheetHeightViewHolder {
		final int listHeight;
		final int infoHeight;

		public BuildingBottomSheetHeightViewHolder(int listHeight, int infoHeight) {
			this.listHeight = listHeight;
			this.infoHeight = infoHeight;
		}
	}

	static class MarkerHolder {
		final KakaoLocalDocument kakaoLocalDocument;
		final MarkerType markerType;

		public MarkerHolder(MarkerType markerType) {
			this.kakaoLocalDocument = null;
			this.markerType = markerType;
		}

		public MarkerHolder(KakaoLocalDocument kakaoLocalDocument, MarkerType markerType) {
			this.kakaoLocalDocument = kakaoLocalDocument;
			this.markerType = markerType;
		}

	}

	static class FavoriteMarkerHolder extends MarkerHolder {
		final FavoriteLocationDTO favoriteLocationDTO;

		public FavoriteMarkerHolder(FavoriteLocationDTO favoriteLocationDTO, MarkerType markerType) {
			super(markerType);
			this.favoriteLocationDTO = favoriteLocationDTO;
		}

	}
}