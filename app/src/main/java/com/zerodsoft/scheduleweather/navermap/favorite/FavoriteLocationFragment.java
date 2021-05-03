package com.zerodsoft.scheduleweather.navermap.favorite;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.naver.maps.map.overlay.Marker;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.OnBackPressedCallbackController;
import com.zerodsoft.scheduleweather.databinding.FragmentFavoriteLocationBinding;
import com.zerodsoft.scheduleweather.etc.FragmentStateCallback;
import com.zerodsoft.scheduleweather.event.places.interfaces.PoiItemOnClickListener;
import com.zerodsoft.scheduleweather.navermap.BottomSheetType;
import com.zerodsoft.scheduleweather.navermap.fragment.search.LocationSearchFragment;
import com.zerodsoft.scheduleweather.navermap.fragment.searchresult.LocationSearchResultFragment;
import com.zerodsoft.scheduleweather.navermap.interfaces.BottomSheetController;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapData;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.navermap.interfaces.SearchBarController;
import com.zerodsoft.scheduleweather.navermap.interfaces.SearchFragmentController;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;

public class FavoriteLocationFragment extends Fragment implements OnBackPressedCallbackController, OnClickedFavoriteItem
{
    private FragmentFavoriteLocationBinding binding;
    public static final String TAG = "FavoriteLocationFragment";

    private final IMapData iMapData;
    private final PoiItemOnClickListener<Marker> poiItemOnClickListener;
    private final OnBackPressedCallbackController mainFragmentOnBackPressedCallbackController;
    private final BottomSheetController bottomSheetController;

    public FavoriteLocationFragment(IMapData iMapData, OnBackPressedCallbackController onBackPressedCallbackController
            , BottomSheetController bottomSheetController
            , PoiItemOnClickListener<Marker> poiItemOnClickListener)
    {
        this.mainFragmentOnBackPressedCallbackController = onBackPressedCallbackController;
        this.bottomSheetController = bottomSheetController;
        this.iMapData = iMapData;
        this.poiItemOnClickListener = poiItemOnClickListener;
    }


    public OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true)
    {
        @Override
        public void handleOnBackPressed()
        {
            getParentFragmentManager().popBackStack();
        }
    };

    @Override
    public void onHiddenChanged(boolean hidden)
    {
        super.onHiddenChanged(hidden);

        if (hidden)
        {
            removeOnBackPressedCallback();
            if (getParentFragmentManager().getBackStackEntryCount() == 0)
            {
                mainFragmentOnBackPressedCallbackController.addOnBackPressedCallback();
            }
            bottomSheetController.setStateOfBottomSheet(BottomSheetType.FAVORITE_LOCATIONS, BottomSheetBehavior.STATE_COLLAPSED);
        } else
        {
            addOnBackPressedCallback();
            if (getParentFragmentManager().getBackStackEntryCount() == 0)
            {
                mainFragmentOnBackPressedCallbackController.removeOnBackPressedCallback();
            }
            bottomSheetController.setStateOfBottomSheet(BottomSheetType.FAVORITE_LOCATIONS, BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = FragmentFavoriteLocationBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void addOnBackPressedCallback()
    {
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    @Override
    public void removeOnBackPressedCallback()
    {
        onBackPressedCallback.remove();
    }

    @Override
    public void onClickedListItem(FavoriteLocationDTO e)
    {

    }

    @Override
    public void deleteListItem(FavoriteLocationDTO e, int position)
    {

    }

    @Override
    public void onClickedEditButton(FavoriteLocationDTO e)
    {

    }

    @Override
    public void onClickedShareButton(FavoriteLocationDTO e)
    {

    }
}