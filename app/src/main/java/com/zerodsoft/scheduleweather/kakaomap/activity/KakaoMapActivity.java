package com.zerodsoft.scheduleweather.kakaomap.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.map.fragment.search.SearchFragment;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.SearchResultListFragment;
import com.zerodsoft.scheduleweather.databinding.ActivityKakaoMapBinding;
import com.zerodsoft.scheduleweather.etc.AppPermission;
import com.zerodsoft.scheduleweather.etc.FragmentStateCallback;
import com.zerodsoft.scheduleweather.etc.IPermission;
import com.zerodsoft.scheduleweather.kakaomap.fragment.KakaoMapFragment;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IBottomSheet;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapToolbar;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.INetwork;
import com.zerodsoft.scheduleweather.kakaomap.model.CustomPoiItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import net.daum.mf.map.api.MapPOIItem;

public class KakaoMapActivity extends AppCompatActivity implements IBottomSheet, IMapToolbar, INetwork, IPermission
{
    public static final int REQUEST_CODE_LOCATION = 10000;

    protected ActivityKakaoMapBinding binding;
    protected BottomSheetBehavior bottomSheetBehavior;
    protected KakaoMapFragment kakaoMapFragment;
    protected SearchView searchView;
    protected ConnectivityManager.NetworkCallback networkCallback;
    protected ConnectivityManager connectivityManager;
    protected AppPermission appPermission;

    public KakaoMapActivity()
    {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_kakao_map);

        appPermission = new AppPermission(this);
        setNetworkCallback();

        kakaoMapFragment = (KakaoMapFragment) getSupportFragmentManager().findFragmentById(R.id.kakao_map_fragment);
        kakaoMapFragment.setiBottomSheet(this);
        kakaoMapFragment.setiMapToolbar(this);
        kakaoMapFragment.setINetwork(this);

