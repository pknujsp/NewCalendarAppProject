package com.zerodsoft.scheduleweather.kakaomap.fragment.searchheader;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.SearchHistoryDataController;
import com.zerodsoft.scheduleweather.databinding.FragmentLocationSearchBarBinding;
import com.zerodsoft.scheduleweather.kakaomap.fragment.search.LocationSearchFragment;
import com.zerodsoft.scheduleweather.kakaomap.fragment.searchresult.LocationSearchResultFragment;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapData;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.PlacesListBottomSheetController;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.SearchBarController;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.SearchFragmentController;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;
import com.zerodsoft.scheduleweather.room.dto.SearchHistoryDTO;

public class MapHeaderSearchFragment extends Fragment implements SearchBarController
{
    public static final String TAG = "MapHeaderSearchFragment";
    private FragmentLocationSearchBarBinding binding;

    private final LocationSearchListener locationSearchListener;
    private final SearchFragmentController searchFragmentController;
    private final IMapData iMapData;
    private final PlacesListBottomSheetController placesListBottomSheetController;
    private SearchHistoryDataController<SearchHistoryDTO> searchHistoryDataController;

    private Drawable mapDrawable;
    private Drawable listDrawable;

    public MapHeaderSearchFragment(Fragment fragment)
    {
        this.locationSearchListener = (LocationSearchListener) fragment;
        this.searchFragmentController = (SearchFragmentController) fragment;
        this.placesListBottomSheetController = (PlacesListBottomSheetController) fragment;
        this.iMapData = (IMapData) fragment;
    }

    public void setSearchHistoryDataController(SearchHistoryDataController<SearchHistoryDTO> searchHistoryDataController)
    {
        this.searchHistoryDataController = searchHistoryDataController;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
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
                //현재 프래그먼트 파악
                FragmentManager fragmentManager = getParentFragmentManager();

                if (fragmentManager.findFragmentByTag(LocationSearchFragment.TAG) != null)
                {
                    if (fragmentManager.findFragmentByTag(LocationSearchFragment.TAG).isVisible())
                    {
                        searchFragmentController.closeSearchFragments(LocationSearchFragment.TAG);
                    }
                }

                if (fragmentManager.findFragmentByTag(LocationSearchResultFragment.TAG) != null)
                {
                    if (fragmentManager.findFragmentByTag(LocationSearchResultFragment.TAG).isVisible())
                    {
                        searchFragmentController.closeSearchFragments(LocationSearchResultFragment.TAG);
                        searchFragmentController.closeSearchFragments(LocationSearchFragment.TAG);
                    }
                }

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
                    binding.viewTypeButton.setVisibility(View.VISIBLE);
                    locationSearchListener.search(binding.edittext.getText().toString());
                    searchHistoryDataController.insertValueToHistory(binding.edittext.getText().toString());
                    return true;
                }
                return false;
            }
        });

        binding.viewTypeButton.setVisibility(View.GONE);

        mapDrawable = ContextCompat.getDrawable(getContext(), R.drawable.map_icon);
        listDrawable = ContextCompat.getDrawable(getContext(), R.drawable.list_icon);
    }

    public FragmentLocationSearchBarBinding getBinding()
    {
        return binding;
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
            locationSearchListener.search(query);
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

    public void changeFragment()
    {
        boolean bottomSheetStateIsExpanded = searchFragmentController.getStateOfSearchBottomSheet() == BottomSheetBehavior.STATE_EXPANDED;
        changeViewTypeImg(bottomSheetStateIsExpanded ? SearchBarController.LIST : SearchBarController.MAP);

        if (bottomSheetStateIsExpanded)
        {
            // to map
            // 버튼 이미지, 프래그먼트 숨김/보이기 설정
            iMapData.showAllPoiItems();
            placesListBottomSheetController.setPlacesListBottomSheetState(BottomSheetBehavior.STATE_COLLAPSED);
            searchFragmentController.setStateOfSearchBottomSheet(BottomSheetBehavior.STATE_COLLAPSED);
        } else
        {
            // to list
            iMapData.backToPreviousView();
            placesListBottomSheetController.setPlacesListBottomSheetState(BottomSheetBehavior.STATE_COLLAPSED);
            searchFragmentController.setStateOfSearchBottomSheet(BottomSheetBehavior.STATE_EXPANDED);
        }
    }


    @Override
    public void setViewTypeVisibility(int visibility)
    {
        binding.viewTypeButton.setVisibility(visibility);
    }


    public interface LocationSearchListener
    {
        void search(String query);
    }

}
