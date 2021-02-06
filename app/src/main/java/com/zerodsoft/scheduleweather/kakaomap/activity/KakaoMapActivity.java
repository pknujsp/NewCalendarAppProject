package com.zerodsoft.scheduleweather.kakaomap.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.map.fragment.search.SearchFragment;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.SearchResultListFragment;
import com.zerodsoft.scheduleweather.databinding.ActivityKakaoMapBinding;
import com.zerodsoft.scheduleweather.etc.FragmentStateCallback;
import com.zerodsoft.scheduleweather.kakaomap.fragment.KakaoMapFragment;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IBottomSheet;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapToolbar;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;

public class KakaoMapActivity extends AppCompatActivity implements IBottomSheet, IMapToolbar
{
    protected ActivityKakaoMapBinding binding;
    protected BottomSheetBehavior bottomSheetBehavior;
    protected KakaoMapFragment kakaoMapFragment;
    protected SearchView searchView;

    public KakaoMapActivity()
    {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_kakao_map);

        kakaoMapFragment = (KakaoMapFragment) getSupportFragmentManager().findFragmentById(R.id.kakao_map_fragment);
        kakaoMapFragment.setiBottomSheet(this);
        kakaoMapFragment.setiMapToolbar(this);

        initToolbar();
        initBottomSheet();
    }

    public void onClickedSearchView()
    {
        setState(BottomSheetBehavior.STATE_HIDDEN);
        setItemVisibility(View.GONE);
        setFragmentVisibility(View.VISIBLE);

        getSupportFragmentManager().beginTransaction()
                .add(binding.bottomSheet.mapBottomSheetFragmentContainer.getId(), SearchFragment.newInstance(KakaoMapActivity.this, kakaoMapFragment, new FragmentStateCallback()
                {

                }), SearchFragment.TAG).commitNow();
        setState(BottomSheetBehavior.STATE_EXPANDED);
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
                            closeSearchView(IBottomSheet.SEARCH_RESULT_VIEW);
                            break;
                    }
                }
                return false;
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
    public void closeSearchView(int viewType)
    {
        switch (viewType)
        {
            case IBottomSheet.SEARCH_VIEW:
                setState(BottomSheetBehavior.STATE_HIDDEN);
                searchView.setIconified(true);
                searchView.onActionViewCollapsed();
                setItemVisibility(View.VISIBLE);
                setFragmentVisibility(View.GONE);
                break;

            case IBottomSheet.SEARCH_RESULT_VIEW:
                kakaoMapFragment.removeAllPoiItems();
                setState(BottomSheetBehavior.STATE_HIDDEN);
                setItemVisibility(View.VISIBLE);
                setFragmentVisibility(View.GONE);
                searchView.setIconified(true);
                searchView.onActionViewCollapsed();
                break;
        }

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

}