        initToolbar();
        initBottomSheet();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        connectivityManager.unregisterNetworkCallback(networkCallback);
    }

    public void onClickedSearchView()
    {
        setBottomSheetState(BottomSheetBehavior.STATE_HIDDEN);
        setItemVisibility(View.GONE);
        setFragmentVisibility(View.VISIBLE);

        getSupportFragmentManager().beginTransaction()
                .add(binding.bottomSheet.mapBottomSheetFragmentContainer.getId(), SearchFragment.newInstance(KakaoMapActivity.this, kakaoMapFragment, new FragmentStateCallback()
                {

                }), SearchFragment.TAG).commitNow();
        setBottomSheetState(BottomSheetBehavior.STATE_EXPANDED);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.default_map_toolbar, menu);

        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint(getString(R.string.input_location));
        searchView.setOnSearchClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onClickedSearchView();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                if (!query.isEmpty())
                {
                    // 현재 프래그먼트가 검색 결과 프래그먼트인 경우
                    FragmentManager fragmentManager = getSupportFragmentManager();

                    if (fragmentManager.getBackStackEntryCount() > 0)
                    {
                        FragmentManager.BackStackEntry backStackEntry = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1);

                        if (backStackEntry.getName().equals(SearchResultListFragment.TAG))
                        {
                            SearchResultListFragment.getInstance().getOnBackPressedCallback().handleOnBackPressed();
                            searchView.setQuery(query, false);
                        }
                    }
                    SearchFragment.getInstance().search(query);
                    return true;
                } else
                {
                    return false;
                }
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener()
        {
            @Override
            public boolean onClose()
            {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment showingFragment = fragmentManager.findFragmentByTag(SearchResultListFragment.TAG);
                if (showingFragment != null)
                {
                    if (showingFragment.isVisible())
                    {
                        fragmentManager.popBackStackImmediate();
                        fragmentManager.beginTransaction().remove(SearchFragment.getInstance()).commitNow();
                        closeSearchView(IBottomSheet.SEARCH_RESULT_VIEW);
                    }
                } else
                {
                    showingFragment = fragmentManager.findFragmentByTag(SearchFragment.TAG);
                    if (showingFragment != null)
                    {
                        if (showingFragment.isVisible())
                        {
                            fragmentManager.beginTransaction().remove(SearchFragment.getInstance()).commitNow();
                            closeSearchView(IBottomSheet.SEARCH_VIEW);
                        }
                    }
                }
                searchView.onActionViewCollapsed();
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void initToolbar()
    {
        setSupportActionBar(binding.mapSearchToolbar.getRoot());
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
    }

    private void initBottomSheet()
    {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.getRoot());
        bottomSheetBehavior.setDraggable(false);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback()
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

        binding.bottomSheet.mapBottomSheetToolbar.leftLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                int index = kakaoMapFragment.getSelectedPoiItemIndex() != 0 ? kakaoMapFragment.getSelectedPoiItemIndex() - 1 : kakaoMapFragment.mapView.getPOIItems().length - 1;
                kakaoMapFragment.selectPoiItem(index);
            }
        });

        binding.bottomSheet.mapBottomSheetToolbar.rightLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                int index = kakaoMapFragment.getSelectedPoiItemIndex() == kakaoMapFragment.mapView.getPOIItems().length - 1 ? 0 : kakaoMapFragment.getSelectedPoiItemIndex() + 1;
                kakaoMapFragment.selectPoiItem(index);
            }
        });

        binding.bottomSheet.mapBottomSheetToolbar.selectLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onSelectLocation();
            }
        });

        binding.bottomSheet.mapBottomSheetToolbar.removeLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onRemoveLocation();
            }
        });
    }

    public void onSelectLocation()
    {

    }

    public void onRemoveLocation()
    {

    }

    @Override
    public void setBottomSheetState(int state)
    {
        bottomSheetBehavior.setState(state);
    }

    @Override
    public int getBottomSheetState()
    {
        return bottomSheetBehavior.getState();
    }

    @Override
    public void setVisibility(int viewType, int state)
    {
        if (viewType == IBottomSheet.ADDRESS)
        {
            binding.bottomSheet.addressView.getRoot().setVisibility(state);
        } else if (viewType == IBottomSheet.PLACE)
        {
            binding.bottomSheet.placeView.getRoot().setVisibility(state);
        }
    }

    @Override
    public void setAddress(AddressResponseDocuments documents)
    {
        binding.bottomSheet.addressView.getRoot().setAddress(documents);
    }

    @Override
    public void setPlace(PlaceDocuments documents)
    {
        binding.bottomSheet.placeView.getRoot().setPlace(documents);
    }

    @Override
    public void setItemVisibility(int state)
    {
        binding.bottomSheet.bottomSheetItemLayout.setVisibility(state);
    }

    @Override
    public void setFragmentVisibility(int state)
    {
        binding.bottomSheet.mapBottomSheetFragmentContainer.setVisibility(state);
    }

    @Override
    public void closeSearchView(int viewType)
    {
        switch (viewType)
        {
            case IBottomSheet.SEARCH_VIEW:
                break;
            case IBottomSheet.SEARCH_RESULT_VIEW:
                setMenuVisibility(IMapToolbar.ALL, false);
                kakaoMapFragment.removeAllPoiItems();
                break;
        }
        setBottomSheetState(BottomSheetBehavior.STATE_HIDDEN);
        searchView.setIconified(true);
        setItemVisibility(View.VISIBLE);
        setFragmentVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.map:
            case R.id.list:
                SearchResultListFragment.getInstance().changeFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void changeOpenCloseMenuVisibility(boolean isSearching)
    {

    }

    @Override
    public void setMenuVisibility(int type, boolean state)
    {
        switch (type)
        {
            case IMapToolbar.MAP:
                binding.mapSearchToolbar.getRoot().getMenu().findItem(R.id.list).setVisible(!state);
                binding.mapSearchToolbar.getRoot().getMenu().findItem(R.id.map).setVisible(state);
                break;
            case IMapToolbar.LIST:
                binding.mapSearchToolbar.getRoot().getMenu().findItem(R.id.list).setVisible(state);
                binding.mapSearchToolbar.getRoot().getMenu().findItem(R.id.map).setVisible(!state);
                break;
            case IMapToolbar.ALL:
                binding.mapSearchToolbar.getRoot().getMenu().findItem(R.id.list).setVisible(state);
                binding.mapSearchToolbar.getRoot().getMenu().findItem(R.id.map).setVisible(state);
                break;
        }
    }

    @Override
    public void setText(String text)
    {
        searchView.setQuery(text, false);
    }

    public void setNetworkCallback()
    {
        connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkCallback = new ConnectivityManager.NetworkCallback()
        {
            @Override
            public void onAvailable(Network network)
            {
                super.onAvailable(network);
                Toast.makeText(KakaoMapActivity.this, getString(R.string.connected_network), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLost(Network network)
            {
                super.onLost(network);
                Toast.makeText(KakaoMapActivity.this, getString(R.string.disconnected_network), Toast.LENGTH_SHORT).show();
            }
        };
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
        builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);
        connectivityManager.registerNetworkCallback(builder.build(), networkCallback);
    }

    @Override
    public boolean networkAvailable()
    {
        if (connectivityManager.getActiveNetwork() == null)
        {
            Toast.makeText(KakaoMapActivity.this, getString(R.string.map_network_not_connected), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(KakaoMapActivity.this, getString(R.string.map_network_not_connected), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }

    public LocationDTO getSelectedLocationDto(int calendarId, long eventId)
    {
        // 선택된 poiitem의 리스트내 인덱스를 가져온다.
        int poiItemIndex = kakaoMapFragment.getSelectedPoiItemIndex();
        MapPOIItem[] poiItems = kakaoMapFragment.mapView.getPOIItems();
        // 인덱스로 아이템을 가져온다.
        CustomPoiItem item = (CustomPoiItem) poiItems[poiItemIndex];

        LocationDTO location = new LocationDTO();
        location.setCalendarId(calendarId);
        location.setEventId(eventId);

        // 주소인지 장소인지를 구분한다.
        if (item.getPlaceDocument() != null)
        {
            location.setPlaceId(item.getPlaceDocument().getId());
            location.setPlaceName(item.getPlaceDocument().getPlaceName());
            location.setLatitude(item.getPlaceDocument().getY());
            location.setLongitude(item.getPlaceDocument().getX());
        } else if (item.getAddressDocument() != null)
        {
            location.setAddressName(item.getAddressDocument().getAddressName());
            location.setLatitude(item.getAddressDocument().getY());
            location.setLongitude(item.getAddressDocument().getX());
        }
        return location;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_LOCATION)
        {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                // 권한 허용됨
                kakaoMapFragment.getGpsButton().callOnClick();
            } else
            {
                // 권한 거부됨
            }
        }
    }

    @Override
    public void requestPermissions(int requestCode, String... permissions)
    {
        appPermission.requestPermissions(requestCode, permissions);
    }

    @Override
    public boolean grantedPermissions(int requestCode, String... permissions)
    {
        return appPermission.grantedPermissions(requestCode, permissions);
    }
}