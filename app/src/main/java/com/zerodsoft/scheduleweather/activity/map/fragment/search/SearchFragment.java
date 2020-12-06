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

import com.zerodsoft.scheduleweather.activity.map.MapActivity;
import com.zerodsoft.scheduleweather.activity.map.fragment.dto.SearchData;
import com.zerodsoft.scheduleweather.activity.map.fragment.map.MapFragment;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.SearchResultFragment;
import com.zerodsoft.scheduleweather.activity.map.fragment.interfaces.CustomOnBackPressed;
import com.zerodsoft.scheduleweather.databinding.FragmentSearchBinding;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;

import net.daum.mf.map.api.MapPoint;

public class SearchFragment extends Fragment implements CustomOnBackPressed
{
    public static final String TAG = "SearchFragment";
    private FragmentSearchBinding binding;
    private SearchCategoryViewAdapter searchCategoryViewAdapter;

    public SearchFragment()
    {
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
        searchCategoryViewAdapter = new SearchCategoryViewAdapter(this);
        binding.categoryRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        binding.categoryRecyclerview.setAdapter(searchCategoryViewAdapter);

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

        super.onViewCreated(view, savedInstanceState);
    }


    private void search(String searchWord)
    {
        MapPoint.GeoCoordinate currentMapPoint = MapFragment.currentMapPoint.getMapPointGeoCoord();

        LocalApiPlaceParameter parameter = new LocalApiPlaceParameter()
                .setX(Double.toString(currentMapPoint.longitude)).setY(Double.toString(currentMapPoint.latitude))
                .setSort(LocalApiPlaceParameter.SORT_ACCURACY).setPage(LocalApiPlaceParameter.DEFAULT_PAGE);

        Bundle bundle = new Bundle();
        SearchData searchData = new SearchData(searchWord, parameter);
        bundle.putParcelable("searchData", searchData);
        ((MapActivity) getActivity()).replaceFragment(SearchResultFragment.TAG, bundle);
    }

    @Override
    public void onBackPressed()
    {
        MapFragment.getInstance(getActivity()).setMain();
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.popBackStackImmediate();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.show(MapFragment.getInstance(getActivity())).commit();
    }
}