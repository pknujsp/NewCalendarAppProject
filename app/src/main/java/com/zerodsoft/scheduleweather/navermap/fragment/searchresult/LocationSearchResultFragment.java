package com.zerodsoft.scheduleweather.navermap.fragment.searchresult;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.common.interfaces.OnBackPressedCallbackController;
import com.zerodsoft.scheduleweather.databinding.FragmentSearchResultListBinding;
import com.zerodsoft.scheduleweather.etc.LocationType;
import com.zerodsoft.scheduleweather.event.places.interfaces.PoiItemOnClickListener;
import com.zerodsoft.scheduleweather.navermap.BottomSheetType;
import com.zerodsoft.scheduleweather.navermap.PoiItemType;
import com.zerodsoft.scheduleweather.navermap.interfaces.BottomSheetController;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnClickedLocListItem;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapData;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.navermap.fragment.searchresult.adapter.SearchResultListAdapter;
import com.zerodsoft.scheduleweather.navermap.fragment.searchresult.interfaces.IndicatorCreater;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.navermap.interfaces.SearchBarController;
import com.zerodsoft.scheduleweather.navermap.interfaces.SearchFragmentController;
import com.zerodsoft.scheduleweather.navermap.model.SearchResultChecker;
import com.zerodsoft.scheduleweather.navermap.model.callback.CheckerCallback;
import com.zerodsoft.scheduleweather.navermap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnExtraListDataListener;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressKakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceKakaoLocalResponse;

import java.util.ArrayList;
import java.util.List;

