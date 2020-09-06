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
import android.widget.EditText;
import android.widget.ImageButton;

import com.zerodsoft.scheduleweather.activity.mapactivity.MapActivity;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.recyclerviewadapter.SearchCategoryViewAdapter;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryCode;
import com.zerodsoft.scheduleweather.retrofit.LocalApiPlaceParameter;

public class SearchFragment extends Fragment implements SearchCategoryViewAdapter.OnCategoryClickListener, MapActivity.OnBackPressedListener
{
    public static final String TAG = "Search Fragment";
    private static SearchFragment instance;

    private ImageButton backButton;
    private EditText searchEditText;
    private ImageButton searchButton;
    private RecyclerView searchHistoryRecyclerView;
    private RecyclerView itemCategoryRecyclerView;

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
        this.latitude = bundle.getDouble("latitude");
        this.longitude = bundle.getDouble("longitude");
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
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        backButton = (ImageButton) view.findViewById(R.id.back_button);
        searchEditText = (EditText) view.findViewById(R.id.search_edittext);
        searchButton = (ImageButton) view.findViewById(R.id.search_button);
        searchHistoryRecyclerView = (RecyclerView) view.findViewById(R.id.search_history_recyclerview);
        itemCategoryRecyclerView = (RecyclerView) view.findViewById(R.id.category_recyclerview);

        SearchCategoryViewAdapter searchCategoryViewAdapter = new SearchCategoryViewAdapter(this);
        itemCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        itemCategoryRecyclerView.setAdapter(searchCategoryViewAdapter);

        searchButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Bundle bundle = new Bundle();
                LocalApiPlaceParameter parameter = new LocalApiPlaceParameter();
                // String searchWord, double latitude, double longitude, String sort, String page
                // 검색 파라미터 설정
                parameter.setQuery(searchEditText.getText().toString()).setX(longitude).setY(latitude)
                        .setSort(LocalApiPlaceParameter.SORT_ACCURACY).setPage("1");
                bundle.putParcelable("parameter", parameter);
                ((MapActivity) getActivity()).onFragmentChanged(SearchResultFragment.TAG, bundle);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener()
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
        searchEditText.setText("");
        MapFragment mapFragment = MapFragment.getInstance();
        mapFragment.clearAllPoiItems();
        mapFragment.setZoomGpsButtonVisibility(View.VISIBLE);
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }
}