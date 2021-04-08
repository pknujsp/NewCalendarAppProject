package com.zerodsoft.scheduleweather.kakaomap.fragment.search;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentLocationSearchBarBinding;
import com.zerodsoft.scheduleweather.etc.FragmentStateCallback;
import com.zerodsoft.scheduleweather.event.places.interfaces.PoiItemOnClickListener;
import com.zerodsoft.scheduleweather.kakaomap.fragment.searchresult.LocationSearchResultFragment;
import com.zerodsoft.scheduleweather.kakaomap.fragment.searchresult.adapter.SearchResultListAdapter;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapData;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.PlacesListBottomSheetController;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.SearchBarController;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;

public class LocationSearchTransactionFragment extends DialogFragment implements SearchBarController
{
    public static final String TAG = "LocationSearchFragment";

    private FragmentLocationSearchBarBinding binding;

    private Drawable mapDrawable;
    private Drawable listDrawable;

    private final IMapPoint iMapPoint;
    private final IMapData iMapData;
    private final FragmentStateCallback fragmentStateCallback;
    private final PlacesListBottomSheetController placesListBottomSheetController;
    private final PoiItemOnClickListener poiItemOnClickListener;

    private SearchResultListAdapter searchResultListAdapter;

    private LocationSearchFragment locationSearchFragment;
    private LocationSearchResultFragment locationSearchResultFragment;

    private boolean isVisibleList = true;

    public LocationSearchTransactionFragment(Fragment fragment, FragmentStateCallback fragmentStateCallback)
    {
        this.iMapPoint = (IMapPoint) fragment;
        this.iMapData = (IMapData) fragment;
        this.placesListBottomSheetController = (PlacesListBottomSheetController) fragment;
        this.poiItemOnClickListener = (PoiItemOnClickListener) fragment;
        this.fragmentStateCallback = fragmentStateCallback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.AppTheme_FullScreenDialog);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        return new Dialog(getActivity(), getTheme())
        {
            @Override
            public void onBackPressed()
            {
                if (LocationSearchResultFragment.getInstance() != null)
                {
                    if (isVisibleList)
                    {
                        // list인 경우
                        iMapData.removeAllPoiItems();
                        binding.viewTypeButton.setVisibility(View.GONE);
                        getChildFragmentManager().popBackStackImmediate();
                        LocationSearchResultFragment.close();
                    } else
                    {
                        // map인 경우
                        placesListBottomSheetController.setPlacesListBottomSheetState(BottomSheetBehavior.STATE_COLLAPSED);
                        changeFragment();
                    }
                } else if (locationSearchFragment.isVisible())
                {
                    super.onBackPressed();
                    fragmentStateCallback.onChangedState(FragmentStateCallback.ON_REMOVED);
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = FragmentLocationSearchBarBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        binding.closeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });

        binding.viewTypeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                changeFragment();
            }
        });


        binding.edittext.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent)
            {
                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                {
                    //검색
                    search(binding.edittext.getText().toString());
                    locationSearchFragment.insertHistory(binding.edittext.getText().toString());
                    return true;
                }
                return false;
            }
        });

        binding.viewTypeButton.setVisibility(View.GONE);

        mapDrawable = ContextCompat.getDrawable(getContext(), R.drawable.map_icon);
        listDrawable = ContextCompat.getDrawable(getContext(), R.drawable.list_icon);

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        locationSearchFragment = new LocationSearchFragment(iMapPoint, iMapData,
                new FragmentStateCallback()
                {
                    @Override
                    public void onChangedState(int state)
                    {
                        if (state == FragmentStateCallback.ON_REMOVED)
                        {

                        }
                    }
                }, placesListBottomSheetController, poiItemOnClickListener, LocationSearchTransactionFragment.this);
        fragmentTransaction.add(binding.fragmentContainerView.getId(), locationSearchFragment, LocationSearchFragment.TAG).commitNow();
    }

    @Override
    public void setQuery(String query, boolean submit)
    {
        if (KakaoLocalApiCategoryUtil.isCategory(query))
        {
            binding.edittext.setText(KakaoLocalApiCategoryUtil.getDefaultDescription(query));
        } else
        {
            binding.edittext.setText(query);
        }

        if (submit)
        {
            search(query);
        } else
        {

        }
    }

    @Override
    public void changeViewTypeImg(int type)
    {
        if (type == SearchBarController.MAP)
        {
            binding.viewTypeButton.setImageDrawable(mapDrawable);
        } else
        {
            binding.viewTypeButton.setImageDrawable(listDrawable);
        }
    }

    private void search(String query)
    {
        if (LocationSearchResultFragment.getInstance() == null)
        {
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

            fragmentTransaction.add(binding.fragmentContainerView.getId()
                    , LocationSearchResultFragment.newInstance(query, iMapPoint, iMapData
                            , placesListBottomSheetController, poiItemOnClickListener, LocationSearchTransactionFragment.this)
                    , LocationSearchResultFragment.TAG).hide(locationSearchFragment).addToBackStack(LocationSearchResultFragment.TAG).commit();

            binding.viewTypeButton.setVisibility(View.VISIBLE);
        } else
        {
            LocationSearchResultFragment.getInstance().search(query);
        }
    }

    @Override
    public void showList()
    {
        isVisibleList = true;
        getDialog().show();
    }

    @Override
    public void showMap()
    {
        isVisibleList = false;
        getDialog().hide();
    }

    public void changeFragment()
    {
        changeViewTypeImg(isVisibleList ? SearchBarController.MAP : SearchBarController.LIST);

        if (isVisibleList)
        {
            // to map
            // 버튼 이미지, 프래그먼트 숨김/보이기 설정
            iMapData.showAllPoiItems();
            showMap();
        } else
        {
            // to list
            iMapData.backToPreviousView();
            showList();
        }

    }

}