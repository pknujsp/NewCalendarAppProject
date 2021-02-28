package com.zerodsoft.scheduleweather.event.location.placefragments.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.placecategory.activity.PlaceCategoryActivity;
import com.zerodsoft.scheduleweather.activity.placecategory.viewmodel.PlaceCategoryViewModel;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IstartActivity;
import com.zerodsoft.scheduleweather.databinding.PlaceCategoriesFragmentBinding;
import com.zerodsoft.scheduleweather.event.common.interfaces.ILocation;
import com.zerodsoft.scheduleweather.event.location.activity.MoreActivity;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.event.location.placefragments.adapter.CategoryViewAdapter;
import com.zerodsoft.scheduleweather.event.location.placefragments.interfaces.IClickedPlaceItem;
import com.zerodsoft.scheduleweather.event.location.placefragments.interfaces.IPlaceItem;
import com.zerodsoft.scheduleweather.event.location.placefragments.interfaces.IPlacesFragment;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class PlacesFragment extends Fragment implements IClickedPlaceItem, IPlacesFragment, IPlaceItem
{
    // 이벤트의 위치 값으로 정확한 위치를 지정하기 위해 위치 지정 액티비티 생성(카카오맵 검색 값 기반)
    // 맵 프래그먼트와 카테고리 별 데이타 목록 프래그먼트로 분리
    private final ILocation iLocation;
    private final IstartActivity istartActivity;
    private PlaceCategoriesFragmentBinding binding;
    private CategoryViewAdapter adapter;
    private List<PlaceCategoryDTO> placeCategoryList;
    private PlaceCategoryViewModel placeCategoryViewModel;

    public PlacesFragment(Activity activity)
    {
        this.iLocation = (ILocation) activity;
        this.istartActivity = (IstartActivity) activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = PlaceCategoriesFragmentBinding.inflate(inflater);
        return binding.getRoot();
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
        binding.mapCategoryViewContainer.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        placeCategoryViewModel = new ViewModelProvider(this).get(PlaceCategoryViewModel.class);

        initLocation();
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
                                    // 편의점, 주차장, ATM을 보여주기로 했다고 가정
                                    binding.locationName.setText((location.getPlaceName() == null ? location.getAddressName() : location.getPlaceName()) + getString(R.string.info_around_location));

                                    adapter = new CategoryViewAdapter(location, result, PlacesFragment.this);
                                    binding.mapCategoryViewContainer.setAdapter(adapter);

                                    binding.categorySettingsFab.setOnClickListener(new View.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(View view)
                                        {
                                            Intent intent = new Intent(getActivity(), PlaceCategoryActivity.class);
                                            istartActivity.startActivityResult(intent, 0);
                                        }
                                    });
                                }
                            });

                        }
                    });
                }
            }
        });
    }


    @Override
    public void onClickedItem(PlaceDocuments document)
    {
        Toast.makeText(getActivity(), document.getPlaceName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickedMore(String categoryDescription)
    {
        Intent intent = new Intent(requireActivity(), MoreActivity.class);
        intent.putExtra("map", (HashMap<String, List<PlaceDocuments>>) adapter.getAllItems());
        intent.putExtra("selectedCategoryDescription", categoryDescription);
        startActivity(intent);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
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
    public List<PlaceDocuments> getPlaceItems(PlaceCategoryDTO placeCategory)
    {
        return adapter.getPlaceItems(placeCategory);
    }

    @Override
    public List<String> getCategoryNames()
    {
        List<String> names = new LinkedList<>();

        for (PlaceCategoryDTO category : placeCategoryList)
        {
            names.add(category.getDescription());
        }
        return names;
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
