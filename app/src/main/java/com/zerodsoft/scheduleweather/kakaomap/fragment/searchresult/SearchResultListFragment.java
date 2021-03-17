package com.zerodsoft.scheduleweather.kakaomap.fragment.searchresult;

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
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.OnClickedLocListItem;
import com.zerodsoft.scheduleweather.kakaomap.fragment.searchresult.interfaces.ResultFragmentChanger;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.PlacesListBottomSheetController;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.SearchBottomSheetController;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapData;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.kakaomap.fragment.searchresult.adapter.SearchResultListAdapter;
import com.zerodsoft.scheduleweather.kakaomap.fragment.searchresult.interfaces.IndicatorCreater;
import com.zerodsoft.scheduleweather.etc.ViewPagerIndicator;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapToolbar;
import com.zerodsoft.scheduleweather.kakaomap.model.SearchResultChecker;
import com.zerodsoft.scheduleweather.kakaomap.model.callback.CheckerCallback;
import com.zerodsoft.scheduleweather.kakaomap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.KakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressKakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceKakaoLocalResponse;

import java.util.ArrayList;
import java.util.List;

public class SearchResultListFragment extends Fragment implements IndicatorCreater, ResultFragmentChanger, OnClickedLocListItem
{
    public static final String TAG = "SearchResultFragment";
    private static SearchResultListFragment instance;

    private FrameLayout rootLayout;
    private ViewPager2 fragmentsViewPager;
    private SearchResultListAdapter searchResultListAdapter;

    private ViewPagerIndicator viewPagerIndicator;
    private final String SEARCH_WORD;

    private OnPageCallback onPageCallback;
    private OnBackPressedCallback onBackPressedCallback;
    private IMapPoint iMapPoint;
    private IMapData iMapData;
    private IMapToolbar iMapToolbar;
    private SearchBottomSheetController searchBottomSheetController;
    private PlacesListBottomSheetController placesListBottomSheetController;

    private ProgressBar progressBar;
    private TextView errorTextView;
    private boolean isVisibleList = true;

    @Override
    public void setIndicator(int fragmentSize)
    {
        viewPagerIndicator.createDot(0, fragmentSize);
    }

    public SearchResultListFragment(String searchWord, IMapPoint iMapPoint, IMapData iMapData, IMapToolbar iMapToolbar
            , SearchBottomSheetController searchBottomSheetController, PlacesListBottomSheetController placesListBottomSheetController)
    {
        this.SEARCH_WORD = searchWord;
        this.iMapPoint = iMapPoint;
        this.iMapData = iMapData;
        this.iMapToolbar = iMapToolbar;
        this.searchBottomSheetController = searchBottomSheetController;
        this.placesListBottomSheetController = placesListBottomSheetController;
    }

    public static SearchResultListFragment getInstance()
    {
        return instance;
    }

