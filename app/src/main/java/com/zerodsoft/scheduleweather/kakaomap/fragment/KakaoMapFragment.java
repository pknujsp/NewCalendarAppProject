package com.zerodsoft.scheduleweather.kakaomap.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapData;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.activity.map.fragment.map.BottomSheetItemView;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.List;

public class KakaoMapFragment extends Fragment implements IMapPoint, IMapData, MapView.POIItemEventListener, MapView.MapViewEventListener
{
    protected MapView mapView;
    protected ConnectivityManager.NetworkCallback networkCallback;
    protected ConnectivityManager connectivityManager;
    protected String appKey;
    protected BottomSheetBehavior bottomSheetBehavior;

    protected LinearLayout headerBar;
    protected FrameLayout mapViewContainer;
    protected LinearLayout bottomSheet;

    protected ImageButton gpsButton;

    protected BottomSheetItemView bottomSheetPlaceItemView;
    protected BottomSheetItemView bottomSheetAddressItemView;

    protected int selectedPoiItemIndex;

    protected static final int PLACE_ITEM = 0;
    protected static final int ADDRESS_ITEM = 1;

    public KakaoMapFragment()
    {

    }

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
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        headerBar = (LinearLayout) view.findViewById(R.id.map_header_bar);
        mapViewContainer = (FrameLayout) view.findViewById(R.id.map_view);
        bottomSheet = (LinearLayout) view.findViewById(R.id.map_item_bottom_sheet);
        gpsButton = (ImageButton) view.findViewById(R.id.gps_button);

        LayoutInflater layoutInflater = getLayoutInflater();
        bottomSheetPlaceItemView = (BottomSheetItemView) layoutInflater.inflate(R.layout.map_bottom_sheet_place, bottomSheet, false);
        bottomSheetAddressItemView = (BottomSheetItemView) layoutInflater.inflate(R.layout.map_bottom_sheet_address, bottomSheet, false);

        bottomSheet.addView(bottomSheetPlaceItemView, PLACE_ITEM);
        bottomSheet.addView(bottomSheetAddressItemView, ADDRESS_ITEM);

        bottomSheetAddressItemView.setVisibility(View.GONE);
        bottomSheetPlaceItemView.setVisibility(View.GONE);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setDraggable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

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
                switch (newState)
                {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        mapView.deselectPOIItem(mapView.getPOIItems()[selectedPoiItemIndex]);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset)
            {
                if (slideOffset == 0)
                {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });

        ((ImageButton) view.findViewById(R.id.zoom_in_button)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mapView.zoomIn(true);
            }
        });

        ((ImageButton) view.findViewById(R.id.zoom_out_button)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mapView.zoomOut(true);
            }
        });


        initMapView();
    }

    protected boolean checkNetwork()
    {
        if (connectivityManager.getActiveNetwork() == null)
        {
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
                return false;
            }
        }
    }

    protected void setNetworkCallback()
    {
        connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkCallback = new ConnectivityManager.NetworkCallback()
        {
            @Override
            public void onAvailable(Network network)
            {
                super.onAvailable(network);
                Toast.makeText(getActivity(), "연결됨", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLost(Network network)
            {
                super.onLost(network);
                Toast.makeText(getActivity(), "연결 끊김", Toast.LENGTH_SHORT).show();
            }
        };
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
        builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);
        connectivityManager.registerNetworkCallback(builder.build(), networkCallback);
    }


    protected void initMapView()
    {
        mapView = new MapView(requireActivity());
        mapViewContainer.addView(mapView);

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

    protected void showRequestGpsDialog()
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
        connectivityManager.unregisterNetworkCallback(networkCallback);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
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
        mapView.removeAllPOIItems();

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
        mapView.removeAllPOIItems();

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
        mapView.selectPOIItem(mapView.getPOIItems()[index], false);
        selectedPoiItemIndex = index;
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
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
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
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
        {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint)
    {

    }

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

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem)
    {
        // poiitem을 선택하였을 경우에 수행됨
        if (((CustomPoiItem) mapPOIItem).getAddressDocument() != null)
        {
            bottomSheetAddressItemView.setAddress(((CustomPoiItem) mapPOIItem).getAddressDocument());
            bottomSheetAddressItemView.setVisibility(View.VISIBLE);
            bottomSheetPlaceItemView.setVisibility(View.GONE);
        } else if (((CustomPoiItem) mapPOIItem).getPlaceDocument() != null)
        {
            bottomSheetPlaceItemView.setPlace(((CustomPoiItem) mapPOIItem).getPlaceDocument());
            bottomSheetAddressItemView.setVisibility(View.GONE);
            bottomSheetPlaceItemView.setVisibility(View.VISIBLE);
        }
        mapView.setMapCenterPoint(mapPOIItem.getMapPoint(), true);
        selectedPoiItemIndex = mapPOIItem.getTag();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
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

    protected class CustomPoiItem extends MapPOIItem
    {
        private AddressResponseDocuments addressDocument;
        private PlaceDocuments placeDocument;

        private void setAddressDocument(AddressResponseDocuments document)
        {
            this.addressDocument = document;
        }

        public void setPlaceDocument(PlaceDocuments placeDocument)
        {
            this.placeDocument = placeDocument;
        }


        public AddressResponseDocuments getAddressDocument()
        {
            return addressDocument;
        }

        public PlaceDocuments getPlaceDocument()
        {
            return placeDocument;
        }

    }
}
