package com.zerodsoft.scheduleweather.kakaomap.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.map.util.RequestLocationTimer;
import com.zerodsoft.scheduleweather.databinding.FragmentMapBinding;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IBottomSheet;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapData;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.activity.map.fragment.map.BottomSheetItemView;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapToolbar;
import com.zerodsoft.scheduleweather.kakaomap.model.CustomPoiItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.util.List;
import java.util.Timer;

public class KakaoMapFragment extends Fragment implements IMapPoint, IMapData, MapView.POIItemEventListener, MapView.MapViewEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener
{
    protected FragmentMapBinding binding;
    public MapView mapView;
    public ConnectivityManager.NetworkCallback networkCallback;
    public ConnectivityManager connectivityManager;
    public String appKey;
    public MapReverseGeoCoder mapReverseGeoCoder;
    public LocationManager locationManager;

    public IBottomSheet iBottomSheet;
    public IMapToolbar iMapToolbar;

    private ImageButton zoomInButton;
    private ImageButton zoomOutButton;
    private ImageButton gpsButton;

    private int selectedPoiItemIndex;
    private boolean isSelectedPoiItem;

    public KakaoMapFragment()
    {

    }

    public void setiBottomSheet(IBottomSheet iBottomSheet)
    {
        this.iBottomSheet = iBottomSheet;
    }

    public KakaoMapFragment setiMapToolbar(IMapToolbar iMapToolbar)
    {
        this.iMapToolbar = iMapToolbar;
        return this;
    }

    public final LocationListener locationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(location.getLatitude(), location.getLongitude()), true);
            mapReverseGeoCoder.findAddressForMapPointSync(appKey, mapView.getMapCenterPoint(), MapReverseGeoCoder.AddressType.FullAddress);
            locationManager.removeUpdates(locationListener);
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
        setNetworkCallback();
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
        binding = FragmentMapBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        zoomInButton = binding.mapButtonsLayout.zoomInButton;
        zoomOutButton = binding.mapButtonsLayout.zoomOutButton;
        gpsButton = binding.mapButtonsLayout.gpsButton;

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
                boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                int fineLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
                int coarseLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

                if (checkNetwork())
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
                                        int fineLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
                                        int coarseLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
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
        initMapView();

        mapView.setPOIItemEventListener(this);
        mapView.setMapViewEventListener(this);
    }

    public boolean checkNetwork()
    {
        if (connectivityManager.getActiveNetwork() == null)
        {
            Toast.makeText(getActivity(), getString(R.string.map_network_not_connected), Toast.LENGTH_SHORT).show();
            return false;
        } else
        {
            NetworkCapabilities nc = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());

            if (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
            {
                return true;
            } else
            {
                Toast.makeText(getActivity(), getString(R.string.map_network_not_connected), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }

    public void setNetworkCallback()
    {
        connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkCallback = new ConnectivityManager.NetworkCallback()
        {
            @Override
            public void onAvailable(Network network)
            {
                super.onAvailable(network);
                Toast.makeText(getActivity(), getString(R.string.connected_network), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLost(Network network)
            {
                super.onLost(network);
                Toast.makeText(getActivity(), getString(R.string.disconnected_network), Toast.LENGTH_SHORT).show();
            }
        };
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
        builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);
        connectivityManager.registerNetworkCallback(builder.build(), networkCallback);
    }


    public void initMapView()
    {
        mapView = new MapView(requireActivity());
        binding.mapView.addView(mapView);

        if (appKey == null)
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
        }
        mapReverseGeoCoder = new MapReverseGeoCoder(appKey, mapView.getMapCenterPoint(), this, getActivity());
        mapReverseGeoCoder.startFindingAddress(MapReverseGeoCoder.AddressType.FullAddress);
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
    public void onDetach()
    {
        super.onDetach();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        connectivityManager.unregisterNetworkCallback(networkCallback);
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
    public void createPlacesPoiItems(List<PlaceDocuments> placeDocuments)
    {
        if (mapView.getPOIItems().length > 0)
        {
            mapView.removeAllPOIItems();
        }
        if (!placeDocuments.isEmpty())
        {
            CustomPoiItem[] poiItems = new CustomPoiItem[placeDocuments.size()];

            int index = 0;
            for (PlaceDocuments document : placeDocuments)
            {
                poiItems[index] = new CustomPoiItem();
                poiItems[index].setItemName(document.getPlaceName());
                poiItems[index].setMapPoint(MapPoint.mapPointWithGeoCoord(document.getY(), document.getX()));
                poiItems[index].setPlaceDocument(document);
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
            CustomPoiItem[] poiItems = new CustomPoiItem[addressDocuments.size()];

            int index = 0;
            for (AddressResponseDocuments document : addressDocuments)
            {
                poiItems[index] = new CustomPoiItem();
                poiItems[index].setItemName(document.getAddressName());
                poiItems[index].setMapPoint(MapPoint.mapPointWithGeoCoord(document.getY(), document.getX()));
                poiItems[index].setAddressDocument(document);
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
        mapView.selectPOIItem(mapView.getPOIItems()[index], true);
        onPOIItemSelected(mapView, mapView.getPOIItems()[index]);
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
    public void onMapViewInitialized(MapView mapView)
    {

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
            iBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
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
        if (checkNetwork())
        {
            // mapReverseGeoCoder = new MapReverseGeoCoder(appKey, mapPoint, this, getActivity());
            binding.mapButtonsLayout.currentAddress.setText(mapReverseGeoCoder.findAddressForMapPointSync(appKey, mapPoint, MapReverseGeoCoder.AddressType.FullAddress));
            // mapReverseGeoCoder.startFindingAddress(MapReverseGeoCoder.AddressType.ShortAddress);
        }
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem)
    {
        selectedPoiItemIndex = mapPOIItem.getTag();
        isSelectedPoiItem = true;

        // poiitem을 선택하였을 경우에 수행됨
        mapView.setMapCenterPoint(mapPOIItem.getMapPoint(), true);
        CustomPoiItem poiItem = (CustomPoiItem) mapPOIItem;
        // bottomsheet에 위치 정보 데이터를 설정한다.
        if (poiItem.getAddressDocument() != null)
        {
            iBottomSheet.setAddress(poiItem.getAddressDocument());
            iBottomSheet.setVisibility(IBottomSheet.ADDRESS, View.VISIBLE);
            iBottomSheet.setVisibility(IBottomSheet.PLACE, View.GONE);
        } else if (poiItem.getPlaceDocument() != null)
        {
            iBottomSheet.setPlace(poiItem.getPlaceDocument());
            iBottomSheet.setVisibility(IBottomSheet.ADDRESS, View.GONE);
            iBottomSheet.setVisibility(IBottomSheet.PLACE, View.VISIBLE);
        }

        // 시트가 열리지 않은 경우 연다.
        if (iBottomSheet.getState() != BottomSheetBehavior.STATE_EXPANDED)
        {
            iBottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem)
    {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType)
    {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint)
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

    public boolean isSelectedPoiItem()
    {
        return isSelectedPoiItem;
    }

    public int getSelectedPoiItemIndex()
    {
        return selectedPoiItemIndex;
    }

    public ImageButton getZoomInButton()
    {
        return zoomInButton;
    }

    public ImageButton getZoomOutButton()
    {
        return zoomOutButton;
    }

    public ImageButton getGpsButton()
    {
        return gpsButton;
    }
}