    public static SearchResultListFragment newInstance(String searchWord, IMapPoint iMapPoint, IMapData iMapData
            , IMapToolbar iMapToolbar, SearchBottomSheetController searchBottomSheetController, PlacesListBottomSheetController placesListBottomSheetController)
    {
        instance = new SearchResultListFragment(searchWord, iMapPoint, iMapData
                , iMapToolbar, searchBottomSheetController, placesListBottomSheetController);
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
                if (isVisibleList)
                {
                    // list인 경우, to map
                    iMapData.removeAllPoiItems();
                    iMapToolbar.setText("");
                    iMapToolbar.setMenuVisibility(IMapToolbar.ALL, false);
                    getParentFragmentManager().popBackStackImmediate();
                    iMapPoint.setMapVisibility(View.GONE);
                    onBackPressedCallback.remove();
                } else
                {
                    // map인 경우
                    placesListBottomSheetController.setPlacesListBottomSheetState(BottomSheetBehavior.STATE_HIDDEN);
                    changeFragment();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    public OnBackPressedCallback getOnBackPressedCallback()
    {
        return onBackPressedCallback;
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

        rootLayout = (FrameLayout) view.findViewById(R.id.search_fragment_root_layout);
        fragmentsViewPager = (ViewPager2) view.findViewById(R.id.map_search_result_viewpager);
        viewPagerIndicator = (ViewPagerIndicator) view.findViewById(R.id.map_result_view_pager_indicator);
        progressBar = (ProgressBar) view.findViewById(R.id.map_request_progress_bar);
        errorTextView = (TextView) view.findViewById(R.id.error_text);

        searchResultListAdapter = new SearchResultListAdapter(SearchResultListFragment.this);

        final LocalApiPlaceParameter addressParameter = LocalParameterUtil.getAddressParameter(SEARCH_WORD, LocalApiPlaceParameter.DEFAULT_SIZE
                , LocalApiPlaceParameter.DEFAULT_PAGE);
        final LocalApiPlaceParameter placeParameter = LocalParameterUtil.getPlaceParameter(SEARCH_WORD, iMapPoint.getLatitude(), iMapPoint.getLongitude(),
                LocalApiPlaceParameter.DEFAULT_SIZE, LocalApiPlaceParameter.DEFAULT_PAGE, LocalApiPlaceParameter.SEARCH_CRITERIA_SORT_TYPE_ACCURACY);

        // 주소, 주소 & 장소, 장소, 검색 결과없음 인 경우
        SearchResultChecker.checkExisting(addressParameter, placeParameter, new CheckerCallback<DataWrapper<KakaoLocalResponse>>()
        {
            @Override
            public void onResult()
            {
                // 오류 여부 확인
                for (DataWrapper<KakaoLocalResponse> response : list)
                {
                    if (response.getException() != null)
                    {
                        // error, exception
                        errorTextView.setText(getContext().getString(R.string.error) + ", (" + response.getException().getMessage() + ")");
                        errorTextView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        return;
                    }
                }

                // 오류 없으면 진행
                int totalResultCount = 0;

                for (DataWrapper<KakaoLocalResponse> response : list)
                {
                    if (response.getData() instanceof AddressKakaoLocalResponse)
                    {
                        totalResultCount += response.getData().size();
                    } else if (response.getData() instanceof PlaceKakaoLocalResponse)
                    {
                        totalResultCount += response.getData().size();
                    }
                }

                if (totalResultCount == 0)
                {
                    // 검색 결과 없음
                    errorTextView.setText(getContext().getString(R.string.not_founded_search_result));
                    errorTextView.setVisibility(View.VISIBLE);
                    // searchview클릭 후 재검색 시 search fragment로 이동
                } else
                {
                    List<Fragment> fragments = new ArrayList<>();

                    for (DataWrapper<KakaoLocalResponse> response : list)
                    {
                        if (response.getData() instanceof PlaceKakaoLocalResponse)
                        {
                            PlaceKakaoLocalResponse placeKakaoLocalResponse = (PlaceKakaoLocalResponse) response.getData();

                            if (!placeKakaoLocalResponse.getPlaceDocuments().isEmpty())
                            {
                                fragments.add(new PlaceListFragment(iMapPoint, SEARCH_WORD, iMapData, SearchResultListFragment.this));
                            }
                        } else if (response.getData() instanceof AddressKakaoLocalResponse)
                        {
                            AddressKakaoLocalResponse addressKakaoLocalResponse = (AddressKakaoLocalResponse) response.getData();

                            if (!addressKakaoLocalResponse.getAddressResponseDocumentsList().isEmpty())
                            {
                                fragments.add(new AddressListFragment(SEARCH_WORD, iMapData, SearchResultListFragment.this));
                            }
                        }
                    }

                    searchResultListAdapter.setFragments(fragments);
                    fragmentsViewPager.setAdapter(searchResultListAdapter);

                    onPageCallback = new OnPageCallback();
                    fragmentsViewPager.registerOnPageChangeCallback(onPageCallback);
                    viewPagerIndicator.createDot(0, fragments.size());
                    iMapToolbar.setMenuVisibility(IMapToolbar.LIST, true);
                }
                progressBar.setVisibility(View.GONE);
            }
        });
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
    public void changeFragment()
    {
        iMapToolbar.setMenuVisibility(isVisibleList ? IMapToolbar.MAP : IMapToolbar.LIST, true);

        if (isVisibleList)
        {
            // to map
            // 버튼 이미지, 프래그먼트 숨김/보이기 설정
            iMapPoint.setMapVisibility(View.VISIBLE);
            iMapData.showAllPoiItems();
            searchBottomSheetController.setSearchBottomSheetState(BottomSheetBehavior.STATE_HIDDEN);
        } else
        {
            // to list
            iMapPoint.setMapVisibility(View.GONE);
            iMapData.backToPreviousView();
            searchBottomSheetController.setSearchBottomSheetState(BottomSheetBehavior.STATE_EXPANDED);
            placesListBottomSheetController.setPlacesListBottomSheetState(BottomSheetBehavior.STATE_HIDDEN);
        }

        isVisibleList = !isVisibleList;
    }

    @Override
    public void onClickedLocItem(int index)
    {
        isVisibleList = false;

        iMapToolbar.setMenuVisibility(IMapToolbar.MAP, true);
        iMapData.selectPoiItem(index);
        iMapPoint.setMapVisibility(View.VISIBLE);

        searchBottomSheetController.setSearchBottomSheetState(BottomSheetBehavior.STATE_HIDDEN);
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

            Fragment curFragment = searchResultListAdapter.getFragment(position);

            if (curFragment instanceof AddressListFragment)
            {
                ((AddressListFragment) curFragment).onChangedPage();
            } else if (curFragment instanceof PlaceListFragment)
            {
                ((PlaceListFragment) curFragment).onChangedPage();
            }
        }
    }
}