package com.zerodsoft.scheduleweather.activity.map.fragment.search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.map.fragment.dto.SearchData;
import com.zerodsoft.scheduleweather.activity.map.fragment.interfaces.OnSelectedMapCategory;
import com.zerodsoft.scheduleweather.activity.map.fragment.map.MapFragment;
import com.zerodsoft.scheduleweather.activity.map.fragment.search.adapter.PlaceCategoriesAdapter;
import com.zerodsoft.scheduleweather.activity.map.fragment.interfaces.CustomOnBackPressed;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.SearchResultFragmentController;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.interfaces.IMapInfo;
import com.zerodsoft.scheduleweather.databinding.FragmentSearchBinding;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategory;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;

import net.daum.mf.map.api.MapPoint;

import java.util.List;

public class SearchFragment extends Fragment implements CustomOnBackPressed, OnSelectedMapCategory
{
    public static final String TAG = "SearchFragment";
    private FragmentSearchBinding binding;
    private PlaceCategoriesAdapter categoriesAdapter;
    private IMapInfo iMapInfo;


    public SearchFragment(IMapInfo iMapInfo)
    {
        this.iMapInfo = iMapInfo;
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
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        categoriesAdapter = new PlaceCategoriesAdapter(this);
        binding.categoriesRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        binding.categoriesRecyclerview.setAdapter(categoriesAdapter);

        binding.searchButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                search(binding.searchEdittext.getText().toString());
            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onBackPressed();
            }
        });
    }


    private void search(String searchWord)
    {
        MapPoint.GeoCoordinate currentMapPoint = iMapInfo.getMapCenterPoint();

        LocalApiPlaceParameter parameter = new LocalApiPlaceParameter()
                .setX(Double.toString(currentMapPoint.longitude)).setY(Double.toString(currentMapPoint.latitude))
                .setSort(LocalApiPlaceParameter.SORT_ACCURACY).setPage(LocalApiPlaceParameter.DEFAULT_PAGE);

        Bundle bundle = new Bundle();
        SearchData searchData = new SearchData(searchWord, parameter);
        bundle.putParcelable("searchData", searchData);

        SearchResultFragmentController searchResultFragmentController = new SearchResultFragmentController(bundle);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(SearchFragment.this).add(R.id.map_activity_fragment_container, searchResultFragmentController, SearchFragment.TAG).addToBackStack(null).commit();
    }

    @Override
    public void onSelectedMapCategory(KakaoLocalApiCategory category)
    {
        search(Integer.toString(category.getId()));
    }

    @Override
    public void onBackPressed()
    {
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.popBackStackImmediate();

        List<Fragment> fragments = fragmentManager.getFragments();
        MapFragment mapFragment = null;
        for (Fragment fragment : fragments)
        {
            if (fragment instanceof MapFragment)
            {
                mapFragment = (MapFragment) fragment;
                break;
            }
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.show(mapFragment).commit();
    }
}