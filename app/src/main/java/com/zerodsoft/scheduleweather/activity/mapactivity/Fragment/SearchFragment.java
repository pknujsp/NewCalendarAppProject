package com.zerodsoft.scheduleweather.activity.mapactivity.Fragment;

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
    private LocalApiPlaceParameter parameters;
    private int calledDownloadTotalCount;

    private double latitude;
    private double longitude;

    public SearchFragment()
    {
    }

    public static SearchFragment getInstance()
    {
        if (instance == null)
        {
            instance = new SearchFragment();
        }
        return instance;
    }

    public void setInitialData(Bundle bundle)
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
                String searchWord = searchEditText.getText().toString();
                String name = getCategoryName(searchWord);

                parameters = null;
                parameters = new LocalApiPlaceParameter().setX(longitude)
                        .setY(latitude).setPage(LocalApiPlaceParameter.DEFAULT_PAGE)
                        .setSize(LocalApiPlaceParameter.DEFAULT_SIZE)
                        .setSort(LocalApiPlaceParameter.DEFAULT_SORT);

                if (name != null)
                {
                    searchEditText.setText(searchWord);
                    parameters.setCategoryGroupCode(name);
                    calledDownloadTotalCount = 1;
                    KakaoLocalApi.searchPlaceCategory(handler, parameters);
                } else
                {
                    parameters.setQuery(searchWord);
                    calledDownloadTotalCount = 2;
                    KakaoLocalApi.searchAddress(handler, parameters);
                    KakaoLocalApi.searchPlaceKeyWord(handler, parameters);
                }
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
    public void selectedCategory(String name, String description)
    {
        // 카테고리 이름을 전달받음
        searchEditText.setText(description);
        parameters = null;
        parameters = new LocalApiPlaceParameter().setX(longitude)
                .setY(latitude)
                .setPage(LocalApiPlaceParameter.DEFAULT_PAGE)
                .setSize(LocalApiPlaceParameter.DEFAULT_SIZE)
                .setSort(LocalApiPlaceParameter.DEFAULT_SORT)
                .setCategoryGroupCode(name);

        calledDownloadTotalCount = 1;
        KakaoLocalApi.searchPlaceCategory(handler, parameters);
    }

    private String getCategoryName(String searchWord)
    {
        KakaoLocalApiCategoryCode.loadCategoryMap();
        String name = KakaoLocalApiCategoryCode.getName(searchWord);

        if (name != null)
        {
            return name;
        } else
        {
            return null;
        }
    }

    public void setData(Bundle bundle)
    {
        this.latitude = bundle.getDouble("latitude");
        this.longitude = bundle.getDouble("longitude");
    }

    @Override
    public void onBackPressed()
    {
        searchEditText.setText("");

        ((MapActivity) getActivity()).clearAllPoiItems();
        ((MapActivity) getActivity()).setZoomGpsButtonVisibility(View.VISIBLE);
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }
}