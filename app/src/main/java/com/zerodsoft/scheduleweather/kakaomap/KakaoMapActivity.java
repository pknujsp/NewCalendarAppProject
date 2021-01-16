package com.zerodsoft.scheduleweather.kakaomap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.map.fragment.map.MapFragment;
import com.zerodsoft.scheduleweather.activity.map.fragment.search.SearchFragment;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.SearchResultListFragment;
import com.zerodsoft.scheduleweather.databinding.ActivityKakaoMapBinding;
import com.zerodsoft.scheduleweather.etc.FragmentStateCallback;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IBottomSheet;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapToolbar;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;

import java.util.List;

public class KakaoMapActivity extends AppCompatActivity implements IBottomSheet, IMapToolbar
{
    public ActivityKakaoMapBinding binding;
    public BottomSheetBehavior bottomSheetBehavior;
    public MapFragment kakaoMapFragment;

    private SearchView searchView;
    public static final int MAP = 0;
    public static final int LIST = 1;

    public KakaoMapActivity()
    {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_kakao_map);
        initToolbar();
        initBottomSheet();
    }

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
                if (SearchFragment.getInstance() == null)
                {
                    setState(BottomSheetBehavior.STATE_HIDDEN);
                    setItemVisibility(View.GONE);
                    setFragmentVisibility(View.VISIBLE);

                    getSupportFragmentManager().beginTransaction()
                            .add(binding.bottomSheet.mapBottomSheetFragmentContainer.getId(), SearchFragment.newInstance(KakaoMapActivity.this, kakaoMapFragment, new FragmentStateCallback()
                            {
                                @Override
                                public void onChangedState(int state)
                                {
                                    if (state == FragmentStateCallback.ON_REMOVED)
                                    {
                                        searchView.setIconified(true);
                                        searchView.clearFocus();
                                    }
                                }
                            }), SearchFragment.TAG).addToBackStack(SearchFragment.TAG).commit();
                    setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                if (!query.isEmpty())
                {
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
                final int topIndexStack = fragmentManager.getBackStackEntryCount() - 1;

                if (topIndexStack >= 0)
                {
                    FragmentManager.BackStackEntry backStackEntry = fragmentManager.getBackStackEntryAt(topIndexStack);
                    String name = backStackEntry.getName();

                    switch (name)
                    {
                        case SearchFragment.TAG:
                            searchView.setQuery("", false);
                            break;
                        case SearchResultListFragment.TAG:
                            setState(BottomSheetBehavior.STATE_HIDDEN);
                            setItemVisibility(View.VISIBLE);
                            setFragmentVisibility(View.GONE);
                            fragmentManager.popBackStack();
                            fragmentManager.popBackStack();
                            searchView.setIconified(true);
                            searchView.clearFocus();
                            break;
                    }
                }
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
                        if (kakaoMapFragment.isSelectedPoiItem)
                        {
                            kakaoMapFragment.deselectPoiItem();
                        }
                        // 검색 화면인 경우
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        final int topIndexStack = fragmentManager.getBackStackEntryCount() - 1;

                        if (topIndexStack >= 0)
                        {
                            FragmentManager.BackStackEntry backStackEntry = fragmentManager.getBackStackEntryAt(topIndexStack);
                            String name = backStackEntry.getName();

                            switch (name)
                            {
                                case SearchFragment.TAG:
                                    fragmentManager.popBackStack();
                                    searchView.setIconified(true);
                                    searchView.clearFocus();
                                    setItemVisibility(View.VISIBLE);
                                    setFragmentVisibility(View.GONE);
                                    break;
                                case SearchResultListFragment.TAG:
                                    SearchResultListFragment.getInstance().changeFragment();
                                    break;
                            }
                        }
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

        binding.bottomSheet.mapBottomSheetToolbar.leftLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                int index = kakaoMapFragment.selectedPoiItemIndex != 0 ? kakaoMapFragment.selectedPoiItemIndex - 1 : kakaoMapFragment.mapView.getPOIItems().length - 1;
                kakaoMapFragment.selectPoiItem(index);
            }
        });

        binding.bottomSheet.mapBottomSheetToolbar.rightLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                int index = kakaoMapFragment.selectedPoiItemIndex == kakaoMapFragment.mapView.getPOIItems().length - 1 ? 0 : kakaoMapFragment.selectedPoiItemIndex + 1;
                kakaoMapFragment.selectPoiItem(index);
            }
        });
    }


    @Override
    public void setState(int state)
    {
        bottomSheetBehavior.setState(state);
    }

    @Override
    public int getState()
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
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.search:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void changeOpenCloseMenuVisibility(boolean isSearching)
    {

    }

    @Override
    public void setViewTypeMenuVisibility(int type)
    {
        switch (type)
        {
            case MAP:
                binding.mapSearchToolbar.getRoot().getMenu().getItem(1).setVisible(true);
                binding.mapSearchToolbar.getRoot().getMenu().getItem(0).setVisible(false);
                break;
            case LIST:
                binding.mapSearchToolbar.getRoot().getMenu().getItem(1).setVisible(false);
                binding.mapSearchToolbar.getRoot().getMenu().getItem(0).setVisible(true);
                break;
        }
    }

    /*
          bottomSheetLayout.findViewById(R.id.choice_location_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                LocationDTO locationDTO = null;
                CustomPoiItem poiItem = (CustomPoiItem) mapView.getPOIItems()[selectedPoiItemIndex];
                if (poiItem.getAddressDocument() != null)
                {
                    AddressResponseDocuments document = poiItem.getAddressDocument();
                    AddressDTO addressDTO = new AddressDTO();
                    addressDTO.setAddressName(document.getAddressName());
                    addressDTO.setLatitude(Double.toString(document.getY()));
                    addressDTO.setLongitude(Double.toString(document.getX()));

                    locationDTO = addressDTO;
                } else if (poiItem.getPlaceDocument() != null)
                {
                    PlaceDocuments document = poiItem.getPlaceDocument();
                    PlaceDTO placeDTO = new PlaceDTO();
                    placeDTO.setPlaceName(document.getPlaceName());
                    placeDTO.setId(Integer.parseInt(document.getId()));
                    placeDTO.setLongitude(Double.toString(document.getX()));
                    placeDTO.setLatitude(Double.toString(document.getY()));

                    locationDTO = placeDTO;
                }
                iCatchedLocation.choiceLocation(locationDTO);
            }
        });

     */
}