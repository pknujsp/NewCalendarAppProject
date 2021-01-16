package com.zerodsoft.scheduleweather.activity.map.fragment.searchresult;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.interfaces.ResultFragmentChanger;
import com.zerodsoft.scheduleweather.kakaomap.KakaoMapActivity;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IBottomSheet;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapData;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.adapter.SearchResultListAdapter;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.interfaces.IndicatorCreater;
import com.zerodsoft.scheduleweather.etc.ViewPagerIndicator;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapToolbar;

public class SearchResultListFragment extends Fragment implements IndicatorCreater, ResultFragmentChanger
{
    public static final String TAG = "SearchResultFragment";
    private static SearchResultListFragment instance;

    private ViewPager2 fragmentsViewPager;
    private SearchResultListAdapter searchResultListAdapter;

    private ViewPagerIndicator viewPagerIndicator;
    private final String SEARCH_WORD;

    private OnPageCallback onPageCallback;
    private OnBackPressedCallback onBackPressedCallback;
    private IMapPoint iMapPoint;
    private IMapData iMapData;
    private IMapToolbar iMapToolbar;
    private IBottomSheet iBottomSheet;

    private boolean isShowList = true;

    @Override
    public void setIndicator(int fragmentSize)
    {
        viewPagerIndicator.createDot(0, fragmentSize);
    }

    public SearchResultListFragment(String searchWord, IMapPoint iMapPoint, IMapData iMapData, IMapToolbar iMapToolbar, IBottomSheet iBottomSheet)
    {
        this.SEARCH_WORD = searchWord;
        this.iMapPoint = iMapPoint;
        this.iMapData = iMapData;
        this.iMapToolbar = iMapToolbar;
        this.iBottomSheet = iBottomSheet;
    }

    public static SearchResultListFragment getInstance()
    {
        return instance;
    }

    public static SearchResultListFragment newInstance(String searchWord, IMapPoint iMapPoint, IMapData iMapData, IMapToolbar iMapToolbar, IBottomSheet iBottomSheet)
    {
        instance = new SearchResultListFragment(searchWord, iMapPoint, iMapData, iMapToolbar, iBottomSheet);
        return instance;
    }


    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        onBackPressedCallback = new OnBackPressedCallback(true)
        {
            @Override
            public void handleOnBackPressed()
            {
                if (isShowList)
                {
                    // list인 경우
                    getParentFragmentManager().popBackStack();
                    iMapData.removeAllPoiItems();
                    iMapToolbar.changeOpenCloseMenuVisibility(false);
                } else
                {
                    // map인 경우
                    changeFragment();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
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
        return inflater.inflate(R.layout.fragment_search_result_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        fragmentsViewPager = (ViewPager2) view.findViewById(R.id.map_search_result_viewpager);
        viewPagerIndicator = (ViewPagerIndicator) view.findViewById(R.id.map_result_view_pager_indicator);

        searchResultListAdapter = new SearchResultListAdapter(this, iMapPoint, iMapData, iBottomSheet, SEARCH_WORD);
        onPageCallback = new OnPageCallback();

        fragmentsViewPager.setAdapter(searchResultListAdapter);
        fragmentsViewPager.registerOnPageChangeCallback(onPageCallback);
        viewPagerIndicator.createDot(0, 2);
    }


    @Override
    public void onDetach()
    {
        super.onDetach();
        onBackPressedCallback.remove();
    }

    @Override
    public void changeFragment()
    {
        iMapToolbar.setViewTypeMenuVisibility(isShowList ? KakaoMapActivity.LIST : KakaoMapActivity.MAP);

        if (isShowList)
        {
            // to map
            // 버튼 이미지, 프래그먼트 숨김/보이기 설정
            iMapData.showAllPoiItems();
            iBottomSheet.setItemVisibility(View.VISIBLE);
            iBottomSheet.setFragmentVisibility(View.GONE);
            iBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else
        {
            // to list
            iMapData.backToPreviousView();
            iBottomSheet.setItemVisibility(View.GONE);
            iBottomSheet.setFragmentVisibility(View.VISIBLE);
            iBottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
        }

        isShowList = !isShowList;
    }

    class OnPageCallback extends ViewPager2.OnPageChangeCallback
    {
        public int lastPosition;

        @Override
        public void onPageSelected(int position)
        {
            super.onPageSelected(position);
            lastPosition = position;
            viewPagerIndicator.selectDot(position);
        }
    }

}