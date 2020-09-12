package com.zerodsoft.scheduleweather.activity.mapactivity.Fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.activity.mapactivity.MapActivity;
import com.zerodsoft.scheduleweather.databinding.FragmentSearchBinding;
import com.zerodsoft.scheduleweather.recyclerviewadapter.SearchCategoryViewAdapter;
import com.zerodsoft.scheduleweather.retrofit.LocalApiPlaceParameter;

public class SearchFragment extends Fragment implements SearchCategoryViewAdapter.OnCategoryClickListener, MapActivity.OnBackPressedListener
{
    public static final String TAG = "Search Fragment";
    private static SearchFragment instance;
    private FragmentSearchBinding binding;
    private SearchCategoryViewAdapter searchCategoryViewAdapter;

    private double latitude;
    private double longitude;

    private MapController.OnDownloadListener onDownloadListener;

    public SearchFragment(Activity activity)
    {
        onDownloadListener = (MapController.OnDownloadListener) activity;
    }

    public static SearchFragment getInstance(Activity activity)
    {
        if (instance == null)
        {
            instance = new SearchFragment(activity);
        }
        return instance;
    }

    public void setInitialData(Bundle bundle)
    {
        if (!bundle.isEmpty())
        {
            this.latitude = bundle.getDouble("latitude");
            this.longitude = bundle.getDouble("longitude");
        }
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
                Bundle bundle = new Bundle();
                LocalApiPlaceParameter parameter = new LocalApiPlaceParameter();
                // String searchWord, double latitude, double longitude, String sort, String page
                // 검색 파라미터 설정
                parameter.setQuery(binding.searchEdittext.getText().toString()).setX(longitude).setY(latitude)
                        .setSort(LocalApiPlaceParameter.SORT_ACCURACY).setPage("1");
                bundle.putParcelable("parameter", parameter);
                ((MapActivity) getActivity()).onFragmentChanged(SearchResultFragment.TAG, bundle);
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

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }


    @Override
    public void onBackPressed()
    {
        binding.searchEdittext.setText("");
        MapFragment mapFragment = MapFragment.getInstance(getActivity());
        mapFragment.removeAllPoiItems();
        mapFragment.setZoomGpsButtonVisibility(View.VISIBLE);

        ((MapActivity) getActivity()).onFragmentChanged(MapFragment.TAG, new Bundle());
    }

    @Override
    public void selectedCategory(String name, String description)
    {
        Bundle bundle = new Bundle();
        LocalApiPlaceParameter parameter = new LocalApiPlaceParameter();
        // String searchWord, double latitude, double longitude, String sort, String page
        // 검색 파라미터 설정
        parameter.setQuery(description).setX(longitude).setY(latitude)
                .setSort(LocalApiPlaceParameter.SORT_ACCURACY).setPage("1");
        bundle.putParcelable("parameter", parameter);
        ((MapActivity) getActivity()).onFragmentChanged(SearchResultFragment.TAG, bundle);
    }
}