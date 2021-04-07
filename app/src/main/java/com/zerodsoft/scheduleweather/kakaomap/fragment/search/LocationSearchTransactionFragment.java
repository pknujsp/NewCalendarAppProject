package com.zerodsoft.scheduleweather.kakaomap.fragment.search;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

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

            }
        });

        binding.edittext.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent)
            {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    //검색
                    search(textView.getText().toString());
                    locationSearchFragment.insertHistory(textView.getText().toString());
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
                            dismiss();
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
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        fragmentTransaction.add(binding.fragmentContainerView.getId()
                , LocationSearchResultFragment.newInstance(query, iMapPoint, iMapData
                        , placesListBottomSheetController, poiItemOnClickListener, LocationSearchTransactionFragment.this)
                , LocationSearchResultFragment.TAG).hide(locationSearchFragment).addToBackStack(LocationSearchResultFragment.TAG).commit();

        binding.viewTypeButton.setVisibility(View.VISIBLE);
    }

}