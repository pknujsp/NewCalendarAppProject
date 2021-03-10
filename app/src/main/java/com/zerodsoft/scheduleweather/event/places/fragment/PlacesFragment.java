package com.zerodsoft.scheduleweather.event.places.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.activity.placecategory.activity.PlaceCategoryActivity;
import com.zerodsoft.scheduleweather.activity.placecategory.viewmodel.PlaceCategoryViewModel;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IstartActivity;
import com.zerodsoft.scheduleweather.databinding.PlaceCategoriesFragmentBinding;
import com.zerodsoft.scheduleweather.event.common.interfaces.ILocation;
import com.zerodsoft.scheduleweather.event.places.interfaces.IFragment;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.event.places.adapter.CategoryViewAdapter;
import com.zerodsoft.scheduleweather.event.places.interfaces.IClickedPlaceItem;
import com.zerodsoft.scheduleweather.event.places.interfaces.IPlaceItem;
import com.zerodsoft.scheduleweather.event.places.interfaces.IPlacesFragment;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import java.util.List;

public class PlacesFragment extends Fragment implements IPlacesFragment, IPlaceItem
{
    public static final String TAG = "PlacesFragment";
    // 이벤트의 위치 값으로 정확한 위치를 지정하기 위해 위치 지정 액티비티 생성(카카오맵 검색 값 기반)
    // 맵 프래그먼트와 카테고리 별 데이타 목록 프래그먼트로 분리
    private final ILocation iLocation;
    private final IstartActivity istartActivity;
    private final IFragment iFragment;
    private IClickedPlaceItem iClickedPlaceItem;

    private PlaceCategoriesFragmentBinding binding;
    private CategoryViewAdapter adapter;
    private List<PlaceCategoryDTO> placeCategoryList;
    private PlaceCategoryViewModel placeCategoryViewModel;
    private OnBackPressedCallback onBackPressedCallback;

    public PlacesFragment(ILocation iLocation, IstartActivity istartActivity, IFragment iFragment)
    {
        this.iLocation = iLocation;
        this.istartActivity = istartActivity;
        this.iFragment = iFragment;
    }

    public void setiClickedPlaceItem(IClickedPlaceItem iClickedPlaceItem)
    {
        this.iClickedPlaceItem = iClickedPlaceItem;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = PlaceCategoriesFragmentBinding.inflate(inflater);
        return binding.getRoot();
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
                requireActivity().finish();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        binding.searchingRadiusRange.setText(App.getPreference_key_radius_range() + "M");
        binding.mapCategoryViewContainer.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        placeCategoryViewModel = new ViewModelProvider(this).get(PlaceCategoryViewModel.class);
        binding.categorySettingsFab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getActivity(), PlaceCategoryActivity.class);
                istartActivity.startActivityResult(intent, 0);
            }
        });

        initLocation();
    }


    @Override
    public void onDetach()
    {
        super.onDetach();
        onBackPressedCallback.remove();
    }

    private void initLocation()
    {
        iLocation.getLocation(new CarrierMessagingService.ResultCallback<LocationDTO>()
        {
            @Override
            public void onReceiveResult(@NonNull LocationDTO location) throws RemoteException
            {
                if (location.getId() >= 0)
                {
                    placeCategoryViewModel.selectConvertedSelected(new CarrierMessagingService.ResultCallback<List<PlaceCategoryDTO>>()
                    {
                        @Override
                        public void onReceiveResult(@NonNull List<PlaceCategoryDTO> result) throws RemoteException
                        {
                            placeCategoryList = result;
                            getActivity().runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    if (!placeCategoryList.isEmpty())
                                    {
                                        binding.notSelectedCategory.setVisibility(View.GONE);

                                        // 편의점, 주차장, ATM을 보여주기로 했다고 가정
                                        binding.locationName.setText((location.getPlaceName() == null ? location.getAddressName() : location.getPlaceName()) + getString(R.string.info_around_location));

                                        adapter = new CategoryViewAdapter(location, result, iClickedPlaceItem, PlacesFragment.this);
                                        binding.mapCategoryViewContainer.setAdapter(adapter);
                                    } else
                                    {
                                        binding.notSelectedCategory.setVisibility(View.VISIBLE);
                                        //카테고리 추가 액티비티 실행
                                        binding.categorySettingsFab.performClick();
                                    }
                                }
                            });

                        }
                    });
                }
            }
        });
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        onBackPressedCallback.remove();
    }

    @Override
    public LifecycleOwner getLifeCycleOwner()
    {
        return PlacesFragment.this;
    }

    @Override
    public ViewModelStoreOwner getViewModelStoreOwner()
    {
        return PlacesFragment.this;
    }

    @Override
    public Fragment getFragment()
    {
        return this;
    }

    @Override
    public List<PlaceDocuments> getPlaceItems(PlaceCategoryDTO placeCategory)
    {
        return adapter.getPlaceItems(placeCategory);
    }


    @Override
    public int getPlaceItemsSize(PlaceCategoryDTO placeCategory)
    {
        return adapter.getPlaceItemsSize(placeCategory);
    }

    public void refresh()
    {
        initLocation();
    }
}
