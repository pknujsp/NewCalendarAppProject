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
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.placecategory.activity.CategorySettingsActivity;
import com.zerodsoft.scheduleweather.activity.placecategory.activity.PlaceCategoryActivity;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IstartActivity;
import com.zerodsoft.scheduleweather.databinding.PlaceCategoriesFragmentBinding;
import com.zerodsoft.scheduleweather.event.common.interfaces.ILocation;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.event.location.placefragments.adapter.CategoryViewAdapter;
import com.zerodsoft.scheduleweather.event.location.placefragments.interfaces.IClickedPlaceItem;
import com.zerodsoft.scheduleweather.event.location.placefragments.interfaces.IPlaceItem;
import com.zerodsoft.scheduleweather.event.location.placefragments.interfaces.IPlacesFragment;
import com.zerodsoft.scheduleweather.retrofit.PlaceCategory;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import java.util.LinkedList;
import java.util.List;

public class PlacesFragment extends Fragment implements IClickedPlaceItem, IPlacesFragment, IPlaceItem
{
    // 이벤트의 위치 값으로 정확한 위치를 지정하기 위해 위치 지정 액티비티 생성(카카오맵 검색 값 기반)
    private final ILocation iLocation;
    private final IstartActivity istartActivity;
    private PlaceCategoriesFragmentBinding binding;
    private CategoryViewAdapter adapter;
    private List<PlaceCategory> categories;

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
                    // 편의점, 주차장, ATM을 보여주기로 했다고 가정
                    binding.locationName.setText((location.getPlaceName() == null ? location.getAddressName() : location.getPlaceName()) + getString(R.string.info_around_location));

                    List<String> selectedCategories = new LinkedList<>();
                    selectedCategories.add("1");
                    selectedCategories.add("5");
                    selectedCategories.add(getString(R.string.atm));

                    categories = convertCategoryName(selectedCategories);
                    adapter = new CategoryViewAdapter(location, categories, PlacesFragment.this);

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
            }
        });
    }


    private List<PlaceCategory> convertCategoryName(List<String> categories)
    {
        List<PlaceCategory> convertedCategories = new LinkedList<>();
        int index = 0;
        for (String name : categories)
        {
            PlaceCategory placeCategory = new PlaceCategory();

            if (KakaoLocalApiCategoryUtil.isCategory(name))
            {
                placeCategory.setDescription(KakaoLocalApiCategoryUtil.getDescription(Integer.parseInt(name)));
                placeCategory.setCode(KakaoLocalApiCategoryUtil.getName(Integer.parseInt(name)));
            } else
            {
                placeCategory.setDescription(name);
            }
            convertedCategories.add(placeCategory);
            index++;
        }
        return convertedCategories;
    }

    @Override
    public void onClickedItem(PlaceDocuments document)
    {
        Toast.makeText(getActivity(), document.getPlaceName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickedMore(String categoryDescription)
    {
        /*
        PlacesMapFragment mapFragment = PlacesMapFragment.getInstance();

        if (mapFragment == null)
        {
            mapFragment = PlacesMapFragment.newInstance(this, categoryDescription);
            fragmentManager.beginTransaction().add(R.id.places_around_location_fragment_container, mapFragment, PlacesMapFragment.TAG).commit();
            fragmentManager.beginTransaction().show(mapFragment).hide(this).addToBackStack(null).commit();
        } else
        {
            mapFragment.selectChip(categoryDescription);
            fragmentManager.beginTransaction().show(mapFragment).hide(this).addToBackStack(null).commit();
        }

        Intent intent = new Intent(requireActivity(), AroundPlacesActivity.class);
        intent.putExtra("map", (HashMap<String, List<PlaceDocuments>>) adapter.getAllItems());
        intent.putExtra("selectedCategory", categoryDescription);
        startActivity(intent);

         */
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        // PlacesMapFragment.close();
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
    public List<PlaceDocuments> getPlaceItems(String categoryName)
    {
        return adapter.getPlaceItems(categoryName);
    }

    @Override
    public List<String> getCategoryNames()
    {
        List<String> names = new LinkedList<>();

        for (PlaceCategory category : categories)
        {
            names.add(category.getDescription());
        }
        return names;
    }

    @Override
    public int getPlaceItemsSize(String categoryName)
    {
        return adapter.getPlaceItemsSize(categoryName);
    }
}