public class LocationSearchResultFragment extends Fragment implements IndicatorCreater, OnClickedLocListItem, OnBackPressedCallbackController,
        OnExtraListDataListener<LocationType>
{
    public static final String TAG = "LocationSearchResultFragment";
    private FragmentSearchResultListBinding binding;

    private SearchResultListAdapter searchResultListAdapter;

    private final String SEARCH_WORD;

    private OnPageCallback onPageCallback;
    private IMapPoint iMapPoint;
    private IMapData iMapData;
    private PoiItemOnClickListener poiItemOnClickListener;
    private SearchBarController searchBarController;
    private SearchFragmentController searchFragmentController;
    private BottomSheetController bottomSheetController;

    private OnExtraListDataListener<LocationType> placesOnExtraListDataListener;
    private OnExtraListDataListener<LocationType> addressesOnExtraListDataListener;

    public OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true)
    {
        @Override
        public void handleOnBackPressed()
        {
            if (bottomSheetController.getStateOfBottomSheet(BottomSheetType.SEARCH_LOCATION) == BottomSheetBehavior.STATE_EXPANDED)
            {
                // list인 경우
                getParentFragmentManager().popBackStackImmediate();
                searchFragmentController.closeSearchFragments(LocationSearchResultFragment.TAG);
            } else
            {
                // map인 경우
                searchBarController.changeViewTypeImg(SearchBarController.MAP);
                bottomSheetController.setStateOfBottomSheet(BottomSheetType.SEARCH_LOCATION, BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetController.setStateOfBottomSheet(BottomSheetType.LOCATION_ITEM, BottomSheetBehavior.STATE_COLLAPSED);
            }
        }
    };

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        addOnBackPressedCallback();
    }


    @Override
    public void setIndicator(int fragmentSize)
    {
        binding.viewpagerIndicator.createDot(0, fragmentSize);
    }

    public LocationSearchResultFragment(String searchWord, Fragment fragment, SearchBarController searchBarController)
    {
        this.SEARCH_WORD = searchWord;
        this.iMapPoint = (IMapPoint) fragment;
        this.iMapData = (IMapData) fragment;
        this.poiItemOnClickListener = (PoiItemOnClickListener) fragment;
        this.searchFragmentController = (SearchFragmentController) fragment;
        this.searchBarController = searchBarController;
        this.bottomSheetController = (BottomSheetController) fragment;
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
        binding = FragmentSearchResultListBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        searchLocation(SEARCH_WORD);
    }

    public void searchLocation(String searchWord)
    {
        final LocalApiPlaceParameter addressParameter = LocalParameterUtil.getAddressParameter(searchWord, "1"
                , LocalApiPlaceParameter.DEFAULT_PAGE);
        final LocalApiPlaceParameter placeParameter = LocalParameterUtil.getPlaceParameter(searchWord, null, null,
                "1", LocalApiPlaceParameter.DEFAULT_PAGE, LocalApiPlaceParameter.SEARCH_CRITERIA_SORT_TYPE_ACCURACY);

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
                        binding.listViewpager.removeAllViews();
                        binding.errorText.setText(getContext().getString(R.string.error) + ", (" + response.getException().getMessage() + ")");
                        binding.errorText.setVisibility(View.VISIBLE);
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

                searchBarController.setViewTypeVisibility(View.VISIBLE);

                if (totalResultCount == 0)
                {
                    // 검색 결과 없음
                    binding.listViewpager.removeAllViews();
                    binding.errorText.setText(getContext().getString(R.string.not_founded_search_result));
                    binding.errorText.setVisibility(View.VISIBLE);
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
                                SearchResultPlaceListFragment searchResultPlaceListFragment = new SearchResultPlaceListFragment(iMapPoint, searchWord, iMapData, LocationSearchResultFragment.this);
                                placesOnExtraListDataListener = searchResultPlaceListFragment;
                                fragments.add(searchResultPlaceListFragment);
                            }
                        } else if (response.getData() instanceof AddressKakaoLocalResponse)
                        {
                            AddressKakaoLocalResponse addressKakaoLocalResponse = (AddressKakaoLocalResponse) response.getData();

                            if (!addressKakaoLocalResponse.getAddressResponseDocumentsList().isEmpty())
                            {
                                SearchResultAddressListFragment addressesListFragment = new SearchResultAddressListFragment(searchWord, iMapData, LocationSearchResultFragment.this);
                                addressesOnExtraListDataListener = addressesListFragment;
                                fragments.add(addressesListFragment);
                            }
                        }
                    }

                    searchResultListAdapter = new SearchResultListAdapter(LocationSearchResultFragment.this);
                    searchResultListAdapter.setFragments(fragments);
                    binding.listViewpager.setAdapter(searchResultListAdapter);

                    onPageCallback = new OnPageCallback();
                    binding.listViewpager.registerOnPageChangeCallback(onPageCallback);
                    binding.viewpagerIndicator.createDot(0, fragments.size());
                }
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
        removeOnBackPressedCallback();
    }

    @Override
    public void onClickedLocItem(int index)
    {
        poiItemOnClickListener.onPOIItemSelectedByList(index, PoiItemType.SEARCH_RESULT);
        searchBarController.changeViewTypeImg(SearchBarController.LIST);
        bottomSheetController.setStateOfBottomSheet(BottomSheetType.SEARCH_LOCATION, BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void loadExtraListData(LocationType e, RecyclerView.AdapterDataObserver adapterDataObserver)
    {


    }

    @Override
    public void loadExtraListData(RecyclerView.AdapterDataObserver adapterDataObserver)
    {
        Fragment curFragment = searchResultListAdapter.getFragment(binding.listViewpager.getCurrentItem());

        if (curFragment instanceof SearchResultAddressListFragment)
        {
            addressesOnExtraListDataListener.loadExtraListData(adapterDataObserver);
        } else if (curFragment instanceof SearchResultPlaceListFragment)
        {
            placesOnExtraListDataListener.loadExtraListData(adapterDataObserver);
        }
    }

    class OnPageCallback extends ViewPager2.OnPageChangeCallback
    {
        public int lastPosition;

        @Override
        public void onPageSelected(int position)
        {
            super.onPageSelected(position);
            lastPosition = position;
            binding.viewpagerIndicator.selectDot(position);

            Fragment curFragment = searchResultListAdapter.getFragment(position);

            if (curFragment instanceof SearchResultAddressListFragment)
            {
                ((SearchResultAddressListFragment) curFragment).onChangedPage();
            } else if (curFragment instanceof SearchResultPlaceListFragment)
            {
                ((SearchResultPlaceListFragment) curFragment).onChangedPage();
            }
        }
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

}