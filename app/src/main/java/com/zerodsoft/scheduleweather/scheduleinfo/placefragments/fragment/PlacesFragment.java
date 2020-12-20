package com.zerodsoft.scheduleweather.scheduleinfo.placefragments.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.map.MapActivity;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.scheduleinfo.ScheduleInfoActivity;
import com.zerodsoft.scheduleweather.scheduleinfo.placefragments.LocationInfo;
import com.zerodsoft.scheduleweather.scheduleinfo.placefragments.adapter.CategoryViewAdapter;
import com.zerodsoft.scheduleweather.scheduleinfo.placefragments.interfaces.IClickedPlaceItem;
import com.zerodsoft.scheduleweather.scheduleinfo.placefragments.interfaces.IPlaceItem;
import com.zerodsoft.scheduleweather.scheduleinfo.placefragments.interfaces.IPlacesFragment;
import com.zerodsoft.scheduleweather.scheduleinfo.placefragments.interfaces.LocationInfoGetter;
import com.zerodsoft.scheduleweather.scheduleinfo.placefragments.model.PlaceCategory;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class PlacesFragment extends Fragment implements LocationInfoGetter, IClickedPlaceItem, IPlacesFragment, IPlaceItem
{
    private final LocationInfo LocationInfo;
    private RecyclerView categoryRecyclerView;
    private CategoryViewAdapter adapter;
    private FragmentManager fragmentManager;
    private List<PlaceCategory> categories;

    public PlacesFragment(LocationInfo LocationInfo)
    {
        this.LocationInfo = LocationInfo;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.place_categories_fragment, container, false);
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
        // 위치 이름 표시
        ((TextView) view.findViewById(R.id.location_name)).setText(LocationInfo.getLocationName() + " " + getString(R.string.info_around_location));

        fragmentManager = getParentFragmentManager();

        categoryRecyclerView = (RecyclerView) view.findViewById(R.id.map_category_view_container);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        // 편의점, 주차장, ATM을 보여주기로 했다고 가정
        List<String> selectedCategories = new LinkedList<>();
        selectedCategories.add("1");
        selectedCategories.add("5");
        selectedCategories.add(getString(R.string.atm));

        categories = convertCategoryName(selectedCategories);

        adapter = new CategoryViewAdapter(LocationInfo.copy(), categories, this);
        categoryRecyclerView.setAdapter(adapter);
    }

    @Override
    public LocationInfo getLocationInfo()
    {
        return LocationInfo.copy();
    }

    private List<PlaceCategory> convertCategoryName(List<String> categories)
    {
        List<PlaceCategory> convertedCategories = new LinkedList<>();
        int index = 0;
        for (String name : categories)
        {
            if (KakaoLocalApiCategoryUtil.isCategory(name))
            {
                convertedCategories.add(new PlaceCategory(index, KakaoLocalApiCategoryUtil.getDescription(Integer.parseInt(name)),
                        KakaoLocalApiCategoryUtil.getName(Integer.parseInt(name))));
            } else
            {
                convertedCategories.add(new PlaceCategory(index, name));
            }
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
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        PlacesMapFragment.close();
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
            names.add(category.getCategoryName());
        }
        return names;
    }

    @Override
    public int getPlaceItemsSize(String categoryName)
    {
        return adapter.getPlaceItemsSize(categoryName);
    }
}
